package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.exception.BizException;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.model.AccountRecord;
import net.starnet.erp.fc.service.CollectionIssueService;
import net.starnet.erp.fc.service.AccountRecordService;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.service.CollectionService;
import net.starnet.erp.fc.service.ReceivableService;
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
    private CollectionIssueService collectionIssueService;
    @Autowired
    private ReceivableService receivableService;

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

            // 删除 该单原来的账户列表 accountList[]
            // 即 删除 单据账户 fc_account_record
            accountRecordService.deleteByBusiness(collection.getId());

            // 删除 该单原来的明细列表 collectionIssueList[]
            // 即 删除 付款单据明细列表 fc_collection_issue
            collectionIssueService.deleteByCollection(collection.getId());
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
        persistedCollection.setListerId(collection.getListerId());
        persistedCollection.setAuditorId(collection.getAuditorId());
        persistedCollection.setRemark(collection.getRemark());
        // 新增/更新 付款单 fc_collection
        collectionService.saveOrUpdate(persistedCollection);

        // 新增 付款单据的 账户列表 accountList[]
        // 新增 单据账户 fc_account_record，更新 结算账户 uc_settlement_account
        accountRecordService.addRecordList(accountList, Define.ACCOUNT_RECORD_TYPE_IN, persistedCollection.getIssueDate(), Define.BUSINESS_TYPE_COLLECTION, persistedCollection.getId());

        // 新增 付款单据的 明细列表 collectionIssueList[]
        addCollectionIssueList();

        // 处理应收账款
        handleReceivable();

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
