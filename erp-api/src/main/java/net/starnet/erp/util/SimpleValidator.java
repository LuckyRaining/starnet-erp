package net.starnet.erp.util;

import net.kingborn.core.util.StrKit;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;


/**
 * 一个简单的验证类
 *
 * @author Hao Liu
 *
 */
public class SimpleValidator {

	/// 邮箱
	/// - 用户名部分：由字母、数字、下划线、连字符或点组成。
	/// - @ 符号。
	/// - 域名部分：支持标准的域名格式（如 example.com）或 IP 地址格式（如 [192.168.1.1]）。
	/// - 后缀：2 到 4 个字母（如 .com, .cn）或 1 到 3 位数字。
	private static final Pattern PATTERN_EMAIL = Pattern
			.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	/// 昵称
	/// - 允许中文汉字、英文字母（大小写）、数字、下划线 \_ 和小数点 .。长度至少为 1 位。
	private static final Pattern PATTERN_NICKNAME = Pattern.compile("^[\\x{4e00}-\\x{9fa5}a-zA-z0-9_.]+$");
    /// 用户名
    /// - 允许中文汉字、英文字母（大小写）、数字和下划线 _。与昵称相比，不允许使用小数点 .。
    private static final Pattern PATTERN_USER_NAME = Pattern.compile("^[\\x{4e00}-\\x{9fa5}a-zA-z0-9_]+$");
    /// 密码
    /// - 必须是 6 到 20 位的非空白字符（\S 表示任何非空格、制表符等的字符）。
	private static final Pattern PATTERN_PASSWORD = Pattern.compile("^[\\S]{6,20}$");
    /// 真实姓名
    /// - 仅允许中文汉字，长度限制在 2 到 5 个字之间。
    private static final Pattern PATTERN_TRUENAME = Pattern.compile("^[\\x{4e00}-\\x{9fa5}]{2,5}$");
    /// 身份证号
    /// - 允前 17 位为数字，最后一位可以是数字或字母 x/X（用于校验码）。这是标准的 18 位身份证格式。
	private static final Pattern PATTERN_ID_CARD = Pattern.compile("^\\d{17}[0-9xX]$");
    /// 银行卡号
    /// - 必须是 16 位或 19 位的纯数字。
    private static final Pattern PATTERN_BANK_CARD_ID = Pattern.compile("^(\\d{16}|\\d{19})$");
    /// 手机号
    /// - 以数字 1 开头，后面紧跟 10 位数字，总共 11 位。
	private static final Pattern PATTERN_MOBILE = Pattern.compile("^1\\d{10}$");
    /// 固定电话
    /// - 可选的区号：3 位或 4 位数字加连字符 -。
    /// - 电话号码主体：7 位或 8 位数字。
    /// - 例如：010-12345678 或 12345678。
    private static final Pattern PATTERN_PHONE = Pattern.compile("^(\\d{4}-|\\d{3}-)?(\\d{8}|\\d{7})$");
    /// 日期
    /// - 格式为 YYYY-MM-DD 或 YY-MM-DD。
    /// - 月份：01-12。
    /// - 日期：01-31（此正则未严格区分大小月或闰年，仅做范围检查）。
	private static final Pattern PATTERN_DATE = Pattern
			.compile("^(\\d{4}|\\d{2})-((0([1-9]))|(1[0-2]))-((0[1-9])|([12]([0-9]))|(3[0|1]))$");
    /// QQ 号
    /// - 以非零数字开头，后面至少跟 4 位数字，总长度至少 5 位。
    private static final Pattern PATTERN_QQ = Pattern.compile("^[1-9]\\d{4,}$");
    /// 整数
    /// - 可选的正负号 +/-，后面跟 1 到 9 位数字。
	private static final Pattern PATTERN_INTEGER = Pattern.compile("^[+-]?\\d{1,9}$");
    /// 金额
    /// - 整数部分：可以是 0 或以非零数字开头的多位数。
    /// - 小数部分：可选，如果有则必须是小数点后跟 1 到 2 位数字。
    /// - 支持正负号。
	private static final Pattern PATTERN_MONEY = Pattern
			.compile("^(([+-]?[1-9]{1}\\d*)|([+-]?[0]{1}))(\\.(\\d){1,2})?$");
    /// 日期时间
    /// - 这是一个非常严格的日期时间正则，格式为 YYYY-MM-DD。
    /// - 它考虑了不同月份的天数（如 30 天或 31 天）。
    /// - 它甚至包含了闰年 2 月 29 日的校验逻辑。
    /// - 年份不能为 0000。
    private static final Pattern PATTERN_DATE_TIME = Pattern
			.compile("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$");
    /// 时间
    /// - 24 小时制，格式为 HH:mm。
    /// - 小时：00-23。
    /// - 分钟：00-59。
    private static final Pattern PATTERN_TIME = Pattern
			.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");
    /// 网址
    /// - 协议：必须以 http:// 或 https:// 开头。
    /// - 可选的用户认证信息（如 user:pass@）。
    /// - 域名或 IP。
    /// - 可选的端口号。
    /// - 可选的路径及查询参数。
    private static final Pattern PATTERN_SITE = Pattern
			.compile("^(http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?$");
    /// OSS 视频 URL
    /// - 固定路径前缀：data/static/video/。
    /// - 文件名：由字母、数字、下划线、连字符或点组成。
    /// - 后缀：必须是 .mp4。
    private static final Pattern PATTERN_OSS_VIDEO_URL = Pattern.compile("^data\\/static\\/video\\/([a-zA-Z0-9_\\-\\.]+)(\\.mp4)$");

	/**
	 * 校验邮箱
	 *
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email) {
		return PATTERN_EMAIL.matcher(email).matches();
	}

	/**
	 * 校验用户名
	 *
	 * @param userName
	 * @return
	 */
	public static boolean validateUserName(String userName) {
		if (StrKit.isBlank(userName)) {
			return false;
		}

		if (validateInteger(userName)) {
			return false;
		}

		int length = userName.length();
		return length > 4 && length < 20 && PATTERN_USER_NAME.matcher(userName).matches();
	}

	/**
	 * 校验昵称
	 *
	 * @param nickname
	 * @return
	 */
	public static boolean validateNickname(String nickname) {
		if (StrKit.isBlank(nickname)) {
			return false;
		}

		try {
			// TODO 一个汉字会被认为长度为3
			int length = nickname.getBytes("utf-8").length;
			return length > 3 && length < 20 && PATTERN_NICKNAME.matcher(nickname).matches();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 校验密码
	 *
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		return PATTERN_PASSWORD.matcher(password).matches();
	}

	/**
	 * 校验真实姓名
	 *
	 * @param truename
	 * @return
	 */
	public static boolean validateTruename(String truename) {
		return PATTERN_TRUENAME.matcher(truename).matches();
	}

	/**
	 * 校验身份证
	 *
	 * @param idCard
	 * @return
	 */
	public static boolean validateIdCard(String idCard) {
		return PATTERN_ID_CARD.matcher(idCard).matches();
	}

	/**
	 * 校验银行卡
	 *
	 * @return
	 */
	public static boolean validateBankCardId(String bankCardId) {
		return PATTERN_BANK_CARD_ID.matcher(bankCardId).matches();
	}

	/**
	 * 校验手机
	 *
	 * @return
	 */
	public static boolean validateMobile(String mobile) {
		return PATTERN_MOBILE.matcher(mobile).matches();
	}

	/**
	 * 校验电话
	 *
	 * @return
	 */
	public static boolean validatePhone(String phone) {
		return PATTERN_PHONE.matcher(phone).matches();
	}

	/**
	 * 校验日期
	 *
	 * @return
	 */
	public static boolean validateDate(String date) {
		return PATTERN_DATE.matcher(date).matches();
	}

	/**
	 * 校验QQ
	 *
	 * @param qq
	 * @return
	 */
	public static boolean validateQq(String qq) {
		return PATTERN_QQ.matcher(qq).matches();
	}

	/**
	 * 校验自然数
	 *
	 * @param integerStr
	 * @return
	 */
	public static boolean validateInteger(String integerStr) {
		return PATTERN_INTEGER.matcher(integerStr).matches();
	}

	/**
	 * 校验金额
	 *
	 * @param moneyStr
	 * @return
	 */
	public static boolean validateMoney(String moneyStr) {
		return PATTERN_MONEY.matcher(moneyStr).matches();
	}

	/**
	 * 校验日期时间
	 *
	 * @param dateTime
	 * @return
	 */
	public static boolean validateDateTime(String dateTime) {
		return PATTERN_DATE_TIME.matcher(dateTime).matches();
	}

	/**
	 * 校验网址
	 *
	 * @param site
	 * @return
	 */
	public static boolean validateSite(String site) {
		return PATTERN_SITE.matcher(site).matches();
	}

	/**
	 * 校验日期时间
	 *
	 * @param time
	 * @return
	 */
	public static boolean validateTime(String time) {
		return PATTERN_TIME.matcher(time).matches();
	}

	/**
	 * 验证oss视频资源地址
	 *
	 * @param ossVidelUrl
	 * @return
	 */
	public static boolean validateOssVideoUrl(String ossVidelUrl) {
		return PATTERN_OSS_VIDEO_URL.matcher(ossVidelUrl).matches();
	}
}
