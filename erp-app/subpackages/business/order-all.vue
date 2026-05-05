<template>
	<view class="page">
		<view class="tabs">
			<view class="tab" :class="{ active: tab === 'purchase' }" @click="switchTab('purchase')">
				购货订单
			</view>
			<view class="tab" :class="{ active: tab === 'sale' }" @click="switchTab('sale')">
				销货订单
			</view>
		</view>

		<view v-if="loading" class="state">加载中…</view>

		<template v-else-if="tab === 'purchase'">
			<view v-for="item in purchaseRecords" :key="item.id" class="card">
				<view class="row">
					<text class="code">{{ item.code }}</text>
					<text class="money">¥{{ formatAmt(item.preferredAmount) }}</text>
				</view>
				<view class="meta">{{ item.issueDate }} · {{ item.supplierName || '供应商' }} · {{ purchaseStatusText(item) }}</view>
			</view>
			<view v-if="!purchaseRecords.length" class="empty">暂无购货订单</view>
		</template>

		<template v-else>
			<view v-for="item in saleRecords" :key="item.id" class="card">
				<view class="row">
					<text class="code">{{ item.code }}</text>
					<text class="money">¥{{ formatAmt(item.preferredAmount) }}</text>
				</view>
				<view class="meta">{{ item.issueDate }} · {{ item.customerName || '客户' }} · {{ saleStatusText(item) }}</view>
			</view>
			<view v-if="!saleRecords.length" class="empty">暂无销货订单</view>
		</template>
	</view>
</template>

<script>
import { formatMoney } from '@/utils/format';

export default {
	data() {
		return {
			tab: 'purchase',
			loading: false,
			purchaseRecords: [],
			saleRecords: []
		};
	},

	onShow() {
		this.loadCurrentTab();
	},

	methods: {
		formatAmt(v) {
			return formatMoney(v != null ? v : 0);
		},

		purchaseStatusText(row) {
			if (row && row.checked === false) return '未审核';
			const st = row && row.status;
			if (st === 10) return '未付款';
			if (st === 20) return '部分付款';
			if (st === 30) return '已付款';
			return '已审核';
		},

		saleStatusText(row) {
			const st = row && row.status;
			if (st === 10) return '未收款';
			if (st === 20) return '部分收款';
			if (st === 30) return '已收款';
			return '';
		},

		switchTab(t) {
			if (this.tab === t) return;
			this.tab = t;
			this.loadCurrentTab();
		},

		async loadCurrentTab() {
			this.loading = true;
			try {
				if (this.tab === 'purchase') {
					const data = await this.$api.purchasePage({
						query: { type: 'buy' },
						current: 1,
						size: 50
					});
					this.purchaseRecords = (data.purchasePage && data.purchasePage.records) || [];
				} else {
					const data = await this.$api.salePage({
						query: { type: 'sell' },
						current: 1,
						size: 50
					});
					this.saleRecords = (data.salePage && data.salePage.records) || [];
				}
			} catch (e) {
				uni.showToast({ title: e.message || '加载失败', icon: 'none' });
			} finally {
				this.loading = false;
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f8f8f8;
	padding: 0 20rpx 40rpx;
	box-sizing: border-box;
}

.tabs {
	display: flex;
	flex-direction: row;
	background: #fff;
	border-radius: 12rpx;
	margin: 20rpx 0;
	overflow: hidden;
}

.tab {
	flex: 1;
	text-align: center;
	padding: 24rpx 0;
	font-size: 28rpx;
	color: #666;
}

.tab.active {
	color: #c00000;
	font-weight: 600;
	background: #fff5f5;
}

.state {
	text-align: center;
	padding: 40rpx;
	color: #999;
}

.card {
	background: #fff;
	border-radius: 14rpx;
	padding: 22rpx;
	margin-bottom: 16rpx;
}

.row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.code {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.money {
	font-size: 28rpx;
	color: #c00000;
	font-weight: 600;
}

.meta {
	margin-top: 10rpx;
	font-size: 24rpx;
	color: #888;
	line-height: 1.4;
}

.empty {
	text-align: center;
	color: #999;
	margin-top: 120rpx;
	font-size: 28rpx;
}
</style>
