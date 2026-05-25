package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.*;
import net.starnet.erp.bc.service.*;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.service.OrderService;
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
 * 客户订单详情
 */
@Command
public class COrderDetail extends BaseCommand {

    @Autowired
    private OrderService orderService;
    @Autowired
    private IssueProductService orderProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    @Param(required = true)
    private String orderId; // 客户订单ID

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Order order = orderService.getById(orderId);
        Assert.notNull(order, "ID为【" + orderId + "】的客户订单不存在！");

        // 1. 获取 商品列表 productList；
        // 2. 并添加 商品名称 productName、单位名称 unitName、仓库名称 warehouseName；
        // 3. 然后添加到 客户订单 order 下
        List<IssueProduct> productList = orderProductService.findListByBusiness(order.getId());
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
        order.put("productList", productList);

        // 将 客户订单 order 作为 查询结果 Result，并用 response 返回给前端
        data.put("order", order);
    }
}
