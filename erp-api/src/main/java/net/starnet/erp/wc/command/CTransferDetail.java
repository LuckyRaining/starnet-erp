package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.model.TransferProduct;
import net.starnet.erp.wc.service.TransferProductService;
import net.starnet.erp.wc.service.TransferService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 调拨单详情
 */
@Command
public class CTransferDetail extends BaseCommand {

    @Autowired
    private TransferService transferService;
    @Autowired
    private TransferProductService transferProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    @Param(required = true)
    private String transferId; // 调拨订单ID
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Transfer transfer = transferService.getById(transferId);
        Assert.notNull(transfer, "ID为【" + transferId + "】的调拨订单不存在！");

        // 1. 获取 商品列表 productList[]；
        // 2. 并添加 商品名称 productName、单位名称 unitName、调出仓库名称 fromWarehouseName、调入仓库名称 toWarehouseName；
        // 3. 然后添加到 客户订单 order 下
        List<TransferProduct> productList = transferProductService.findListByTransfer(transfer.getId());
        for (TransferProduct transferProduct : productList) {
            Product product = productService.getById(transferProduct.getProductId());
            Assert.notNull(product, "ID为【" + transferProduct.getProductId() + "】的商品不存在！");
            transferProduct.put("productName", product.getName());

            DictItem dictItem = dictItemService.getById(product.getUnitId());
            Assert.notNull(dictItem, "ID为【" + product.getUnitId() + "】的单位不存在！");
            transferProduct.put("unitName", dictItem.getName());

            Warehouse fromWarehouse = warehouseService.getById(transferProduct.getFromWarehouseId());
            Assert.notNull(fromWarehouse, "ID为【" + transferProduct.getFromWarehouseId() + "】的调出仓库不存在！");
            transferProduct.put("fromWarehouseName", fromWarehouse.getName());

            Warehouse toWarehouse = warehouseService.getById(transferProduct.getToWarehouseId());
            Assert.notNull(toWarehouse, "ID为【" + transferProduct.getToWarehouseId() + "】的调入仓库不存在！");
            transferProduct.put("toWarehouseName", toWarehouse.getName());
        }
        transfer.put("productList", productList);

        // 将 调拨订单 transfer 作为 查询结果 Result，并用 response 返回给前端
        data.put("transfer", transfer);
    }
}
