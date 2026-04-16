# BC模块 API接口文档

> **业务模块（Business Center）** - 包含客户订单、购货单、销售单相关接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 客户订单管理 (Order)](#1-客户订单管理-order)
  - [1.1 分页查询客户订单列表](#11-分页查询客户订单列表)
  - [1.2 获取客户订单详情](#12-获取客户订单详情)
  - [1.3 生成客户订单编号](#13-生成客户订单编号)
  - [1.4 保存客户订单](#14-保存客户订单)
  - [1.5 删除客户订单](#15-删除客户订单)
  - [1.6 切换客户订单审核状态](#16-切换客户订单审核状态)
- [2. 购货单管理 (Purchase)](#2-购货单管理-purchase)
  - [2.1 分页查询购货单列表](#21-分页查询购货单列表)
  - [2.2 获取购货单详情](#22-获取购货单详情)
  - [2.3 生成购货单编号](#23-生成购货单编号)
  - [2.4 保存购货单](#24-保存购货单)
  - [2.5 删除购货单](#25-删除购货单)
  - [2.6 切换购货单审核状态](#26-切换购货单审核状态)
  - [2.7 获取供应商已审核购货单列表](#27-获取供应商已审核购货单列表)
- [3. 销售单管理 (Sale)](#3-销售单管理-sale)
  - [3.1 分页查询销售单列表](#31-分页查询销售单列表)
  - [3.2 获取销售单详情](#32-获取销售单详情)
  - [3.3 生成销售单编号](#33-生成销售单编号)
  - [3.4 保存销售单](#34-保存销售单)
  - [3.5 删除销售单](#35-删除销售单)
  - [3.6 切换销售单审核状态](#36-切换销售单审核状态)
  - [3.7 获取客户已审核销售单列表](#37-获取客户已审核销售单列表)

---

## 通用说明

### 响应格式

所有接口返回统一的JSON格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 具体业务数据
  }
}
```

**响应码说明**：
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权/登录失效
- `500`: 服务器内部错误

### 通用分页参数

分页查询接口返回的数据结构：

```json
{
  "records": [],      // 数据列表
  "total": 100,       // 总记录数
  "size": 10,         // 每页大小
  "current": 1,       // 当前页码
  "pages": 10         // 总页数
}
```

### 通用查询参数

分页查询接口的 `query` 对象支持以下字段：

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期，格式：yyyy-MM-dd |
| endDate | String | 否 | 结束日期，格式：yyyy-MM-dd |
| keyword | String | 否 | 关键字搜索（根据具体业务而定） |
| 其他业务字段 | - | 否 | 根据不同模块有所差异 |

---

## 1. 客户订单管理 (Order)

**控制器路径**: `/order`

### 1.1 分页查询客户订单列表

**接口地址**: `POST /order/page`

**接口描述**: 分页查询客户订单列表，支持按日期范围筛选

**请求参数**:

```json
{
  "query": {
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.startDate | String | 否 | - | 开始日期，格式：yyyy-MM-dd |
| query.endDate | String | 否 | - | 结束日期，格式：yyyy-MM-dd |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderPage": {
      "records": [
        {
          "id": "订单ID",
          "issueDate": "单据日期",
          "deliveryDate": "交货日期",
          "code": "单据编号",
          "businessType": 1,
          "customerId": "客户ID",
          "customerName": "客户名称",
          "totalAmount": 1000.00,
          "discountedAmount": 950.00,
          "quantity": 10.0,
          "discountRate": 0.05,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "auditorId": "审核人ID",
          "auditorName": "审核人姓名",
          "checked": false,
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 100,
      "size": 10,
      "current": 1,
      "pages": 10
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 订单ID |
| issueDate | String | 单据日期 |
| deliveryDate | String | 交货日期 |
| code | String | 单据编号 |
| businessType | Integer | 业务类型 |
| customerId | String | 客户ID |
| customerName | String | 客户名称（关联查询） |
| totalAmount | Double | 总金额 |
| discountedAmount | Double | 优惠后金额 |
| quantity | Double | 数量 |
| discountRate | Double | 优惠率 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| auditorId | String | 审核人ID |
| auditorName | String | 审核人姓名（关联查询） |
| checked | Boolean | 审核状态 |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 1.2 获取客户订单详情

**接口地址**: `POST /order/detail`

**接口描述**: 根据订单ID获取客户订单详细信息，包含商品明细列表

**请求参数**:

```json
{
  "orderId": "订单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 客户订单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "order": {
      "id": "订单ID",
      "issueDate": "单据日期",
      "deliveryDate": "交货日期",
      "code": "单据编号",
      "businessType": 1,
      "customerId": "客户ID",
      "totalAmount": 1000.00,
      "discountedAmount": 950.00,
      "quantity": 10.0,
      "discountRate": 0.05,
      "listerId": "制单人ID",
      "auditorId": "审核人ID",
      "checked": false,
      "remark": "备注",
      "productList": [
        {
          "id": "商品明细ID",
          "businessType": 1,
          "businessId": "订单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "warehouseId": "仓库ID",
          "warehouseName": "仓库名称",
          "quantity": 5.0,
          "price": 100.00,
          "discountRate": 0.05,
          "discountAmount": 25.00,
          "amount": 475.00,
          "unitName": "单位",
          "remark": "备注"
        }
      ]
    }
  }
}
```

**商品明细字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 商品明细ID |
| businessType | Integer | 业务类型 |
| businessId | String | 业务ID（订单ID） |
| productId | String | 商品ID |
| productName | String | 商品名称（关联查询） |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称（关联查询） |
| quantity | Double | 数量 |
| price | Double | 单价 |
| discountRate | Double | 优惠率 |
| discountAmount | Double | 优惠金额 |
| amount | Double | 金额 |
| unitName | String | 单位名称（关联查询） |
| remark | String | 备注 |

---

### 1.3 生成客户订单编号

**接口地址**: `POST /order/createCode`

**接口描述**: 自动生成唯一的客户订单编号，格式：CO + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "CO20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`CO` (Customer Order)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 1.4 保存客户订单

**接口地址**: `POST /order/save`

**接口描述**: 新增或更新客户订单，包含订单基本信息和商品明细列表

**请求参数**:

```json
{
  "order": {
    "id": "订单ID（更新时必填，新增时不传）",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "deliveryDate": "2024-01-15",
    "businessType": 1,
    "customerId": "客户ID",
    "totalAmount": 1000.00,
    "discountedAmount": 950.00,
    "discountRate": 0.05,
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "productId": "商品ID",
      "warehouseId": "仓库ID",
      "quantity": 5.0,
      "price": 100.00,
      "discountRate": 0.05,
      "discountAmount": 25.00,
      "amount": 475.00,
      "remark": "商品备注"
    }
  ]
}
```

**order对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 订单ID，新增时不传，更新时必填 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| deliveryDate | String | 是 | 交货日期 |
| businessType | Integer | 是 | 业务类型 |
| customerId | String | 是 | 客户ID |
| totalAmount | Double | 是 | 总金额 |
| discountedAmount | Double | 是 | 优惠后金额 |
| discountRate | Double | 否 | 优惠率 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |
| warehouseId | String | 是 | 仓库ID |
| quantity | Double | 是 | 数量 |
| price | Double | 是 | 单价 |
| discountRate | Double | 否 | 优惠率 |
| discountAmount | Double | 否 | 优惠金额 |
| amount | Double | 是 | 金额 |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "order": {
      "id": "订单ID",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "deliveryDate": "2024-01-15",
      "businessType": 1,
      "customerId": "客户ID",
      "totalAmount": 1000.00,
      "discountedAmount": 950.00,
      "quantity": 5.0,
      "discountRate": 0.05,
      "listerId": "制单人ID",
      "checked": false,
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增订单时，系统会自动设置 `checked=false`（未审核状态）
2. 更新订单时，会先删除原有的商品明细，再保存新的商品明细
3. 系统会自动计算订单总数量（productList中所有商品数量之和）
4. 需要校验客户ID是否存在
5. 需要校验商品ID和仓库ID是否存在

---

### 1.5 删除客户订单

**接口地址**: `POST /order/delete`

**接口描述**: 根据订单ID删除客户订单及其关联的商品明细

**请求参数**:

```json
{
  "orderId": "订单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 客户订单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除订单主表记录
2. 同时删除该订单关联的所有商品明细记录
3. 使用事务保证数据一致性

---

### 1.6 切换客户订单审核状态

**接口地址**: `POST /order/switchCheck`

**接口描述**: 切换客户订单的审核状态（已审核 ↔ 未审核）

**请求参数**:

```json
{
  "orderId": "订单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderId | String | 是 | 客户订单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "order": {
      "id": "订单ID",
      "code": "单据编号",
      "checked": true,
      "auditorId": "审核人ID",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 如果当前状态为未审核，则切换为已审核
2. 如果当前状态为已审核，则切换为未审核
3. 返回更新后的订单信息

---

## 2. 购货单管理 (Purchase)

**控制器路径**: `/purchase`

### 2.1 分页查询购货单列表

**接口地址**: `POST /purchase/page`

**接口描述**: 分页查询购货单列表，支持按类型和日期范围筛选

**请求参数**:

```json
{
  "query": {
    "type": "buy",
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.type | String | 否 | - | 购货类型：buy-采购，refund-采购退货 |
| query.startDate | String | 否 | - | 开始日期，格式：yyyy-MM-dd |
| query.endDate | String | 否 | - | 结束日期，格式：yyyy-MM-dd |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "purchasePage": {
      "records": [
        {
          "id": "购货单ID",
          "type": "buy",
          "supplierId": "供应商ID",
          "supplierName": "供应商名称",
          "issueDate": "单据日期",
          "code": "单据编号",
          "quantity": 100.0,
          "discountAmount": 50.00,
          "amount": 10000.00,
          "preferentialRate": 0.05,
          "preferentialAmount": 500.00,
          "preferredAmount": 9450.00,
          "currentAmount": 5000.00,
          "debtAmount": 4450.00,
          "contracts": "[{\"name\":\"合同.pdf\",\"url\":\"...\"}]",
          "status": 10,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "auditorId": "审核人ID",
          "auditorName": "审核人姓名",
          "checked": false,
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 50,
      "size": 10,
      "current": 1,
      "pages": 5
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 购货单ID |
| type | String | 类型：buy-采购，refund-采购退货 |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称（关联查询） |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| quantity | Double | 数量 |
| discountAmount | Double | 折扣额 |
| amount | Double | 购货金额 |
| preferentialRate | Double | 优惠率 |
| preferentialAmount | Double | 优惠金额 |
| preferredAmount | Double | 优惠后金额 |
| currentAmount | Double | 本次付/退款金额 |
| debtAmount | Double | 欠款金额 |
| contracts | String | 采购合同（JSON数组字符串） |
| status | Integer | 付款状态：10-未付/退款，20-部分付/退，30-全部付/退 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| auditorId | String | 审核人ID |
| auditorName | String | 审核人姓名（关联查询） |
| checked | Boolean | 审核状态 |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 2.2 获取购货单详情

**接口地址**: `POST /purchase/detail`

**接口描述**: 根据购货单ID获取购货单详细信息，包含商品明细和账户记录

**请求参数**:

```json
{
  "purchaseId": "购货单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| purchaseId | String | 是 | 购货单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "purchase": {
      "id": "购货单ID",
      "type": "buy",
      "supplierId": "供应商ID",
      "issueDate": "单据日期",
      "code": "单据编号",
      "quantity": 100.0,
      "discountAmount": 50.00,
      "amount": 10000.00,
      "preferentialRate": 0.05,
      "preferentialAmount": 500.00,
      "preferredAmount": 9450.00,
      "currentAmount": 5000.00,
      "debtAmount": 4450.00,
      "contracts": "[{\"name\":\"合同.pdf\",\"url\":\"...\"}]",
      "status": 10,
      "listerId": "制单人ID",
      "auditorId": "审核人ID",
      "checked": false,
      "remark": "备注",
      "productList": [
        {
          "id": "商品明细ID",
          "code": "商品编码",
          "issueDate": "单据日期",
          "businessType": "buy",
          "businessId": "购货单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "warehouseId": "仓库ID",
          "warehouseName": "仓库名称",
          "quantity": 50.0,
          "price": 100.00,
          "discountRate": 0.05,
          "discountAmount": 250.00,
          "amount": 4750.00,
          "unitName": "单位",
          "remark": "备注"
        }
      ],
      "accountList": [
        {
          "id": "账户记录ID",
          "accountId": "结算账户ID",
          "amount": 5000.00,
          "type": "out",
          "issueDate": "单据日期",
          "businessType": "buy",
          "businessId": "购货单ID",
          "remark": "备注"
        }
      ]
    }
  }
}
```

**商品明细字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 商品明细ID |
| code | String | 商品编码 |
| issueDate | String | 单据日期 |
| businessType | String | 业务类型 |
| businessId | String | 业务ID（购货单ID） |
| productId | String | 商品ID |
| productName | String | 商品名称（关联查询） |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称（关联查询） |
| quantity | Double | 数量 |
| price | Double | 单价 |
| discountRate | Double | 优惠率 |
| discountAmount | Double | 优惠金额 |
| amount | Double | 金额 |
| unitName | String | 单位名称（关联查询） |
| remark | String | 备注 |

**账户记录字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 账户记录ID |
| accountId | String | 结算账户ID |
| amount | Double | 金额 |
| type | String | 类型：in-收入，out-支出 |
| issueDate | String | 单据日期 |
| businessType | String | 业务类型 |
| businessId | String | 业务ID |
| remark | String | 备注 |

---

### 2.3 生成购货单编号

**接口地址**: `POST /purchase/createCode`

**接口描述**: 自动生成唯一的购货单编号，格式：PL + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "PL20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`PL` (Purchase List)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 2.4 保存购货单

**接口地址**: `POST /purchase/save`

**接口描述**: 新增或更新购货单，包含购货单基本信息、商品明细列表和账户记录列表

**请求参数**:

```json
{
  "purchase": {
    "id": "购货单ID（更新时必填，新增时不传）",
    "type": "buy",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "supplierId": "供应商ID",
    "quantity": 100.0,
    "discountAmount": 50.00,
    "amount": 10000.00,
    "preferentialRate": 0.05,
    "preferentialAmount": 500.00,
    "preferredAmount": 9450.00,
    "currentAmount": 5000.00,
    "debtAmount": 4450.00,
    "contracts": "[{\"name\":\"合同.pdf\",\"url\":\"...\"}]",
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "code": "商品编码",
      "productId": "商品ID",
      "warehouseId": "仓库ID",
      "quantity": 50.0,
      "price": 100.00,
      "discountRate": 0.05,
      "discountAmount": 250.00,
      "amount": 4750.00,
      "remark": "商品备注"
    }
  ],
  "accountList": [
    {
      "accountId": "结算账户ID",
      "remark": "账户备注"
    }
  ]
}
```

**purchase对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 购货单ID，新增时不传，更新时必填 |
| type | String | 是 | 类型：buy-采购，refund-采购退货 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| supplierId | String | 是 | 供应商ID |
| quantity | Double | 是 | 数量 |
| discountAmount | Double | 是 | 折扣额 |
| amount | Double | 是 | 购货金额 |
| preferentialRate | Double | 否 | 优惠率 |
| preferentialAmount | Double | 否 | 优惠金额 |
| preferredAmount | Double | 是 | 优惠后金额 |
| currentAmount | Double | 是 | 本次付/退款金额 |
| debtAmount | Double | 是 | 欠款金额 |
| contracts | String | 否 | 采购合同（JSON数组字符串） |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| code | String | 否 | 商品编码 |
| productId | String | 是 | 商品ID |
| warehouseId | String | 是 | 仓库ID |
| quantity | Double | 是 | 数量 |
| price | Double | 是 | 单价 |
| discountRate | Double | 否 | 优惠率 |
| discountAmount | Double | 否 | 优惠金额 |
| amount | Double | 是 | 金额 |
| remark | String | 否 | 备注 |

**accountList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountId | String | 是 | 结算账户ID |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "purchase": {
      "id": "购货单ID",
      "type": "buy",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "supplierId": "供应商ID",
      "quantity": 100.0,
      "discountAmount": 50.00,
      "amount": 10000.00,
      "preferentialRate": 0.05,
      "preferentialAmount": 500.00,
      "preferredAmount": 9450.00,
      "currentAmount": 5000.00,
      "debtAmount": 4450.00,
      "contracts": "[{\"name\":\"合同.pdf\",\"url\":\"...\"}]",
      "status": 10,
      "listerId": "制单人ID",
      "checked": false,
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增购货单时，系统会自动设置 `checked=false`（未审核状态），`status=10`（未付款状态）
2. 更新购货单时，会先删除原有的商品明细和账户记录，再保存新的数据
3. 系统会自动处理库存：
   - 采购类型（buy）：增加库存
   - 采购退货类型（refund）：减少库存
4. 系统会自动处理应付账款：
   - 采购类型：增加应付账款
   - 采购退货类型：减少应付账款
5. 系统会自动创建账户记录，类型为：
   - 采购类型：out（支出）
   - 采购退货类型：in（收入）
6. 需要校验供应商ID、商品ID和仓库ID是否存在
7. 新增时会校验单据编号是否已存在

---

### 2.5 删除购货单

**接口地址**: `POST /purchase/delete`

**接口描述**: 根据购货单ID删除购货单及其关联的商品明细和账户记录

**请求参数**:

```json
{
  "purchaseId": "购货单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| purchaseId | String | 是 | 购货单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除购货单主表记录
2. 同时删除该购货单关联的所有商品明细记录
3. 同时删除该购货单关联的所有账户记录
4. 使用事务保证数据一致性

---

### 2.6 切换购货单审核状态

**接口地址**: `POST /purchase/switchCheck`

**接口描述**: 切换购货单的审核状态（已审核 ↔ 未审核）

**请求参数**:

```json
{
  "purchaseId": "购货单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| purchaseId | String | 是 | 购货单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "purchase": {
      "id": "购货单ID",
      "code": "单据编号",
      "checked": true,
      "auditorId": "审核人ID",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 如果当前状态为未审核，则切换为已审核
2. 如果当前状态为已审核，则切换为未审核
3. 返回更新后的购货单信息

---

### 2.7 获取供应商已审核购货单列表

**接口地址**: `POST /purchase/findCheckedListBySupplier`

**接口描述**: 根据供应商ID获取该供应商所有已审核的购货单列表

**请求参数**:

```json
{
  "supplierId": "供应商ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| supplierId | String | 是 | 供应商ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "purchaseList": [
      {
        "id": "购货单ID",
        "type": "buy",
        "supplierId": "供应商ID",
        "issueDate": "单据日期",
        "code": "单据编号",
        "amount": 10000.00,
        "debtAmount": 4450.00,
        "checked": true,
        "verifiedAmount": 0,
        "unverifiedAmount": 0
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 购货单ID |
| type | String | 类型：buy-采购，refund-采购退货 |
| supplierId | String | 供应商ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| amount | Double | 购货金额 |
| debtAmount | Double | 欠款金额 |
| checked | Boolean | 审核状态（固定为true） |
| verifiedAmount | Integer | 已核销金额（预留字段，当前为0） |
| unverifiedAmount | Integer | 未核销金额（预留字段，当前为0） |

---

## 3. 销售单管理 (Sale)

**控制器路径**: `/sale`

### 3.1 分页查询销售单列表

**接口地址**: `POST /sale/page`

**接口描述**: 分页查询销售单列表，支持按日期范围筛选

**请求参数**:

```json
{
  "query": {
    "startDate": "2024-01-01",
    "endDate": "2024-12-31"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.startDate | String | 否 | - | 开始日期，格式：yyyy-MM-dd |
| query.endDate | String | 否 | - | 结束日期，格式：yyyy-MM-dd |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "salePage": {
      "records": [
        {
          "id": "销售单ID",
          "type": "sell",
          "issueDate": "单据日期",
          "code": "单据编号",
          "customerId": "客户ID",
          "customerName": "客户名称",
          "sellerId": "销售员ID",
          "sellerName": "销售员姓名",
          "contactName": "联系人姓名",
          "address": "地址",
          "phone": "电话号码",
          "quantity": 50.0,
          "discountAmount": 100.00,
          "amount": 5000.00,
          "preferentialRate": 0.05,
          "preferentialAmount": 250.00,
          "preferredAmount": 4650.00,
          "customerFee": 50.00,
          "currentAmount": 3000.00,
          "debtAmount": 1650.00,
          "status": 20,
          "attachments": "[{\"name\":\"附件.pdf\",\"url\":\"...\"}]",
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "auditorId": "审核人ID",
          "auditorName": "审核人姓名",
          "checked": false,
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 80,
      "size": 10,
      "current": 1,
      "pages": 8
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 销售单ID |
| type | String | 类型：sell-售货，returned-退货 |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| customerId | String | 客户ID |
| customerName | String | 客户名称（关联查询） |
| sellerId | String | 销售员ID（职员ID） |
| sellerName | String | 销售员姓名（关联查询） |
| contactName | String | 联系人姓名 |
| address | String | 地址 |
| phone | String | 电话号码 |
| quantity | Double | 数量 |
| discountAmount | Double | 折扣金额 |
| amount | Double | 金额 |
| preferentialRate | Double | 优惠率 |
| preferentialAmount | Double | 优惠金额 |
| preferredAmount | Double | 优惠后金额 |
| customerFee | Double | 客户费用 |
| currentAmount | Double | 本次收款金额 |
| debtAmount | Double | 本次欠款金额 |
| status | Integer | 收款状态：10-未收/退款，20-部分收/退，30-全部收/退 |
| attachments | String | 销售附件（JSON数组字符串） |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| auditorId | String | 审核人ID |
| auditorName | String | 审核人姓名（关联查询） |
| checked | Boolean | 审核状态 |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 3.2 获取销售单详情

**接口地址**: `POST /sale/detail`

**接口描述**: 根据销售单ID获取销售单详细信息，包含商品明细列表

**请求参数**:

```json
{
  "saleId": "销售单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| saleId | String | 是 | 销售单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sale": {
      "id": "销售单ID",
      "type": "sell",
      "issueDate": "单据日期",
      "code": "单据编号",
      "customerId": "客户ID",
      "sellerId": "销售员ID",
      "contactName": "联系人姓名",
      "address": "地址",
      "phone": "电话号码",
      "quantity": 50.0,
      "discountAmount": 100.00,
      "amount": 5000.00,
      "preferentialRate": 0.05,
      "preferentialAmount": 250.00,
      "preferredAmount": 4650.00,
      "customerFee": 50.00,
      "currentAmount": 3000.00,
      "debtAmount": 1650.00,
      "status": 20,
      "attachments": "[{\"name\":\"附件.pdf\",\"url\":\"...\"}]",
      "listerId": "制单人ID",
      "auditorId": "审核人ID",
      "checked": false,
      "remark": "备注",
      "productList": [
        {
          "id": "商品明细ID",
          "issueDate": "单据日期",
          "businessType": "sell",
          "businessId": "销售单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "warehouseId": "仓库ID",
          "warehouseName": "仓库名称",
          "quantity": 25.0,
          "price": 100.00,
          "discountRate": 0.05,
          "discountAmount": 125.00,
          "amount": 2375.00,
          "code": "商品编码",
          "unitName": "单位",
          "remark": "备注"
        }
      ]
    }
  }
}
```

**商品明细字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 商品明细ID |
| issueDate | String | 单据日期 |
| businessType | String | 业务类型 |
| businessId | String | 业务ID（销售单ID） |
| productId | String | 商品ID |
| productName | String | 商品名称（关联查询） |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称（关联查询） |
| quantity | Double | 数量 |
| price | Double | 单价 |
| discountRate | Double | 优惠率 |
| discountAmount | Double | 优惠金额 |
| amount | Double | 金额 |
| code | String | 商品编码 |
| unitName | String | 单位名称（关联查询） |
| remark | String | 备注 |

---

### 3.3 生成销售单编号

**接口地址**: `POST /sale/createCode`

**接口描述**: 根据销售类型自动生成唯一的销售单编号

**请求参数**:

```json
{
  "type": "sell"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | String | 是 | 销售类型：sell-售货，returned-退货 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "SE20240101120000000123"
  }
}
```

**编号规则**: 
- 售货类型：`SE` (Sale) + 17位时间戳 + 2位随机数字
- 退货类型：`RE` (Return) + 17位时间戳 + 2位随机数字

**示例**:
- 售货单：`SE20240101120000000123`
- 退货单：`RE20240101120000000456`

---

### 3.4 保存销售单

**接口地址**: `POST /sale/save`

**接口描述**: 新增或更新销售单，包含销售单基本信息、商品明细列表和账户记录列表

**请求参数**:

```json
{
  "sale": {
    "id": "销售单ID（更新时必填，新增时不传）",
    "type": "sell",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "customerId": "客户ID",
    "sellerId": "销售员ID",
    "contactName": "联系人姓名",
    "address": "送货地址",
    "phone": "联系电话",
    "quantity": 50.0,
    "discountAmount": 100.00,
    "amount": 5000.00,
    "preferentialRate": 0.05,
    "preferentialAmount": 250.00,
    "preferredAmount": 4650.00,
    "customerFee": 50.00,
    "currentAmount": 3000.00,
    "debtAmount": 1650.00,
    "attachments": "[{\"name\":\"附件.pdf\",\"url\":\"...\"}]",
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "productId": "商品ID",
      "warehouseId": "仓库ID",
      "quantity": 25.0,
      "price": 100.00,
      "discountRate": 0.05,
      "discountAmount": 125.00,
      "amount": 2375.00,
      "code": "商品编码",
      "remark": "商品备注"
    }
  ],
  "accountList": [
    {
      "accountId": "结算账户ID",
      "remark": "账户备注"
    }
  ]
}
```

**sale对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 销售单ID，新增时不传，更新时必填 |
| type | String | 是 | 类型：sell-售货，returned-退货 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| customerId | String | 是 | 客户ID |
| sellerId | String | 是 | 销售员ID（职员ID） |
| contactName | String | 否 | 联系人姓名 |
| address | String | 否 | 地址 |
| phone | String | 否 | 电话号码 |
| quantity | Double | 是 | 数量 |
| discountAmount | Double | 是 | 折扣金额 |
| amount | Double | 是 | 金额 |
| preferentialRate | Double | 否 | 优惠率 |
| preferentialAmount | Double | 否 | 优惠金额 |
| preferredAmount | Double | 是 | 优惠后金额 |
| customerFee | Double | 否 | 客户费用 |
| currentAmount | Double | 是 | 本次收款金额 |
| debtAmount | Double | 是 | 本次欠款金额 |
| attachments | String | 否 | 销售附件（JSON数组字符串） |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |
| warehouseId | String | 是 | 仓库ID |
| quantity | Double | 是 | 数量 |
| price | Double | 是 | 单价 |
| discountRate | Double | 否 | 优惠率 |
| discountAmount | Double | 否 | 优惠金额 |
| amount | Double | 是 | 金额 |
| code | String | 否 | 商品编码 |
| remark | String | 否 | 备注 |

**accountList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountId | String | 是 | 结算账户ID |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sale": {
      "id": "销售单ID",
      "type": "sell",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "customerId": "客户ID",
      "sellerId": "销售员ID",
      "contactName": "联系人姓名",
      "address": "送货地址",
      "phone": "联系电话",
      "quantity": 50.0,
      "discountAmount": 100.00,
      "amount": 5000.00,
      "preferentialRate": 0.05,
      "preferentialAmount": 250.00,
      "preferredAmount": 4650.00,
      "customerFee": 50.00,
      "currentAmount": 3000.00,
      "debtAmount": 1650.00,
      "status": 20,
      "attachments": "[{\"name\":\"附件.pdf\",\"url\":\"...\"}]",
      "listerId": "制单人ID",
      "checked": false,
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增销售单时，系统会自动设置 `checked=false`（未审核状态），`status=20`（部分收款状态）
2. 更新销售单时，会先删除原有的商品明细，再保存新的数据
3. 系统会自动处理库存：
   - 售货类型（sell）：减少库存
   - 退货类型（returned）：增加库存
4. 系统会自动处理应收账款：
   - 售货类型：增加应收账款
   - 退货类型：减少应收账款
5. 系统会自动创建账户记录，类型为：
   - 售货类型：in（收入）
   - 退货类型：out（支出）
6. 需要校验客户ID、销售员ID、商品ID和仓库ID是否存在
7. 新增时会校验单据编号是否已存在

---

### 3.5 删除销售单

**接口地址**: `POST /sale/delete`

**接口描述**: 根据销售单ID删除销售单及其关联的商品明细

**请求参数**:

```json
{
  "saleId": "销售单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| saleId | String | 是 | 销售单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除销售单主表记录
2. 同时删除该销售单关联的所有商品明细记录
3. 注意：不会删除账户记录（与购货单不同）

---

### 3.6 切换销售单审核状态

**接口地址**: `POST /sale/switchCheck`

**接口描述**: 切换销售单的审核状态（已审核 ↔ 未审核）

**请求参数**:

```json
{
  "saleId": "销售单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| saleId | String | 是 | 销售单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sale": {
      "id": "销售单ID",
      "code": "单据编号",
      "checked": true,
      "auditorId": "审核人ID",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 如果当前状态为未审核，则切换为已审核
2. 如果当前状态为已审核，则切换为未审核
3. 返回更新后的销售单信息

---

### 3.7 获取客户已审核销售单列表

**接口地址**: `POST /sale/findCheckedListByCustomer`

**接口描述**: 根据客户ID获取该客户所有已审核的销售单列表

**请求参数**:

```json
{
  "customerId": "客户ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| customerId | String | 是 | 客户ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "saleList": [
      {
        "id": "销售单ID",
        "type": "sell",
        "customerId": "客户ID",
        "issueDate": "单据日期",
        "code": "单据编号",
        "amount": 5000.00,
        "debtAmount": 1650.00,
        "checked": true,
        "verifiedAmount": 0,
        "unverifiedAmount": 0
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 销售单ID |
| type | String | 类型：sell-售货，returned-退货 |
| customerId | String | 客户ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| amount | Double | 金额 |
| debtAmount | Double | 欠款金额 |
| checked | Boolean | 审核状态（固定为true） |
| verifiedAmount | Integer | 已核销金额（预留字段，当前为0） |
| unverifiedAmount | Integer | 未核销金额（预留字段，当前为0） |

---

## 附录

### A. 业务类型常量说明

#### 客户订单业务类型 (businessType)
- 具体值需参考后端 `Define` 常量类

#### 购货单类型 (type)
- `buy`: 采购
- `refund`: 采购退货

#### 销售单类型 (type)
- `sell`: 售货
- `returned`: 退货

#### 付款/收款状态 (status)
- `10`: 未付/未收
- `20`: 部分付/收
- `30`: 全部付/收

#### 账户记录类型
- `in`: 收入/进账
- `out`: 支出/出账

### B. 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数是否符合要求 |
| 401 | 未授权 | 检查登录状态，重新登录 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### C. 注意事项

1. **日期格式**: 所有日期字段均使用 `yyyy-MM-dd` 格式
2. **金额精度**: 金额字段为Double类型，建议前端保留两位小数
3. **事务处理**: 保存和删除操作均使用事务，保证数据一致性
4. **权限控制**: 部分接口可能需要特定权限，请确保用户有相应权限
5. **数据校验**: 后端会对关键数据进行校验，如ID是否存在、编号是否重复等
6. **库存影响**: 购货单和销售单的保存操作会影响库存，请注意业务逻辑
7. **应收应付**: 购货单影响应付账款，销售单影响应收账款

### D. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含客户订单、购货单、销售单所有接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
