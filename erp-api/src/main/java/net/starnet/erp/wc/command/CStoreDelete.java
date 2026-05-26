package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import net.starnet.erp.wc.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 入库单删除
 */
@Command
public class CStoreDelete extends BaseCommand {

    @Autowired
    private StoreService storeService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private IssueProductService issueProductService;

    /** 入库单ID */
    @Param(required = true)
    private String storeId;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Store store = storeService.getById(storeId);
        Assert.notNull(store, "ID为【" + storeId + "】的入库单不存在！");

        // 删除 该单原来的信息
        // 即 删除 入库单 wc_store
        storeService.removeById(storeId);

        // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
        stockService.rollbackStock(storeId);
        stockRecordService.deleteByBusiness(storeId);
        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_issue_product
        issueProductService.deleteByBusiness(storeId);

        // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

        // ！不涉及！ 删除 该单原来的账户列表 accountList[]
        // ！不涉及！ 即 删除 单据账户 fc_account_record

        // ！不涉及！ 删除 该单原来的 应付账款记录
        // ！不涉及！ 即 删除 应付账款记录 fc_payable
    }
}
