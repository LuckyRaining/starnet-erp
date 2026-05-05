/**
 * 配置管理
 */

const ENV_DEVELOPMENT = "http://127.0.0.1:9090"; // 开发环境
const ENV_PRODUCTION = "http://127.0.0.1:9090"; // 生产环境

const CONFIG = {
	ENV_DEVELOPMENT: ENV_DEVELOPMENT, // 开发环境
	ENV_PRODUCTION: ENV_PRODUCTION, // 生产环境
	BASE_URL: process.env.NODE_ENV === "development" ? ENV_DEVELOPMENT : ENV_PRODUCTION, // 根 URL

	TIMEOUT: 15000, // request 超时时间
	DURATION: 2000, // showToast 持续展示时间

	TOKEN_KEY: "STAR_NET_TOKEN", // 令牌
	USER_INFO_KEY: "STAR_NET_USER_INFO", // 用户信息

	HISTORY_STORAGE_KEY: 'STAR_NET_SEARCH_HISTORY', // 搜索历史
	CART_STORAGE_KEY: 'STAR_NET_CART', // 购物车
	
	TABBAR_INDEX_HOME: 0, // home 页面的 tabbar_index
	TABBAR_INDEX_CATEGORY: 1, // category 页面的 tabbar_index
	TABBAR_INDEX_CART: 2, // cart 页面的 tabbar_index
	TABBAR_INDEX_STATISTIC: 3, // statistic 页面的 tabbar_index
	TABBAR_INDEX_MINE: 4, // mine 页面的 tabbar_index

	CURRENT: 1, // 当前页码
	SIZE: 15, // 每页记录数
	
    // ----- ----- ----- ----- ----- -----类别类型
    CATEGORY_TYPE_CUSTOMER : 10, // 客户
    CATEGORY_TYPE_SUPPLIER : 20, // 供应商
    CATEGORY_TYPE_PRODUCT : 30, // 商品
    CATEGORY_TYPE_EXPENSE : 40, // 支出
    CATEGORY_TYPE_INCOME : 50, // 收入
};

export default CONFIG;