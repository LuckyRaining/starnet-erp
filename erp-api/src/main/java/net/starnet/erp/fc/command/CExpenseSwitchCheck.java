package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Expense;
import net.starnet.erp.fc.service.ExpenseService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 其他支出单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkExpense}。
 */
@Command
public class CExpenseSwitchCheck extends BaseCommand {

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String expenseId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Expense expense = expenseService.getById(expenseId);
        Assert.notNull(expense, "ID为【" + expenseId + "】的其他支出单不存在！");

        // 委托统一审核服务，避免与 CExpenseSave 重复维护审核逻辑
        saveAuditService.checkExpense(expense, auditorId);
        data.put("expense", expense);
    }
}
