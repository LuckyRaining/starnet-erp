package net.starnet.erp.fc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.fc.model.PaymentIssue;

import java.util.List;

/**
 * 付款单据服务
 */
public interface PaymentIssueService extends IService<PaymentIssue> {
    /**
     * 获取付款单据列表
     *
     * @param paymentId
     * @return
     */
    List<PaymentIssue> findListByPayment(String paymentId);

    /**
     * 根据付款单据删除
     *
     * @param paymentId
     */
    void deleteByPayment(String paymentId);
}
