<template>
	<view class="page">
		<view class="hero">
			<text class="hero-title">星络收银 小程序管理系统</text>
			<text class="hero-sub">StarNet · 移动端业务助手</text>
		</view>

		<view class="card">
			<text class="card-title">系统简介</text>
			<text class="para">
				本小程序是 星络收银网页端 的移动端延伸，面向门店与仓储场景的收银、采购与查询需求。通过统一的账号体系对接后端服务，在手机上即可完成商品检索、采购清单、待支付确认、订单查看与基础数据配置等操作，与 PC 端 共用同一套业务数据。
			</text>
		</view>

		<view class="card">
			<text class="card-title">主要功能</text>
			<view class="bullet" v-for="(line, i) in features" :key="i">
				<text class="dot">·</text>
				<text class="bullet-text">{{ line }}</text>
			</view>
		</view>

		<view class="card">
			<text class="card-title">技术说明</text>
			<view class="meta-row"><text class="meta-k">客户端</text><text class="meta-v">uni-app · Vue · Vuex</text></view>
			<view class="meta-row"><text class="meta-k">服务端</text><text class="meta-v">Spring Boot · REST API</text></view>
			<view class="meta-row"><text class="meta-k">版本</text><text class="meta-v">{{ version }}</text></view>
		</view>

		<view class="footer">
			<text class="footer-text">仅供学习与内部业务使用；数据以服务器配置与权限为准。</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			version: 'v1.0.0',
			features: [
				'首页与分类：商品浏览、快捷入口',
				'购物车 / 采购清单：扫码与搜索加购、挂单与取单',
				'结算与采购待支付：仓库、供应商、结算账户选择与购货单提交',
				'我的：登录与用户信息、待付款草稿、购货/销货订单查询',
				'公司设置：系统参数（服务端）与本机小程序默认项',
				'数据统计等扩展能力（依项目启用情况）'
			]
		};
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	padding: 24rpx 24rpx 48rpx;
	box-sizing: border-box;
	background: linear-gradient(180deg, #f0f4ff 0%, #f8f8f8 220rpx);
}

.hero {
	padding: 32rpx 8rpx 28rpx;
}

.hero-title {
	display: block;
	font-size: 36rpx;
	font-weight: 700;
	color: #1a1a2e;
	line-height: 1.35;
}

.hero-sub {
	display: block;
	margin-top: 12rpx;
	font-size: 26rpx;
	color: #64748b;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 28rpx 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 20rpx rgba(15, 23, 42, 0.06);
}

.card-title {
	display: block;
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 16rpx;
}

.para {
	display: block;
	font-size: 28rpx;
	color: #555;
	line-height: 1.65;
	text-align: justify;
}

.bullet {
	display: flex;
	flex-direction: row;
	align-items: flex-start;
	margin-bottom: 12rpx;
}

.dot {
	font-size: 32rpx;
	color: #c00000;
	line-height: 1.4;
	margin-right: 10rpx;
	flex-shrink: 0;
}

.bullet-text {
	flex: 1;
	font-size: 27rpx;
	color: #444;
	line-height: 1.55;
}

.meta-row {
	display: flex;
	flex-direction: row;
	justify-content: space-between;
	padding: 14rpx 0;
	border-bottom: 1px solid #f0f0f0;
	font-size: 27rpx;
}

.meta-row:last-child {
	border-bottom: none;
}

.meta-k {
	color: #888;
}

.meta-v {
	color: #333;
	text-align: right;
	max-width: 65%;
}

.footer {
	padding: 16rpx 8rpx 0;
}

.footer-text {
	font-size: 22rpx;
	color: #aaa;
	line-height: 1.5;
	text-align: center;
	display: block;
}
</style>
