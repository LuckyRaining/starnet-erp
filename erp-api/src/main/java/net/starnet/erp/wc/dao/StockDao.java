package net.starnet.erp.wc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.wc.model.Stock;
import org.springframework.stereotype.Component;

/**
 * 库存Dao
 */
@Component
public interface StockDao extends BaseMapper<Stock> {

}
