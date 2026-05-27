package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.wc.model.Store;
import net.starnet.erp.wc.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 其他入库单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkStore}。
 */
@Command
public class CStoreSwitchCheck extends BaseCommand {

    @Autowired
    private StoreService storeService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String storeId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Store store = storeService.getById(storeId);
        Assert.notNull(store, "ID为【" + storeId + "】的其他入库单不存在！");

        // 委托统一审核服务，避免与 CStoreSave 重复维护审核逻辑
        saveAuditService.checkStore(store, auditorId);
        data.put("store", store);
    }
}
