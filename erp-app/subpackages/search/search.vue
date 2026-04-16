<template>
	<view class="container">
		<view class="search-header">
			<view class="search-box">
				<text class="search-icon">🔍</text>
				<input 
					class="input" 
					v-model="keyword"
					placeholder="搜索商品名称/编码"
					placeholder-class="placeholder"
					@confirm="handleSearch"
				/>
			</view>
		</view>
		
		<view class="content">
			<view class="tip-box">
				<text class="tip-icon">⚠️</text>
				<text class="tip-title">功能暂未开放</text>
				<text class="tip-desc">搜索功能需要后端API支持,目前后端暂未提供商品搜索接口</text>
				<text class="tip-hint">您可以:</text>
				<view class="tip-list">
					<text class="tip-item">• 在商品页面浏览所有商品</text>
					<text class="tip-item">• 使用扫码功能快速添加商品</text>
					<text class="tip-item">• 联系开发人员完善搜索功能</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			keyword: ''
		}
	},
	
	methods: {
		handleSearch() {
			if (!this.keyword) {
				uni.showToast({
					title: '请输入搜索关键词',
					icon: 'none'
				})
				return
			}
			
			uni.showModal({
				title: '提示',
				content: '搜索功能暂未开放,请联系开发人员完善后端API',
				showCancel: false
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

.search-header {
	background: #ffffff;
	padding: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
	
	.search-box {
		display: flex;
		align-items: center;
		height: 70rpx;
		background: #f5f5f5;
		border-radius: 35rpx;
		padding: 0 30rpx;
		
		.search-icon {
			font-size: 32rpx;
			margin-right: 10rpx;
		}
		
		.input {
			flex: 1;
			font-size: 28rpx;
			color: #333;
		}
		
		.placeholder {
			color: #999;
		}
	}
}

.content {
	padding: 40rpx;
}

.tip-box {
	background: #ffffff;
	border-radius: 12rpx;
	padding: 60rpx 40rpx;
	text-align: center;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	
	.tip-icon {
		display: block;
		font-size: 100rpx;
		margin-bottom: 20rpx;
	}
	
	.tip-title {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.tip-desc {
		display: block;
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
		margin-bottom: 30rpx;
	}
	
	.tip-hint {
		display: block;
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
		margin-bottom: 20rpx;
		text-align: left;
	}
	
	.tip-list {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
		
		.tip-item {
			font-size: 26rpx;
			color: #666;
			line-height: 2;
			text-align: left;
		}
	}
}
</style>
