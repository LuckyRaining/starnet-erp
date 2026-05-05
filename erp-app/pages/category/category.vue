<template>
	<view class="page">
		<!-- 支持 搜索 和 扫码 功能 -->
		<my-search placeholder="按名称/编码/条码搜索商品" :bgcolor="'#FFFFFF'" :radius="'36rpx'" @click="goSearch" @scan="onScan" />

		<view class="category-wrap" :style="{ height: contentHeightPx + 'px' }">
			<!-- 左侧 滚动列表显示 一级分类 -->
			<scroll-view scroll-y class="col-left" :style="{ height: contentHeightPx + 'px' }">
				<view v-for="(lv1, index) in level1List" :key="lv1.id" :class="['left-item', { active: index === activeLevel1Index }]" @click="activeChanged(index)">
					<text class="left-item-text">{{ lv1.name }}</text>
				</view>
			</scroll-view>

			<!-- 右侧 显示对应一级分类下的 二级和三级分类 -->
			<scroll-view :key="'right-' + activeLevel1Index" scroll-y class="col-right" :style="{ height: contentHeightPx + 'px' }">
				<!-- 二级分类数组 为空 -->
				<view v-if="!currentLevel2List.length" class="right-empty">
					<text class="right-empty-text">暂无子分类</text>
				</view>

				<!-- 二级分类数组 非空 -->
				<view v-for="lv2 in currentLevel2List" :key="lv2.id" class="right-block">
					<text class="lv2-title"># {{ lv2.name }} #</text>
					<view class="lv3-grid">
						<!-- 点击三级分类 可跳转到 商品列表页 -->
						<view v-for="lv3 in lv2ChildList(lv2)" :key="lv3.id" class="lv3-item" @click="openProductList(lv3)">
							<text class="lv3-text">{{ lv3.name }}</text>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
import MySearch from '@/components/my-search/my-search.vue';
import CONFIG from '../../utils/config';

export default {
	components: {
		MySearch
	},

	data() {
		return {
			/** 分类页面 内容高度（单位：px） */
			contentHeightPx: 400,
			/** 一级分类数组 */
			level1List: [],
			/** 选中的 左侧一级分类项，为 class=.left-item 的容器施加 .active 状态 */
			activeLevel1Index: 0
		};
	},

	computed: {
		/** 当前选中的一级分类下的 二级分类数组 */
		currentLevel2List() {
			const lv1 = this.level1List[this.activeLevel1Index];
			return (lv1 && lv1.childList) || [];
		}
	},

	onLoad() {
		// 动态计算并更新 分类页面 内容区域的高度
		this.updateContentHeight();
		// 加载商品分类数据
		this.loadCategories();
	},

	onShow() {
		// 动态计算并更新 分类页面 内容区域的高度
		this.updateContentHeight();
	},

	methods: {
		/**
		 * 参数：
		 * - 屏幕高度 screenHeight
		 * - 原生导航栏高度 NavigationBar（含状态栏高度 statusBarHeight）
		 * - 状态栏高度 statusBarHeight
		 * - 可使用窗口高度 windowHeight
		 * - safeAreaInsets 为 在竖屏正方向下的安全区域插入位置，含 top、left、right、bottom。
		 */
		/**
		 * 参数间的关系计算：
		 * - 屏幕高度 screenHeight = 原生 NavigationBar 高度（含状态栏高度 statusBarHeight）
		 * 						+ 可使用窗口高度 windowHeight
		 * 						+ 原生 TabBar 高度
		 * - 可使用窗口高度 windowHeight 不包含 NavigationBar 和 TabBar 的高度。
		 *
		 * - 屏幕高度 screenHeight = 竖屏安全区域 safeAreaInsets + 顶部和底部手机刘海 高度
		 * - safeAreaInsets 为 在竖屏正方向下的安全区域插入位置，含 top、left、right、bottom。
		 * - safeAreaInsets = 除顶部刘海外的 原生 NavigationBar 高度 + 可使用窗口高度 windowHeight + 除底部刘海外的 原生 TabBar 高度
		 */
		/**
		 * 动态计算并更新 分类页面 内容区域的高度
		 *
		 * - 分类页面内容高度 contentHeightPx ≈ 可使用窗口高度 windowHeight − my-search（72rpx + 14rpx）。
		 */
		updateContentHeight() {
			/** 获取窗口信息 */
			const windowInfo = uni.getWindowInfo();
			/** my-search 组件高度值：86rpx -> ?px */
			const mySearchHeightPx = 86 * (windowInfo.windowWidth / 750);
			// console.log(mySearchPx);
			this.contentHeightPx = windowInfo.windowHeight - mySearchHeightPx;
		},

		/**
		 * 获取二级分类下的三级分类列表
		 *
		 * @param {Object} lv2 - 二级分类对象
		 * @param {Array} lv2.childList - 三级分类数组
		 * @returns {Array} 三级分类列表，若无子分类则返回空数组
		 */
		lv2ChildList(lv2) {
			return (lv2 && lv2.childList) || [];
		},

		/**
		 * 加载商品分类数据
		 *
		 * - 调用 API 获取商品分类列表（返回 data 为一级分类，包含子分类数据）。
		 * - 并更新到 level1List。
		 * - 若当前选中索引 超出范围，则重置为 0。
		 *
		 * @throws {Error} 当接口调用失败时显示错误提示
		 */
		async loadCategories() {
			try {
				// 调用 API 获取商品分类列表
				const data = await this.$api.categoryList({
					type: CONFIG.CATEGORY_TYPE_PRODUCT, // 商品
					query: {}
				});

				// 返回 data 为一级分类，包含子分类数据。
				const list = data.categoryList || [];
				// 并更新到 level1List。
				this.level1List = list;

				// 若当前选中索引 超出范围，则重置为 0。
				if (this.activeLevel1Index >= this.level1List.length) {
					this.activeLevel1Index = 0;
				}
			} catch (e) {
				uni.$showMsg(e.message || '分类加载失败');
			}
		},

		/**
		 * 切换一级分类的 选中状态
		 *
		 * @param {Number} index - 要激活的 一级分类索引
		 */
		activeChanged(index) {
			if (index === this.activeLevel1Index) return;

			this.activeLevel1Index = index;
		},

		/**
		 * 打开三级分类对应的商品列表页面
		 *
		 * @param {Object} lv3 - 三级分类对象
		 * @param {String|Number} lv3.id - 分类ID
		 * @param {String} [lv3.name] - 分类名称（用于页面标题显示）
		 */
		openProductList(lv3) {
			if (!lv3 || !lv3.id) return;

			const query = `keyword=${encodeURIComponent(lv3.id)}&categoryName=${encodeURIComponent(lv3.name || '')}`;
			uni.navigateTo({
				url: `/subpackages/business/product-list?${query}`
			});
		},

		/**
		 * 跳转至 搜索页面
		 */
		goSearch() {
			uni.navigateTo({
				url: '/subpackages/business/search'
			});
		},

		/**
		 * 执行 扫码 操作，并查询商品信息
		 *
		 * 扫码结果当作 keyword，查 /product/page ———— api 接口；
		 * 若 有记录，则默认打开 第一条商品信息的详情；
		 * 若 无记录，则用提示框 提示信息。
		 *
		 * 异常处理：
		 * - 用户取消扫码：静默返回，不显示提示
		 * - 扫码失败：显示错误提示
		 *
		 * @throws {Error} 扫码失败或商品查询失败时显示相应提示
		 */
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
				const keyword = (res && res.result) || '';
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
					url: `/subpackages/business/product-list?keyword=${encodeURIComponent(first.id)}`
				});
			} catch (error) {
				const msg = error.errMsg || error.message || '';
				if (msg.includes('cancel') || msg.includes('取消')) return;
				uni.$showMsg(error.message || '扫码失败');
			}
		}
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	background: #f8f8f8;
	box-sizing: border-box;
}

.category-wrap {
	display: flex;
	flex-direction: row;
	align-items: stretch;
	width: 100%;
	box-sizing: border-box;
}

.col-left {
	flex-shrink: 0;
	width: 200rpx;
	background: #f0f0f0;
	box-sizing: border-box;
}

.left-item {
	padding: 28rpx 16rpx;
	box-sizing: border-box;
	border-left: 6rpx solid transparent;
}

.left-item.active {
	background: #fff;
	border-left-color: #c00000;
}

.left-item-text {
	font-size: 28rpx;
	color: #555;
	line-height: 1.35;
}

.left-item.active .left-item-text {
	color: #c00000;
	font-weight: 600;
}

.col-right {
	flex: 1;
	min-width: 0;
	background: #fff;
	box-sizing: border-box;
}

.right-empty {
	padding: 80rpx 32rpx;
	display: flex;
	justify-content: center;
}

.right-empty-text {
	font-size: 28rpx;
	color: #999;
}

.right-block {
	padding: 24rpx 24rpx 8rpx;
	box-sizing: border-box;
}

.lv2-title {
	display: block;
	font-size: 30rpx;
	color: #888;
	font-weight: 600;
	text-align: center;
	margin-bottom: 16rpx;
}

.lv3-grid {
	display: flex;
	flex-direction: row;
	flex-wrap: wrap;
	margin: 0 -10rpx;
}

.lv3-item {
	width: 33.33%;
	box-sizing: border-box;
	padding: 10rpx;
	margin-bottom: 8rpx;
}

.lv3-text {
	display: -webkit-box;
	-webkit-box-orient: vertical;
	-webkit-line-clamp: 2;
	line-clamp: 2;
	overflow: hidden;
	text-align: center;
	font-size: 26rpx;
	color: #333;
	background: #f8f8f8;
	border-radius: 8rpx;
	padding: 18rpx 8rpx;
	line-height: 1.3;
	box-sizing: border-box;
}
</style>
