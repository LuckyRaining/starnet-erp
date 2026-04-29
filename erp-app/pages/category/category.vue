<template>
	<view class="page">
		<view class="search-wrap">
			<uni-search-bar placeholder="按名称/条码搜索商品" v-model="keyword" @confirm="loadProducts" @clear="clearSearch" />
		</view>

		<view class="list">
			<view v-for="item in products" :key="item.id" class="card" @click="openDetail(item)">
				<view class="row">
					<text class="name">{{ item.name }}</text>
					<text class="price">¥{{ item.retailPrice || 0 }}</text>
				</view>

				<view class="row sub">
					<text>条码：{{ item.barcode || '-' }}</text>
					<text>库存：{{ item.stock || 0 }}</text>
				</view>
			</view>

			<uni-load-more :status="loading ? 'loading' : products.length ? 'noMore' : 'more'" />
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			keyword: '',
			products: [],
			loading: false // 是否正在请求数据
		};
	},

	onShow() {
		this.loadProducts();
	},
	methods: {
		// 获取商品列表数据的方法
		async loadProducts() {
			// 打开节流阀，表示 正在请求数据
			this.loading = true;

			// 发起请求
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
				uni.showToast({
					title: error.message,
					icon: 'none'
				});
			} finally {
				// 关闭节流阀
				this.loading = false;
			}
		},

		clearSearch() {
			this.keyword = '';
			this.loadProducts();
		},

		openDetail(item) {
			uni.navigateTo({
				url: `/subpackages/business/product-detail?id=${item.id}`
			});
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.search-wrap {
	background: #fff;
	border-radius: 14rpx;
	padding: 8rpx;
	margin-bottom: 20rpx;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 22rpx;
	margin-bottom: 16rpx;
}

.row {
	display: flex;
	justify-content: space-between;
}

.name {
	font-size: 30rpx;
	color: #222;
	font-weight: 600;
}

.price {
	color: #ff5f2e;
	font-size: 30rpx;
}

.sub {
	margin-top: 12rpx;
	font-size: 24rpx;
	color: #888;
}
</style>
