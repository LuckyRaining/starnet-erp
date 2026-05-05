<template>
	<view class="login-card">
		<view class="title">微信授权登录</view>
		<text class="sub">授权后可获取头像、昵称；手机号需单独授权（若当前端支持）。</text>

		<button class="btn-auth" open-type="getUserInfo" @getuserinfo="getUserInfo">登录</button>

		<!-- #ifdef MP-WEIXIN -->
		<button class="btn-phone" open-type="getPhoneNumber" @getphonenumber="onPhoneNumber">授权手机号</button>
		<!-- #endif -->
	</view>
</template>

<script>
import { mapMutations, mapState } from 'vuex';

export default {
	name: 'MyLogin',

	computed: {
		...mapState('m_user', ['redirectInfo'])
	},

	methods: {
		...mapMutations('m_user', ['updateUserinfo', 'updateToken', 'updateRedirectInfo']),

		getUserInfo(e) {
			const detail = e && e.detail;
			if (!detail || (detail.errMsg && detail.errMsg.indexOf('fail') !== -1)) {
				uni.$showMsg('需要授权基本信息才能登录');
				return;
			}
			const u = detail.userInfo;
			if (!u) {
				uni.$showMsg('未获取到用户信息');
				return;
			}
			this.updateUserinfo({
				nickName: u.nickName || '',
				avatarUrl: u.avatarUrl || '',
				gender: u.gender,
				country: u.country || '',
				province: u.province || '',
				city: u.city || ''
			});
			this.getToken(u);
		},

		onPhoneNumber(e) {
			const d = e && e.detail;
			if (!d || d.errMsg === 'getPhoneNumber:fail user deny') {
				uni.$showMsg('已取消手机号授权');
				return;
			}
			this.updateUserinfo({
				phoneCode: d.code || '',
				phoneEncryptedData: d.encryptedData || '',
				phoneIv: d.iv || ''
			});
			uni.$showMsg('手机号授权已记录');
		},

		async getToken(profile) {
			try {
				await new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: resolve,
						fail: reject
					});
				});
			} catch (err) {
				/* 非微信端 uni.login 可能失败，仍尝试走后端账号登录 */
			}

			try {
				const wxNick = (profile && profile.nickName) || '';
				const data = await this.$api.userLogin({
					loginName: 'admin',
					password: '123456'
				});
				const token = data && data.token;
				if (!token) {
					throw new Error('登录接口未返回 token');
				}
				this.updateToken(token);
				this.updateUserinfo({
					loginName: 'admin',
					nickName: wxNick || 'admin',
					avatarUrl: (profile && profile.avatarUrl) || ''
				});
				uni.$showMsg('登录成功', 1500, 'success');
				setTimeout(() => this.navigateBack(), 400);
			} catch (error) {
				uni.$showMsg(error.message || '登录失败');
			}
		},

		navigateBack() {
			const info = this.redirectInfo;
			this.updateRedirectInfo(null);
			if (!info || !info.from) {
				uni.switchTab({ url: '/pages/home/home' });
				return;
			}
			const type = info.openType || 'switchTab';
			if (type === 'switchTab') {
				uni.switchTab({ url: info.from });
			} else if (type === 'navigateTo') {
				uni.navigateTo({ url: info.from });
			} else if (type === 'redirectTo') {
				uni.redirectTo({ url: info.from });
			} else {
				uni.navigateBack();
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.login-card {
	background: #fff;
	border-radius: 20rpx;
	padding: 40rpx 32rpx;
	margin-top: 20rpx;
}

.title {
	font-size: 34rpx;
	font-weight: 700;
	text-align: center;
	margin-bottom: 16rpx;
}

.sub {
	display: block;
	font-size: 24rpx;
	color: #999;
	line-height: 1.5;
	margin-bottom: 36rpx;
	text-align: center;
}

.btn-auth,
.btn-phone {
	margin-top: 20rpx;
	font-size: 30rpx;
	border-radius: 12rpx;
}

.btn-auth {
	background: #c00000;
	color: #fff;
}

.btn-phone {
	background: #f5f5f5;
	color: #333;
}
</style>
