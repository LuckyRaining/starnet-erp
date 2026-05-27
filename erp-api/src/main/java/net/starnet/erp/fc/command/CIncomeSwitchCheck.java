package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Income;
import net.starnet.erp.fc.service.IncomeService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 其他收入单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkIncome}。
 */
@Command
public class CIncomeSwitchCheck extends BaseCommand {

    @Autowired
    private IncomeService incomeService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String incomeId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Income income = incomeService.getById(incomeId);
        Assert.notNull(income, "ID为【" + incomeId + "】的其他收入单不存在！");

        // 委托统一审核服务，避免与 CIncomeSave 重复维护审核逻辑
        saveAuditService.checkIncome(income, auditorId);
        data.put("income", income);
    }
}
