# WC模块 API接口文档

> **仓库中心模块（Warehouse Center）** - 包含入库单、出库单、调拨单等库存管理业务接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 入库管理 (Store)](#1-入库管理-store)
  - [1.1 分页查询入库单列表](#11-分页查询入库单列表)
  - [1.2 获取入库单详情](#12-获取入库单详情)
  - [1.3 生成入库单编号](#13-生成入库单编号)
  - [1.4 保存入库单](#14-保存入库单)
  - [1.5 删除入库单](#15-删除入库单)
- [2. 出库管理 (Checkout)](#2-出库管理-checkout)
  - [2.1 分页查询出库单列表](#21-分页查询出库单列表)
  - [2.2 获取出库单详情](#22-获取出库单详情)
  - [2.3 生成出库单编号](#23-生成出库单编号)
  - [2.4 保存出库单](#24-保存出库单)
  - [2.5 删除出库单](#25-删除出库单)
- [3. 调拨管理 (Transfer)](#3-调拨管理-transfer)
  - [3.1 分页查询调拨单列表](#31-分页查询调拨单列表)
  - [3.2 获取调拨单详情](#32-获取调拨单详情)
  - [3.3 生成调拨单编号](#33-生成调拨单编号)
  - [3.4 保存调拨单](#34-保存调拨单)
  - [3.5 删除调拨单](#35-删除调拨单)
  - [3.6 审核/反审核调拨单](#36-审核反审核调拨单)

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

### 日期格式规范

- 所有日期参数使用 `yyyy-MM-dd` 格式
- 单据日期（issueDate）也使用此格式

---

## 1. 入库管理 (Store)

**控制器路径**: `/store`

### 1.1 分页查询入库单列表

**接口地址**: `POST /store/page`

**接口描述**: 分页查询入库单列表，支持按日期范围筛选，自动关联供应商和制单人信息

**请求参数**:

```json
{
  "query": {
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "keyword": "关键字",
    "supplierId": "供应商ID",
    "type": 10
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
| query.keyword | String | 否 | - | 关键字搜索（单据编号） |
| query.supplierId | String | 否 | - | 供应商ID |
| query.type | Integer | 否 | - | 入库类型：10-其他入库，20-盘盈 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "storePage": {
      "records": [
        {
          "id": "入库单ID",
          "issueDate": "2024-01-15",
          "code": "RK20240115001",
          "type": 10,
          "supplierId": "供应商ID",
          "supplierName": "供应商名称",
          "amount": 5000.00,
          "quantity": 100.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "remark": "备注",
          "createdTime": "2024-01-15T10:30:00",
          "updatedTime": "2024-01-15T10:30:00"
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
| id | String | 入库单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| type | Integer | 入库类型：10-其他入库，20-盘盈 |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称 |
| amount | Double | 入库金额 |
| quantity | Double | 入库数量 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名 |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**业务逻辑说明**:
1. 支持按日期范围筛选
2. 自动关联查询供应商名称
3. 自动关联查询制单人姓名
4. 支持按单据编号关键字搜索

---

### 1.2 获取入库单详情

**接口地址**: `POST /store/detail`

**接口描述**: 根据入库单ID获取入库单详细信息，包含商品明细列表

**请求参数**:

```json
{
  "storeId": "入库单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| storeId | String | 是 | 入库单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "store": {
      "id": "入库单ID",
      "issueDate": "2024-01-15",
      "code": "RK20240115001",
      "type": 10,
      "supplierId": "供应商ID",
      "supplierName": "供应商名称",
      "amount": 5000.00,
      "quantity": 100.00,
      "listerId": "制单人ID",
      "listerName": "制单人姓名",
      "remark": "备注",
      "productList": [
        {
          "id": "出入库商品ID",
          "businessId": "入库单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "spec": "规格型号",
          "unitName": "单位",
          "warehouseId": "仓库ID",
          "warehouseName": "仓库名称",
          "quantity": 50.00,
          "price": 50.00,
          "amount": 2500.00,
          "remark": "备注"
        }
      ],
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 入库单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| type | Integer | 入库类型 |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称 |
| amount | Double | 入库总金额 |
| quantity | Double | 入库总数量 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名 |
| remark | String | 备注 |
| productList | Array | 商品明细列表 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**productList数组字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 出入库商品记录ID |
| businessId | String | 关联的入库单ID |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| quantity | Double | 数量 |
| price | Double | 单价 |
| amount | Double | 金额 |
| remark | String | 备注 |

---

### 1.3 生成入库单编号

**接口地址**: `POST /store/createCode`

**接口描述**: 自动生成一个新的入库单编号

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "RK20240115001"
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | String | 生成的单据编号，格式：RK + 日期 + 流水号 |

**业务逻辑说明**:
1. 编号格式：RK + yyyyMMdd + 3位流水号
2. 每天从001开始递增
3. 保证编号唯一性

---

### 1.4 保存入库单

**接口地址**: `POST /store/save`

**接口描述**: 新增或更新入库单，同时保存商品明细列表，自动处理库存

**请求参数**:

```json
{
  "store": {
    "id": "入库单ID（更新时必填，新增时不传）",
    "code": "RK20240115001",
    "issueDate": "2024-01-15",
    "type": 10,
    "supplierId": "供应商ID",
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "id": "出入库商品ID（更新时传，新增时不传）",
      "productId": "商品ID",
      "warehouseId": "仓库ID",
      "quantity": 50.00,
      "price": 50.00,
      "amount": 2500.00,
      "remark": "备注"
    }
  ]
}
```

**store对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 入库单ID，新增时不传 |
| code | String | 是 | 单据编号，不能重复 |
| issueDate | String | 是 | 单据日期 |
| type | Integer | 是 | 入库类型：10-其他入库，20-盘盈 |
| supplierId | String | 否 | 供应商ID |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 出入库商品ID，新增时不传 |
| productId | String | 是 | 商品ID |
| warehouseId | String | 是 | 仓库ID |
| quantity | Double | 是 | 数量 |
| price | Double | 是 | 单价 |
| amount | Double | 是 | 金额（= quantity × price） |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "store": {
      "id": "入库单ID",
      "code": "RK20240115001",
      "issueDate": "2024-01-15",
      "type": 10,
      "supplierId": "供应商ID",
      "amount": 5000.00,
      "quantity": 100.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

**业务逻辑说明**:
1. **事务处理**：入库单和商品明细的保存在一个事务中
2. **编号校验**：单据编号不能为空且不能重复
3. **自动计算**：系统会自动计算入库总金额和总数量
4. **库存处理**：
   - 保存成功后会自动增加对应仓库的库存
   - 根据入库类型设置业务类型（盘盈/其他入库）
5. **更新逻辑**：
   - 更新时会先删除原有商品明细，再保存新的商品列表
   - 会重新计算总金额和总数量
6. **数据校验**：
   - 商品ID和仓库ID不能为空
   - 商品和仓库必须存在

---

### 1.5 删除入库单

**接口地址**: `POST /store/delete`

**接口描述**: 根据入库单ID删除入库单及其商品明细

**请求参数**:

```json
{
  "storeId": "入库单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| storeId | String | 是 | 入库单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除入库单的同时会删除所有商品明细
2. 会自动回滚库存（减少对应仓库的库存）
3. 删除前会检查入库单是否存在

---

## 2. 出库管理 (Checkout)

**控制器路径**: `/checkout`

### 2.1 分页查询出库单列表

**接口地址**: `POST /checkout/page`

**接口描述**: 分页查询出库单列表，支持按日期范围筛选，自动关联客户和制单人信息

**请求参数**:

```json
{
  "query": {
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "keyword": "关键字",
    "customerId": "客户ID",
    "type": 10
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.startDate | String | 否 | - | 开始日期 |
| query.endDate | String | 否 | - | 结束日期 |
| query.keyword | String | 否 | - | 关键字搜索 |
| query.customerId | String | 否 | - | 客户ID |
| query.type | Integer | 否 | - | 出库类型：10-其他出库，20-盘亏 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "checkoutPage": {
      "records": [
        {
          "id": "出库单ID",
          "issueDate": "2024-01-15",
          "code": "CK20240115001",
          "type": 10,
          "customerId": "客户ID",
          "customerName": "客户名称",
          "amount": 3000.00,
          "quantity": 60.00,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "remark": "备注",
          "createdTime": "2024-01-15T10:30:00",
          "updatedTime": "2024-01-15T10:30:00"
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
| id | String | 出库单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| type | Integer | 出库类型：10-其他出库，20-盘亏 |
| customerId | String | 客户ID |
| customerName | String | 客户名称 |
| amount | Double | 出库成本 |
| quantity | Double | 出库数量 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名 |
| remark | String | 备注 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 2.2 获取出库单详情

**接口地址**: `POST /checkout/detail`

**接口描述**: 根据出库单ID获取出库单详细信息，包含商品明细列表

**请求参数**:

```json
{
  "checkoutId": "出库单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| checkoutId | String | 是 | 出库单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "checkout": {
      "id": "出库单ID",
      "issueDate": "2024-01-15",
      "code": "CK20240115001",
      "type": 10,
      "customerId": "客户ID",
      "customerName": "客户名称",
      "amount": 3000.00,
      "quantity": 60.00,
      "listerId": "制单人ID",
      "listerName": "制单人姓名",
      "remark": "备注",
      "productList": [
        {
          "id": "出入库商品ID",
          "businessId": "出库单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "spec": "规格型号",
          "unitName": "单位",
          "warehouseId": "仓库ID",
          "warehouseName": "仓库名称",
          "quantity": 30.00,
          "price": 50.00,
          "amount": 1500.00,
          "remark": "备注"
        }
      ],
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

**字段说明**: 与入库单详情类似，参考1.2节

---

### 2.3 生成出库单编号

**接口地址**: `POST /checkout/createCode`

**接口描述**: 自动生成一个新的出库单编号

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "CK20240115001"
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | String | 生成的单据编号，格式：CK + 日期 + 流水号 |

**业务逻辑说明**:
1. 编号格式：CK + yyyyMMdd + 3位流水号
2. 每天从001开始递增

---

### 2.4 保存出库单

**接口地址**: `POST /checkout/save`

**接口描述**: 新增或更新出库单，同时保存商品明细列表，自动处理库存

**请求参数**:

```json
{
  "checkout": {
    "id": "出库单ID（更新时必填，新增时不传）",
    "code": "CK20240115001",
    "issueDate": "2024-01-15",
    "type": 10,
    "customerId": "客户ID",
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "id": "出入库商品ID（更新时传，新增时不传）",
      "productId": "商品ID",
      "warehouseId": "仓库ID",
      "quantity": 30.00,
      "price": 50.00,
      "amount": 1500.00,
      "remark": "备注"
    }
  ]
}
```

**checkout对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 出库单ID，新增时不传 |
| code | String | 是 | 单据编号，不能重复 |
| issueDate | String | 是 | 单据日期 |
| type | Integer | 是 | 出库类型：10-其他出库，20-盘亏 |
| customerId | String | 否 | 客户ID |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**: 与入库单商品明细相同

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "checkout": {
      "id": "出库单ID",
      "code": "CK20240115001",
      "issueDate": "2024-01-15",
      "type": 10,
      "customerId": "客户ID",
      "amount": 3000.00,
      "quantity": 60.00,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

**业务逻辑说明**:
1. **事务处理**：出库单和商品明细的保存在一个事务中
2. **编号校验**：单据编号不能为空且不能重复
3. **自动计算**：系统会自动计算出库总成本和总数量
4. **库存处理**：
   - 保存成功后会自动减少对应仓库的库存
   - 根据出库类型设置业务类型（盘亏/其他出库）
5. **更新逻辑**：与入库单相同
6. **数据校验**：与入库单相同

---

### 2.5 删除出库单

**接口地址**: `POST /checkout/delete`

**接口描述**: 根据出库单ID删除出库单及其商品明细

**请求参数**:

```json
{
  "checkoutId": "出库单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| checkoutId | String | 是 | 出库单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除出库单的同时会删除所有商品明细
2. 会自动回滚库存（增加对应仓库的库存）
3. 删除前会检查出库单是否存在

---

## 3. 调拨管理 (Transfer)

**控制器路径**: `/transfer`

### 3.1 分页查询调拨单列表

**接口地址**: `POST /transfer/page`

**接口描述**: 分页查询调拨单列表，支持按日期范围筛选，自动关联制单人、审核人和商品明细

**请求参数**:

```json
{
  "query": {
    "startDate": "2024-01-01",
    "endDate": "2024-12-31",
    "keyword": "关键字",
    "checked": true
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.startDate | String | 否 | - | 开始日期 |
| query.endDate | String | 否 | - | 结束日期 |
| query.keyword | String | 否 | - | 关键字搜索 |
| query.checked | Boolean | 否 | - | 审核状态：true-已审核，false-未审核 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transferPage": {
      "records": [
        {
          "id": "调拨单ID",
          "issueDate": "2024-01-15",
          "code": "DB20240115001",
          "quantity": 100.00,
          "checked": false,
          "listerId": "制单人ID",
          "listerName": "制单人姓名",
          "auditorId": "审核人ID",
          "auditorName": "审核人姓名",
          "remark": "备注",
          "productList": [
            {
              "id": "调拨商品ID",
              "transferId": "调拨单ID",
              "productId": "商品ID",
              "productName": "商品名称",
              "unitName": "单位",
              "fromWarehouseId": "调出仓库ID",
              "fromWarehouseName": "调出仓库名称",
              "toWarehouseId": "调入仓库ID",
              "toWarehouseName": "调入仓库名称",
              "quantity": 50.00,
              "remark": "备注"
            }
          ],
          "createdTime": "2024-01-15T10:30:00",
          "updatedTime": "2024-01-15T10:30:00"
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
| id | String | 调拨单ID |
| issueDate | String | 单据日期 |
| code | String | 单据编号 |
| quantity | Double | 调拨总数量 |
| checked | Boolean | 审核状态 |
| listerId | String | 制单人ID |
| listerName | String | 制单人姓名 |
| auditorId | String | 审核人ID |
| auditorName | String | 审核人姓名 |
| remark | String | 备注 |
| productList | Array | 商品明细列表 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**productList数组字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 调拨商品记录ID |
| transferId | String | 关联的调拨单ID |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| unitName | String | 计量单位 |
| fromWarehouseId | String | 调出仓库ID |
| fromWarehouseName | String | 调出仓库名称 |
| toWarehouseId | String | 调入仓库ID |
| toWarehouseName | String | 调入仓库名称 |
| quantity | Double | 调拨数量 |
| remark | String | 备注 |

**业务逻辑说明**:
1. 自动关联查询制单人和审核人姓名
2. 自动加载每个调拨单的商品明细列表
3. 商品明细中包含调出仓库和调入仓库信息
4. 支持按审核状态筛选

---

### 3.2 获取调拨单详情

**接口地址**: `POST /transfer/detail`

**接口描述**: 根据调拨单ID获取调拨单详细信息，包含商品明细列表

**请求参数**:

```json
{
  "transferId": "调拨单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| transferId | String | 是 | 调拨单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transfer": {
      "id": "调拨单ID",
      "issueDate": "2024-01-15",
      "code": "DB20240115001",
      "quantity": 100.00,
      "checked": false,
      "listerId": "制单人ID",
      "listerName": "制单人姓名",
      "auditorId": "审核人ID",
      "auditorName": "审核人姓名",
      "remark": "备注",
      "productList": [
        {
          "id": "调拨商品ID",
          "transferId": "调拨单ID",
          "productId": "商品ID",
          "productName": "商品名称",
          "unitName": "单位",
          "fromWarehouseId": "调出仓库ID",
          "fromWarehouseName": "调出仓库名称",
          "toWarehouseId": "调入仓库ID",
          "toWarehouseName": "调入仓库名称",
          "quantity": 50.00,
          "remark": "备注"
        }
      ],
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

---

### 3.3 生成调拨单编号

**接口地址**: `POST /transfer/createCode`

**接口描述**: 自动生成一个新的调拨单编号

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "code": "DB20240115001"
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | String | 生成的单据编号，格式：DB + 日期 + 流水号 |

**业务逻辑说明**:
1. 编号格式：DB + yyyyMMdd + 3位流水号
2. 每天从001开始递增

---

### 3.4 保存调拨单

**接口地址**: `POST /transfer/save`

**接口描述**: 新增或更新调拨单，同时保存商品明细列表，自动处理库存调拨

**请求参数**:

```json
{
  "transfer": {
    "id": "调拨单ID（更新时必填，新增时不传）",
    "code": "DB20240115001",
    "issueDate": "2024-01-15",
    "listerId": "制单人ID",
    "remark": "备注"
  },
  "productList": [
    {
      "id": "调拨商品ID（更新时传，新增时不传）",
      "productId": "商品ID",
      "fromWarehouseId": "调出仓库ID",
      "toWarehouseId": "调入仓库ID",
      "quantity": 50.00,
      "remark": "备注"
    }
  ]
}
```

**transfer对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 调拨单ID，新增时不传 |
| code | String | 是 | 单据编号，不能重复 |
| issueDate | String | 是 | 单据日期 |
| listerId | String | 是 | 制单人ID |
| remark | String | 否 | 备注 |

**productList数组字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 调拨商品ID，新增时不传 |
| productId | String | 是 | 商品ID |
| fromWarehouseId | String | 是 | 调出仓库ID |
| toWarehouseId | String | 是 | 调入仓库ID |
| quantity | Double | 是 | 调拨数量 |
| remark | String | 否 | 备注 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "transfer": {
      "id": "调拨单ID",
      "code": "DB20240115001",
      "issueDate": "2024-01-15",
      "quantity": 100.00,
      "checked": false,
      "listerId": "制单人ID",
      "remark": "备注",
      "createdTime": "2024-01-15T10:30:00",
      "updatedTime": "2024-01-15T10:30:00"
    }
  }
}
```

**业务逻辑说明**:
1. **事务处理**：调拨单和商品明细的保存在一个事务中
2. **编号校验**：单据编号不能为空且不能重复
3. **自动计算**：系统会自动计算调拨总数量
4. **库存处理**：
   - 保存成功后会从调出仓库减少库存
   - 同时向调入仓库增加库存
   - 实现库存的转移
5. **审核状态**：新增调拨单默认未审核（checked=false）
6. **更新逻辑**：与入库单相同
7. **数据校验**：
   - 商品ID、调出仓库ID、调入仓库ID不能为空
   - 商品和仓库必须存在
   - 调出仓库和调入仓库不能相同

---

### 3.5 删除调拨单

**接口地址**: `POST /transfer/delete`

**接口描述**: 根据调拨单ID删除调拨单及其商品明细

**请求参数**:

```json
{
  "transferId": "调拨单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| transferId | String | 是 | 调拨单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除调拨单的同时会删除所有商品明细
2. 会自动回滚库存（调出仓库增加，调入仓库减少）
3. 只能删除未审核的调拨单
4. 删除前会检查调拨单是否存在

---

### 3.6 审核/反审核调拨单

**接口地址**: `POST /transfer/switchCheck`

**接口描述**: 切换调拨单的审核状态（审核/反审核）

**请求参数**:

```json
{
  "transferId": "调拨单ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| transferId | String | 是 | 调拨单ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "checked": true,
    "auditorId": "审核人ID",
    "auditorName": "审核人姓名"
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| checked | Boolean | 审核状态 |
| auditorId | String | 审核人ID（当前登录用户） |
| auditorName | String | 审核人姓名 |

**业务逻辑说明**:
1. 如果调拨单当前是未审核状态，则进行审核
2. 如果调拨单当前是已审核状态，则进行反审核
3. 审核时会记录审核人（当前登录用户）
4. 审核后的调拨单才能生效，库存才会真正转移
5. 只有未审核的调拨单才能删除

---

## 附录

### A. 入库类型说明

| 类型值 | 说明 | 业务场景 |
|--------|------|----------|
| 10 | 其他入库 | 非采购入库的其他入库业务 |
| 20 | 盘盈 | 库存盘点发现的实际库存多于账面库存 |

### B. 出库类型说明

| 类型值 | 说明 | 业务场景 |
|--------|------|----------|
| 10 | 其他出库 | 非销售出库的其他出库业务 |
| 20 | 盘亏 | 库存盘点发现的实际库存少于账面库存 |

### C. 单据编号规则

| 单据类型 | 编号格式 | 示例 |
|----------|----------|------|
| 入库单 | RK + yyyyMMdd + 3位流水号 | RK20240115001 |
| 出库单 | CK + yyyyMMdd + 3位流水号 | CK20240115001 |
| 调拨单 | DB + yyyyMMdd + 3位流水号 | DB20240115001 |

### D. 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查必填参数是否完整，日期格式是否正确 |
| 401 | 未授权 | 检查Token是否有效，重新登录 |
| 403 | 没有权限 | 联系管理员分配权限 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### E. 注意事项

1. **单据编号唯一性**: 同类型单据编号不能重复
2. **库存影响**: 
   - 入库单会增加库存
   - 出库单会减少库存
   - 调拨单会从一个仓库转移到另一个仓库
3. **审核机制**: 只有调拨单有审核机制，入库单和出库单保存即生效
4. **删除限制**: 
   - 只能删除未审核的调拨单
   - 删除会自动回滚库存
5. **数据一致性**: 所有操作都在事务中执行，保证数据一致性
6. **商品明细**: 保存单据时必须至少有一条商品明细
7. **仓库选择**: 入库和出库必须指定仓库，调拨必须指定调出和调入仓库
8. **金额计算**: 入库和出库的金额由系统自动计算（数量×单价）

### F. 业务流程说明

#### 入库业务流程
1. 生成入库单编号
2. 填写入库单基本信息（日期、类型、供应商等）
3. 添加入库商品明细（商品、仓库、数量、单价）
4. 保存入库单（自动增加库存）
5. 如需修改，可更新入库单（会重新计算并调整库存）
6. 如需删除，删除入库单（会自动回滚库存）

#### 出库业务流程
1. 生成出库单编号
2. 填写出库单基本信息（日期、类型、客户等）
3. 添加出库商品明细（商品、仓库、数量、单价）
4. 保存出库单（自动减少库存）
5. 如需修改，可更新出库单（会重新计算并调整库存）
6. 如需删除，删除出库单（会自动回滚库存）

#### 调拨业务流程
1. 生成调拨单编号
2. 填写调拨单基本信息（日期、制单人等）
3. 添加调拨商品明细（商品、调出仓库、调入仓库、数量）
4. 保存调拨单（此时库存未真正转移）
5. 审核调拨单（审核后库存真正转移）
6. 如需取消，可反审核调拨单（库存回滚）
7. 只能删除未审核的调拨单

### G. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含入库单、出库单、调拨单所有管理接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
