package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 其他出库单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkCheckout}。
 */
@Command
public class CCheckoutSwitchCheck extends BaseCommand {

    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String checkoutId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Checkout checkout = checkoutService.getById(checkoutId);
        Assert.notNull(checkout, "ID为【" + checkoutId + "】的其他出库单不存在！");

        // 委托统一审核服务，避免与 CCheckoutSave 重复维护审核逻辑
        saveAuditService.checkCheckout(checkout, auditorId);
        data.put("checkout", checkout);
    }
}
