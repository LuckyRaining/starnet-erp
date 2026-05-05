<!--
  商品行组件：商品列表 / 购物车里复用同一块 UI。
  - 列表页：只传 item，不传 showRadio / showNum（或传 false）→ 不显示勾选和数量步进器。
  - 购物车：:show-radio="true"、:show-num="true" → 左侧可勾选、右侧可改数量。
  - @click：整行点击（如跳详情）；@radio-change、@num-change：把操作交给父组件去改 Vuex。
-->
<template>
	<view class="card" @click="onCardClick">
		<view class="main-row">
			<!-- @click.stop：点 radio / 图区域时不冒泡，避免误触整行的 onCardClick -->
			<view class="left-zone" @click.stop>
				<radio
					v-if="showRadio"
					class="pick-radio"
					color="#C00000"
					:checked="radioChecked"
					@click.stop.prevent="onRadioTap"
				/>
				<image class="thumb" :src="thumbSrc" mode="aspectFill" />
			</view>

			<view class="mid">
				<text class="name">{{ displayName }}</text>
				<text class="sub">编码：{{ displayCode }}</text>
				<text class="sub">条码：{{ displayBarcode }}</text>
				<text v-if="showStockLine" class="sub">库存：{{ displayStock }}</text>
				<text class="price">¥{{ displayPriceText }}</text>
			</view>

			<!-- 数量变化由 uni-number-box 的 @change 抛出，再转成自定义事件 num-change 给购物车页 -->
			<view v-if="showNum" class="num-zone" @click.stop>
				<uni-number-box :min="1" :value="displayCount" @change="onNumChange" />
			</view>
		</view>
	</view>
</template>

<script>
/**
 * 本组件的 item 支持两种来源：
 * 1）接口商品：id、name、code、barcode、estimatedPurchasePrice、stock 等
 * 2）购物车行：product_id、product_name、product_price、product_count、cart_state
 * 下面用计算属性把两种字段“归一”成展示和事件里用的 goods_id、价格、数量等。
 */
export default {
	name: 'MyProductItem',

	props: {
		item: {
			type: Object,
			required: false,
			default: () => ({})
		},
		/** 为 true 时显示左侧 radio，用于购物车勾选 */
		showRadio: {
			type: Boolean,
			default: false
		},
		/** 为 true 时显示 uni-number-box，用于购物车改数量 */
		showNum: {
			type: Boolean,
			default: false
		}
	},

	emits: ['click', 'radio-change', 'num-change'],

	computed: {
		/** 事件里用 goods_id 命名与教材一致，值来自 product_id 或商品 id */
		goodsId() {
			const row = this.item;
			if (!row || typeof row !== 'object') return '';
			if (row.product_id != null) return row.product_id;
			if (row.id != null) return row.id;
			return '';
		},

		displayName() {
			const row = this.item;
			if (!row) return '';
			if (row.product_name != null && row.product_name !== '') return String(row.product_name);
			if (row.name != null) return String(row.name);
			return '';
		},

		displayCode() {
			const row = this.item;
			if (!row || row.code == null || row.code === '') return '-';
			return String(row.code);
		},

		displayBarcode() {
			const row = this.item;
			if (!row || row.barcode == null || row.barcode === '') return '-';
			return String(row.barcode);
		},

		displayStock() {
			const row = this.item;
			if (!row || row.stock == null) return 0;
			return row.stock;
		},

		/** 购物车行一般没有 stock 字段；列表行有 stock 且未开 radio 时显示库存一行 */
		showStockLine() {
			return this.item && this.item.stock != null && !this.showRadio;
		},

		/** 购物车用 product_price；列表用 estimatedPurchasePrice */
		displayPriceVal() {
			const row = this.item;
			if (!row) return 0;
			const p =
				row.product_price != null && row.product_price !== ''
					? row.product_price
					: row.estimatedPurchasePrice;
			const n = Number(p);
			return Number.isFinite(n) ? n : 0;
		},

		displayPriceText() {
			return this.displayPriceVal.toFixed(2);
		},

		displayCount() {
			const row = this.item;
			if (!row || row.product_count == null) return 1;
			const n = Number(row.product_count);
			return Number.isFinite(n) && n >= 1 ? n : 1;
		},

		/** cart_state 为 false 表示未勾选；缺省按 true 处理 */
		radioChecked() {
			return this.item && this.item.cart_state !== false;
		},

		thumbSrc() {
			const row = this.item;
			const url = row && (row.coverImg || row.image || row.picUrl);
			return url ? String(url) : '/static/uni-logo.png';
		}
	},

	methods: {
		/** 列表页：父组件监听 @click 去打开详情等 */
		onCardClick() {
			this.$emit('click', this.item);
		},

		/** 只负责“通知父组件要改成什么状态”，真正写入 Vuex 在 cart 页的 onRadioChange */
		onRadioTap() {
			if (!this.showRadio || !this.goodsId) return;
			const next = !this.radioChecked;
			this.$emit('radio-change', {
				goods_id: this.goodsId,
				cart_state: next
			});
		},

		/** 父组件用 goods_id + goods_count 调用 updateGoodsCount Mutation */
		onNumChange(val) {
			const id = this.goodsId;
			if (!id) return;
			const n = Number(val);
			const goods_count = Number.isFinite(n) && n >= 1 ? n : 1;
			this.$emit('num-change', { goods_id: id, goods_count });
		}
	}
};
</script>

<style lang="scss" scoped>
.card {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
	margin-bottom: 16rpx;
}

.main-row {
	display: flex;
	flex-direction: row;
	align-items: stretch;
}

.left-zone {
	display: flex;
	flex-direction: row;
	align-items: center;
	flex-shrink: 0;
	margin-right: 16rpx;
}

.pick-radio {
	transform: scale(0.82);
	margin-right: 8rpx;
}

.thumb {
	width: 140rpx;
	height: 140rpx;
	margin-right: 10rpx;
	border-radius: 12rpx;
	background: #f5f5f5;
	flex-shrink: 0;
}

.mid {
	flex: 1;
	min-width: 0;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.name {
	font-size: 30rpx;
	color: #222;
	font-weight: 600;
	line-height: 1.35;
}

.sub {
	margin-top: 8rpx;
	font-size: 24rpx;
	color: #888;
	line-height: 1.3;
}

.price {
	margin-top: 12rpx;
	font-size: 30rpx;
	color: #ff5f2e;
	font-weight: 600;
}

.num-zone {
	flex-shrink: 0;
	align-self: flex-end;
	margin-left: 12rpx;
	padding-top: 8rpx;
}
</style>
