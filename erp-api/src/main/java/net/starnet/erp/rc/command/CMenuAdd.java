package net.starnet.erp.rc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.starnet.erp.rc.model.Menu;
import net.starnet.erp.rc.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CMenuAdd extends BaseCommand {

    @Autowired
    private MenuService menuService;

    @Param(required = true)
    private Menu menu;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        menuService.save(menu);

        data.put("menu", menu);
    }
}
