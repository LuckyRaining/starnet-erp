package net.starnet.erp.uc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.uc.dao.SupplierContactDao;
import net.starnet.erp.uc.model.SupplierContact;
import net.starnet.erp.uc.service.SupplierContactService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 供应商联系人服务
 */
@Service
public class SupplierContactServiceImp extends ServiceImpl<SupplierContactDao, SupplierContact> implements SupplierContactService {
    @Override
    public void deleteBySupplier(String supplierId) {
        this.remove(new QueryWrapper<SupplierContact>().eq("supplierId", supplierId));
    }

    @Override
    public List<SupplierContact> findListBySupplier(String supplierId) {
        return this.list(new QueryWrapper<SupplierContact>().eq("supplierId", supplierId));
    }
}
