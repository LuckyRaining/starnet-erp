package net.xingluo.erp.fc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xingluo.erp.fc.model.PaymentIssue;
import org.springframework.stereotype.Component;

/**
 * 付款单据Dao
 */
@Component
public interface PaymentIssueDao extends BaseMapper<PaymentIssue> {
}
