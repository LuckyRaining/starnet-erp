<template>
  <view class="page">
    <view class="page-body">
      <view v-if="product.id" class="card">
        <!-- /**
         * 每个商品的信息对象，都包含如下 22 个属性：
         * id: ID主键,
         * code: 编号,
         * name: 名称,
         * barcode: 条码,
         * spec: 规格,
         * categoryId: 类别ID,
         * categoryName: 类别,
         * primaryWarehouseId: 首选仓库ID,
         * unitId: 计量单位ID,
         * unitName: 计量单位,

         * retailPrice: 零售价,
         * wholesalePrice: 批发价,
         * vipPrice: VIP价格,
         * discountRate1: 折扣率1,
         * discountRate2: 折扣率2,
         * estimatedPurchasePrice: 预计采购价,

         * remark: 备注,
         * minimumStock: 最低库存,
         * maximumStock: 最高库存,
         * active: 是否启用,
         * createdTime: 创建时间,
         * updatedTime: 更新时间,
         */ -->
        <view class="name">{{ product.name }}</view>
        <view class="line">编码：{{ product.code || '-' }}</view>
        <view class="line">条码：{{ product.barcode || '-' }}</view>
        <view class="line">规格：{{ product.spec || '-' }}</view>
        <view class="line">类别：{{ product.categoryName || '-' }}</view>
        <view class="line">计量单位：{{ product.unitName || '-' }}</view>

        <view class="line">零售价：¥{{ product.retailPrice || 0 }}</view>
        <view class="line">批发价：¥{{ product.wholesalePrice || 0 }}</view>
        <view class="line">VIP价格：¥{{ product.vipPrice || 0 }}</view>
        <view class="line">折扣率1：¥{{ product.discountRate1 || 0 }}</view>
        <view class="line">折扣率2：¥{{ product.discountRate2 || 0 }}</view>
        <view class="line">预计采购价：¥{{ product.estimatedPurchasePrice || 0 }}</view>

        <view class="line">当前库存：{{ product.stock || 0 }}</view>
        <view class="line">最低库存：{{ product.minimumStock || 0 }}</view>
        <view class="line">最高库存：{{ product.maximumStock || 0 }}</view>

        <view class="line">备注：{{ product.remark || 0 }}</view>
      </view>

      <view v-else class="empty">商品不存在或已下架！</view>
    </view>

    <!-- 商品导航：固定在页面最底部 -->
    <!-- @click：左侧点击事件 -->
    <!-- @buttonClick：右侧按钮组点击事件 -->
    <view class="goods-nav-fixed">
      <uni-goods-nav :fill="true" :options="options" :button-group="buttonGroup" @click="onGoodsNavClick"
                     @buttonClick="buttonClick"/>
    </view>
  </view>
</template>

<script>
// vue2 语法
// import {mapGetters, mapMutations} from 'vuex';
// import store from '@/store/store.js';

export default {
  data() {
    return {
      /**
       * 商品详细信息，都包含如下 22 个属性：
       * id: ID主键,
       * code: 编号,
       * name: 名称,
       * barcode: 条码,
       * spec: 规格,
       * categoryId: 类别ID,
       * categoryName: 类别,
       * primaryWarehouseId: 首选仓库ID,
       * unitId: 计量单位ID,
       * unitName: 计量单位,
       *
       * retailPrice: 零售价,
       * wholesalePrice: 批发价,
       * vipPrice: VIP价格,
       * discountRate1: 折扣率1,
       * discountRate2: 折扣率2,
       * estimatedPurchasePrice: 预计采购价,
       *
       * remark: 备注,
       * minimumStock: 最低库存,
       * maximumStock: 最高库存,
       * active: 是否启用,
       * createdTime: 创建时间,
       * updatedTime: 更新时间,
       */
      product: {},

      /** 组件参数：左侧 图标按钮（店铺、购物车） */
      options: [
        {icon: 'shop', text: '店铺'},
        {icon: 'cart', text: '购物车'}
      ],

      /** 组件按钮组参数：右侧 操作按钮组（加入购物车、立即购买） */
      buttonGroup: [
        {
          text: '加入购物车',
          backgroundColor: 'linear-gradient(90deg, #FFCD1E, #FF8A18)',
          color: '#fff'
        },
        {
          text: '立即购买',
          backgroundColor: 'linear-gradient(90deg, #FE6035, #EF1224)',
          color: '#fff'
        }
      ]
    };
  },

  computed: {
    // vue2 语法
    // ...mapGetters('m_cart', ['total'])
    /**
     * 计算购物车商品总数
     *
     * @returns {Number} 购物车中所有商品的数量总和
     */
    total() {
      return this.$store.getters['m_cart/total'];
    },
  },

  watch: {
    /**
     * 监听购物车总数 total 变化，更新购物车图标角标
     *
     * 规则：
     * - 数量为 0：不显示角标
     * - 数量 1-99：显示实际数字
     * - 数量 > 99：显示 "99+"
     */
    total: {
      /**
       * 处理购物车总数变化的回调函数
       *
       * @param {Number} newVal - 新的购物车总数
       */
      handler(newVal) {
			const n = Number(newVal) || 0;
		// >0 or <=0
        if (n > 0) {

        	// this.$set(this.options[1], 'info', n > 99 ? '99+' : n);
			this.options[1].info = n > 99 ? '99+' : n;
        } else {
			delete this.options[1].info;
			// this.$delete(this.options[1], 'info');
        }
      },
      /**
       * immediate 属性用来声明此侦听器，是否在页面初次加载完毕后立即调用
       */
      immediate: true
    }
  },

  onLoad(query) {
    /**
     * 从路由参数获取商品 Id，并请求详情
     *
     * @param {Object} query - 路由查询参数对象
     * @param {String} [query.id] - 商品ID（URL编码格式）
     */
    const productId = query.id ? decodeURIComponent(query.id) : '';
    if (productId) {
      this.loadDetail(productId);
    }
  },

  methods: {
    // vue2 语法
    // ...mapMutations('m_cart', ['addToCart'])
    /**
     * 将商品添加到购物车
     *
     * @param {Object} product_brief - 商品简要信息对象
     *
     * @param {String|Number} product_brief.product_id - 商品ID
     * @param {String} product_brief.product_name - 商品名称
     * @param {Number} product_brief.product_price - 商品价格
     * @param {Number} product_brief.product_count - 商品数量
     * @param {Boolean} product_brief.cart_state - 商品选中状态
     */
    addToCart(product_brief) {
      this.$store.commit('m_cart/addToCart', product_brief);
    },

    /**
     * 加载商品详情数据
     *
     * @param {String|Number} productId - 商品ID
     * @throws {Error} 当接口调用失败时显示错误提示
     */
    async loadDetail(productId) {
      try {
        const data = await this.$api.productDetail({productId});
        this.product = data.product || {};
      } catch (error) {
        uni.$showMsg(error.message || '加载失败');
      }
    },

    /**
     * 左侧 options 点击事件：uni-goods-nav 的 @click
     * e = { index, content }：事件对象 e 含 index（下标）、content（当前按钮配置对象）
     * index = 0：店铺，index = 1：购物车
     */
    onGoodsNavClick(e) {
      // console.log(e);

      /**
       * 点击 店铺
       */
      if (e.index === 0) {
        uni.$showMsg('店铺功能开发中');
      }

      /**
       * 点击 购物车
       */
      if (e.index === 1) {
        /**
         * 切换到 购物车 页面
         */
        uni.switchTab({
          url: '/pages/cart/cart'
        });
      }
    },

    /**
     * 右侧按钮组：uni-goods-nav 的 @buttonClick
     * e = { index, content }。
     * index = 0：加入购物车，index = 1：立即购买
     */
    buttonClick(e) {
      /**
       * 点击 加入购物车
       */
      if (e.index === 0) {
        if (!this.product || !this.product.id) {
          uni.$showMsg('商品不存在或已下架！');
          return;
        }

        const product_brief = {
          product_id: this.product.id,
          product_name: this.product.name || '',
          product_price: Number(
            this.product.estimatedPurchasePrice != null
              ? this.product.estimatedPurchasePrice
              : this.product.wholesalePrice != null
                ? this.product.wholesalePrice
                : 0
          ),
          product_count: 1,
          cart_state: true
        };

        this.addToCart(product_brief);
        uni.$showMsg('已加入购物车', 1500, 'success');
        return;
      }

      /**
       * 点击 立即购买
       */
      if (e.index === 1) {
        uni.$showMsg('立即购买功能开发中');
      }
    }
  }
};
</script>

<style lang="scss">
.page {
  min-height: 100vh;
  background: #f8f8f8;
  position: relative;
}

.page-body {
  padding: 20rpx;
  /* 避免内容被底部固定导航遮挡：导航条高度约 50px + 安全区 */
  padding-bottom: calc(50px + constant(safe-area-inset-bottom) + 24rpx);
  padding-bottom: calc(50px + env(safe-area-inset-bottom) + 24rpx);
}

.card {
  background: #fff;
  padding: 24rpx;
  border-radius: 16rpx;
}

.name {
  font-size: 34rpx;
  font-weight: 700;
  margin-bottom: 20rpx;
}

.line {
  font-size: 26rpx;
  color: #555;
  margin-bottom: 12rpx;
}

.empty {
  text-align: center;
  margin-top: 30vh;
  color: #999;
}

.goods-nav-fixed {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background-color: #fff;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  box-shadow: 0 -6rpx 24rpx rgba(0, 0, 0, 0.06);
}
</style>
