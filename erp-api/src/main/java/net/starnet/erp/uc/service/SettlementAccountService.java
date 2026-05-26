package net.starnet.erp.uc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.uc.model.SettlementAccount;

/**
 * 结算账户服务
 */
public interface SettlementAccountService extends IService<SettlementAccount> {

    /**
     * 回滚业务单据 对应的结算账户余额变动
     *
     * @param businessId
     */
    void rollbackByBusiness(String businessId);

}
