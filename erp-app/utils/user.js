/**
 * 用户缓存
 */

import CONFIG from "./config";

/**
 * 获取Token
 */
function getToken() {
	return uni.getStorageSync(CONFIG.TOKEN_KEY) || "";
}

/**
 * 设置Token
 */
function setToken(token) {
	uni.setStorageSync(CONFIG.TOKEN_KEY, token || "");
}

/**
 * 清除Token
 */
function clearToken() {
	uni.removeStorageSync(CONFIG.TOKEN_KEY);
}

/**
 * 获取当前用户信息
 */
function getUserInfo() {
	return uni.getStorageSync(CONFIG.USER_INFO_KEY) || null;
}

/**
 * 设置用户信息
 */
function setUserInfo(user) {
	uni.setStorageSync(CONFIG.USER_INFO_KEY, user || null);
}

/**
 * 清除用户信息
 */
function clearUserInfo() {
	uni.removeStorageSync(CONFIG.USER_INFO_KEY);
}

/**
 * 清除登录信息(登出)
 */
function clearLoginState() {
	clearToken();
	clearUserInfo();
}

/**
 * 检查是否已登录
 */
function isLogin() {
	return !!getToken();
}

export default {
	getToken,
	setToken,
	clearToken,

	getUserInfo,
	setUserInfo,
	clearUserInfo,

	clearLoginState,
	isLogin,
}