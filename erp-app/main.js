// #ifndef VUE3
import Vue from 'vue'
import App from './App'
// 导入 store 的实例对象
import store from './store/store.js'
import api from './utils/api'
import getToken from './utils/user'
import CONFIG from './utils/config'

/**
 * 全局轻提示：对 uni.showToast 做默认配置，减少重复代码。
 * 用法：uni.$showMsg('文字') 或 uni.$showMsg('...', 3000, 'success', true)
 * 需在任意页面使用前先保证 main.js 已执行（应用启动即挂载）。
 */
uni.$showMsg = function (title = '数据加载失败！', duration = CONFIG.DURATION, icon = 'none', mask = false) {
	uni.showToast({
		title: title,
		duration: duration,
		icon: icon,
		mask: mask,
	});
};

Vue.config.productionTip = false
Vue.prototype.$api = api
Vue.prototype.$hasLogin = () => !!getToken()

App.mpType = 'app'

const app = new Vue({
	...App,
	// 将 store 挂载到 Vue 实例上
	store
})
app.$mount()
// #endif



// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
import App from './App.vue'
import store from './store/store.js'
import api from './utils/api'
import getToken from './utils/user'
import CONFIG from './utils/config'

/**
 * 全局轻提示（Vue3 构建）：逻辑与 #ifndef VUE3 分支相同，避免切换编译版本后丢失 uni.$showMsg。
 * 用法：uni.$showMsg('文字') 或 uni.$showMsg('...', 3000, 'success', true)
 */
uni.$showMsg = function (title = '数据加载失败！', duration = CONFIG.DURATION, icon = 'none', mask = false) {
	uni.showToast({
		title: title,
		duration: duration,
		icon: icon,
		mask: mask,
	});
};

export function createApp() {
	const app = createSSRApp(App)
	app.use(store)
	app.config.globalProperties.$api = api
	app.config.globalProperties.$hasLogin = () => !!getToken()
	return {
		app
	}
}
// #endif