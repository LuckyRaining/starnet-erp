/**
 * 前端 API 声明与集中管理
 */

import request from "./request";

// ==================== bc包 ====================
// ==================== 订单管理 (OrderController) ====================
const OrderControllerApi = {
	/**
	 * 订单分页列表
	 */
	orderPage: (data) => request({
		url: "/order/page",
		method: "POST",
		data: data
	}),

	/**
	 * 订单详情
	 */
	orderDetail: (data) => request({
		url: "/order/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建订单编号
	 */
	orderCreateCode: (data) => request({
		url: "/order/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 订单保存
	 */
	orderSave: (data) => request({
		url: "/order/save",
		method: "POST",
		data: data
	}),

	/**
	 * 订单删除
	 */
	orderDelete: (data) => request({
		url: "/order/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 订单切换审查
	 */
	orderSwitchCheck: (data) => request({
		url: "/order/switchCheck",
		method: "POST",
		data: data
	})
};

// ==================== 采购管理 (PurchaseController) ====================
const PurchaseControllerApi = {
	/**
	 * 采购单分页列表
	 */
	purchasePage: (data) => request({
		url: "/purchase/page",
		method: "POST",
		data: data
	}),

	/**
	 * 采购单详情
	 */
	purchaseDetail: (data) => request({
		url: "/purchase/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建采购单编号
	 */
	purchaseCreateCode: (data) => request({
		url: "/purchase/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 采购单保存
	 */
	purchaseSave: (data) => request({
		url: "/purchase/save",
		method: "POST",
		data: data
	}),

	/**
	 * 采购单删除
	 */
	purchaseDelete: (data) => request({
		url: "/purchase/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 采购单切换审查
	 */
	purchaseSwitchCheck: (data) => request({
		url: "/purchase/switchCheck",
		method: "POST",
		data: data
	}),

	/**
	 * 根据供应商查询已审核采购单列表
	 */
	purchaseFindCheckedListBySupplier: (data) => request({
		url: "/purchase/findCheckedListBySupplier",
		method: "POST",
		data: data
	})
};

// ==================== 销售管理 (SaleController) ====================
const SaleControllerApi = {
	/**
	 * 销售单分页列表
	 */
	salePage: (data) => request({
		url: "/sale/page",
		method: "POST",
		data: data
	}),

	/**
	 * 销售单详情
	 */
	saleDetail: (data) => request({
		url: "/sale/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建销售单编号
	 */
	saleCreateCode: (data) => request({
		url: "/sale/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 销售单保存
	 */
	saleSave: (data) => request({
		url: "/sale/save",
		method: "POST",
		data: data
	}),

	/**
	 * 销售单删除
	 */
	saleDelete: (data) => request({
		url: "/sale/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 销售单切换审查
	 */
	saleSwitchCheck: (data) => request({
		url: "/sale/switchCheck",
		method: "POST",
		data: data
	}),

	/**
	 * 根据客户查询已审核销售单列表
	 */
	saleFindCheckedListByCustomer: (data) => request({
		url: "/sale/findCheckedListByCustomer",
		method: "POST",
		data: data
	})
};

// ==================== fc包 ====================
// ==================== 收款管理 (CollectionController) ====================
const CollectionControllerApi = {
	/**
	 * 收款单分页列表
	 */
	collectionPage: (data) => request({
		url: "/collection/page",
		method: "POST",
		data: data
	}),

	/**
	 * 收款单详情
	 */
	collectionDetail: (data) => request({
		url: "/collection/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建收款单编号
	 */
	collectionCreateCode: (data) => request({
		url: "/collection/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 收款单保存
	 */
	collectionSave: (data) => request({
		url: "/collection/save",
		method: "POST",
		data: data
	}),

	/**
	 * 收款单删除
	 */
	collectionDelete: (data) => request({
		url: "/collection/delete",
		method: "POST",
		data: data
	})
};

// ==================== 付款管理 (PaymentController) ====================
const PaymentControllerApi = {
	/**
	 * 付款单分页列表
	 */
	paymentPage: (data) => request({
		url: "/payment/page",
		method: "POST",
		data: data
	}),

	/**
	 * 付款单详情
	 */
	paymentDetail: (data) => request({
		url: "/payment/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建付款单编号
	 */
	paymentCreateCode: (data) => request({
		url: "/payment/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 付款单保存
	 */
	paymentSave: (data) => request({
		url: "/payment/save",
		method: "POST",
		data: data
	}),

	/**
	 * 付款单删除
	 */
	paymentDelete: (data) => request({
		url: "/payment/delete",
		method: "POST",
		data: data
	})
};

// ==================== 收入管理 (IncomeController) ====================
const IncomeControllerApi = {
	/**
	 * 收入单分页列表
	 */
	incomePage: (data) => request({
		url: "/income/page",
		method: "POST",
		data: data
	}),

	/**
	 * 收入单详情
	 */
	incomeDetail: (data) => request({
		url: "/income/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建收入单编号
	 */
	incomeCreateCode: (data) => request({
		url: "/income/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 收入单保存
	 */
	incomeSave: (data) => request({
		url: "/income/save",
		method: "POST",
		data: data
	}),

	/**
	 * 收入单删除
	 */
	incomeDelete: (data) => request({
		url: "/income/delete",
		method: "POST",
		data: data
	})
};

// ==================== 支出管理 (ExpenseController) ====================
const ExpenseControllerApi = {
	/**
	 * 支出单分页列表
	 */
	expensePage: (data) => request({
		url: "/expense/page",
		method: "POST",
		data: data
	}),

	/**
	 * 支出单详情
	 */
	expenseDetail: (data) => request({
		url: "/expense/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建支出单编号
	 */
	expenseCreateCode: (data) => request({
		url: "/expense/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 支出单保存
	 */
	expenseSave: (data) => request({
		url: "/expense/save",
		method: "POST",
		data: data
	}),

	/**
	 * 支出单删除
	 */
	expenseDelete: (data) => request({
		url: "/expense/delete",
		method: "POST",
		data: data
	})
};

// ==================== rc包 ====================
// ==================== 类别管理 (CategoryController) ====================
const CategoryControllerApi = {
	/**
	 * 类别列表
	 */
	categoryList: (data) => request({
		url: "/category/list",
		method: "POST",
		data: data
	}),

	/**
	 * 类别保存
	 */
	categorySave: (data) => request({
		url: "/category/save",
		method: "POST",
		data: data
	}),

	/**
	 * 类别详情
	 */
	categoryDetail: (data) => request({
		url: "/category/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 类别删除
	 */
	categoryDelete: (data) => request({
		url: "/category/delete",
		method: "POST",
		data: data
	})
};

// ==================== 字典管理 (DictController) ====================
const DictControllerApi = {
	/**
	 * 字典项列表
	 */
	dictItemList: (data) => request({
		url: "/dict/itemList",
		method: "POST",
		data: data
	}),

	/**
	 * 字典项详情
	 */
	dictItemDetail: (data) => request({
		url: "/dict/itemDetail",
		method: "POST",
		data: data
	}),

	/**
	 * 字典项保存
	 */
	dictItemSave: (data) => request({
		url: "/dict/itemSave",
		method: "POST",
		data: data
	}),

	/**
	 * 字典项删除
	 */
	dictItemDelete: (data) => request({
		url: "/dict/itemDelete",
		method: "POST",
		data: data
	})
};

// ==================== 系统配置 (IndexController) ====================
const SystemControllerApi = {
	/**
	 * 获取系统配置
	 */
	getSystemConfiguration: (data = {}) => request({
		url: "/getSystemConfiguration",
		method: "POST",
		data: data
	}),

	/**
	 * 设置系统配置
	 */
	setSystemConfiguration: (data) => request({
		url: "/setSystemConfiguration",
		method: "POST",
		data: data
	})
};

// ==================== 日志管理 (LogController) ====================
const LogControllerApi = {
	/**
	 * 日志分页列表
	 */
	logPage: (data) => request({
		url: "/log/page",
		method: "POST",
		data: data
	})
};

// ==================== 菜单管理 (MenuController) ====================
const MenuControllerApi = {
	/**
	 * 菜单新增
	 */
	menuAdd: (data) => request({
		url: "/menu/add",
		method: "POST",
		data: data
	}),

	/**
	 * 菜单列表
	 */
	menuList: (data) => request({
		url: "/menu/list",
		method: "POST",
		data: data
	})
};

// ==================== sc包 ====================
// ==================== 报表分析 (AnalysisController) ====================
const AnalysisControllerApi = {
	/**
	 * 采购明细表
	 */
	analysisPurchaseDetailList: (data) => request({
		url: "/analysis/purchase/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 采购汇总表（按商品）
	 */
	analysisPurchaseProductSummaryList: (data) => request({
		url: "/analysis/purchase/productSummaryList",
		method: "POST",
		data: data
	}),

	/**
	 * 采购汇总表（按供应商）
	 */
	analysisPurchaseSupplierSummaryList: (data) => request({
		url: "/analysis/purchase/supplierSummaryList",
		method: "POST",
		data: data
	}),

	/**
	 * 销售明细表
	 */
	analysisSaleDetailList: (data) => request({
		url: "/analysis/sale/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 销售汇总表（按商品）
	 */
	analysisSaleProductSummaryList: (data) => request({
		url: "/analysis/sale/productSummaryList",
		method: "POST",
		data: data
	}),

	/**
	 * 销售汇总表（按客户）
	 */
	analysisSaleCustomerSummaryList: (data) => request({
		url: "/analysis/sale/customerSummaryList",
		method: "POST",
		data: data
	}),

	/**
	 * 收发明细表
	 */
	analysisStockDetailList: (data) => request({
		url: "/analysis/stock/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 库存余额表
	 */
	analysisStockAmountList: (data) => request({
		url: "/analysis/stock/amountList",
		method: "POST",
		data: data
	}),

	/**
	 * 收发汇总表
	 */
	analysisStockSummaryList: (data) => request({
		url: "/analysis/stock/summaryList",
		method: "POST",
		data: data
	}),

	/**
	 * 现金银行报表
	 */
	analysisAccountDetailList: (data) => request({
		url: "/analysis/account/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 应收账款明细表
	 */
	analysisReceivableDetailList: (data) => request({
		url: "/analysis/receivable/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 应付账款明细表
	 */
	analysisPayableDetailList: (data) => request({
		url: "/analysis/payable/detailList",
		method: "POST",
		data: data
	}),

	/**
	 * 客户对账单
	 */
	analysisReceivableCustomerList: (data) => request({
		url: "/analysis/receivable/customerList",
		method: "POST",
		data: data
	}),

	/**
	 * 供应商对账单
	 */
	analysisPayableSupplierList: (data) => request({
		url: "/analysis/payable/supplierList",
		method: "POST",
		data: data
	}),

	/**
	 * 其他收支明细表
	 */
	analysisFlowDetailList: (data) => request({
		url: "/analysis/flow/detailList",
		method: "POST",
		data: data
	})
};

// ==================== uc包 ====================
// ==================== 客户管理 (CustomerController) ====================
const CustomerControllerApi = {
	/**
	 * 客户分页列表
	 */
	customerPage: (data) => request({
		url: "/customer/page",
		method: "POST",
		data: data
	}),

	/**
	 * 联系人列表
	 */
	customerContactList: (data) => request({
		url: "/customer/contactList",
		method: "POST",
		data: data
	}),

	/**
	 * 客户保存
	 */
	customerSave: (data) => request({
		url: "/customer/save",
		method: "POST",
		data: data
	}),

	/**
	 * 客户详情
	 */
	customerDetail: (data) => request({
		url: "/customer/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 客户删除
	 */
	customerDelete: (data) => request({
		url: "/customer/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 客户启停
	 */
	customerSwitchActive: (data) => request({
		url: "/customer/switchActive",
		method: "POST",
		data: data
	}),

	/**
	 * 导入Excel
	 */
	customerImportExcel: (data) => request({
		url: "/customer/importExcel",
		method: "POST",
		data: data
	}),

	/**
	 * 导出Excel
	 */
	customerExportExcel: (data) => request({
		url: "/customer/exportExcel",
		method: "POST",
		data: data
	})
};

// ==================== 供应商管理 (SupplierController) ====================
const SupplierControllerApi = {
	/**
	 * 供应商分页列表
	 */
	supplierPage: (data) => request({
		url: "/supplier/page",
		method: "POST",
		data: data
	}),

	/**
	 * 供应商保存
	 */
	supplierSave: (data) => request({
		url: "/supplier/save",
		method: "POST",
		data: data
	}),

	/**
	 * 供应商详情
	 */
	supplierDetail: (data) => request({
		url: "/supplier/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 供应商删除
	 */
	supplierDelete: (data) => request({
		url: "/supplier/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 供应商启停
	 */
	supplierSwitchActive: (data) => request({
		url: "/supplier/switchActive",
		method: "POST",
		data: data
	}),

	/**
	 * 导入Excel
	 */
	supplierImportExcel: (data) => request({
		url: "/supplier/importExcel",
		method: "POST",
		data: data
	}),

	/**
	 * 导出Excel
	 */
	supplierExportExcel: (data) => request({
		url: "/supplier/exportExcel",
		method: "POST",
		data: data
	})
};

// ==================== 商品管理 (ProductController) ====================
const ProductControllerApi = {
	/**
	 * 商品分页列表
	 */
	productPage: (data) => request({
		url: "/product/page",
		method: "POST",
		data: data
	}),

	/**
	 * 商品详情
	 */
	productDetail: (data) => request({
		url: "/product/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 商品保存
	 */
	productSave: (data) => request({
		url: "/product/save",
		method: "POST",
		data: data
	}),

	/**
	 * 商品删除
	 */
	productDelete: (data) => request({
		url: "/product/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 商品启停
	 */
	productSwitchActive: (data) => request({
		url: "/product/switchActive",
		method: "POST",
		data: data
	})
};

// ==================== 仓库管理 (WarehouseController) ====================
const WarehouseControllerApi = {
	/**
	 * 仓库分页列表
	 */
	warehousePage: (data) => request({
		url: "/warehouse/page",
		method: "POST",
		data: data
	}),

	/**
	 * 仓库保存
	 */
	warehouseSave: (data) => request({
		url: "/warehouse/save",
		method: "POST",
		data: data
	}),

	/**
	 * 仓库详情
	 */
	warehouseDetail: (data) => request({
		url: "/warehouse/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 仓库删除
	 */
	warehouseDelete: (data) => request({
		url: "/warehouse/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 仓库启停
	 */
	warehouseSwitchActive: (data) => request({
		url: "/warehouse/switchActive",
		method: "POST",
		data: data
	})
};

// ==================== 职员管理 (EmployeeController) ====================
const EmployeeControllerApi = {
	/**
	 * 职员分页列表
	 */
	employeePage: (data) => request({
		url: "/employee/page",
		method: "POST",
		data: data
	}),

	/**
	 * 职员保存
	 */
	employeeSave: (data) => request({
		url: "/employee/save",
		method: "POST",
		data: data
	}),

	/**
	 * 职员详情
	 */
	employeeDetail: (data) => request({
		url: "/employee/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 职员删除
	 */
	employeeDelete: (data) => request({
		url: "/employee/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 职员启停
	 */
	employeeSwitchActive: (data) => request({
		url: "/employee/switchActive",
		method: "POST",
		data: data
	})
};

// ==================== 结算账户管理 (SettlementAccountController) ====================
const SettlementAccountControllerApi = {
	/**
	 * 结算账户列表
	 */
	settlementAccountList: (data) => request({
		url: "/settlementAccount/list",
		method: "POST",
		data: data
	}),

	/**
	 * 结算账户保存
	 */
	settlementAccountSave: (data) => request({
		url: "/settlementAccount/save",
		method: "POST",
		data: data
	}),

	/**
	 * 结算账户详情
	 */
	settlementAccountDetail: (data) => request({
		url: "/settlementAccount/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 结算账户删除
	 */
	settlementAccountDelete: (data) => request({
		url: "/settlementAccount/delete",
		method: "POST",
		data: data
	})
};

// ==================== 用户管理 (UserController) ====================
const UserControllerApi = {
	/**
	 * 新增用户
	 */
	userAdd: (data) => request({
		url: "/user/add",
		method: "POST",
		data: data
	}),

	/**
	 * 用户登录
	 */
	userLogin: (data) => request({
		url: "/user/login",
		method: "POST",
		data: data
	}),

	/**
	 * 是否启用切换
	 */
	userSwitchActive: (data) => request({
		url: "/user/switchActive",
		method: "POST",
		data: data
	}),

	/**
	 * 用户分页列表
	 */
	userPage: (data) => request({
		url: "/user/page",
		method: "POST",
		data: data
	}),

	/**
	 * 重置密码
	 */
	userResetPassword: (data) => request({
		url: "/user/resetPassword",
		method: "POST",
		data: data
	})
};

// ==================== wc包 ====================
// ==================== 入库管理 (StoreController) ====================
const StoreControllerApi = {
	/**
	 * 入库单分页列表
	 */
	storePage: (data) => request({
		url: "/store/page",
		method: "POST",
		data: data
	}),

	/**
	 * 入库单详情
	 */
	storeDetail: (data) => request({
		url: "/store/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建入库单编号
	 */
	storeCreateCode: (data) => request({
		url: "/store/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 入库单保存
	 */
	storeSave: (data) => request({
		url: "/store/save",
		method: "POST",
		data: data
	}),

	/**
	 * 入库单删除
	 */
	storeDelete: (data) => request({
		url: "/store/delete",
		method: "POST",
		data: data
	})
};

// ==================== 出库管理 (CheckoutController) ====================
const CheckoutControllerApi = {
	/**
	 * 出库单分页列表
	 */
	checkoutPage: (data) => request({
		url: "/checkout/page",
		method: "POST",
		data: data
	}),

	/**
	 * 出库单详情
	 */
	checkoutDetail: (data) => request({
		url: "/checkout/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建出库单编号
	 */
	checkoutCreateCode: (data) => request({
		url: "/checkout/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 出库单保存
	 */
	checkoutSave: (data) => request({
		url: "/checkout/save",
		method: "POST",
		data: data
	}),

	/**
	 * 出库单删除
	 */
	checkoutDelete: (data) => request({
		url: "/checkout/delete",
		method: "POST",
		data: data
	})
};

// ==================== 调拨管理 (TransferController) ====================
const TransferControllerApi = {
	/**
	 * 调拨单分页列表
	 */
	transferPage: (data) => request({
		url: "/transfer/page",
		method: "POST",
		data: data
	}),

	/**
	 * 调拨单详情
	 */
	transferDetail: (data) => request({
		url: "/transfer/detail",
		method: "POST",
		data: data
	}),

	/**
	 * 创建调拨单编号
	 */
	transferCreateCode: (data) => request({
		url: "/transfer/createCode",
		method: "POST",
		data: data
	}),

	/**
	 * 调拨单保存
	 */
	transferSave: (data) => request({
		url: "/transfer/save",
		method: "POST",
		data: data
	}),

	/**
	 * 调拨单删除
	 */
	transferDelete: (data) => request({
		url: "/transfer/delete",
		method: "POST",
		data: data
	}),

	/**
	 * 调拨单切换审查
	 */
	transferSwitchCheck: (data) => request({
		url: "/transfer/switchCheck",
		method: "POST",
		data: data
	})
};

// ==================== 统一导出 ====================
const api = {
	// ==================== bc包 ====================
	// 订单管理
	...OrderControllerApi,
	// 采购管理
	...PurchaseControllerApi,
	// 销售管理
	...SaleControllerApi,

	// ==================== fc包 ====================
	// 收款管理
	...CollectionControllerApi,
	// 付款管理
	...PaymentControllerApi,
	// 收入管理
	...IncomeControllerApi,
	// 支出管理
	...ExpenseControllerApi,

	// ==================== rc包 ====================
	// 类别管理
	...CategoryControllerApi,
	// 字典管理
	...DictControllerApi,
	// 系统配置
	...SystemControllerApi,
	// 日志管理
	...LogControllerApi,
	// 菜单管理
	...MenuControllerApi,

	// ==================== sc包 ====================
	// 报表分析
	...AnalysisControllerApi,

	// ==================== uc包 ====================
	// 客户管理
	...CustomerControllerApi,
	// 供应商管理
	...SupplierControllerApi,
	// 商品管理
	...ProductControllerApi,
	// 仓库管理
	...WarehouseControllerApi,
	// 职员管理
	...EmployeeControllerApi,
	// 结算账户管理
	...SettlementAccountControllerApi,
	// 用户管理
	...UserControllerApi,

	// ==================== wc包 ====================
	// 入库管理
	...StoreControllerApi,
	// 出库管理
	...CheckoutControllerApi,
	// 调拨管理
	...TransferControllerApi,
};

export default api;