package net.starnet.erp.wc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.wc.dao.TransferProductDao;
import net.starnet.erp.wc.model.TransferProduct;
import net.starnet.erp.wc.service.TransferProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferProductServiceImpl extends ServiceImpl<TransferProductDao, TransferProduct> implements TransferProductService {

    @Override
    public List<TransferProduct> findListByTransfer(String transferId) {
        return this.list(new QueryWrapper<TransferProduct>().eq("transferId", transferId));
    }

    @Override
    public void deleteByTransfer(String transferId) {
        this.remove(new QueryWrapper<TransferProduct>().eq("transferId", transferId));
    }
}
