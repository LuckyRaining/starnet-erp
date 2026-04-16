<template>
	<view class="container">
		<view class="header">
			<text class="title">挂单记录</text>
			<text class="subtitle">共 {{ holdOrders.length }} 个挂单</text>
		</view>
		
		<scroll-view class="list" scroll-y v-if="holdOrders.length > 0">
			<view class="order-card" v-for="(order, index) in holdOrders" :key="order.id">
				<view class="order-header">
					<text class="order-time">{{ order.time }}</text>
					<view class="order-actions">
						<view class="action-btn restore" @tap="restoreOrder(index)">
							<text>取单</text>
						</view>
						<view class="action-btn delete" @tap="deleteOrder(index)">
							<text>删除</text>
						</view>
					</view>
				</view>
				
				<view class="order-items">
					<view class="item" v-for="(item, idx) in order.items" :key="idx">
						<text class="item-name">{{ item.productName }}</text>
						<text class="item-qty">x{{ item.quantity }}</text>
						<text class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</text>
					</view>
				</view>
				
				<view class="order-footer">
					<text class="total-label">合计:</text>
					<text class="total-amount">¥{{ order.totalAmount }}</text>
				</view>
			</view>
		</scroll-view>
		
		<view class="empty" v-else>
			<text class="empty-icon">📝</text>
			<text class="empty-text">暂无挂单</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			holdOrders: []
		}
	},
	
	onShow() {
		this.loadHoldOrders()
	},
	
	methods: {
		loadHoldOrders() {
			this.holdOrders = uni.getStorageSync('holdOrders') || []
		},
		
		saveHoldOrders() {
			uni.setStorageSync('holdOrders', this.holdOrders)
		},
		
		restoreOrder(index) {
			const order = this.holdOrders[index]
			
			// 恢复到购物车
			uni.setStorageSync('cart', order.items)
			
			// 删除挂单
			this.holdOrders.splice(index, 1)
			this.saveHoldOrders()
			
			uni.showToast({
				title: '已恢复到购物车',
				icon: 'success'
			})
			
			setTimeout(() => {
				uni.switchTab({
					url: '/pages/checkout/checkout'
				})
			}, 1500)
		},
		
		deleteOrder(index) {
			uni.showModal({
				title: '提示',
				content: '确定删除该挂单?',
				success: (res) => {
					if (res.confirm) {
						this.holdOrders.splice(index, 1)
						this.saveHoldOrders()
					}
				}
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

.header {
	background: #ffffff;
	padding: 30rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	
	.title {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	
	.subtitle {
		display: block;
		font-size: 26rpx;
		color: #999;
	}
}

.list {
	height: calc(100vh - 160rpx);
	padding: 20rpx;
}

.order-card {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	
	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 16rpx;
		border-bottom: 1rpx solid #f5f5f5;
		
		.order-time {
			font-size: 26rpx;
			color: #999;
		}
		
		.order-actions {
			display: flex;
			
			.action-btn {
				padding: 10rpx 20rpx;
				border-radius: 6rpx;
				font-size: 24rpx;
				margin-left: 10rpx;
				
				&.restore {
					background: #e3f2fd;
					color: #2196f3;
				}
				
				&.delete {
					background: #ffebee;
					color: #f44336;
				}
			}
		}
	}
	
	.order-items {
		margin-bottom: 20rpx;
		
		.item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 12rpx 0;
			
			.item-name {
				flex: 1;
				font-size: 28rpx;
				color: #333;
			}
			
			.item-qty {
				font-size: 26rpx;
				color: #666;
				margin: 0 20rpx;
			}
			
			.item-price {
				font-size: 28rpx;
				color: #ff6b6b;
				font-weight: 500;
			}
		}
	}
	
	.order-footer {
		display: flex;
		justify-content: flex-end;
		align-items: center;
		padding-top: 16rpx;
		border-top: 1rpx solid #f5f5f5;
		
		.total-label {
			font-size: 26rpx;
			color: #666;
			margin-right: 10rpx;
		}
		
		.total-amount {
			font-size: 32rpx;
			color: #ff6b6b;
			font-weight: bold;
		}
	}
}

.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
	
	.empty-icon {
		font-size: 100rpx;
		margin-bottom: 20rpx;
	}
	
	.empty-text {
		font-size: 28rpx;
		color: #999;
	}
}
</style>
