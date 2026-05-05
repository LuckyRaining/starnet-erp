import { createStore } from 'vuex';
import cart from './cart.js';
import user from './user.js';

/**
 * Vuex 4：使用 createStore；Vue2 分支在 main.js 里传入 `new Vue({ store })`，
 * Vue3 分支使用 `app.use(store)`，无需再 Vue.use(Vuex)。
 *
 * m_cart：购物车数组与勾选、数量、持久化；
 * m_user：收货仓库、token、用户信息、结算回跳 redirectInfo 与持久化。
 */
export default createStore({
	modules: {
		m_cart: cart,
		m_user: user
	}
});
