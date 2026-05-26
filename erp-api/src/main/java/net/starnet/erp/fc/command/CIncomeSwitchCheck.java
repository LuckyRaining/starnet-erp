package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Income;
import net.starnet.erp.fc.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CIncomeSwitchCheck extends BaseCommand {

    @Autowired
    private IncomeService incomeService;

    @Param(required = true)
    private String incomeId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Income income = incomeService.getById(incomeId);
        Assert.notNull(income, "ID为【" + incomeId + "】的其他收入单不存在！");

        income.setChecked(!income.isChecked());
        incomeService.saveOrUpdate(income);

        data.put("income", income);
    }
}
