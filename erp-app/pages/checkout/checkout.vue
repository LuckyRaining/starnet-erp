<template>
	<view class="container">
		<!-- 顶部搜索和扫码 -->
		<view class="header-bar">
			<view class="search-box" @tap="goToSearch">
				<text class="search-icon">🔍</text>
				<text class="search-placeholder">搜索商品</text>
			</view>
			<view class="scan-btn" @tap="scanCode">
				<text class="scan-icon">📱</text>
			</view>
		</view>
		
		<!-- 购物车列表 -->
		<scroll-view class="cart-list" scroll-y v-if="cartList.length > 0">
			<view class="cart-item" v-for="(item, index) in cartList" :key="index">
				<view class="item-info">
					<text class="item-name">{{ item.productName }}</text>
					<text class="item-spec" v-if="item.spec">{{ item.spec }}</text>
					<text class="item-price">¥{{ item.price }}</text>
				</view>
				<view class="item-quantity">
					<view class="qty-btn" @tap="decreaseQty(index)">-</view>
					<text class="qty-value">{{ item.quantity }}</text>
					<view class="qty-btn" @tap="increaseQty(index)">+</view>
				</view>
				<view class="item-total">
					<text class="total-label">小计:</text>
					<text class="total-amount">¥{{ (item.price * item.quantity).toFixed(2) }}</text>
				</view>
				<view class="delete-btn" @tap="deleteItem(index)">
					<text>🗑️</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 空购物车提示 -->
		<view class="empty-cart" v-else>
			<text class="empty-icon">🛒</text>
			<text class="empty-text">购物车是空的</text>
			<text class="empty-hint">快去添加商品吧~</text>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="footer-bar" v-if="cartList.length > 0">
			<view class="left-actions">
				<view class="action-btn" @tap="clearCart">
					<text>清空</text>
				</view>
				<view class="action-btn hold-btn" @tap="holdOrder">
					<text>挂单</text>
				</view>
			</view>
			<view class="right-actions">
				<view class="total-info">
					<text class="total-label">合计:</text>
					<text class="total-amount">¥{{ totalAmount }}</text>
				</view>
				<view class="checkout-btn" @tap="checkout">
					<text>结算</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { checkoutApi, warehouseApi } from '../../utils/api.js'
import { getUserInfo } from '../../utils/user.js'

export default {
	data() {
		return {
			cartList: [],
			defaultWarehouseId: '' // 默认仓库ID
		}
	},
	
	computed: {
		totalAmount() {
			return this.cartList.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
		}
	},
	
	onShow() {
		this.loadCart()
		this.loadDefaultWarehouse()
	},
	
	methods: {
		async loadDefaultWarehouse() {
			try {
				const res = await warehouseApi.page({
					current: 1,
					size: 1
				})
				if (res.data && res.data.warehousePage && res.data.warehousePage.records.length > 0) {
					this.defaultWarehouseId = res.data.warehousePage.records[0].id
				}
			} catch (err) {
				console.error('加载仓库失败:', err)
			}
		},
		
		loadCart() {
			this.cartList = uni.getStorageSync('cart') || []
		},
		
		saveCart() {
			uni.setStorageSync('cart', this.cartList)
		},
		
		increaseQty(index) {
			this.cartList[index].quantity += 1
			this.saveCart()
		},
		
		decreaseQty(index) {
			if (this.cartList[index].quantity > 1) {
				this.cartList[index].quantity -= 1
				this.saveCart()
			} else {
				this.deleteItem(index)
			}
		},
		
		deleteItem(index) {
			uni.showModal({
				title: '提示',
				content: '确定删除该商品?',
				success: (res) => {
					if (res.confirm) {
						this.cartList.splice(index, 1)
						this.saveCart()
					}
				}
			})
		},
		
		clearCart() {
			uni.showModal({
				title: '提示',
				content: '确定清空购物车?',
				success: (res) => {
					if (res.confirm) {
						this.cartList = []
						this.saveCart()
					}
				}
			})
		},
		
		holdOrder() {
			if (this.cartList.length === 0) {
				uni.showToast({
					title: '购物车为空',
					icon: 'none'
				})
				return
			}
			
			// 保存挂单
			let holdOrders = uni.getStorageSync('holdOrders') || []
			holdOrders.push({
				id: Date.now(),
				time: new Date().toLocaleString(),
				items: [...this.cartList],
				totalAmount: this.totalAmount
			})
			uni.setStorageSync('holdOrders', holdOrders)
			
			// 清空当前购物车
			this.cartList = []
			this.saveCart()
			
			uni.showToast({
				title: '挂单成功',
				icon: 'success'
			})
		},
		
		async checkout() {
			if (this.cartList.length === 0) {
				uni.showToast({
					title: '购物车为空',
					icon: 'none'
				})
				return
			}
			
			// 检查是否登录
			const userInfo = getUserInfo()
			if (!userInfo) {
				uni.showModal({
					title: '提示',
					content: '请先登录后再结算',
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: '/subpackages/auth/login'
							})
						}
					}
				})
				return
			}
			
			try {
				uni.showLoading({
					title: '结算中...'
				})
				
				// 获取出库单编号
				const codeRes = await checkoutApi.createCode()
				const code = codeRes.data.code
				
				// 构建商品列表(必须包含warehouseId)
				const productList = this.cartList.map(item => ({
					productId: item.productId,
					warehouseId: this.defaultWarehouseId, // 使用默认仓库
					quantity: item.quantity,
					price: item.price,
					amount: item.price * item.quantity
				}))
				
				// 计算出库总金额
				const amount = this.cartList.reduce((sum, item) => sum + item.price * item.quantity, 0)
				const quantity = this.cartList.reduce((sum, item) => sum + item.quantity, 0)
				
				// 保存出库单
				const today = new Date().toISOString().split('T')[0]
				await checkoutApi.save({
					checkout: {
						code,
						issueDate: today,
						type: 10, // 普通出库
						amount,
						quantity,
						listerId: userInfo.id,
						remark: '小程序收银结算'
					},
					productList
				})
				
				// 清空购物车
				this.cartList = []
				this.saveCart()
				
				uni.hideLoading()
				uni.showModal({
					title: '结算成功',
					content: `订单号: ${code}\n总金额: ¥${amount.toFixed(2)}`,
					showCancel: false,
					success: () => {
						// TODO: 可以跳转到订单详情页或打印小票
					}
				})
			} catch (err) {
				uni.hideLoading()
				console.error('结算失败:', err)
				uni.showToast({
					title: '结算失败,请重试',
					icon: 'none'
				})
			}
		},
		
		goToSearch() {
			uni.navigateTo({
				url: '/subpackages/search/search'
			})
		},
		
		scanCode() {
			uni.scanCode({
				success: (res) => {
					console.log('扫码结果:', res.result)
					// TODO: 根据扫码结果查找商品并添加到购物车
					uni.showToast({
						title: `扫码: ${res.result}`,
						icon: 'none'
					})
				},
				fail: (err) => {
					console.error('扫码失败:', err)
				}
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	height: 100vh;
	display: flex;
	flex-direction: column;
	background: #f5f5f5;
}

.header-bar {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: #ffffff;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	gap: 15rpx;
	
	.search-box {
		flex: 1;
		height: 70rpx;
		background: #f5f5f5;
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		
		.search-icon {
			font-size: 32rpx;
			margin-right: 10rpx;
		}
		
		.search-placeholder {
			font-size: 28rpx;
			color: #999;
		}
	}
	
	.scan-btn {
		width: 70rpx;
		height: 70rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
		transition: all 0.3s;
		
		&:active {
			transform: scale(0.95);
			box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.2);
		}
		
		.scan-icon {
			font-size: 36rpx;
			filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.1));
		}
	}
}

.cart-list {
	flex: 1;
	padding: 20rpx;
}

.cart-item {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
	position: relative;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	
	.item-info {
		margin-bottom: 16rpx;
		
		.item-name {
			display: block;
			font-size: 30rpx;
			color: #333;
			font-weight: 500;
			margin-bottom: 8rpx;
		}
		
		.item-spec {
			display: block;
			font-size: 24rpx;
			color: #999;
			margin-bottom: 8rpx;
		}
		
		.item-price {
			display: block;
			font-size: 28rpx;
			color: #ff6b6b;
			font-weight: bold;
		}
	}
	
	.item-quantity {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
		
		.qty-btn {
			width: 60rpx;
			height: 60rpx;
			background: #f5f5f5;
			border-radius: 8rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			font-size: 36rpx;
			color: #666;
			
			&:active {
				background: #e0e0e0;
			}
		}
		
		.qty-value {
			flex: 1;
			text-align: center;
			font-size: 32rpx;
			color: #333;
			font-weight: 500;
		}
	}
	
	.item-total {
		display: flex;
		justify-content: flex-end;
		align-items: center;
		
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
	
	.delete-btn {
		position: absolute;
		top: 20rpx;
		right: 20rpx;
		width: 50rpx;
		height: 50rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 36rpx;
	}
}

.empty-cart {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	
	.empty-icon {
		font-size: 120rpx;
		margin-bottom: 20rpx;
	}
	
	.empty-text {
		font-size: 32rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	.empty-hint {
		font-size: 26rpx;
		color: #ccc;
	}
}

.footer-bar {
	background: #ffffff;
	padding: 20rpx;
	box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.06);
	
	.left-actions {
		display: flex;
		margin-bottom: 20rpx;
		
		.action-btn {
			flex: 1;
			height: 70rpx;
			background: #f5f5f5;
			border-radius: 35rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 20rpx;
			font-size: 28rpx;
			color: #666;
			
			&:last-child {
				margin-right: 0;
			}
			
			&.hold-btn {
				background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
				color: #ffffff;
			}
		}
	}
	
	.right-actions {
		display: flex;
		align-items: center;
		
		.total-info {
			flex: 1;
			
			.total-label {
				font-size: 26rpx;
				color: #666;
				margin-right: 10rpx;
			}
			
			.total-amount {
				font-size: 40rpx;
				color: #ff6b6b;
				font-weight: bold;
			}
		}
		
		.checkout-btn {
			width: 200rpx;
			height: 80rpx;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-radius: 40rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			color: #ffffff;
			font-size: 32rpx;
			font-weight: bold;
			box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
			
			&:active {
				transform: scale(0.98);
			}
		}
	}
}
</style>
