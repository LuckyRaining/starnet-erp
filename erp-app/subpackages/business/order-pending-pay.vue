<template>
	<view class="page">
		<view v-if="!list.length" class="empty">暂无待付款采购单</view>
		<view v-for="item in list" :key="item.id" class="card">
			<view class="card-main" @click="resume(item)">
				<view class="row-top">
					<text class="tag">采购待支付</text>
					<text class="amt">¥{{ amountText(item) }}</text>
				</view>
				<text class="meta">{{ formatTime(item.createdAt) }} · {{ item.lines.length }} 种商品</text>
			</view>
			<view class="card-actions">
				<button size="mini" class="btn-cancel" @click.stop="cancel(item)">取消</button>
				<button size="mini" type="warn" @click.stop="resume(item)">去支付</button>
			</view>
		</view>
	</view>
</template>

<script>
import { formatMoney } from '@/utils/format';
import { listPendingPayPurchases, removePendingPurchaseDraft } from '@/utils/pending-pay-purchase';

export default {
	data() {
		return {
			list: []
		};
	},

	onShow() {
		this.refresh();
	},

	methods: {
		refresh() {
			this.list = listPendingPayPurchases();
		},

		amountText(item) {
			const lines = (item && item.lines) || [];
			const sum = lines.reduce((s, row) => {
				const price = Number(row.product_price) || 0;
				const qty = Number(row.product_count) || 0;
				return s + price * qty;
			}, 0);
			return formatMoney(sum);
		},

		formatTime(ts) {
			if (!ts) return '';
			const d = new Date(Number(ts));
			const pad = (n) => `${n}`.padStart(2, '0');
			return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
		},

		resume(item) {
			if (!item || !item.id) return;
			uni.navigateTo({
				url: `/subpackages/business/purchase-checkout?pendingId=${encodeURIComponent(item.id)}`
			});
		},

		cancel(item) {
			if (!item || !item.id) return;
			uni.showModal({
				title: '取消待付款',
				content: '确定取消该笔待支付采购？取消后需从购物车重新结算。',
				success: ({ confirm }) => {
					if (!confirm) return;
					removePendingPurchaseDraft(item.id);
					this.refresh();
					uni.showToast({ title: '已取消', icon: 'none' });
				}
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f8f8f8;
	padding: 20rpx;
	box-sizing: border-box;
}

.empty {
	text-align: center;
	color: #999;
	margin-top: 28vh;
	font-size: 28rpx;
}

.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
}

.row-top {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.tag {
	font-size: 24rpx;
	color: #c00000;
	background: #ffecec;
	padding: 6rpx 14rpx;
	border-radius: 8rpx;
}

.amt {
	font-size: 34rpx;
	font-weight: 700;
	color: #333;
}

.meta {
	display: block;
	margin-top: 14rpx;
	font-size: 24rpx;
	color: #888;
}

.card-actions {
	display: flex;
	justify-content: flex-end;
	gap: 16rpx;
	margin-top: 20rpx;
}

.btn-cancel {
	background: #f2f2f2;
	color: #666;
}
</style>
