package net.starnet.erp.sc.command;

import com.alibaba.fastjson.JSONObject;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.service.PurchaseService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.util.SimpleValidator;
import net.starnet.erp.wc.model.IssueProduct;
import net.starnet.erp.wc.service.IssueProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 采购汇总表（按商品）
 * <p>
 * 按单据日期、供应商、商品、仓库等条件，查询购货单/购退单（{@code wc_issue_product}）
 * 中的商品明细行，并为前端报表补充名称类展示字段（商品、供应商、仓库、单位等）。
 */
@Command
public class CAnalysisPurchaseProductSummaryList extends BaseCommand {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;

    /** 查询起始 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param
    String startDate;
    /** 查询结束 单据日期（含），格式 yyyy-MM-dd，可选 */
    private @Param String endDate;
    /** 供应商 ID 列表，可选；传入时只查这些供应商的购货/购退单 */
    private @Param List<String> supplierIdList;
    /** 商品 ID 列表，可选；传入时只查这些商品明细行 */
    private @Param List<String> productIdList;
    /** 仓库 ID 列表，可选；传入时只查这些仓库的明细行 */
    private @Param List<String> warehouseIdList;

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
    }

    /**
     * 查询 采购汇总表（按商品） 并 组装报表 展示数据。
     * <p>
     * 流程：
     * <ol>
     *     <li>组装 查询条件，调用 {@link IssueProductService#analysisPurchaseListByProduct} 查明细行；</li>
     *     <li>逐行关联 购货单、商品、单位、仓库，写入 临时展示字段；</li>
     *     <li>将结果放入 {@code data.summaryList} 返回前端。</li>
     * </ol>
     */
    @Override
    protected void doCommand() throws Exception {
        JSONObject query = new JSONObject();
        query.put("startDate", startDate);
        query.put("endDate", endDate);
        query.put("supplierIdList", supplierIdList);
        query.put("productIdList", productIdList);
        query.put("warehouseIdList", warehouseIdList);

        // 查询 wc_issue_product 中 businessType 为 buy/refund 的明细行（已关联 bc_purchase 过滤）
        List<IssueProduct> productList = issueProductService.analysisPurchaseListByProduct(query);

        for (IssueProduct issueProduct : productList) {
            // 商品信息
            Product product = productService.getById(issueProduct.getProductId());
            Assert.notNull(product, "ID为【" + issueProduct.getProductId() + "】的商品不存在！");
            issueProduct.put("productName", product.getName());
            issueProduct.put("spec", product.getSpec());
            issueProduct.put("productCode", product.getCode());

            // 计量单位名称（来自字典项）
            DictItem unit = dictItemService.getById(product.getUnitId());
            issueProduct.put("unitName", unit.getName());

            // 仓库信息
            Warehouse warehouse = warehouseService.getById(issueProduct.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + issueProduct.getWarehouseId() + "】的仓库不存在！");
            issueProduct.put("warehouseName", warehouse.getName());
        }

        // 将结果放入 {@code data.summaryList} 返回前端
        data.put("summaryList", productList);
    }
}
