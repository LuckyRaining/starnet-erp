<template>
	<view class="page">
		<view class="card">
			<view class="title">关于星络收银系统</view>
			<view class="line">版本：v1.0.0</view>
			<view class="line">架构：uni-app + Spring Boot</view>
			<view class="line">核心模块：商品管理、智能收银、数据统计</view>
			<view class="line">支持：扫码加购、挂单取单、模拟支付、ECharts可视化</view>
		</view>
	</view>
</template>

<script>
	export default {}
</script>

<style lang="scss">
	.page {
		padding: 20rpx;
	}

	.card {
		background: #fff;
		border-radius: 16rpx;
		padding: 22rpx;
	}

	.title {
		font-size: 32rpx;
		font-weight: 700;
		margin-bottom: 18rpx;
	}

	.line {
		font-size: 26rpx;
		color: #555;
		margin-bottom: 10rpx;
	}
</style>
