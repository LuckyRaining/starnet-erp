<template>
	<view class="page">
		<view class="toolbar">
			<button size="mini" type="primary" @click="scanCode">扫码加购</button>
			<button size="mini" @click="goSearch">搜索商品</button>
			<button size="mini" @click="holdCurrentOrder">挂单</button>
			<button size="mini" @click="goHoldList">取单</button>
		</view>

		<view class="cart-box">
			<view class="row title">
				<text>购物车（{{ cart.length }}）</text>
				<text class="clear" @click="clearCart">清空</text>
			</view>

			<view v-for="item in cart" :key="item.product_id" class="row item">
				<view>
					<text class="name">{{ item.product_name }}</text>
					<text class="price">¥{{ item.product_price }}</text>
				</view>
				<uni-number-box
					:min="1"
					:value="item.product_count"
					@change="(val) => updateCount(item.product_id, val)"
				/>
			</view>

			<view v-if="!cart.length" class="empty">请扫码或搜索商品后添加</view>
		</view>

		<view class="footer">
			<view>
				<text>合计：</text>
				<text class="total">¥{{ totalAmount }}</text>
			</view>
			<button type="warn" :disabled="!cart.length" @click="payNow">模拟支付</button>
		</view>
	</view>
</template>

<script>
// 从 vuex 中按需导出 mapState 辅助方法
import { mapState, mapMutations, mapGetters } from 'vuex';
import { formatDate, formatMoney } from '@/utils/format';
import { getHoldOrders, saveHoldOrder } from '@/utils/hold';
import tabbarBadge from '@/mixins/tabbar-badge.js';

export default {
	// 将 tabbarBadge 混入到当前的页面中进行使用
	mixins: [tabbarBadge],

	computed: {
		...mapState('m_cart', ['cart']),
		...mapGetters('m_cart', ['total']),
		totalAmount() {
			return formatMoney(
				this.cart.reduce((sum, item) => sum + item.product_price * item.product_count, 0)
			);
		}
	},

	onShow() {
		const selected = uni.getStorageSync('STAR_NET_SELECTED_PRODUCT');

		if (selected && selected.id != null) {
			this.addToCart({
				product_id: selected.id,
				product_name: selected.name || '',
				product_price: Number(
					selected.estimatedPurchasePrice != null
						? selected.estimatedPurchasePrice
						: selected.wholesalePrice != null
							? selected.wholesalePrice
							: 0
				),
				product_count: 1,
				cart_state: true
			});
			uni.removeStorageSync('STAR_NET_SELECTED_PRODUCT');
		}

		const holdOrder = uni.getStorageSync('STAR_NET_SELECTED_HOLD_ORDER');
		if (holdOrder && holdOrder.id) {
			this.setCart(holdOrder.items || []);
			uni.removeStorageSync('STAR_NET_SELECTED_HOLD_ORDER');
		}
	},

	methods: {
		...mapMutations('m_cart', ['addToCart', 'updateGoodsCount', 'clearCart', 'setCart']),
		async scanCode() {
			try {
				const res = await uni.scanCode({
					onlyFromCamera: true,
					scanType: ['barCode', 'qrCode']
				});
				const code = res.result;
				this.searchAndAdd(code);
			} catch (error) {
				uni.showToast({ title: '扫码已取消', icon: 'none' });
			}
		},

		async searchAndAdd(keyword) {
			try {
				const data = await this.$api.productPage({
					query: { keyword },
					current: 1,
					size: 1
				});
				const item = (data.productPage && data.productPage.records && data.productPage.records[0]) || null;
				if (!item) throw new Error('未匹配到商品');
				this.addToCart({
					product_id: item.id,
					product_name: item.name || '',
					product_price: Number(
						item.estimatedPurchasePrice != null
							? item.estimatedPurchasePrice
							: item.wholesalePrice != null
								? item.wholesalePrice
								: 0
					),
					product_count: 1,
					cart_state: true
				});
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			}
		},

		updateCount(product_id, count) {
			this.updateGoodsCount({ product_id, product_count: count });
		},

		goSearch() {
			uni.navigateTo({ url: '/subpackages/business/search' });
		},

		goHoldList() {
			uni.navigateTo({ url: '/subpackages/business/hold-order' });
		},

		holdCurrentOrder() {
			if (!this.cart.length) {
				uni.showToast({ title: '购物车为空', icon: 'none' });
				return;
			}
			saveHoldOrder({
				createdAt: Date.now(),
				createdDate: formatDate(new Date()),
				amount: this.totalAmount,
				items: this.cart
			});
			this.clearCart();
			uni.showToast({ title: '挂单成功', icon: 'success' });
		},

		async payNow() {
			try {
				const codeData = await this.$api.saleCreateCode({ type: 'sell' });
				const saleCode = codeData.code;
				uni.showModal({
					title: '模拟支付',
					content: `应收 ¥${this.totalAmount}，确认支付成功？`,
					success: async ({ confirm }) => {
						if (!confirm) return;
						await this.submitSale(saleCode);
						uni.showToast({ title: '支付成功', icon: 'success' });
						this.clearCart();
					}
				});
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			}
		},

		async submitSale(code) {
			const hold = getHoldOrders();
			const warehouseId = (uni.getStorageSync('STAR_NET_COMPANY_SETTING') || {}).defaultWarehouseId || '';
			const sellerId = (uni.getStorageSync('STAR_NET_USER') || {}).employeeId || '';
			const customerId = (uni.getStorageSync('STAR_NET_COMPANY_SETTING') || {}).defaultCustomerId || '';
			if (!warehouseId || !sellerId || !customerId) {
				throw new Error('请先在公司设置中配置默认仓库/客户，并确保登录用户绑定职员');
			}
			const productList = this.cart.map((item) => ({
				productId: item.product_id,
				warehouseId,
				quantity: item.product_count,
				price: item.product_price,
				discountRate: 0,
				discountAmount: 0,
				amount: item.product_price * item.product_count
			}));

			await this.$api.saleSave({
				sale: {
					type: 'sell',
					code,
					issueDate: formatDate(new Date()),
					customerId,
					sellerId,
					quantity: this.cart.reduce((sum, item) => sum + item.product_count, 0),
					discountAmount: 0,
					amount: Number(this.totalAmount),
					preferentialRate: 0,
					preferentialAmount: 0,
					preferredAmount: Number(this.totalAmount),
					customerFee: 0,
					currentAmount: Number(this.totalAmount),
					debtAmount: 0,
					listerId: sellerId,
					remark: `小程序收银，挂单数量：${hold.length}`
				},
				productList,
				accountList: []
			});
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx 20rpx 170rpx;
}

.toolbar {
	display: flex;
	gap: 16rpx;
	flex-wrap: wrap;
	margin-bottom: 20rpx;
}

.cart-box {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
}

.row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.title {
	padding-bottom: 18rpx;
	border-bottom: 1px solid #f0f0f0;
}

.item {
	padding: 18rpx 0;
	border-bottom: 1px solid #f5f5f5;
}

.name {
	display: block;
	font-size: 28rpx;
}

.price {
	display: block;
	font-size: 24rpx;
	color: #ff5f2e;
	margin-top: 6rpx;
}

.clear {
	color: #888;
	font-size: 24rpx;
}

.empty {
	text-align: center;
	font-size: 24rpx;
	color: #999;
	padding: 40rpx 0;
}

.footer {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #fff;
	padding: 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 -6rpx 16rpx rgba(0, 0, 0, 0.06);
}

.total {
	font-size: 40rpx;
	color: #ff5f2e;
	font-weight: 600;
}
</style>
