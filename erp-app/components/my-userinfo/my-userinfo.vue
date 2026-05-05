<template>
	<view class="wrap">
		<view class="header">
			<image class="avatar" :src="avatarSrc" mode="aspectFill" />
			<view class="header-text">
				<text class="nickname">{{ displayName }}</text>
				<text class="hint">星络收银</text>
			</view>
		</view>

		<view class="panel">
			<view class="panel-row">
				<view v-for="item in row1" :key="item.label" class="panel-cell" @click="onShortcut(item)">
					<text class="cell-label">{{ item.label }}</text>
				</view>
			</view>
		</view>

		<view class="panel panel-orders">
			<view class="panel-title">我的订单</view>
			<view class="panel-row">
				<view v-for="item in row2" :key="item.label" class="panel-cell" @click="onShortcut(item)">
					<text class="cell-label">{{ item.label }}</text>
				</view>
			</view>
		</view>

		<view class="menu">
			<view class="item" @click="go('/subpackages/business/warehouse')">
				<text>收货仓库</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
			<!-- #ifdef MP-WEIXIN -->
			<button class="item item-btn" open-type="contact">
				<text>联系客服</text>
				<uni-icons type="right" size="16" color="#999" />
			</button>
			<!-- #endif -->
			<!-- #ifndef MP-WEIXIN -->
			<view class="item" @click="contactHint">
				<text>联系客服</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
			<!-- #endif -->
			<view class="item" @click="go('/subpackages/system/company-setting')">
				<text>公司设置</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
			<view class="item" @click="go('/subpackages/system/about-system')">
				<text>关于系统</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
			<view class="item danger" @click="logout">
				<text>退出登录</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
		</view>
	</view>
</template>

<script>
import { mapMutations, mapState } from 'vuex';

export default {
	name: 'MyUserinfo',

	data() {
		return {
			row1: [
				{ label: '收藏的店铺', action: 'toast' },
				{ label: '收藏的商品', action: 'toast' },
				{ label: '关注的商品', action: 'toast' },
				{ label: '足迹', action: 'toast' }
			],
			row2: [
				{ label: '待付款', path: '/subpackages/business/order-pending-pay' },
				{ label: '待收货', action: 'toast' },
				{ label: '退款/退货', action: 'toast' },
				{ label: '全部订单', path: '/subpackages/business/order-all' }
			]
		};
	},

	computed: {
		...mapState('m_user', ['userinfo']),

		displayName() {
			const u = this.userinfo || {};
			return u.nickName || u.loginName || u.name || '用户';
		},

		avatarSrc() {
			const u = this.userinfo || {};
			return u.avatarUrl || '/static/tabbar_icons/mine.png';
		}
	},

	methods: {
		...mapMutations('m_user', ['updateToken', 'updateUserinfo', 'updateRedirectInfo']),

		go(url) {
			uni.navigateTo({ url });
		},

		contactHint() {
			uni.$showMsg('请在微信小程序内使用客服会话');
		},

		onShortcut(item) {
			if (item && item.path) {
				uni.navigateTo({ url: item.path });
				return;
			}
			if (item && item.action === 'toast') {
				uni.showToast({ title: `${item.label}（敬请期待）`, icon: 'none' });
			}
		},

		logout() {
			this.updateToken('');
			this.updateUserinfo(null);
			this.updateRedirectInfo(null);
			uni.showToast({ title: '已退出', icon: 'success' });
		}
	}
};
</script>

<style lang="scss" scoped>
.wrap {
	padding-bottom: 40rpx;
}

.header {
	background: #fff;
	border-radius: 16rpx;
	padding: 28rpx 24rpx;
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	background: #f2f2f2;
	flex-shrink: 0;
}

.header-text {
	flex: 1;
	min-width: 0;
}

.nickname {
	font-size: 32rpx;
	font-weight: 600;
	color: #333;
	display: block;
}

.hint {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
	display: block;
}

.panel {
	background: #fff;
	border-radius: 16rpx;
	margin-top: 20rpx;
	padding: 24rpx 16rpx;
}

.panel-orders .panel-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 20rpx;
	padding-left: 8rpx;
}

.panel-row {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	gap: 8rpx;
}

.panel-cell {
	flex: 1;
	min-width: 0;
	padding: 16rpx 4rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.cell-label {
	font-size: 22rpx;
	color: #444;
	text-align: center;
	line-height: 1.3;
}

.menu {
	background: #fff;
	border-radius: 16rpx;
	margin-top: 20rpx;
	padding: 0 20rpx;
}

.item {
	height: 92rpx;
	display: flex;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid #f2f2f2;
	font-size: 28rpx;
	color: #333;
}

.item:last-child {
	border-bottom: none;
}

.item.danger {
	color: #c00000;
}

.item-btn {
	width: 100%;
	height: 92rpx;
	line-height: 92rpx;
	padding: 0 0;
	margin: 0;
	background: transparent;
	border: none;
	border-bottom: 1px solid #f2f2f2;
	border-radius: 0;
	text-align: left;
	font-size: 28rpx;
	color: #333;
}

.item-btn::after {
	border: none;
}
</style>
