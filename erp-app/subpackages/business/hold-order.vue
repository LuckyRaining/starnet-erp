<template>
	<view class="page">
		<view v-for="item in list" :key="item.id" class="card">
			<view class="row">
				<text>挂单时间：{{ item.createdDate }}</text>
				<text>¥{{ item.amount }}</text>
			</view>

			<view class="count">{{ item.items.length }} 件商品</view>

			<view class="actions">
				<button size="mini" type="primary" @click="take(item)">取单</button>
				<button size="mini" @click="remove(item.id)">删除</button>
			</view>
		</view>

		<view v-if="!list.length" class="empty">暂无挂单</view>
	</view>
</template>

<script>
import { getHoldOrders, removeHoldOrder } from '@/utils/hold';

export default {
	data() {
		return {
			list: []
		};
	},

	onShow() {
		this.list = getHoldOrders();
	},

	methods: {
		take(item) {
			uni.setStorageSync('STAR_NET_SELECTED_HOLD_ORDER', item);
			removeHoldOrder(item.id);
			uni.navigateBack();
		},

		remove(id) {
			removeHoldOrder(id);
			this.list = getHoldOrders();
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 16rpx;
}

.row {
	display: flex;
	justify-content: space-between;
}

.count {
	color: #666;
	font-size: 24rpx;
	margin: 12rpx 0;
}

.actions {
	display: flex;
	gap: 12rpx;
}

.empty {
	text-align: center;
	color: #999;
	margin-top: 30vh;
}
</style>
