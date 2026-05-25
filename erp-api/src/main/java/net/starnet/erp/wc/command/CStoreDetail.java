package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StoreService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 入库单详情
 */
@Command
public class CStoreDetail extends BaseCommand {

    @Autowired
    private StoreService storeService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    @Param(required = true)
    private String storeId; // 入库订单ID
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Store store = storeService.getById(storeId);
        Assert.notNull(store, "ID为【" + storeId + "】的入库订单不存在！");

        // 1. 获取 商品列表 productList；
        // 2. 并添加 商品名称 productName、单位名称 unitName、仓库名称 warehouseName；
        // 3. 然后添加到 入库订单 store 下
        List<IssueProduct> productList = issueProductService.findListByBusiness(store.getId());
        for (IssueProduct storeProduct : productList) {
            Product product = productService.getById(storeProduct.getProductId());
            Assert.notNull(product, "ID为【" + storeProduct.getProductId() + "】的商品不存在！");
            storeProduct.put("productName", product.getName());

            DictItem dictItem = dictItemService.getById(product.getUnitId());
            Assert.notNull(dictItem, "ID为【" + product.getUnitId() + "】的单位不存在！");
            storeProduct.put("unitName", dictItem.getName());

            Warehouse warehouse = warehouseService.getById(storeProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + storeProduct.getWarehouseId() + "】的仓库不存在！");
            storeProduct.put("warehouseName", warehouse.getName());
        }
        store.put("productList", productList);

        // 将 入库订单 store 作为 查询结果 Result，并用 response 返回给前端
        data.put("store", store);
    }
}
