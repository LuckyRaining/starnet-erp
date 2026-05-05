<!--
  底部固定结算条 + 悬浮「取单」：
  - 购物车无商品时隐藏结算条，仍保留「取单」；
  - 展示数据来自 Vuex getters（已选件数、合计金额、全部件数 total）；
  - 全选：调用 Mutation updateAllProductsState；
  - 结算：先校验「已勾选商品 / 登录 token」；仓库与供应商在采购待支付页选择，通过后 $emit('settle')；
  - 挂单 / 取单：不在这里写业务，只 $emit 给 cart.vue 执行。
-->
<template>
	<view class="settle-wrap">
		<view class="float-pick" :class="{ 'float-pick--solo': !showSettleBar }" @click="onPick">
			<text class="float-pick-text">取单</text>
		</view>

		<view v-if="showSettleBar" class="settle-bar safe-bottom">
			<label class="all-row" @click="changeAllState">
				<radio color="#C00000" :checked="isFullCheck" />
				<text class="all-text">全选</text>
			</label>

			<view class="sum-block">
				<view class="sum-inner">
					<text class="pieces">已选 {{ checkedCount }} 件</text>
					<view class="sum-line">
						<text class="sum-label">合计：</text>
						<text class="sum-num">¥{{ amountText }}</text>
					</view>
				</view>
			</view>

			<button class="btn hold" size="mini" @click="onHold">挂单</button>
			<button class="btn pay" type="warn" size="mini" @click="onSettleClick">结算</button>
		</view>

	</view>
</template>

<script>
import { mapGetters, mapMutations, mapState } from 'vuex';
import { formatMoney } from '@/utils/format';

export default {
	name: 'MySettle',

	data() {
		return {
			seconds: 3,
			timer: null
		};
	},

	computed: {
		...mapState('m_cart', ['cart']),
		...mapState('m_user', ['token']),

		/** 有商品行时才展示结算条；空车时仍展示「取单」悬浮钮 */
		showSettleBar() {
			return Array.isArray(this.cart) && this.cart.length > 0;
		},

		/** total：购物车里所有行的数量之和（含未勾选），用来判断是否「全选」 */
		...mapGetters('m_cart', ['total', 'checkedCount', 'checkedGoodsAmount']),

		/** 全选：有商品且「已勾选件数 === 总件数」——未勾选任何一行时 checkedCount 会小于 total */
		isFullCheck() {
			const t = Number(this.total) || 0;
			const c = Number(this.checkedCount) || 0;
			return t > 0 && c === t;
		},

		amountText() {
			return formatMoney(this.checkedGoodsAmount);
		}
	},

	beforeDestroy() {
		this.clearDelayTimer();
	},

	beforeUnmount() {
		this.clearDelayTimer();
	},

	methods: {
		...mapMutations('m_cart', ['updateAllProductsState']),
		...mapMutations('m_user', ['updateRedirectInfo']),

		/** 当前已全选则全部取消，否则全部勾选 */
		changeAllState() {
			this.updateAllProductsState(!this.isFullCheck);
		},

		onHold() {
			this.$emit('hold');
		},

		onPick() {
			this.$emit('pick');
		},

		/** 结算：勾选商品 → 登录；仓库/供应商/结算账户在采购待支付页处理 */
		onSettleClick() {
			if (!this.showSettleBar) return;

			const checked = Number(this.checkedCount) || 0;
			if (checked <= 0) {
				uni.showToast({ title: '请先勾选要结算的商品', icon: 'none' });
				return;
			}

			const t = this.token != null ? String(this.token).trim() : '';
			if (!t) {
				this.delayNavigate();
				return;
			}

			this.$emit('settle');
		},

		showTips() {
			if (this.seconds <= 0) return;
			uni.showToast({
				title: `请先登录，${this.seconds}秒后跳转`,
				icon: 'none',
				duration: 1000
			});
		},

		clearDelayTimer() {
			if (this.timer != null) {
				clearInterval(this.timer);
				this.timer = null;
			}
		},

		delayNavigate() {
			this.clearDelayTimer();
			this.seconds = 3;
			this.showTips();
			this.timer = setInterval(() => {
				this.seconds--;
				if (this.seconds <= 0) {
					this.clearDelayTimer();
					this.updateRedirectInfo({
						openType: 'switchTab',
						from: '/pages/cart/cart'
					});
					uni.switchTab({ url: '/pages/mine/mine' });
					return;
				}
				this.showTips();
			}, 1000);
		}
	}
};
</script>

<style lang="scss" scoped>
/* 整块不接点击，避免挡住上方列表；需要点的子元素单独 pointer-events: auto */
.settle-wrap {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 100;
	pointer-events: none;
}

.float-pick {
	pointer-events: auto;
	position: absolute;
	right: 32rpx;
	/* 贴在结算条上方，整体更靠近原生 tabBar */
	bottom: calc(108rpx + constant(safe-area-inset-bottom));
	bottom: calc(108rpx + env(safe-area-inset-bottom));
	width: 100rpx;
	height: 100rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #ff9f43, #ff5f2e);
	box-shadow: 0 8rpx 24rpx rgba(255, 95, 46, 0.45);
	display: flex;
	align-items: center;
	justify-content: center;
}

/* 无结算条：贴页面内容区底边，仅留出安全区，靠近 tabBar */
.float-pick--solo {
	bottom: calc(20rpx + constant(safe-area-inset-bottom));
	bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.float-pick-text {
	font-size: 26rpx;
	color: #fff;
	font-weight: 600;
}

.settle-bar {
	pointer-events: auto;
	display: flex;
	flex-direction: row;
	align-items: center;
	background: #fff;
	padding: 12rpx 20rpx;
	box-shadow: 0 -6rpx 16rpx rgba(0, 0, 0, 0.06);
	gap: 12rpx;
}

.safe-bottom {
	padding-bottom: calc(6rpx + constant(safe-area-inset-bottom));
	padding-bottom: calc(6rpx + env(safe-area-inset-bottom));
}

.all-row {
	display: flex;
	flex-direction: row;
	align-items: center;
	flex-shrink: 0;
}

.all-text {
	margin-left: 8rpx;
	font-size: 26rpx;
	color: #333;
}

.sum-block {
	flex: 1;
	min-width: 0;
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: flex-end;
	margin-right: 8rpx;
}

.sum-inner {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
}

.pieces {
	font-size: 22rpx;
	color: #888;
	margin-bottom: 4rpx;
}

.sum-line {
	display: flex;
	flex-direction: row;
	align-items: baseline;
}

.sum-label {
	font-size: 24rpx;
	color: #666;
}

.sum-num {
	font-size: 34rpx;
	color: #ff5f2e;
	font-weight: 700;
	margin-left: 6rpx;
}

.btn {
	flex-shrink: 0;
	margin: 0;
	font-size: 26rpx;
}

.btn.hold {
	background: #f0f0f0;
	color: #333;
}

.btn.pay {
	min-width: 140rpx;
}
</style>
