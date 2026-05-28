package net.starnet.erp.sc.command;

import com.alibaba.fastjson.JSONObject;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品库存余额表
 * <p>
 * 按单据日期、商品、仓库等条件，查询库存余额（{@code wc_stock_record}），
 * 并为前端报表 补充名称类展示字段（商品、仓库、单位等）。
 */
@Command
public class CAnalysisStockAmountList extends BaseCommand {

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
    private JSONObject warehouseProduct;
    private List<JSONObject> warehouseProductList = new ArrayList<>();

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
     * 查询 商品库存余额 并 组装报表 展示数据。
     * <p>
     * 流程：
     * <ol>
     *     <li>组装 查询条件，调用 {@link StockRecordService#distinctProductIdList} 查明细行；</li>
     *     <li>逐行关联 商品、单位、仓库，写入 临时展示字段；</li>
     *     <li>将结果放入 {@code data.productList} 和 {@code data.warehouseList} 返回前端。</li>
     * </ol>
     */
    @Override
    protected void doCommand() throws Exception {

        // 获取 不同商品ID列表
        List<String> productIdList = recordService.distinctProductIdList(query);

        for (String productId : productIdList) {
            warehouseProduct = new JSONObject();

            // 商品信息
            Product product = productService.getById(productId);
            Assert.notNull(product, "ID为【" + productId + "】的商品不存在！");
            warehouseProduct.put("productName", product.getName());
            warehouseProduct.put("spec", product.getSpec());
            warehouseProduct.put("productCode", product.getCode());

            // 计量单位名称（来自字典项）
            DictItem unit = dictItemService.getById(product.getUnitId());
            warehouseProduct.put("unitName", unit.getName());

            // 组装各个仓库的库存信息
            // 即：warehouseProduct.put("warehouseAmountMapping", warehouseAmountMapping);
            composeWarehouseStockInfo(productId);

            warehouseProductList.add(warehouseProduct);
        }

        // 查询 仓库列表
        List<Warehouse> warehouseList = warehouseService.findByIdList(warehouseIdList);

        // 将结果放入 {@code data.productList} 返回前端
        data.put("productList", warehouseProductList);
        // 将结果放入 {@code data.warehouseList} 返回前端
        data.put("warehouseList", warehouseList);
    }

    /**
     * 组装各个仓库的库存信息
     * <p>
     * 流程：
     * <ol>
     *     <li>根据 productId 获取最近的各仓库出入仓记录；</li>
     *     <li>逐行关联 仓库，写入 临时展示字段；</li>
     *     <li>将结果放入 {@code warehouseProduct.warehouseAmountMapping} 返回前端。</li>
     * </ol>
     *
     * @param productId
     */
    private void composeWarehouseStockInfo(String productId) {
        // 获取 最近的（最新的） 各仓库出入仓记录

        // 获取 不同商品ID 对应的 不同仓库ID列表
        List<String> warehouseIdList = recordService.distinctWarehouseIdListByProduct(query, productId);

        Map<String, JSONObject> warehouseAmountMapping = new HashMap<>();
        JSONObject warehouseAmount;
        double totalAmount = 0.0d;
        double totalQuantity = 0.0d;
        for (String warehouseId : warehouseIdList) {
            // warehouseAmount = {<"warehouseId", warehouseId>, <"warehouseName", warehouseName>, <"currentQuantity", currentQuantity>, <"currentAmount", currentAmount>}
            warehouseAmount = new JSONObject();

            StockRecord record = recordService.findLatestRecord(query, productId, warehouseId);
            warehouseAmount.put("warehouseId", warehouseId);
            warehouseAmount.put("warehouseName", warehouseService.getNameById(warehouseId));
            warehouseAmount.put("currentQuantity", record.getCurrentQuantity());

            // warehouseAmount.put("currentAmount", record.getCurrentQuantity() * record.getPrice());
            warehouseAmount.put("currentAmount", record.getAmount());

            // warehouseAmountMapping = {<warehouseId, warehouseAmount>}
            warehouseAmountMapping.put(warehouseId, warehouseAmount);

            totalAmount += warehouseAmount.getDoubleValue("currentAmount");
            totalQuantity += warehouseAmount.getDoubleValue("currentQuantity");
        }
        // 所有仓库总量
        // totalWarehouseAmount = {<"totalAmount", totalAmount>, <"totalQuantity", totalQuantity>}
        JSONObject totalWarehouseAmount = new JSONObject();
        totalWarehouseAmount.put("totalAmount", totalAmount);
        totalWarehouseAmount.put("totalQuantity", totalQuantity);

        // warehouseAmountMapping = {<warehouseId, warehouseAmount>, <total, totalWarehouseAmount>}
        warehouseAmountMapping.put("total", totalWarehouseAmount);

        warehouseProduct.put("warehouseAmountMapping", warehouseAmountMapping);
    }
}
