<template>
	<view class="page">
		<view v-if="product.id" class="card">
			<view class="name">{{ product.name }}</view>
			<view class="line">编码：{{ product.code || '-' }}</view>
			<view class="line">条码：{{ product.barcode || '-' }}</view>
			<view class="line">规格：{{ product.spec || '-' }}</view>
			<view class="line">零售价：¥{{ product.retailPrice || 0 }}</view>
			<view class="line">库存：{{ product.stock || 0 }}</view>
		</view>

		<view v-else class="empty">商品不存在或已下架</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			product: {}
		};
	},

	onLoad(query) {
		if (query.id) this.loadDetail(query.id);
	},

	methods: {
		async loadDetail(productId) {
			try {
				const data = await this.$api.productDetail({ productId });
				this.product = data.product || {};
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

.card {
	background: #fff;
	padding: 24rpx;
	border-radius: 16rpx;
}

.name {
	font-size: 34rpx;
	font-weight: 700;
	margin-bottom: 20rpx;
}

.line {
	font-size: 26rpx;
	color: #555;
	margin-bottom: 12rpx;
}

.empty {
	text-align: center;
	margin-top: 30vh;
	color: #999;
}
</style>
