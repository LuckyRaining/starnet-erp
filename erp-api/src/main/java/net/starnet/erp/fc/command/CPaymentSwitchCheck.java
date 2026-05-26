package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Payment;
import net.starnet.erp.fc.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CPaymentSwitchCheck extends BaseCommand {

    @Autowired
    private PaymentService paymentService;

    @Param(required = true)
    private String paymentId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Payment payment = paymentService.getById(paymentId);
        Assert.notNull(payment, "ID为【" + paymentId + "】的付款单不存在！");

        payment.setChecked(!payment.isChecked());
        paymentService.saveOrUpdate(payment);

        data.put("payment", payment);
    }
}
