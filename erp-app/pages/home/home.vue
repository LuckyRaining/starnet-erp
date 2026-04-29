<template>
	<view class="page">
		<view class="hero">
			<text class="title">星络微信收银</text>
			<text class="sub">中小零售场景一站式经营助手</text>
		</view>
		<view class="grid">
			<view v-for="item in menus" :key="item.path" class="cell" @click="go(item.path)">
				<uni-icons :type="item.icon" size="24" color="#2f7df6" />
				<text class="cell-text">{{ item.label }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				menus: [{
						label: "商品分类",
						icon: "shop",
						path: "/pages/category/category"
					},
					{
						label: "智能收银",
						icon: "wallet",
						path: "/pages/cart/cart"
					},
					{
						label: "数据统计",
						icon: "bars",
						path: "/pages/statistic/statistic"
					},
					{
						label: "挂单取单",
						icon: "download",
						path: "/subpackages/business/hold-order"
					}
				]
			}
		},
		methods: {
			go(path) {
				const tabBarPages = [
					'/pages/home/home',
					'/pages/category/category',
					'/pages/cart/cart',
					'/pages/statistic/statistic',
					'/pages/mine/mine'
				];

				if (tabBarPages.includes(path)) {
					uni.switchTab({
						url: path
					});
				} else {
					uni.navigateTo({
						url: path
					});
				}
			}
		}
	}
</script>

<style lang="scss">
	.page {
		padding: 24rpx;
	}

	.hero {
		background: linear-gradient(120deg, #2f7df6, #26c2a7);
		border-radius: 20rpx;
		padding: 36rpx 28rpx;
		color: #fff;
		margin-bottom: 24rpx;
	}

	.title {
		font-size: 38rpx;
		font-weight: 600;
		display: block;
	}

	.sub {
		display: block;
		margin-top: 10rpx;
		font-size: 26rpx;
		opacity: 0.9;
	}

	.grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.cell {
		background: #fff;
		border-radius: 16rpx;
		padding: 30rpx 0;
		text-align: center;
	}

	.cell-text {
		display: block;
		margin-top: 12rpx;
		color: #333;
		font-size: 28rpx;
	}
</style>