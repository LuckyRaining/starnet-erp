<template>
	<view class="page">
		<view class="profile">
			<uni-icons type="contact" size="44" color="#2f7df6" />
			<view class="info">
				<text class="name">{{ userName }}</text>
				<text class="hint">欢迎使用星络收银</text>
			</view>
		</view>

		<view class="menu">
			<view class="item" @click="go('/subpackages/business/purchase-orders')">
				<text>我的采购订单</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>

			<view class="item" @click="go('/subpackages/system/company-setting')">
				<text>公司设置</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>

			<view class="item" @click="go('/subpackages/system/about-system')">
				<text>关于系统</text>
				<uni-icons type="right" size="16" color="#999" />
			</view>
		</view>

		<view class="logout">
			<button @click="logout">退出登录</button>
		</view>
	</view>
</template>

<script>
import userUtils from '@/utils/user';
const { getUserInfo, clearLoginState } = userUtils;

export default {
	computed: {
		userName() {
			const user = getUserInfo() || {};
			return user.name || user.loginName || '未登录用户';
		}
	},

	methods: {
		go(url) {
			uni.navigateTo({ url });
		},

		logout() {
			clearLoginState();
			uni.reLaunch({ url: '/subpackages/auth/login' });
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.profile {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	display: flex;
	align-items: center;
	gap: 18rpx;
}

.name {
	font-size: 30rpx;
	font-weight: 600;
	display: block;
}

.hint {
	font-size: 24rpx;
	color: #999;
	display: block;
	margin-top: 6rpx;
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
}

.item:last-child {
	border-bottom: none;
}

.logout {
	margin-top: 24rpx;
}
</style>
