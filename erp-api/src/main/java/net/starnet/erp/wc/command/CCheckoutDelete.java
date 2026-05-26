package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.service.CheckoutService;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 出库单删除
 */
@Command
public class CCheckoutDelete extends BaseCommand {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private IssueProductService issueProductService;

    /** 出库单ID */
    @Param(required = true)
    private String checkoutId;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Checkout checkout = checkoutService.getById(checkoutId);
        Assert.notNull(checkout, "ID为【" + checkoutId + "】的出库单不存在！");

        // 删除 该单原来的信息
        // 即 删除 出库单 wc_checkout
        checkoutService.removeById(checkoutId);

        // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
        stockService.rollbackStock(checkoutId);
        stockRecordService.deleteByBusiness(checkoutId);
        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_issue_product
        issueProductService.deleteByBusiness(checkoutId);

        // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

        // ！不涉及！ 删除 该单原来的账户列表 accountList[]
        // ！不涉及！ 即 删除 单据账户 fc_account_record

        // ！不涉及！ 删除 该单原来的 应收账款记录
        // ！不涉及！ 即 删除 应收账款记录 fc_receivable
    }
}
