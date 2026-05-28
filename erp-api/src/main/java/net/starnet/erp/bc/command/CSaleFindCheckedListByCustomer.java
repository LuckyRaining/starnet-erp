package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.service.CollectionIssueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 获取 用户已审核的 销售单
 */
@Command
public class CSaleFindCheckedListByCustomer extends BaseCommand {

    @Autowired
    private SaleService saleService;
    @Autowired
    private CollectionIssueService collectionIssueService;

    /** 客户ID，可选；不传时返回全部已审核销货/销退单 */
    // @Param(required = true)
    @Param
    private String customerId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        List<Sale> saleList = saleService.findCheckedListByCustomer(customerId);
        // 如果 最新收款单据明细 不存在，则不显示该销货单、销退单
        saleList.removeIf(sale -> !appendVerifiedAmount(sale));

        data.put("saleList", saleList);
    }

    /**
     * 根据源单编号查询 fc_collection_issue 最新一条记录，回填 已核销/未核销金额
     */
    private boolean appendVerifiedAmount(Sale sale) {
        CollectionIssue collectionIssue = null;
        if (StrKit.notBlank(sale.getCode())) {
            collectionIssue = collectionIssueService.findLatestBySourceCode(sale.getCode());
        } else {
            return false;
        }

        if (collectionIssue != null) {
            // 如果 最新收款单据明细 存在，则回填 已核销/未核销金额，并返回 true
            sale.put("verifiedAmount", collectionIssue.getVerifiedAmount());
            sale.put("unverifiedAmount", collectionIssue.getUnverifiedAmount());
            return true;
        } else {
            // 如果 最新收款单据明细 不存在，则返回 false
            return false;
        }
    }
}
