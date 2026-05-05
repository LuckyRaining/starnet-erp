/**
 * 用户相关 Vuex（命名空间 m_user）
 * - 收货仓库、登录 token、微信用户信息、结算后回跳的 redirectInfo；
 * - updateWarehouse / updateToken / updateUserinfo 会同步本地缓存，供请求头与下次启动读取。
 */
import CONFIG from '../utils/config';
import userUtils from '../utils/user';

/** 把接口/缓存里的对象整理成固定字段 { id, code, name } */
function normalizeWarehouse(raw) {
	if (!raw || typeof raw !== 'object') return null;
	const id = raw.id != null ? String(raw.id) : '';
	if (!id) return null;
	return {
		id,
		code: raw.code != null ? String(raw.code) : '',
		name: raw.name != null ? String(raw.name) : ''
	};
}

function readWarehouseFromStorage() {
	try {
		const raw = uni.getStorageSync(CONFIG.WAREHOUSE_STORAGE_KEY);
		if (raw === '' || raw === undefined || raw === null) return null;
		const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
		return normalizeWarehouse(parsed);
	} catch (e) {
		return null;
	}
}

function readTokenFromStorage() {
	try {
		return userUtils.getToken() || '';
	} catch (e) {
		return '';
	}
}

function readUserinfoFromStorage() {
	try {
		const u = userUtils.getUserInfo();
		if (u && typeof u === 'object') return { ...u };
		return {};
	} catch (e) {
		return {};
	}
}

const mutations = {
	/** 与本地 STAR_NET_TOKEN 同步，供 request 携带 Authorization */
	updateToken(state, token) {
		const t = token != null ? String(token) : '';
		state.token = t;
		if (t) {
			userUtils.setToken(t);
		} else {
			userUtils.clearToken();
		}
	},

	/** payload 为 null 时清空；否则与已有 userinfo 合并，并写入 STAR_NET_USER_INFO */
	updateUserinfo(state, payload) {
		if (payload == null) {
			state.userinfo = {};
			userUtils.clearUserInfo();
			return;
		}
		state.userinfo = { ...state.userinfo, ...payload };
		userUtils.setUserInfo(state.userinfo);
	},

	/** 结算未登录时写入回跳信息；payload 为 null 则清空 */
	updateRedirectInfo(state, payload) {
		if (payload == null || typeof payload !== 'object') {
			state.redirectInfo = null;
			return;
		}
		state.redirectInfo = {
			openType: payload.openType != null ? String(payload.openType) : 'switchTab',
			from: payload.from != null ? String(payload.from) : ''
		};
	},

	/** 从本地缓存恢复 token、userinfo（例如账号密码登录页只写了 storage） */
	hydrateFromStorage(state) {
		state.token = readTokenFromStorage();
		state.userinfo = readUserinfoFromStorage();
	},

	/** payload 传 null/无效对象 可视为清空仓库（同时删本地键） */
	updateWarehouse(state, payload) {
		const w = normalizeWarehouse(payload);
		state.warehouse = w;
		if (w) {
			uni.setStorageSync(CONFIG.WAREHOUSE_STORAGE_KEY, JSON.stringify(w));
		} else {
			try {
				uni.removeStorageSync(CONFIG.WAREHOUSE_STORAGE_KEY);
			} catch (e) {
				/* ignore */
			}
		}
	}
};

export default {
	namespaced: true,

	state() {
		return {
			warehouse: readWarehouseFromStorage(),
			token: readTokenFromStorage(),
			userinfo: readUserinfoFromStorage(),
			redirectInfo: null
		};
	},

	mutations
};
