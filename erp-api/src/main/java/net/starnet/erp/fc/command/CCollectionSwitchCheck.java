package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.service.CollectionService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 收款单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkCollection}。
 */
@Command
public class CCollectionSwitchCheck extends BaseCommand {

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String collectionId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Collection collection = collectionService.getById(collectionId);
        Assert.notNull(collection, "ID为【" + collectionId + "】的收款单不存在！");

        // 委托统一审核服务，避免与 CCollectionSave 重复维护审核逻辑
        saveAuditService.checkCollection(collection, auditorId);
        data.put("collection", collection);
    }
}
