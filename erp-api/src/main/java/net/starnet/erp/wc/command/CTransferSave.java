package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.model.TransferProduct;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import net.starnet.erp.wc.service.TransferProductService;
import net.starnet.erp.uc.model.*;
import net.starnet.erp.uc.service.*;
import net.starnet.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 调拨单保存
 */
@Command
public class CTransferSave extends BaseCommand {

    @Autowired
    private TransferService transferService;
    @Autowired
    private TransferProductService transferProductService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;

    @Param(required = true)
    private Transfer transfer;
    @Param(defaultValue = "[]")
    private List<TransferProduct> productList;

    private Transfer persistedTransfer;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 计算
        // 调拨单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(transfer.getId())) { // transfer.id 为空时，即没传，为“新增”的意思
            persistedTransfer = new Transfer();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增调拨单
            validateTransferCode(transfer.getCode());
            persistedTransfer.setCode(transfer.getCode());

            // 初始化 调拨单 的 checked 为 false
            persistedTransfer.setChecked(false);

        } else { // transfer.id 非空时，即传了，为“更新”的意思
            persistedTransfer = transferService.getById(transfer.getId());
            Assert.notNull(persistedTransfer, "ID为【" + transfer.getId() + "】的调拨订单不存在！");

            // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
            // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
            stockService.rollbackTransferStock(transfer.getId());
            stockRecordService.deleteByBusiness(transfer.getId());
            // 删除 该单原来的商品列表 productList[]
            // 即 删除 单据商品 wc_transfer_product
            transferProductService.deleteByTransfer(transfer.getId());

            // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

            // ！不涉及！ 删除 该单原来的账户列表 accountList[]
            // ！不涉及！ 即 删除 单据账户 fc_account_record

            // ！不涉及！ 删除 该单原来的 应付账款记录/应收账款记录
            // ！不涉及！ 即 删除 应付账款记录 fc_payable / 应收账款记录 fc_receivable
        }

        persistedTransfer.setIssueDate(transfer.getIssueDate());
        persistedTransfer.setQuantity(getQuantity());
        persistedTransfer.setListerId(transfer.getListerId());
        persistedTransfer.setRemark(transfer.getRemark()); // 实则新建 调拨单 时，并不会 备注
        // 新增/更新 调拨单 wc_store
        transferService.saveOrUpdate(persistedTransfer);

        // 新增 调拨的 商品列表 productList[]
        addProductList();

        // ！不涉及！ 新增 单据的 账户列表 accountList[]

        // ！不涉及！ 新增 应付账款记录/应收账款记录

        data.put("transfer", persistedTransfer);
    }

    /**
     * 校验 单据编号 是否合法
     *
     * @param code
     */
    private void validateTransferCode(String code) {
        Transfer testTransfer = transferService.findByCode(code);
        if (testTransfer != null) {
            Assert.notNull(testTransfer, "单据编号为【" + code + "】的调拨单已经存在！");
            throw new BizException("单据编号为【" + code + "】的调拨单已经存在！");
        }
    }

    /**
     * 获取 总数量
     *
     * @return
     */
    private Double getQuantity() {
        double quantity = 0.0d;
        for (TransferProduct product : productList) {
            quantity += product.getQuantity();
        }

        return quantity;
    }

    /**
     * 新增 调拨的 商品列表 productList[]
     */
    private void addProductList() {
        if (productList == null || productList.size() == 0) return;

        List<TransferProduct> persistedTransferProductList = new ArrayList<>();
        TransferProduct persistedTransferProduct;
        for (TransferProduct transferProduct : productList) {
            Assert.notBlank(transferProduct.getProductId(), "商品ID不能为空！");
            Product product = productService.getById(transferProduct.getProductId());
            Assert.notNull(product, "ID为【" + transferProduct.getProductId() + "】的商品不存在！");

            Assert.notBlank(transferProduct.getFromWarehouseId(), "调出仓库ID不能为空！");
            Warehouse fromWarehouse = warehouseService.getById(transferProduct.getFromWarehouseId());
            Assert.notNull(fromWarehouse, "ID为【" + transferProduct.getFromWarehouseId() + "】的仓库不存在！");

            Assert.notBlank(transferProduct.getToWarehouseId(), "调入仓库ID不能为空！");
            Warehouse toWarehouse = warehouseService.getById(transferProduct.getToWarehouseId());
            Assert.notNull(toWarehouse, "ID为【" + transferProduct.getToWarehouseId() + "】的仓库不存在！");


            persistedTransferProduct = new TransferProduct();
            persistedTransferProduct.setTransferId(persistedTransfer.getId());
            persistedTransferProduct.setIssueDate(persistedTransfer.getIssueDate());
            persistedTransferProduct.setProductId(product.getId());
            persistedTransferProduct.setFromWarehouseId(fromWarehouse.getId());
            persistedTransferProduct.setToWarehouseId(toWarehouse.getId());

            // TODO 校验数据
            persistedTransferProduct.setQuantity(transferProduct.getQuantity());

            persistedTransferProduct.setRemark(transferProduct.getRemark());

            // 处理 库存：其他入库 为 入库
            // 更新 库存商品 wc_stock，新增 出入库记录 wc_stock_record
            stockService.handleTransferStock(persistedTransferProduct);

            persistedTransferProductList.add(persistedTransferProduct);
        }

        // 新增 调拨商品 wc_transfer_product
        transferProductService.saveBatch(persistedTransferProductList);
    }
}
