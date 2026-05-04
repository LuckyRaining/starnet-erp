<template>
	<view class="page">
		<!-- 顶部：时段问候 + 本地/登录用户名（见 refreshWelcome） -->
		<view class="welcome-bar">
			<text class="welcome-text">{{ greetingLine }}</text>
			<text class="welcome-sub">星络收银 · 高效经营</text>
		</view>

		<!-- 广告轮播：多条时才显示指示点与自动播放；点击逻辑见 onBannerTap -->
		<swiper
			class="banner-swiper"
			:indicator-dots="banners.length > 1"
			:autoplay="banners.length > 1"
			interval="4500"
			duration="400"
			circular
			indicator-color="rgba(255,255,255,.45)"
			indicator-active-color="#ffffff"
		>
			<swiper-item v-for="(item, index) in banners" :key="index">
				<view class="banner-slide" @click="onBannerTap(item)">
					<image v-if="item.image" class="banner-img" :src="item.image" mode="aspectFill" />
					
					<view v-else class="banner-placeholder">
						<uni-icons type="image-filled" size="44" color="rgba(255,255,255,.85)" />
						<text class="banner-placeholder-text">{{ item.title || '广告招租中' }}</text>
						<text class="banner-placeholder-hint">优质展位 · 欢迎洽谈</text>
					</view>
				</view>
			</swiper-item>
		</swiper>

		<!-- 快捷入口：tabBar 页用 switchTab，分包页用 navigateTo（见 go） -->
		<view>
			<view class="section-title">
				<text class="section-title-text">快捷功能</text>
			</view>
			<view class="quick-row">
				<view
					v-for="item in quickMenus"
					:key="item.path"
					class="quick-item"
					@click="go(item.path)"
				>
					<view class="quick-icon-wrap">
						<uni-icons :type="item.icon" size="26" color="#C00000" />
					</view>
					<text class="quick-label">{{ item.label }}</text>
				</view>
			</view>
		</view>

		<!-- 底部扩展区：按星期轮换贴士 + 跳转统计/搜索 -->
		<view class="insight-card">
			<view class="insight-head">
				<uni-icons class="insight-star-icon" type="star-filled" size="22" color="#b8860b" />
				<text class="insight-title">今日经营贴士</text>
			</view>
			<text class="insight-line">{{ dailyTip.line1 }}</text>
			<text class="insight-line muted">{{ dailyTip.line2 }}</text>
			<view class="insight-actions">
				<view class="chip" @click="go('/pages/statistic/statistic')">
					<uni-icons class="chip-icon" type="bars" size="16" color="#555" />
					<text>去看统计</text>
				</view>
				<view class="chip" @click="go('/subpackages/business/search')">
					<uni-icons class="chip-icon" type="search" size="16" color="#555" />
					<text>搜商品</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import userUtils from '@/utils/user';

const { getUserInfo } = userUtils;

/** 按当前小时返回问候前缀（与需求中的时段文案对应） */
function timeGreetingPrefix(hour) {
	if (hour >= 0 && hour < 5) return '凌晨好';
	if (hour >= 5 && hour < 8) return '早上好';
	if (hour >= 8 && hour < 11) return '上午好';
	if (hour >= 11 && hour < 13) return '中午好';
	if (hour >= 13 && hour < 17) return '下午好';
	if (hour >= 17 && hour < 19) return '傍晚好';
	if (hour >= 19 && hour < 22) return '晚上好';
	return '夜深了';
}

export default {
	data() {
		return {
			displayName: 'XXX',
			/** onShow 里会被 refreshWelcome 覆盖为时段问候 */
			greetingPrefix: '您好',
			/**
			 * 轮播数据：默认占位。接入接口后可替换为多条。
			 * kind 说明：
			 * - placeholder：占位图，点击仅提示
			 * - product：需 productId，跳转商品详情分包页
			 * - miniProgram：需 appId（可选 path），微信内跳转外部小程序
			 * - web：需 url（http/https），H5 直接跳转；小程序/App 走 external-web 分包
			 * - app：弹窗说明，可选 downloadUrl 复制下载链接
			 * 有 image 时用 image；无 image 则渲染占位样式。
			 */
			banners: [
				{
					kind: 'placeholder',

					// placeholder
					title: '广告招租中',
					hint: '',

					//product
					productId: 0,
					image: '',

					// miniProgram
					appId: '',
					path: '',

					// web
					url: '',

					// app
					downloadUrl: ''
				}
			],
			quickMenus: [
				{ label: '商品管理', icon: 'shop', path: '/pages/category/category' },
				{ label: '智能收银', icon: 'wallet', path: '/pages/cart/cart' },
				{ label: '数据统计', icon: 'bars', path: '/pages/statistic/statistic' },
				{ label: '挂单&取单', icon: 'download', path: '/subpackages/business/hold-order' }
			]
		};
	},

	computed: {
		/** 完整问候语：时段前缀 + 昵称（未登录或无用户信息时为 XXX） */
		greetingLine() {
			return `${this.greetingPrefix}，${this.displayName}`;
		},

		/** 根据星期几 取不同贴士，避免首页下半区空洞 */
		dailyTip() {
			const tips = [
				{
					line1: '开门三件事：清点陈列、核对价签、检查收银。',
					line2: '高峰前提早补货，顾客少时整理滞销品，盘活货架。'
				},
				{
					line1: '库存周转快一分，资金沉淀少一点。',
					line2: '畅销品放在视线黄金带，新品用小标签提醒尝鲜价。'
				},
				{
					line1: '收银高峰开启「挂单&取单」，复杂订单也能从容处理。',
					line2: '晚间闭店前对照统计页，复盘今日流水与品类占比。'
				},
				{
					line1: '会员与老客回访成本低，一句问候常常换来复购。',
					line2: '分类页快速建档新品，条码对齐可减少收银纠错。'
				},
				{
					line1: '促销规则写清楚：买赠、折扣、套餐分开备注。',
					line2: '异常退款保留小票或截图，便于后续对账追溯。'
				},
				{
					line1: '周末客流大，提前备好零钱与购物袋。',
					line2: '闲置时段可做货架深度清洁，顾客体感立竿见影。'
				},
				{
					line1: '新品上架首周关注动销，及时调整陈列与话术。',
					line2: '「数据统计」里看趋势，比凭感觉进货更稳。'
				}
			];
			// getDay()：获取 星期几，使用本地时间
			const d = new Date().getDay();
			return tips[d % tips.length];
		}
	},

	onShow() {
		// 从登录页返回或 token 不变时仍刷新一次，保证昵称与时段最新
		this.refreshWelcome();
	},

	methods: {
		/** 同步时段问候前缀 + 本地缓存中的用户信息（loginName 等与登录页 setUserInfo 一致） */
		refreshWelcome() {
			const hour = new Date().getHours();
			this.greetingPrefix = timeGreetingPrefix(hour);
			
			const info = getUserInfo();
			const name = info && (info.loginName || info.userName || info.nickname);
			this.displayName = name || 'XXX';
		},

		/**
		 * 轮播点击：按 kind 分流。外链类先 showModal 再跳转，符合小程序审核与用户预期。
		 */
		onBannerTap(item) {
			if (!item || item.kind === 'placeholder') {
				uni.$showMsg('广告位招商中，敬请期待');
				return;
			}
			if (item.kind === 'product') {
				const id = item.productId != null ? String(item.productId) : '';
				if (!id) {
					uni.$showMsg('商品信息缺失');
					return;
				}
				uni.navigateTo({
					url: `/subpackages/business/product-detail?id=${encodeURIComponent(id)}`
				});
				return;
			}
			if (item.kind === 'miniProgram') {
				const hint = item.hint || '将打开其他小程序，是否继续？';
				uni.showModal({
					title: '跳转确认',
					content: hint,
					success: (res) => {
						if (!res.confirm) return;
						// #ifdef MP-WEIXIN
						if (!item.appId) {
							uni.$showMsg('未配置小程序 AppId');
							return;
						}
						uni.navigateToMiniProgram({
							appId: item.appId,
							path: item.path || '',
							fail: () => uni.$showMsg('跳转失败，请稍后重试')
						});
						// #endif
						// #ifndef MP-WEIXIN
						uni.$showMsg('当前环境不支持跳转外部小程序');
						// #endif
					}
				});
				return;
			}
			if (item.kind === 'web') {
				const url = item.url || '';
				const hint = item.hint || '将打开外部网页，是否继续？';
				if (!/^https?:\/\//i.test(url)) {
					uni.$showMsg('链接格式不正确');
					return;
				}
				uni.showModal({
					title: '即将离开',
					content: hint,
					success: (res) => {
						if (!res.confirm) return;
						// #ifdef H5
						// H5 无分包 web-view 壳时直接当前窗口打开
						window.location.assign(url);
						// #endif
						// #ifndef H5
						// 小程序/App：全屏 web-view 页打开（微信小程序需在后台配置业务域名）
						const encoded = encodeURIComponent(url);
						uni.navigateTo({
							url: `/subpackages/system/external-web?url=${encoded}`,
							fail: () => {
								uni.setClipboardData({
									data: url,
									success: () => uni.$showMsg('链接已复制，请在浏览器中打开')
								});
							}
						});
						// #endif
					}
				});
				return;
			}
			if (item.kind === 'app') {
				const hint = item.hint || '请前往应用商店下载对应 APP，若已安装可复制下载链接。';
				uni.showModal({
					title: '打开 APP',
					content: hint,
					confirmText: item.downloadUrl ? '复制链接' : '知道了',
					success: (res) => {
						if (!res.confirm || !item.downloadUrl) return;
						uni.setClipboardData({
							data: item.downloadUrl,
							success: () => uni.$showMsg('下载链接已复制')
						});
					}
				});
			}
		},

		/** tabBar 页面必须用 switchTab，否则跳转失败；其余页面 navigateTo */
		go(path) {
			const tabBarPages = [
				'/pages/home/home',
				'/pages/category/category',
				'/pages/cart/cart',
				'/pages/statistic/statistic',
				'/pages/mine/mine'
			];
			if (tabBarPages.includes(path)) {
				uni.switchTab({ url: path });
			} else {
				uni.navigateTo({ url: path });
			}
		}
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	padding: 24rpx 24rpx 48rpx;
	box-sizing: border-box;
}

.welcome-bar {
	background: linear-gradient(125deg, #1a237e 0%, #3949ab 45%, #5c6bc0 100%);
	border-radius: 20rpx;
	padding: 32rpx 28rpx 28rpx;
	color: #fff;
	margin-bottom: 24rpx;
	box-shadow: 0 12rpx 32rpx rgba(26, 35, 126, 0.28);
}

.welcome-text {
	font-size: 36rpx;
	font-weight: 600;
	display: block;
	letter-spacing: 1rpx;
}

.welcome-sub {
	display: block;
	margin-top: 12rpx;
	font-size: 24rpx;
	opacity: 0.88;
}

.banner-swiper {
	height: 280rpx;
	border-radius: 20rpx;
	overflow: hidden;
	margin-bottom: 28rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.banner-slide {
	height: 100%;
	width: 100%;
}

.banner-img {
	width: 100%;
	height: 100%;
	display: block;
}

.banner-placeholder {
	height: 100%;
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, #37474f 0%, #546e7a 40%, #78909c 100%);
	color: #fff;
	padding: 24rpx;
	box-sizing: border-box;
}

.banner-placeholder-text {
	margin-top: 16rpx;
	font-size: 34rpx;
	font-weight: 600;
}

.banner-placeholder-hint {
	margin-top: 10rpx;
	font-size: 24rpx;
	opacity: 0.85;
}

.section-title {
	margin-bottom: 16rpx;
}

.section-title-text {
	font-size: 30rpx;
	font-weight: 700;
	color: #222;
}

.quick-row {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	background: #fff;
	border-radius: 20rpx;
	padding: 28rpx 12rpx 32rpx;
	margin-bottom: 28rpx;
	box-shadow: 0 4rpx 18rpx rgba(0, 0, 0, 0.06);
}

.quick-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.quick-icon-wrap {
	width: 88rpx;
	height: 88rpx;
	border-radius: 24rpx;
	background: #fff5f5;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 14rpx;
}

.quick-label {
	font-size: 24rpx;
	color: #333;
	text-align: center;
	line-height: 1.35;
}

.insight-card {
	background: #fff;
	border-radius: 20rpx;
	padding: 28rpx 26rpx 26rpx;
	border-left: 8rpx solid #c00000;
	box-shadow: 0 4rpx 18rpx rgba(0, 0, 0, 0.06);
}

.insight-head {
	display: flex;
	flex-direction: row;
	align-items: center;
	margin-bottom: 18rpx;
}

.insight-star-icon {
	margin-right: 10rpx;
}

.insight-title {
	font-size: 30rpx;
	font-weight: 700;
	color: #333;
}

.insight-line {
	display: block;
	font-size: 26rpx;
	color: #444;
	line-height: 1.65;
	margin-bottom: 10rpx;
}

.insight-line.muted {
	color: #777;
	font-size: 24rpx;
	margin-bottom: 22rpx;
}

.insight-actions {
	display: flex;
	flex-direction: row;
	flex-wrap: wrap;
}

.chip {
	display: inline-flex;
	flex-direction: row;
	align-items: center;
	padding: 12rpx 22rpx;
	border-radius: 999rpx;
	background: #f5f5f5;
	font-size: 24rpx;
	color: #555;
	margin-right: 16rpx;
	margin-bottom: 12rpx;
}

.chip-icon {
	margin-right: 8rpx;
}
</style>
