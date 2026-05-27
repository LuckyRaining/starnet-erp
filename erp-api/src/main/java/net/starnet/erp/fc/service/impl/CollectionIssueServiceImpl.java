package net.starnet.erp.fc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.fc.dao.CollectionIssueDao;
import net.starnet.erp.fc.model.CollectionIssue;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.CollectionIssueService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionIssueServiceImpl extends ServiceImpl<CollectionIssueDao, CollectionIssue> implements CollectionIssueService {


    @Override
    public List<CollectionIssue> findListByCollection(String collectionId) {
        return this.list(new QueryWrapper<CollectionIssue>().eq("collectionId", collectionId));
    }

    @Override
    public void deleteByCollection(String collectionId) {
        this.remove(new QueryWrapper<CollectionIssue>().eq("collectionId", collectionId));
    }

    @Override
    public CollectionIssue findOldestBySourceCode(String sourceCode) {
        // 如果 源单编号 为空，则不查询
        if (StrKit.isBlank(sourceCode)) {
            return null;
        }

        // 查询 由源单审核生成、尚未关联收款单 的明细
        return this.getOne(new QueryWrapper<CollectionIssue>()
                .isNull("collectionId")
                .eq("sourceCode", sourceCode));
    }

    @Override
    public CollectionIssue findLatestBySourceCode(String sourceCode) {
        // 如果 源单编号 为空，则不查询
        if (StrKit.isBlank(sourceCode)) {
            return null;
        }

        // 按源单编号查询最新一条付款单据明细（创建时间降序）
        return this.getOne(new QueryWrapper<CollectionIssue>()
                .eq("sourceCode", sourceCode)
                .orderByDesc("createdTime")
                .last("LIMIT 1"));
    }

}
