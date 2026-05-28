package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.CheckoutService;
import net.starnet.erp.uc.model.*;
import net.starnet.erp.uc.service.*;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 出库单保存
 */
@Command
public class CCheckoutSave extends BaseCommand {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Checkout checkout;
    @Param(defaultValue = "[]")
    private List<IssueProduct> productList;

    private Checkout persistedCheckout;

    @Override
    protected void init() throws Exception {
        // BizException 为后端展示
        if (!Define.validateCheckoutType(checkout.getType())) {
            throw new BizException("采购类型不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        // 校验数据
        Assert.notBlank(checkout.getCustomerId(), "供应商ID不能为空！");
        Customer customer = customerService.getById(checkout.getCustomerId());
        Assert.notNull(customer, "ID为【" + checkout.getCustomerId() + "】的供应商不存在！");

        // 同 init() 初始化校验，但 Assert 为前端展示
        Assert.notFalse(Define.validateCheckoutType(checkout.getType()), "类型不正确！");

        // 计算
        // 出库单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(checkout.getId())) { // checkout.id 为空时，即没传，为“新增”的意思
            persistedCheckout = new Checkout();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增出库单
            validateCheckoutCode(checkout.getCode());
            persistedCheckout.setCode(checkout.getCode());

            // 初始化 出库单 的 checked 为 false
            persistedCheckout.setChecked(false);

        } else { // checkout.id 非空时，即传了，为“更新”的意思
            persistedCheckout = checkoutService.getById(checkout.getId());
            Assert.notNull(persistedCheckout, "ID为【" + checkout.getId() + "】的出库订单不存在！");

            // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
            // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
            stockService.rollbackStock(checkout.getId());
            stockRecordService.deleteByBusiness(checkout.getId());
            // 删除 该单原来的商品列表 productList[]
            // 即 删除 单据商品 wc_issue_product
            issueProductService.deleteByBusiness(checkout.getId());

            // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

            // ！不涉及！ 删除 该单原来的账户列表 accountList[]
            // ！不涉及！ 即 删除 单据账户 fc_account_record

            // ！不涉及！ 删除 该单原来的 应收账款记录
            // ！不涉及！ 即 删除 应收账款记录 fc_receivable
        }

        persistedCheckout.setIssueDate(checkout.getIssueDate());
        persistedCheckout.setType(checkout.getType());
        persistedCheckout.setCustomerId(checkout.getCustomerId());
        persistedCheckout.setAmount(getAmount());
        persistedCheckout.setQuantity(getQuantity());
        persistedCheckout.setListerId(checkout.getListerId());

        // 是否需要 审核？
        // 新增保存 出库单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(checkout.getAuditorId()) && !checkout.isChecked();

        persistedCheckout.setAuditorId(checkout.getAuditorId());
        persistedCheckout.setRemark(checkout.getRemark());
        // 新增/更新 出库单 wc_checkout
        checkoutService.saveOrUpdate(persistedCheckout);

        // 新增 单据的 商品列表 productList[]
        addProductList();

        // ！不涉及！ 新增 单据的 账户列表 accountList[]

        // ！不涉及！ 新增 应收账款记录

        // 新增保存时：Save 页已选审核人但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CCheckoutSwitchCheck 一致）
        if (shouldCheck) {
            saveAuditService.checkCheckout(persistedCheckout, persistedCheckout.getAuditorId());
        }

        data.put("checkout", persistedCheckout);
    }

    /**
     * 校验 单据编号 是否合法
     *
     * @param code
     */
    private void validateCheckoutCode(String code) {
        Checkout testCheckout = checkoutService.findByCode(code);
        if (testCheckout != null) {
            Assert.notNull(testCheckout, "单据编号为【" + code + "】的出库单已经存在！");
            throw new BizException("单据编号为【" + code + "】的出库单已经存在！");
        }
    }

    /**
     * 获取 总金额
     *
     * @return
     */
    private Double getAmount() {
        double amount = 0.0d;
        for (IssueProduct product : productList) {
            amount += product.getAmount();
        }

        return amount;
    }

    /**
     * 获取 总数量
     *
     * @return
     */
    private Double getQuantity() {
        double quantity = 0.0d;
        for (IssueProduct product : productList) {
            quantity += product.getQuantity();
        }

        return quantity;
    }

    /**
     * 新增 单据的 商品列表 productList[]
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
            persistedIssueProduct.setIssueDate(persistedCheckout.getIssueDate());

            if (Define.CHECKOUT_TYPE_LOSS == checkout.getType()) {
                persistedIssueProduct.setBusinessType(Define.BUSINESS_TYPE_CHECKOUT_LOSS);
            } else {
                persistedIssueProduct.setBusinessType(Define.BUSINESS_TYPE_CHECKOUT_OTHER);
            }

            persistedIssueProduct.setBusinessId(persistedCheckout.getId());
            persistedIssueProduct.setProductId(product.getId());
            persistedIssueProduct.setWarehouseId(warehouse.getId());

            // TODO 校验数据
            persistedIssueProduct.setQuantity(issueProduct.getQuantity());
            // 设置 单价：使用商品的 “预计采购价” 作为库存成本对外展示的 “单价”，而非 “售价” or “批发价”。
            persistedIssueProduct.setPrice(product.getEstimatedPurchasePrice());
            // persistedIssueProduct.setPrice(issueProduct.getPrice());
            persistedIssueProduct.setAmount(issueProduct.getAmount());

            // TODO 需不需要设置 单据编号？
            // persistedIssueProduct.setCode(issueProduct.getCode());
            persistedIssueProduct.setCode(issueProduct.getCode());

            persistedIssueProduct.setRemark(issueProduct.getRemark());

            // 处理 库存：其他出库 为 出库
            // 更新 库存商品 wc_stock，新增 出入库记录 wc_stock_record
            stockService.handleStock(persistedIssueProduct, Define.STOCK_TYPE_IN);

            persistedIssueProductList.add(persistedIssueProduct);
        }

        // 新增 单据商品 wc_issue_product
        issueProductService.saveBatch(persistedIssueProductList);
    }
}
