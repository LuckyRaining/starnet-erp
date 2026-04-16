package net.xingluo.erp.rc.controller;

import net.kingborn.core.controller.BaseController;
import net.kingborn.core.controller.Controller;
import net.kingborn.core.entry.Result;
import net.xingluo.erp.rc.command.CMenuAdd;
import net.xingluo.erp.rc.command.CMenuList;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单控制器
 */
@Controller("/menu")
public class MenuController extends BaseController {

    @PostMapping("/add")
    public Result add() {
        return doAction(CMenuAdd.class);
    }

    @PostMapping("/list")
    public Result list() {
        return doAction(CMenuList.class);
    }

}
