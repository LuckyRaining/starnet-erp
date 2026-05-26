package net.starnet.erp.fc.command;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.constant.Define;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.model.User;
import net.starnet.erp.uc.service.CustomerService;
import net.starnet.erp.uc.service.UserService;
import net.starnet.erp.util.SimpleValidator;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 收款单分页列表
 */
@Command
public class CCollectionPage extends BaseCommand {

    @Autowired
    private CollectionService collectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;

    @Param(defaultValue = "{}")
    private JSONObject query; // 查询对象
    @Param(defaultValue = Define.CURRENT)
    private long current;
    @Param(defaultValue = Define.SIZE)
    private long size;

    @Override
    protected void init() throws Exception {
        String startDate = query.getString("startDate");
        if (StrKit.notBlank(startDate)) {
            Assert.notFalse(SimpleValidator.validateDate(startDate), "起始时间不正确！");
        }

        String endDate = query.getString("endDate");
        if (StrKit.notBlank(endDate)) {
            Assert.notFalse(SimpleValidator.validateDate(endDate), "结束时间不正确！");
        }
    }

    @Override
    protected void doCommand() throws Exception {
        Page<Collection> collectionPage = collectionService.pageSearch(current, size, query);

        Customer customer;
        User lister;
        User auditor;
        for (Collection collection : collectionPage.getRecords()) {
            customer = customerService.getById(collection.getCustomerId());
            collection.put("customerName", customer.getName());

            if (StrKit.notBlank(collection.getListerId())) {
                lister = userService.getById(collection.getListerId());
                collection.put("listerName", lister.getName());
            }

            if (StrKit.notBlank(collection.getAuditorId())) {
                auditor = userService.getById(collection.getAuditorId());
                collection.put("auditorName", auditor.getName());
            }
        }

        data.put("collectionPage", collectionPage);
    }
}
