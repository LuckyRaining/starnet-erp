package net.starnet.erp.fc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.fc.model.FlowRecord;
import org.springframework.stereotype.Component;

/**
 * 收支Dao
 */
@Component
public interface FlowRecordDao extends BaseMapper<FlowRecord> {
}
