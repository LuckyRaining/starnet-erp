package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.Income;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.model.FlowRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.FlowRecordService;
import net.starnet.erp.fc.service.IncomeService;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 收入单保存
 */
@Command
public class CIncomeSave extends BaseCommand {

    @Autowired
    private IncomeService incomeService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private FlowRecordService flowRecordService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Income income;
    @Param(defaultValue = "[]")
    private List<FlowRecord> recordList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Income persistedIncome;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 计算
        if (StrKit.isBlank(income.getId())) { // income.id 为空时，即没传，为“新增”的意思
            persistedIncome = new Income();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增收入单
            validateCode(income.getCode());
            persistedIncome.setCode(income.getCode());
            
            // 初始化 收入单 的 checked 为 false
            persistedIncome.setChecked(false);

        } else { // income.id 非空时，即传了，为“更新”的意思
            persistedIncome = incomeService.getById(income.getId());
            Assert.notNull(persistedIncome, "ID为【" + income.getId() + "】的收入订单不存在！");

            // 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // 即 回滚 结算账户 uc_settlement_account
            settlementAccountService.rollbackByBusiness(income.getId());
            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(income.getId());

            // 删除 该单原来的收支记录列表 flowRecordList[]
            // 即 删除 收支记录 fc_flow_record
            flowRecordService.deleteByBusiness(income.getId());
        }

        persistedIncome.setCustomerId(income.getCustomerId());
        persistedIncome.setIssueDate(income.getIssueDate());
        persistedIncome.setCollectAmount(income.getCollectAmount());
        persistedIncome.setListerId(income.getListerId());

        // 是否需要 审核？
        // 新增保存 其他收入单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(income.getAuditorId()) && !income.isChecked();

        persistedIncome.setAuditorId(income.getAuditorId());
        persistedIncome.setRemark(income.getRemark());
        incomeService.saveOrUpdate(persistedIncome);

        // 新增账户
        for (AccountRecord record : accountList) {
            record.setAmount(persistedIncome.getCollectAmount());
        }
        accountRecordService.addRecordList(accountList, Define.ACCOUNT_RECORD_TYPE_IN, persistedIncome.getIssueDate(), Define.BUSINESS_TYPE_INCOME, persistedIncome.getId());

        // 新增单据
        addRecordList();

        // 新增保存时：Save 页已选审核人但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CIncomeSwitchCheck 一致）
        if (shouldCheck) {
            saveAuditService.checkIncome(persistedIncome, persistedIncome.getAuditorId());
        }

        data.put("income", persistedIncome);
    }

    /**
     * 校验单据编号是否合法
     *
     * @param code
     */
    private void validateCode(String code) {
        Income Income = incomeService.findByCode(code);
        if (Income != null) {
            throw new BizException("单据编号为【" + code + "】的收入单已经存在！");
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
            persistedRecord.setIssueDate(persistedIncome.getIssueDate());
            persistedRecord.setBusinessType(Define.BUSINESS_TYPE_INCOME);
            persistedRecord.setBusinessId(persistedIncome.getId());
            persistedRecord.setCategoryId(record.getCategoryId());
            persistedRecord.setAmount(record.getAmount());
            persistedRecord.setRemark(record.getRemark());

            persistedRecordList.add(persistedRecord);
        }

        flowRecordService.saveBatch(persistedRecordList);
    }
}
