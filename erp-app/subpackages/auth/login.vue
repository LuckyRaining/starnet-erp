<template>
	<view class="container">
		<view class="login-box">
			<view class="logo">
				<text class="logo-icon">🏪</text>
				<text class="logo-text">星络ERP</text>
			</view>
			
			<view class="form">
				<view class="form-item">
					<view class="input-wrapper">
						<text class="input-icon">👤</text>
						<input 
							class="input" 
							v-model="loginName" 
							placeholder="请输入用户名/手机号"
							placeholder-class="placeholder"
						/>
					</view>
				</view>
				
				<view class="form-item">
					<view class="input-wrapper">
						<text class="input-icon">🔒</text>
						<input 
							class="input" 
							v-model="password" 
							type="password"
							placeholder="请输入密码"
							placeholder-class="placeholder"
						/>
					</view>
				</view>
				
				<view class="login-btn" @tap="handleLogin" :class="{ disabled: loading }">
					<text>{{ loading ? '登录中...' : '登录' }}</text>
				</view>
			</view>
			
			<view class="tips">
				<text class="tip-text">测试账号: admin / 123456</text>
			</view>
		</view>
	</view>
</template>

<script>
import { userApi } from '../../utils/api.js'
import { setToken, setUserInfo } from '../../utils/user.js'

export default {
	data() {
		return {
			loginName: '',
			password: '',
			loading: false
		}
	},
	
	methods: {
		async handleLogin() {
			if (!this.loginName || !this.password) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			this.loading = true
			
			try {
				const res = await userApi.login({
					loginName: this.loginName,
					password: this.password
				})
				
				if (res.data && res.data.token) {
					// 保存token
					setToken(res.data.token)
					
					// 获取用户信息(这里简化处理,实际应该调用用户详情接口)
					setUserInfo({
						username: this.loginName,
						name: this.loginName
					})
					
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})
					
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/mine/mine'
						})
					}, 1500)
				}
			} catch (err) {
				console.error('登录失败:', err)
			} finally {
				this.loading = false
			}
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx;
}

.login-box {
	width: 100%;
	max-width: 600rpx;
	background: #ffffff;
	border-radius: 20rpx;
	padding: 60rpx 40rpx;
	box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.2);
	
	.logo {
		text-align: center;
		margin-bottom: 60rpx;
		
		.logo-icon {
			display: block;
			font-size: 100rpx;
			margin-bottom: 20rpx;
		}
		
		.logo-text {
			display: block;
			font-size: 40rpx;
			font-weight: bold;
			color: #667eea;
		}
	}
	
	.form {
		.form-item {
			margin-bottom: 30rpx;
			
			.input-wrapper {
				display: flex;
				align-items: center;
				height: 90rpx;
				background: #f5f5f5;
				border-radius: 45rpx;
				padding: 0 30rpx;
				
				.input-icon {
					font-size: 36rpx;
					margin-right: 15rpx;
				}
				
				.input {
					flex: 1;
					font-size: 30rpx;
					color: #333;
				}
				
				.placeholder {
					color: #999;
				}
			}
		}
		
		.login-btn {
			height: 90rpx;
			background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
			border-radius: 45rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			color: #ffffff;
			font-size: 32rpx;
			font-weight: bold;
			margin-top: 40rpx;
			box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.4);
			transition: all 0.3s;
			
			&:active {
				transform: scale(0.98);
			}
			
			&.disabled {
				opacity: 0.6;
			}
		}
	}
	
	.tips {
		margin-top: 40rpx;
		text-align: center;
		
		.tip-text {
			font-size: 24rpx;
			color: #999;
		}
	}
}
</style>
