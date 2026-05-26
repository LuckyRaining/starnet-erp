package net.starnet.erp.wc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.wc.model.Transfer;
import net.starnet.erp.wc.service.TransferProductService;
import net.starnet.erp.wc.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 调拨单删除
 */
@Command
public class CTransferDelete extends BaseCommand {

    @Autowired
    private TransferService transferService;
    @Autowired
    private TransferProductService transferProductService;

    /** 调拨单ID */
    @Param(required = true)
    private String transferId;
    
    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Transfer transfer = transferService.getById(transferId);
        Assert.notNull(transfer, "ID为【" + transferId + "】的调拨单不存在！");

        // 删除 该单原来的信息
        // 即 删除 调拨单 wc_transfer
        transferService.removeById(transferId);

        // ！不涉及！ 删除 该单原来的商品列表 productList[] 对应的数据表中的相关库存信息
        // ！不涉及！ 即 回滚 库存商品 wc_stock，删除 出入库记录 wc_stock_record

        // 删除 该单原来的商品列表 productList[]
        // 即 删除 单据商品 wc_transfer_product
        transferProductService.deleteByTransfer(transferId);

        // ！不涉及！ 删除 该单原来的账户列表 accountList[] 对应的收支信息
        // ！不涉及！ 即 回滚 结算账户 uc_settlement_account

        // ！不涉及！ 删除 该单原来的账户列表 accountList[]
        // ！不涉及！ 即 删除 单据账户 fc_account_record

        // ！不涉及！ 删除 该单原来的 应付账款记录/应收账款记录
        // ！不涉及！ 即 删除 应付账款记录 fc_payable / 应收账款记录 fc_receivable
    }
}
