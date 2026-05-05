/** 购物车模块（命名空间 m_cart） */
import CONFIG from '../utils/config';

/**
 * 购物车单行结构（仅此五种字段）：
 * product_id: 商品ID,
 * product_name: 商品名称,
 * product_price: 采购价 or 批发价,
 * product_count: 商品数量,
 * cart_state: 购物车中的本件商品是否被选中,
 */

function rowFromPayload(row) {
	if (!row || typeof row !== 'object') return null;
	const product_id = row.product_id;
	if (product_id == null || product_id === '') return null;
	return {
		product_id,
		product_name: row.product_name != null ? String(row.product_name) : '',
		product_price: Number(row.product_price) || 0,
		product_count: Number(row.product_count) || 1,
		cart_state: row.cart_state !== false
	};
}

function readCartFromStorage() {
	try {
		const raw = uni.getStorageSync(CONFIG.CART_STORAGE_KEY);
		if (raw === '' || raw === undefined || raw === null) return [];
		const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
		if (!Array.isArray(parsed)) return [];
		return parsed.map(rowFromPayload).filter(Boolean);
	} catch (e) {
		return [];
	}
}

const mutations = {
	saveCartToStorage(state) {
		uni.setStorageSync(CONFIG.CART_STORAGE_KEY, JSON.stringify(state.cart || []));
	},

	addToCart(state, product_brief) {
		const line = rowFromPayload(product_brief);
		if (!line) return;

		const found = state.cart.find((item) => item.product_id === line.product_id);
		if (found) {
			found.product_count = (Number(found.product_count) || 0) + line.product_count;
			found.cart_state = line.cart_state;
		} else {
			state.cart.push(line);
		}
		mutations.saveCartToStorage(state);
	},

	updateGoodsCount(state, payload) {
		const product_id = payload && payload.product_id;
		const product_count = payload && payload.product_count;
		
		const target = state.cart.find((item) => item.product_id === product_id);
		if (target) target.product_count = product_count || 1;
		mutations.saveCartToStorage(state);
	},

	clearCart(state) {
		state.cart = [];
		mutations.saveCartToStorage(state);
	},

	setCart(state, list) {
		state.cart = Array.isArray(list) ? list.map(rowFromPayload).filter(Boolean) : [];
		mutations.saveCartToStorage(state);
	},

	/** 更新单行商品的勾选状态 */
	updateGoodsState(state, payload) {
		const product_id = payload && payload.product_id;
		const cart_state = payload && payload.cart_state;
		const target = state.cart.find((item) => item.product_id === product_id);
		if (target) target.cart_state = cart_state !== false;
		mutations.saveCartToStorage(state);
	},

	/** 按商品 Id 从购物车中移除一行 */
	removeGoodsById(state, product_id) {
		if (product_id == null || product_id === '') return;
		state.cart = state.cart.filter((item) => item.product_id !== product_id);
		mutations.saveCartToStorage(state);
	},

	/** 全选 / 反选：将所有行的 cart_state 设为同一值 */
	updateAllProductsState(state, newState) {
		const on = newState !== false;
		state.cart.forEach((item) => {
			item.cart_state = on;
		});
		mutations.saveCartToStorage(state);
	}
};

export default {
	namespaced: true,

	state() {
		return {
			/** 
             * 购物车的数组，用来存储购物车中每个商品的信息对象
             * 每个商品的信息对象，都包含如下 22 个属性：
             * id: ID主键,
             * code: 编号,
             * name: 名称,
             * barcode: 条码,
             * spec: 规格,
             * categoryId: 类别ID,
             * categoryName: 类别,
             * primaryWarehouseId: 首选仓库ID,
             * unitId: 计量单位ID,
             * unitName: 计量单位,
             * retailPrice: 零售价,
             * wholesalePrice: 批发价,
             * vipPrice: VIP价格,
             * discountRate1: 折扣率1,
             * discountRate2: 折扣率2,
             * estimatedPurchasePrice: 预计采购价,
             * remark: 备注,
             * minimumStock: 最低库存,
             * maximumStock: 最高库存,
             * active: 是否启用,
             * createdTime: 创建时间,
             * updatedTime: 更新时间,
             */ 
            /** 
             * product_id: 商品ID,
             * product_name: 商品名称,
             * product_price: 采购价 or 批发价,
             * product_count: 商品数量,
             * cart_state：购物车中的本件商品 是否被选中,
             */
			cart: readCartFromStorage()
		};
	},

	mutations,

	getters: {
		/** 购物车「全部」商品件数（不管是否勾选），TabBar 徽标用这个 */
		total(state) {
			return state.cart.reduce((sum, item) => sum + (Number(item.product_count) || 0), 0);
		},

		/** 仅统计 cart_state !== false 的行的件数，与底部「已选 x 件」一致 */
		checkedCount(state) {
			return state.cart.reduce((sum, item) => {
				if (item.cart_state === false) return sum;
				return sum + (Number(item.product_count) || 0);
			}, 0);
		},

		/** Σ(单价 × 数量)，只含已勾选行 */
		checkedGoodsAmount(state) {
			return state.cart.reduce((sum, item) => {
				if (item.cart_state === false) return sum;
				const price = Number(item.product_price) || 0;
				const count = Number(item.product_count) || 0;
				return sum + price * count;
			}, 0);
		}
	}
};
