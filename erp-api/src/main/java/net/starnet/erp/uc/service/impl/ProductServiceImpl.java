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
            wrapper.and(w -> w.like("id", keyword)
                    .or()
                    .like("code", keyword)
                    .or()
                    .like("name", keyword)
                    .or()
                    .like("barcode", keyword)
                    .or()
                    .like("categoryId", keyword)
                    .or()
                    .like("unitId", keyword));
        }

//        // 通过 categoryId 搜索
//        String categoryId = query.getString("categoryId");
//        if (StrKit.notBlank(categoryId)) {
//            wrapper.eq("categoryId", categoryId);
//        }
//
//        // 通过 unitId 搜索
//        String unitId = query.getString("unitId");
//        if (StrKit.notBlank(unitId)) {
//            wrapper.eq("unitId", unitId);
//        }

        wrapper.orderByDesc("createdTime");

        return this.page(new Page<>(current, size), wrapper);
    }

    @Override
    public Product findByCode(String code) {
        return this.getOne(new QueryWrapper<Product>().eq("code", code));
    }
}
