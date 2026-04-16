<template>
	<view class="container">
		<!-- 用户信息区域 -->
		<view class="user-section">
			<view class="user-info" v-if="isLogin" @tap="goToProfile">
				<view class="avatar">
					<text class="avatar-text">{{ userName.charAt(0) }}</text>
				</view>
				<view class="info">
					<text class="name">{{ userName }}</text>
					<text class="phone">{{ userPhone }}</text>
				</view>
			</view>
			<view class="login-prompt" v-else @tap="goToLogin">
				<view class="avatar">
					<text class="avatar-icon">👤</text>
				</view>
				<view class="prompt-text">
					<text class="title">点击登录</text>
					<text class="subtitle">登录后享受更多服务</text>
				</view>
			</view>
		</view>
		
		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-group">
				<view class="menu-item" @tap="goToHoldOrder">
					<view class="menu-left">
						<text class="menu-icon">📝</text>
						<text class="menu-text">挂单记录</text>
					</view>
					<text class="arrow">›</text>
				</view>
				<view class="menu-item" @tap="showRegisterTip">
					<view class="menu-left">
						<text class="menu-icon">📱</text>
						<text class="menu-text">注册账号</text>
					</view>
					<text class="arrow">›</text>
				</view>
			</view>
			
			<view class="menu-group">
				<view class="menu-item" @tap="goToAbout">
					<view class="menu-left">
						<text class="menu-icon">ℹ️</text>
						<text class="menu-text">关于系统</text>
					</view>
					<text class="arrow">›</text>
				</view>
			</view>
			
			<view class="menu-group" v-if="isLogin">
				<view class="menu-item logout" @tap="handleLogout">
					<view class="menu-left">
						<text class="menu-icon">🚪</text>
						<text class="menu-text">退出登录</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 版本信息 -->
		<view class="version-info">
			<text>星络ERP收银系统 v1.0.0</text>
		</view>
	</view>
</template>

<script>
import { getUserInfo, clearUserInfo, isLogin } from '../../utils/user.js'

export default {
	data() {
		return {
			userName: '',
			userPhone: ''
		}
	},
	
	computed: {
		isLogin() {
			return isLogin()
		}
	},
	
	onShow() {
		this.loadUserInfo()
	},
	
	methods: {
		loadUserInfo() {
			const userInfo = getUserInfo()
			if (userInfo) {
				this.userName = userInfo.name || userInfo.username || '用户'
				this.userPhone = userInfo.mobile || '未绑定手机'
			}
		},
		
		goToLogin() {
			uni.navigateTo({
				url: '/subpackages/auth/login'
			})
		},
		
		goToProfile() {
			// TODO: 跳转到个人资料页
			uni.showToast({
				title: '个人资料功能开发中',
				icon: 'none'
			})
		},
		
		showRegisterTip() {
			uni.showModal({
				title: '注册提示',
				content: '注册功能暂未开放,请联系管理员进行注册',
				showCancel: false,
				confirmText: '知道了'
			})
		},
		
		goToHoldOrder() {
			uni.navigateTo({
				url: '/subpackages/business/holdorder'
			})
		},
		
		goToAbout() {
			uni.navigateTo({
				url: '/subpackages/system/about'
			})
		},
		
		handleLogout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗?',
				success: (res) => {
					if (res.confirm) {
						clearUserInfo()
						this.userName = ''
						this.userPhone = ''
						uni.showToast({
							title: '已退出登录',
							icon: 'success'
						})
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

.user-section {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 80rpx 30rpx 60rpx;
	
	.user-info, .login-prompt {
		display: flex;
		align-items: center;
	}
	
	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.3);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 30rpx;
		border: 4rpx solid rgba(255, 255, 255, 0.5);
		
		.avatar-text {
            display: flex;
            align-items: center;
            justify-content: center;
			font-size: 48rpx;
			color: #ffffff;
			font-weight: bold;
		}
		
		.avatar-icon {
			font-size: 60rpx;
		}
	}
	
	.info {
		display: flex;
		flex-direction: column;
		
		.name {
			font-size: 36rpx;
			color: #ffffff;
			font-weight: bold;
			margin-bottom: 10rpx;
		}
		
		.phone {
			font-size: 26rpx;
			color: rgba(255, 255, 255, 0.8);
		}
	}
	
	.prompt-text {
		display: flex;
		flex-direction: column;
		
		.title {
			font-size: 36rpx;
			color: #ffffff;
			font-weight: bold;
			margin-bottom: 10rpx;
		}
		
		.subtitle {
			font-size: 26rpx;
			color: rgba(255, 255, 255, 0.8);
		}
	}
}

.menu-section {
	padding: 20rpx;
	
	.menu-group {
		background: #ffffff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
		
		.menu-item {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 30rpx;
			border-bottom: 1rpx solid #f5f5f5;
			
			&:last-child {
				border-bottom: none;
			}
			
			&:active {
				background: #f9f9f9;
			}
			
			.menu-left {
				display: flex;
				align-items: center;
				
				.menu-icon {
					font-size: 40rpx;
					margin-right: 20rpx;
				}
				
				.menu-text {
					font-size: 30rpx;
					color: #333;
				}
			}
			
			.arrow {
				font-size: 48rpx;
				color: #ccc;
			}
			
			&.logout {
				.menu-text {
					color: #ff6b6b;
				}
			}
		}
	}
}

.version-info {
	text-align: center;
	padding: 40rpx 0;
	
	text {
		font-size: 24rpx;
		color: #999;
	}
}
</style>
