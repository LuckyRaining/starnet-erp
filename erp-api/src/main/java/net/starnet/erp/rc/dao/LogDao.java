package net.starnet.erp.rc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.rc.model.Log;
import org.springframework.stereotype.Component;

/**
 * 日志Dao
 */
@Component
public interface LogDao extends BaseMapper<Log> {
}
