package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.OrderService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.uc.model.*;
import net.starnet.erp.uc.service.*;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.CustomerService;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.SettlementAccountService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户订单保存
 */
@Command
public class COrderSave extends BaseCommand {

    @Autowired
    private OrderService orderService;
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

    @Param(required = true)
    private Order order;
    @Param(defaultValue = "[]")
    private List<IssueProduct> productList;

    private Order persistedOrder;
    
    @Override
    protected void init() throws Exception {
        // BizException 为后端展示
        if (!Define.validateOrderBusinessType(order.getBusinessType())) {
            throw new BizException("客户订单类型不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        // 校验数据
        Assert.notBlank(order.getCustomerId(), "客户ID不能为空！");
        Customer customer = customerService.getById(order.getCustomerId());
        Assert.notNull(customer, "ID为【" + order.getCustomerId() + "】的客户不存在！");

        // 同 init() 初始化校验，但 Assert 为前端展示
        Assert.notFalse(Define.validateOrderBusinessType(order.getBusinessType()), "客户订单类型不正确！");

        // 计算
        // 客户订单ID（更新时必填，新增时不传）
        if (StrKit.isBlank(order.getId())) { // order.id 为空时，即没传，为“新增”的意思
            persistedOrder = new Order();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增客户订单
            validateOrderCode(order.getCode());
            persistedOrder.setCode(order.getCode());

            // 初始化 客户订单 的 checked 为 false
            persistedOrder.setChecked(false);

        } else { // order.id 非空时，即传了，为“更新”的意思
            persistedOrder = orderService.getById(order.getId());
            Assert.notNull(persistedOrder, "ID为【" + order.getId() + "】的客户订单不存在！");

            // ！不涉及！ 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
            // ！不涉及！ 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record

            // 删除 该单原来的商品列表 productList[]
            // 即 删除 单据商品 wc_issue_product
            issueProductService.deleteByBusiness(order.getId());

            // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

            // ！不涉及！ 删除 该单原来的账户列表 accountList[]
            // ！不涉及！ 即 删除 单据账户 fc_account_record

            // ！不涉及！ 删除 该单原来的 应付账款记录/应收账款记录
            // ！不涉及！ 即 删除 应付账款记录 fc_payable / 应收账款记录 fc_receivable
        }

        persistedOrder.setIssueDate(order.getIssueDate());
        persistedOrder.setDeliveryDate(order.getDeliveryDate());
        persistedOrder.setBusinessType(order.getBusinessType());
        persistedOrder.setCustomerId(order.getCustomerId());
        persistedOrder.setTotalAmount(order.getTotalAmount());
        persistedOrder.setDiscountedAmount(order.getDiscountedAmount());
        persistedOrder.setQuantity(getQuantity());
        persistedOrder.setDiscountRate(order.getDiscountRate());
        persistedOrder.setListerId(order.getListerId());
        persistedOrder.setAuditorId(order.getAuditorId());
        persistedOrder.setRemark(order.getRemark());
        // 新增/更新 客户订单 bc_order
        orderService.saveOrUpdate(persistedOrder);

        // 新增 单据的 商品列表 productList[]
        addProductList();

        // ！不涉及！ 新增 单据的 账户列表 accountList[]

        // ！不涉及！ 新增 应付账款记录/应收账款记录

        data.put("order", persistedOrder);
    }

    /**
     * 校验 单据编号 是否合法
     *
     * @param code
     */
    private void validateOrderCode(String code) {
        Order testOrder = orderService.findByCode(code);
        if (testOrder != null) {
            Assert.notNull(testOrder, "单据编号为【" + code + "】的客户订单已经存在！");
            throw new BizException("单据编号为【" + code + "】的客户订单已经存在！");
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
     * 新增 单据的 商品列表 productList[]
     */
    private void addProductList() {
        if (productList == null || productList.size() == 0) return;

        List<IssueProduct> persistedIssueProductList = new ArrayList<>();
        IssueProduct persistedIssueProduct;
        for (IssueProduct orderProduct : productList) {
            Assert.notBlank(orderProduct.getProductId(), "商品ID不能为空！");
            Product product = productService.getById(orderProduct.getProductId());
            Assert.notNull(product, "ID为【" + orderProduct.getProductId() + "】的商品不存在！");

            Assert.notBlank(orderProduct.getWarehouseId(), "仓库ID不能为空！");
            Warehouse warehouse = warehouseService.getById(orderProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + orderProduct.getWarehouseId() + "】的仓库不存在！");


            persistedIssueProduct = new IssueProduct();
            persistedIssueProduct.setBusinessType(Define.BUSINESS_TYPE_SALE_ORDER);
            persistedIssueProduct.setBusinessId(persistedOrder.getId());
            persistedIssueProduct.setProductId(product.getId());
            persistedIssueProduct.setWarehouseId(warehouse.getId());

            // TODO 校验数据
            persistedIssueProduct.setQuantity(orderProduct.getQuantity());
            // 设置 单价：使用商品的 “预计采购价” 作为库存成本对外展示的 “单价”，而非 “售价” or “批发价”。
            persistedIssueProduct.setPrice(product.getEstimatedPurchasePrice());
            // persistedIssueProduct.setPrice(orderProduct.getPrice());
            persistedIssueProduct.setDiscountRate(orderProduct.getDiscountRate());
            persistedIssueProduct.setDiscountAmount(orderProduct.getDiscountAmount());
            persistedIssueProduct.setAmount(orderProduct.getAmount());

            // TODO 需不需要设置 单据编号？
            // persistedIssueProduct.setCode(orderProduct.getCode());
             persistedIssueProduct.setCode(orderProduct.getCode());

            persistedIssueProduct.setRemark(orderProduct.getRemark());

            // ！不涉及！ 处理 库存：订货 为 入库，退货 为 出库
            // ！不涉及！ 更新 库存商品 wc_stock，新增 出入库记录 wc_stock_record

            persistedIssueProductList.add(persistedIssueProduct);
        }

        // 新增 单据商品 wc_issue_product
        issueProductService.saveBatch(persistedIssueProductList);
    }
}
