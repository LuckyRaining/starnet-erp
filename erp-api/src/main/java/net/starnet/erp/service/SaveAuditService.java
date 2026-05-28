package net.starnet.erp.service;

import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.OrderService;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.model.Expense;
import net.starnet.erp.fc.model.Income;
import net.starnet.erp.fc.model.Payment;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.CollectionIssueService;
import net.starnet.erp.fc.service.CollectionService;
import net.starnet.erp.fc.service.ExpenseService;
import net.starnet.erp.fc.service.IncomeService;
import net.starnet.erp.fc.service.PaymentIssueService;
import net.starnet.erp.fc.service.PaymentService;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.service.CheckoutService;
import net.starnet.erp.wc.service.StoreService;
import net.starnet.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 单据审核业务统一入口。
 * <p>
 * 本类集中维护两类场景的审核逻辑，避免 {@code *Save} 与 {@code *SwitchCheck} 重复实现：
 * <ul>
 *     <li><b>列表页切换</b>：List 页点击审核开关，由对应的 {@code *SwitchCheck} 命令委托本类执行。</li>
 * </ul>
 * <p>
 * 所有业务单据均为<b>单向审核</b>（仅 false → true，审核后不可取消）。
 * 其中购货/销货/订单审核通过后还会生成待核销的收/付款单据明细。
 */
@Service
public class SaveAuditService {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private PaymentIssueService paymentIssueService;
    @Autowired
    private CollectionIssueService collectionIssueService;

    // ==================== 单向审核（bc 业务单据） ====================

    /**
     * 购货单 / 购退单 审核（单向，不可取消）。
     * <p>
     * 审核通过后额外生成 {@code fc_payment_issue}，供后续付款单核销源单。
     *
     * @param purchase  已持久化的购货单
     * @param auditorId 审核人 ID，不可为空
     */
    public void checkPurchase(Purchase purchase, String auditorId) {
        Assert.notFalse(!purchase.isChecked(), "已审核的购货单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        purchase.setChecked(true);
        purchase.setAuditorId(auditorId);
        purchaseService.saveOrUpdate(purchase);

        // 审核通过后，生成尚未关联付款单的付款单据明细
        addPaymentIssue(purchase);
    }

    /**
     * 销货单 / 销退单 审核（单向，不可取消）。
     * <p>
     * 审核通过后额外生成 {@code fc_collection_issue}，供后续收款单核销源单。
     *
     * @param sale      已持久化的销货单
     * @param auditorId 审核人 ID，不可为空
     */
    public void checkSale(Sale sale, String auditorId) {
        Assert.notFalse(!sale.isChecked(), "已审核的销货单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        sale.setChecked(true);
        sale.setAuditorId(auditorId);
        saleService.saveOrUpdate(sale);

        // 审核通过后，生成尚未关联收款单的收款单据明细
        addCollectionIssue(sale);
    }

    /**
     * 客户订单 审核（单向，不可取消）。
     * <p>
     * 审核通过后额外生成 {@code fc_collection_issue}，供后续收款单核销源单。
     *
     * @param order     已持久化的客户订单
     * @param auditorId 审核人 ID，不可为空
     */
    public void checkOrder(Order order, String auditorId) {
        Assert.notFalse(!order.isChecked(), "已审核的客户订单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        order.setChecked(true);
        order.setAuditorId(auditorId);
        orderService.saveOrUpdate(order);

        // 审核通过后，生成尚未关联收款单的收款单据明细
        addCollectionIssue(order);
    }

    // ==================== 单向审核（fc 业务单据） ====================

    /**
     * 付款单 审核（单向，不可取消）。
     */
    public void checkPayment(Payment payment, String auditorId) {
        Assert.notFalse(!payment.isChecked(), "已审核的付款单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        payment.setChecked(true);
        payment.setAuditorId(auditorId);
        paymentService.saveOrUpdate(payment);
    }

    /**
     * 收款单 审核（单向，不可取消）。
     */
    public void checkCollection(Collection collection, String auditorId) {
        Assert.notFalse(!collection.isChecked(), "已审核的收款单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        collection.setChecked(true);
        collection.setAuditorId(auditorId);
        collectionService.saveOrUpdate(collection);
    }

    /**
     * 其他收入单 审核（单向，不可取消）。
     */
    public void checkIncome(Income income, String auditorId) {
        Assert.notFalse(!income.isChecked(), "已审核的其他收入单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        income.setChecked(true);
        income.setAuditorId(auditorId);
        incomeService.saveOrUpdate(income);
    }

    /**
     * 其他支出单 审核（单向，不可取消）。
     */
    public void checkExpense(Expense expense, String auditorId) {
        Assert.notFalse(!expense.isChecked(), "已审核的其他支出单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        expense.setChecked(true);
        expense.setAuditorId(auditorId);
        expenseService.saveOrUpdate(expense);
    }

    // ==================== 单向审核（wc 业务单据） ====================

    /**
     * 其他入库单 审核（单向，不可取消）。
     */
    public void checkStore(Store store, String auditorId) {
        Assert.notFalse(!store.isChecked(), "已审核的其他入库单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        store.setChecked(true);
        store.setAuditorId(auditorId);
        storeService.saveOrUpdate(store);
    }

    /**
     * 其他出库单 审核（单向，不可取消）。
     */
    public void checkCheckout(Checkout checkout, String auditorId) {
        Assert.notFalse(!checkout.isChecked(), "已审核的其他出库单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        checkout.setChecked(true);
        checkout.setAuditorId(auditorId);
        checkoutService.saveOrUpdate(checkout);
    }

    /**
     * 调拨单 审核（单向，不可取消）。
     */
    public void checkTransfer(Transfer transfer, String auditorId) {
        Assert.notFalse(!transfer.isChecked(), "已审核的调拨单不能取消审核！");
        Assert.notBlank(auditorId, "当前未登录，请登陆后审核！");

        transfer.setChecked(true);
        transfer.setAuditorId(auditorId);
        transferService.saveOrUpdate(transfer);
    }

    // ==================== 审核副作用：生成待核销明细 ====================

    /**
     * 购货单审核通过后，生成尚未关联付款单的 {@code fc_payment_issue}。
     * <p>
     * 跳过条件：源单编号为空，或该编号已存在 orphan 明细（{@code paymentId IS NULL}）。
     */
    private void addPaymentIssue(Purchase purchase) {
        if (StrKit.isBlank(purchase.getCode())) {
            return;
        }
        if (paymentIssueService.findOldestBySourceCode(purchase.getCode()) != null) {
            return;
        }

        PaymentIssue paymentIssue = new PaymentIssue();
        paymentIssue.setPaymentId(null);
        paymentIssue.setSourceCode(purchase.getCode());
        paymentIssue.setType(resolvePaymentIssueType(purchase.getType()));
        paymentIssue.setIssueDate(purchase.getIssueDate());
        paymentIssue.setIssueAmount(purchase.getPreferredAmount()); // 设置 单据金额 issueAmount 为 购货单的 优惠后金额 preferentialAmount
        paymentIssue.setVerifiedAmount(purchase.getCurrentAmount());
        paymentIssue.setUnverifiedAmount(purchase.getDebtAmount());
        paymentIssue.setCurrentVerifiedAmount(purchase.getCurrentAmount());
        paymentIssueService.save(paymentIssue);
    }

    /**
     * 销货单审核通过后，生成尚未关联收款单的 {@code fc_collection_issue}。
     */
    private void addCollectionIssue(Sale sale) {
        if (StrKit.isBlank(sale.getCode())) {
            return;
        }
        if (collectionIssueService.findOldestBySourceCode(sale.getCode()) != null) {
            return;
        }

        CollectionIssue collectionIssue = new CollectionIssue();
        collectionIssue.setCollectionId(null);
        collectionIssue.setSourceCode(sale.getCode());
        collectionIssue.setType(resolveSaleCollectionIssueType(sale.getType()));
        collectionIssue.setIssueDate(sale.getIssueDate());
        collectionIssue.setIssueAmount(sale.getPreferredAmount()); // 设置 单据金额 issueAmount 为 销货单的 优惠后金额 preferentialAmount
        collectionIssue.setVerifiedAmount(sale.getCurrentAmount());
        collectionIssue.setUnverifiedAmount(sale.getDebtAmount());
        collectionIssue.setCurrentVerifiedAmount(sale.getCurrentAmount());
        collectionIssueService.save(collectionIssue);
    }

    /**
     * 客户订单审核通过后，生成尚未关联收款单的 {@code fc_collection_issue}。
     */
    private void addCollectionIssue(Order order) {
        if (StrKit.isBlank(order.getCode())) {
            return;
        }
        if (collectionIssueService.findOldestBySourceCode(order.getCode()) != null) {
            return;
        }

        CollectionIssue collectionIssue = new CollectionIssue();
        collectionIssue.setCollectionId(null);
        collectionIssue.setSourceCode(order.getCode());
        collectionIssue.setType(resolveOrderCollectionIssueType(order.getBusinessType()));
        collectionIssue.setIssueDate(order.getIssueDate());
        collectionIssue.setIssueAmount(order.getTotalAmount());
        collectionIssue.setVerifiedAmount(0);
        collectionIssue.setUnverifiedAmount(order.getTotalAmount());
        collectionIssue.setCurrentVerifiedAmount(0);
        collectionIssueService.save(collectionIssue);
    }

    /** 购货类型 → 付款单据明细类型 */
    private int resolvePaymentIssueType(String purchaseType) {
        if (Define.BUSINESS_TYPE_PURCHASE_BUY.equals(purchaseType)) {
            return Define.PAYMENT_ISSUE_TYPE_PURCHASE_BUY;
        }
        return Define.PAYMENT_ISSUE_TYPE_PURCHASE_REFUND;
    }

    /** 销货类型 → 收款单据明细类型 */
    private int resolveSaleCollectionIssueType(String saleType) {
        if (Define.BUSINESS_TYPE_SALE_SELL.equals(saleType)) {
            return Define.COLLECTION_ISSUE_TYPE_SALE_SELL;
        }
        return Define.COLLECTION_ISSUE_TYPE_SALE_RETURNED;
    }

    /** 客户订单类型 → 收款单据明细类型 */
    private int resolveOrderCollectionIssueType(int orderType) {
        if (Define.ORDER_BUSINESS_TYPE_ORDER == orderType) {
            return Define.COLLECTION_ISSUE_TYPE_ORDER_ORDER;
        }
        return Define.COLLECTION_ISSUE_TYPE_ORDER_RETURN;
    }
}
