package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 调拨单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkTransfer}。
 */
@Command
public class CTransferSwitchCheck extends BaseCommand {

    @Autowired
    private TransferService transferService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String transferId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Transfer transfer = transferService.getById(transferId);
        Assert.notNull(transfer, "ID为【" + transferId + "】的调拨订单不存在！");

        // 委托统一审核服务，避免与 CTransferSave 重复维护审核逻辑
        saveAuditService.checkTransfer(transfer, auditorId);
        data.put("transfer", transfer);
    }
}
