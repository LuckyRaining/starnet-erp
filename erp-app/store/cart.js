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

    // 模块的 mutations 方法
	mutations,

    // 模块的 mutations 方法
	getters: {
		total(state) {
			// reduce(function, 0) === 从 0 开始加的 forEach() 函数
			return state.cart.reduce((sum, item) => sum + (Number(item.product_count) || 0), 0);
		}
	}
};
