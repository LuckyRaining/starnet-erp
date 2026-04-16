# FC模块 API接口文档

> **财务模块（Financial Center）** - 包含收款单、付款单、收入单、支出单相关接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 收款单管理 (Collection)](#1-收款单管理-collection)
  - [1.1 分页查询收款单列表](#11-分页查询收款单列表)
  - [1.2 获取收款单详情](#12-获取收款单详情)
  - [1.3 生成收款单编号](#13-生成收款单编号)
  - [1.4 保存收款单](#14-保存收款单)
  - [1.5 删除收款单](#15-删除收款单)
- [2. 付款单管理 (Payment)](#2-付款单管理-payment)
  - [2.1 分页查询付款单列表](#21-分页查询付款单列表)
  - [2.2 获取付款单详情](#22-获取付款单详情)
  - [2.3 生成付款单编号](#23-生成付款单编号)
  - [2.4 保存付款单](#24-保存付款单)
  - [2.5 删除付款单](#25-删除付款单)
- [3. 收入单管理 (Income)](#3-收入单管理-income)
  - [3.1 分页查询收入单列表](#31-分页查询收入单列表)
  - [3.2 获取收入单详情](#32-获取收入单详情)
  - [3.3 生成收入单编号](#33-生成收入单编号)
  - [3.4 保存收入单](#34-保存收入单)
  - [3.5 删除收入单](#35-删除收入单)
- [4. 支出单管理 (Expense)](#4-支出单管理-expense)
  - [4.1 分页查询支出单列表](#41-分页查询支出单列表)
  - [4.2 获取支出单详情](#42-获取支出单详情)
  - [4.3 生成支出单编号](#43-生成支出单编号)
  - [4.4 保存支出单](#44-保存支出单)
  - [4.5 删除支出单](#45-删除支出单)

---

## 通用说明

### 响应格式

所有接口返回统一的JSON格式：

```json5
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

```json5
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

---

## 1. 收款单管理 (Collection)

**控制器路径**: `/collection`

### 1.1 分页查询收款单列表

**接口地址**: `POST /collection/page`

**接口描述**: 分页查询收款单列表，支持按日期范围筛选

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
    "collectionPage": {
      "records": [
        {
          "id": "收款单ID",
          "issueDate": "单据日期",
          "code": "单据编号",
          "customerId": "客户ID",
          "customerName": "客户名称",
          "collectAmount": 5000.00,
          "issueAmount": 5000.00,
          "discountAmount": 100.00,
          "verifiedAmount": 3000.00,
          "unverifiedAmount": 2000.00,
          "currentVerifiedAmount": 3000.00,
          "advanceCollectAmount": 0.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
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
| id | String | 收款单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| customerId | String | 客户ID |
| customerName | String | 客户名称（关联查询） |
| collectAmount | Double | 收款金额 |
| issueAmount | Double | 单据金额 |
| discountAmount | Double | 折扣金额 |
| verifiedAmount | Double | 已核销金额 |
| unverifiedAmount | Double | 未核销金额 |
| currentVerifiedAmount | Double | 本次核销金额 |
| advanceCollectAmount | Double | 预收款金额 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 1.2 获取收款单详情

**接口地址**: `POST /collection/detail`

**接口描述**: 根据收款单ID获取收款单详细信息，包含账户记录和核销单据列表

**请求参数**:

```json
{
  "collectionId": "收款单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| collectionId | String | 是 | 收款单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "collection": {
      "id": "收款单ID",
      "issueDate": "单据日期",
      "code": "单据编号",
      "customerId": "客户ID",
      "collectAmount": 5000.00,
      "issueAmount": 5000.00,
      "discountAmount": 100.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 2000.00,
      "currentVerifiedAmount": 3000.00,
      "advanceCollectAmount": 0.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "accountList": [
        {
          "id": "账户记录ID",
          "accountId": "结算账户ID",
          "amount": 5000.00,
          "type": "in",
          "issueDate": "单据日期",
          "businessType": "collection",
          "businessId": "收款单ID",
          "remark": "备注"
        }
      ],
      "issueList": [
        {
          "id": "核销明细ID",
          "collectionId": "收款单ID",
          "sourceCode": "源单据编号",
          "type": "sell",
          "issueDate": "单据日期",
          "issueAmount": 3000.00,
          "verifiedAmount": 3000.00,
          "unverifiedAmount": 0.00,
          "currentVerifiedAmount": 3000.00
        }
      ]
    }
  }
}
```

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

**核销明细字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 核销明细ID |
| collectionId | String | 收款单ID |
| sourceCode | String | 源单据编号（如销售单编号） |
| type | String | 源单据类型 |
| issueDate | String | 源单据日期 |
| issueAmount | Double | 源单据金额 |
| verifiedAmount | Double | 已核销金额 |
| unverifiedAmount | Double | 未核销金额 |
| currentVerifiedAmount | Double | 本次核销金额 |

---

### 1.3 生成收款单编号

**接口地址**: `POST /collection/createCode`

**接口描述**: 自动生成唯一的收款单编号，格式：CL + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "CL20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`CL` (CoLlection)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 1.4 保存收款单

**接口地址**: `POST /collection/save`

**接口描述**: 新增或更新收款单，包含收款单基本信息、核销单据列表和账户记录列表

**请求参数**:

```json
{
  "collection": {
    "id": "收款单ID（更新时必填，新增时不传）",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "customerId": "客户ID",
    "collectAmount": 5000.00,
    "issueAmount": 5000.00,
    "discountAmount": 100.00,
    "verifiedAmount": 3000.00,
    "unverifiedAmount": 2000.00,
    "currentVerifiedAmount": 3000.00,
    "advanceCollectAmount": 0.00,
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "issueList": [
    {
      "sourceCode": "SE202401010000000001",
      "type": "sell",
      "issueDate": "2024-01-01",
      "issueAmount": 3000.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 0.00,
      "currentVerifiedAmount": 3000.00
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

**collection对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 收款单ID，新增时不传，更新时必填 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| customerId | String | 是 | 客户ID |
| collectAmount | Double | 是 | 收款金额 |
| issueAmount | Double | 是 | 单据金额 |
| discountAmount | Double | 否 | 折扣金额 |
| verifiedAmount | Double | 是 | 已核销金额 |
| unverifiedAmount | Double | 是 | 未核销金额 |
| currentVerifiedAmount | Double | 是 | 本次核销金额 |
| advanceCollectAmount | Double | 否 | 预收款金额 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**issueList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sourceCode | String | 是 | 源单据编号（如销售单编号） |
| type | String | 是 | 源单据类型 |
| issueDate | String | 是 | 源单据日期 |
| issueAmount | Double | 是 | 源单据金额 |
| verifiedAmount | Double | 是 | 已核销金额 |
| unverifiedAmount | Double | 是 | 未核销金额 |
| currentVerifiedAmount | Double | 是 | 本次核销金额 |

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
    "collection": {
      "id": "收款单ID",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "customerId": "客户ID",
      "collectAmount": 5000.00,
      "issueAmount": 5000.00,
      "discountAmount": 100.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 2000.00,
      "currentVerifiedAmount": 3000.00,
      "advanceCollectAmount": 0.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增收款单时，系统会校验单据编号是否已存在
2. 更新收款单时，会先删除原有的账户记录和核销单据，再保存新的数据
3. 系统会自动创建账户记录，类型为 `in`（收入）
4. 系统会自动处理应收账款：
   - 根据预收款金额更新客户的应收账款
5. 需要校验客户ID是否存在
6. 核销单据列表用于记录该收款单核销了哪些源单据（如销售单）

---

### 1.5 删除收款单

**接口地址**: `POST /collection/delete`

**接口描述**: 根据收款单ID删除收款单及其关联的核销单据和账户记录

**请求参数**:

```json
{
  "collectionId": "收款单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| collectionId | String | 是 | 收款单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除收款单主表记录
2. 同时删除该收款单关联的所有核销单据记录
3. 同时删除该收款单关联的所有账户记录
4. 使用事务保证数据一致性

---

## 2. 付款单管理 (Payment)

**控制器路径**: `/payment`

### 2.1 分页查询付款单列表

**接口地址**: `POST /payment/page`

**接口描述**: 分页查询付款单列表，支持按日期范围筛选

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
    "paymentPage": {
      "records": [
        {
          "id": "付款单ID",
          "issueDate": "单据日期",
          "code": "单据编号",
          "supplierId": "供应商ID",
          "supplierName": "供应商名称",
          "paidAmount": 5000.00,
          "issueAmount": 5000.00,
          "discountAmount": 100.00,
          "verifiedAmount": 3000.00,
          "unverifiedAmount": 2000.00,
          "currentVerifiedAmount": 3000.00,
          "advancePaidAmount": 0.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 40,
      "size": 10,
      "current": 1,
      "pages": 4
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 付款单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称（关联查询） |
| paidAmount | Double | 付款金额 |
| issueAmount | Double | 单据金额 |
| discountAmount | Double | 折扣金额 |
| verifiedAmount | Double | 已核销金额 |
| unverifiedAmount | Double | 未核销金额 |
| currentVerifiedAmount | Double | 本次核销金额 |
| advancePaidAmount | Double | 预付款金额 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 2.2 获取付款单详情

**接口地址**: `POST /payment/detail`

**接口描述**: 根据付款单ID获取付款单详细信息，包含账户记录和核销单据列表

**请求参数**:

```json
{
  "paymentId": "付款单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| paymentId | String | 是 | 付款单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "payment": {
      "id": "付款单ID",
      "issueDate": "单据日期",
      "code": "单据编号",
      "supplierId": "供应商ID",
      "paidAmount": 5000.00,
      "issueAmount": 5000.00,
      "discountAmount": 100.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 2000.00,
      "currentVerifiedAmount": 3000.00,
      "advancePaidAmount": 0.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "accountList": [
        {
          "id": "账户记录ID",
          "accountId": "结算账户ID",
          "amount": 5000.00,
          "type": "out",
          "issueDate": "单据日期",
          "businessType": "payment",
          "businessId": "付款单ID",
          "remark": "备注"
        }
      ],
      "issueList": [
        {
          "id": "核销明细ID",
          "paymentId": "付款单ID",
          "sourceCode": "PL202401010000000001",
          "type": "buy",
          "issueDate": "单据日期",
          "issueAmount": 3000.00,
          "verifiedAmount": 3000.00,
          "unverifiedAmount": 0.00,
          "currentVerifiedAmount": 3000.00
        }
      ]
    }
  }
}
```

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

**核销明细字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 核销明细ID |
| paymentId | String | 付款单ID |
| sourceCode | String | 源单据编号（如购货单编号） |
| type | String | 源单据类型 |
| issueDate | String | 源单据日期 |
| issueAmount | Double | 源单据金额 |
| verifiedAmount | Double | 已核销金额 |
| unverifiedAmount | Double | 未核销金额 |
| currentVerifiedAmount | Double | 本次核销金额 |

---

### 2.3 生成付款单编号

**接口地址**: `POST /payment/createCode`

**接口描述**: 自动生成唯一的付款单编号，格式：FK + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "FK20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`FK` (FuKuan)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 2.4 保存付款单

**接口地址**: `POST /payment/save`

**接口描述**: 新增或更新付款单，包含付款单基本信息、核销单据列表和账户记录列表

**请求参数**:

```json
{
  "payment": {
    "id": "付款单ID（更新时必填，新增时不传）",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "supplierId": "供应商ID",
    "paidAmount": 5000.00,
    "issueAmount": 5000.00,
    "discountAmount": 100.00,
    "verifiedAmount": 3000.00,
    "unverifiedAmount": 2000.00,
    "currentVerifiedAmount": 3000.00,
    "advancePaidAmount": 0.00,
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "issueList": [
    {
      "sourceCode": "PL202401010000000001",
      "type": "buy",
      "issueDate": "2024-01-01",
      "issueAmount": 3000.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 0.00,
      "currentVerifiedAmount": 3000.00
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

**payment对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 付款单ID，新增时不传，更新时必填 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| supplierId | String | 是 | 供应商ID |
| paidAmount | Double | 是 | 付款金额 |
| issueAmount | Double | 是 | 单据金额 |
| discountAmount | Double | 否 | 折扣金额 |
| verifiedAmount | Double | 是 | 已核销金额 |
| unverifiedAmount | Double | 是 | 未核销金额 |
| currentVerifiedAmount | Double | 是 | 本次核销金额 |
| advancePaidAmount | Double | 否 | 预付款金额 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**issueList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sourceCode | String | 是 | 源单据编号（如购货单编号） |
| type | String | 是 | 源单据类型 |
| issueDate | String | 是 | 源单据日期 |
| issueAmount | Double | 是 | 源单据金额 |
| verifiedAmount | Double | 是 | 已核销金额 |
| unverifiedAmount | Double | 是 | 未核销金额 |
| currentVerifiedAmount | Double | 是 | 本次核销金额 |

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
    "payment": {
      "id": "付款单ID",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "supplierId": "供应商ID",
      "paidAmount": 5000.00,
      "issueAmount": 5000.00,
      "discountAmount": 100.00,
      "verifiedAmount": 3000.00,
      "unverifiedAmount": 2000.00,
      "currentVerifiedAmount": 3000.00,
      "advancePaidAmount": 0.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增付款单时，系统会校验单据编号是否已存在
2. 更新付款单时，会先删除原有的账户记录和核销单据，再保存新的数据
3. 系统会自动创建账户记录，类型为 `out`（支出）
4. 系统会自动处理应付账款：
   - 根据预付款金额更新供应商的应付账款
5. 需要校验供应商ID是否存在
6. 核销单据列表用于记录该付款单核销了哪些源单据（如购货单）

---

### 2.5 删除付款单

**接口地址**: `POST /payment/delete`

**接口描述**: 根据付款单ID删除付款单及其关联的核销单据和账户记录

**请求参数**:

```json
{
  "paymentId": "付款单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| paymentId | String | 是 | 付款单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除付款单主表记录
2. 同时删除该付款单关联的所有核销单据记录
3. 同时删除该付款单关联的所有账户记录
4. 使用事务保证数据一致性

---

## 3. 收入单管理 (Income)

**控制器路径**: `/income`

### 3.1 分页查询收入单列表

**接口地址**: `POST /income/page`

**接口描述**: 分页查询收入单列表，支持按日期范围筛选

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
    "incomePage": {
      "records": [
        {
          "id": "收入单ID",
          "issueDate": "单据日期",
          "code": "单据编号",
          "customerId": "客户ID",
          "customerName": "客户名称",
          "amount": 5000.00,
          "collectAmount": 5000.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 30,
      "size": 10,
      "current": 1,
      "pages": 3
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 收入单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| customerId | String | 客户ID |
| customerName | String | 客户名称（关联查询） |
| amount | Double | 金额 |
| collectAmount | Double | 收款金额 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 3.2 获取收入单详情

**接口地址**: `POST /income/detail`

**接口描述**: 根据收入单ID获取收入单详细信息，包含账户记录和流水记录列表

**请求参数**:

```json
{
  "incomeId": "收入单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| incomeId | String | 是 | 收入单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "income": {
      "id": "收入单ID",
      "issueDate": "单据日期",
      "code": "单据编号",
      "customerId": "客户ID",
      "amount": 5000.00,
      "collectAmount": 5000.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "accountList": [
        {
          "id": "账户记录ID",
          "accountId": "结算账户ID",
          "amount": 5000.00,
          "type": "in",
          "issueDate": "单据日期",
          "businessType": "income",
          "businessId": "收入单ID",
          "remark": "备注"
        }
      ],
      "recordList": [
        {
          "id": "流水记录ID",
          "issueDate": "单据日期",
          "businessType": "income",
          "businessId": "收入单ID",
          "categoryId": "收入分类ID",
          "amount": 5000.00,
          "remark": "备注"
        }
      ]
    }
  }
}
```

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

**流水记录字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 流水记录ID |
| issueDate | String | 单据日期 |
| businessType | String | 业务类型 |
| businessId | String | 业务ID |
| categoryId | String | 收入分类ID |
| amount | Double | 金额 |
| remark | String | 备注 |

---

### 3.3 生成收入单编号

**接口地址**: `POST /income/createCode`

**接口描述**: 自动生成唯一的收入单编号，格式：SR + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "SR20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`SR` (ShouRu)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 3.4 保存收入单

**接口地址**: `POST /income/save`

**接口描述**: 新增或更新收入单，包含收入单基本信息、流水记录列表和账户记录列表

**请求参数**:

```json
{
  "income": {
    "id": "收入单ID（更新时必填，新增时不传）",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "customerId": "客户ID",
    "amount": 5000.00,
    "collectAmount": 5000.00,
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "recordList": [
    {
      "categoryId": "收入分类ID",
      "amount": 5000.00,
      "remark": "流水备注"
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

**income对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 收入单ID，新增时不传，更新时必填 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| customerId | String | 是 | 客户ID |
| amount | Double | 是 | 金额 |
| collectAmount | Double | 是 | 收款金额 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**recordList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | String | 是 | 收入分类ID |
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
    "income": {
      "id": "收入单ID",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "customerId": "客户ID",
      "amount": 5000.00,
      "collectAmount": 5000.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增收入单时，系统会校验单据编号是否已存在
2. 更新收入单时，会先删除原有的账户记录和流水记录，再保存新的数据
3. 系统会自动创建账户记录，类型为 `in`（收入），金额为收款金额
4. 流水记录用于记录收入的详细分类信息
5. 需要校验客户ID是否存在

---

### 3.5 删除收入单

**接口地址**: `POST /income/delete`

**接口描述**: 根据收入单ID删除收入单及其关联的流水记录和账户记录

**请求参数**:

```json
{
  "incomeId": "收入单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| incomeId | String | 是 | 收入单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除收入单主表记录
2. 同时删除该收入单关联的所有流水记录
3. 同时删除该收入单关联的所有账户记录
4. 使用事务保证数据一致性

---

## 4. 支出单管理 (Expense)

**控制器路径**: `/expense`

### 4.1 分页查询支出单列表

**接口地址**: `POST /expense/page`

**接口描述**: 分页查询支出单列表，支持按日期范围筛选

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
    "expensePage": {
      "records": [
        {
          "id": "支出单ID",
          "issueDate": "单据日期",
          "code": "单据编号",
          "supplierId": "供应商ID",
          "supplierName": "供应商名称",
          "amount": 5000.00,
          "paidAmount": 5000.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "remark": "备注",
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 25,
      "size": 10,
      "current": 1,
      "pages": 3
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 支出单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称（关联查询） |
| amount | Double | 金额 |
| paidAmount | Double | 付款金额 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名（关联查询） |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 4.2 获取支出单详情

**接口地址**: `POST /expense/detail`

**接口描述**: 根据支出单ID获取支出单详细信息，包含账户记录和流水记录列表

**请求参数**:

```json
{
  "expenseId": "支出单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| expenseId | String | 是 | 支出单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "expense": {
      "id": "支出单ID",
      "issueDate": "单据日期",
      "code": "单据编号",
      "supplierId": "供应商ID",
      "amount": 5000.00,
      "paidAmount": 5000.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "accountList": [
        {
          "id": "账户记录ID",
          "accountId": "结算账户ID",
          "amount": 5000.00,
          "type": "out",
          "issueDate": "单据日期",
          "businessType": "expense",
          "businessId": "支出单ID",
          "remark": "备注"
        }
      ],
      "recordList": [
        {
          "id": "流水记录ID",
          "issueDate": "单据日期",
          "businessType": "expense",
          "businessId": "支出单ID",
          "categoryId": "支出分类ID",
          "amount": 5000.00,
          "remark": "备注"
        }
      ]
    }
  }
}
```

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

**流水记录字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 流水记录ID |
| issueDate | String | 单据日期 |
| businessType | String | 业务类型 |
| businessId | String | 业务ID |
| categoryId | String | 支出分类ID |
| amount | Double | 金额 |
| remark | String | 备注 |

---

### 4.3 生成支出单编号

**接口地址**: `POST /expense/createCode`

**接口描述**: 自动生成唯一的支出单编号，格式：ZC + 时间戳(yyyyMMddHHmmssSSS) + 2位随机数

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "ZC20240101120000000123"
  }
}
```

**编号规则**: 
- 前缀：`ZC` (ZhiChu)
- 中间：17位时间戳 (yyyyMMddHHmmssSSS)
- 后缀：2位随机数字 (0-9)

---

### 4.4 保存支出单

**接口地址**: `POST /expense/save`

**接口描述**: 新增或更新支出单，包含支出单基本信息、流水记录列表和账户记录列表

**请求参数**:

```json
{
  "expense": {
    "id": "支出单ID（更新时必填，新增时不传）",
    "code": "单据编号",
    "issueDate": "2024-01-01",
    "supplierId": "供应商ID",
    "amount": 5000.00,
    "paidAmount": 5000.00,
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "recordList": [
    {
      "categoryId": "支出分类ID",
      "amount": 5000.00,
      "remark": "流水备注"
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

**expense对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 支出单ID，新增时不传，更新时必填 |
| code | String | 是 | 单据编号 |
| issueDate | String | 是 | 单据日期 |
| supplierId | String | 是 | 供应商ID |
| amount | Double | 是 | 金额 |
| paidAmount | Double | 是 | 付款金额 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**recordList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | String | 是 | 支出分类ID |
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
    "expense": {
      "id": "支出单ID",
      "code": "单据编号",
      "issueDate": "2024-01-01",
      "supplierId": "供应商ID",
      "amount": 5000.00,
      "paidAmount": 5000.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增支出单时，系统会校验单据编号是否已存在
2. 更新支出单时，会先删除原有的账户记录和流水记录，再保存新的数据
3. 系统会自动创建账户记录，类型为 `out`（支出），金额为付款金额
4. 流水记录用于记录支出的详细分类信息
5. 需要校验供应商ID是否存在

---

### 4.5 删除支出单

**接口地址**: `POST /expense/delete`

**接口描述**: 根据支出单ID删除支出单及其关联的流水记录和账户记录

**请求参数**:

```json
{
  "expenseId": "支出单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| expenseId | String | 是 | 支出单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除支出单主表记录
2. 同时删除该支出单关联的所有流水记录
3. 同时删除该支出单关联的所有账户记录
4. 使用事务保证数据一致性

---

## 附录

### A. 业务类型常量说明

#### 业务类型 (businessType)
- `collection`: 收款
- `payment`: 付款
- `income`: 收入
- `expense`: 支出

#### 账户记录类型 (type)
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
6. **核销机制**: 收款单和付款单支持核销功能，可以关联多个源单据进行核销
7. **流水记录**: 收入单和支出单通过流水记录实现分类统计

### D. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含收款单、付款单、收入单、支出单所有接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
