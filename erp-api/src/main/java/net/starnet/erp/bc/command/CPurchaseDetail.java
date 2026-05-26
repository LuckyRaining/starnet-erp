package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 购货单详情
 */
@Command
public class CPurchaseDetail extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private AccountRecordService accountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    @Param(required = true)
    private String purchaseId; // 购货订单ID

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Purchase purchase = purchaseService.getById(purchaseId);
        Assert.notNull(purchase, "ID为【" + purchaseId + "】的购货订单不存在！");

        // 1. 获取 商品列表 productList[]；
        // 2. 并添加 商品名称 productName、单位名称 unitName、仓库名称 warehouseName；
        // 3. 然后添加到 购货订单 purchase 下
        List<IssueProduct> productList = issueProductService.findListByBusiness(purchase.getId());
        for (IssueProduct issueProduct : productList) {
            Product product = productService.getById(issueProduct.getProductId());
            Assert.notNull(product, "ID为【" + issueProduct.getProductId() + "】的商品不存在！");
            issueProduct.put("productName", product.getName());

            DictItem dictItem = dictItemService.getById(product.getUnitId());
            Assert.notNull(dictItem, "ID为【" + product.getUnitId() + "】的单位不存在！");
            issueProduct.put("unitName", dictItem.getName());

            Warehouse warehouse = warehouseService.getById(issueProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + issueProduct.getWarehouseId() + "】的仓库不存在！");
            issueProduct.put("warehouseName", warehouse.getName());
        }
        purchase.put("productList", productList);

        // 1. 获取 单据账户列表 accountList[]；
        // 2. 然后添加到 购货订单 purchase 下
        List<AccountRecord> accountList = accountService.findListByBusiness(purchase.getId());
        purchase.put("accountList", accountList);

        // 将 购货订单 purchase 作为 查询结果 Result，并用 response 返回给前端
        data.put("purchase", purchase);
    }
}
