package net.starnet.erp.fc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.fc.dao.PaymentIssueDao;
import net.starnet.erp.fc.model.PaymentIssue;
import net.starnet.erp.fc.service.PaymentIssueService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentIssueServiceImpl extends ServiceImpl<PaymentIssueDao, PaymentIssue> implements PaymentIssueService {


    @Override
    public List<PaymentIssue> findListByPayment(String paymentId) {
        return this.list(new QueryWrapper<PaymentIssue>().eq("paymentId", paymentId));
    }

    @Override
    public void deleteByPayment(String paymentId) {
        this.remove(new QueryWrapper<PaymentIssue>().eq("paymentId", paymentId));
    }

    @Override
    public PaymentIssue findOldestBySourceCode(String sourceCode) {
        // 如果 源单编号 为空，则不查询
        if (StrKit.isBlank(sourceCode)) {
            return null;
        }

        // 查询 由源单审核生成、尚未关联付款单 的明细
        return this.getOne(new QueryWrapper<PaymentIssue>()
                .isNull("paymentId")
                .eq("sourceCode", sourceCode));
    }

    @Override
    public PaymentIssue findLatestBySourceCode(String sourceCode) {
        // 如果 源单编号 为空，则不查询
        if (StrKit.isBlank(sourceCode)) {
            return null;
        }

        // 按源单编号查询最新一条付款单据明细（创建时间降序）
        return this.getOne(new QueryWrapper<PaymentIssue>()
                .eq("sourceCode", sourceCode)
                .orderByDesc("createdTime")
                .last("LIMIT 1"));
    }

}
