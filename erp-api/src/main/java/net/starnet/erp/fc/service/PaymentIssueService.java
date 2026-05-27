package net.starnet.erp.fc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.fc.model.PaymentIssue;

import java.util.List;

/**
 * 付款单据明细 服务
 */
public interface PaymentIssueService extends IService<PaymentIssue> {
    /**
     * 根据 付款单ID 获取 付款单据明细 列表
     *
     * @param paymentId
     * @return
     */
    List<PaymentIssue> findListByPayment(String paymentId);

    /**
     * 根据 付款单ID 删除 付款单据明细
     *
     * @param paymentId
     */
    void deleteByPayment(String paymentId);

    /**
     * 查询 由源单审核生成、尚未关联付款单 的明细
     *
     * @param sourceCode 源单编号
     */
    PaymentIssue findOldestBySourceCode(String sourceCode);

    /**
     * 按源单编号查询最新一条付款单据明细（创建时间降序）
     *
     * @param sourceCode 源单编号
     */
    PaymentIssue findLatestBySourceCode(String sourceCode);
}
