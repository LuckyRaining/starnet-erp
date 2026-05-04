<template>
	<view class="page">
		<!-- 点击搜索区 → 搜索页；扫码 → 用条码调接口并可能进详情 -->
		<my-search
			class="top-search"
			:bgcolor="'#ffffff'"
			:radius="'36rpx'"
			@click="goSearch"
			@scan="onScan"
		/>

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
			products: [],
			loading: false // 是否正在请求数据
		};
	},

	onShow() {
		this.loadProducts();
	},

	methods: {
		goSearch() {
			uni.navigateTo({
				url: '/subpackages/business/search'
			});
		},

		/** 扫码结果当作 keyword 查 product/page；无记录提示；有记录默认打开第一条详情 */
		async onScan() {
			try {
				// 调用 uni 扫码接口，获取扫码结果
				// onlyFromCamera: true, 表示只从相机获取扫码结果
				// scanType: ['barCode', 'qrCode'] 表示扫码类型，可以是条码或二维码
				const res = await uni.scanCode({
					onlyFromCamera: true,
					scanType: ['barCode', 'qrCode']
				});
				// 获取扫码结果，将扫码结果赋值给 keyword
				const keyword = res.result || '';
				// 如果扫码结果为空，则不进行后续操作
				if (!keyword) return;

				const data = await this.$api.productPage({
					query: { keyword },
					current: 1,
					size: 10
				});

				const records = (data.productPage && data.productPage.records) || [];
				if (!records.length) {
					uni.$showMsg('不存在该商品');
					return;
				}
				const first = records[0];
				uni.navigateTo({
					url: `/subpackages/business/product-detail?id=${first.id}`
				});
			} catch (error) {
				const msg = error.errMsg || error.message || '';
				if (msg.includes('cancel') || msg.includes('取消')) return;
				uni.$showMsg(error.message || '扫码失败');
			}
		},

		/** 分类 Tab：拉一页商品列表（不按关键词筛选，keyword 传空） */
		async loadProducts() {
			this.loading = true;
			try {
				const data = await this.$api.productPage({
					query: {
						keyword: ''
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
				this.loading = false;
			}
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

.top-search {
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
