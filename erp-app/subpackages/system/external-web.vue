<!--
  外链承载页：首页轮播 kind=web 时，非 H5 端 navigateTo 到此页，由 web-view 打开目标地址。
  微信小程序需在公众平台配置业务域名；非法 scheme 不赋值 src，避免 web-view 报错。
-->
<template>
	<web-view v-if="src" :src="src" />
	<view v-else class="empty">链接无效或已失效</view>
</template>

<script>
export default {
	data() {
		return {
			src: ''
		};
	},
	onLoad(query) {
		// 上一级页面已对 url 做过 encodeURIComponent，此处 decode 还原真实链接
		const raw = query.url ? decodeURIComponent(query.url) : '';
		// 仅允许 http/https，防止 javascript: 等危险协议进入 web-view
		this.src = raw && /^https?:\/\//i.test(raw) ? raw : '';
		if (!this.src && raw) {
			uni.$showMsg && uni.$showMsg('仅支持 http / https 链接');
		}
	}
};
</script>

<style lang="scss" scoped>
.empty {
	padding: 80rpx 40rpx;
	text-align: center;
	color: #999;
	font-size: 28rpx;
}
</style>
