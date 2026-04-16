package net.starnet.erp.sc.command;

import com.alibaba.fastjson.JSONObject;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.wc.service.IssueProductService;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.sc.model.SaleCustomerSummary;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.model.Warehouse;
import net.starnet.erp.uc.service.CustomerService;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.uc.service.WarehouseService;
import net.starnet.erp.util.SimpleValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 销售汇总表（按客户）
 */
@Command
public class CAnalysisSaleCustomerSummaryList extends BaseCommand {

    @Autowired
    private IssueProductService issueProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private DictItemService dictItemService;
    @Autowired
    private CustomerService customerService;

    /** 开始时间 */
    private @Param
    String startDate;
    /** 结束时间 */
    private @Param String endDate;
    /** 客户ID列表 */
    private @Param
    List<String> customerIdList;
    /** 商品ID列表 */
    private @Param List<String> productIdList;
    /** 仓库ID列表 */
    private @Param List<String> warehouseIdList;

    @Override
    protected void init() throws Exception {
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        JSONObject query = new JSONObject();
        query.put("startDate", startDate);
        query.put("endDate", endDate);
        query.put("customerIdList", customerIdList);
        query.put("productIdList", productIdList);
        query.put("warehouseIdList", warehouseIdList);
        List<SaleCustomerSummary> summaryList = issueProductService.analysisSaleListByCustomer(query);

        for (SaleCustomerSummary summary : summaryList) {
            Product product = productService.getById(summary.getProductId());
            Assert.notNull(product, "ID为【" + summary.getProductId() + "】的商品不存在！");

            summary.put("productName", product.getName());
            summary.put("spec", product.getSpec());
            summary.put("productCode", product.getCode());

            Customer customer = customerService.getById(summary.getCustomerId());
            Assert.notNull(customer, "ID为【" + summary.getCustomerId() + "】的客户不存在！");
            summary.put("customerName", customer.getName());

            DictItem unit = dictItemService.getById(product.getUnitId());
            summary.put("unitName", unit.getName());

            Warehouse warehouse = warehouseService.getById(summary.getWarehouseId());
            Assert.notNull(warehouse, "ID为【" + summary.getWarehouseId() + "】的仓库不存在！");
            summary.put("warehouseName", warehouse.getName());
        }

        data.put("summaryList", summaryList);
    }
}
