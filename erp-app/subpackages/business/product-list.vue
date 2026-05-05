<template>
	<view class="page">
		<!-- 1. 从 搜索确认 or 点某个历史记录 进入。url带 query.keyword -->
		<!-- 2. 从 商品分类页 进入。url带 query.keyword -->
		<view class="list">
			<my-product-item
				v-for="item in products"
				:key="item.id"
				:item="item"
				@click="openProductDetail"
			>
				<!-- 通过 slot 插槽进行模板插入 -->
				<template #price>
					<!-- #ifdef VUE2 -->
					<!-- 使用管道符 “|”，调用过滤函数 -->
					<text class="price">¥{{ item.retailPrice | priceDecimaltoFixed_2_father }}</text>
					<!-- #endif -->

					<!-- #ifdef VUE3 -->
					<!-- 使用普通函数，进行过滤 -->
					<text class="price">¥{{ priceDecimaltoFixed_2_father(item.retailPrice) }}</text>
					<!-- #endif -->
				</template>
			</my-product-item>

			<!-- 加载更多 -->
			<uni-load-more :status="loadStatus" />
		</view>
	</view>
</template>

<script>
import MyProductItem from '@/components/my-product-item/my-product-item.vue';

function priceDecimaltoFixed_2_father(val) {
	const n = Number(val);
	return (Number.isFinite(n) ? n : 0).toFixed(2);
}

export default {
	components: {
		MyProductItem
	},

	data() {
		return {
			/** 关键词 */
			keyword: '',
			/** 三级分类 id（从分类页带入，用于按类别筛选商品） */
			// categoryId: '',

			/** 商品列表 */
			products: [],

			/** 当前页码 */
			current: 1,
			/** 每页记录数 */
			size: 20,
			/** 总页数 */
			pages: 1,
			/** 总记录数 */
			total: 0,

			/**
			 * 节流阀：为 true 时表示正在请求，避免重复发起请求（含上拉触底）。
			 */
			loading: false,
			/** 是否已加载完全部数据（无下一页） */
			finished: false
		};
	},

	filters: {
		priceDecimaltoFixed_2_father
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
		// this.categoryId = query.categoryId ? decodeURIComponent(query.categoryId) : '';
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

	/** 监听页面上拉触底，加载更多商品 */
	onReachBottom() {
		if (this.finished || this.loading) return;
		this.loadMore();
	},

	methods: {
		priceDecimaltoFixed_2_father,

		resetAndLoad() {
			this.current = 1;
			this.products = [];
			this.finished = false;
			return this.loadPage(true);
		},

		/**
		 * isRefresh：
		 * - 为 true：首次进入或下拉刷新，替换列表；
		 * - 为 false：上拉加载更多，在请求成功后将新数据拼接到旧数据之后。
		 */
		async loadPage(isRefresh) {
			// 判断是否正在请求其它数据，如果是，则不发起额外的请求
			if (this.loading) return;

			// 打开节流阀，表示 正在请求数据
			this.loading = true;

			try {
				const queryPayload = {};
				if (this.keyword) queryPayload.keyword = this.keyword;
				// categoryId 也能发送到 后端进行请求
				// if (this.categoryId) queryPayload.categoryId = this.categoryId;

				const data = await this.$api.productPage({
					query: queryPayload,
					current: this.current,
					size: this.size
				});

				const page = data.productPage;
				/** 新记录 */
				const records = (page && page.records) || [];
				/** 总记录数 */
				this.total = page && page.total != null ? page.total : 0;

				if (isRefresh) {
					this.products = records;
				} else {
					/** 旧记录 + 新记录，拼接而成 */
					this.products = [...this.products, ...records];
				}

				/**
				 * 如果下面的公式成立，则证明没有下一页数据了：
				 * 当前的页码值 * 每页显示多少条数据 >= 总数据条数
				 * 没有下一页：current * size >= total
				 */
				// if (this.current >= this.pages || records.length < this.size) {
				if (this.current * this.size >= this.total) {
					this.finished = true;
				}
			} catch (error) {
				uni.$showMsg(error.message || '加载失败');
				if (!isRefresh && this.current > 1) {
					this.current -= 1;
				}
			} finally {
				// 关闭节流阀，表示 数据已请求完毕
				this.loading = false;
			}
		},

		loadMore() {
			// 判断是否已到 最后一页。若是，则不发起额外的请求
			if (this.finished || this.loading) return;

			this.current += 1;
			this.loadPage(false);
		},

		openProductDetail(item) {
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

.list .price {
	color: #ff5f2e;
	font-size: 30rpx;
	flex-shrink: 0;
}
</style>
