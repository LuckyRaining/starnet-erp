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

	HISTORY_STORAGE_KEY : 'STAR_NET_SEARCH_HISTORY', // 搜索历史
};

export default CONFIG;