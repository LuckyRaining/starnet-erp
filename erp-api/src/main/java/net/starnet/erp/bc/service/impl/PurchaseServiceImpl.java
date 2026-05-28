package net.starnet.erp.bc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.dao.PurchaseDao;
import net.starnet.erp.bc.model.Purchase;
import net.starnet.erp.bc.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, Purchase> implements PurchaseService {

    @Autowired
    private PurchaseDao purchaseDao;

    @Override
    public Page<Purchase> pageSearch(long current, long size, JSONObject query) {
        return purchaseDao.queryPage(new Page<>(current, size), query);
    }

    @Override
    public Purchase findByCode(String code) {
        return getOne(new QueryWrapper<Purchase>().eq("code", code));
    }

    @Override
    public List<Purchase> findCheckedListBySupplier(String supplierId) {
        QueryWrapper<Purchase> wrapper = new QueryWrapper<Purchase>().eq("checked", true);
        if (StrKit.notBlank(supplierId)) {
            wrapper.eq("supplierId", supplierId);
        }
        return this.list(wrapper.orderByDesc("createdTime"));
    }
}
