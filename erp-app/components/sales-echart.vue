<template>
	<view class="chart-wrap">
		<!-- #ifdef H5 -->
		<view id="sales-echart" class="chart" :prop="optionText" :change:prop="echartRenderer.render"></view>
		<!-- #endif -->

		<!-- #ifndef H5 -->
		<view class="fallback">
			<view v-for="item in chartData" :key="item.name" class="bar-row">
				<text class="label">{{ item.name }}</text>

				<view class="bar-bg">
					<view class="bar" :style="{ width: `${item.percent}%` }"></view>
				</view>

				<text class="value">{{ item.value }}</text>
			</view>
		</view>
		<!-- #endif -->
	</view>
</template>

<script>
export default {
	name: 'SalesEchart',

	props: {
		option: {
			type: Object,
			default: () => ({})
		}
	},

	computed: {
		optionText() {
			return JSON.stringify(this.option || {});
		},

		chartData() {
			const xAxis = (this.option.xAxis && this.option.xAxis.data) || [];
			const values = (this.option.series && this.option.series[0] && this.option.series[0].data) || [];
			const max = Math.max(...values, 1);
			return xAxis.map((name, i) => ({
				name,
				value: values[i] || 0,
				percent: Math.round(((values[i] || 0) / max) * 100)
			}));
		}
	}
};
</script>

<script module="echartRenderer" lang="renderjs">
let chart;

function loadEcharts() {
	return new Promise((resolve, reject) => {
		if (window.echarts) return resolve(window.echarts);
		const script = document.createElement("script");
		script.src = "https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js";
		script.onload = () => resolve(window.echarts);
		script.onerror = reject;
		document.head.appendChild(script);
	});
}

export default {
	methods: {
		async render(optionText) {
			const echarts = await loadEcharts();
			if (!chart) {
				chart = echarts.init(document.getElementById("sales-echart"));
				window.addEventListener("resize", () => chart && chart.resize());
			}
			const option = JSON.parse(optionText || "{}");
			chart.setOption(option);
		}
	}
}
</script>

<style lang="scss">
.chart-wrap {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
}

.chart {
	width: 100%;
	height: 420rpx;
}

.fallback {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.bar-row {
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.label {
	width: 120rpx;
	font-size: 24rpx;
	color: #666;
}

.bar-bg {
	flex: 1;
	height: 16rpx;
	background: #f2f5fb;
	border-radius: 999rpx;
	overflow: hidden;
}

.bar {
	height: 100%;
	background: linear-gradient(90deg, #2f7df6, #26c2a7);
}

.value {
	width: 90rpx;
	font-size: 24rpx;
	text-align: right;
	color: #333;
}
</style>
