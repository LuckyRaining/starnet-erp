<template>
	<view class="page">
		<!-- 吸顶搜索框；有建议列表则 v-if 建议区，否则 v-else 历史区 -->
		<view class="search-sticky">
			<!-- 通过 v-model，随时将 输入的 value值，绑定至 keyword -->
			<uni-search-bar v-model="keyword" placeholder="按名称/编码/条码搜索商品" cancel-button="none" @input="onSearchInput" @confirm="onConfirm" @clear="onClear" />
		</view>

		<!-- 建议区，与下方历史区域互斥展示 -->
		<view v-if="searchResults.length !== 0" class="panel">
			<view class="panel-title">搜索建议</view>

			<view v-for="item in searchResults" :key="item.id" class="suggest-item" @click="pickProduct(item)">
				<view>
					<text class="name">{{ item.name }}</text>
					<text class="meta">编码：{{ item.code || '-' }} · 条码：{{ item.barcode || '-' }}</text>
				</view>

				<text class="price">¥{{ item.retailPrice || 0 }}</text>
			</view>
		</view>

		<!-- 历史区，与上方建议区域互斥展示 -->
		<view v-else class="panel history-panel">
			<view class="history-head">
				<text class="panel-title">搜索历史</text>
				<text v-if="historyList.length" class="clear-all" @click="clearAllHistory">清空</text>
			</view>

			<view v-if="!historyList.length" class="empty-hint">暂无搜索历史</view>

			<view v-for="item in historys" :key="item" class="history-row">
				<text class="history-keyword" @click="openProductList(item)">{{ item }}</text>
				<text class="history-remove" @click.stop="removeHistory(item)">删除</text>
			</view>
		</view>
	</view>
</template>

<script>
import CONFIG from '@/utils/config';

export default {
	data() {
		return {
			/** keyword 为 要搜索的 关键词 */
			keyword: '',
			/**
			 * searchResults 为 搜索 正在输入的关键词，得到暂时性的搜索数组。
			 * 若不为空，则不展示搜索历史
			 */
			searchResults: [],
			/**
			 * 历史搜索列表 historyList 中的元素排列顺序：旧 → 新（即：尾部为最近搜索）；
			 * 当要展示时，使用计算属性 historys（即：头部为最近搜索）（对 historyList 进行 reverse 反转）
			 */
			historyList: [],
			/** input 防抖定时器句柄，需在 clear 时，卸载时清理避免多余请求 */
			debounceTimer: null
		};
	},

	computed: {
		/** 将 最新关键词 排在列表最前，且不直接暴露 historyList 给模板，以防误改源数据 */
		historys() {
			return [...this.historyList].reverse();
		}
	},

	onLoad() {
		try {
			const raw = uni.getStorageSync(CONFIG.HISTORY_STORAGE_KEY);
			if (raw) {
				const parsed = JSON.parse(raw);
				this.historyList = Array.isArray(parsed) ? parsed : [];
			}
		} catch {
			this.historyList = [];
		}
	},

	methods: {
		/** 持久化保存 历史搜索列表 historyList 到本地 */
		saveHistoryStorage() {
			uni.setStorageSync(CONFIG.HISTORY_STORAGE_KEY, JSON.stringify(this.historyList));
		},

		/** 保存关键词 keyword 到 historyList 中 */
		pushKeyword(keyword) {
			const trimmedKeyword = (keyword || '').trim();

			if (!trimmedKeyword) return;

			// 1. 将 Array 数组转化为 Set 对象
			const set = new Set(this.historyList);
			// 2. 调用 Set 对象的 delete 方法，移除对应的元素
			set.delete(trimmedKeyword);
			// 3. 调用 Set 对象的 add 方法，向 Set 中添加元素
			set.add(trimmedKeyword);
			// 4. 将 Set 对象转化为 Array 数组
			this.historyList = Array.from(set);

			this.saveHistoryStorage();
		},

		/** 处理正在输入的事件，通过 v-model，随时将 输入的 value值，绑定至 keyword；此处在做完防抖后，拉取搜索建议列表 searchResults */
		onSearchInput(value) {
			// console.log('value (v-model="keyword"): ' + value); // value === keyword

			// 如果有残留 debounceTimer，则清空原指针所指内存数据，并初始化
			if (this.debounceTimer) {
				clearTimeout(this.debounceTimer);
				this.debounceTimer = null;
			}

			// 500ms 防抖：如果 500 毫秒内，没有触发新的输入事件，才再次为 关键词 keyword 赋值
			this.debounceTimer = setTimeout(() => {
				this.debounceTimer = null;
				this.fetchSuggestions();
			}, 500);
		},

		/** 获得搜索建议列表。根据 keyword（编码/名称/条码），到后端搜索。然后得到搜索建议列表 */
		async fetchSuggestions() {
			const kw = (this.keyword || '').trim();

			// 关键词 keyword 为空时，不请求后端。并清空搜索建议 searchResults（露出历史区）
			if (!kw) {
				this.searchResults = [];
				return;
			}

			try {
				const data = await this.$api.productPage({
					query: { keyword: kw },
					current: 1,
					size: 20
				});
				this.searchResults = (data.productPage && data.productPage.records) || [];
			} catch (error) {
				this.searchResults = [];
				uni.$showMsg(error.message || '加载失败');
			}
		},

		/** 确认输入，进行搜索。写入历史并跳到列表页（带 keyword 查询参数） */
		onConfirm(e) {
			// console.log('e: ' + e); // Object
			// console.log('e.value: ' + e.value); // e.value === keyword
			const kw = ((e && e.value) || this.keyword || '').trim();
			if (!kw) return;
			this.pushKeyword(kw);

			uni.navigateTo({
				url: `/subpackages/business/product-list?keyword=${encodeURIComponent(kw)}`
			});
		},

		/** 清空输入 */
		onClear() {
			this.keyword = '';
			this.searchResults = [];

			// 如果有残留 debounceTimer，则清空原指针所指内存数据，并初始化
			if (this.debounceTimer) {
				clearTimeout(this.debounceTimer);
				this.debounceTimer = null;
			}
		},

		/** 加入 购物车。选中商品，并写入缓存 */
		pickProduct(item) {
			if (this.keyword) {
				this.pushKeyword(this.keyword);
			}

			uni.navigateTo({
				url: `/subpackages/business/product-detail?id=${encodeURIComponent(item.id)}`
			});
			// uni.setStorageSync('STAR_NET_SELECTED_PRODUCT', item);
			// uni.navigateBack(); // 返回上一页
		},

		/** 打开 商品列表 */
		openProductList(keyword) {
			this.pushKeyword(keyword);
			uni.navigateTo({
				url: `/subpackages/business/product-list?keyword=${encodeURIComponent(keyword)}`
			});
		},

		/** 删除 选定的 历史记录 */
		removeHistory(keyword) {
			this.historyList = this.historyList.filter((x) => x !== keyword);
			this.saveHistoryStorage();
		},

		/** 清空 所有 历史记录 */
		clearAllHistory() {
			uni.showModal({
				title: '提示',
				content: '确定清空全部搜索历史？',
				success: (res) => {
					// 取消清空
					if (!res.confirm) return;

					// 二次确认，清空搜索历史记录
					this.historyList = [];
					uni.removeStorageSync(CONFIG.HISTORY_STORAGE_KEY);
				}
			});
		}
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	background: #f8f8f8;
	padding-bottom: 40rpx;
}

/* 滚动时搜索栏贴在视口顶部（注意：位于原生导航栏之下） */
.search-sticky {
	position: sticky;
	top: 0;
	z-index: 100;
	background: #fff;
	padding-bottom: 8rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.panel {
	margin: 16rpx 20rpx 0;
	background: #fff;
	border-radius: 16rpx;
	padding: 16rpx 20rpx 24rpx;
}

.panel-title {
	font-size: 26rpx;
	color: #666;
	font-weight: 600;
}

.history-panel .panel-title {
	flex-shrink: 0;
}

.history-head {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 12rpx;
}

.clear-all {
	font-size: 24rpx;
	color: #999;
}

.empty-hint {
	text-align: center;
	padding: 40rpx 0;
	color: #bbb;
	font-size: 26rpx;
}

.suggest-item {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	align-items: center;
	padding: 22rpx 0;
	border-bottom: 1px solid #f3f3f3;
}

.suggest-item:last-child {
	border-bottom: none;
}

.name {
	display: block;
	font-size: 28rpx;
	color: #222;
}

.meta {
	display: block;
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

.price {
	color: #ff5f2e;
	font-size: 28rpx;
	flex-shrink: 0;
	margin-left: 16rpx;
}

.history-row {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 0;
	border-bottom: 1px solid #f3f3f3;
}

.history-row:last-child {
	border-bottom: none;
}

.history-keyword {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.history-remove {
	font-size: 24rpx;
	color: #c00000;
	padding-left: 24rpx;
}
</style>
