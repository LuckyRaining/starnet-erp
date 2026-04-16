/**
 * 用户状态管理
 */
import { CONFIG } from './config.js'

/**
 * 获取当前用户信息
 */
function getUserInfo() {
	return uni.getStorageSync(CONFIG.USER_INFO_KEY) || null
}

/**
 * 设置用户信息
 */
function setUserInfo(userInfo) {
	uni.setStorageSync(CONFIG.USER_INFO_KEY, userInfo)
}

/**
 * 获取Token
 */
function getToken() {
	return uni.getStorageSync(CONFIG.TOKEN_KEY) || ''
}

/**
 * 设置Token
 */
function setToken(token) {
	uni.setStorageSync(CONFIG.TOKEN_KEY, token)
}

/**
 * 清除用户信息(登出)
 */
function clearUserInfo() {
	uni.removeStorageSync(CONFIG.TOKEN_KEY)
	uni.removeStorageSync(CONFIG.USER_INFO_KEY)
}

/**
 * 检查是否已登录
 */
function isLogin() {
	return !!getToken()
}

export {
	getUserInfo,
	setUserInfo,
	getToken,
	setToken,
	clearUserInfo,
	isLogin
}
