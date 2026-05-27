package net.starnet.erp.bc.controller;

import net.kingborn.core.controller.BaseController;
import net.kingborn.core.controller.Controller;
import net.kingborn.core.entry.Result;
import net.starnet.erp.bc.command.*;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 购货单控制器
 */
@Controller("/purchase")
public class PurchaseController extends BaseController {

    /**
     * 分页
     */
    @PostMapping("/page")
    public Result page() {
        return doAction(CPurchasePage.class);
    }

    /**
     * 购货单详情
     */
    @PostMapping("/detail")
    public Result detail() {
        return doAction(CPurchaseDetail.class);
    }

    /**
     * 创建一个新的单据编号
     */
    @PostMapping("/createCode")
    public Result createCode() {
        return doAction(CPurchaseCreateCode.class);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public Result save() {
        return doAction(CPurchaseSave.class);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public Result delete() {
        return doAction(CPurchaseDelete.class);
    }

    /**
     * 购货单 切换审核
     */
    @PostMapping("/switchCheck")
    public Result switchCheck() {
        return doAction(CPurchaseSwitchCheck.class);
    }

    /**
     * 获取 用户已审核的 购货单
     */
    @PostMapping("/findCheckedListBySupplier")
    public Result findCheckedListBySupplier() {
        return doAction(CPurchaseFindCheckedListBySupplier.class);
    }

}
