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
     * 处理 库存
     */
    Stock handleStock(IssueProduct issueProduct, String stockType);

    /**
     * 处理 调拨库存
     *
     * @param transferProduct
     */
    void handTransferStock(TransferProduct transferProduct);
}
