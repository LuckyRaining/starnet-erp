import { mapGetters } from 'vuex';
import CONFIG from '../utils/config';

/** 与 pages.json 里 tabBar.list 的 pagePath 一致（无前置 /） */
const TABBAR_ROUTES = [
	'pages/home/home',
	'pages/category/category',
	'pages/cart/cart',
	'pages/statistic/statistic',
	'pages/mine/mine'
];

/**
 * 微信小程序：setTabBarBadge / removeTabBarBadge 仅能在「当前显示的页面是 tabBar 页」时调用，
 * 否则会报 setTabBarBadge:fail not TabBar page（例如用户在分包详情页时，后台 tab 页实例上的 watch 仍会触发）。
 */
function isCurrentPageTabBar() {
	const stack = getCurrentPages();
	const cur = stack[stack.length - 1];
	if (!cur || !cur.route) return false;
	const route = String(cur.route).replace(/^\//, '');
	return TABBAR_ROUTES.includes(route);
}

export default {
	computed: {
		...mapGetters('m_cart', ['total'])
	},

	// 购物车 total（ getters ）一变就刷新角标；immediate 保证第一次进入页面也会执行一次
	watch: {
		total: {
			handler() {
				this.setBadge();
			},
			immediate: true
		}
	},

	onShow() {
		this.setBadge();
	},

	methods: {
		setBadge() {
			if (!isCurrentPageTabBar()) return;

			const n = Number(this.total) || 0;
			/** 购物车 tabBar 在 pages.json 中的下标（首页 0、分类 1、购物车 2、统计 3、我的 4） */
			const idx = CONFIG.TABBAR_INDEX_CART;

			try {
				if (n <= 0) {
					uni.removeTabBarBadge({ index: idx });
					return;
				}
				
				// 调用 uni.setTabBarBadge() 方法，为购物车设置右上角的徽标
				uni.setTabBarBadge({
					index: idx,
					text: n > 99 ? '99+' : String(n) // 注意：text 的值必须是字符串，不能是数字
				});
			} catch (e) {
				// 个别端在非 tabBar 上下文仍可能抛错，忽略即可；下次进入 tabBar 页 onShow 会再同步
			}
		}
	}
};
