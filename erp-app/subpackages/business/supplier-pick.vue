<template>
	<view class="page">
		<view class="search-row">
			<uni-easyinput v-model="keyword" placeholder="供应商名称/编码" confirm-type="search" @confirm="loadList" />
			<button size="mini" type="primary" @click="loadList">搜索</button>
		</view>

		<view v-if="loading" class="state">加载中…</view>
		<view v-else-if="!list.length" class="state">暂无供应商</view>
		<scroll-view v-else scroll-y class="scroll">
			<view
				v-for="s in list"
				:key="s.id"
				class="row"
				:class="{ disabled: s.active === false }"
				@click="select(s)"
			>
				<view class="main">
					<text class="name">{{ s.name || '未命名' }}</text>
					<text class="code">编码：{{ s.code || '-' }}</text>
				</view>
				<text v-if="s.active === false" class="tag">已停用</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import CONFIG from '@/utils/config';

export default {
	data() {
		return {
			keyword: '',
			list: [],
			loading: false
		};
	},

	onLoad() {
		this.loadList();
	},

	methods: {
		async loadList() {
			this.loading = true;
			try {
				const data = await this.$api.supplierPage({
					query: { keyword: this.keyword || '' },
					current: 1,
					size: 100
				});
				const page = data && data.supplierPage;
				this.list = (page && page.records) || [];
			} catch (e) {
				uni.showToast({ title: e.message || '加载失败', icon: 'none' });
				this.list = [];
			} finally {
				this.loading = false;
			}
		},

		select(s) {
			if (!s || !s.id) return;
			if (s.active === false) {
				uni.showToast({ title: '该供应商已停用', icon: 'none' });
				return;
			}
			uni.setStorageSync(CONFIG.CHECKOUT_RETURN_SUPPLIER_KEY, {
				id: s.id,
				name: s.name || '',
				code: s.code || ''
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

.search-row {
	display: flex;
	flex-direction: row;
	align-items: center;
	gap: 16rpx;
	padding: 16rpx 20rpx;
	background: #fff;
}

.scroll {
	height: calc(100vh - 120rpx);
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
