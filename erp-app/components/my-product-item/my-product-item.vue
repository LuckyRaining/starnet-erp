<template>
	<view class="card" @click="onProductItemClick">
		<view class="row">
			<text class="name">{{ item.name }}</text>
			<text class="price">¥{{ priceDecimaltoFixed_2_children }}</text>
		</view>

		<view class="row sub">
			<text>编码：{{ item.code || '-' }}</text>
			<text>条码：{{ item.barcode || '-' }}</text>
		</view>

		<view class="row sub">
			<text>库存：{{ item.stock || 0 }}</text>
		</view>
	</view>
</template>

<script>
export default {
	name: 'MyProductItem',

	props: {
		item: {
			type: Object,
			// required: true,
			required: false,
		}
	},

	emits: ['click'],

	computed: {
		priceDecimaltoFixed_2_children() {
			const n = Number(this.item.estimatedPurchasePrice);
			return (Number.isFinite(n) ? n : 0).toFixed(2);
		}
	},

	methods: {
		onProductItemClick() {
			this.$emit('click', this.item);
		}
	}
};
</script>

<style lang="scss" scoped>
.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 22rpx;
	margin-bottom: 16rpx;
}

.row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.name {
	font-size: 30rpx;
	color: #222;
	font-weight: 600;
	flex: 1;
	padding-right: 16rpx;
}

.price {
	color: #ff5f2e;
	font-size: 30rpx;
	flex-shrink: 0;
}

.sub {
	margin-top: 12rpx;
	font-size: 24rpx;
	color: #888;
}
</style>
