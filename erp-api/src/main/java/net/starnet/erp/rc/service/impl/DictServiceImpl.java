package net.starnet.erp.rc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.rc.dao.DictDao;
import net.starnet.erp.rc.model.Dict;
import net.starnet.erp.rc.service.DictService;
import org.springframework.stereotype.Service;

/**
 * 字段服务实现
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictDao, Dict> implements DictService {
    @Override
    public Dict findByCode(String code) {
        return getOne(new QueryWrapper<Dict>().eq("code", code));
    }
}
