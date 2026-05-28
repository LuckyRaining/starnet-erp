package net.starnet.erp.sc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Supplier;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.CustomerService;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.SupplierService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.util.SimpleValidator;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.model.StockRecord;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.service.CheckoutService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StoreService;
import net.starnet.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;

/**
 * 商品收发明细表
 * <p>
 * 按单据日期、商品、仓库等条件，查询库存变动记录（{@code wc_stock_record}），
 * 并为前端报表补充名称类展示字段（商品、仓库、单位等）。
 */
@Command
public class CAnalysisStockDetailList extends BaseCommand {

    @Autowired
    private StockRecordService recordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CheckoutService checkoutService;

    /** 查询起始 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param String startDate;
    /** 查询结束 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param String endDate;
    /** 商品 ID 列表，可选；传入时只查这些 商品明细行 */
    private @Param List<String> productIdList;
    /** 仓库 ID 列表，可选；传入时只查这些 仓库的明细行 */
    private @Param List<String> warehouseIdList;

    /**
     * 命令执行前参数校验。
     * 仅校验已传入的日期字符串格式是否合法，不强制要求必须传日期。
     */
    @Override
    protected void init() throws Exception {
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }
    }

    /**
     * 查询 商品收发明细 并 组装报表 展示数据。
     * <p>
     * 流程：
     * <ol>
     *     <li>组装 查询条件，调用 {@link StockRecordService#analysisList} 查明细行；</li>
     *     <li>逐行关联 商品、单位、仓库，写入 临时展示字段；</li>
     *     <li>将结果放入 {@code data.recordList} 返回前端。</li>
     * </ol>
     */
    @Override
    protected void doCommand() throws Exception {

        // 列出 所有 出入库记录
        List<StockRecord> recordList = recordService.analysisList(startDate, endDate, productIdList, warehouseIdList);

        for (StockRecord record : recordList) {
            // 商品信息
            Product product = productService.getById(record.getProductId());
            Assert.notNull(product, "ID为【" + record.getProductId() + "】的商品不存在！");
            record.put("productName", product.getName());
            record.put("spec", product.getSpec());
            record.put("productCode", product.getCode());

            // 计量单位名称（来自字典项）
            DictItem unit = dictItemService.getById(product.getUnitId());
            record.put("unitName", unit.getName());

            // 仓库信息
            Warehouse warehouse = warehouseService.getById(record.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + record.getWarehouseId() + "】的仓库不存在！");
            record.put("warehouseName", warehouse.getName());

            // 组装单据信息
            composeIssueInfo(record);

            // 设置 入库数量/出库数量
            // stockType 是从数据库经 MyBatis 读出来的新 String 对象，和 常量 Define.STOCK_TYPE_IN（"in"）不是同一个引用。用 == 比较几乎永远为 false，所以每条记录都会进 else 分支
            // 改成用 equals 比较
            // if (record.getStockType() == Define.STOCK_TYPE_IN) {
            if (Define.STOCK_TYPE_IN.equals(record.getStockType())) {
                record.put("stockInQuantity", record.getQuantity());
                record.put("stockOutQuantity", 0.0d);
            } else {
                record.put("stockInQuantity", 0.0d);
                record.put("stockOutQuantity", record.getQuantity());
            }
        }

        // 将结果放入 {@code data.recordList} 返回前端
        data.put("recordList", recordList);
    }

    /**
     * 组装单据信息
     * <p>
     * 流程：
     * <ol>
     *     <li>根据 businessType 判断业务类型，关联 购货单、销货单、调拨单、盘盈单、其他入库单、盘亏单、其他出库单；</li>
     *     <li>写入 临时展示字段：businessTypeName、issueCode、relatedUnit；</li>
     * </ol>
     *
     * @param record
     */
    private void composeIssueInfo(StockRecord record) {
        if (Define.BUSINESS_TYPE_PURCHASE_BUY.equals(record.getBusinessType())) {
            record.put("businessTypeName", "购货");
            Purchase purchase = purchaseService.getById(record.getBusinessId());
            record.put("issueCode", purchase.getCode());
            record.put("relatedUnit", getSupplierName(purchase.getSupplierId()));

        } else if (Define.BUSINESS_TYPE_PURCHASE_REFUND.equals(record.getBusinessType())) {
            record.put("businessTypeName", "购货退货");
            Purchase purchase = purchaseService.getById(record.getBusinessId());
            record.put("issueCode", purchase.getCode());
            record.put("relatedUnit", getSupplierName(purchase.getSupplierId()));

        } else if (Define.BUSINESS_TYPE_SALE_SELL.equals(record.getBusinessType())) {
            record.put("businessTypeName", "销货");
            Sale sale = saleService.getById(record.getBusinessId());
            record.put("issueCode", sale.getCode());
            record.put("relatedUnit", getCustomerName(sale.getCustomerId()));

        } else if (Define.BUSINESS_TYPE_SALE_RETURNED.equals(record.getBusinessType())) {
            record.put("businessTypeName", "销退");
            Sale sale = saleService.getById(record.getBusinessId());
            record.put("issueCode", sale.getCode());
            record.put("relatedUnit", getCustomerName(sale.getCustomerId()));

        } else if (Define.BUSINESS_TYPE_TRANSFER_IN.equals(record.getBusinessType())
                || Define.BUSINESS_TYPE_TRANSFER_OUT.equals(record.getBusinessType())) {
            Transfer transfer = transferService.getById(record.getBusinessId());
            record.put("businessTypeName", "调拨单");
            record.put("issueCode", transfer.getCode());
            // 无 往来单位 信息

        } else if (Define.BUSINESS_TYPE_STORE_PROFIT.equals(record.getBusinessType())) {
            Store store = storeService.getById(record.getBusinessId());
            record.put("businessTypeName", "盘盈");
            record.put("issueCode", store.getCode());
            record.put("relatedUnit", getSupplierName(store.getSupplierId()));

        } else if (Define.BUSINESS_TYPE_STORE_OTHER.equals(record.getBusinessType())) {
            Store store = storeService.getById(record.getBusinessId());
            record.put("businessTypeName", "其他入库");
            record.put("issueCode", store.getCode());
            record.put("relatedUnit", getSupplierName(store.getSupplierId()));

        } else if (Define.BUSINESS_TYPE_CHECKOUT_LOSS.equals(record.getBusinessType())) {
            Checkout checkout = checkoutService.getById(record.getBusinessId());
            record.put("businessTypeName", "盘亏");
            record.put("issueCode", checkout.getCode());
            record.put("relatedUnit", getCustomerName(checkout.getCustomerId()));
        } else if (Define.BUSINESS_TYPE_CHECKOUT_OTHER.equals(record.getBusinessType())) {
            Checkout checkout = checkoutService.getById(record.getBusinessId());
            record.put("businessTypeName", "其他出库");
            record.put("issueCode", checkout.getCode());
            record.put("relatedUnit", getCustomerName(checkout.getCustomerId()));
        }
    }

    /**
     * 获取供应商名称
     * <p>
     * 流程：
     * <ol>
     *     <li>根据 supplierId 获取 供应商信息；</li>
     *     <li>返回 供应商名称。</li>
     * </ol>
     *
     * @param supplierId
     * @return
     */
    private String getSupplierName(String supplierId) {
        if (StrKit.isBlank(supplierId)) {
            return "";
        }
        Supplier supplier = supplierService.getById(supplierId);

        return supplier.getName();
    }

    /**
     * 获取客户名称
     * <p>
     * 流程：
     * <ol>
     *     <li>根据 customerId 获取 客户信息；</li>
     *     <li>返回 客户名称。</li>
     * </ol>
     *
     * @param customerId
     * @return
     */
    private String getCustomerName(String customerId) {
        if (StrKit.isBlank(customerId)) {
            return "";
        }
        Customer customer = customerService.getById(customerId);

        return customer.getName();
    }

}
