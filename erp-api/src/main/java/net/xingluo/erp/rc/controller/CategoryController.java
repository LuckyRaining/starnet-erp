package net.xingluo.erp.rc.controller;

import net.kingborn.core.controller.BaseController;
import net.kingborn.core.controller.Controller;
import net.kingborn.core.entry.Result;
import net.xingluo.erp.rc.command.CCategoryDelete;
import net.xingluo.erp.rc.command.CCategoryDetail;
import net.xingluo.erp.rc.command.CCategoryList;
import net.xingluo.erp.rc.command.CCategorySave;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 类别控制器
 */
@Controller("/category")
public class CategoryController extends BaseController {

    /**
     * 列表
     *
     * @return
     */
    @PostMapping("/list")
    public Result list() {
        return doAction(CCategoryList.class);
    }

    /**
     * 新增或修改
     *
     * @return
     */
    @PostMapping("/save")
    public Result save() {
        return doAction(CCategorySave.class);
    }

    /**
     * 删除
     *
     * @return
     */
    @PostMapping("/delete")
    public Result delete() {
        return doAction(CCategoryDelete.class);
    }

    /**
     * 根据Id获取
     */
    @PostMapping("/detail")
    public Result detail() {
        return doAction(CCategoryDetail.class);
    }


}
