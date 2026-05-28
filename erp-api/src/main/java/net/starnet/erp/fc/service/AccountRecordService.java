package net.starnet.erp.fc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.fc.model.AccountRecord;

import java.util.List;

/**
 * 账单服务
 */
public interface AccountRecordService extends IService<AccountRecord> {

    /**
     * 获取 账单 列表
     *
     * @param businessId
     * @return
     */
    List<AccountRecord> findListByBusiness(String businessId);

    /**
     * 根据 业务ID 删除
     *
     * @param businessId
     */
    void deleteByBusiness(String businessId);

    /**
     * 新增 记录 列表
     *
     * @param recordList
     */
    void addRecordList(List<AccountRecord> recordList, String type, String issueDate, String businessType, String businessId);

    /**
     * 获取 现金/银行 报表
     *
     * @param startDate
     * @param endDate
     * @param accountIdList
     * @return
     */
    List<AccountRecord> analysisList(String startDate, String endDate, List<String> accountIdList);
}
