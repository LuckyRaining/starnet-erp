package net.xingluo.erp.uc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.xingluo.erp.uc.model.Supplier;
import net.xingluo.erp.uc.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 供应商启停
 */
@Command
public class CSupplierSwitchActive extends BaseCommand {

    @Autowired
    private SupplierService supplierService;

    @Param(required = true)
    private String supplierId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Supplier supplier = supplierService.getById(supplierId);
        Assert.notNull(supplier, "ID为【" + supplierId + "】的供应商不存在！");

        supplier.setActive(!supplier.isActive());
        supplierService.saveOrUpdate(supplier);

        data.put("supplier", supplier);
    }
}
