package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.ReceivableService;
import net.starnet.erp.uc.service.SettlementAccountService;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 销售单删除
 */
@Command
public class CSaleDelete extends BaseCommand {

    @Autowired
    private SaleService saleService;
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
    private ReceivableService receivableService;

    /** 销售单ID */
    @Param(required = true)
    private String saleId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Sale sale = saleService.getById(saleId);
        Assert.notNull(sale, "ID为【" + saleId + "】的销售单不存在！");
        Assert.notFalse(!sale.isChecked(), "已审核的销货单不能删除！");

        // 删除 该单原来的信息
        // 即 删除 销售单 bc_sale
        saleService.removeById(saleId);

        // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
        stockService.rollbackStock(saleId);
        stockRecordService.deleteByBusiness(saleId);
        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_issue_product
        issueProductService.deleteByBusiness(saleId);

        // 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // 即 回滚 结算账户 uc_settlement_account
        settlementAccountService.rollbackByBusiness(saleId);
        // 删除 该单原来的账户列表 accountList[]
        // 即 删除 单据账户 fc_account_record
        accountRecordService.deleteByBusiness(saleId);

        // 删除 该单原来的 应收账款记录
        // 即 删除 应收账款记录 fc_receivable
        receivableService.deleteByBusiness(saleId);
    }
}
