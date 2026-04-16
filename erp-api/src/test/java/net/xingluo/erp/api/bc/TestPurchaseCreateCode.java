package net.xingluo.erp.api.bc;

import net.xingluo.erp.api.TestUtil;
import net.kingborn.test.BaseUrl;
import net.kingborn.test.TestCommand;
import org.junit.Test;

/**
 * 购货单获取新编码
 */
public class TestPurchaseCreateCode extends TestCommand {

    @Override
    public void init() throws Exception {
        headerMap = TestUtil.getAuthHeader();
    }

    @Test
    @Override
    public void doTest() {
        System.out.println(formatJson(doPost()));
    }

    @Override
    protected BaseUrl getBaseUrl() {
        return new BaseUrl("localhost", 9090, "/purchase/createCode");
    }
}
