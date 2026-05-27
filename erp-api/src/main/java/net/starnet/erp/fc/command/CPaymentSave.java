package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.model.Payment;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.PayableService;
import net.starnet.erp.fc.service.PaymentIssueService;
import net.starnet.erp.fc.service.PaymentService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 付款单保存
 */
@Command
public class CPaymentSave extends BaseCommand {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private PaymentIssueService paymentIssueService;
    @Autowired
    private PayableService payableService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Payment payment;
    @Param(defaultValue = "[]")
    private List<PaymentIssue> paymentIssueList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Payment persistedPayment;
    private boolean isNew;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 计算
        // 付款单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(payment.getId())) { // payment.id 为空时，即没传，为“新增”的意思
            isNew = true;
            persistedPayment = new Payment();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增付款单
            validateCode(payment.getCode());
            persistedPayment.setCode(payment.getCode());

            // 初始化 付款单 的 checked 为 false
            persistedPayment.setChecked(false);

        } else { // payment.id 非空时，即传了，为“更新”的意思
            isNew = false;
            persistedPayment = paymentService.getById(payment.getId());
            Assert.notNull(persistedPayment, "ID为【" + payment.getId() + "】的付款订单不存在！");

            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(payment.getId());

            // 删除 该单原来的明细列表 paymentIssueList[]
            // 即 删除 付款单据明细列表 fc_payment_issue
            paymentIssueService.deleteByPayment(payment.getId());
        }

        persistedPayment.setIssueDate(payment.getIssueDate());
        persistedPayment.setSupplierId(payment.getSupplierId());
        persistedPayment.setPaidAmount(payment.getPaidAmount());
        persistedPayment.setIssueAmount(payment.getIssueAmount());
        persistedPayment.setDiscountAmount(payment.getDiscountAmount());
        persistedPayment.setVerifiedAmount(payment.getVerifiedAmount());
        persistedPayment.setUnverifiedAmount(payment.getUnverifiedAmount());
        persistedPayment.setCurrentVerifiedAmount(payment.getCurrentVerifiedAmount());
        persistedPayment.setAdvancePaidAmount(payment.getAdvancePaidAmount());
        persistedPayment.setListerId(payment.getListerId());
        persistedPayment.setAuditorId(payment.getAuditorId());
        persistedPayment.setRemark(payment.getRemark());
        // 新增/更新 付款单 fc_payment
        paymentService.saveOrUpdate(persistedPayment);

        // 新增 付款单据的 账户列表 accountList[]
        // 新增 单据账户 fc_account_record，更新 结算账户 uc_settlement_account
        accountRecordService.addRecordList(accountList, Define.ACCOUNT_RECORD_TYPE_OUT, persistedPayment.getIssueDate(), Define.BUSINESS_TYPE_PAYMENT, persistedPayment.getId());

        // 新增 付款单据的 明细列表 paymentIssueList[]
        addPaymentIssueListList();

        // 新增 应付账款记录
        handlePayable();

        // 新增保存时：Save 页已选审核人但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CPaymentSwitchCheck 一致）
        if (saveAuditService.shouldAuditOnNewSave(isNew, persistedPayment.isChecked(), persistedPayment.getAuditorId())) {
            saveAuditService.checkPayment(persistedPayment, persistedPayment.getAuditorId());
        }

        data.put("payment", persistedPayment);
    }

    /**
     * 校验单据编号是否合法
     *
     * @param code
     */
    private void validateCode(String code) {
        Payment Payment = paymentService.findByCode(code);
        if (Payment != null) {
            throw new BizException("单据编号为【" + code + "】的入库单已经存在！");
        }
    }

    /**
     * 新增 付款单据的 明细列表 paymentIssueList[]
     */
    private void addPaymentIssueListList() {
        if (paymentIssueList == null || paymentIssueList.size() == 0) return;

        List<PaymentIssue> persistedPaymentIssueListList = new ArrayList<>();
        PaymentIssue persistedPaymentIssue;
        for (PaymentIssue paymentIssue : paymentIssueList) {
            persistedPaymentIssue = new PaymentIssue();
            persistedPaymentIssue.setPaymentId(persistedPayment.getId());
            persistedPaymentIssue.setSourceCode(paymentIssue.getSourceCode());
            persistedPaymentIssue.setType(paymentIssue.getType());
            persistedPaymentIssue.setIssueDate(paymentIssue.getIssueDate());
            persistedPaymentIssue.setIssueAmount(paymentIssue.getIssueAmount());
            persistedPaymentIssue.setVerifiedAmount(paymentIssue.getVerifiedAmount());
            persistedPaymentIssue.setUnverifiedAmount(paymentIssue.getUnverifiedAmount());
            persistedPaymentIssue.setCurrentVerifiedAmount(paymentIssue.getCurrentVerifiedAmount());

            persistedPaymentIssueListList.add(persistedPaymentIssue);
        }

        // 新增 单据商品 fc_payment_issue
        paymentIssueService.saveBatch(persistedPaymentIssueListList);
    }

    /**
     * 新增 应付账款记录
     */
    private void handlePayable() {
//        if (StrKit.notBlank(payment.getId())) {
//            // 删除 该单原来的 应付账款记录
//            payableService.deleteByBusiness(payment.getId());
//        }

        // 新增 应付账款记录 fc_payable
        payableService.businessAdd(persistedPayment.getSupplierId(), persistedPayment.getIssueDate(),
                Define.BUSINESS_TYPE_PAYMENT, persistedPayment.getId(), 0, persistedPayment.getAdvancePaidAmount());
    }

}
