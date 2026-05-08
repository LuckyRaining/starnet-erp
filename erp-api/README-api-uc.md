# UC模块 API接口文档

> **用户中心模块（User Center）** - 包含客户、供应商、商品、仓库、职员、结算账户、用户等基础资料管理接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 客户管理 (Customer)](#1-客户管理-customer)
  - [1.1 分页查询客户列表](#11-分页查询客户列表)
  - [1.2 获取联系人列表](#12-获取联系人列表)
  - [1.3 保存客户](#13-保存客户)
  - [1.4 获取客户详情](#14-获取客户详情)
  - [1.5 删除客户](#15-删除客户)
  - [1.6 启停客户](#16-启停客户)
  - [1.7 导入Excel](#17-导入excel)
  - [1.8 导出Excel](#18-导出excel)
- [2. 供应商管理 (Supplier)](#2-供应商管理-supplier)
  - [2.1 分页查询供应商列表](#21-分页查询供应商列表)
  - [2.2 保存供应商](#22-保存供应商)
  - [2.3 获取供应商详情](#23-获取供应商详情)
  - [2.4 删除供应商](#24-删除供应商)
  - [2.5 启停供应商](#25-启停供应商)
  - [2.6 导入Excel](#26-导入excel)
  - [2.7 导出Excel](#27-导出excel)
- [3. 商品管理 (Product)](#3-商品管理-product)
  - [3.1 分页查询商品列表](#31-分页查询商品列表)
  - [3.2 保存商品](#32-保存商品)
  - [3.3 获取商品详情](#33-获取商品详情)
  - [3.4 删除商品](#34-删除商品)
  - [3.5 启停商品](#35-启停商品)
- [4. 仓库管理 (Warehouse)](#4-仓库管理-warehouse)
  - [4.1 分页查询仓库列表](#41-分页查询仓库列表)
  - [4.2 保存仓库](#42-保存仓库)
  - [4.3 获取仓库详情](#43-获取仓库详情)
  - [4.4 删除仓库](#44-删除仓库)
  - [4.5 启停仓库](#45-启停仓库)
- [5. 职员管理 (Employee)](#5-职员管理-employee)
  - [5.1 分页查询职员列表](#51-分页查询职员列表)
  - [5.2 保存职员](#52-保存职员)
  - [5.3 获取职员详情](#53-获取职员详情)
  - [5.4 删除职员](#54-删除职员)
  - [5.5 启停职员](#55-启停职员)
- [6. 结算账户管理 (Settlement Account)](#6-结算账户管理-settlement-account)
  - [6.1 获取结算账户列表](#61-获取结算账户列表)
  - [6.2 保存结算账户](#62-保存结算账户)
  - [6.3 获取账户详情](#63-获取账户详情)
  - [6.4 删除账户](#64-删除账户)
- [7. 用户管理 (User)](#7-用户管理-user)
  - [7.1 用户登录](#71-用户登录)
  - [7.2 新增用户](#72-新增用户)
  - [7.3 分页查询用户列表](#73-分页查询用户列表)
  - [7.4 启停用户](#74-启停用户)
  - [7.5 重置密码](#75-重置密码)

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

---

## 1. 客户管理 (Customer)

**控制器路径**: `/customer`

### 1.1 分页查询客户列表

**接口地址**: `POST /customer/page`

**接口描述**: 分页查询客户列表，支持多条件筛选，自动关联客户类别和联系人信息

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字",
    "categoryId": "分类ID",
    "level": "客户等级"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索（客户编码、名称） |
| query.categoryId | String | 否 | - | 客户分类ID |
| query.level | String | 否 | - | 客户等级 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "customerPage": {
      "records": [
        {
          "id": "客户ID",
          "code": "KH001",
          "name": "客户名称",
          "categoryId": "分类ID",
          "categoryName": "分类名称",
          "level": "A",
          "balanceTime": "2024-01-01",
          "beginReceivableAmount": 10000.00,
          "beginPrepaidAmount": 5000.00,
          "beginBalance": -5000.00,
          "remark": "备注",
          "active": true,
          "contactList": [
            {
              "id": "联系人ID",
              "customerId": "客户ID",
              "name": "联系人姓名",
              "mobile": "手机号",
              "phone": "电话",
              "email": "邮箱",
              "primary": true
            }
          ],
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
| id | String | 客户ID |
| code | String | 客户编码 |
| name | String | 客户名称 |
| categoryId | String | 客户分类ID |
| categoryName | String | 客户分类名称 |
| level | String | 客户等级 |
| balanceTime | Date | 余额日期 |
| beginReceivableAmount | Double | 期初应收款 |
| beginPrepaidAmount | Double | 期初预付款 |
| beginBalance | Double | 期初余额（预付款-应收款） |
| remark | String | 备注 |
| active | Boolean | 是否启用 |
| contactList | Array | 联系人列表 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**业务逻辑说明**:
1. 自动关联查询客户分类名称
2. 自动查询每个客户的联系人列表
3. 自动计算期初余额 = 期初预付款 - 期初应收款
4. 支持按关键字、分类、等级等多条件筛选

---

### 1.2 获取联系人列表

**接口地址**: `POST /customer/contactList`

**接口描述**: 根据客户ID获取该客户的所有联系人列表

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
    "contactList": [
      {
        "id": "联系人ID",
        "customerId": "客户ID",
        "name": "联系人姓名",
        "mobile": "手机号",
        "phone": "电话",
        "email": "邮箱",
        "position": "职位",
        "primary": true,
        "createdTime": "2024-01-01T00:00:00",
        "updatedTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 联系人ID |
| customerId | String | 客户ID |
| name | String | 联系人姓名 |
| mobile | String | 手机号 |
| phone | String | 电话 |
| email | String | 邮箱 |
| position | String | 职位 |
| primary | Boolean | 是否首要联系人 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 1.3 保存客户

**接口地址**: `POST /customer/save`

**接口描述**: 新增或更新客户信息，同时保存联系人列表

**请求参数**:

```json
{
  "customer": {
    "id": "客户ID（更新时必填，新增时不传）",
    "code": "KH001",
    "name": "客户名称",
    "categoryId": "分类ID",
    "level": "A",
    "balanceTime": "2024-01-01",
    "beginReceivableAmount": 10000.00,
    "beginPrepaidAmount": 5000.00,
    "remark": "备注"
  },
  "contactList": [
    {
      "id": "联系人ID（更新时传，新增时不传）",
      "name": "联系人姓名",
      "mobile": "13800138000",
      "phone": "010-12345678",
      "email": "contact@example.com",
      "position": "采购经理",
      "primary": true
    }
  ]
}
```

**customer对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 客户ID，新增时不传，更新时必填 |
| code | String | 是 | 客户编码，不能重复 |
| name | String | 是 | 客户名称 |
| categoryId | String | 是 | 客户分类ID |
| level | String | 是 | 客户等级 |
| balanceTime | Date | 否 | 余额日期 |
| beginReceivableAmount | Double | 否 | 期初应收款，默认0 |
| beginPrepaidAmount | Double | 否 | 期初预付款，默认0 |
| remark | String | 否 | 备注 |

**contactList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 联系人ID，新增时不传 |
| name | String | 是 | 联系人姓名 |
| mobile | String | 否 | 手机号 |
| phone | String | 否 | 电话 |
| email | String | 否 | 邮箱 |
| position | String | 否 | 职位 |
| primary | Boolean | 否 | 是否首要联系人，只能有一个 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "customer": {
      "id": "客户ID",
      "code": "KH001",
      "name": "客户名称",
      "categoryId": "分类ID",
      "level": "A",
      "balanceTime": "2024-01-01",
      "beginReceivableAmount": 10000.00,
      "beginPrepaidAmount": 5000.00,
      "remark": "备注",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. **事务处理**：客户和联系人的保存在一个事务中
2. **编码校验**：客户编码不能为空且不能重复
3. **分类校验**：必须选择有效的客户分类
4. **等级校验**：客户等级必须是系统定义的等级
5. **联系人处理**：
   - 更新客户时会先删除原有联系人，再保存新联系人列表
   - 如果没有设置首要联系人，自动将第一个联系人设为首要联系人
   - 如果设置了多个首要联系人，只保留第一个
6. **启用状态**：新增客户默认启用（active=true）

---

### 1.4 获取客户详情

**接口地址**: `POST /customer/detail`

**接口描述**: 根据客户ID获取客户详细信息

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
    "customer": {
      "id": "客户ID",
      "code": "KH001",
      "name": "客户名称",
      "categoryId": "分类ID",
      "categoryName": "分类名称",
      "level": "A",
      "balanceTime": "2024-01-01",
      "beginReceivableAmount": 10000.00,
      "beginPrepaidAmount": 5000.00,
      "remark": "备注",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 1.5 删除客户

**接口地址**: `POST /customer/delete`

**接口描述**: 根据客户ID删除客户

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
  "data": {}
}
```

**业务逻辑说明**:
1. 删除前会检查是否有业务单据引用该客户
2. 如果有未完成的业务，不允许删除

---

### 1.6 启停客户

**接口地址**: `POST /customer/switchActive`

**接口描述**: 切换客户的启用/停用状态

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
    "active": false
  }
}
```

**业务逻辑说明**:
1. 如果客户当前是启用状态，则停用
2. 如果客户当前是停用状态，则启用
3. 停用的客户不能在业务单据中使用

---

### 1.7 导入Excel

**接口地址**: `POST /customer/importExcel`

**接口描述**: 从Excel文件批量导入客户数据

**请求参数**: 
- Content-Type: `multipart/form-data`
- 参数名: `file` (Excel文件)

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 50,
    "failCount": 2,
    "failList": [
      {
        "row": 3,
        "reason": "客户编码重复"
      }
    ]
  }
}
```

**业务逻辑说明**:
1. 支持批量导入客户数据
2. 返回成功和失败的数量
3. 失败的行会说明原因

---

### 1.8 导出Excel

**接口地址**: `POST /customer/exportExcel`

**接口描述**: 导出客户数据到Excel文件

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字",
    "categoryId": "分类ID"
  }
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | Object | 否 | 筛选条件，不传则导出全部 |

**响应数据**: 
- 直接返回Excel文件流
- Content-Type: `application/vnd.ms-excel`

---

## 2. 供应商管理 (Supplier)

**控制器路径**: `/supplier`

### 2.1 分页查询供应商列表

**接口地址**: `POST /supplier/page`

**接口描述**: 分页查询供应商列表，支持多条件筛选

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字",
    "categoryId": "分类ID"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索 |
| query.categoryId | String | 否 | - | 供应商分类ID |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "supplierPage": {
      "records": [
        {
          "id": "供应商ID",
          "code": "GYS001",
          "name": "供应商名称",
          "categoryId": "分类ID",
          "categoryName": "分类名称",
          "balanceTime": "2024-01-01",
          "beginReceivableAmount": 8000.00,
          "beginPrepaidAmount": 3000.00,
          "vatRate": 13,
          "remark": "备注",
          "active": true,
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
| id | String | 供应商ID |
| code | String | 供应商编码 |
| name | String | 供应商名称 |
| categoryId | String | 供应商分类ID |
| categoryName | String | 供应商分类名称 |
| balanceTime | Date | 余额日期 |
| beginReceivableAmount | Double | 期初应收款 |
| beginPrepaidAmount | Double | 期初预付款 |
| vatRate | Integer | 增值税税率（%） |
| remark | String | 备注 |
| active | Boolean | 是否启用 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 2.2 保存供应商

**接口地址**: `POST /supplier/save`

**接口描述**: 新增或更新供应商信息

**请求参数**:

```json
{
  "supplier": {
    "id": "供应商ID（更新时必填）",
    "code": "GYS001",
    "name": "供应商名称",
    "categoryId": "分类ID",
    "balanceTime": "2024-01-01",
    "beginReceivableAmount": 8000.00,
    "beginPrepaidAmount": 3000.00,
    "vatRate": 13,
    "remark": "备注"
  },
  "contactList": [
    {
      "name": "联系人姓名",
      "mobile": "手机号",
      "phone": "座机",
      "qq": "QQ/微信",
      "address": "联系地址",
      "primary": true
    }
  ]
}

```

**supplier对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 供应商ID，新增时不传 |
| code | String | 是 | 供应商编码，不能重复 |
| name | String | 是 | 供应商名称 |
| categoryId | String | 是 | 供应商分类ID |
| balanceTime | Date | 否 | 余额日期 |
| beginReceivableAmount | Double | 否 | 期初应收款 |
| beginPrepaidAmount | Double | 否 | 期初预付款 |
| vatRate | Integer | 否 | 增值税税率，默认0 |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "supplier": {
      "id": "供应商ID",
      "code": "GYS001",
      "name": "供应商名称",
      "categoryId": "分类ID",
      "balanceTime": "2024-01-01",
      "beginReceivableAmount": 8000.00,
      "beginPrepaidAmount": 3000.00,
      "vatRate": 13,
      "remark": "备注",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 供应商编码不能为空且不能重复
2. 必须选择有效的供应商分类
3. 新增供应商默认启用

---

### 2.3 获取供应商详情

**接口地址**: `POST /supplier/detail`

**接口描述**: 根据供应商ID获取供应商详细信息

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
    "supplier": {
      "id": "供应商ID",
      "code": "GYS001",
      "name": "供应商名称",
      "categoryId": "分类ID",
      "categoryName": "分类名称",
      "balanceTime": "2024-01-01",
      "beginReceivableAmount": 8000.00,
      "beginPrepaidAmount": 3000.00,
      "vatRate": 13,
      "remark": "备注",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 2.4 删除供应商

**接口地址**: `POST /supplier/delete`

**接口描述**: 根据供应商ID删除供应商

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
  "data": {}
}
```

---

### 2.5 启停供应商

**接口地址**: `POST /supplier/switchActive`

**接口描述**: 切换供应商的启用/停用状态

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
    "active": false
  }
}
```

---

### 2.6 导入Excel

**接口地址**: `POST /supplier/importExcel`

**接口描述**: 从Excel文件批量导入供应商数据

**请求参数**: 
- Content-Type: `multipart/form-data`
- 参数名: `file` (Excel文件)

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "successCount": 30,
    "failCount": 1,
    "failList": [
      {
        "row": 5,
        "reason": "供应商编码重复"
      }
    ]
  }
}
```

---

### 2.7 导出Excel

**接口地址**: `POST /supplier/exportExcel`

**接口描述**: 导出供应商数据到Excel文件

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字",
    "categoryId": "分类ID"
  }
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| query | Object | 否 | 筛选条件 |

**响应数据**: 
- 直接返回Excel文件流

---

## 3. 商品管理 (Product)

**控制器路径**: `/product`

### 3.1 分页查询商品列表

**接口地址**: `POST /product/page`

**接口描述**: 分页查询商品列表，支持多条件筛选，自动关联商品分类和单位

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字",
    "categoryId": "分类ID",
    "unitId": "单位ID"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索（商品编码、名称、条形码） |
| query.categoryId | String | 否 | - | 商品分类ID |
| query.unitId | String | 否 | - | 计量单位ID |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productPage": {
      "records": [
        {
          "id": "商品ID",
          "code": "SP001",
          "name": "商品名称",
          "barcode": "6901234567890",
          "spec": "规格型号",
          "categoryId": "分类ID",
          "categoryName": "分类名称",
          "primaryWarehouseId": "仓库ID",
          "unitId": "单位ID",
          "unitName": "个",
          "retailPrice": 100.00,
          "wholesalePrice": 80.00,
          "vipPrice": 90.00,
          "discountRate1": 0.95,
          "discountRate2": 0.90,
          "estimatedPurchasePrice": 60.00,
          "remark": "备注",
          "minimumStock": 10.00,
          "maximumStock": 1000.00,
          "stock": 2345.67,
          "active": true,
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 200,
      "size": 10,
      "current": 1,
      "pages": 20
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 商品ID |
| code | String | 商品编码 |
| name | String | 商品名称 |
| barcode | String | 条形码 |
| spec | String | 规格型号 |
| categoryId | String | 商品分类ID |
| categoryName | String | 商品分类名称 |
| primaryWarehouseId | String | 首选仓库ID |
| unitId | String | 计量单位ID |
| unitName | String | 计量单位名称 |
| retailPrice | Double | 零售价格 |
| wholesalePrice | Double | 批发价格 |
| vipPrice | Double | VIP价格 |
| discountRate1 | Double | 折扣率1 |
| discountRate2 | Double | 折扣率2 |
| estimatedPurchasePrice | Double | 预计采购价 |
| remark | String | 备注 |
| minimumStock | Double | 最低库存 |
| maximumStock | Double | 最高库存 |
| stock | Double | 当前库存（TODO：待实现） |
| active | Boolean | 是否启用 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 3.2 保存商品

**接口地址**: `POST /product/save`

**接口描述**: 新增或更新商品信息

**请求参数**:

```json
{
  "product": {
    "id": "商品ID（更新时必填，新增时不传）",
    "code": "SP001",
    "name": "商品名称",
    "barcode": "6901234567890",
    "spec": "规格型号",
    "categoryId": "分类ID",
    "primaryWarehouseId": "仓库ID",
    "unitId": "单位ID",
    "retailPrice": 100.00,
    "wholesalePrice": 80.00,
    "vipPrice": 90.00,
    "discountRate1": 0.95,
    "discountRate2": 0.90,
    "estimatedPurchasePrice": 60.00,
    "remark": "备注",
    "minimumStock": 10.00,
    "maximumStock": 1000.00
  }
}
```

**product对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 商品ID，新增时不传 |
| code | String | 是 | 商品编码，不能重复 |
| name | String | 是 | 商品名称 |
| barcode | String | 否 | 条形码 |
| spec | String | 否 | 规格型号 |
| categoryId | String | 否 | 商品分类ID |
| primaryWarehouseId | String | 否 | 首选仓库ID |
| unitId | String | 否 | 计量单位ID |
| retailPrice | Double | 否 | 零售价格，默认0 |
| wholesalePrice | Double | 否 | 批发价格，默认0 |
| vipPrice | Double | 否 | VIP价格，默认0 |
| discountRate1 | Double | 否 | 折扣率1，默认1.0 |
| discountRate2 | Double | 否 | 折扣率2，默认1.0 |
| estimatedPurchasePrice | Double | 否 | 预计采购价，默认0 |
| remark | String | 否 | 备注 |
| minimumStock | Double | 否 | 最低库存，默认0 |
| maximumStock | Double | 否 | 最高库存，默认0 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "product": {
      "id": "商品ID",
      "code": "SP001",
      "name": "商品名称",
      "barcode": "6901234567890",
      "spec": "规格型号",
      "categoryId": "分类ID",
      "primaryWarehouseId": "仓库ID",
      "unitId": "单位ID",
      "retailPrice": 100.00,
      "wholesalePrice": 80.00,
      "vipPrice": 90.00,
      "discountRate1": 0.95,
      "discountRate2": 0.90,
      "estimatedPurchasePrice": 60.00,
      "remark": "备注",
      "minimumStock": 10.00,
      "maximumStock": 1000.00,
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 商品编码不能为空且不能重复
2. 如果指定了分类，必须是有效的商品分类
3. 如果指定了首选仓库，必须是有效的仓库
4. 如果指定了计量单位，必须是有效的单位字典项
5. 新增商品默认启用

---

### 3.3 获取商品详情

**接口地址**: `POST /product/detail`

**接口描述**: 根据商品ID获取商品详细信息

**请求参数**:

```json
{
  "productId": "商品ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "product": {
      "id": "商品ID",
      "code": "SP001",
      "name": "商品名称",
      "barcode": "6901234567890",
      "spec": "规格型号",
      "categoryId": "分类ID",
      "categoryName": "分类名称",
      "primaryWarehouseId": "仓库ID",
      "unitId": "单位ID",
      "unitName": "个",
      "retailPrice": 100.00,
      "wholesalePrice": 80.00,
      "vipPrice": 90.00,
      "discountRate1": 0.95,
      "discountRate2": 0.90,
      "estimatedPurchasePrice": 60.00,
      "remark": "备注",
      "minimumStock": 10.00,
      "maximumStock": 1000.00,
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 3.4 删除商品

**接口地址**: `POST /product/delete`

**接口描述**: 根据商品ID删除商品

**请求参数**:

```json
{
  "productId": "商品ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除前会检查是否有业务单据引用该商品
2. 如果有库存或未完成业务，不允许删除

---

### 3.5 启停商品

**接口地址**: `POST /product/switchActive`

**接口描述**: 切换商品的启用/停用状态

**请求参数**:

```json
{
  "productId": "商品ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| productId | String | 是 | 商品ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "active": false
  }
}
```

---

## 4. 仓库管理 (Warehouse)

**控制器路径**: `/warehouse`

### 4.1 分页查询仓库列表

**接口地址**: `POST /warehouse/page`

**接口描述**: 分页查询仓库列表

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索（仓库编码、名称） |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "warehousePage": {
      "records": [
        {
          "id": "仓库ID",
          "code": "CK001",
          "name": "仓库名称",
          "active": true,
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 5,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 仓库ID |
| code | String | 仓库编码 |
| name | String | 仓库名称 |
| active | Boolean | 是否启用 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 4.2 保存仓库

**接口地址**: `POST /warehouse/save`

**接口描述**: 新增或更新仓库信息

**请求参数**:

```json
{
  "warehouse": {
    "id": "仓库ID（更新时必填，新增时不传）",
    "code": "CK001",
    "name": "仓库名称"
  }
}
```

**warehouse对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 仓库ID，新增时不传 |
| code | String | 是 | 仓库编码 |
| name | String | 是 | 仓库名称 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "warehouse": {
      "id": "仓库ID",
      "code": "CK001",
      "name": "仓库名称",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 4.3 获取仓库详情

**接口地址**: `POST /warehouse/detail`

**接口描述**: 根据仓库ID获取仓库详细信息

**请求参数**:

```json
{
  "warehouseId": "仓库ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| warehouseId | String | 是 | 仓库ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "warehouse": {
      "id": "仓库ID",
      "code": "CK001",
      "name": "仓库名称",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 4.4 删除仓库

**接口地址**: `POST /warehouse/delete`

**接口描述**: 根据仓库ID删除仓库

**请求参数**:

```json
{
  "warehouseId": "仓库ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| warehouseId | String | 是 | 仓库ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除前会检查仓库是否有库存
2. 如果有库存，不允许删除

---

### 4.5 启停仓库

**接口地址**: `POST /warehouse/switchActive`

**接口描述**: 切换仓库的启用/停用状态

**请求参数**:

```json
{
  "warehouseId": "仓库ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| warehouseId | String | 是 | 仓库ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "active": false
  }
}
```

---

## 5. 职员管理 (Employee)

**控制器路径**: `/employee`

### 5.1 分页查询职员列表

**接口地址**: `POST /employee/page`

**接口描述**: 分页查询职员列表

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "employeePage": {
      "records": [
        {
          "id": "职员ID",
          "code": "ZY001",
          "name": "职员姓名",
          "department": "部门",
          "position": "职位",
          "mobile": "手机号",
          "email": "邮箱",
          "active": true,
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 20,
      "size": 10,
      "current": 1,
      "pages": 2
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 职员ID |
| code | String | 职员编码 |
| name | String | 职员姓名 |
| department | String | 部门 |
| position | String | 职位 |
| mobile | String | 手机号 |
| email | String | 邮箱 |
| active | Boolean | 是否启用 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 5.2 保存职员

**接口地址**: `POST /employee/save`

**接口描述**: 新增或更新职员信息

**请求参数**:

```json
{
  "employee": {
    "id": "职员ID（更新时必填，新增时不传）",
    "code": "ZY001",
    "name": "职员姓名",
    "department": "销售部",
    "position": "销售经理",
    "mobile": "13800138000",
    "email": "employee@example.com"
  }
}
```

**employee对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 职员ID，新增时不传 |
| code | String | 是 | 职员编码 |
| name | String | 是 | 职员姓名 |
| department | String | 否 | 部门 |
| position | String | 否 | 职位 |
| mobile | String | 否 | 手机号 |
| email | String | 否 | 邮箱 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "employee": {
      "id": "职员ID",
      "code": "ZY001",
      "name": "职员姓名",
      "department": "销售部",
      "position": "销售经理",
      "mobile": "13800138000",
      "email": "employee@example.com",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 5.3 获取职员详情

**接口地址**: `POST /employee/detail`

**接口描述**: 根据职员ID获取职员详细信息

**请求参数**:

```json
{
  "employeeId": "职员ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | String | 是 | 职员ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "employee": {
      "id": "职员ID",
      "code": "ZY001",
      "name": "职员姓名",
      "department": "销售部",
      "position": "销售经理",
      "mobile": "13800138000",
      "email": "employee@example.com",
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 5.4 删除职员

**接口地址**: `POST /employee/delete`

**接口描述**: 根据职员ID删除职员

**请求参数**:

```json
{
  "employeeId": "职员ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | String | 是 | 职员ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

### 5.5 启停职员

**接口地址**: `POST /employee/switchActive`

**接口描述**: 切换职员的启用/停用状态

**请求参数**:

```json
{
  "employeeId": "职员ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | String | 是 | 职员ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "active": false
  }
}
```

---

## 6. 结算账户管理 (Settlement Account)

**控制器路径**: `/settlementAccount`

### 6.1 获取结算账户列表

**接口地址**: `POST /settlementAccount/list`

**接口描述**: 获取所有结算账户列表（不分页）

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accountList": [
      {
        "id": "账户ID",
        "code": "ZH001",
        "name": "账户名称",
        "type": "bank",
        "bankName": "银行名称",
        "accountNumber": "账号",
        "balance": 100000.00,
        "active": true,
        "createdTime": "2024-01-01T00:00:00",
        "updatedTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 账户ID |
| code | String | 账户编码 |
| name | String | 账户名称 |
| type | String | 账户类型：cash-现金，bank-银行 |
| bankName | String | 银行名称 |
| accountNumber | String | 账号 |
| balance | Double | 账户余额 |
| active | Boolean | 是否启用 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 6.2 保存结算账户

**接口地址**: `POST /settlementAccount/save`

**接口描述**: 新增或更新结算账户信息

**请求参数**:

```json
{
  "account": {
    "id": "账户ID（更新时必填，新增时不传）",
    "code": "ZH001",
    "name": "账户名称",
    "type": "bank",
    "bankName": "中国工商银行",
    "accountNumber": "6222021234567890"
  }
}
```

**account对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 账户ID，新增时不传 |
| code | String | 是 | 账户编码 |
| name | String | 是 | 账户名称 |
| type | String | 是 | 账户类型：cash-现金，bank-银行 |
| bankName | String | 否 | 银行名称（银行账户必填） |
| accountNumber | String | 否 | 账号（银行账户必填） |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "account": {
      "id": "账户ID",
      "code": "ZH001",
      "name": "账户名称",
      "type": "bank",
      "bankName": "中国工商银行",
      "accountNumber": "6222021234567890",
      "balance": 0.00,
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 6.3 获取账户详情

**接口地址**: `POST /settlementAccount/detail`

**接口描述**: 根据账户ID获取账户详细信息

**请求参数**:

```json
{
  "accountId": "账户ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountId | String | 是 | 账户ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "account": {
      "id": "账户ID",
      "code": "ZH001",
      "name": "账户名称",
      "type": "bank",
      "bankName": "中国工商银行",
      "accountNumber": "6222021234567890",
      "balance": 100000.00,
      "active": true,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

---

### 6.4 删除账户

**接口地址**: `POST /settlementAccount/delete`

**接口描述**: 根据账户ID删除账户

**请求参数**:

```json
{
  "accountId": "账户ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| accountId | String | 是 | 账户ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除前会检查账户是否有余额
2. 如果有余额或有业务记录，不允许删除

---

## 7. 用户管理 (User)

**控制器路径**: `/user`

### 7.1 用户登录

**接口地址**: `POST /user/login`

**接口描述**: 用户登录，验证用户名和密码，返回JWT令牌

**请求参数**:

```json
{
  "loginName": "admin",
  "password": "123456"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| loginName | String | 是 | 登录名（用户名） |
| password | String | 是 | 密码（明文，后端会加密比对） |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| token | String | JWT令牌，后续请求需要在Header中携带 |

**业务逻辑说明**:
1. 验证登录名是否存在
2. 验证密码是否正确（BCrypt加密比对）
3. 生成JWT令牌，包含用户ID、用户名、角色信息
4. 记录登录日志
5. 后续请求需要在Header中携带：`Authorization: Bearer {token}`

---

### 7.2 新增用户

**接口地址**: `POST /user/add`

**接口描述**: 新增系统用户

**请求参数**:

```json
{
  "user": {
    "username": "zhangsan",
    "name": "张三",
    "mobile": "13800138000",
    "password": "123456"
  }
}
```

**user对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（登录名），不能重复 |
| name | String | 是 | 真实姓名 |
| mobile | String | 否 | 手机号 |
| password | String | 是 | 密码（后端会加密存储） |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": "用户ID",
      "username": "zhangsan",
      "name": "张三",
      "mobile": "13800138000",
      "active": true,
      "deleted": false,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 用户名不能重复
2. 密码会使用BCrypt加密后存储
3. 新增用户默认启用

---

### 7.3 分页查询用户列表

**接口地址**: `POST /user/page`

**接口描述**: 分页查询用户列表

**请求参数**:

```json
{
  "query": {
    "keyword": "关键字"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索（用户名、姓名） |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userPage": {
      "records": [
        {
          "id": "用户ID",
          "username": "admin",
          "name": "管理员",
          "mobile": "13800138000",
          "active": true,
          "deleted": false,
          "createdTime": "2024-01-01T00:00:00",
          "updatedTime": "2024-01-01T00:00:00"
        }
      ],
      "total": 10,
      "size": 10,
      "current": 1,
      "pages": 1
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 用户ID |
| username | String | 用户名 |
| name | String | 真实姓名 |
| mobile | String | 手机号 |
| active | Boolean | 是否启用 |
| deleted | Boolean | 是否已删除 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 7.4 启停用户

**接口地址**: `POST /user/switchActive`

**接口描述**: 切换用户的启用/停用状态

**请求参数**:

```json
{
  "userId": "用户ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 是 | 用户ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "active": false
  }
}
```

**业务逻辑说明**:
1. 停用的用户无法登录系统
2. 不能停用当前登录的用户

---

### 7.5 重置密码

**接口地址**: `POST /user/resetPassword`

**接口描述**: 重置用户密码

**请求参数**:

```json
{
  "userId": "用户ID",
  "newPassword": "新密码"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 是 | 用户ID |
| newPassword | String | 是 | 新密码 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 新密码会使用BCrypt加密后存储
2. 需要管理员权限才能重置其他用户密码

---

## 附录

### A. 客户等级说明

| 等级值 | 说明 |
|--------|------|
| A | A级客户 |
| B | B级客户 |
| C | C级客户 |

*注：具体等级值请参考系统字典配置*

### B. 账户类型说明

| 类型值 | 说明 |
|--------|------|
| cash | 现金账户 |
| bank | 银行账户 |

### C. 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查必填参数是否完整 |
| 401 | 未授权 | 检查Token是否有效，重新登录 |
| 403 | 没有权限 | 联系管理员分配权限 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### D. 注意事项

1. **编码唯一性**: 客户、供应商、商品、仓库等的编码都不能重复
2. **分类校验**: 保存时必须选择正确的分类类型
3. **启用状态**: 停用的基础资料不能在业务单据中使用
4. **删除限制**: 有业务引用的基础资料不能删除
5. **密码安全**: 密码使用BCrypt加密存储，不可逆
6. **Token有效期**: JWT令牌有有效期，过期需要重新登录
7. **联系人管理**: 客户联系人随客户一起保存，更新时会替换原有联系人
8. **期初数据**: 客户和供应商的期初数据用于财务核算

### E. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含客户、供应商、商品、仓库、职员、结算账户、用户所有管理接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
