<template>
	<scroll-view class="page" scroll-y :enhanced="true">
		<view class="panel date-panel">
			<text class="panel-title">统计时间段</text>
			<view class="date-grid">
				<view class="date-cell">
					<text class="label">开始日期</text>
					<uni-datetime-picker type="date" return-type="string" v-model="startDate" @change="onRangeChange" />
				</view>
				<view class="date-cell">
					<text class="label">结束日期</text>
					<uni-datetime-picker type="date" return-type="string" v-model="endDate" @change="onRangeChange" />
				</view>
			</view>
			<button class="refresh" type="primary" size="mini" :loading="loading" @click="loadAll">刷新数据</button>
			<text class="hint">以下图表数据均来自后端 /analysis/ 接口；H5 使用 ECharts 渲染，小程序端为条形示意图。</text>
		</view>

		<!-- 采购分析 -->
		<uni-section class="chart-card-title" title="采购分析" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.purchaseBySupplier" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.purchaseByProduct" />
		</view>

		<!-- 销售分析 -->
		<uni-section class="chart-card-title" title="销售分析" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.saleByProduct" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.saleByCustomer" />
		</view>

		<!-- 库存分析 -->
		<uni-section class="chart-card-title" title="库存分析" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.stockInboundQty" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.stockBalanceAmt" />
		</view>

		<!-- 资金分析 -->
		<uni-section class="chart-card-title" title="资金分析（结算账户）" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="360rpx" :option="opts.fundIncome" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="360rpx" :option="opts.fundExpense" />
		</view>

		<!-- 应收应付 -->
		<uni-section class="chart-card-title" title="应收 / 应付分析" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.receivableByCustomer" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="380rpx" :option="opts.payableBySupplier" />
		</view>

		<!-- 其他收支 -->
		<uni-section class="chart-card-title" title="其他收支分析" type="line" padding="0 20rpx" />
		<view class="chart-card">
			<sales-echart chart-height="360rpx" :option="opts.flowIncome" />
		</view>
		<view class="chart-card">
			<sales-echart chart-height="360rpx" :option="opts.flowExpense" />
		</view>

		<view class="bottom-space" />
	</scroll-view>
</template>

<script>
import { formatDate } from '@/utils/format';
import SalesEchart from '@/components/sales-echart/sales-echart.vue';
import {
	aggregateAmount,
	toBarSlices,
	barOption,
	stockAmountSlices,
	stockSummaryInboundSlices
} from '@/utils/analysis-chart-options';

function emptyBar(title) {
	return barOption({
		title,
		names: ['暂无数据'],
		values: [0],
		color: '#e0e0e0'
	});
}

export default {
	components: {
		SalesEchart
	},

	data() {
		const now = new Date();
		const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);
		return {
			startDate: formatDate(monthStart),
			endDate: formatDate(now),
			loading: false,
			opts: {
				purchaseBySupplier: emptyBar('采购额 TOP（按供应商）'),
				purchaseByProduct: emptyBar('采购额 TOP（按商品）'),
				saleByProduct: emptyBar('销售额 TOP（按商品）'),
				saleByCustomer: emptyBar('销售额 TOP（按客户）'),
				stockInboundQty: emptyBar('入库合计数量 TOP（按商品）'),
				stockBalanceAmt: emptyBar('库存期末金额 TOP（按商品，截止结束日）'),
				fundIncome: emptyBar('账户收入合计 TOP'),
				fundExpense: emptyBar('账户支出合计 TOP'),
				receivableByCustomer: emptyBar('应收流水规模 TOP（按客户 | 绝对额）'),
				payableBySupplier: emptyBar('应付流水规模 TOP（按供应商 | 绝对额）'),
				flowIncome: emptyBar('其他收入 TOP（按类别）'),
				flowExpense: emptyBar('其他支出 TOP（按类别）')
			}
		};
	},

	onShow() {
		this.loadAll();
	},

	methods: {
		rangeQuery() {
			return {
				startDate: this.startDate || '',
				endDate: this.endDate || ''
			};
		},

		onRangeChange() {
			if (this.startDate && this.endDate && this.startDate > this.endDate) {
				uni.showToast({ title: '开始日期不能晚于结束日期', icon: 'none' });
				return;
			}
			this.loadAll();
		},

		async loadAll() {
			if (this.startDate && this.endDate && this.startDate > this.endDate) {
				uni.showToast({ title: '请调整日期范围', icon: 'none' });
				return;
			}

			const q = this.rangeQuery();
			const endOnly = { endDate: this.endDate || '' };

			this.loading = true;

			const safe = async (fn) => {
				try {
					return await fn();
				} catch (e) {
					return {};
				}
			};

			const [
				purSup,
				purProd,
				saleProd,
				saleCust,
				stockSum,
				stockAmt,
				acc,
				recv,
				pay,
				flow
			] = await Promise.all([
				safe(() => this.$api.analysisPurchaseSupplierSummaryList(q)),
				safe(() => this.$api.analysisPurchaseProductSummaryList(q)),
				safe(() => this.$api.analysisSaleProductSummaryList(q)),
				safe(() => this.$api.analysisSaleCustomerSummaryList(q)),
				safe(() => this.$api.analysisStockSummaryList(q)),
				safe(() => this.$api.analysisStockAmountList(endOnly)),
				safe(() => this.$api.analysisAccountDetailList(q)),
				safe(() => this.$api.analysisReceivableDetailList(q)),
				safe(() => this.$api.analysisPayableDetailList(q)),
				safe(() => this.$api.analysisFlowDetailList(q))
			]);

			// 采购：供应商 / 商品 维度聚合金额
			const psAgg = aggregateAmount(purSup.summaryList || [], 'supplierName', 'totalAmount');
			const ppAgg = aggregateAmount(purProd.summaryList || [], 'productName', 'totalAmount');
			let s = toBarSlices(psAgg);
			this.opts.purchaseBySupplier =
				s.values.some((v) => v > 0)
					? barOption({
							title: '采购额 TOP（按供应商）',
							names: s.names,
							values: s.values,
							color: '#5c6bc0'
					  })
					: emptyBar('采购额 TOP（按供应商）');
			s = toBarSlices(ppAgg);
			this.opts.purchaseByProduct =
				s.values.some((v) => v > 0)
					? barOption({
							title: '采购额 TOP（按商品）',
							names: s.names,
							values: s.values,
							color: '#3949ab'
					  })
					: emptyBar('采购额 TOP（按商品）');

			// 销售
			const spAgg = aggregateAmount(saleProd.summaryList || [], 'productName', 'totalAmount');
			const scAgg = aggregateAmount(saleCust.summaryList || [], 'customerName', 'totalAmount');
			s = toBarSlices(spAgg);
			this.opts.saleByProduct =
				s.values.some((v) => v > 0)
					? barOption({
							title: '销售额 TOP（按商品）',
							names: s.names,
							values: s.values,
							color: '#00897b'
					  })
					: emptyBar('销售额 TOP（按商品）');
			s = toBarSlices(scAgg);
			this.opts.saleByCustomer =
				s.values.some((v) => v > 0)
					? barOption({
							title: '销售额 TOP（按客户）',
							names: s.names,
							values: s.values,
							color: '#26a69a'
					  })
					: emptyBar('销售额 TOP（按客户）');

			// 库存：收发汇总入库量 + 余额表期末金额
			const inbound = stockSummaryInboundSlices(stockSum.stockList || [], 8);
			this.opts.stockInboundQty =
				inbound.values.some((v) => v > 0)
					? barOption({
							title: '入库合计数量 TOP（按商品）',
							names: inbound.names,
							values: inbound.values,
							color: '#fb8c00'
					  })
					: emptyBar('入库合计数量 TOP（按商品）');

			const bal = stockAmountSlices(stockAmt.productList || [], 8);
			this.opts.stockBalanceAmt =
				bal.values.some((v) => v > 0)
					? barOption({
							title: '库存期末金额 TOP（截止结束日）',
							names: bal.names,
							values: bal.values,
							color: '#f4511e'
					  })
					: emptyBar('库存期末金额 TOP（按商品，截止结束日）');

			// 资金：按账户汇总收入 / 支出
			const records = acc.recordList || [];
			const incomeByAcc = aggregateAmount(
				records.map((r) => ({
					_name: r.accountName || '未知账户',
					_v: Number(r.incomeAmount || 0)
				})),
				'_name',
				'_v'
			);
			const expenseByAcc = aggregateAmount(
				records.map((r) => ({
					_name: r.accountName || '未知账户',
					_v: Number(r.expenseAmount || 0)
				})),
				'_name',
				'_v'
			);
			s = toBarSlices(incomeByAcc.filter((x) => x.value > 0));
			this.opts.fundIncome =
				s.values.length && s.values.some((v) => v > 0)
					? barOption({
							title: '账户收入合计 TOP',
							names: s.names,
							values: s.values,
							color: '#43a047'
					  })
					: emptyBar('账户收入合计 TOP');
			s = toBarSlices(expenseByAcc.filter((x) => x.value > 0));
			this.opts.fundExpense =
				s.values.length && s.values.some((v) => v > 0)
					? barOption({
							title: '账户支出合计 TOP',
							names: s.names,
							values: s.values,
							color: '#e53935'
					  })
					: emptyBar('账户支出合计 TOP');

			// 应收应付：按往来单位汇总单据绝对额（便于看出流水规模）
			const recvRows = (recv.receivableList || []).map((r) => ({
				_name: r.customerName || '未知客户',
				_v: Math.abs(Number(r.amount || 0))
			}));
			const payRows = (pay.payableList || []).map((r) => ({
				_name: r.supplierName || '未知供应商',
				_v: Math.abs(Number(r.amount || 0))
			}));
			const recvAgg = aggregateAmount(recvRows, '_name', '_v');
			const payAgg = aggregateAmount(payRows, '_name', '_v');
			s = toBarSlices(recvAgg);
			this.opts.receivableByCustomer =
				s.values.some((v) => v > 0)
					? barOption({
							title: '应收流水规模 TOP（按客户）',
							names: s.names,
							values: s.values,
							color: '#7e57c2'
					  })
					: emptyBar('应收流水规模 TOP（按客户 | 绝对额）');
			s = toBarSlices(payAgg);
			this.opts.payableBySupplier =
				s.values.some((v) => v > 0)
					? barOption({
							title: '应付流水规模 TOP（按供应商）',
							names: s.names,
							values: s.values,
							color: '#ab47bc'
					  })
					: emptyBar('应付流水规模 TOP（按供应商 | 绝对额）');

			// 其他收支
			const flowList = flow.recordList || [];
			const inc = flowList.filter((r) => Number(r.incomeAmount || 0) > 0);
			const exp = flowList.filter((r) => Number(r.expenseAmount || 0) > 0);
			const incAgg = aggregateAmount(inc, 'categoryName', 'incomeAmount');
			const expAgg = aggregateAmount(exp, 'categoryName', 'expenseAmount');
			s = toBarSlices(incAgg);
			this.opts.flowIncome =
				s.values.some((v) => v > 0)
					? barOption({
							title: '其他收入 TOP（按类别）',
							names: s.names,
							values: s.values,
							color: '#29b6f6'
					  })
					: emptyBar('其他收入 TOP（按类别）');
			s = toBarSlices(expAgg);
			this.opts.flowExpense =
				s.values.some((v) => v > 0)
					? barOption({
							title: '其他支出 TOP（按类别）',
							names: s.names,
							values: s.values,
							color: '#78909c'
					  })
					: emptyBar('其他支出 TOP（按类别）');

			this.loading = false;
		}
	}
};
</script>

<style lang="scss">
.page {
	height: 100vh;
	background: #f5f6fa;
	box-sizing: border-box;
}

.date-panel {
	margin: 20rpx;
	padding: 24rpx;
	background: #fff;
	border-radius: 16rpx;
}

.panel-title {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
	display: block;
	margin-bottom: 16rpx;
}

.date-grid {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.date-cell {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}

.date-cell .label {
	font-size: 26rpx;
	color: #666;
}

.refresh {
	margin-top: 20rpx;
	align-self: flex-start;
}

.hint {
	display: block;
	margin-top: 16rpx;
	font-size: 22rpx;
	color: #999;
	line-height: 1.45;
}

.chart-card-title{
	margin-bottom: 20rpx;
}
.chart-card {
	margin: 20rpx 20rpx;
}

.bottom-space {
	height: calc(40rpx + env(safe-area-inset-bottom));
}
</style>
