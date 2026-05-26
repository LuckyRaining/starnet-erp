package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Expense;
import net.starnet.erp.fc.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CExpenseSwitchCheck extends BaseCommand {

    @Autowired
    private ExpenseService expenseService;

    @Param(required = true)
    private String expenseId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Expense expense = expenseService.getById(expenseId);
        Assert.notNull(expense, "ID为【" + expenseId + "】的其他支出单不存在！");

        expense.setChecked(!expense.isChecked());
        expenseService.saveOrUpdate(expense);

        data.put("expense", expense);
    }
}
