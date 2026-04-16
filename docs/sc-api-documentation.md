# SC模块 API接口文档

> **统计分析模块（Statistics Center）** - 包含采购、销售、库存、应收应付、资金流水等全面的业务分析报表接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 采购分析报表](#1-采购分析报表)
  - [1.1 采购明细表](#11-采购明细表)
  - [1.2 采购汇总表（按商品）](#12-采购汇总表按商品)
  - [1.3 采购汇总表（按供应商）](#13-采购汇总表按供应商)
- [2. 销售分析报表](#2-销售分析报表)
  - [2.1 销售明细表](#21-销售明细表)
  - [2.2 销售汇总表（按商品）](#22-销售汇总表按商品)
  - [2.3 销售汇总表（按客户）](#23-销售汇总表按客户)
- [3. 库存分析报表](#3-库存分析报表)
  - [3.1 收发明细表](#31-收发明细表)
  - [3.2 库存余额表](#32-库存余额表)
  - [3.3 收发汇总表](#33-收发汇总表)
- [4. 资金分析报表](#4-资金分析报表)
  - [4.1 现金银行报表](#41-现金银行报表)
- [5. 应收应付报表](#5-应收应付报表)
  - [5.1 应收账款明细表](#51-应收账款明细表)
  - [5.2 应付账款明细表](#52-应付账款明细表)
  - [5.3 客户对账单](#53-客户对账单)
  - [5.4 供应商对账单](#54-供应商对账单)
- [6. 其他收支报表](#6-其他收支报表)
  - [6.1 其他收支明细表](#61-其他收支明细表)

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

### 日期格式规范

- 所有日期参数使用 `yyyy-MM-dd` 格式
- 日期范围查询时，开始时间和结束时间都是可选的
- 如果不传日期参数，则查询全部数据

---

## 1. 采购分析报表

**控制器路径**: `/analysis/purchase`

### 1.1 采购明细表

**接口地址**: `POST /analysis/purchase/detailList`

**接口描述**: 查询采购业务的详细记录，包含每条采购商品的详细信息

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "supplierIdList": ["供应商ID1", "供应商ID2"],
  "productIdList": ["商品ID1", "商品ID2"],
  "warehouseIdList": ["仓库ID1", "仓库ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期，格式：yyyy-MM-dd |
| endDate | String | 否 | 结束日期，格式：yyyy-MM-dd |
| supplierIdList | Array | 否 | 供应商ID列表，不传则查询所有供应商 |
| productIdList | Array | 否 | 商品ID列表，不传则查询所有商品 |
| warehouseIdList | Array | 否 | 仓库ID列表，不传则查询所有仓库 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productList": [
      {
        "id": "出入库产品ID",
        "businessId": "购货单ID",
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "quantity": 100.00,
        "price": 50.00,
        "amount": 5000.00,
        "issueDate": "2024-01-15",
        "purchaseCode": "PL20240115001",
        "type": "buy",
        "supplierName": "供应商名称"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 出入库产品记录ID |
| businessId | String | 关联的购货单ID |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位名称 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| quantity | Double | 数量 |
| price | Double | 单价 |
| amount | Double | 金额 |
| issueDate | String | 单据日期 |
| purchaseCode | String | 购货单编号 |
| type | String | 业务类型：buy-采购，refund-退货 |
| supplierName | String | 供应商名称 |

**业务逻辑说明**:
1. 支持多条件组合筛选
2. 返回的是每一笔采购的商品明细记录
3. 自动关联查询商品、供应商、仓库等信息
4. 包含采购和采购退货两种业务类型

---

### 1.2 采购汇总表（按商品）

**接口地址**: `POST /analysis/purchase/productSummaryList`

**接口描述**: 按商品维度汇总采购数据，统计每个商品的采购总量和总金额

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "supplierIdList": ["供应商ID1"],
  "productIdList": ["商品ID1"],
  "warehouseIdList": ["仓库ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| supplierIdList | Array | 否 | 供应商ID列表 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summaryList": [
      {
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "totalQuantity": 500.00,
        "totalAmount": 25000.00,
        "averagePrice": 50.00
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| totalQuantity | Double | 采购总数量 |
| totalAmount | Double | 采购总金额 |
| averagePrice | Double | 平均单价 |

**业务逻辑说明**:
1. 按商品和仓库进行分组汇总
2. 统计指定时间段内的采购总量和总金额
3. 自动计算平均单价
4. 支持多维度筛选条件

---

### 1.3 采购汇总表（按供应商）

**接口地址**: `POST /analysis/purchase/supplierSummaryList`

**接口描述**: 按供应商维度汇总采购数据，统计每个供应商的采购情况

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "supplierIdList": ["供应商ID1"],
  "productIdList": ["商品ID1"],
  "warehouseIdList": ["仓库ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| supplierIdList | Array | 否 | 供应商ID列表 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summaryList": [
      {
        "supplierId": "供应商ID",
        "supplierName": "供应商名称",
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "totalQuantity": 300.00,
        "totalAmount": 15000.00
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称 |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| totalQuantity | Double | 采购总数量 |
| totalAmount | Double | 采购总金额 |

**业务逻辑说明**:
1. 按供应商、商品和仓库进行分组汇总
2. 便于分析各供应商的供货情况
3. 支持多维度筛选和统计

---

## 2. 销售分析报表

**控制器路径**: `/analysis/sale`

### 2.1 销售明细表

**接口地址**: `POST /analysis/sale/detailList`

**接口描述**: 查询销售业务的详细记录，包含每条销售商品的详细信息

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "customerIdList": ["客户ID1", "客户ID2"],
  "productIdList": ["商品ID1", "商品ID2"],
  "warehouseIdList": ["仓库ID1", "仓库ID2"],
  "sellerIdList": ["销售人员ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| customerIdList | Array | 否 | 客户ID列表 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |
| sellerIdList | Array | 否 | 销售人员ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productList": [
      {
        "id": "出入库产品ID",
        "businessId": "销货单ID",
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "quantity": 50.00,
        "price": 80.00,
        "amount": 4000.00,
        "issueDate": "2024-01-20",
        "saleCode": "SE20240120001",
        "type": "sell",
        "customerName": "客户名称",
        "sellerName": "销售人员姓名"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 出入库产品记录ID |
| businessId | String | 关联的销货单ID |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| quantity | Double | 数量 |
| price | Double | 单价 |
| amount | Double | 金额 |
| issueDate | String | 单据日期 |
| saleCode | String | 销货单编号 |
| type | String | 业务类型：sell-销售，returned-退货 |
| customerName | String | 客户名称 |
| sellerName | String | 销售人员姓名 |

**业务逻辑说明**:
1. 支持按客户、商品、仓库、销售人员多维度筛选
2. 返回每一笔销售的商品明细记录
3. 包含销售和销退两种业务类型
4. 自动关联查询客户、销售人员等信息

---

### 2.2 销售汇总表（按商品）

**接口地址**: `POST /analysis/sale/productSummaryList`

**接口描述**: 按商品维度汇总销售数据，统计每个商品的销售情况

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "customerIdList": ["客户ID1"],
  "productIdList": ["商品ID1"],
  "warehouseIdList": ["仓库ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| customerIdList | Array | 否 | 客户ID列表 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summaryList": [
      {
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "totalQuantity": 200.00,
        "totalAmount": 16000.00,
        "averagePrice": 80.00
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| totalQuantity | Double | 销售总数量 |
| totalAmount | Double | 销售总金额 |
| averagePrice | Double | 平均单价 |

**业务逻辑说明**:
1. 按商品和仓库进行分组汇总
2. 统计指定时间段内的销售总量和总金额
3. 便于分析商品销售表现

---

### 2.3 销售汇总表（按客户）

**接口地址**: `POST /analysis/sale/customerSummaryList`

**接口描述**: 按客户维度汇总销售数据，统计每个客户的购买情况

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "customerIdList": ["客户ID1"],
  "productIdList": ["商品ID1"],
  "warehouseIdList": ["仓库ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| customerIdList | Array | 否 | 客户ID列表 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summaryList": [
      {
        "customerId": "客户ID",
        "customerName": "客户名称",
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "totalQuantity": 100.00,
        "totalAmount": 8000.00
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| customerId | String | 客户ID |
| customerName | String | 客户名称 |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| totalQuantity | Double | 销售总数量 |
| totalAmount | Double | 销售总金额 |

**业务逻辑说明**:
1. 按客户、商品和仓库进行分组汇总
2. 便于分析客户购买行为和贡献度
3. 支持客户价值分析

---

## 3. 库存分析报表

**控制器路径**: `/analysis/stock`

### 3.1 收发明细表

**接口地址**: `POST /analysis/stock/detailList`

**接口描述**: 查询商品出入库的详细流水记录，包含所有类型的出入库业务

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "productIdList": ["商品ID1", "商品ID2"],
  "warehouseIdList": ["仓库ID1", "仓库ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recordList": [
      {
        "id": "库存记录ID",
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "stockType": 1,
        "stockInQuantity": 100.00,
        "stockOutQuantity": 0.00,
        "quantity": 100.00,
        "price": 50.00,
        "currentQuantity": 500.00,
        "businessType": "buy",
        "businessTypeName": "购货",
        "issueCode": "PL20240115001",
        "relatedUnit": "供应商名称",
        "createdTime": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 库存记录ID |
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| stockType | Integer | 库存类型：1-入库，2-出库 |
| stockInQuantity | Double | 入库数量 |
| stockOutQuantity | Double | 出库数量 |
| quantity | Double | 本次变动数量 |
| price | Double | 单价 |
| currentQuantity | Double | 当前库存数量 |
| businessType | String | 业务类型 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| relatedUnit | String | 往来单位（客户或供应商） |
| createdTime | DateTime | 创建时间 |

**业务类型说明**:

| 业务类型 | 说明 |
|----------|------|
| buy | 购货入库 |
| refund | 购货退货出库 |
| sell | 销货出库 |
| returned | 销退入库 |
| transfer_in | 调拨入库 |
| transfer_out | 调拨出库 |
| store_profit | 盘盈入库 |
| store_other | 其他入库 |
| checkout_loss | 盘亏出库 |
| checkout_other | 其他出库 |

**业务逻辑说明**:
1. 包含所有类型的出入库业务
2. 每条记录显示入库数量和出库数量（其中一个为0）
3. 显示当前库存数量和变动后的结存
4. 自动关联单据信息和往来单位

---

### 3.2 库存余额表

**接口地址**: `POST /analysis/stock/amountList`

**接口描述**: 查询截止到指定日期的商品库存余额，支持多仓库展示

**请求参数**:

```json
{
  "endDate": "2024-12-31",
  "productIdList": ["商品ID1", "商品ID2"],
  "warehouseIdList": ["仓库ID1", "仓库ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| endDate | String | 否 | 截止日期，不传则为当前日期 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "productList": [
      {
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseAmountMapping": {
          "仓库ID1": {
            "warehouseId": "仓库ID1",
            "warehouseName": "仓库名称1",
            "currentQuantity": 100.00,
            "currentAmount": 5000.00
          },
          "仓库ID2": {
            "warehouseId": "仓库ID2",
            "warehouseName": "仓库名称2",
            "currentQuantity": 200.00,
            "currentAmount": 10000.00
          },
          "total": {
            "totalQuantity": 300.00,
            "totalAmount": 15000.00
          }
        }
      }
    ],
    "warehouseList": [
      {
        "id": "仓库ID1",
        "name": "仓库名称1"
      },
      {
        "id": "仓库ID2",
        "name": "仓库名称2"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseAmountMapping | Object | 各仓库库存映射 |
| warehouseAmountMapping[仓库ID] | Object | 单个仓库库存信息 |
| warehouseAmountMapping[仓库ID].warehouseId | String | 仓库ID |
| warehouseAmountMapping[仓库ID].warehouseName | String | 仓库名称 |
| warehouseAmountMapping[仓库ID].currentQuantity | Double | 当前库存数量 |
| warehouseAmountMapping[仓库ID].currentAmount | Double | 当前库存金额 |
| warehouseAmountMapping.total | Object | 合计信息 |
| warehouseAmountMapping.total.totalQuantity | Double | 总库存数量 |
| warehouseAmountMapping.total.totalAmount | Double | 总库存金额 |
| warehouseList | Array | 仓库列表 |

**业务逻辑说明**:
1. 查询截止到指定日期的库存余额
2. 按商品分组，展示每个商品在各仓库的分布
3. 自动计算每个商品的总库存数量和金额
4. 同时返回仓库列表，便于前端表格列展示

---

### 3.3 收发汇总表

**接口地址**: `POST /analysis/stock/summaryList`

**接口描述**: 按商品和仓库汇总统计期初、入库、出库、期末的数量和金额

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "productIdList": ["商品ID1"],
  "warehouseIdList": ["仓库ID1"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| productIdList | Array | 否 | 商品ID列表 |
| warehouseIdList | Array | 否 | 仓库ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "stockList": [
      {
        "productId": "商品ID",
        "productName": "商品名称",
        "spec": "规格型号",
        "productCode": "商品编码",
        "unitName": "单位",
        "warehouseId": "仓库ID",
        "warehouseName": "仓库名称",
        "startQuantity": 100.00,
        "startAmount": 5000.00,
        "buyQuantity": 500.00,
        "returnedQuantity": 50.00,
        "transferInQuantity": 30.00,
        "storeProfitQuantity": 10.00,
        "storeOtherQuantity": 20.00,
        "storeTotalQuantity": 610.00,
        "sellQuantity": 300.00,
        "refundQuantity": 20.00,
        "transferOutQuantity": 40.00,
        "checkoutLossQuantity": 5.00,
        "checkoutOtherQuantity": 15.00,
        "checkoutTotalQuantity": 380.00,
        "endQuantity": 330.00,
        "endAmount": 16500.00
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| productId | String | 商品ID |
| productName | String | 商品名称 |
| spec | String | 规格型号 |
| productCode | String | 商品编码 |
| unitName | String | 计量单位 |
| warehouseId | String | 仓库ID |
| warehouseName | String | 仓库名称 |
| startQuantity | Double | 期初数量 |
| startAmount | Double | 期初金额 |
| buyQuantity | Double | 采购入库数量 |
| returnedQuantity | Double | 销退入库数量 |
| transferInQuantity | Double | 调拨入库数量 |
| storeProfitQuantity | Double | 盘盈数量 |
| storeOtherQuantity | Double | 其他入库数量 |
| storeTotalQuantity | Double | 入库合计数量 |
| sellQuantity | Double | 销售出库数量 |
| refundQuantity | Double | 购退出库数量 |
| transferOutQuantity | Double | 调拨出库数量 |
| checkoutLossQuantity | Double | 盘亏数量 |
| checkoutOtherQuantity | Double | 其他出库数量 |
| checkoutTotalQuantity | Double | 出库合计数量 |
| endQuantity | Double | 期末数量 |
| endAmount | Double | 期末金额 |

**计算公式**:
- 入库合计 = 采购入库 + 销退入库 + 调拨入库 + 盘盈 + 其他入库
- 出库合计 = 销售出库 + 购退出库 + 调拨出库 + 盘亏 + 其他出库
- 期末数量 = 期初数量 + 入库合计 - 出库合计

**业务逻辑说明**:
1. 按商品和仓库分组统计
2. 分别统计各种业务类型的出入库数量
3. 自动计算期初、入库合计、出库合计、期末
4. 提供完整的库存流转分析

---

## 4. 资金分析报表

**控制器路径**: `/analysis/account`

### 4.1 现金银行报表

**接口地址**: `POST /analysis/account/detailList`

**接口描述**: 查询结算账户的资金流水明细，包含所有收支业务

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "accountIdList": ["账户ID1", "账户ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| accountIdList | Array | 否 | 结算账户ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recordList": [
      {
        "id": "账户记录ID",
        "accountId": "账户ID",
        "accountCode": "账户编码",
        "accountName": "账户名称",
        "type": 1,
        "incomeAmount": 10000.00,
        "expenseAmount": 0.00,
        "amount": 10000.00,
        "balance": 50000.00,
        "businessType": "collection",
        "businessTypeName": "收款",
        "issueCode": "CL20240120001",
        "relatedUnit": "客户名称",
        "createdTime": "2024-01-20T14:30:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 账户记录ID |
| accountId | String | 结算账户ID |
| accountCode | String | 账户编码 |
| accountName | String | 账户名称 |
| type | Integer | 类型：1-收入，2-支出 |
| incomeAmount | Double | 收入金额 |
| expenseAmount | Double | 支出金额 |
| amount | Double | 变动金额 |
| balance | Double | 账户余额 |
| businessType | String | 业务类型 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| relatedUnit | String | 往来单位 |
| createdTime | DateTime | 创建时间 |

**业务类型说明**:

| 业务类型 | 说明 |
|----------|------|
| buy | 普通采购付款 |
| refund | 采购退回收款 |
| sell | 普通销售收款 |
| returned | 销售退回付款 |
| collection | 收款 |
| payment | 付款 |
| income | 其他收入 |
| expense | 其他支出 |

**业务逻辑说明**:
1. 展示所有结算账户的资金流水
2. 收入和支出分别显示，便于对账
3. 显示每笔交易后的账户余额
4. 关联原始单据和往来单位信息

---

## 5. 应收应付报表

**控制器路径**: `/analysis/receivable` 和 `/analysis/payable`

### 5.1 应收账款明细表

**接口地址**: `POST /analysis/receivable/detailList`

**接口描述**: 查询应收账款的明细记录，包含销货和收款业务

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "customerIdList": ["客户ID1", "客户ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| customerIdList | Array | 否 | 客户ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "receivableList": [
      {
        "id": "应收记录ID",
        "customerId": "客户ID",
        "customerName": "客户名称",
        "businessType": "sell",
        "businessTypeName": "销货",
        "issueCode": "SE20240120001",
        "amount": 10000.00,
        "receivableAmount": 10000.00,
        "receivedAmount": 0.00,
        "unreceivedAmount": 10000.00,
        "createdTime": "2024-01-20T10:00:00"
      },
      {
        "id": "应收记录ID",
        "customerId": "客户ID",
        "customerName": "客户名称",
        "businessType": "collection",
        "businessTypeName": "收款",
        "issueCode": "CL20240125001",
        "amount": 5000.00,
        "receivableAmount": -5000.00,
        "receivedAmount": 5000.00,
        "unreceivedAmount": 5000.00,
        "createdTime": "2024-01-25T15:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 应收记录ID |
| customerId | String | 客户ID |
| customerName | String | 客户名称 |
| businessType | String | 业务类型：sell-销货，returned-销退，collection-收款 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| amount | Double | 单据金额 |
| receivableAmount | Double | 应收金额（正数为增加应收，负数为减少应收） |
| receivedAmount | Double | 已收金额 |
| unreceivedAmount | Double | 未收金额 |
| createdTime | DateTime | 创建时间 |

**业务逻辑说明**:
1. 包含销货（增加应收）和收款（减少应收）两类业务
2. 销退业务会减少应收账款
3. 便于跟踪每个客户的应收情况
4. 可以计算客户的欠款余额

---

### 5.2 应付账款明细表

**接口地址**: `POST /analysis/payable/detailList`

**接口描述**: 查询应付账款的明细记录，包含购货和付款业务

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "supplierIdList": ["供应商ID1", "供应商ID2"]
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| supplierIdList | Array | 否 | 供应商ID列表 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "payableList": [
      {
        "id": "应付记录ID",
        "supplierId": "供应商ID",
        "supplierName": "供应商名称",
        "businessType": "buy",
        "businessTypeName": "购货",
        "issueCode": "PL20240115001",
        "amount": 8000.00,
        "payableAmount": 8000.00,
        "paidAmount": 0.00,
        "unpaidAmount": 8000.00,
        "createdTime": "2024-01-15T10:00:00"
      },
      {
        "id": "应付记录ID",
        "supplierId": "供应商ID",
        "supplierName": "供应商名称",
        "businessType": "payment",
        "businessTypeName": "付款",
        "issueCode": "FK20240120001",
        "amount": 4000.00,
        "payableAmount": -4000.00,
        "paidAmount": 4000.00,
        "unpaidAmount": 4000.00,
        "createdTime": "2024-01-20T15:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 应付记录ID |
| supplierId | String | 供应商ID |
| supplierName | String | 供应商名称 |
| businessType | String | 业务类型：buy-购货，refund-购退，payment-付款 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| amount | Double | 单据金额 |
| payableAmount | Double | 应付金额（正数为增加应付，负数为减少应付） |
| paidAmount | Double | 已付金额 |
| unpaidAmount | Double | 未付金额 |
| createdTime | DateTime | 创建时间 |

**业务逻辑说明**:
1. 包含购货（增加应付）和付款（减少应付）两类业务
2. 购退业务会减少应付账款
3. 便于跟踪每个供应商的应付情况
4. 可以计算欠供应商的款项

---

### 5.3 客户对账单

**接口地址**: `POST /analysis/receivable/customerList`

**接口描述**: 生成指定客户的对账单，包含该客户的所有应收业务明细

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "customerId": "客户ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| customerId | String | **是** | 客户ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "receivableList": [
      {
        "id": "应收记录ID",
        "businessType": "sell",
        "businessTypeName": "销货",
        "issueCode": "SE20240120001",
        "amount": 10000.00,
        "discountAmount": 500.00,
        "preferredAmount": 200.00,
        "actualAmount": 9300.00,
        "receivableAmount": 9300.00,
        "createdTime": "2024-01-20T10:00:00"
      },
      {
        "id": "应收记录ID",
        "businessType": "collection",
        "businessTypeName": "收款",
        "issueCode": "CL20240125001",
        "actualAmount": 5000.00,
        "receivableAmount": -5000.00,
        "createdTime": "2024-01-25T15:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 应收记录ID |
| businessType | String | 业务类型 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| amount | Double | 单据金额（仅销货/销退） |
| discountAmount | Double | 优惠金额（仅销货/销退） |
| preferredAmount | Double | 折让金额（仅销货/销退） |
| actualAmount | Double | 实际金额 |
| receivableAmount | Double | 应收金额 |
| createdTime | DateTime | 创建时间 |

**业务逻辑说明**:
1. **必须传入客户ID**
2. 展示该客户在指定时间段内的所有应收业务
3. 包含销货、销退、收款三类业务
4. 销货业务显示详细的金额构成（原价、优惠、折让、实收）
5. 可用于与客户对账

---

### 5.4 供应商对账单

**接口地址**: `POST /analysis/payable/supplierList`

**接口描述**: 生成指定供应商的对账单，包含该供应商的所有应付业务明细

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "supplierId": "供应商ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| supplierId | String | **是** | 供应商ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "payableList": [
      {
        "id": "应付记录ID",
        "businessType": "buy",
        "businessTypeName": "购货",
        "issueCode": "PL20240115001",
        "amount": 8000.00,
        "discountAmount": 400.00,
        "preferredAmount": 100.00,
        "actualAmount": 7500.00,
        "payableAmount": 7500.00,
        "createdTime": "2024-01-15T10:00:00"
      },
      {
        "id": "应付记录ID",
        "businessType": "payment",
        "businessTypeName": "付款",
        "issueCode": "FK20240120001",
        "actualAmount": 4000.00,
        "payableAmount": -4000.00,
        "createdTime": "2024-01-20T15:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 应付记录ID |
| businessType | String | 业务类型 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| amount | Double | 单据金额（仅购货/购退） |
| discountAmount | Double | 优惠金额（仅购货/购退） |
| preferredAmount | Double | 折让金额（仅购货/购退） |
| actualAmount | Double | 实际金额 |
| payableAmount | Double | 应付金额 |
| createdTime | DateTime | 创建时间 |

**业务逻辑说明**:
1. **必须传入供应商ID**
2. 展示该供应商在指定时间段内的所有应付业务
3. 包含购货、购退、付款三类业务
4. 购货业务显示详细的金额构成
5. 可用于与供应商对账

---

## 6. 其他收支报表

**控制器路径**: `/analysis/flow`

### 6.1 其他收支明细表

**接口地址**: `POST /analysis/flow/detailList`

**接口描述**: 查询其他收入和其他支出的明细记录

**请求参数**:

```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "categoryId": "类别ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startDate | String | 否 | 开始日期 |
| endDate | String | 否 | 结束日期 |
| categoryId | String | 否 | 收支类别ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "recordList": [
      {
        "id": "流水记录ID",
        "businessType": "income",
        "businessTypeName": "其他收入",
        "issueCode": "SR20240120001",
        "categoryId": "类别ID",
        "categoryName": "类别名称",
        "incomeAmount": 2000.00,
        "expenseAmount": 0.00,
        "relatedUnit": "客户名称",
        "createdTime": "2024-01-20T10:00:00"
      },
      {
        "id": "流水记录ID",
        "businessType": "expense",
        "businessTypeName": "其他支出",
        "issueCode": "ZC20240125001",
        "categoryId": "类别ID",
        "categoryName": "类别名称",
        "incomeAmount": 0.00,
        "expenseAmount": 1500.00,
        "relatedUnit": "供应商名称",
        "createdTime": "2024-01-25T15:00:00"
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 流水记录ID |
| businessType | String | 业务类型：income-收入，expense-支出 |
| businessTypeName | String | 业务类型名称 |
| issueCode | String | 单据编号 |
| categoryId | String | 收支类别ID |
| categoryName | String | 收支类别名称 |
| incomeAmount | Double | 收入金额 |
| expenseAmount | Double | 支出金额 |
| relatedUnit | String | 往来单位 |
| createdTime | DateTime | 创建时间 |

**业务逻辑说明**:
1. 包含其他收入和其他支出两类业务
2. 收入和支出分别显示金额
3. 按收支类别进行分类统计
4. 关联往来单位信息

---

## 附录

### A. 业务类型常量说明

#### 采购业务类型

| 类型值 | 说明 |
|--------|------|
| buy | 普通采购 |
| refund | 采购退货 |

#### 销售业务类型

| 类型值 | 说明 |
|--------|------|
| sell | 普通销售 |
| returned | 销售退货 |

#### 库存业务类型

| 类型值 | 说明 |
|--------|------|
| buy | 购货入库 |
| refund | 购货退货出库 |
| sell | 销货出库 |
| returned | 销退入库 |
| transfer_in | 调拨入库 |
| transfer_out | 调拨出库 |
| store_profit | 盘盈入库 |
| store_other | 其他入库 |
| checkout_loss | 盘亏出库 |
| checkout_other | 其他出库 |

#### 资金业务类型

| 类型值 | 说明 |
|--------|------|
| buy | 采购付款 |
| refund | 采购退款 |
| sell | 销售收款 |
| returned | 销售退款 |
| collection | 收款 |
| payment | 付款 |
| income | 其他收入 |
| expense | 其他支出 |

### B. 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查日期格式是否正确（yyyy-MM-dd） |
| 401 | 未授权 | 检查登录状态，重新登录 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### C. 注意事项

1. **日期格式**: 所有日期参数必须使用 `yyyy-MM-dd` 格式
2. **日期校验**: 系统会自动校验日期格式的合法性
3. **可选参数**: 大部分筛选参数都是可选的，不传则查询全部数据
4. **必填参数**: 客户对账单和供应商对账单必须传入客户ID或供应商ID
5. **性能优化**: 建议传入筛选条件以减少数据量，提高查询性能
6. **数据一致性**: 所有报表数据都基于已审核的单据
7. **金额精度**: 金额字段保留2位小数，数量字段根据系统配置保留相应精度

### D. 报表使用建议

1. **采购分析**: 
   - 使用采购明细表查看具体采购记录
   - 使用采购汇总表分析供应商合作情况和商品采购趋势

2. **销售分析**:
   - 使用销售明细表查看具体销售记录
   - 使用销售汇总表分析客户贡献度和商品销售表现

3. **库存分析**:
   - 使用收发明细表追踪库存变动历史
   - 使用库存余额表了解当前库存状况
   - 使用收发汇总表进行库存周转分析

4. **财务分析**:
   - 使用现金银行报表监控资金流动
   - 使用应收应付明细表跟踪债权债务
   - 使用对账单与客户/供应商对账

5. **其他收支**:
   - 使用其他收支明细表管理非主营业务收支

### E. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含采购、销售、库存、资金、应收应付、其他收支所有分析报表接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
