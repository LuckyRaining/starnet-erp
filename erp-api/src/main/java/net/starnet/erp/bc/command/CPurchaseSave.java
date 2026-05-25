package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.PayableService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Supplier;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.SettlementAccountService;
import net.starnet.erp.uc.service.SupplierService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 购货单保存
 */
@Command
public class CPurchaseSave extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private StockService stockService;
    @Autowired
    private PayableService payableService;

    @Param(required = true)
    private Purchase purchase;
    @Param(defaultValue = "[]")
    private List<IssueProduct> productList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Purchase persistedPurchase;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 校验数据
        Assert.notBlank(purchase.getSupplierId(), "供应商ID不能为空！");
        Supplier supplier = supplierService.getById(purchase.getSupplierId());
        Assert.notNull(supplier, "ID为【" + purchase.getSupplierId() + "】的供应商不存在！");

        Assert.notFalse(Define.validatePurchaseType(purchase.getType()), "类型不正确！");

        // 计算
        // 购货单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(purchase.getId())) { // purchase.id 为空时，即没传，为“新增”的意思
            persistedPurchase = new Purchase();

            persistedPurchase.setType(purchase.getType());
            validatePurchaseCode(purchase.getCode()); // 校验 单据编号 是否合法，合法才能“新增”，即 加购货单
            persistedPurchase.setCode(purchase.getCode());
            persistedPurchase.setChecked(false);

        } else { // purchase.id 非空时，即传了，为“更新”的意思
            persistedPurchase = purchaseService.getById(purchase.getId());
            Assert.notNull(persistedPurchase, "ID为【" + purchase.getId() + "】的购货单不存在！");

            // 删除该单原来的商品列表
            issueProductService.deleteByBusiness(purchase.getId());

            // 删除该单原来的账户列表
            accountRecordService.deleteByBusiness(purchase.getId());
        }

        persistedPurchase.setSupplierId(purchase.getSupplierId());
        persistedPurchase.setIssueDate(purchase.getIssueDate());
        persistedPurchase.setCode(purchase.getCode());
        persistedPurchase.setStatus(Define.PURCHASE_STATUS_UNPAID); // TODO 完善此处
        persistedPurchase.setQuantity(getPurchaseQuantity());
        persistedPurchase.setDiscountAmount(purchase.getDiscountAmount());
        persistedPurchase.setAmount(purchase.getAmount());
        persistedPurchase.setPreferentialRate(purchase.getPreferentialRate());
        persistedPurchase.setPreferentialAmount(purchase.getPreferentialAmount());
        persistedPurchase.setPreferredAmount(purchase.getPreferredAmount());
        persistedPurchase.setCurrentAmount(purchase.getCurrentAmount());
        persistedPurchase.setDebtAmount(purchase.getDebtAmount());
        persistedPurchase.setContracts(purchase.getContracts());
        persistedPurchase.setListerId(purchase.getListerId());
        persistedPurchase.setRemark(purchase.getRemark());
        purchaseService.saveOrUpdate(persistedPurchase);

        // 新增商品
        addProductList();

        // 新增账户
        String accountType = persistedPurchase.getType().equals(Define.BUSINESS_TYPE_PURCHASE_BUY) ? Define.ACCOUNT_RECORD_TYPE_OUT : Define.ACCOUNT_RECORD_TYPE_IN;
        for (AccountRecord record : accountList) {
            record.setAmount(persistedPurchase.getCurrentAmount());
        }
        accountRecordService.addRecordList(accountList, accountType, persistedPurchase.getIssueDate(), persistedPurchase.getType(), persistedPurchase.getId());

        // 处理应付账款
        handlePayable();

        data.put("purchase", persistedPurchase);
    }

    /**
     * 处理应付账款
     */
    private void handlePayable() {
        if (StrKit.notBlank(purchase.getId())) {
            // 删除原来的应收账款记录
            payableService.deleteByBusiness(purchase.getId());
        }

        if (Define.BUSINESS_TYPE_PURCHASE_BUY.equals(persistedPurchase.getType())) {
            payableService.businessAdd(persistedPurchase.getSupplierId(), persistedPurchase.getIssueDate(),
                    Define.BUSINESS_TYPE_PURCHASE_BUY, persistedPurchase.getId(), persistedPurchase.getDebtAmount(), 0);

        } else { // 销退
            payableService.businessAdd(persistedPurchase.getSupplierId(), persistedPurchase.getIssueDate(),
                    Define.BUSINESS_TYPE_PURCHASE_REFUND, persistedPurchase.getId(), -persistedPurchase.getDebtAmount(), 0);
        }
    }

    /**
     * 校验单据编号是否合法
     *
     * @param code
     */
    private void validatePurchaseCode(String code) {
        Purchase purchase = purchaseService.findByCode(code);
        if (purchase != null) {
            Assert.notNull(purchase, "单据编号为【" + code + "】的购货单已经存在！");
            throw new BizException("单据编号为【" + code + "】的购货单已经存在！");
        }
    }

    /**
     * 获取总数量
     *
     * @return
     */
    private Double getPurchaseQuantity() {
        double quantity = 0.0d;
        for (IssueProduct product : productList) {
            quantity += product.getQuantity();
        }

        return quantity;
    }

    /**
     * 新增商品列表
     */
    private void addProductList() {
        if (productList == null || productList.size() == 0) return;

        List<IssueProduct> persistedIssueProductList = new ArrayList<>();
        IssueProduct persistedIssueProduct;
        for (IssueProduct issueProduct : productList) {
            Assert.notBlank(issueProduct.getProductId(), "商品ID不能为空！");
            Product product = productService.getById(issueProduct.getProductId());
            Assert.notNull(product, "ID为【" + issueProduct.getProductId() + "】的商品不存在！");


            Assert.notBlank(issueProduct.getWarehouseId(), "仓库ID不能为空！");
            Warehouse warehouse = warehouseService.getById(issueProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + issueProduct.getWarehouseId() + "】的仓库不存在！");

            persistedIssueProduct = new IssueProduct();
            persistedIssueProduct.setIssueDate(persistedPurchase.getIssueDate());
            persistedIssueProduct.setBusinessType(persistedPurchase.getType());
            persistedIssueProduct.setBusinessId(persistedPurchase.getId());
            persistedIssueProduct.setProductId(product.getId());
            persistedIssueProduct.setWarehouseId(warehouse.getId());

            // TODO 校验数据
            //  保证单价都是售价/预计采购价/批发价，统一折扣率为折扣率一
            persistedIssueProduct.setQuantity(issueProduct.getQuantity());
            persistedIssueProduct.setPrice(issueProduct.getPrice());
            persistedIssueProduct.setDiscountRate(issueProduct.getDiscountRate());
            persistedIssueProduct.setDiscountAmount(issueProduct.getDiscountAmount());
            persistedIssueProduct.setAmount(issueProduct.getAmount());
            persistedIssueProduct.setCode(issueProduct.getCode());
            persistedIssueProduct.setRemark(issueProduct.getRemark());

            // 处理库存：购货为入库，购退为出库
            String stockType = persistedPurchase.getType().equals(Define.BUSINESS_TYPE_PURCHASE_BUY) ?
                    Define.STOCK_TYPE_IN : Define.STOCK_TYPE_OUT;
            stockService.handleStock(persistedIssueProduct, stockType);

            persistedIssueProductList.add(persistedIssueProduct);
        }

        issueProductService.saveBatch(persistedIssueProductList);
    }
}
