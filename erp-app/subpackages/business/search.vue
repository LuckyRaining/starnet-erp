<template>
	<view class="page">
		<uni-search-bar v-model="keyword" placeholder="搜索商品名称/条码" @confirm="search" />

		<view class="result">
			<view v-for="item in products" :key="item.id" class="item" @click="pick(item)">
				<view>
					<text class="name">{{ item.name }}</text>
					<text class="meta">条码：{{ item.barcode || '-' }}</text>
				</view>
				<text class="price">¥{{ item.retailPrice || 0 }}</text>
			</view>

			<view v-if="!products.length" class="empty">暂无搜索结果</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			keyword: '',
			products: []
		};
	},

	methods: {
		async search() {
			try {
				const data = await this.$api.productPage({
					query: {
						keyword: this.keyword
					},
					current: 1,
					size: 50
				});
				this.products = (data.productPage && data.productPage.records) || [];
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			}
		},

		pick(item) {
			uni.setStorageSync('STAR_NET_SELECTED_PRODUCT', item);
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.result {
	margin-top: 12rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 0 20rpx;
}

.item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1px solid #f3f3f3;
}

.item:last-child {
	border-bottom: none;
}

.name {
	display: block;
	font-size: 28rpx;
}

.meta {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

.price {
	color: #ff5f2e;
}

.empty {
	text-align: center;
	padding: 28rpx 0;
	color: #999;
}
</style>
