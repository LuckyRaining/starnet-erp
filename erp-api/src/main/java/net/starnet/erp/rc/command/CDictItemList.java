package net.starnet.erp.rc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.rc.model.Dict;
import net.starnet.erp.rc.model.DictItem;
import net.starnet.erp.rc.service.DictItemService;
import net.starnet.erp.rc.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 字典项列表
 */
@Command
public class CDictItemList extends BaseCommand {

    @Autowired
    private DictService dictService;
    @Autowired
    private DictItemService itemService;

    @Param(required = true)
    private String dictCode;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Dict dict = dictService.findByCode(dictCode);
        Assert.notNull(dict, "编码为【" + dictCode + "】的字典不存在！");

        List<DictItem> itemList = itemService.findListByCode(dictCode);
        data.put("itemList", itemList);
    }
}
