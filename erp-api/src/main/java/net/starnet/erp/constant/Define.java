package net.starnet.erp.constant;

/**
 * 常量
 */
public class Define {

    // ---------------------------------------- pageSearch 中的 queryPage 部分参数
    public static final String CURRENT = "1";
    public static final String SIZE = "15";


    // ---------------------------------------- 系统设置的键
    public static final String SYSTEM_CONFIGURATION_KEY = "system_configuration";


    // ---------------------------------------- 日志类型
    public static final int LOG_TYPE_LOGIN = 10000; // 登录
    /**
     * 用户相关
     */
    public static final int LOG_TYPE_USER_ADD = 11010; // 新增用户
    public static final int LOG_TYPE_USER_ACTIVATE = 11020; // 启用
    public static final int LOG_TYPE_USER_DEACTIVATE = 11030; // 停用
    public static final int LOG_TYPE_USER_RESET_PASSWORD = 11040; // 修改密码


    // ---------------------------------------- 类别类型
    public static final int CATEGORY_TYPE_CUSTOMER = 10; // 客户
    public static final int CATEGORY_TYPE_SUPPLIER = 20; // 供应商
    public static final int CATEGORY_TYPE_PRODUCT = 30; // 商品
    public static final int CATEGORY_TYPE_EXPENSE = 40; // 支出
    public static final int CATEGORY_TYPE_INCOME = 50; // 收入

    /**
     * 校验类别
     */
    public static boolean validateCategoryType(int type) {
        return CATEGORY_TYPE_CUSTOMER == type || CATEGORY_TYPE_SUPPLIER == type
                || CATEGORY_TYPE_PRODUCT == type || CATEGORY_TYPE_EXPENSE == type
                || CATEGORY_TYPE_INCOME == type;
    }


    // ---------------------------------------- 字典编码
    public static final String DICT_CODE_UNIT = "unit"; // 计量单位
    public static final String DICT_CODE_SETTLEMENT = "settlement"; // 计算方式
    public static final String DICT_CODE_CUSTOMER_LEVEL = "customer_level"; // 客户等级
    public static final String DICT_CODE_ACCOUNT_TYPE = "account_type"; // 账户类别


    // ---------------------------------------- 业务分类
    public static final String BUSINESS_CATEGORY_PURCHASE = "purchase"; // 采购
    public static final String BUSINESS_CATEGORY_SALE = "sale"; // 销售


    // ---------------------------------------- 业务类型
    public static final String BUSINESS_TYPE_PURCHASE_BUY = "buy"; // 购货
    public static final String BUSINESS_TYPE_PURCHASE_REFUND = "refund"; // 购退
    public static final String BUSINESS_TYPE_SALE_ORDER = "order"; // 客户订单
    public static final String BUSINESS_TYPE_SALE_SELL = "sell"; // 销货
    public static final String BUSINESS_TYPE_SALE_RETURNED = "returned"; // 销退
    public static final String BUSINESS_TYPE_TRANSFER = "transfer"; // 调拨
    public static final String BUSINESS_TYPE_TRANSFER_IN = "transfer_in"; // 调拨入库
    public static final String BUSINESS_TYPE_TRANSFER_OUT = "transfer_out"; // 调拨出库
    public static final String BUSINESS_TYPE_STORE = "store"; // 入库
    public static final String BUSINESS_TYPE_STORE_PROFIT = "store_profit"; // 入库盘盈
    public static final String BUSINESS_TYPE_STORE_OTHER = "store_other"; // 入库其他
    public static final String BUSINESS_TYPE_CHECKOUT = "checkout"; // 出库
    public static final String BUSINESS_TYPE_CHECKOUT_LOSS = "checkout_loss"; // 出库盘亏
    public static final String BUSINESS_TYPE_CHECKOUT_OTHER = "checkout_other"; // 出库其他
    public static final String BUSINESS_TYPE_COLLECTION = "collection"; // 收款
    public static final String BUSINESS_TYPE_PAYMENT = "payment"; // 付款
    public static final String BUSINESS_TYPE_INCOME = "income"; // 收入
    public static final String BUSINESS_TYPE_EXPENSE = "expense"; // 支出

    /**
     * 校验 销售类型
     **/
    public static boolean validateSaleType(String type) {
        return BUSINESS_TYPE_SALE_SELL.equals(type) || BUSINESS_TYPE_SALE_RETURNED.equals(type);
    }

    /**
     * 校验 采购类型
     **/
    public static boolean validatePurchaseType(String type) {
        return BUSINESS_TYPE_PURCHASE_BUY.equals(type) || BUSINESS_TYPE_PURCHASE_REFUND.equals(type);
    }


    // ---------------------------------------- 客户订单 类型
    public static final int ORDER_BUSINESS_TYPE_ORDER = 10; // 订货
    public static final int ORDER_BUSINESS_TYPE_RETURN = 20; // 退货

    /**
     * 校验 客户订单类型
     **/
    public static boolean validateOrderBusinessType(int type) {
        return ORDER_BUSINESS_TYPE_ORDER == type || ORDER_BUSINESS_TYPE_RETURN == type;
    }


    // ---------------------------------------- 购货单 付款状态
    public static final int PURCHASE_STATUS_UNPAID = 10; // 未付/退款
    public static final int PURCHASE_STATUS_PARTIAL = 20; // 部分付/退款
    public static final int PURCHASE_STATUS_PAID = 30; // 已付/退款


    // ---------------------------------------- 销售单 收款状态
    public static final int SALE_STATUS_UNPAID = 10; // 未收/退款
    public static final int SALE_STATUS_PARTIAL = 20; // 部分收/退款
    public static final int SALE_STATUS_PAID = 30; // 全部收/退款


    // ---------------------------------------- 付款单据明细 类别
    public static final int PAYMENT_ISSUE_TYPE_PURCHASE_BUY = 10; // 购货
    public static final int PAYMENT_ISSUE_TYPE_PURCHASE_REFUND = 20; // 购货退货


    // ---------------------------------------- 收款单据明细 类别
    public static final int COLLECTION_ISSUE_TYPE_SALE_SELL = 10; // 销货
    public static final int COLLECTION_ISSUE_TYPE_SALE_RETURNED = 20; // 销货退货
    public static final int COLLECTION_ISSUE_TYPE_ORDER_ORDER = 30; // 订货
    public static final int COLLECTION_ISSUE_TYPE_ORDER_RETURN = 40; // 退货


    // ---------------------------------------- 账户记录 类型
    public static final String ACCOUNT_RECORD_TYPE_IN = "in"; // 收入
    public static final String ACCOUNT_RECORD_TYPE_OUT = "out"; // 支出


    // ---------------------------------------- 出入库 类型
    public static final String STOCK_TYPE_IN = "in"; // 入库
    public static final String STOCK_TYPE_OUT = "out"; // 出库


    // ---------------------------------------- 其他入库 类型
    public static final int STORE_TYPE_PROFIT = 10; // 盘盈入库
    public static final int STORE_TYPE_OTHER = 20; // 其他入库

    /**
     * 校验 其他入库类型
     */
    public static boolean validateStoreType(int type) {
        return STORE_TYPE_PROFIT == type || STORE_TYPE_OTHER == type;
    }


    // ---------------------------------------- 其他出库 类型
    public static final int CHECKOUT_TYPE_LOSS = 10; // 盘亏出库
    public static final int CHECKOUT_TYPE_OTHER = 20; // 其他出库

    /**
     * 校验 其他出库类型
     */
    public static boolean validateCheckoutType(int type) {
        return CHECKOUT_TYPE_LOSS == type || CHECKOUT_TYPE_OTHER == type;
    }


}
