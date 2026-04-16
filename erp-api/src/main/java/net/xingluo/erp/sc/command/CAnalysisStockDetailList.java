package net.xingluo.erp.sc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.xingluo.erp.bc.model.Purchase;
import net.xingluo.erp.bc.model.Sale;
import net.xingluo.erp.bc.service.PurchaseService;
import net.xingluo.erp.bc.service.SaleService;
import net.xingluo.erp.constant.Define;
import net.xingluo.erp.rc.model.DictItem;
import net.xingluo.erp.rc.service.DictItemService;
import net.xingluo.erp.uc.model.Customer;
import net.xingluo.erp.uc.model.Product;
import net.xingluo.erp.uc.model.Supplier;
import net.xingluo.erp.uc.model.Warehouse;
import net.xingluo.erp.uc.service.CustomerService;
import net.xingluo.erp.uc.service.ProductService;
import net.xingluo.erp.uc.service.SupplierService;
import net.xingluo.erp.uc.service.WarehouseService;
import net.xingluo.erp.util.SimpleValidator;
import net.xingluo.erp.wc.model.Checkout;
import net.xingluo.erp.wc.model.StockRecord;
import net.xingluo.erp.wc.model.Store;
import net.xingluo.erp.wc.model.Transfer;
import net.xingluo.erp.wc.service.CheckoutService;
import net.xingluo.erp.wc.service.StockRecordService;
import net.xingluo.erp.wc.service.StoreService;
import net.xingluo.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 商品收发明细表
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

    /** 开始时间 */
    private @Param
    String startDate;
    /** 结束时间 */
    private @Param String endDate;
    /** 商品ID列表 */
    private @Param
    List<String> productIdList;
    /** 仓库ID列表 */
    private @Param List<String> warehouseIdList;

    @Override
    protected void init() throws Exception {
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        List<StockRecord> recordList = recordService.analysisList(startDate, endDate, productIdList, warehouseIdList);
        for (StockRecord record : recordList) {
            Product product = productService.getById(record.getProductId());
            Assert.notNull(product, "ID为【" + record.getProductId() + "】的商品不存在！");

            record.put("productName", product.getName());
            record.put("spec", product.getSpec());
            record.put("productCode", product.getCode());

            DictItem unit = dictItemService.getById(product.getUnitId());
            record.put("unitName", unit.getName());

            Warehouse warehouse = warehouseService.getById(record.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + record.getWarehouseId() + "】的仓库不存在！");
            record.put("warehouseName", warehouse.getName());

            composeIssueInfo(record);

            if (record.getStockType() == Define.STOCK_TYPE_IN) {
                record.put("stockInQuantity", record.getQuantity());
                record.put("stockOutQuantity", 0.0);
            } else {
                record.put("stockInQuantity", 0.0);
                record.put("stockOutQuantity", record.getQuantity());
            }
        }

        data.put("recordList", recordList);
    }

    /**
     * 组装单据信息
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
