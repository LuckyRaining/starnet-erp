package net.starnet.erp.bc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Order;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.OrderService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.service.CollectionIssueService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 客户订单 切换审核
 */
@Command
public class COrderSwitchCheck extends BaseCommand {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CollectionIssueService collectionIssueService;

    @Param(required = true)
    private String orderId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Order order = orderService.getById(orderId);
        Assert.notNull(order, "ID为【" + orderId + "】的客户订单不存在！");
        Assert.notFalse(!order.isChecked(), "已审核的客户订单不能取消审核！");

        order.setChecked(true);
        // 更新 客户订单 bc_order
        orderService.saveOrUpdate(order);

        // 审核通过后，生成尚未关联收款单的收款单据明细
        addCollectionIssue(order);

        data.put("order", order);
    }

    /**
     * 审核通过后，生成尚未关联收款单的 收款单据明细
     */
    private void addCollectionIssue(Order order) {
        // 如果 源单编号 为空，则不生成 收款单据明细
        if (StrKit.isBlank(order.getCode())) {
            return;
        }
        // 如果 源单编号 已生成收款单据明细，则不生成 新的收款单据明细
        if (collectionIssueService.findOldestBySourceCode(order.getCode()) != null) {
            return;
        }

        CollectionIssue collectionIssue = new CollectionIssue();
        collectionIssue.setCollectionId(null);
        collectionIssue.setSourceCode(order.getCode());
        collectionIssue.setType(resolveCollectionIssueType(order.getBusinessType()));
        collectionIssue.setIssueDate(order.getIssueDate());
        collectionIssue.setIssueAmount(order.getTotalAmount());
        collectionIssue.setVerifiedAmount(0);
        collectionIssue.setUnverifiedAmount(order.getTotalAmount());
        collectionIssue.setCurrentVerifiedAmount(0);
        // 新增 收款单据明细 fc_collection_issue
        collectionIssueService.save(collectionIssue);
    }

    private int resolveCollectionIssueType(int orderType) {
        if (Define.ORDER_BUSINESS_TYPE_ORDER == orderType) {
            return Define.COLLECTION_ISSUE_TYPE_ORDER_ORDER;
        }
        return Define.COLLECTION_ISSUE_TYPE_ORDER_RETURN;
    }
}
