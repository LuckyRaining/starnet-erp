<template>
	<view class="page">
		<view class="page-body">
			<view v-if="product.id" class="card">
				<view class="name">{{ product.name }}</view>
				<view class="line">编码：{{ product.code || '-' }}</view>
				<view class="line">条码：{{ product.barcode || '-' }}</view>
				<view class="line">规格：{{ product.spec || '-' }}</view>
				<view class="line">零售价：¥{{ product.retailPrice || 0 }}</view>
				<view class="line">库存：{{ product.stock || 0 }}</view>
			</view>

			<view v-else class="empty">商品不存在或已下架！</view>
		</view>

		<!-- 商品导航：固定在页面最底部 -->
		<!-- @click：左侧点击事件 -->
		<!-- @buttonClick：右侧按钮组点击事件 -->
		<view class="goods-nav-fixed">
			<uni-goods-nav
				:fill="true"
				:options="options"
				:button-group="buttonGroup"
				@click="onGoodsNavClick"
			/>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			/** 商品详细信息 */
			product: {},

			/** 组件参数：左侧 图标按钮（店铺、购物车） */
			options: [
				{ icon: 'shop', text: '店铺' },
				{ icon: 'cart', text: '购物车' }
			],

			/** 组件按钮组参数：右侧 操作按钮组（加入购物车、立即购买） */
			buttonGroup: [
				{
					text: '加入购物车',
					backgroundColor: 'linear-gradient(90deg, #FFCD1E, #FF8A18)',
					color: '#fff'
				},
				{
					text: '立即购买',
					backgroundColor: 'linear-gradient(90deg, #FE6035, #EF1224)',
					color: '#fff'
				}
			]
		};
	},

	onLoad(query) {
		// 8. 从路由参数获取商品 Id，并请求详情
		const productId = query.id ? decodeURIComponent(query.id) : '';
		if (productId) {
			this.loadDetail(productId);
		}
	},

	methods: {
		async loadDetail(productId) {
			try {
				const data = await this.$api.productDetail({ productId });
				this.product = data.product || {};
			} catch (error) {
				uni.$showMsg(error.message || '加载失败');
			}
		},

		/**
		 * 左侧 options 点击事件：uni-goods-nav 的 @click
		 * e = { index, content }：事件对象 e 含 index（下标）、content（当前按钮配置对象）
		 */
		onGoodsNavClick(e) {
			console.log(e);
			// uni.$showMsg(`${content.text}（index: ${index}）`);
			if (e.content.text === '购物车') {
				// 切换到 购物车页面
				uni.switchTab({
					url: '/pages/cart/cart'
				})
			}
		}
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	background: #f8f8f8;
	position: relative;
}

.page-body {
	padding: 20rpx;
	/* 避免内容被底部固定导航遮挡：导航条高度约 50px + 安全区 */
	padding-bottom: calc(50px + constant(safe-area-inset-bottom) + 24rpx);
	padding-bottom: calc(50px + env(safe-area-inset-bottom) + 24rpx);
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

.goods-nav-fixed {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
	background-color: #fff;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	box-shadow: 0 -6rpx 24rpx rgba(0, 0, 0, 0.06);
}
</style>
