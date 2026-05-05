import { createStore } from 'vuex';
import cart from './cart.js';

/**
 * Vuex 4：使用 createStore；Vue2 分支在 main.js 里传入 `new Vue({ store })`，
 * Vue3 分支使用 `app.use(store)`，无需再 Vue.use(Vuex)。
 */
export default createStore({
	modules: {
		m_cart: cart
	}
});
