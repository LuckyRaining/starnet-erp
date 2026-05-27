package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.PaymentIssueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 获取 该供应商已审核的购货单、购退单，并附带 最新付款单据明细 的核销金额
 */
@Command
public class CPurchaseFindCheckedListBySupplier extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private PaymentIssueService paymentIssueService;

    @Param(required = true)
    private String supplierId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        List<Purchase> purchaseList = purchaseService.findCheckedListBySupplier(supplierId);
        // 如果 最新付款单据明细 不存在，则不显示该购货单、购退单
        purchaseList.removeIf(purchase -> !appendVerifiedAmount(purchase));

        data.put("purchaseList", purchaseList);
    }

    /**
     * 根据源单编号查询 fc_payment_issue 最新一条记录，回填 已核销/未核销金额
     */
    private boolean appendVerifiedAmount(Purchase purchase) {
        PaymentIssue paymentIssue = null;
        if (StrKit.notBlank(purchase.getCode())) {
            paymentIssue = paymentIssueService.findLatestBySourceCode(purchase.getCode());
        } else {
            return false;
        }

        if (paymentIssue != null) {
            // 如果 最新付款单据明细 存在，则回填 已核销/未核销金额，并返回 true
            purchase.put("verifiedAmount", paymentIssue.getVerifiedAmount());
            purchase.put("unverifiedAmount", paymentIssue.getUnverifiedAmount());
            return true;
        } else {
            // 如果 最新付款单据明细 不存在，则返回 false
            return false;
        }
    }
}
