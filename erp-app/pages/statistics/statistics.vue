<template>
	<view class="container">
		<!-- 时间筛选 -->
		<view class="filter-bar">
			<picker mode="date" :value="startDate" @change="onStartDateChange">
				<view class="date-picker">{{ startDate || '开始日期' }}</view>
			</picker>
			<text class="separator">至</text>
			<picker mode="date" :value="endDate" @change="onEndDateChange">
				<view class="date-picker">{{ endDate || '结束日期' }}</view>
			</picker>
			<view class="query-btn" @tap="loadStatistics">
				<text>查询</text>
			</view>
		</view>
		
		<!-- 统计卡片 -->
		<view class="stats-cards">
			<view class="stat-card">
				<text class="stat-value">{{ stats.totalSales }}</text>
				<text class="stat-label">总销售额</text>
			</view>
			<view class="stat-card">
				<text class="stat-value">{{ stats.totalOrders }}</text>
				<text class="stat-label">订单数</text>
			</view>
			<view class="stat-card">
				<text class="stat-value">{{ stats.avgAmount }}</text>
				<text class="stat-label">客单价</text>
			</view>
		</view>
		
		<!-- 商品销售排行 -->
		<view class="chart-section">
			<view class="section-title">商品销售排行 TOP10</view>
			<view class="chart-container">
				<canvas canvas-id="productChart" id="productChart" class="chart"></canvas>
			</view>
		</view>
		
		<!-- 销售趋势 -->
		<view class="chart-section">
			<view class="section-title">销售趋势</view>
			<view class="chart-container">
				<canvas canvas-id="trendChart" id="trendChart" class="chart"></canvas>
			</view>
		</view>
		
		<!-- 空数据提示 -->
		<view class="empty-tip" v-if="!hasData">
			<text>暂无统计数据</text>
		</view>
	</view>
</template>

<script>
import { analysisApi } from '../../utils/api.js'

export default {
	data() {
		return {
			startDate: '',
			endDate: '',
			stats: {
				totalSales: 0,
				totalOrders: 0,
				avgAmount: 0
			},
			productRanking: [],
			hasData: false
		}
	},
	
	onShow() {
		// 默认显示最近7天
		this.initDateRange()
		this.loadStatistics()
	},
	
	methods: {
		initDateRange() {
			const end = new Date()
			const start = new Date()
			start.setDate(start.getDate() - 6)
			
			this.endDate = this.formatDate(end)
			this.startDate = this.formatDate(start)
		},
		
		formatDate(date) {
			const year = date.getFullYear()
			const month = String(date.getMonth() + 1).padStart(2, '0')
			const day = String(date.getDate()).padStart(2, '0')
			return `${year}-${month}-${day}`
		},
		
		onStartDateChange(e) {
			this.startDate = e.detail.value
		},
		
		onEndDateChange(e) {
			this.endDate = e.detail.value
		},
		
		async loadStatistics() {
			if (!this.startDate || !this.endDate) {
				uni.showToast({
					title: '请选择日期范围',
					icon: 'none'
				})
				return
			}
			
			try {
				uni.showLoading({
					title: '加载中...'
				})
				
				// 获取销售明细
				const res = await analysisApi.saleDetailList({
					startDate: this.startDate,
					endDate: this.endDate
				})
				
				if (res.data && res.data.productList) {
					const sales = res.data.productList
					
					// 计算统计数据
					this.stats.totalOrders = sales.length
					this.stats.totalSales = sales.reduce((sum, item) => sum + (item.amount || 0), 0).toFixed(2)
					this.stats.avgAmount = this.stats.totalOrders > 0 
						? (this.stats.totalSales / this.stats.totalOrders).toFixed(2)
						: 0
					
					// 商品销售排行
					this.calculateProductRanking(sales)
					
					this.hasData = true
					
					// 绘制图表
					setTimeout(() => {
						this.drawProductChart()
						this.drawTrendChart(sales)
					}, 100)
				}
				
				uni.hideLoading()
			} catch (err) {
				uni.hideLoading()
				console.error('加载统计数据失败:', err)
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				})
			}
		},
		
		calculateProductRanking(sales) {
			// 按商品汇总
			const productMap = {}
			sales.forEach(item => {
				const key = item.productId
				if (!productMap[key]) {
					productMap[key] = {
						productName: item.productName || '未知商品',
						quantity: 0,
						amount: 0
					}
				}
				productMap[key].quantity += item.quantity || 0
				productMap[key].amount += item.amount || 0
			})
			
			// 转换为数组并排序
			this.productRanking = Object.values(productMap)
				.sort((a, b) => b.amount - a.amount)
				.slice(0, 10) // 取前10
		},
		
		drawProductChart() {
			const ctx = uni.createCanvasContext('productChart', this)
			const width = 350
			const height = 300
			const padding = 50
			const barHeight = 20
			const gap = 15
			
			// 清空画布
			ctx.clearRect(0, 0, width, height)
			
			// 绘制标题
			ctx.setFontSize(14)
			ctx.setFillStyle('#333')
			ctx.setTextAlign('center')
			ctx.fillText('销售金额', width / 2, 25)
			
			if (this.productRanking.length === 0) {
				ctx.setTextAlign('center')
				ctx.setFillStyle('#999')
				ctx.fillText('暂无数据', width / 2, height / 2)
				ctx.draw()
				return
			}
			
			// 找出最大值用于缩放
			const maxAmount = Math.max(...this.productRanking.map(p => p.amount))
			const maxBarWidth = width - padding * 2 - 120 // 留出标签空间
			
			// 绘制柱状图
			this.productRanking.forEach((product, index) => {
				const y = padding + index * (barHeight + gap)
				const barWidth = (product.amount / maxAmount) * maxBarWidth
				
				// 商品名称
				ctx.setFontSize(11)
				ctx.setFillStyle('#666')
				ctx.setTextAlign('right')
				const name = product.productName.length > 8 
					? product.productName.substring(0, 8) + '...'
					: product.productName
				ctx.fillText(name, padding - 5, y + barHeight / 2 + 4)
				
				// 柱子
				const gradient = ctx.createLinearGradient(padding, 0, padding + barWidth, 0)
				gradient.addColorStop(0, '#667eea')
				gradient.addColorStop(1, '#764ba2')
				ctx.setFillStyle(gradient)
				ctx.fillRect(padding, y, barWidth, barHeight)
				
				// 金额
				ctx.setFontSize(10)
				ctx.setFillStyle('#ff6b6b')
				ctx.setTextAlign('left')
				ctx.fillText(`¥${product.amount.toFixed(0)}`, padding + barWidth + 5, y + barHeight / 2 + 4)
			})
			
			ctx.draw()
		},
		
		drawTrendChart(sales) {
			const ctx = uni.createCanvasContext('trendChart', this)
			const width = 350
			const height = 300
			const padding = 50
			
			// 清空画布
			ctx.clearRect(0, 0, width, height)
			
			// 按日期汇总
			const dateMap = {}
			sales.forEach(item => {
				const date = item.issueDate
				if (!dateMap[date]) {
					dateMap[date] = 0
				}
				dateMap[date] += item.amount || 0
			})
			
			const dates = Object.keys(dateMap).sort()
			const amounts = dates.map(date => dateMap[date])
			
			if (dates.length === 0) {
				ctx.setFontSize(14)
				ctx.setFillStyle('#999')
				ctx.setTextAlign('center')
				ctx.fillText('暂无数据', width / 2, height / 2)
				ctx.draw()
				return
			}
			
			// 绘制标题
			ctx.setFontSize(14)
			ctx.setFillStyle('#333')
			ctx.setTextAlign('center')
			ctx.fillText('销售趋势', width / 2, 25)
			
			// 计算坐标
			const chartWidth = width - padding * 2
			const chartHeight = height - padding * 2
			const maxAmount = Math.max(...amounts)
			const pointGap = dates.length > 1 ? chartWidth / (dates.length - 1) : 0
			
			// 绘制折线
			ctx.setStrokeStyle('#667eea')
			ctx.setLineWidth(2)
			ctx.beginPath()
			
			dates.forEach((date, index) => {
				const x = padding + index * pointGap
				const y = padding + chartHeight - (amounts[index] / maxAmount) * chartHeight
				
				if (index === 0) {
					ctx.moveTo(x, y)
				} else {
					ctx.lineTo(x, y)
				}
			})
			
			ctx.stroke()
			
			// 绘制数据点
			dates.forEach((date, index) => {
				const x = padding + index * pointGap
				const y = padding + chartHeight - (amounts[index] / maxAmount) * chartHeight
				
				ctx.beginPath()
				ctx.arc(x, y, 4, 0, 2 * Math.PI)
				ctx.setFillStyle('#667eea')
				ctx.fill()
				
				// 日期标签
				ctx.setFontSize(10)
				ctx.setFillStyle('#666')
				ctx.setTextAlign('center')
				const label = date.substring(5) // 只显示月-日
				ctx.fillText(label, x, height - padding + 20)
			})
			
			ctx.draw()
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 40rpx;
}

.filter-bar {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: #ffffff;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	gap: 15rpx;
	
	.date-picker {
		flex: 1;
		min-width: 220rpx;
		height: 70rpx;
		background: #f5f5f5;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 26rpx;
		color: #333;
		padding: 0 15rpx;
	}
	
	.separator {
		font-size: 26rpx;
		color: #999;
		flex-shrink: 0;
	}
	
	.query-btn {
		width: 140rpx;
		height: 70rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		color: #ffffff;
		font-size: 26rpx;
		flex-shrink: 0;
	}
}

.stats-cards {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
	padding: 30rpx 20rpx;
	
	.stat-card {
		background: #ffffff;
		border-radius: 12rpx;
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

.chart-section {
	margin: 20rpx;
	
	.section-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		padding-left: 10rpx;
		border-left: 6rpx solid #667eea;
	}
	
	.chart-container {
		background: #ffffff;
		border-radius: 12rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
		
		.chart {
			width: 100%;
			height: 600rpx;
		}
	}
}

.empty-tip {
	text-align: center;
	padding: 100rpx 0;
	color: #999;
	font-size: 28rpx;
}
</style>
