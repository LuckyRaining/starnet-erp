/**
 * 格式化工具
 */

/**
 * 格式化 金额 为 保留两位小数的字符串
 * @param {number|string} value - 需要格式化的金额值，如果为空则默认为 0
 * @returns {string} 格式化后的金额字符串，固定保留两位小数
 */
export function formatMoney(value) {
    const num = Number(value || 0);
    return num.toFixed(2);
}

/**
 * 格式化 日期 为 YYYY-MM-DD 格式的字符串
 * @param {Date} [date=new Date()] - 需要格式化的日期对象，默认为当前日期
 * @returns {string} 格式化后的日期字符串，格式为 YYYY-MM-DD
 */
export function formatDate(date = new Date()) {
    const year = date.getFullYear(); // YYYY
    const month = `${date.getMonth() + 1}`.padStart(2, "0"); // MM
    const day = `${date.getDate()}`.padStart(2, "0"); // DD
    return `${year}-${month}-${day}`;
}
