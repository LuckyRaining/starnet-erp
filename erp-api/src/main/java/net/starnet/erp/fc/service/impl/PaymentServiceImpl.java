package net.starnet.erp.fc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.starnet.erp.fc.dao.PaymentDao;
import net.starnet.erp.fc.model.Payment;
import net.starnet.erp.fc.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentDao, Payment> implements PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Override
    public Page<Payment> pageSearch(long current, long size, JSONObject query) {
        return paymentDao.queryPage(new Page<>(current, size), query);
    }

    @Override
    public Payment findByCode(String code) {
        return getOne(new QueryWrapper<Payment>().eq("code", code));
    }
}
