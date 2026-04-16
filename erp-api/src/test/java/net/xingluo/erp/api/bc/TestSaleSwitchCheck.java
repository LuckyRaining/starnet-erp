package net.xingluo.erp.api.bc;

import net.xingluo.erp.api.TestUtil;
import net.kingborn.test.BaseUrl;
import net.kingborn.test.TestCommand;
import org.junit.Test;

/**
 * 审查/反审查销货单
 */
public class TestSaleSwitchCheck extends TestCommand {
    @Override
    public void init() throws Exception {
        headerMap = TestUtil.getAuthHeader();

        postParamsMap.put("sale", "1298800372468236288");
    }

    @Test
    @Override
    public void doTest() {
        System.out.println(formatJson(doPost()));
    }

    @Override
    protected BaseUrl getBaseUrl() {
        return new BaseUrl("localhost", 9090, "/sale/switchCheck");
    }
}
