/**
 * 本地「待付款」采购草稿：购物车结算时写入，支付完成或用户取消时移除。
 * 非服务端单据，仅用于小程序内从「我的 — 待付款」回到采购待支付页。
 */
import CONFIG from './config';

function readList() {
	try {
		const raw = uni.getStorageSync(CONFIG.PENDING_PAY_PURCHASES_KEY);
		return Array.isArray(raw) ? raw : [];
	} catch (e) {
		return [];
	}
}

function writeList(list) {
	uni.setStorageSync(CONFIG.PENDING_PAY_PURCHASES_KEY, list);
}

/**
 * @param {{ lines: Array, checkedCount?: number, checkedGoodsAmount?: number }} payload
 * @returns {string} pendingId
 */
export function addPendingPurchaseDraft(payload) {
	const lines = payload && Array.isArray(payload.lines) ? payload.lines : [];
	const id = `pp_${Date.now()}_${Math.random().toString(36).slice(2, 10)}`;
	const entry = {
		id,
		bizType: 'purchase',
		status: 'pending_pay',
		createdAt: Date.now(),
		lines,
		checkedCount: payload.checkedCount,
		checkedGoodsAmount: payload.checkedGoodsAmount
	};
	const list = readList();
	list.unshift(entry);
	writeList(list);
	return id;
}

export function getPendingPurchaseDraft(id) {
	if (id == null || id === '') return null;
	return readList().find((x) => x.id === id) || null;
}

export function removePendingPurchaseDraft(id) {
	if (id == null || id === '') return;
	writeList(readList().filter((x) => x.id !== id));
}

export function listPendingPayPurchases() {
	return readList().filter((x) => x.bizType === 'purchase' && x.status === 'pending_pay');
}
