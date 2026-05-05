<template>
	<!-- 模拟搜索条：无真实 input；父页面 @click 跳转搜索、@scan 扫码 -->
	<view class="my-search-row">
		<view class="my-search-container" :style="containerStyle" @click.stop="onSearchClick">
			<view class="my-search-box" :style="boxStyle">
				<uni-icons type="search" size="18" color="#c0c4cc" />
				<text class="my-search-placeholder">{{ placeholder }}</text>
			</view>
		</view>

		<view class="my-search-scan" @click.stop="onScanClick">
			<uni-icons type="scan" size="26" color="#333" />
		</view>
	</view>
</template>

<script>
/**
 * 分类页顶部「搜索 + 扫码」条。
 * 样式：bgcolor / radius 通过内联 style 绑定到容器与内层盒子，便于复用时换肤。
 */
export default {
	name: 'MySearch',

	emits: ['click', 'scan'],

	props: {
		placeholder: {
			type: String,
			default: '按名称/编码/条码搜索商品'
		},
		bgcolor: {
			type: String,
			default: '#F8F8F8'
		},
		radius: {
			type: [String, Number],
			default: '36rpx'
		}
	},

	computed: {
		// radius 若为数字，则按 px 拼接；若为字符串（如 36rpx），则原样用于 border-radius
		radiusCss() {
			return typeof this.radius === 'number' ? `${this.radius}rpx` : this.radius;
		},
		containerStyle() {
			return {
				backgroundColor: this.bgcolor,
				borderRadius: this.radiusCss
			};
		},
		boxStyle() {
			return {
				// borderRadius: this.radiusCss
			};
		}
	},

	methods: {
		onSearchClick() {
			this.$emit('click'); // 将组件事件 透传给父页面，让父页面处理 点击事件
		},
		onScanClick() {
			this.$emit('scan'); // 将组件事件 透传给父页面，让父页面处理 扫码事件
		}
	}
};
</script>

<style lang="scss" scoped>
/* 组件可视高度：与右侧扫码按钮对齐 */
$my-search-height: 72rpx;

.my-search-row {
	margin-bottom: 14rpx;
	display: flex;
	flex-direction: row;
	align-items: center;
	height: $my-search-height;
	box-sizing: content-box;
}

.my-search-container {
	flex: 1;
	height: $my-search-height;
	margin: auto;
	margin-left: 16rpx;
	box-sizing: border-box;
	padding: 0 24rpx;
	display: flex;
	align-items: center;
	overflow: hidden;
}

.my-search-box {
	flex: 1;
	height: 100%;
	display: flex;
	flex-direction: row;
	align-items: center;
	// padding-left: 8rpx;
	box-sizing: border-box;
}

.my-search-placeholder {
	flex: 1;
	margin-left: 12rpx;
	font-size: 28rpx;
	color: #b3b3b3;
}

.my-search-scan {
	width: 72rpx;
	height: $my-search-height;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-left: 16rpx;
	margin-right: 10rpx;
}
</style>
