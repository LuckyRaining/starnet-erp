package net.starnet.erp.fc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.fc.model.AccountRecord;
import org.springframework.stereotype.Component;

/**
 * 账单Dao
 */
@Component
public interface AccountRecordDao extends BaseMapper<AccountRecord> {
}
