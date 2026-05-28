package net.starnet.erp.sc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.kingborn.core.util.StrKit;
import net.starnet.erp.bc.model.Sale;
import net.starnet.erp.bc.service.SaleService;
import net.starnet.erp.constant.Define;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.model.Receivable;
import net.starnet.erp.fc.service.CollectionService;
import net.starnet.erp.fc.service.ReceivableService;
import net.starnet.erp.uc.model.Customer;
import net.starnet.erp.uc.service.CustomerService;
import net.starnet.erp.util.SimpleValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 应收账款明细表
 */
@Command
public class CAnalysisReceivableDetailList extends BaseCommand {

    @Autowired
    private ReceivableService receivableService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private CustomerService customerService;

    /** 开始时间 */
    private @Param String startDate;
    /** 结束时间 */
    private @Param String endDate;
    /** 客户ID列表 */
    private @Param List<String> customerIdList;

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

        List<Receivable> receivableList = receivableService.analysisList(startDate, endDate, customerIdList);

        for (Receivable receivable : receivableList) {
            // 客户信息
            Customer customer = customerService.getById(receivable.getCustomerId());
            Assert.notNull(customer, "ID为【" + receivable.getCustomerId() + "】的客户不存在！");
            receivable.put("customerName", customer.getName());

            if (receivable.getBusinessType().equals(Define.BUSINESS_TYPE_SALE_SELL) || receivable.getBusinessType().equals(Define.BUSINESS_TYPE_SALE_RETURNED)) {
                // 销货/销退订单信息
                Sale sale = saleService.getById(receivable.getBusinessId());
                Assert.notNull(sale, "ID为【" + receivable.getBusinessId() + "】的销货/销退订单不存在！");

                receivable.put("issueCode", sale.getCode());
                receivable.put("businessTypeName", receivable.getBusinessType().equals(Define.BUSINESS_TYPE_SALE_SELL) ?
                        "销货" : "销退");

            } else if (receivable.getBusinessType().equals(Define.BUSINESS_TYPE_COLLECTION)) {
                // 收款单信息
                Collection collection = collectionService.getById(receivable.getBusinessId());
                Assert.notNull(collection, "ID为【" + receivable.getBusinessId() + "】的收款单不存在！");

                receivable.put("issueCode", collection.getCode());
                receivable.put("businessTypeName", "收款");
            }
        }

        data.put("receivableList", receivableList);
    }
}
