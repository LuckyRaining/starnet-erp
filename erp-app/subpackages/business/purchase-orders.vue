<template>
	<view class="page">
		<view class="toolbar">
			<text class="label">仅展示已审核购货单</text>
		</view>

		<view v-for="item in records" :key="item.id" class="card">
			<view class="row">
				<text>{{ item.code }}</text>
				<text>¥{{ item.preferredAmount || 0 }}</text>
			</view>

			<view class="meta">{{ item.issueDate }} | 供应商：{{ item.supplierName || '-' }}</view>
		</view>

		<view v-if="!records.length" class="empty">暂无采购订单</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			records: []
		};
	},

	onShow() {
		this.loadData();
	},

	methods: {
		async loadData() {
			try {
				const data = await this.$api.purchasePage({
					query: {
						type: 'buy',
						checked: true
					},
					current: 1,
					size: 20
				});
				this.records = (data.purchasePage && data.purchasePage.records) || [];
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			}
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.toolbar {
	background: #fff;
	border-radius: 14rpx;
	padding: 16rpx 20rpx;
	margin-bottom: 16rpx;
}

.label {
	color: #666;
	font-size: 24rpx;
}

.card {
	background: #fff;
	border-radius: 14rpx;
	padding: 18rpx;
	margin-bottom: 12rpx;
}

.row {
	display: flex;
	justify-content: space-between;
	font-size: 28rpx;
}

.meta {
	margin-top: 8rpx;
	font-size: 24rpx;
	color: #888;
}

.empty {
	text-align: center;
	color: #999;
	margin-top: 28vh;
}
</style>
