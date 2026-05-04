<template>
	<view class="page">
		<view class="panel">
			<uni-datetime-picker type="daterange" v-model="dateRange" @change="loadSummary" />
		</view>

		<sales-echart :option="chartOption" />

		<view class="panel mt">
			<view class="header">销售排行</view>
			<view v-for="item in summaryList" :key="item.productId + item.warehouseId" class="row">
				<text>{{ item.productName }}</text>
				<text>¥{{ item.amount || 0 }}</text>
			</view>
			<view v-if="!summaryList.length" class="empty">暂无统计数据</view>
		</view>
	</view>
</template>

<script>
// import SalesEchart from '@/components/sales-echart.vue';

export default {
	// components: {
	// 	SalesEchart
	// },

	data() {
		return {
			dateRange: [],
			summaryList: []
		};
	},

	computed: {
		chartOption() {
			return {
				tooltip: {},

				xAxis: {
					type: 'category',
					data: this.summaryList.map((item) => item.productName)
				},

				yAxis: {
					type: 'value'
				},

				series: [
					{
						type: 'bar',
						data: this.summaryList.map((item) => Number(item.amount || 0)),
						itemStyle: {
							color: '#2f7df6'
						}
					}
				]
			};
		}
	},

	onShow() {
		this.loadSummary();
	},

	methods: {
		async loadSummary() {
			try {
				const data = await this.$api.analysisSaleProductSummaryList({
					startDate: this.dateRange[0] || '',
					endDate: this.dateRange[1] || ''
				});
				this.summaryList = (data.summaryList || []).slice(0, 8);
			} catch (error) {
				uni.$showMsg(error.message || '不存在该商品');
			}
		}
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
}

.panel {
	background: #fff;
	border-radius: 16rpx;
	padding: 20rpx;
}

.mt {
	margin-top: 20rpx;
}

.header {
	font-size: 30rpx;
	font-weight: 600;
	margin-bottom: 16rpx;
}

.row {
	display: flex;
	justify-content: space-between;
	padding: 14rpx 0;
	border-bottom: 1px solid #f3f3f3;
	font-size: 26rpx;
}

.empty {
	text-align: center;
	color: #999;
	font-size: 24rpx;
	padding-top: 20rpx;
}
</style>
