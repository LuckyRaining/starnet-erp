package net.starnet.erp.uc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.uc.dao.ProductDao;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务实现
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements ProductService {
    @Override
    public Page<Product> pageSearch(long current, long size, JSONObject query) {
        QueryWrapper<Product> wrapper = new QueryWrapper<>();

        // 通过 keyword 搜索
        String keyword = query.getString("keyword");
        if (StrKit.notBlank(keyword)) {
            wrapper.and(w -> w.like("id", keyword) // 商品ID
                    .or() // 或
                    .like("code", keyword) // 商品编号
                    .or() // 或
                    .like("name", keyword) // 商品名称
                    .or() // 或
                    .like("barcode", keyword) // 条码
                    .or() // 或
                    .like("categoryId", keyword) // 分类ID
                    .or() // 或
                    .like("unitId", keyword) // 单位ID
                    .or() // 或
                    .like("spec", keyword)); // 规格型号
        }

        // 通过 categoryId 搜索
        String categoryId = query.getString("categoryId");
        if (StrKit.notBlank(categoryId)) {
            wrapper.eq("categoryId", categoryId);
        }

        // // 通过 unitId 搜索
        // String unitId = query.getString("unitId");
        // if (StrKit.notBlank(unitId)) {
        //     wrapper.eq("unitId", unitId);
        // }

        wrapper.orderByDesc("createdTime");

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    public Product findByCode(String code) {
        return this.getOne(new QueryWrapper<Product>().eq("code", code));
    }

    @Override
    public Map<String, Long> countGroupByCategoryId() {
        /*
          SELECT categoryId, COUNT(1) AS productCount FROM uc_product WHERE categoryId IS NOT NULL AND categoryId != '' GROUP BY categoryId;

          countList =  [{<productCount, ?>, <categoryId, ?>},
                        {<productCount, ?>, <categoryId, ?>},
                        {<productCount, ?>, <categoryId, ?>},
                        {<productCount, ?>, <categoryId, ?>}]
          countList =  {<categoryId, ?>, <categoryId, ?>, <categoryId, ?>, <categoryId, ?>}
         */
        // 统计每个分类的商品数量
        List<Map<String, Object>> countList = this.listMaps(new QueryWrapper<Product>()
                .select("categoryId", "COUNT(1) AS productCount") // 统计每个分类的商品数量
                .isNotNull("categoryId") // 非空
                .ne("categoryId", "") // 不为空
                .groupBy("categoryId")); // 按分类分组统计

        // 将统计结果转换为 Map<String, Long>
        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : countList) {
            Object categoryId = item.get("categoryId");
            Object productCount = item.get("productCount");
            if (categoryId == null || productCount == null) {
                continue;
            }

            countMap.put(String.valueOf(categoryId), Long.parseLong(String.valueOf(productCount)));
        }

        return countMap;
    }

    @Override
    public long countAll() {
        return this.count();
    }
}
