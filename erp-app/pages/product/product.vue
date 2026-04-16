<template>
	<view class="container">
		<!-- 顶部搜索和扫码 -->
		<view class="header-bar">
			<view class="search-box" @tap="goToSearch">
				<text class="search-icon">🔍</text>
				<text class="search-placeholder">搜索商品名称/编码</text>
			</view>
			<view class="scan-btn" @tap="scanCode">
				<text class="scan-icon">📱</text>
			</view>
		</view>
		
		<!-- 分类侧边栏 + 商品列表 -->
		<view class="content">
			<!-- 左侧分类 -->
			<scroll-view class="category-sidebar" scroll-y>
				<view 
					class="category-item" 
					:class="{ active: currentCategoryId === item.id }"
					v-for="item in categoryList" 
					:key="item.id"
					@tap="selectCategory(item)"
				>
					<text class="category-name">{{ item.name }}</text>
				</view>
			</scroll-view>
			
			<!-- 右侧内容区 -->
			<view class="right-content">
				<!-- 二级分类横向滚动 -->
				<scroll-view 
					class="sub-category-bar" 
					scroll-x 
					v-if="currentSubCategories.length > 0"
				>
					<view class="sub-category-list">
						<view 
							class="sub-category-item"
							:class="{ active: currentSubCategoryId === subItem.id }"
							v-for="subItem in currentSubCategories"
							:key="subItem.id"
							@tap="selectSubCategory(subItem)"
						>
							<text>{{ subItem.name }}</text>
						</view>
					</view>
				</scroll-view>
				
				<!-- 商品列表 -->
				<scroll-view class="product-list" scroll-y @scrolltolower="loadMore">
				<view class="product-grid">
					<view class="product-card" 
						v-for="product in productList" 
						:key="product.id"
						@tap="addToCart(product)"
					>
						<view class="product-image">
							<text class="no-image">📦</text>
						</view>
						<view class="product-info">
							<text class="product-name">{{ product.name }}</text>
							<text class="product-spec" v-if="product.spec">{{ product.spec }}</text>
							<view class="product-price-row">
								<text class="product-price">¥{{ (product.retailPrice || 0).toFixed(2) }}</text>
							</view>
							<view class="product-stock-row">
								<text class="product-stock">库存: {{ product.stock || 0 }}</text>
							</view>
						</view>
						<view class="add-btn">+</view>
					</view>
				</view>
				
				<!-- 加载更多提示 -->
				<view class="load-more" v-if="hasMore">
					<text>加载中...</text>
				</view>
				<view class="no-more" v-else-if="productList.length > 0">
					<text>没有更多了</text>
				</view>
				<view class="empty" v-if="productList.length === 0 && !loading">
					<text>暂无商品</text>
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
import { productApi, categoryApi } from '../../utils/api.js'

export default {
	data() {
		return {
			categoryList: [],
			productList: [],
			currentCategoryId: '',
			currentSubCategoryId: '', // 当前选中的二级分类ID
			currentSubCategories: [], // 当前一级分类下的二级分类列表
			current: 1,
			size: 20,
			hasMore: true,
			loading: false
		}
	},
	
	onShow() {
		this.loadCategories()
		this.loadProducts()
	},
	
	methods: {
		async loadCategories() {
			try {
				const res = await categoryApi.list(30) // 30表示商品分类
				if (res.data && res.data.categoryList) {
					this.categoryList = res.data.categoryList
					if (this.categoryList.length > 0 && !this.currentCategoryId) {
						this.currentCategoryId = this.categoryList[0].id
						// 加载第一个分类的子分类
						this.loadSubCategories(this.categoryList[0])
					}
				}
			} catch (err) {
				console.error('加载分类失败:', err)
			}
		},
		
		// 加载二级分类
		loadSubCategories(category) {
			if (category.childList && category.childList.length > 0) {
				this.currentSubCategories = category.childList
				// 默认选中第一个子分类
				this.currentSubCategoryId = category.childList[0].id
			} else {
				this.currentSubCategories = []
				this.currentSubCategoryId = ''
			}
		},
		
		async loadProducts(refresh = false) {
			if (this.loading) return
			
			this.loading = true
			if (refresh) {
				this.current = 1
				this.productList = []
				this.hasMore = true
			}
			
			try {
				const query = {}
				// 优先使用二级分类ID,如果没有则使用一级分类ID
				if (this.currentSubCategoryId) {
					query.categoryId = this.currentSubCategoryId
				} else if (this.currentCategoryId) {
					query.categoryId = this.currentCategoryId
				}
				
				const res = await productApi.page({
					current: this.current,
					size: this.size,
					query
				})
				
				if (res.data && res.data.productPage) {
					const newProducts = res.data.productPage.records || []
					
					if (refresh) {
						this.productList = newProducts
					} else {
						this.productList = [...this.productList, ...newProducts]
					}
					
					this.hasMore = this.productList.length < res.data.productPage.total
					this.current++
				}
			} catch (err) {
				console.error('加载商品失败:', err)
			} finally {
				this.loading = false
			}
		},
		
		selectCategory(category) {
			this.currentCategoryId = category.id
			// 加载该分类的子分类
			this.loadSubCategories(category)
			// 重置二级分类选择
			this.currentSubCategoryId = ''
			// 重新加载商品
			this.loadProducts(true)
		},
		
		// 选择二级分类
		selectSubCategory(subCategory) {
			this.currentSubCategoryId = subCategory.id
			this.loadProducts(true)
		},
		
		loadMore() {
			if (this.hasMore && !this.loading) {
				this.loadProducts()
			}
		},
		
		addToCart(product) {
			// 获取购物车
			let cart = uni.getStorageSync('cart') || []
			
			// 检查是否已存在
			const existIndex = cart.findIndex(item => item.productId === product.id)
			if (existIndex > -1) {
				cart[existIndex].quantity += 1
			} else {
				cart.push({
					productId: product.id,
					productName: product.name,
					price: product.retailPrice || 0,
					quantity: 1,
					spec: product.spec || ''
				})
			}
			
			// 保存购物车
			uni.setStorageSync('cart', cart)
			
			uni.showToast({
				title: '已添加到购物车',
				icon: 'success'
			})
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
					// TODO: 根据扫码结果查找商品
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

.content {
	flex: 1;
	display: flex;
	overflow: hidden;
}

.category-sidebar {
	width: 180rpx;
	background: #f8f8f8;
	
	.category-item {
		padding: 30rpx 20rpx;
		text-align: center;
		border-left: 4rpx solid transparent;
		transition: all 0.3s;
		
		&.active {
			background: #ffffff;
			border-left-color: #667eea;
			
			.category-name {
				color: #667eea;
				font-weight: bold;
			}
		}
		
		.category-name {
			font-size: 26rpx;
			color: #666;
		}
	}
}

.right-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	background: #ffffff;
}

.sub-category-bar {
	height: 80rpx;
	background: #fafafa;
	border-bottom: 1rpx solid #f0f0f0;
	white-space: nowrap;
	
	.sub-category-list {
		display: inline-flex;
		padding: 10rpx 20rpx;
		
		.sub-category-item {
			display: inline-flex;
			align-items: center;
			justify-content: center;
			padding: 0 24rpx;
			height: 60rpx;
			margin-right: 15rpx;
			background: #ffffff;
			border-radius: 30rpx;
			font-size: 26rpx;
			color: #666;
			border: 1rpx solid #e8e8e8;
			transition: all 0.3s;
			
			&:last-child {
				margin-right: 0;
			}
			
			&.active {
				background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
				color: #ffffff;
				border-color: transparent;
				box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.3);
			}
		}
	}
}

.product-list {
	flex: 1;
	background: #ffffff;
}

.product-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
	padding: 20rpx;
}

.product-card {
	background: #ffffff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	position: relative;
	transition: all 0.3s;
	
	&:active {
		transform: scale(0.98);
	}
	
	.product-image {
		width: 100%;
		height: 200rpx;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		display: flex;
		align-items: center;
		justify-content: center;
		
		.no-image {
			font-size: 80rpx;
		}
	}
	
	.product-info {
		padding: 20rpx;
		
		.product-name {
			display: block;
			font-size: 28rpx;
			color: #333;
			font-weight: 500;
			margin-bottom: 8rpx;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		
		.product-spec {
			display: block;
			font-size: 24rpx;
			color: #999;
			margin-bottom: 12rpx;
		}
		
		.product-price-row {
			margin-bottom: 8rpx;
			
			.product-price {
				font-size: 32rpx;
				color: #ff6b6b;
				font-weight: bold;
			}
		}
		
		.product-stock-row {
			.product-stock {
				font-size: 22rpx;
				color: #999;
			}
		}
	}
	
	.add-btn {
		position: absolute;
		right: 20rpx;
		bottom: 20rpx;
		width: 50rpx;
		height: 50rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 50%;
		color: #ffffff;
		font-size: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
	}
}

.load-more, .no-more, .empty {
	text-align: center;
	padding: 30rpx;
	color: #999;
	font-size: 26rpx;
}
</style>