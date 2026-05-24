package net.starnet.erp.uc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.SettlementAccount;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

/**
 * 账户列表
 */
@Command
public class CSettlementAccountList extends BaseCommand {

    @Autowired
    private SettlementAccountService accountService;
    @Autowired
    private DictItemService itemService;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        List<SettlementAccount> accountList = accountService.list();
        // 按时间倒序：日期最新的账户排在最前面
        accountList.sort((o1, o2) -> o2.getBalanceTime().compareTo(o1.getBalanceTime()));

        for (SettlementAccount account : accountList) {
            DictItem item = itemService.getById(account.getType());
            account.put("typeName", item.getName());
        }

        data.put("accountList", accountList);
    }
}
