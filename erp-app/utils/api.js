/**
 * API接口定义
 */
import { request } from './request.js'

// 用户相关API
const userApi = {
	// 登录
	login(data) {
		return request({
			url: '/user/login',
			method: 'POST',
			data
		})
	},
	
	// 注册(暂不开放)
	register(data) {
		return request({
			url: '/user/add',
			method: 'POST',
			data
		})
	}
}

// 商品相关API
const productApi = {
	// 商品分页列表
	page(data) {
		return request({
			url: '/product/page',
			method: 'POST',
			data
		})
	},
	
	// 商品详情
	detail(productId) {
		return request({
			url: '/product/detail',
			method: 'POST',
			data: { productId }
		})
	}
}

// 分类相关API
const categoryApi = {
	// 分类列表(商品分类type=30)
	list(type, query = {}) {
		return request({
			url: '/category/list',
			method: 'POST',
			data: { type, query }
		})
	}
}

// 销售分析API
const analysisApi = {
	// 销售汇总表(按商品)
	saleProductSummaryList(data) {
		return request({
			url: '/analysis/sale/productSummaryList',
			method: 'POST',
			data
		})
	},
	
	// 销售汇总表(按客户)
	saleCustomerSummaryList(data) {
		return request({
			url: '/analysis/sale/customerSummaryList',
			method: 'POST',
			data
		})
	},
	
	// 销售明细表
	saleDetailList(data) {
		return request({
			url: '/analysis/sale/detailList',
			method: 'POST',
			data
		})
	}
}

// 出库单API(收银结算)
const checkoutApi = {
	// 创建出库单编号
	createCode() {
		return request({
			url: '/checkout/createCode',
			method: 'POST',
			data: {}
		})
	},
	
	// 保存出库单
	save(data) {
		return request({
			url: '/checkout/save',
			method: 'POST',
			data
		})
	}
}

// 字典项API
const dictApi = {
	// 获取字典项列表
	itemList(dictCode) {
		return request({
			url: '/dict/itemList',
			method: 'POST',
			data: { dictCode }
		})
	}
}

// 仓库相关API
const warehouseApi = {
	// 分页列表
	page(data) {
		return request({url: '/warehouse/page', method: 'POST', data})
	}
}

export {
	userApi,
	productApi,
	categoryApi,
	analysisApi,
	checkoutApi,
	dictApi,
	warehouseApi
}
