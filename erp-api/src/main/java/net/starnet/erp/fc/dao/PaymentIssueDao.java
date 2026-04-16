package net.starnet.erp.fc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.fc.model.PaymentIssue;
import org.springframework.stereotype.Component;

/**
 * 付款单据Dao
 */
@Component
public interface PaymentIssueDao extends BaseMapper<PaymentIssue> {
}
