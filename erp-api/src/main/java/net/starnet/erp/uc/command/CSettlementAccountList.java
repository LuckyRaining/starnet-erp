package net.starnet.erp.uc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.SettlementAccount;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
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
        // 按 余额日期 倒序（即：建账日期）；空日期 排到最后
        accountList.sort((o1, o2) -> {
            Date d1 = o1.getBalanceTime();
            Date d2 = o2.getBalanceTime();
            if (d1 == null && d2 == null) {
                return 0;
            }
            if (d1 == null) {
                return 1;
            }
            if (d2 == null) {
                return -1;
            }
            return d2.compareTo(d1);
        });

        DictItem item;
        for (SettlementAccount account : accountList) {
            // 获取 账户类别 字典项ID
            if (StrKit.notBlank(account.getType())) {
                item = itemService.getById(account.getType());
                if (item != null) {
                    // 设置 账户类别 字典项名称
                    account.put("typeName", item.getName());
                }
            }
        }

        data.put("accountList", accountList);
    }
}
