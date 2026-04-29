<template>
	<view class="page">
		<view class="card">
			<view class="title">默认收银配置</view>

			<uni-easyinput v-model="form.defaultCustomerId" placeholder="默认客户ID（saleSave必填）" />
			<uni-easyinput v-model="form.defaultWarehouseId" placeholder="默认仓库ID（商品明细必填）" />

			<button type="primary" @click="save">保存配置</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			form: {
				defaultCustomerId: '',
				defaultWarehouseId: ''
			}
		};
	},

	onShow() {
		this.form = uni.getStorageSync('STAR_NET_COMPANY_SETTING') || this.form;
	},

	methods: {
		save() {
			uni.setStorageSync('STAR_NET_COMPANY_SETTING', this.form);
			uni.showToast({ title: '保存成功', icon: 'success' });
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.title {
	font-size: 30rpx;
	font-weight: 600;
}
</style>
