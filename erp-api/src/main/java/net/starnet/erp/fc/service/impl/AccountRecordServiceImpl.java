package net.starnet.erp.fc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.dao.AccountRecordDao;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.uc.model.SettlementAccount;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class AccountRecordServiceImpl extends ServiceImpl<AccountRecordDao, AccountRecord> implements AccountRecordService {

    private static final SimpleDateFormat codeFormatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final Random random = new Random();

    @Autowired
    private AccountRecordDao recordDao;

    @Autowired
    private SettlementAccountService accountService;

    @Override
    public List<AccountRecord> findListByBusiness(String businessId) {
        return this.list(new QueryWrapper<AccountRecord>().eq("businessId", businessId));
    }

    @Override
    public void deleteByBusiness(String businessId) {
        this.remove(new QueryWrapper<AccountRecord>().eq("businessId", businessId));
    }

    /**
     * 生成结算号：JS + 17位时间戳 + 2位随机数
     */
    private String generateSettlementCode() {
        return "JS" + codeFormatter.format(new Date()) + random.nextInt(10) + random.nextInt(10);
    }

    @Override
    public void addRecordList(List<AccountRecord> recordList, String type, String issueDate, String businessType, String businessId) {
        if (recordList == null || recordList.size() == 0) return;

        List<AccountRecord> persistedPurchaseAccountList = new ArrayList<>();
        AccountRecord persistedRecord;
        for (AccountRecord record : recordList) {
            Assert.notBlank(record.getAccountId(), "结算账户的ID不能为空！");
            SettlementAccount account = accountService.getById(record.getAccountId());
            Assert.notNull(account, "ID为【" + record.getAccountId() + "】的结算账户不存在！");

            persistedRecord = new AccountRecord();
            persistedRecord.setType(type);
            persistedRecord.setIssueDate(issueDate);
            persistedRecord.setBusinessType(businessType);

            persistedRecord.setBusinessId(businessId);
            persistedRecord.setAccountId(record.getAccountId());

            // TODO 校验 结算金额
            persistedRecord.setAmount(record.getAmount());

            // 自动生成结算号
            persistedRecord.setSettlementCode(StrKit.isBlank(record.getSettlementCode()) ?
                    generateSettlementCode() : record.getSettlementCode());
            persistedRecord.setSettlementType(record.getSettlementType());

            if (Define.ACCOUNT_RECORD_TYPE_IN.equals(type)) {
                account.setCurrentBalance(account.getCurrentBalance() + record.getAmount());
            } else {
                account.setCurrentBalance(account.getCurrentBalance() - record.getAmount());
            }

            // 更新 结算账户 uc_settlement_account
            accountService.updateById(account);

            persistedRecord.setCurrentAmount(account.getCurrentBalance());
            persistedRecord.setRemark(record.getRemark());

            persistedPurchaseAccountList.add(persistedRecord);
        }

        // 新增 单据账户 fc_account_record
        saveBatch(persistedPurchaseAccountList);
    }

    @Override
    public List<AccountRecord> analysisList(String startDate, String endDate, List<String> accountIdList) {
        QueryWrapper<AccountRecord> wrapper = new QueryWrapper<>();
        if (StrKit.notBlank(startDate)) {
            wrapper.ge("issueDate", startDate);
        }
        if (StrKit.notBlank(endDate)) {
            wrapper.le("issueDate", endDate);
        }
        if (accountIdList != null && accountIdList.size() > 0) {
            wrapper.in("accountId", accountIdList);
        }
        return this.list(wrapper.orderByAsc("accountId", "issueDate", "createdTime"));
    }

}
