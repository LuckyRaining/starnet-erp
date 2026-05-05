<template>
	<view class="page">
		<my-login v-if="!isLoggedIn" />
		<my-userinfo v-else />
	</view>
</template>

<script>
import { mapMutations, mapState } from 'vuex';
import tabbarBadge from '@/mixins/tabbar-badge.js';
import MyLogin from '@/components/my-login/my-login.vue';
import MyUserinfo from '@/components/my-userinfo/my-userinfo.vue';

export default {
	components: {
		MyLogin,
		MyUserinfo
	},

	mixins: [tabbarBadge],

	computed: {
		...mapState('m_user', ['token']),

		isLoggedIn() {
			const t = this.token != null ? String(this.token).trim() : '';
			return !!t;
		}
	},

	onShow() {
		this.hydrateFromStorage();
	},

	methods: {
		...mapMutations('m_user', ['hydrateFromStorage'])
	}
};
</script>

<style lang="scss">
.page {
	padding: 20rpx;
	min-height: 100vh;
	box-sizing: border-box;
	background: #f8f8f8;
}
</style>
