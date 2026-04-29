/**
 * 挂单缓存工具
 */

const HOLD_KEY = "STAR_NET_HOLD_ORDER_LIST";

/**
 * 获取所有挂单列表
 * @returns {Array} 挂单数组，如果没有挂单则返回空数组
 */
export function getHoldOrders() {
    return uni.getStorageSync(HOLD_KEY) || [];
}

/**
 * 保存新的挂单到列表头部
 * @param {Object} order - 需要保存的挂单对象
 * @returns {void}
 */
export function saveHoldOrder(order) {
    const list = getHoldOrders();
    // unshift() 方法将 指定元素 添加到数组 “开头”，并返回 数组的新长度。
    list.unshift({
        ...order,
        id: `${Date.now()}_${Math.random().toString(16).slice(2, 8)}`
    });
    uni.setStorageSync(HOLD_KEY, list);
}

/**
 * 根据ID删除指定的挂单
 * @param {string} id - 需要删除的挂单ID
 * @returns {void}
 */
export function removeHoldOrder(id) {
    // filter() 返回 满足回调函数中 指定条件 的数组元素，即返回 与要删除 id 不匹配的元素 item
    const list = getHoldOrders().filter((item) => item.id !== id);
    uni.setStorageSync(HOLD_KEY, list);
}
