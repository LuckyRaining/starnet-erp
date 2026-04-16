package net.starnet.erp.uc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.starnet.erp.uc.model.Product;
import org.springframework.stereotype.Component;

/**
 * 商品Dao
 */
@Component
public interface ProductDao extends BaseMapper<Product> {
}
