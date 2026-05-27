package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.PayableService;
import net.starnet.erp.uc.service.SettlementAccountService;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 购货单删除
 */
@Command
@Transactional
public class CPurchaseDelete extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private PayableService payableService;

    /** 购货单ID */
    @Param(required = true)
    private String purchaseId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Purchase purchase = purchaseService.getById(purchaseId);
        Assert.notNull(purchase, "ID为【" + purchaseId + "】的购货单不存在！");
        Assert.notFalse(!purchase.isChecked(), "已审核的购货单不能删除！");

        // 删除 该单原来的信息
        // 即 删除 购货单 bc_purchase
        purchaseService.removeById(purchaseId);

        // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
        stockService.rollbackStock(purchaseId);
        stockRecordService.deleteByBusiness(purchaseId);
        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_issue_product
        issueProductService.deleteByBusiness(purchaseId);

        // 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // 即 回滚 结算账户 uc_settlement_account
        settlementAccountService.rollbackByBusiness(purchaseId);
        // 删除 该单原来的账户列表 accountList[]
        // 即 删除 单据账户 fc_account_record
        accountRecordService.deleteByBusiness(purchaseId);

        // 删除 该单原来的 应付账款记录
        // 即 删除 应付账款记录 fc_payable
        payableService.deleteByBusiness(purchaseId);
    }
}
