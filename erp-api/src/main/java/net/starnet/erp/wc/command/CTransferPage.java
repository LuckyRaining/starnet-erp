package net.starnet.erp.wc.command;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.model.TransferProduct;
import net.starnet.erp.wc.service.TransferProductService;
import net.starnet.erp.wc.service.TransferService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.uc.model.User;
import net.starnet.erp.uc.service.UserService;
import net.starnet.erp.util.SimpleValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 调拨单分页列表
 */
@Command
public class CTransferPage extends BaseCommand {

    @Autowired
    private TransferService transferService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransferProductService transferProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    @Param(defaultValue = "{}")
    private JSONObject query; // 查询对象
    @Param(defaultValue = Define.CURRENT)
    private long current;
    @Param(defaultValue = Define.SIZE)
    private long size;

    @Override
    protected void init() throws Exception {
        String startDate = query.getString("startDate");
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }

        String endDate = query.getString("endDate");
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        Page<Transfer> TransferPage = transferService.pageSearch(current, size, query);

        User auditor;
        User lister;
        for (Transfer transfer : TransferPage.getRecords()) {
            List<TransferProduct> productList = getProductList(transfer);
            transfer.put("productList", productList);
            appendProductDisplayFields(transfer, productList);

            if (StrKit.notBlank(transfer.getListerId())) {
                lister = userService.getById(transfer.getListerId());
                transfer.put("listerName", lister.getName());
            }

            if (StrKit.notBlank(transfer.getAuditorId())) {
                auditor = userService.getById(transfer.getAuditorId());
                transfer.put("auditorName", auditor.getName());
            }
        }

        data.put("transferPage", TransferPage);
    }

    /**
     * 获取商品信息列表
     *
     * @param transfer
     * @return
     */
    private List<TransferProduct> getProductList(Transfer transfer) {
        List<TransferProduct> transferProductList = transferProductService.findListByTransfer(transfer.getId());
        for (TransferProduct transferProduct : transferProductList) {
            Product product = productService.getById(transferProduct.getProductId());
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

        return transferProductList;
    }

    /**
     * 列表展示用：仅一条调拨商品时展示商品/单位/仓库信息，多条时上述字段显示为破折号。
     */
    private void appendProductDisplayFields(Transfer transfer, List<TransferProduct> transferProductList) {
        if (transferProductList != null && transferProductList.size() == 1) {
            TransferProduct transferProduct = transferProductList.get(0);
            transfer.put("productName", transferProduct.get("productName"));
            transfer.put("unitName", transferProduct.get("unitName"));
            transfer.put("fromWarehouseName", transferProduct.get("fromWarehouseName"));
            transfer.put("toWarehouseName", transferProduct.get("toWarehouseName"));
            transfer.put("quantity", transferProduct.getQuantity());
        } else {
            transfer.put("productName", "-");
            transfer.put("unitName", "-");
            transfer.put("fromWarehouseName", "-");
            transfer.put("toWarehouseName", "-");
        }
    }
}
