package net.starnet.erp.uc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.uc.model.Employee;
import org.springframework.stereotype.Component;

/**
 * 职员Dao
 */
@Component
public interface EmployeeDao extends BaseMapper<Employee> {
}
