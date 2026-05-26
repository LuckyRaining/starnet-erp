package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CStoreSwitchCheck extends BaseCommand {

    @Autowired
    private StoreService storeService;

    @Param(required = true)
    private String storeId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Store store = storeService.getById(storeId);
        Assert.notNull(store, "ID为【" + storeId + "】的其他入库单不存在！");

        store.setChecked(!store.isChecked());
        storeService.saveOrUpdate(store);

        data.put("store", store);
    }
}
