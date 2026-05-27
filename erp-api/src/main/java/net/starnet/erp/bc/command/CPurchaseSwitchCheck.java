package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 购货单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkPurchase}。
 */
@Command
public class CPurchaseSwitchCheck extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String purchaseId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Purchase purchase = purchaseService.getById(purchaseId);
        Assert.notNull(purchase, "ID为【" + purchaseId + "】的购货单不存在！");

        // 委托统一审核服务，避免与 CPurchaseSave 重复维护审核逻辑
        saveAuditService.checkPurchase(purchase, auditorId);

        data.put("purchase", purchase);
    }
}
