<!--
  收银页（购物车）：自上而下
  1）扫码/搜索 → 加商品到 Vuex m_cart；
  2）my-warehouse → 选收货仓库（Vuex m_user）；
  3）商品列表 → mapState 的 cart + my-product-item；左滑 uni-swipe-action 删除；
  4）my-settle → 全选/合计/挂单/结算（进采购待支付页）/取单。
-->
<template>
	<view class="page">
		<!-- 支持 搜索 和 扫码 功能 -->
		<my-search placeholder="按名称/编码/条码搜索商品" :bgcolor="'#FFFFFF'" :radius="'36rpx'" @click="goSearch" @scan="onScan" />

		<!-- 收货仓库：内部已 mapState('m_user') -->
		<my-warehouse />

		<!-- 商品列表：cart 来自 mapState('m_cart') -->
		<view v-if="cart.length" class="cart-box">
			<view class="row title">
				<text class="title-text">商品列表（{{ cart.length }}）</text>
				<text class="clear" @click="clearCart">清空</text>
			</view>

			<!-- 每条一行外包一层 swipe：options 定义右侧「删除」按钮样式与文案 -->
			<uni-swipe-action>
				<uni-swipe-action-item
					v-for="item in cart"
					:key="item.product_id"
					:right-options="options"
					@click="onSwipeClick($event, item)"
				>
					<my-product-item
						:item="item"
						:show-radio="true"
						:show-num="true"
						@radio-change="onRadioChange"
						@num-change="onNumChange"
					/>
				</uni-swipe-action-item>
			</uni-swipe-action>
		</view>

		<!-- 购物车为空 -->
		<view v-else class="empty-wrap">
			<image class="empty-img" src="/static/tabbar_icons/cart.png" mode="aspectFit" />
			<text class="empty-title">购物车还是空的</text>
			<text class="empty-sub">请扫码或搜索商品后加入采购清单</text>
			<view class="empty-actions">
				<button type="primary" size="mini" @click="scanCode">扫码加购</button>
				<button size="mini" @click="goSearch">搜索商品</button>
			</view>
		</view>

		<!-- 底栏：无商品时组件仍挂载，仅隐藏结算条，保留「取单」 -->
		<my-settle @hold="holdCurrentOrder" @settle="payNow" @pick="goHoldList" />
	</view>
</template>

<script>
/**
 * mapState：读 cart
 * mapMutations：改购物车（加减数量、勾选、删除、清空等）
 * mapGetters：读「已选合计」等（结算弹窗里用 checkedGoodsAmount）
 * tabbarBadge：混入里监听 total，刷新底部购物车 tab 角标
 */
import { mapState, mapMutations, mapGetters } from 'vuex';
import { formatDate, formatMoney } from '@/utils/format';
import { saveHoldOrder } from '@/utils/hold';
import tabbarBadge from '@/mixins/tabbar-badge.js';
import CONFIG from '@/utils/config';
import { addPendingPurchaseDraft } from '@/utils/pending-pay-purchase';
import MyWarehouse from '@/components/my-warehouse/my-warehouse.vue';
import MySettle from '@/components/my-settle/my-settle.vue';
import MyProductItem from '@/components/my-product-item/my-product-item.vue';

export default {
	components: {
		MyWarehouse,
		MySettle,
		MyProductItem
	},

	mixins: [tabbarBadge],

	data() {
		return {
			/** uni-swipe-action-item 右侧按钮配置 */
			options: [
				{
					text: '删除',
					style: {
						backgroundColor: '#C00000'
					}
				}
			]
		};
	},

	computed: {
		...mapState('m_cart', ['cart']),
		...mapGetters('m_cart', ['checkedCount', 'checkedGoodsAmount']),

		/** 挂单快照总金额（全部行） */
		holdSheetAmount() {
			return formatMoney(
				this.cart.reduce((sum, item) => sum + Number(item.product_price || 0) * Number(item.product_count || 0), 0)
			);
		}
	},

	onShow() {
		// 从详情等页「带一件商品回收银」：读本地缓存后 addToCart，再清缓存避免重复加
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

		// 从挂单列表「取单」：整单替换当前购物车
		const holdOrder = uni.getStorageSync('STAR_NET_SELECTED_HOLD_ORDER');
		if (holdOrder && holdOrder.id) {
			this.setCart(holdOrder.items || []);
			uni.removeStorageSync('STAR_NET_SELECTED_HOLD_ORDER');
		}

		const doneIds = uni.getStorageSync(CONFIG.PURCHASE_DONE_IDS_KEY);
		if (Array.isArray(doneIds) && doneIds.length) {
			doneIds.forEach((id) => this.removeGoodsById(id));
			uni.removeStorageSync(CONFIG.PURCHASE_DONE_IDS_KEY);
		}
	},

	methods: {
		...mapMutations('m_cart', [
			'addToCart',
			'updateGoodsCount',
			'updateGoodsState',
			'removeGoodsById',
			'clearCart',
			'setCart'
		]),

		/** 子组件用 goods_id，Vuex 里字段名是 product_id */
		onRadioChange(e) {
			const id = e && e.goods_id;
			if (id == null || id === '') return;
			this.updateGoodsState({
				product_id: id,
				cart_state: !!e.cart_state
			});
		},

		/** goods_count → Mutation updateGoodsCount 的 product_count */
		onNumChange(e) {
			const id = e && e.goods_id;
			if (id == null || id === '') return;
			this.updateGoodsCount({
				product_id: id,
				product_count: e.goods_count
			});
		},

		/** uni-swipe-action-item 点击右侧按钮时触发，e.content 对应 options 里的一项 */
		onSwipeClick(e, item) {
			const content = e && e.content;
			if (content && content.text === '删除' && item && item.product_id != null) {
				this.removeGoodsById(item.product_id);
			}
		},

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
				const row =
					(data.productPage && data.productPage.records && data.productPage.records[0]) || null;
				if (!row) throw new Error('未匹配到商品');
				this.addToCart({
					product_id: row.id,
					product_name: row.name || '',
					product_price: Number(
						row.estimatedPurchasePrice != null
							? row.estimatedPurchasePrice
							: row.wholesalePrice != null
								? row.wholesalePrice
								: 0
					),
					product_count: 1,
					cart_state: true
				});
			} catch (error) {
				uni.showToast({ title: error.message, icon: 'none' });
			}
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
				amount: this.holdSheetAmount,
				items: this.cart
			});
			this.clearCart();
			uni.showToast({ title: '挂单成功', icon: 'success' });
		},

		payNow() {
			const lines = this.cart.filter((item) => item.cart_state !== false);
			if (!lines.length) {
				uni.showToast({ title: '请先勾选要结算的商品', icon: 'none' });
				return;
			}

			const snapshot = lines.map((row) => ({
				product_id: row.product_id,
				product_name: row.product_name,
				product_price: row.product_price,
				product_count: row.product_count,
				cart_state: row.cart_state
			}));

			const pendingId = addPendingPurchaseDraft({
				lines: snapshot,
				checkedCount: this.checkedCount,
				checkedGoodsAmount: this.checkedGoodsAmount
			});

			uni.navigateTo({
				url: `/subpackages/business/purchase-checkout?pendingId=${encodeURIComponent(pendingId)}`
			});
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 0rpx 20rpx 280rpx;
	min-height: 100vh;
	box-sizing: border-box;
	background: #f8f8f8;
}

.toolbar {
	display: flex;
	gap: 16rpx;
	flex-wrap: wrap;
	margin-bottom: 16rpx;
}

.cart-box {
	background: transparent;
}

.row.title {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 8rpx 4rpx 18rpx;
}

.title-text {
	font-size: 28rpx;
	font-weight: 600;
	color: #333;
}

.clear {
	color: #888;
	font-size: 24rpx;
}

.empty-wrap {
	background: #fff;
	border-radius: 16rpx;
	padding: 80rpx 40rpx 100rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.empty-img {
	width: 160rpx;
	height: 160rpx;
	opacity: 0.35;
	margin-bottom: 24rpx;
}

.empty-title {
	font-size: 32rpx;
	color: #333;
	font-weight: 600;
}

.empty-sub {
	margin-top: 12rpx;
	font-size: 26rpx;
	color: #999;
	text-align: center;
}

.empty-actions {
	margin-top: 40rpx;
	display: flex;
	gap: 24rpx;
	flex-wrap: wrap;
	justify-content: center;
}
</style>
