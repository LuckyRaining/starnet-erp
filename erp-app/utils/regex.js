/**
 * 正则工具
 */

export const REGEX = {
    // 手机号(mobile phone)中国(宽松), 只要是13,14,15,16,17,18,19开头即可
    mobile: /^(?:(?:\+|00)86)?1[3-9]\d{9}$/,
    // 数字
    number: /^\d+(\.\d+)?$/,
    // 金额
    price: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
    // ID
    idLike: /^[A-Za-z0-9_-]{4,64}$/,
};

export function isValidMobile(value) {
    // RegExp 的 test() 方法会使用此 正则表达式 进行搜索，以查找 正则表达式 与 指定字符串String 之间的匹配情况。
    // 如果 存在匹配，则返回 true; 否则返回 false。

    // String 的 trim() 方法会从 该字符串的两端 移除 空白字符，并返回一个 新的字符串，而不会修改 原始字符串。
    // 如果 字符串的开头或结尾 都没有空格，则仍会返回 一个新的字符串(本质上是 str 的副本)。
    return REGEX.mobile.test(String(value || "").trim());
}

export function isValidPrice(value) {
    return REGEX.price.test(String(value || "").trim());
}
