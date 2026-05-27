package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.service.CollectionIssueService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 销售单 切换审核
 */
@Command
public class CSaleSwitchCheck extends BaseCommand {

    @Autowired
    private SaleService saleService;
    @Autowired
    private CollectionIssueService collectionIssueService;

    @Param(required = true)
    private String saleId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Sale sale = saleService.getById(saleId);
        Assert.notNull(sale, "ID为【" + saleId + "】的销售订单不存在！");
        Assert.notFalse(!sale.isChecked(), "已审核的销货单不能取消审核！");

        sale.setChecked(true);
        // 更新 销售订单 bc_sale
        saleService.saveOrUpdate(sale);

        // 审核通过后，生成 尚未关联收款单的 收款单据明细
        addCollectionIssue(sale);

        data.put("sale", sale);
    }

    /**
     * 审核通过后，生成尚未关联收款单的 收款单据明细
     */
    private void addCollectionIssue(Sale sale) {
        // 如果 源单编号 为空，则不生成 收款单据明细
        if (StrKit.isBlank(sale.getCode())) {
            return;
        }
        // 如果 源单编号 已生成收款单据明细，则不生成 新的收款单据明细
        if (collectionIssueService.findOldestBySourceCode(sale.getCode()) != null) {
            return;
        }

        CollectionIssue collectionIssue = new CollectionIssue();
        collectionIssue.setCollectionId(null);
        collectionIssue.setSourceCode(sale.getCode());
        collectionIssue.setType(resolveCollectionIssueType(sale.getType()));
        collectionIssue.setIssueDate(sale.getIssueDate());
        collectionIssue.setIssueAmount(sale.getAmount());
        collectionIssue.setVerifiedAmount(0);
        collectionIssue.setUnverifiedAmount(sale.getDebtAmount());
        collectionIssue.setCurrentVerifiedAmount(sale.getCurrentAmount());
        // 新增 收款单据明细 fc_collection_issue
        collectionIssueService.save(collectionIssue);
    }

    private int resolveCollectionIssueType(String saleType) {
        if (Define.BUSINESS_TYPE_SALE_SELL.equals(saleType)) {
            return Define.COLLECTION_ISSUE_TYPE_SALE_SELL;
        }
        return Define.COLLECTION_ISSUE_TYPE_SALE_RETURNED;
    }
}
