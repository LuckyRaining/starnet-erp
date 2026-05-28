package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.Expense;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.model.FlowRecord;
import net.starnet.erp.fc.service.ExpenseService;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.FlowRecordService;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 支出单保存
 */
@Command
public class CExpenseSave extends BaseCommand {

    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private FlowRecordService flowRecordService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Expense expense;
    @Param(defaultValue = "[]")
    private List<FlowRecord> recordList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Expense persistedExpense;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 计算
        if (StrKit.isBlank(expense.getId())) { // expense.id 为空时，即没传，为“新增”的意思
            persistedExpense = new Expense();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增支出单
            validateCode(expense.getCode());
            persistedExpense.setCode(expense.getCode());
            
            // 初始化 支出单 的 checked 为 false
            persistedExpense.setChecked(false);

        } else { // expense.id 非空时，即传了，为“更新”的意思
            persistedExpense = expenseService.getById(expense.getId());
            Assert.notNull(persistedExpense, "ID为【" + expense.getId() + "】的支出订单不存在！");

            // 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // 即 回滚 结算账户 uc_settlement_account
            settlementAccountService.rollbackByBusiness(expense.getId());
            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(expense.getId());

            // 删除 该单原来的收支记录列表 recordList[]
            // 即 删除 收支记录 fc_flow_record
            flowRecordService.deleteByBusiness(expense.getId());
        }

        persistedExpense.setSupplierId(expense.getSupplierId());
        persistedExpense.setIssueDate(expense.getIssueDate());
        persistedExpense.setAmount(expense.getAmount());
        persistedExpense.setPaidAmount(expense.getPaidAmount());
        persistedExpense.setListerId(expense.getListerId());

        // 是否需要 审核？
        // 新增保存 其他支出单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(expense.getAuditorId()) && !expense.isChecked();

        persistedExpense.setAuditorId(expense.getAuditorId());
        persistedExpense.setRemark(expense.getRemark());
        expenseService.saveOrUpdate(persistedExpense);

        // 新增账户
        for (AccountRecord record : accountList) {
            record.setAmount(persistedExpense.getPaidAmount());
        }
        accountRecordService.addRecordList(accountList, Define.ACCOUNT_RECORD_TYPE_OUT, persistedExpense.getIssueDate(), Define.BUSINESS_TYPE_EXPENSE, persistedExpense.getId());

        // 新增单据
        addRecordList();

        // 新增保存时：Save 页已选审核人但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CExpenseSwitchCheck 一致）
        if (shouldCheck) {
            saveAuditService.checkExpense(persistedExpense, persistedExpense.getAuditorId());
        }

        data.put("expense", persistedExpense);
    }

    /**
     * 校验单据编号是否合法
     *
     * @param code
     */
    private void validateCode(String code) {
        Expense Expense = expenseService.findByCode(code);
        if (Expense != null) {
            throw new BizException("单据编号为【" + code + "】的支出单已经存在！");
        }
    }

    /**
     * 新增记录列表
     */
    private void addRecordList() {
        if (recordList == null || recordList.size() == 0) return;

        List<FlowRecord> persistedRecordList = new ArrayList<>();
        FlowRecord persistedRecord;
        for (FlowRecord record : recordList) {
            persistedRecord = new FlowRecord();
            persistedRecord.setIssueDate(persistedExpense.getIssueDate());
            persistedRecord.setBusinessType(Define.BUSINESS_TYPE_EXPENSE);
            persistedRecord.setBusinessId(persistedExpense.getId());
            persistedRecord.setCategoryId(record.getCategoryId());
            persistedRecord.setAmount(record.getAmount());
            persistedRecord.setRemark(record.getRemark());

            persistedRecordList.add(persistedRecord);
        }

        flowRecordService.saveBatch(persistedRecordList);
    }
}
