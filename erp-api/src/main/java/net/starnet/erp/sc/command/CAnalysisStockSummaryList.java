package net.starnet.erp.sc.command;

import com.alibaba.fastjson.JSONObject;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.util.SimpleValidator;
import net.starnet.erp.wc.model.StockRecord;
import net.starnet.erp.wc.service.StockRecordService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 收发汇总表
 * <p>
 * 按单据日期、商品、仓库等条件，查询库存收发汇总（{@code wc_stock_record}），
 * 并为前端报表 补充名称类展示字段（商品、仓库、单位等）。
 */
@Command
public class CAnalysisStockSummaryList extends BaseCommand {

    @Autowired
    private StockRecordService recordService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    /** 查询起始 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param String startDate;
    /** 查询结束 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param String endDate;
    /** 商品 ID 列表，可选；传入时只查这些 商品明细行 */
    private @Param List<String> productIdList;
    /** 仓库 ID 列表，可选；传入时只查这些 仓库的明细行 */
    private @Param List<String> warehouseIdList;

    private JSONObject query;
    private JSONObject stock;
    private List<JSONObject> stockList = new ArrayList<>();

    /**
     * 命令执行前参数校验。
     * 仅校验已传入的日期字符串格式是否合法，不强制要求必须传日期。
     */
    @Override
    protected void init() throws Exception {
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }

        query = new JSONObject();
        query.put("startDate", startDate);
        query.put("endDate", endDate);
        query.put("productIdList", productIdList);
        query.put("warehouseIdList", warehouseIdList);
    }

    /**
     * 查询 库存收发汇总 并 组装报表 展示数据。
     * <p>
     * 流程：
     * <ol>
     *     <li>组装 查询条件，调用 {@link StockRecordService#distinctProductWarehouseIdList} 查明细行；</li>
     *     <li>逐行关联 商品、单位、仓库，写入 临时展示字段；</li>
     *     <li>将结果放入 {@code data.stockList} 返回前端。</li>
     * </ol>
     */
    @Override
    protected void doCommand() throws Exception {

        // 获取 不同商品ID 对应的 不同仓库ID列表
        List<StockRecord> recordList = recordService.distinctProductWarehouseIdList(query);

        for (StockRecord record : recordList) {
            stock = new JSONObject();
            // 两句废话！！其实每个 record 里面都有 productId 和 warehouseId，这里重复赋值
            stock.put("productId", record.getProductId());
            stock.put("warehouseId", record.getWarehouseId());

            // 商品信息
            Product product = productService.getById(record.getProductId());
            Assert.notNull(product, "ID为【" + record.getProductId() + "】的商品不存在！");
            stock.put("productName", product.getName());
            stock.put("spec", product.getSpec());
            stock.put("productCode", product.getCode());

            // 计量单位名称（来自字典项）
            DictItem unit = dictItemService.getById(product.getUnitId());
            stock.put("unitName", unit.getName());

            // 仓库信息
            Warehouse warehouse = warehouseService.getById(record.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + record.getWarehouseId() + "】的仓库不存在！");
            stock.put("warehouseName", warehouse.getName());

            // 组装库存信息
            composeStockInfo(record);

            stockList.add(stock);
        }

        // 将结果放入 {@code data.stockList} 返回前端
        data.put("stockList", stockList);
    }

    /**
     * 组装库存信息
     * <p>
     * 流程：
     * <ol>
     *     <li>根据 productId 和 warehouseId 获取最近的出入仓记录；</li>    
     *     <li>计算期初金额、期初数量、入库数量、出库数量、期末金额、期末数量；</li>
     *     <li>将结果放入 {@code stock} 返回前端。</li>
     * </ol>
     *
     * @param record
     */
    private void composeStockInfo(StockRecord record) {
        List<String> queryProductIdList = new ArrayList<>();
        queryProductIdList.add(record.getProductId());
        List<String> queryWarehouseIdList = new ArrayList<>();
        queryWarehouseIdList.add(record.getWarehouseId());
        List<StockRecord> recordList = recordService.analysisList(startDate, endDate, queryProductIdList, queryWarehouseIdList);

        // 即：商品所在仓库 最旧的 出入记录
        // 即：期初库存信息（初始时 该库存中 该商品的 总成本、总数量）
        StockRecord startRecord = recordList.get(0);
        stock.put("startAmount", startRecord.getCurrentQuantity() * startRecord.getPrice());
        stock.put("startQuantity", startRecord.getCurrentQuantity());

        for (StockRecord stockRecord : recordList) {
            if (Define.BUSINESS_TYPE_TRANSFER_IN.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "transferInQuantity");
            } else if (Define.BUSINESS_TYPE_TRANSFER_OUT.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "transferOutQuantity");
            } else if (Define.BUSINESS_TYPE_STORE_PROFIT.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "storeProfitQuantity");
            } else if (Define.BUSINESS_TYPE_STORE_OTHER.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "storeOtherQuantity");
            } else if (Define.BUSINESS_TYPE_CHECKOUT_LOSS.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "checkoutLossQuantity");
            } else if (Define.BUSINESS_TYPE_CHECKOUT_OTHER.equals(stockRecord.getBusinessType())) {
                putSumInfo(stockRecord, "checkoutOtherQuantity");
            } else {
                putSumInfo(stockRecord, stockRecord.getBusinessType() + "Quantity");
            }
        }

        // TODO 成本调整

        // 入库合计
        double storeTotalQuantity = stock.getDoubleValue("transferInQuantity") + stock.getDoubleValue("buyQuantity")
                + stock.getDoubleValue("returnedQuantity") + stock.getDoubleValue("storeProfitQuantity") + stock.getDoubleValue("storeOtherQuantity");
        stock.put("storeTotalQuantity", storeTotalQuantity);

        // 出库合计
        double checkoutTotalQuantity = stock.getDoubleValue("transferOutQuantity") + stock.getDoubleValue("refundQuantity")
                + stock.getDoubleValue("sellQuantity") + stock.getDoubleValue("checkoutLossQuantity") + stock.getDoubleValue("checkoutOtherQuantity");
        stock.put("checkoutTotalQuantity", checkoutTotalQuantity);

        // 即：商品所在仓库 最新的 出入记录
        // 即：期末库存信息（最新统计时 该库存中 该商品的 总成本、总数量）
        StockRecord endRecord = recordList.get(recordList.size() - 1);
        stock.put("endAmount", endRecord.getCurrentQuantity() * endRecord.getPrice());
        stock.put("endQuantity", endRecord.getCurrentQuantity());
    }

    private void putSumInfo(StockRecord stockRecord, String quantityKey) {
        double currentQuantity = stock.getDouble(quantityKey) == null ? 0.0 : stock.getDoubleValue(quantityKey);

        // stock.put(quantityKey, currentQuantity + stockRecord.getCurrentQuantity());
        stock.put(quantityKey, currentQuantity + stockRecord.getQuantity());
    }
}
