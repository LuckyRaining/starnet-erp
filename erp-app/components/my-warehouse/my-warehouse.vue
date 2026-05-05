<!--
  收银页顶部「收货仓库」区：
  - 未选仓库：按钮 → chooseWarehouse 跳到分包 warehouse 页拉列表选择；
  - 已选：展示名称/编码，整块可点 → 同样跳转重新选择。
  warehouse 来自 Vuex m_user（本地持久化见 store/user.js）。
-->
<template>
	<view class="warehouse-section">
		<view v-if="!warehouse" class="pick-box">
			<button class="pick-btn" size="mini" type="primary" plain @click="chooseWarehouse">
				请选择收货仓库+
			</button>
		</view>
		<view v-else class="info-box" @click="chooseWarehouse">
			<text class="label">收货仓库</text>
			<text class="name">{{ warehouse.name || '未命名仓库' }}</text>
			<text class="code">编码：{{ warehouse.code || '-' }}</text>
			<text class="hint">点击重新选择</text>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';

export default {
	name: 'MyWarehouse',

	computed: {
		/** mapState：把 store 里 m_user.warehouse 映射成组件计算属性 warehouse */
		...mapState('m_user', ['warehouse'])
	},

	methods: {
		/** 统一入口：选仓 / 换仓 */
		chooseWarehouse() {
			uni.navigateTo({
				url: '/subpackages/business/warehouse'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.warehouse-section {
	margin-bottom: 20rpx;
}

.pick-box {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	display: flex;
	justify-content: center;
	align-items: center;
}

.pick-btn {
	font-size: 28rpx;
}

.info-box {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	display: flex;
	flex-direction: column;
	align-items: flex-start;
}

.label {
	font-size: 24rpx;
	color: #888;
	margin-bottom: 8rpx;
}

.name {
	font-size: 32rpx;
	color: #222;
	font-weight: 600;
}

.code {
	margin-top: 10rpx;
	font-size: 26rpx;
	color: #555;
}

.hint {
	margin-top: 12rpx;
	font-size: 22rpx;
	color: #c00000;
}
</style>
