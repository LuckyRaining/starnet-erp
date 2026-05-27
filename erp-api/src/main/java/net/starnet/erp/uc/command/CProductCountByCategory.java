package net.starnet.erp.uc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.starnet.erp.uc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 按商品分类统计商品数量
 */
@Command
public class CProductCountByCategory extends BaseCommand {

    @Autowired
    private ProductService productService;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Map<String, Long> countMap = productService.countGroupByCategoryId();
        data.put("countMap", countMap);
        data.put("totalCount", productService.countAll());
    }
}
