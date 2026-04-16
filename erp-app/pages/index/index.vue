<template>
	<view class="container">
		<!-- 顶部欢迎区域 -->
		<view class="header">
			<view class="welcome">
				<text class="greeting">{{ greeting }}, {{ userName }}</text>
				<text class="date">{{ currentDate }}</text>
			</view>
		</view>
		
		<!-- 广告轮播图区域 -->
		<view class="banner-section">
			<swiper class="banner-swiper" indicator-dots autoplay circular>
				<swiper-item>
					<view class="banner-item ad-placeholder">
						<text class="ad-text">📢 广告招租中</text>
						<text class="ad-subtitle">联系管理员投放您的广告</text>
					</view>
				</swiper-item>
			</swiper>
		</view>
		
		<!-- 快捷功能入口 -->
		<view class="quick-actions">
			<view class="section-title">快捷功能</view>
			<view class="action-grid">
				<view class="action-item" @tap="goToProduct">
					<view class="action-icon product-icon">📦</view>
					<text class="action-text">商品管理</text>
				</view>
				<view class="action-item" @tap="goToCheckout">
					<view class="action-icon checkout-icon">💰</view>
					<text class="action-text">智能收银</text>
				</view>
				<view class="action-item" @tap="goToStatistics">
					<view class="action-icon stats-icon">📊</view>
					<text class="action-text">数据统计</text>
				</view>
				<view class="action-item" @tap="goToHoldOrder">
					<view class="action-icon hold-icon">📝</view>
					<text class="action-text">挂单取单</text>
				</view>
			</view>
		</view>
		
		<!-- 今日数据概览 -->
		<view class="today-stats">
			<view class="section-title">今日概况</view>
			<view class="stats-cards">
				<view class="stat-card">
					<text class="stat-value">{{ todayStats.orderCount || 0 }}</text>
					<text class="stat-label">订单数</text>
				</view>
				<view class="stat-card">
					<text class="stat-value">¥{{ todayStats.totalAmount || 0 }}</text>
					<text class="stat-label">销售额</text>
				</view>
				<view class="stat-card">
					<text class="stat-value">{{ todayStats.productCount || 0 }}</text>
					<text class="stat-label">商品数</text>
				</view>
			</view>
		</view>
		
		<!-- 最近销售 -->
		<view class="recent-sales">
			<view class="section-title">最近销售</view>
			<view class="sales-list" v-if="recentSales.length > 0">
				<view class="sale-item" v-for="(item, index) in recentSales" :key="index">
					<view class="sale-info">
						<text class="sale-code">{{ item.code }}</text>
						<text class="sale-date">{{ item.issueDate }}</text>
					</view>
					<view class="sale-amount">¥{{ item.amount }}</view>
				</view>
			</view>
			<view class="empty-tip" v-else>
				<text>暂无销售记录</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getUserInfo } from '../../utils/user.js'
import { analysisApi } from '../../utils/api.js'

export default {
	data() {
		return {
			userName: '访客',
			currentDate: '',
			todayStats: {
				orderCount: 0,
				totalAmount: 0,
				productCount: 0
			},
			recentSales: []
		}
	},
	
	computed: {
		greeting() {
			const hour = new Date().getHours()
			if (hour < 6) return '凌晨好'
			if (hour < 9) return '早上好'
			if (hour < 12) return '上午好'
			if (hour < 14) return '中午好'
			if (hour < 17) return '下午好'
			if (hour < 19) return '傍晚好'
			if (hour < 22) return '晚上好'
			return '夜深了'
		}
	},
	
	onShow() {
		this.loadUserInfo()
		this.updateDate()
		this.loadTodayStats()
	},
	
	methods: {
		loadUserInfo() {
			const userInfo = getUserInfo()
			if (userInfo && userInfo.name) {
				this.userName = userInfo.name
			}
		},
		
		updateDate() {
			const now = new Date()
			const year = now.getFullYear()
			const month = String(now.getMonth() + 1).padStart(2, '0')
			const day = String(now.getDate()).padStart(2, '0')
			const weekDays = ['日', '一', '二', '三', '四', '五', '六']
			const weekDay = weekDays[now.getDay()]
			this.currentDate = `${year}-${month}-${day} 星期${weekDay}`
		},
		
		async loadTodayStats() {
			try {
				// 获取今日日期
				const today = new Date().toISOString().split('T')[0]
				
				// 调用销售明细API获取今日数据
				const res = await analysisApi.saleDetailList({
					startDate: today,
					endDate: today
				})
				
				if (res.data && res.data.productList) {
					const sales = res.data.productList
					this.todayStats.orderCount = sales.length
					this.todayStats.totalAmount = sales.reduce((sum, item) => sum + (item.amount || 0), 0).toFixed(2)
					
					// 统计不同商品数量
					const uniqueProducts = new Set(sales.map(item => item.productId))
					this.todayStats.productCount = uniqueProducts.size
					
					// 最近5条销售记录
					this.recentSales = sales.slice(0, 5).map(item => ({
						code: item.saleCode || '-',
						issueDate: item.issueDate || today,
						amount: (item.amount || 0).toFixed(2)
					}))
				}
			} catch (err) {
				console.error('加载统计数据失败:', err)
			}
		},
		
		goToProduct() {
			uni.switchTab({
				url: '/pages/product/product'
			})
		},
		
		goToCheckout() {
			uni.switchTab({
				url: '/pages/checkout/checkout'
			})
		},
		
		goToStatistics() {
			uni.switchTab({
				url: '/pages/statistics/statistics'
			})
		},
		
		goToHoldOrder() {
			uni.navigateTo({
				url: '/subpackages/business/holdorder'
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f5f7fa 0%, #ffffff 100%);
	padding-bottom: 20rpx;
}

.header {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 60rpx 30rpx 40rpx;
	border-radius: 0 0 40rpx 40rpx;
	
	.welcome {
		.greeting {
			display: block;
			font-size: 40rpx;
			font-weight: bold;
			color: #ffffff;
			margin-bottom: 10rpx;
		}
		
		.date {
			display: block;
			font-size: 26rpx;
			color: rgba(255, 255, 255, 0.85);
		}
	}
}

.banner-section {
	margin: 30rpx;
	
	.banner-swiper {
		height: 300rpx;
		border-radius: 20rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
		
		.banner-item {
			width: 100%;
			height: 100%;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			
			&.ad-placeholder {
				background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
				
				.ad-text {
					font-size: 48rpx;
					font-weight: bold;
					color: #ff6b6b;
					margin-bottom: 10rpx;
				}
				
				.ad-subtitle {
					font-size: 26rpx;
					color: #ff8787;
				}
			}
		}
	}
}

.quick-actions {
	margin: 30rpx;
	
	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.action-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 20rpx;
		
		.action-item {
			background: #ffffff;
			border-radius: 16rpx;
			padding: 30rpx 10rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
			transition: all 0.3s;
			
			&:active {
				transform: scale(0.95);
			}
			
			.action-icon {
				width: 80rpx;
				height: 80rpx;
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 40rpx;
				margin-bottom: 12rpx;
				
				&.product-icon {
					background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
				}
				
				&.checkout-icon {
					background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
				}
				
				&.stats-icon {
					background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
				}
				
				&.hold-icon {
					background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
				}
			}
			
			.action-text {
				font-size: 24rpx;
				color: #666;
			}
		}
	}
}

.today-stats {
	margin: 30rpx;
	
	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.stats-cards {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
		
		.stat-card {
			background: #ffffff;
			border-radius: 16rpx;
			padding: 30rpx 20rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
			
			.stat-value {
				font-size: 36rpx;
				font-weight: bold;
				color: #667eea;
				margin-bottom: 8rpx;
			}
			
			.stat-label {
				font-size: 24rpx;
				color: #999;
			}
		}
	}
}

.recent-sales {
	margin: 30rpx;
	
	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.sales-list {
		.sale-item {
			background: #ffffff;
			border-radius: 12rpx;
			padding: 24rpx;
			margin-bottom: 16rpx;
			display: flex;
			justify-content: space-between;
			align-items: center;
			box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
			
			.sale-info {
				display: flex;
				flex-direction: column;
				
				.sale-code {
					font-size: 28rpx;
					color: #333;
					font-weight: 500;
					margin-bottom: 6rpx;
				}
				
				.sale-date {
					font-size: 24rpx;
					color: #999;
				}
			}
			
			.sale-amount {
				font-size: 32rpx;
				font-weight: bold;
				color: #f5576c;
			}
		}
	}
	
	.empty-tip {
		text-align: center;
		padding: 60rpx 0;
		color: #999;
		font-size: 28rpx;
	}
}
</style>
