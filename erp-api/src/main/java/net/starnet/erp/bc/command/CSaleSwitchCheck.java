package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.service.SaveAuditService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 销售单 切换审核
 * <p>
 * 单向审核：仅允许 false → true。具体业务见 {@link SaveAuditService#checkSale}。
 */
@Command
public class CSaleSwitchCheck extends BaseCommand {

    @Autowired
    private SaleService saleService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private String saleId;
    /** 审核人 ID，由前端 List 页传入当前登录用户 */
    @Param
    private String auditorId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Sale sale = saleService.getById(saleId);
        Assert.notNull(sale, "ID为【" + saleId + "】的销售订单不存在！");

        // 委托统一审核服务，避免与 CSaleSave 重复维护审核逻辑
        saveAuditService.checkSale(sale, auditorId);
        data.put("sale", sale);
    }
}
