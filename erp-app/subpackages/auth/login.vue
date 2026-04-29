<template>
	<view class="page">
		<view class="card">
			<view class="title">星络收银登录</view>
			<uni-easyinput v-model="form.loginName" placeholder="登录账号" />
			<uni-easyinput v-model="form.password" type="password" placeholder="登录密码" />
			<button type="primary" :loading="loading" @click="submit">登录</button>
		</view>
	</view>
</template>

<script>
import userUtils from '@/utils/user';
const { setToken, setUserInfo } = userUtils;

export default {
	data() {
		return {
			loading: false, // 是否正在请求数据

			form: {
				loginName: 'admin',
				password: '123456'
			}
		};
	},

	methods: {
		async submit() {
			if (!this.form.loginName || !this.form.password) {
				uni.showToast({ title: '请填写账号密码', icon: 'none' });
				return;
			}

			// 打开节流阀，表示 正在请求数据
			this.loading = true;

			try {
				const data = await this.$api.userLogin(this.form);

				setToken(data.token);
				setUserInfo({ loginName: this.form.loginName });

				uni.switchTab({ url: '/pages/home/home' });
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			} finally {
				// 关闭节流阀
				this.loading = false;
			}
		}
	},
	onShow: function () {
		// console.log('process.env.NODE_ENV: ' + process.env.NODE_ENV); // process.env.NODE_ENV === "development"
	}
};
</script>

<style lang="scss">
.page {
	min-height: 100vh;
	display: flex;
	align-items: center;
	padding: 30rpx;
}

.card {
	width: 100%;
	background: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.title {
	font-size: 36rpx;
	font-weight: 700;
	text-align: center;
	margin-bottom: 10rpx;
}
</style>
