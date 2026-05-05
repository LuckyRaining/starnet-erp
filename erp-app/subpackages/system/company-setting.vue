<template>
	<view class="page">
		<view class="card">
			<view class="title">默认收银配置</view>
			<text class="hint">以下 ID 需与 ERP 后台数据一致；采购待支付页可自动带出默认供应商与结算账户；制单人可选。</text>

			<uni-easyinput v-model="form.defaultCustomerId" placeholder="默认客户 ID（旧版销售收银用，可留空）" />
			<uni-easyinput v-model="form.defaultWarehouseId" placeholder="默认仓库 ID（可选）" />

			<view class="sub-title">采购 defaults</view>
			<uni-easyinput v-model="form.defaultSupplierId" placeholder="默认供应商 ID（purchase）" />
			<uni-easyinput v-model="form.defaultSettlementAccountId" placeholder="默认结算账户 ID" />
			<uni-easyinput v-model="form.defaultListerId" placeholder="默认制单人职员 ID（可选）" />

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
				defaultWarehouseId: '',
				defaultSupplierId: '',
				defaultSettlementAccountId: '',
				defaultListerId: ''
			}
		};
	},

	onShow() {
		const saved = uni.getStorageSync('STAR_NET_COMPANY_SETTING');
		if (saved && typeof saved === 'object') {
			this.form = { ...this.form, ...saved };
		}
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

.hint {
	font-size: 24rpx;
	color: #888;
	line-height: 1.45;
	margin-bottom: 8rpx;
}

.sub-title {
	font-size: 26rpx;
	font-weight: 600;
	color: #333;
	margin-top: 12rpx;
}
</style>
