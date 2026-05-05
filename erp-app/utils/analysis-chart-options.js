/**
 * 数据统计页 ECharts option 构建（与 components/sales-echart 的 bar 结构一致，便于小程序端条形回退）
 */

export function truncateLabel(s, max = 8) {
	if (s == null || s === '') return '—';
	const t = String(s);
	return t.length <= max ? t : `${t.slice(0, max)}…`;
}

/** 按名称聚合金额求和 */
export function aggregateAmount(rows, nameKey, amountKey) {
	const map = {};
	for (const row of rows || []) {
		const name = row[nameKey] != null ? String(row[nameKey]) : '其他';
		map[name] = (map[name] || 0) + Number(row[amountKey] || 0);
	}
	return Object.entries(map)
		.map(([name, value]) => ({ name, value }))
		.sort((a, b) => b.value - a.value);
}

export function toBarSlices(items, topN = 8) {
	const slice = (items || []).slice(0, topN);
	return {
		names: slice.map((x) => truncateLabel(x.name)),
		values: slice.map((x) => Number(x.value) || 0)
	};
}

export function barOption({ title, names, values, color = '#2f7df6' }) {
	return {
		title: {
			text: title,
			left: 'center',
			top: 6,
			textStyle: { fontSize: 13, fontWeight: 600 }
		},
		tooltip: {
			trigger: 'axis',
			axisPointer: { type: 'shadow' }
		},
		grid: {
			left: '13%',
			right: '8%',
			bottom: names.some((n) => String(n).length > 7) ? '26%' : '16%',
			top: 48
		},
		xAxis: {
			type: 'category',
			data: names,
			axisLabel: { rotate: 28, fontSize: 10 }
		},
		yAxis: {
			type: 'value',
			splitLine: { lineStyle: { type: 'dashed', color: '#eee' } }
		},
		series: [
			{
				type: 'bar',
				data: values,
				barMaxWidth: 26,
				itemStyle: { color }
			}
		]
	};
}

/** 库存余额：按商品汇总期末金额 */
export function stockAmountSlices(productList, topN = 8) {
	const rows = [];
	for (const p of productList || []) {
		const m = p && p.warehouseAmountMapping && p.warehouseAmountMapping.total;
		const amt = Number((m && m.totalAmount) || 0);
		if (!p || !p.productName) continue;
		rows.push({ name: p.productName, value: amt });
	}
	rows.sort((a, b) => b.value - a.value);
	return toBarSlices(rows, topN);
}

/** 收发汇总：按商品汇总入库合计数量 */
export function stockSummaryInboundSlices(stockList, topN = 8) {
	const agg = aggregateAmount(stockList || [], 'productName', 'storeTotalQuantity');
	return toBarSlices(agg, topN);
}
