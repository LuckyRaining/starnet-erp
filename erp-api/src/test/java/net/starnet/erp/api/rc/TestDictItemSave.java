package net.starnet.erp.api.rc;

import net.starnet.erp.api.TestUtil;
import net.starnet.erp.constant.Define;
import net.starnet.erp.rc.model.DictItem;
import net.kingborn.test.BaseUrl;
import net.kingborn.test.TestCommand;
import org.junit.Test;

/**
 * 保存字典项
 */
public class TestDictItemSave extends TestCommand {
    @Override
    public void init() throws Exception {
        headerMap = TestUtil.getAuthHeader();

        DictItem item = new DictItem();
        // item.setId("1294753269708566530");
        item.setDictCode(Define.DICT_CODE_ACCOUNT_TYPE);
        item.setName("银行存款");
        postParamsMap.put("item", item);
    }

    @Test
    @Override
    public void doTest() {
        System.out.println(formatJson(doPost()));
    }

    @Override
    protected BaseUrl getBaseUrl() {
        return new BaseUrl("localhost", 9090, "/dict/itemSave");
    }
}
