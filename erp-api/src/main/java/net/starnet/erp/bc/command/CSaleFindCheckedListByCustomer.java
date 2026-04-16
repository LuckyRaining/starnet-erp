package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 获取用户已审核的销售单
 */
@Command
public class CSaleFindCheckedListByCustomer extends BaseCommand {

    @Autowired
    private SaleService saleService;

    @Param(required = true)
    private String customerId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        List<Sale> saleList = saleService.findCheckedListByCustomer(customerId);
        for (Sale sale : saleList) {
            sale.put("verifiedAmount", 0);
            sale.put("unverifiedAmount", 0);
        }

        data.put("saleList", saleList);
    }
}
