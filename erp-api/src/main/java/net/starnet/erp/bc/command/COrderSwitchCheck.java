package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.service.OrderService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 客户订单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkOrder}。
 */
@Command
public class COrderSwitchCheck extends BaseCommand {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String orderId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Order order = orderService.getById(orderId);
        Assert.notNull(order, "ID为【" + orderId + "】的客户订单不存在！");

        // 委托统一审核服务，避免与 COrderSave 重复维护审核逻辑
        saveAuditService.checkOrder(order, auditorId);
        data.put("order", order);
    }
}
