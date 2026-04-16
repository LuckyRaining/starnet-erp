package net.xingluo.erp.uc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.xingluo.erp.uc.model.Employee;
import org.springframework.stereotype.Component;

/**
 * 职员Dao
 */
@Component
public interface EmployeeDao extends BaseMapper<Employee> {
}
