package net.xingluo.erp.fc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.xingluo.erp.fc.dao.CollectionDao;
import net.xingluo.erp.fc.model.Collection;
import net.xingluo.erp.fc.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionDao, Collection> implements CollectionService {

    @Autowired
    private CollectionDao collectionDao;

    @Override
    public Page<Collection> pageSearch(long current, long size, JSONObject query) {
        return collectionDao.queryPage(new Page<>(current, size), query);
    }

    @Override
    public Collection findByCode(String code) {
        return getOne(new QueryWrapper<Collection>().eq("code", code));
    }
}
