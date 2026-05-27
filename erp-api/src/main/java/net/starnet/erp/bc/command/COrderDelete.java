package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.service.OrderService;
import net.starnet.erp.wc.service.IssueProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户订单删除
 */
@Command
@Transactional
public class COrderDelete extends BaseCommand {

    @Autowired
    private OrderService orderService;
    @Autowired
    private IssueProductService issueProductService;

    /** 客户订单ID */
    @Param(required = true)
    private String orderId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Order order = orderService.getById(orderId);
        Assert.notNull(order, "ID为【" + orderId + "】的客户订单不存在！");
        Assert.notFalse(!order.isChecked(), "已审核的客户订单不能删除！");

        // 删除 该单原来的信息
        // 即 删除 客户订单 bc_order
        orderService.removeById(orderId);

        // ！不涉及！ 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // ！不涉及！ 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record

        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_issue_product
        issueProductService.deleteByBusiness(orderId);

        // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

        // ！不涉及！ 删除 该单原来的账户列表 accountList[]
        // ！不涉及！ 即 删除 单据账户 fc_account_record

        // ！不涉及！ 删除 该单原来的 应付账款记录/应收账款记录
        // ！不涉及！ 即 删除 应付账款记录 fc_payable / 应收账款记录 fc_receivable
    }
}
