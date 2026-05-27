package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.PaymentIssueService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 购货单 切换审核
 */
@Command
public class CPurchaseSwitchCheck extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PaymentIssueService paymentIssueService;

    @Param(required = true)
    private String purchaseId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Purchase purchase = purchaseService.getById(purchaseId);
        Assert.notNull(purchase, "ID为【" + purchaseId + "】的购货单不存在！");
        Assert.notFalse(!purchase.isChecked(), "已审核的购货单不能取消审核！");

        purchase.setChecked(true);
        // 更新 购货单 bc_purchase
        purchaseService.saveOrUpdate(purchase);

        // 审核通过后，生成 尚未关联付款单的 付款单据明细
        addPaymentIssue(purchase);

        data.put("purchase", purchase);
    }

    /**
     * 审核通过后，生成尚未关联付款单的 付款单据明细
     */
    private void addPaymentIssue(Purchase purchase) {
        // 如果 源单编号 为空，则不生成 付款单据明细
        if (StrKit.isBlank(purchase.getCode())) {
            return;
        }
        // 如果 源单编号 已生成付款单据明细，则不生成 新的付款单据明细
        if (paymentIssueService.findOldestBySourceCode(purchase.getCode()) != null) {
            return;
        }

        PaymentIssue paymentIssue = new PaymentIssue();
        paymentIssue.setPaymentId(null);
        paymentIssue.setSourceCode(purchase.getCode());
        paymentIssue.setType(resolvePaymentIssueType(purchase.getType()));
        paymentIssue.setIssueDate(purchase.getIssueDate());
        paymentIssue.setIssueAmount(purchase.getAmount());
        paymentIssue.setVerifiedAmount(0);
        paymentIssue.setUnverifiedAmount(purchase.getDebtAmount());
        paymentIssue.setCurrentVerifiedAmount(purchase.getCurrentAmount());
        // 新增 付款单据明细 fc_payment_issue
        paymentIssueService.save(paymentIssue);
    }

    private int resolvePaymentIssueType(String purchaseType) {
        if (Define.BUSINESS_TYPE_PURCHASE_BUY.equals(purchaseType)) {
            return Define.PAYMENT_ISSUE_TYPE_PURCHASE_BUY;
        } else {
            return Define.PAYMENT_ISSUE_TYPE_PURCHASE_REFUND;
        }
    }
}
