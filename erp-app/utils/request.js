/**
 * HTTP请求封装、自动带 Authorization
 */

import CONFIG from "./config";
import userUtils from "./user";
const {
	getToken,
	clearLoginState
} = userUtils;

/**
 * 规范化 URL
 */
function normalizeUrl(url) {
	if (url.startsWith("http")) return url;
	return `${CONFIG.BASE_URL}${url}`;
}

/**
 * 统一 request请求 方法
 */
function request(options = {}) {
	const token = getToken();
	console.log(options.data);

	return new Promise((resolve, reject) => {
		uni.request({

			url: normalizeUrl(options.url),
			method: options.method || "POST",
			data: options.data || {},
			timeout: CONFIG.TIMEOUT,
			header: {
				"Content-Type": "application/json",
				"Authorization": token ? `Bearer ${token}` : "",
				...(options.header || {}),
			},

			success: (res) => {
				// 获取响应数据
				const result = res.data || {};

				if (result.code === 0 || result.code === 200) { // 判断响应状态码, 请求成功

					resolve(result.data || {});

				} else if (result.code === 401) { // 判断响应状态码, 登录已过期, 请重新登录

					// 清除登录状态
					clearLoginState();

					// 显示 登录已过期, 请重新登录 提示
					uni.showToast({
						title: "登录已过期, 请重新登录",
						icon: "none"
					});
					console.log(result.message);

					// 300毫秒后重定向到登录页面
					setTimeout(() => {
						uni.reLaunch({
							url: "/subpackages/auth/login"
						});
					}, 300);

				} else { // 判断响应状态码, 请求失败

					// 显示 请求失败 提示
					uni.showToast({
						title: result.message || "请求失败",
						icon: "none"
					});
					console.log(result.message);

					reject(new Error(result.message || "请求失败")); // 请求失败
				}
			},

			fail: (err) => {

				// 显示 网络异常 提示
				uni.showToast({
					title: err.data || "网络异常",
					icon: "none"
				});
				console.log(err.message);

				reject(new Error(err.data || "网络异常")) // 网络异常
			}

		});
	});
}

export default request;