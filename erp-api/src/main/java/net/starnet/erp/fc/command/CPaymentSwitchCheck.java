package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Payment;
import net.starnet.erp.fc.service.PaymentService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 付款单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkPayment}。
 */
@Command
public class CPaymentSwitchCheck extends BaseCommand {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String paymentId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Payment payment = paymentService.getById(paymentId);
        Assert.notNull(payment, "ID为【" + paymentId + "】的付款单不存在！");

        // 委托统一审核服务，避免与 CPaymentSave 重复维护审核逻辑
        saveAuditService.checkPayment(payment, auditorId);
        data.put("payment", payment);
    }
}
