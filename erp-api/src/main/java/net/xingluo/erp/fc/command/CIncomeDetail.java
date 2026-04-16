package net.xingluo.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.xingluo.erp.fc.model.Income;
import net.xingluo.erp.fc.model.AccountRecord;
import net.xingluo.erp.fc.model.FlowRecord;
import net.xingluo.erp.fc.service.AccountRecordService;
import net.xingluo.erp.fc.service.FlowRecordService;
import net.xingluo.erp.fc.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 收入单详情
 */
@Command
public class CIncomeDetail extends BaseCommand {

    @Autowired
    private IncomeService incomeService;
    @Autowired
    private AccountRecordService accountService;
    @Autowired
    private FlowRecordService recordService;

    @Param(required = true)
    private String incomeId; // 收入订单ID
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Income income = incomeService.getById(incomeId);
        Assert.notNull(income, "ID为【" + incomeId + "】的收入订单不存在！");

        List<AccountRecord> accountList = accountService.findListByBusiness(income.getId());
        income.put("accountList", accountList);

        List<FlowRecord> recordList = recordService.findListByBusiness(income.getId());
        income.put("recordList", recordList);

        data.put("income", income);
    }
}
