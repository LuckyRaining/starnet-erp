package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.*;
import net.starnet.erp.fc.service.CollectionIssueService;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.service.CollectionService;
import net.starnet.erp.fc.service.ReceivableService;
import net.starnet.erp.service.SaveAuditService;
import net.starnet.erp.uc.service.SettlementAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 收款单保存
 */
@Command
public class CCollectionSave extends BaseCommand {

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private AccountRecordService accountRecordService;
    @Autowired
    private SettlementAccountService settlementAccountService;
    @Autowired
    private CollectionIssueService collectionIssueService;
    @Autowired
    private ReceivableService receivableService;
    @Autowired
    private SaveAuditService saveAuditService;

    @Param(required = true)
    private Collection collection;
    @Param(defaultValue = "[]")
    private List<CollectionIssue> collectionIssueList;
    @Param(defaultValue = "[]")
    private List<AccountRecord> accountList;

    private Collection persistedCollection;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        // 计算
        if (StrKit.isBlank(collection.getId())) { // collection.id 为空时，即没传，为“新增”的意思
            persistedCollection = new Collection();

            // 校验 单据编号 是否合法，合法才能“新增”，即 新增收款单
            validateCode(collection.getCode());
            persistedCollection.setCode(collection.getCode());

            // 初始化 收款单 的 checked 为 false
            persistedCollection.setChecked(false);

        } else { // collection.id 非空时，即传了，为“更新”的意思
            persistedCollection = collectionService.getById(collection.getId());
            Assert.notNull(persistedCollection, "ID为【" + collection.getId() + "】的入库订单不存在！");

            // 删除 该单原来的账户列表 accountList[] 对应的收支信息
            // 即 回滚 结算账户 uc_settlement_account
            settlementAccountService.rollbackByBusiness(collection.getId());
            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(collection.getId());

            // 删除 该单原来的明细列表 collectionIssueList[]
            // 即 删除 收款单据明细列表 fc_collection_issue
            collectionIssueService.deleteByCollection(collection.getId());

            // 删除 该单原来的 应收账款记录
            // 即 删除 应收账款记录 fc_receivable
            receivableService.deleteByBusiness(collection.getId());
        }

        persistedCollection.setCustomerId(collection.getCustomerId());
        persistedCollection.setIssueDate(collection.getIssueDate());

        persistedCollection.setCollectAmount(collection.getCollectAmount());
        persistedCollection.setIssueAmount(collection.getIssueAmount());
        persistedCollection.setDiscountAmount(collection.getDiscountAmount());
        persistedCollection.setVerifiedAmount(collection.getVerifiedAmount());
        persistedCollection.setUnverifiedAmount(collection.getUnverifiedAmount());
        persistedCollection.setCurrentVerifiedAmount(collection.getCurrentVerifiedAmount());
        persistedCollection.setAdvanceCollectAmount(collection.getAdvanceCollectAmount());

        persistedCollection.setCollectAmount(getCollectAmount());
        persistedCollection.setDiscountAmount(collection.getDiscountAmount());
        persistedCollection.setAdvanceCollectAmount(collection.getAdvanceCollectAmount());
        // 设置 剩余的 金额字段
        setRestOfAmountFields(persistedCollection);

        persistedCollection.setListerId(collection.getListerId());
        persistedCollection.setAuditorId(collection.getAuditorId());

        // 是否需要 审核？
        // 新增保存 收款单时：Save 页已选审核人，但 checked 仍为 false，保存完成后 自动审核
        boolean shouldCheck = StrKit.notNull(collection.getAuditorId()) && !collection.isChecked();

        persistedCollection.setRemark(collection.getRemark());
        // 新增/更新 收款单 fc_collection
        collectionService.saveOrUpdate(persistedCollection);

        // 新增 收款单据的 账户列表 accountList[]
        // 新增 单据账户 fc_account_record，更新 结算账户 uc_settlement_account
        accountRecordService.addRecordList(accountList, Define.ACCOUNT_RECORD_TYPE_IN, persistedCollection.getIssueDate(), Define.BUSINESS_TYPE_COLLECTION, persistedCollection.getId());

        // 新增 收款单据的 明细列表 collectionIssueList[]
        addCollectionIssueList();

        // 处理应收账款
        handleReceivable();

        // 新增保存时：Save 页已选审核人，但 checked 仍为 false，保存完成后自动审核
        // （逻辑与 CCollectionSwitchCheck 一致）
        if (shouldCheck) {
            saveAuditService.checkCollection(persistedCollection, persistedCollection.getAuditorId());
        }

        data.put("collection", persistedCollection);
    }

    /**
     * 校验单据编号是否合法
     *
     * @param code
     */
    private void validateCode(String code) {
        Collection Collection = collectionService.findByCode(code);
        if (Collection != null) {
            throw new BizException("单据编号为【" + code + "】的入库单已经存在！");
        }
    }

    /**
     * 获取 accountList[] 中每个 accountRecord 的 金额字段，并计算出 付款金额 collectAmount
     *
     * @return
     */
    private double getCollectAmount() {
        double collectAmount = 0.0d;
        for (AccountRecord accountRecord : accountList) {
            collectAmount += accountRecord.getAmount();
        }
        return collectAmount;
    }

    /**
     * 获取 collectionIssueList[] 中每个 collectionIssue 的 各种金额字段，并设置到 persistedCollection 中
     *
     * @param persistedCollection
     */
    private void setRestOfAmountFields(Collection persistedCollection) {
        double issueAmount = 0.0d;
        double verifiedAmount = 0.0d;
        double unverifiedAmount = 0.0d;
        double currentVerifiedAmount = 0.0d;

        for (CollectionIssue collectionIssue : collectionIssueList) {
            issueAmount += collectionIssue.getIssueAmount();
            verifiedAmount += collectionIssue.getVerifiedAmount();
            unverifiedAmount += collectionIssue.getUnverifiedAmount();
            currentVerifiedAmount += collectionIssue.getCurrentVerifiedAmount();
        }

        persistedCollection.setIssueAmount(issueAmount);
        persistedCollection.setVerifiedAmount(verifiedAmount);
        persistedCollection.setUnverifiedAmount(unverifiedAmount);
        persistedCollection.setCurrentVerifiedAmount(currentVerifiedAmount);
    }

    /**
     * 新增 付款单据的 明细列表 collectionIssueList[]
     */
    private void addCollectionIssueList() {
        if (collectionIssueList == null || collectionIssueList.size() == 0) return;

        List<CollectionIssue> persistedCollectionIssueList = new ArrayList<>();
        CollectionIssue persistedCollectionIssue;
        for (CollectionIssue collectionIssue : collectionIssueList) {
            persistedCollectionIssue = new CollectionIssue();
            persistedCollectionIssue.setCollectionId(persistedCollection.getId());
            persistedCollectionIssue.setSourceCode(collectionIssue.getSourceCode());
            persistedCollectionIssue.setType(collectionIssue.getType());
            persistedCollectionIssue.setIssueDate(collectionIssue.getIssueDate());
            persistedCollectionIssue.setIssueAmount(collectionIssue.getIssueAmount());
            persistedCollectionIssue.setVerifiedAmount(collectionIssue.getVerifiedAmount());
            persistedCollectionIssue.setUnverifiedAmount(collectionIssue.getUnverifiedAmount());
            persistedCollectionIssue.setCurrentVerifiedAmount(collectionIssue.getCurrentVerifiedAmount());

            persistedCollectionIssueList.add(persistedCollectionIssue);
        }

        // 新增 单据商品 fc_collection_issue
        collectionIssueService.saveBatch(persistedCollectionIssueList);
    }

    /**
     * 新增 应收账款记录
     */
    private void handleReceivable() {
//        if (StrKit.notBlank(collection.getId())) {
//            // 删除 该单原来的 应收账款记录
//            receivableService.deleteByBusiness(collection.getId());
//        }

        // 新增 应收账款记录 fc_receivable
        receivableService.businessAdd(persistedCollection.getCustomerId(), persistedCollection.getIssueDate(),
                Define.BUSINESS_TYPE_COLLECTION, persistedCollection.getId(), 0, persistedCollection.getAdvanceCollectAmount());
    }
}
