package net.starnet.erp.fc.command;

import net.kingborn.core.command.BaseCommand;
import net.kingborn.core.command.Command;
import net.kingborn.core.command.Param;
import net.kingborn.core.exception.Assert;
import net.starnet.erp.fc.model.Collection;
import net.starnet.erp.fc.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;

@Command
public class CCollectionSwitchCheck extends BaseCommand {

    @Autowired
    private CollectionService collectionService;

    @Param(required = true)
    private String collectionId;

    @Override
    protected void init() throws Exception {

    }

    @Override
    protected void doCommand() throws Exception {
        Collection collection = collectionService.getById(collectionId);
        Assert.notNull(collection, "ID为【" + collectionId + "】的收款单不存在！");

        collection.setChecked(!collection.isChecked());
        collectionService.saveOrUpdate(collection);

        data.put("collection", collection);
    }
}
