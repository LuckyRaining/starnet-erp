package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 销售单详情
 */
@Command
public class CSaleDetail extends BaseCommand {

    @Autowired
    private SaleService saleService;
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
    private String saleId; // 销货订单ID
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Sale sale = saleService.getById(saleId);
        Assert.notNull(sale, "ID为【" + saleId + "】的销售订单不存在！");

        // 1. 获取 商品列表 productList；
        // 2. 并添加 商品名称 productName、单位名称 unitName、仓库名称 warehouseName；
        // 3. 然后添加到 销售订单 sale 下
        List<IssueProduct> productList = issueProductService.findListByBusiness(sale.getId());
        for (IssueProduct orderProduct : productList) {
            Product product = productService.getById(orderProduct.getProductId());
            Assert.notNull(product, "ID为【" + orderProduct.getProductId() + "】的商品不存在！");
            orderProduct.put("productName", product.getName());

            DictItem dictItem = dictItemService.getById(product.getUnitId());
            Assert.notNull(dictItem, "ID为【" + product.getUnitId() + "】的单位不存在！");
            orderProduct.put("unitName", dictItem.getName());

            Warehouse warehouse = warehouseService.getById(orderProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + orderProduct.getWarehouseId() + "】的仓库不存在！");
            orderProduct.put("warehouseName", warehouse.getName());
        }
        sale.put("productList", productList);

        // 1. 获取 单据账户列表 accountList；
        // 2. 然后添加到 销售订单 sale 下
        List<AccountRecord> accountList = accountService.findListByBusiness(sale.getId());
        sale.put("accountList", accountList);

        // 将 销售订单 sale 作为 查询结果 Result，并用 response 返回给前端
        data.put("sale", sale);
    }
}
