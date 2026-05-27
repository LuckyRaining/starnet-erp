package net.starnet.erp.fc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.fc.model.CollectionIssue;

import java.util.List;

/**
 * 收款单据服务
 */
public interface CollectionIssueService extends IService<CollectionIssue> {
    /**
     * 获取收款单据列表
     *
     * @param collectionId
     * @return
     */
    List<CollectionIssue> findListByCollection(String collectionId);

    /**
     * 根据收款单据删除
     *
     * @param collectionId
     */
    void deleteByCollection(String collectionId);

    /**
     * 查询 由源单审核生成、尚未关联收款单 的明细
     *
     * @param sourceCode 源单编号
     */
    CollectionIssue findOldestBySourceCode(String sourceCode);

    /**
     * 按源单编号查询最新一条收款单据明细（创建时间降序）
     *
     * @param sourceCode 源单编号
     */
    CollectionIssue findLatestBySourceCode(String sourceCode);
}
