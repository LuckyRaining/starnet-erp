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
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Supplier;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.SettlementAccountService;
import net.starnet.erp.uc.service.SupplierService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockRecordService;
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
    private StockRecordService stockRecordService;
    @Autowired
    private PayableService payableService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Purchase purchase;
    @Param(defaultValue = "[]")
    private List<IssueProduct> productList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Purchase persistedPurchase;

    @Override
    protected void init() throws Exception {
        // BizException 为后端展示
        if (!Define.validatePurchaseType(purchase.getType())) {
            throw new BizException("采购类型不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        // 校验数据
        Assert.notBlank(purchase.getSupplierId(), "供应商ID不能为空！");
        Supplier supplier = supplierService.getById(purchase.getSupplierId());
        Assert.notNull(supplier, "ID为【" + purchase.getSupplierId() + "】的供应商不存在！");

        // 同 init() 初始化校验，但 Assert 为前端展示
        Assert.notFalse(Define.validatePurchaseType(purchase.getType()), "类型不正确！");

        // 计算
        // 购货单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(purchase.getId())) { // purchase.id 为空时，即没传，为“新增”的意思
            persistedPurchase = new Purchase();

            persistedPurchase.setType(purchase.getType());
            // 校验 单据编号 是否合法，合法才能“新增”，即 新增购货单
            validatePurchaseCode(purchase.getCode());
            persistedPurchase.setCode(purchase.getCode());

            // 初始化 购货单 的 checked 为 false
            persistedPurchase.setChecked(false);

        } else { // purchase.id 非空时，即传了，为“更新”的意思
            persistedPurchase = purchaseService.getById(purchase.getId());
            Assert.notNull(persistedPurchase, "ID为【" + purchase.getId() + "】的购货单不存在！");

            // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
            // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
            stockService.rollbackStock(purchase.getId());
            stockRecordService.deleteByBusiness(purchase.getId());
            // 删除 该单原来的商品列表 productList[]
            // 即 删除 单据商品 wc_issue_product
            issueProductService.deleteByBusiness(purchase.getId());

            // 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // 即 回滚 结算账户 uc_settlement_account
            settlementAccountService.rollbackByBusiness(purchase.getId());
            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(purchase.getId());

            // 删除 该单原来的 应付账款记录
            // 即 删除 应付账款记录 fc_payable
            payableService.deleteByBusiness(purchase.getId());
        }

        persistedPurchase.setSupplierId(purchase.getSupplierId());
        persistedPurchase.setIssueDate(purchase.getIssueDate());
        persistedPurchase.setCode(purchase.getCode());

         // TODO 完善 付/退款状态 判定
        persistedPurchase.setStatus(resolvePurchaseStatus(purchase.getPreferredAmount(), purchase.getCurrentAmount()));
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
        persistedPurchase.setAuditorId(purchase.getAuditorId());

        // 是否需要 审核？
        // 新增保存 购货单/购退单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(purchase.getAuditorId()) && !purchase.isChecked();

        persistedPurchase.setRemark(purchase.getRemark());
        // 新增/更新 购货单 bc_purchase
        purchaseService.saveOrUpdate(persistedPurchase);

        // 新增 购货单据的 商品列表 productList[]
        addProductList();

        // 新增 购货单据的 账户列表 accountList[]
        String accountType = persistedPurchase.getType().equals(Define.BUSINESS_TYPE_PURCHASE_BUY) ?
                Define.ACCOUNT_RECORD_TYPE_OUT : Define.ACCOUNT_RECORD_TYPE_IN;
        for (AccountRecord record : accountList) {
            record.setAmount(persistedPurchase.getCurrentAmount());
        }
        // 新增 单据账户 fc_account_record，更新 结算账户 uc_settlement_account
        accountRecordService.addRecordList(accountList, accountType, persistedPurchase.getIssueDate(), persistedPurchase.getType(), persistedPurchase.getId());

        // 新增 应付账款记录
        handlePayable();

        // 新增保存时：Save 页，若选择了审核人，但 checked 仍为 false 的情况下，保存完成后自动审核。
        // （逻辑与 CPurchaseSwitchCheck 一致，审核通过后会生成 fc_payment_issue）
        if (shouldCheck) {
            saveAuditService.checkPurchase(persistedPurchase, persistedPurchase.getAuditorId());
        }

        data.put("purchase", persistedPurchase);
    }

    /**
     * 校验 单据编号 是否合法
     *
     * @param code
     */
    private void validatePurchaseCode(String code) {
        Purchase testPurchase = purchaseService.findByCode(code);
        if (testPurchase != null) {
            Assert.notNull(testPurchase, "单据编号为【" + code + "】的购货单已经存在！");
            throw new BizException("单据编号为【" + code + "】的购货单已经存在！");
        }
    }

    /**
     * 获取 总数量
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
     * 新增 购货单据的 商品列表 productList[]
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
            persistedIssueProduct.setQuantity(issueProduct.getQuantity());
            // 设置 单价：使用商品的 “预计采购价” 作为库存成本对外展示的 “单价”，而非 “售价” or “批发价”。
            persistedIssueProduct.setPrice(product.getEstimatedPurchasePrice());
            // persistedIssueProduct.setPrice(issueProduct.getPrice());
            persistedIssueProduct.setDiscountRate(issueProduct.getDiscountRate());
            persistedIssueProduct.setDiscountAmount(issueProduct.getDiscountAmount());
            persistedIssueProduct.setAmount(issueProduct.getAmount());
            persistedIssueProduct.setCode(issueProduct.getCode());
            persistedIssueProduct.setRemark(issueProduct.getRemark());

            // 处理 库存：购货 为 入库，购退 为 出库
            String stockType = persistedPurchase.getType().equals(Define.BUSINESS_TYPE_PURCHASE_BUY) ?
                    Define.STOCK_TYPE_IN : Define.STOCK_TYPE_OUT;
            // 更新 库存商品 wc_stock，新增 出入库记录 wc_stock_record
            stockService.handleStock(persistedIssueProduct, stockType);

            persistedIssueProductList.add(persistedIssueProduct);
        }

        // 新增 单据商品 wc_issue_product
        issueProductService.saveBatch(persistedIssueProductList);
    }

    /**
     * 新增 应付账款记录
     */
    private void handlePayable() {
//        if (StrKit.notBlank(purchase.getId())) {
//            // 删除 该单原来的 应付账款记录
//            payableService.deleteByBusiness(purchase.getId());
//        }

        // 购货 if ，购退 else
        if (Define.BUSINESS_TYPE_PURCHASE_BUY.equals(persistedPurchase.getType())) { // 购货
            // 新增 应付账款记录 fc_payable
            payableService.businessAdd(persistedPurchase.getSupplierId(), persistedPurchase.getIssueDate(),
                    Define.BUSINESS_TYPE_PURCHASE_BUY, persistedPurchase.getId(), persistedPurchase.getDebtAmount(), persistedPurchase.getCurrentAmount());

        } else { // 购退
            // 新增 应付账款记录 fc_payable
            payableService.businessAdd(persistedPurchase.getSupplierId(), persistedPurchase.getIssueDate(),
                    Define.BUSINESS_TYPE_PURCHASE_REFUND, persistedPurchase.getId(), persistedPurchase.getDebtAmount(), persistedPurchase.getCurrentAmount());
        }
    }

    /**
     * 根据 已付款金额 和 优惠后金额 判定 付/退款状态
     */
    private int resolvePurchaseStatus(double preferredAmount, double paidAmount) {
        // 若 已付款金额 <= 0，则 状态为 未付/退款
        if (paidAmount <= 0) {
            return Define.PURCHASE_STATUS_UNPAID;
        }

        // 若 已付款金额 > 0，且已付款金额 < 优惠后金额，则 状态为 部分付/退款
        if (paidAmount > 0 && paidAmount < preferredAmount) {
            return Define.PURCHASE_STATUS_PARTIAL;
        }

        // 否则 为已付款金额 >= 优惠后金额，状态为 全部付/退款
        return Define.PURCHASE_STATUS_PAID;
    }
}
