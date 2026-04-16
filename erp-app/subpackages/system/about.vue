<template>
	<view class="container">
		<view class="content">
			<view class="logo-section">
				<text class="logo-icon">🏪</text>
				<text class="app-name">星络ERP收银系统</text>
				<text class="version">v1.0.0</text>
			</view>
			
			<view class="info-section">
				<view class="info-item">
					<text class="label">系统介绍</text>
					<text class="value">面向中小型零售场景的智能收银ERP小程序，提供商品管理、智能收银、数据统计等核心功能</text>
				</view>
				
				<view class="info-item">
					<text class="label">主要功能</text>
					<view class="feature-list">
						<text class="feature">✓ 商品分类管理</text>
						<text class="feature">✓ 扫码添加商品</text>
						<text class="feature">✓ 智能购物车</text>
						<text class="feature">✓ 挂单取单</text>
						<text class="feature">✓ 销售数据统计</text>
						<text class="feature">✓ ECharts图表展示</text>
					</view>
				</view>
				
				<view class="info-item">
					<text class="label">技术架构</text>
					<view class="tech-list">
						<text class="tech">前端: uni-app + Vue.js</text>
						<text class="tech">后端: Spring Boot + MyBatis</text>
						<text class="tech">数据库: MySQL</text>
						<text class="tech">图表: Canvas绘图</text>
					</view>
				</view>
				
				<view class="info-item">
					<text class="label">开发团队</text>
					<text class="value">星络ERP个人开发</text>
				</view>
				
				<view class="info-item">
					<text class="label">联系方式</text>
					<text class="value">请联系系统管理员</text>
				</view>
			</view>
			
			<view class="copyright">
				<text>© 2024 星络ERP. All Rights Reserved.</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

.content {
	padding: 40rpx;
}

.logo-section {
	text-align: center;
	padding: 60rpx 0;
	background: #ffffff;
	border-radius: 12rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	
	.logo-icon {
		display: block;
		font-size: 120rpx;
		margin-bottom: 20rpx;
	}
	
	.app-name {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		color: #667eea;
		margin-bottom: 10rpx;
	}
	
	.version {
		display: block;
		font-size: 26rpx;
		color: #999;
	}
}

.info-section {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 30rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	
	.info-item {
		margin-bottom: 30rpx;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.label {
			display: block;
			font-size: 30rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 15rpx;
			padding-left: 10rpx;
			border-left: 6rpx solid #667eea;
		}
		
		.value {
			display: block;
			font-size: 28rpx;
			color: #666;
			line-height: 1.8;
		}
		
		.feature-list, .tech-list {
			display: flex;
			flex-direction: column;
			
			.feature, .tech {
				font-size: 26rpx;
				color: #666;
				line-height: 2;
				padding-left: 10rpx;
			}
		}
	}
}

.copyright {
	text-align: center;
	padding: 40rpx 0;
	
	text {
		font-size: 24rpx;
		color: #999;
	}
}
</style>
