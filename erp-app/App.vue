<script>
import userUtils from '@/utils/user';
const { getToken } = userUtils;

export default {
	onLaunch: function () {
		console.log('App Launch');
	},
	onShow: function () {
		console.log('App Show');
		if (!getToken()) {
			uni.reLaunch({
				url: '/subpackages/auth/login'
			});
		}
	},
	onHide: function () {
		console.log('App Hide');
	}
};
</script>

<style lang="scss">
/*每个页面公共css */
@import '@/uni_modules/uni-scss/index.scss';
/* #ifndef APP-NVUE */
@import '@/static/customicons.css';
// 设置整个项目的背景色
page {
	background-color: #f5f5f5;
}

/* #endif */
.example-info {
	font-size: 28rpx;
	color: #333;
	padding: 20rpx;
}
</style>
