// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import api from './utils/api'
import getToken from './utils/user'

Vue.config.productionTip = false
Vue.prototype.$api = api
Vue.prototype.$hasLogin = () => !!getToken()

App.mpType = 'app'

const app = new Vue({
	...App
})
app.$mount()
// #endif

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
import App from './App.vue'
import api from './utils/api'
import getToken from './utils/user'

export function createApp() {
	const app = createSSRApp(App)
	app.config.globalProperties.$api = api
	app.config.globalProperties.$hasLogin = () => !!getToken()
	return {
		app
	}
}
// #endif