/**
 * HTTP请求封装
 */
import { CONFIG } from './config.js'

/**
 * 封装请求方法
 */
function request(options) {
	return new Promise((resolve, reject) => {
		// 获取token
		const token = uni.getStorageSync(CONFIG.TOKEN_KEY)
		
		uni.request({
			url: CONFIG.BASE_URL + options.url,
			method: options.method || 'POST',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				'Authorization': token ? `Bearer ${token}` : ''
			},
			timeout: CONFIG.TIMEOUT,
			success: (res) => {
				if (res.statusCode === 200) {
					// 后端成功码为0,不是200
					if (res.data.code === 0 || res.data.code === 200) {
						resolve(res.data)
					} else if (res.data.code === 401) {
						// Token失效,跳转登录
						uni.removeStorageSync(CONFIG.TOKEN_KEY)
						uni.removeStorageSync(CONFIG.USER_INFO_KEY)
						uni.showToast({
							title: '登录已过期,请重新登录',
							icon: 'none'
						})
						setTimeout(() => {
							uni.reLaunch({
								url: '/subpackages/auth/login'
							})
						}, 1500)
						reject(res.data)
					} else {
						uni.showToast({
							title: res.data.message || '请求失败',
							icon: 'none'
						})
						reject(res.data)
					}
				} else {
					uni.showToast({
						title: '网络请求失败',
						icon: 'none'
					})
					reject(res)
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络连接失败',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}

export {
	request
}
