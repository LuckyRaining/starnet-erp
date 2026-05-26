package net.starnet.erp.wc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.model.Stock;
import net.starnet.erp.wc.model.TransferProduct;

/**
 * 库存服务
 */
public interface StockService extends IService<Stock> {

    /**
     * 根据 “商品” 和 “仓库” 获取 “库存”
     *
     * @param productId
     * @param warehouseId
     * @return
     */
    Stock findByProductAndWarehouse(String productId, String warehouseId);

    /**
     * 处理 库存：购货为入库，购退为出库
     */
    Stock handleStock(IssueProduct issueProduct, String stockType);

    /**
     * 回滚 库存 变动（不写入 出入库记录）
     */
    void rollbackStock(String businessId);

    /**
     * 处理 调拨库存
     *
     * @param transferProduct
     */
    void handleTransferStock(TransferProduct transferProduct);

    /**
     * 回滚 调拨库存 变动（不写入 出入库记录）
     */
    void rollbackTransferStock(String businessId);
}
