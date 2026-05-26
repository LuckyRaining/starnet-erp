package net.starnet.erp.uc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.uc.dao.SettlementAccountDao;
import net.starnet.erp.uc.model.SettlementAccount;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 结算账户服务实现
 */
@Service
public class SettlementAccountServiceImpl extends ServiceImpl<SettlementAccountDao, SettlementAccount> implements SettlementAccountService {

    @Autowired
    private AccountRecordService accountRecordService;

    @Override
    public void rollbackByBusiness(String businessId) {
        List<AccountRecord> recordList = accountRecordService.findListByBusiness(businessId);

        for (AccountRecord record : recordList) {
            SettlementAccount account = getById(record.getAccountId());

            if (account == null) {
                continue;
            }

            if (Define.ACCOUNT_RECORD_TYPE_IN.equals(record.getType())) {
                account.setCurrentBalance(account.getCurrentBalance() - record.getAmount());
            } else {
                account.setCurrentBalance(account.getCurrentBalance() + record.getAmount());
            }

            // 新增/更新 结算账户 uc_settlement_account
            // saveOrUpdate(account);
            // 更新 结算账户 uc_settlement_account
            updateById(account);
        }
    }
}
