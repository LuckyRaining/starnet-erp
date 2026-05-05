<!--
  采购待支付：从购物车勾选行跳转至此，选择收货仓库、供应商、结算账户后提交 purchase/save（type=buy）；制单人 ID 选填。
-->
<template>
	<view class="page">
		<view class="section">
			<text class="section-title">采购明细（{{ lines.length }} 行）</text>
			<view v-for="item in lines" :key="item.product_id" class="line">
				<text class="line-name">{{ item.product_name || '商品' }}</text>
				<text class="line-meta">×{{ item.product_count }}　¥{{ lineAmount(item) }}</text>
			</view>
		</view>

		<view class="section">
			<text class="section-title">业务信息</text>
			<view class="cell" @click="goWarehouse">
				<text class="label">收货仓库</text>
				<view class="cell-right">
					<text class="value" :class="{ placeholder: !warehouseName }">{{ warehouseName || '请选择' }}</text>
					<uni-icons type="right" size="16" color="#999" />
				</view>
			</view>
			<view class="cell" @click="goSupplierPick">
				<text class="label">供应商</text>
				<view class="cell-right">
					<text class="value" :class="{ placeholder: !supplierName }">{{ supplierName || '请选择' }}</text>
					<uni-icons type="right" size="16" color="#999" />
				</view>
			</view>
			<view class="cell" @click="goAccountPick">
				<text class="label">结算账户</text>
				<view class="cell-right">
					<text class="value" :class="{ placeholder: !accountName }">{{ accountName || '请选择' }}</text>
					<uni-icons type="right" size="16" color="#999" />
				</view>
			</view>
			<view class="field-block">
				<text class="field-label">制单人职员 ID（选填）</text>
				<uni-easyinput v-model="listerId" placeholder="不填则留空；可在公司设置里配置默认" />
			</view>
		</view>

		<view class="sum-card">
			<text class="sum-label">应付合计</text>
			<text class="sum-num">¥{{ totalAmountText }}</text>
			<text class="sum-hint">提交后将生成购货单（采购入库），并记一笔结算账户支出</text>
		</view>

		<button class="submit" type="warn" :loading="submitting" @click="confirmPay">确认采购并支付</button>
	</view>
</template>

<script>
import { mapState } from 'vuex';
import { formatDate, formatMoney } from '@/utils/format';
import CONFIG from '@/utils/config';

export default {
	data() {
		return {
			lines: [],
			supplier: null,
			settlementAccount: null,
			listerId: '',
			submitting: false
		};
	},

	computed: {
		...mapState('m_user', ['warehouse']),

		warehouseName() {
			return (this.warehouse && this.warehouse.name) || '';
		},

		supplierName() {
			return (this.supplier && this.supplier.name) || '';
		},

		accountName() {
			return (this.settlementAccount && this.settlementAccount.name) || '';
		},

		totalAmountText() {
			const sum = this.lines.reduce((s, item) => {
				const price = Number(item.product_price) || 0;
				const qty = Number(item.product_count) || 0;
				return s + price * qty;
			}, 0);
			return formatMoney(sum);
		}
	},

	onLoad() {
		const draft = uni.getStorageSync(CONFIG.PURCHASE_CHECKOUT_KEY);
		if (!draft || !Array.isArray(draft.lines) || !draft.lines.length) {
			uni.showToast({ title: '没有待结算商品', icon: 'none' });
			setTimeout(() => uni.navigateBack(), 1200);
			return;
		}
		this.lines = draft.lines;
	},

	async onShow() {
		const sup = uni.getStorageSync(CONFIG.CHECKOUT_RETURN_SUPPLIER_KEY);
		if (sup && sup.id) {
			this.supplier = sup;
			uni.removeStorageSync(CONFIG.CHECKOUT_RETURN_SUPPLIER_KEY);
		}
		const acc = uni.getStorageSync(CONFIG.CHECKOUT_RETURN_ACCOUNT_KEY);
		if (acc && acc.id) {
			this.settlementAccount = acc;
			uni.removeStorageSync(CONFIG.CHECKOUT_RETURN_ACCOUNT_KEY);
		}
		await this.fillDefaultsFromCompany();
	},

	methods: {
		lineAmount(item) {
			const price = Number(item.product_price) || 0;
			const qty = Number(item.product_count) || 0;
			return formatMoney(price * qty);
		},

		goWarehouse() {
			uni.navigateTo({ url: '/subpackages/business/warehouse' });
		},

		goSupplierPick() {
			uni.navigateTo({ url: '/subpackages/business/supplier-pick' });
		},

		goAccountPick() {
			uni.navigateTo({ url: '/subpackages/business/settlement-account-pick' });
		},

		async fillDefaultsFromCompany() {
			const c = uni.getStorageSync('STAR_NET_COMPANY_SETTING') || {};
			if ((!this.supplier || !this.supplier.id) && c.defaultSupplierId) {
				try {
					const data = await this.$api.supplierDetail({
						supplierId: String(c.defaultSupplierId).trim()
					});
					const s = data && data.supplier;
					if (s && s.id) {
						this.supplier = { id: s.id, name: s.name || '', code: s.code || '' };
					}
				} catch (e) {
					/* 默认 ID 无效时忽略 */
				}
			}
			if ((!this.settlementAccount || !this.settlementAccount.id) && c.defaultSettlementAccountId) {
				try {
					const data = await this.$api.settlementAccountDetail({
						accountId: String(c.defaultSettlementAccountId).trim()
					});
					const a = data && data.account;
					if (a && a.id) {
						this.settlementAccount = { id: a.id, name: a.name || '', code: a.code || '' };
					}
				} catch (e) {
					/* ignore */
				}
			}
			if (!String(this.listerId || '').trim() && c.defaultListerId) {
				this.listerId = String(c.defaultListerId).trim();
			}
		},

		validate() {
			const warehouseId = this.warehouse && this.warehouse.id;
			if (!warehouseId) {
				uni.showToast({ title: '请选择收货仓库', icon: 'none' });
				return false;
			}
			if (!this.supplier || !this.supplier.id) {
				uni.showToast({ title: '请选择供应商', icon: 'none' });
				return false;
			}
			if (!this.settlementAccount || !this.settlementAccount.id) {
				uni.showToast({ title: '请选择结算账户', icon: 'none' });
				return false;
			}
			return true;
		},

		async confirmPay() {
			if (!this.validate()) return;

			const warehouseId = this.warehouse.id;
			const preferredAmount = Number(this.totalAmountText);
			const qtySum = this.lines.reduce((sum, item) => sum + (Number(item.product_count) || 0), 0);

			this.submitting = true;
			try {
				const codeData = await this.$api.purchaseCreateCode({});
				const code = codeData.code;
				if (!code) throw new Error('未生成采购单号');

				await this.$api.purchaseSave({
					purchase: {
						type: 'buy',
						code,
						issueDate: formatDate(new Date()),
						supplierId: this.supplier.id,
						quantity: qtySum,
						discountAmount: 0,
						amount: preferredAmount,
						preferentialRate: 0,
						preferentialAmount: 0,
						preferredAmount,
						currentAmount: preferredAmount,
						debtAmount: 0,
						contracts: '[]',
						listerId: String(this.listerId || '').trim(),
						remark: '小程序采购入库'
					},
					productList: this.lines.map((item) => ({
						code: '',
						productId: item.product_id,
						warehouseId,
						quantity: Number(item.product_count) || 0,
						price: Number(item.product_price) || 0,
						discountRate: 0,
						discountAmount: 0,
						amount:
							(Number(item.product_price) || 0) * (Number(item.product_count) || 0),
						remark: ''
					})),
					accountList: [
						{
							accountId: this.settlementAccount.id,
							remark: ''
						}
					]
				});

				uni.removeStorageSync(CONFIG.PURCHASE_CHECKOUT_KEY);
				const ids = this.lines.map((row) => row.product_id).filter((id) => id != null);
				uni.setStorageSync(CONFIG.PURCHASE_DONE_IDS_KEY, ids);

				uni.showToast({ title: '采购提交成功', icon: 'success' });
				setTimeout(() => uni.navigateBack(), 1200);
			} catch (e) {
				uni.showToast({ title: e.message || '提交失败', icon: 'none' });
			} finally {
				this.submitting = false;
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f8f8f8;
	padding: 20rpx 20rpx 80rpx;
	box-sizing: border-box;
}

.section {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx 24rpx;
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
	display: block;
	margin-bottom: 16rpx;
}

.line {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	padding: 12rpx 0;
	border-bottom: 1px solid #f2f2f2;
}

.line:last-child {
	border-bottom: none;
}

.line-name {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	padding-right: 16rpx;
}

.line-meta {
	font-size: 26rpx;
	color: #666;
	flex-shrink: 0;
}

.cell {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	min-height: 88rpx;
	border-bottom: 1px solid #f2f2f2;
}

.cell:last-of-type {
	border-bottom: none;
}

.label {
	font-size: 28rpx;
	color: #333;
}

.cell-right {
	display: flex;
	flex-direction: row;
	align-items: center;
	gap: 8rpx;
	max-width: 70%;
}

.value {
	font-size: 28rpx;
	color: #333;
	text-align: right;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.value.placeholder {
	color: #999;
}

.field-block {
	margin-top: 16rpx;
}

.field-label {
	font-size: 26rpx;
	color: #666;
	display: block;
	margin-bottom: 12rpx;
}

.sum-card {
	background: linear-gradient(135deg, #fff8f2, #ffffff);
	border-radius: 16rpx;
	padding: 28rpx 24rpx;
	margin-bottom: 28rpx;
	border: 1px solid #ffe4cc;
}

.sum-label {
	font-size: 26rpx;
	color: #888;
}

.sum-num {
	display: block;
	font-size: 44rpx;
	font-weight: 700;
	color: #c00000;
	margin-top: 8rpx;
}

.sum-hint {
	display: block;
	font-size: 22rpx;
	color: #aaa;
	margin-top: 12rpx;
	line-height: 1.4;
}

.submit {
	width: 100%;
	border-radius: 12rpx;
	font-size: 32rpx;
}
</style>
