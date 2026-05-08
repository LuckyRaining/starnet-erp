package net.starnet.erp.uc.command;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.rc.model.Category;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.CategoryService;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.uc.model.Product;
import net.starnet.erp.uc.service.ProductService;
import net.starnet.erp.wc.model.Stock;
import net.starnet.erp.wc.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 商品分页
 */
@Command
public class CProductPage extends BaseCommand {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DictItemService itemService;
    @Autowired
    private StockService stockService;

    @Param(defaultValue = "{}")
    private JSONObject query; // 查询对象
    @Param(defaultValue = Define.CURRENT)
    private long current; // 页码
    @Param(defaultValue = Define.SIZE)
    private long size; // 每页数量

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Page<Product> productPage = productService.pageSearch(current, size, query);

        for (Product product : productPage.getRecords()) {
            if (StrKit.notBlank(product.getCategoryId())) {
                Category category = categoryService.getById(product.getCategoryId());
                product.put("categoryName", category.getName());
            }

            if (StrKit.notBlank(product.getUnitId())) {
                DictItem unit = itemService.getById(product.getUnitId());
                product.put("unitName", unit.getName());
            }

            // 计算当前库存:查询该商品在所有仓库的库存总量
            List<Stock> stockList = stockService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Stock>()
                    .eq("productId", product.getId()));
            double totalStock = stockList.stream()
                    .mapToDouble(Stock::getQuantity)
                    .sum();
            product.put("stock", totalStock);
        }

        data.put("productPage", productPage);
    }
}
