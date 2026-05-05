<!--
  选择收货仓库（分包页）：进入后请求 warehousePage，点某一行使 updateWarehouse 写入 Vuex 并返回上一页。
  与组件 my-warehouse 配合：my-warehouse 只负责展示与跳转，数据落在 m_user.warehouse。
-->
<template>
	<view class="page">
		<view v-if="loading" class="state">加载中…</view>
		<view v-else-if="!list.length" class="state">暂无仓库数据</view>
		<scroll-view v-else scroll-y class="scroll">
			<view
				v-for="w in list"
				:key="w.id"
				class="row"
				:class="{ disabled: w.active === false }"
				@click="select(w)"
			>
				<view class="main">
					<text class="name">{{ w.name || '未命名' }}</text>
					<text class="code">编码：{{ w.code || '-' }}</text>
				</view>
				<text v-if="w.active === false" class="tag">已停用</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { mapMutations } from 'vuex';

export default {
	data() {
		return {
			list: [],
			loading: false
		};
	},

	onLoad() {
		this.loadList();
	},

	methods: {
		...mapMutations('m_user', ['updateWarehouse']),

		async loadList() {
			this.loading = true;
			try {
				// 与商品分页类似，后端把分页对象放在 data.warehousePage 里
				const data = await this.$api.warehousePage({
					query: {},
					current: 1,
					size: 100
				});
				const page = data && data.warehousePage;
				this.list = (page && page.records) || [];
			} catch (e) {
				uni.showToast({ title: e.message || '加载失败', icon: 'none' });
				this.list = [];
			} finally {
				this.loading = false;
			}
		},

		/** 选中后写入 Vuex（顺带写入本地存储），收银页返回后即可读到 warehouse */
		select(w) {
			if (!w || !w.id) return;
			if (w.active === false) {
				uni.showToast({ title: '该仓库已停用', icon: 'none' });
				return;
			}
			this.updateWarehouse({
				id: w.id,
				code: w.code,
				name: w.name
			});
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.page {
	min-height: 100vh;
	background: #f8f8f8;
}

.scroll {
	height: 100vh;
}

.state {
	padding: 80rpx;
	text-align: center;
	font-size: 28rpx;
	color: #999;
}

.row {
	display: flex;
	flex-direction: row;
	align-items: center;
	justify-content: space-between;
	background: #fff;
	margin: 16rpx 20rpx;
	padding: 28rpx 24rpx;
	border-radius: 16rpx;
}

.row.disabled {
	opacity: 0.55;
}

.main {
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.name {
	font-size: 32rpx;
	color: #222;
	font-weight: 600;
}

.code {
	margin-top: 10rpx;
	font-size: 26rpx;
	color: #888;
}

.tag {
	font-size: 22rpx;
	color: #c00000;
	flex-shrink: 0;
}
</style>
