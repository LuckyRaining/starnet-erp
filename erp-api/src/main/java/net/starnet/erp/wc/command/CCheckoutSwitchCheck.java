package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Checkout;
import net.starnet.erp.wc.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CCheckoutSwitchCheck extends BaseCommand {

    @Autowired
    private CheckoutService checkoutService;

    @Param(required = true)
    private String checkoutId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Checkout checkout = checkoutService.getById(checkoutId);
        Assert.notNull(checkout, "ID为【" + checkoutId + "】的其他出库单不存在！");

        checkout.setChecked(!checkout.isChecked());
        checkoutService.saveOrUpdate(checkout);

        data.put("checkout", checkout);
    }
}
