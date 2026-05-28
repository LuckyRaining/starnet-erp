package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.*;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.fc.service.ReceivableService;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.model.Employee;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.uc.model.*;
import net.starnet.erp.uc.service.*;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.StockRecordService;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 销售单保存
 */
@Command
public class CSaleSave extends BaseCommand {

    @Autowired
    private SaleService saleService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockRecordService stockRecordService;
    @Autowired
    private ReceivableService receivableService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Sale sale;
    @Param(defaultValue = "[]")
    private List<IssueProduct> productList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Sale persistedSale;
    
    @Override
    protected void init() throws Exception {
        // BizException 为后端展示
        if (!Define.validateSaleType(sale.getType())) {
            throw new BizException("销售类型不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        // 校验数据
        Assert.notBlank(sale.getCustomerId(), "客户ID不能为空！");
        Customer customer = customerService.getById(sale.getCustomerId());
        Assert.notNull(customer, "ID为【" + sale.getCustomerId() + "】的客户不存在！");

        Assert.notBlank(sale.getSellerId(), "销售员ID不能为空！");
        Employee seller = employeeService.getById(sale.getSellerId());
        Assert.notNull(seller, "ID为【" + sale.getSellerId() + "】的用户不存在！");

        // 同 init() 初始化校验，但 Assert 为前端展示
        Assert.notFalse(Define.validateSaleType(sale.getType()), "销售类型不正确！");

        // 计算
        // 销货单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(sale.getId())) { // sale.id 为空时，即没传，为“新增”的意思
            persistedSale = new Sale();

            persistedSale.setType(sale.getType());
            // 校验 单据编号 是否合法，合法才能“新增”，即 新增销货单
            validateCode(sale.getCode());
            persistedSale.setCode(sale.getCode());

            // 初始化 销货单 的 checked 为 false
            persistedSale.setChecked(false);

        } else { // sale.id 非空时，即传了，为“更新”的意思
            persistedSale = saleService.getById(sale.getId());
            Assert.notNull(persistedSale, "ID为【" + sale.getId() + "】的销售订单不存在！");

            // 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
            // 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record
            stockService.rollbackStock(sale.getId());
            stockRecordService.deleteByBusiness(sale.getId());
            // 删除 该单原来的商品列表 productList[]
            // 即 删除 单据商品 wc_issue_product
            issueProductService.deleteByBusiness(sale.getId());

            // 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // 即 回滚 结算账户 uc_settlement_account
            settlementAccountService.rollbackByBusiness(sale.getId());
            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(sale.getId());

            // 删除 该单原来的 应收账款记录
            // 即 删除 应收账款记录 fc_receivable
            receivableService.deleteByBusiness(sale.getId());
        }

        persistedSale.setCustomerId(sale.getCustomerId());
        persistedSale.setIssueDate(sale.getIssueDate());

        // TODO 完善 收/退款状态 判定
        persistedSale.setStatus(resolveSaleStatus(sale.getPreferredAmount(), sale.getCurrentAmount()));
        persistedSale.setQuantity(getQuantity());
        persistedSale.setDiscountAmount(sale.getDiscountAmount());
        persistedSale.setAmount(sale.getAmount());
        persistedSale.setPreferentialRate(sale.getPreferentialRate());
        persistedSale.setPreferentialAmount(sale.getPreferentialAmount());
        persistedSale.setPreferredAmount(sale.getPreferredAmount());
        persistedSale.setCurrentAmount(sale.getCurrentAmount());
        persistedSale.setDebtAmount(sale.getDebtAmount());
        persistedSale.setAttachments(sale.getAttachments());
        persistedSale.setListerId(sale.getListerId());
        persistedSale.setAuditorId(sale.getAuditorId());

        // 是否需要 审核？
        // 新增保存 销货单/销退单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(sale.getAuditorId()) && !sale.isChecked();

        persistedSale.setRemark(sale.getRemark());

        persistedSale.setSellerId(sale.getSellerId());
        persistedSale.setContactName(sale.getContactName());
        persistedSale.setAddress(sale.getAddress());
        persistedSale.setPhone(sale.getPhone());
        persistedSale.setCustomerFee(sale.getCustomerFee());
        // 新增/更新 销货单 bc_sale
        saleService.saveOrUpdate(persistedSale);

        // 新增 销货单据的 商品列表 productList[]
        addProductList();

        // 新增 销货单据的 账户列表 accountList[]
        String accountType = persistedSale.getType().equals(Define.BUSINESS_TYPE_SALE_SELL) ?
                Define.ACCOUNT_RECORD_TYPE_IN : Define.ACCOUNT_RECORD_TYPE_OUT;
        for (AccountRecord record : accountList) {
            record.setAmount(persistedSale.getCurrentAmount());
        }
        // 新增 单据账户 fc_account_record，更新 结算账户 uc_settlement_account
        accountRecordService.addRecordList(accountList, accountType, persistedSale.getIssueDate(), persistedSale.getType(), persistedSale.getId());

        // 新增 应收账款记录
        handleReceivable();

        // 新增保存时：Save 页已选审核人但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CSaleSwitchCheck 一致，审核通过后会生成 fc_collection_issue）
        if (shouldCheck) {
            saveAuditService.checkSale(persistedSale, persistedSale.getAuditorId());
        }

        data.put("sale", persistedSale);
    }

    /**
     * 校验 单据编号 是否合法
     *
     * @param code
     */
    private void validateCode(String code) {
        Sale testSale = saleService.findByCode(code);
        if (testSale != null) {
            Assert.notNull(testSale, "单据编号为【" + code + "】的销售单已经存在！");
            throw new BizException("单据编号为【" + code + "】的销售单已经存在！");
        }
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
     * 新增 销货单据的 商品列表 productList[]
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
            persistedIssueProduct.setIssueDate(persistedSale.getIssueDate());
            persistedIssueProduct.setBusinessType(persistedSale.getType());
            persistedIssueProduct.setBusinessId(persistedSale.getId());
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

            // 处理 库存：销货 为 入库，销退 为 出库
            String stockType = persistedSale.getType().equals(Define.BUSINESS_TYPE_PURCHASE_BUY) ?
                    Define.STOCK_TYPE_IN : Define.STOCK_TYPE_OUT;
            // 更新 库存商品 wc_stock，新增 出入库记录 wc_stock_record
            stockService.handleStock(persistedIssueProduct, stockType);

            persistedIssueProductList.add(persistedIssueProduct);
        }

        // 新增 单据商品 wc_issue_product
        issueProductService.saveBatch(persistedIssueProductList);
    }

    /**
     * 新增 应收账款记录
     */
    private void handleReceivable() {
//        if (StrKit.notBlank(sale.getId())) {
//            // 删除 该单原来的 应收账款记录
//            receivableService.deleteByBusiness(sale.getId());
//        }

        // 销货 if ，销退 else
        if (Define.BUSINESS_TYPE_SALE_SELL.equals(persistedSale.getType())) { // 销货
            // 新增 应收账款记录 fc_receivable
            receivableService.businessAdd(persistedSale.getCustomerId(), persistedSale.getIssueDate(),
                    Define.BUSINESS_TYPE_SALE_SELL, persistedSale.getId(), persistedSale.getDebtAmount(), persistedSale.getCurrentAmount());

        } else { // 销退
            // 新增 应收账款记录 fc_receivable
            receivableService.businessAdd(persistedSale.getCustomerId(), persistedSale.getIssueDate(),
                    Define.BUSINESS_TYPE_SALE_RETURNED, persistedSale.getId(), persistedSale.getDebtAmount(), persistedSale.getCurrentAmount());
        }
    }

    /**
     * 根据 已收款金额 和 优惠后金额 判定 收/退款状态
     */
    private int resolveSaleStatus(double preferredAmount, double collectedAmount) {
        // 若 已收款金额 <= 0，则 状态为 未收/退款
        if (collectedAmount <= 0) {
            return Define.SALE_STATUS_UNPAID;
        }

        // 若 已收款金额 > 0，且已收款金额 < 优惠后金额，则 状态为 部分收/退款
        if (collectedAmount > 0 && collectedAmount < preferredAmount) {
            return Define.SALE_STATUS_PARTIAL;
        }

        // 否则 为已收款金额 >= 优惠后金额，状态为 全部收/退款
        return Define.SALE_STATUS_PAID;
    }
}
