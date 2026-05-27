package net.starnet.erp.uc.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.uc.model.Product;

import java.util.Map;

/**
 * 商品服务
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询
     *
     * @param current
     * @param size
     * @param query
     * @return
     */
    Page<Product> pageSearch(long current, long size, JSONObject query);

    /**
     * 根据编码获取对象
     *
     * @param code
     * @return
     */
    Product findByCode(String code);

    /**
     * 按商品分类统计商品数量（仅统计直接归属该分类的商品）
     */
    Map<String, Long> countGroupByCategoryId();

    /**
     * 商品总数
     */
    long countAll();
}
