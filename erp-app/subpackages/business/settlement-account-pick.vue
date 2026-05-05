<template>
	<view class="page">
		<view v-if="loading" class="state">加载中…</view>
		<view v-else-if="!list.length" class="state">暂无结算账户</view>
		<scroll-view v-else scroll-y class="scroll">
			<view v-for="a in list" :key="a.id" class="row" @click="select(a)">
				<view class="main">
					<text class="name">{{ a.name || '未命名' }}</text>
					<text class="code">编码：{{ a.code || '-' }}</text>
				</view>
				<text class="type">{{ a.typeName || '' }}</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import CONFIG from '@/utils/config';

export default {
	data() {
		return {
			list: [],
			loading: false
		};
	},

	onLoad() {
		this.loadList();
	},

	methods: {
		async loadList() {
			this.loading = true;
			try {
				const data = await this.$api.settlementAccountList({});
				this.list = (data && data.accountList) || [];
			} catch (e) {
				uni.showToast({ title: e.message || '加载失败', icon: 'none' });
				this.list = [];
			} finally {
				this.loading = false;
			}
		},

		select(a) {
			if (!a || !a.id) return;
			uni.setStorageSync(CONFIG.CHECKOUT_RETURN_ACCOUNT_KEY, {
				id: a.id,
				name: a.name || '',
				code: a.code || ''
			});
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f8f8f8;
}

.scroll {
	height: 100vh;
}

.state {
	padding: 80rpx;
	text-align: center;
	font-size: 28rpx;
	color: #999;
}

.row {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	background: #fff;
	margin: 16rpx 20rpx;
	padding: 28rpx 24rpx;
	border-radius: 16rpx;
}

.main {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.name {
	font-size: 32rpx;
	color: #222;
	font-weight: 600;
}

.code {
	margin-top: 10rpx;
	font-size: 26rpx;
	color: #888;
}

.type {
	font-size: 24rpx;
	color: #666;
	flex-shrink: 0;
	max-width: 40%;
	text-align: right;
}
</style>
