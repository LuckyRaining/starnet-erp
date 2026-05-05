<template>
	<view class="page">
		<!-- 1. 从 搜索确认 or 点某个历史记录 进入。url带 query.keyword -->
		<!-- 2. 从 商品分类页 进入。url带 query.keyword -->
		<view class="list">
			<view v-for="item in products" :key="item.id" class="card" @click="openDetail(item)">
				<view class="row">
					<text class="name">{{ item.name }}</text>
					<text class="price">¥{{ item.retailPrice || 0 }}</text>
				</view>

				<view class="row sub">
					<text>编码：{{ item.code || '-' }}</text>
					<text>条码：{{ item.barcode || '-' }}</text>
				</view>

				<view class="row sub">
					<text>库存：{{ item.stock || 0 }}</text>
				</view>
			</view>

			<!-- 加载更多 -->
			<uni-load-more :status="loadStatus" />
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			/** 关键词 */
			keyword: '',
			/** 三级分类 id（从分类页带入，用于按类别筛选商品） */
			categoryId: '',


			/** 当前页码 */
			products: [],

			/** 当前页码 */
			current: 1,
			/** 每页记录数 */
			size: 20,
			/** 总页数 */
			pages: 1,
			/** 总记录数 */
			total: 1,

			/** 是否正在请求数据 */
			loading: false,
			/** 
			 * 判断是否已到 最后一页。
			 * 若已到 “最后一页” 或 “本页不足 size 条” 时，置 true，避免重复触底请求；
			 * 否则，置 false。
			 */
			finished: false
		};
	},

	computed: {
		loadStatus() {
			if (this.loading) return 'loading';
			if (!this.products.length) return 'more';
			return this.finished ? 'noMore' : 'more';
		}
	},

	onLoad(query) {
		// 与 navigateTo 路径中的 “路径参数=encodeURIComponent(路径参数)” 对应
		this.keyword = query.keyword ? decodeURIComponent(query.keyword) : '';
		this.categoryId = query.categoryId ? decodeURIComponent(query.categoryId) : '';
		const categoryName = query.categoryName ? decodeURIComponent(query.categoryName) : '';
		if (categoryName) {
			uni.setNavigationBarTitle({ title: categoryName });
		}
		this.resetAndLoad();
	},

	onPullDownRefresh() {
		this.resetAndLoad().finally(() => {
			uni.stopPullDownRefresh();
		});
	},

	onReachBottom() {
		if (!this.finished && !this.loading) {
			this.loadMore();
		}
	},

	methods: {
		resetAndLoad() {
			this.current = 1;
			this.products = [];
			this.finished = false;
			return this.loadPage(true);
		},

		/**
		 * 当 isRefresh = true 时：下拉刷新 或 首次进入，初始化列表；
		 * 
		 * 若触底加载，则追加 records。
		 */
		async loadPage(isRefresh) {
			// 判断是否正在请求其它数据，如果是，则不发起额外的请求
			if (this.loading) return;
			// 打开节流阀，表示 正在请求数据
			this.loading = true;
			try {
				const queryPayload = {};
				if (this.keyword) queryPayload.keyword = this.keyword;
				if (this.categoryId) queryPayload.categoryId = this.categoryId;

				const data = await this.$api.productPage({
					query: queryPayload,
					current: this.current,
					size: this.size
				});

				const page = data.productPage;
				/** 新记录 */
				const records = (page && page.records) || [];
				/** 总记录数 */
				this.total = (page && page.total) || 1;

				if (isRefresh) {
					this.products = records;
				} else {
					/** 旧记录 + 新记录，拼接而成 */
					this.products = [...this.products, ...records];
				}

				/**
				 * 如果下面的公式成立，则证明没有下一页数据了：
				 * 当前的页码值 * 每页显示多少条数据 >= 总数据条数
				 */
				// if (this.current >= this.pages || records.length < this.size) {
				if (this.current * this.size >= this.total) {
					this.finished = true;
				}
			} catch (error) {
				uni.$showMsg(error.message || '加载失败');
			} finally {
				// 关闭节流阀，表示 数据已请求完毕
				this.loading = false;
			}
		},

		loadMore() {
			// 判断是否已到 最后一页。若是，则不发起额外的请求
			if (this.finished) return;

			this.current += 1;
			this.loadPage(false);
		},

		openDetail(item) {
			uni.navigateTo({
				url: `/subpackages/business/product-detail?id=${encodeURIComponent(item.id)}`
			});
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
	min-height: 100vh;
	background: #f8f8f8;
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
