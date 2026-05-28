package net.starnet.erp.wc.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.wc.model.StockRecord;

import java.util.List;

/**
 * 出入库服务
 */
public interface StockRecordService extends IService<StockRecord> {

    /**
     * 收发明细表
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param productIdList 商品ID列表
     * @param warehouseIdList 仓库ID列表
     * @return
     */
    List<StockRecord> analysisList(String startDate,
                                   String endDate,
                                   List<String> productIdList,
                                   List<String> warehouseIdList);

    /**
     * 获取 不同的 商品ID列表
     *
     * @param query
     * @return
     */
    List<String> distinctProductIdList(JSONObject query);

    /**
     * 获取 某一商品的 出入库仓列表
     *
     * @param query
     * @param productId
     * @return
     */
    List<String> distinctWarehouseIdListByProduct(JSONObject query, String productId);

    /**
     * 获取 最近的 出入库记录
     *
     * @param query
     * @param productId
     * @param warehouseId
     * @return
     */
    StockRecord findLatestRecord(JSONObject query, String productId, String warehouseId);

    /**
     * 获取 商品仓库ID列表
     *
     * @param query
     * @return
     */
    List<StockRecord> distinctProductWarehouseIdList(JSONObject query);

    /**
     * 获取 出入库记录 列表
     *
     * @param businessId
     * @return
     */
    List<StockRecord> findListByBusiness(String businessId);

    /**
     * 根据 业务ID 删除 出入库记录
     *
     * @param businessId
     */
    void deleteByBusiness(String businessId);
}
