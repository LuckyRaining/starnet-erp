# RC模块 API接口文档

> **基础配置模块（Resource Center）** - 包含分类管理、字典管理、系统配置、日志管理、菜单管理等基础功能接口

**文档版本**: v1.0  
**最后更新**: 2026-04-16  
**基础路径**: `/api` (根据实际部署配置)  
**请求方式**: 所有接口均为 `POST`  
**Content-Type**: `application/json`

---

## 目录

- [1. 分类管理 (Category)](#1-分类管理-category)
  - [1.1 获取分类列表](#11-获取分类列表)
  - [1.2 获取分类详情](#12-获取分类详情)
  - [1.3 保存分类](#13-保存分类)
  - [1.4 删除分类](#14-删除分类)
- [2. 字典管理 (Dict)](#2-字典管理-dict)
  - [2.1 获取字典项列表](#21-获取字典项列表)
  - [2.2 获取字典项详情](#22-获取字典项详情)
  - [2.3 保存字典项](#23-保存字典项)
  - [2.4 删除字典项](#24-删除字典项)
- [3. 系统配置 (System Configuration)](#3-系统配置-system-configuration)
  - [3.1 获取系统配置](#31-获取系统配置)
  - [3.2 设置系统配置](#32-设置系统配置)
- [4. 日志管理 (Log)](#4-日志管理-log)
  - [4.1 分页查询日志列表](#41-分页查询日志列表)
- [5. 菜单管理 (Menu)](#5-菜单管理-menu)
  - [5.1 获取菜单列表](#51-获取菜单列表)
  - [5.2 添加菜单](#52-添加菜单)

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

## 1. 分类管理 (Category)

**控制器路径**: `/category`

### 1.1 获取分类列表

**接口地址**: `POST /category/list`

**接口描述**: 根据分类类型获取分类列表，支持级联查询子分类（仅商品分类）

**请求参数**:

```json
{
  "type": 10,
  "query": {
    "keyword": "关键字"
  }
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| type | Integer | 是 | - | 分类类型：10-商品分类，20-客户分类，30-供应商分类等 |
| query | Object | 否 | {} | 查询条件对象 |
| query.keyword | String | 否 | - | 关键字搜索 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "categoryList": [
      {
        "id": "分类ID",
        "type": 10,
        "parentId": null,
        "name": "电子产品",
        "sortNumber": 1,
        "childList": [
          {
            "id": "子分类ID",
            "type": 10,
            "parentId": "父分类ID",
            "name": "手机",
            "sortNumber": 1,
            "childList": []
          }
        ]
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 分类ID |
| type | Integer | 分类类型 |
| parentId | String | 父分类ID，顶级分类为null |
| name | String | 分类名称 |
| sortNumber | Integer | 排序号 |
| childList | Array | 子分类列表（仅商品分类有此字段） |

**业务逻辑说明**:
1. 必须传入分类类型（type）
2. 如果是商品分类（type=10），会递归查询所有子分类，形成树形结构
3. 其他类型分类只返回一级分类

---

### 1.2 获取分类详情

**接口地址**: `POST /category/detail`

**接口描述**: 根据分类ID获取分类详细信息

**请求参数**:

```json
{
  "categoryId": "分类ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | String | 是 | 分类ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "category": {
      "id": "分类ID",
      "type": 10,
      "parentId": "父分类ID",
      "name": "分类名称",
      "sortNumber": 1,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 分类ID |
| type | Integer | 分类类型 |
| parentId | String | 父分类ID |
| name | String | 分类名称 |
| sortNumber | Integer | 排序号 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 1.3 保存分类

**接口地址**: `POST /category/save`

**接口描述**: 新增或更新分类

**请求参数**:

```json
{
  "category": {
    "id": "分类ID（更新时必填，新增时不传）",
    "type": 10,
    "parentId": "父分类ID（仅商品分类需要）",
    "name": "分类名称"
  }
}
```

**category对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 分类ID，新增时不传，更新时必填 |
| type | Integer | 是 | 分类类型 |
| parentId | String | 否 | 父分类ID，仅商品分类且为子分类时需要 |
| name | String | 是 | 分类名称 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "category": {
      "id": "分类ID",
      "type": 10,
      "parentId": "父分类ID",
      "name": "分类名称",
      "sortNumber": 0,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增分类时，系统会自动设置 `sortNumber=0`
2. 更新分类时，只能修改名称和父分类
3. 商品分类支持父子层级关系，其他类型分类不支持
4. 需要校验分类类型是否合法
5. 如果指定了父分类ID，会校验父分类是否存在

---

### 1.4 删除分类

**接口地址**: `POST /category/delete`

**接口描述**: 根据分类ID删除分类

**请求参数**:

```json
{
  "categoryId": "分类ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| categoryId | String | 是 | 分类ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 删除前会检查该分类是否有子分类
2. 如果存在子分类，会抛出异常："请先删除子分类！"
3. 只有没有子分类的分类才能被删除

---

## 2. 字典管理 (Dict)

**控制器路径**: `/dict`

### 2.1 获取字典项列表

**接口地址**: `POST /dict/itemList`

**接口描述**: 根据字典编码获取字典项列表

**请求参数**:

```json
{
  "dictCode": "unit"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| dictCode | String | 是 | 字典编码，如：unit（单位）、settlement（结算方式）等 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "itemList": [
      {
        "id": "字典项ID",
        "dictCode": "unit",
        "name": "个",
        "sortNumber": 1,
        "createdTime": "2024-01-01T00:00:00",
        "updatedTime": "2024-01-01T00:00:00"
      },
      {
        "id": "字典项ID",
        "dictCode": "unit",
        "name": "件",
        "sortNumber": 2,
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
| id | String | 字典项ID |
| dictCode | String | 字典编码 |
| name | String | 字典项名称 |
| sortNumber | Integer | 排序号 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**常见字典编码**:
- `unit`: 计量单位
- `settlement`: 结算方式

---

### 2.2 获取字典项详情

**接口地址**: `POST /dict/itemDetail`

**接口描述**: 根据字典项ID获取字典项详细信息

**请求参数**:

```json
{
  "itemId": "字典项ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| itemId | String | 是 | 字典项ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "item": {
      "id": "字典项ID",
      "dictCode": "unit",
      "name": "个",
      "sortNumber": 1,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 字典项ID |
| dictCode | String | 字典编码 |
| name | String | 字典项名称 |
| sortNumber | Integer | 排序号 |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

---

### 2.3 保存字典项

**接口地址**: `POST /dict/itemSave`

**接口描述**: 新增或更新字典项

**请求参数**:

```json
{
  "item": {
    "id": "字典项ID（更新时必填，新增时不传）",
    "dictCode": "unit",
    "name": "千克"
  }
}
```

**item对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 否 | 字典项ID，新增时不传，更新时必填 |
| dictCode | String | 是 | 字典编码 |
| name | String | 是 | 字典项名称 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "item": {
      "id": "字典项ID",
      "dictCode": "unit",
      "name": "千克",
      "sortNumber": 0,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 新增字典项时，系统会自动设置 `sortNumber=0`
2. 非开发模式下，只能修改 `unit`（单位）和 `settlement`（结算方式）两种字典
3. 其他字典项在非开发模式下修改会抛出异常："没有权限！"
4. 需要校验字典编码是否存在
5. 字典项名称不能为空

---

### 2.4 删除字典项

**接口地址**: `POST /dict/itemDelete`

**接口描述**: 根据字典项ID删除字典项

**请求参数**:

```json
{
  "itemId": "字典项ID"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| itemId | String | 是 | 字典项ID |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**业务逻辑说明**:
1. 直接删除指定的字典项
2. 删除前会校验字典项是否存在

---

## 3. 系统配置 (System Configuration)

**控制器路径**: `/` (根路径)

### 3.1 获取系统配置

**接口地址**: `POST /getSystemConfiguration`

**接口描述**: 获取系统配置信息

**请求参数**: 无

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "configuration": {
      "companyName": "公司名称",
      "companyAddress": "公司地址",
      "companyPhone": "公司电话",
      "companyFax": "公司传真",
      "companyPostCode": "公司邮编",
      "startTime": "2024-01-01T00:00:00",
      "currency": "RMB",
      "quantityPrecision": 2,
      "pricePrecision": 2,
      "inventoryValuationMethod": 10,
      "checkNegativeStock": false
    }
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| companyName | String | 公司名称 |
| companyAddress | String | 公司地址 |
| companyPhone | String | 公司电话 |
| companyFax | String | 公司传真 |
| companyPostCode | String | 公司邮编 |
| startTime | DateTime | 启用时间 |
| currency | String | 本位币 |
| quantityPrecision | Integer | 数量精度（小数位数） |
| pricePrecision | Integer | 单价精度（小数位数） |
| inventoryValuationMethod | Integer | 存货计价方法 |
| checkNegativeStock | Boolean | 是否检查负库存 |

---

### 3.2 设置系统配置

**接口地址**: `POST /setSystemConfiguration`

**接口描述**: 设置系统配置信息

**请求参数**:

```json
{
  "configuration": {
    "companyName": "公司名称",
    "companyAddress": "公司地址",
    "companyPhone": "公司电话",
    "companyFax": "公司传真",
    "companyPostCode": "公司邮编",
    "quantityPrecision": 2,
    "pricePrecision": 2,
    "checkNegativeStock": false
  }
}
```

**configuration对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| companyName | String | 是 | 公司名称 |
| companyAddress | String | 是 | 公司地址 |
| companyPhone | String | 否 | 公司电话 |
| companyFax | String | 否 | 公司传真 |
| companyPostCode | String | 否 | 公司邮编 |
| quantityPrecision | Integer | 否 | 数量精度（小数位数） |
| pricePrecision | Integer | 否 | 单价精度（小数位数） |
| checkNegativeStock | Boolean | 否 | 是否检查负库存 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "configuration": {
      "companyName": "公司名称",
      "companyAddress": "公司地址",
      "companyPhone": "公司电话",
      "companyFax": "公司传真",
      "companyPostCode": "公司邮编",
      "startTime": "2024-01-01T00:00:00",
      "currency": "RMB",
      "quantityPrecision": 2,
      "pricePrecision": 2,
      "inventoryValuationMethod": 10,
      "checkNegativeStock": false
    }
  }
}
```

**业务逻辑说明**:
1. 公司名称和公司地址为必填项
2. 其他字段为可选项，不传则保持原值
3. 系统会保留原有的配置，只更新传入的字段
4. 启用时间、本位币、存货计价方法等字段不能通过此接口修改

---

## 4. 日志管理 (Log)

**控制器路径**: `/log`

### 4.1 分页查询日志列表

**接口地址**: `POST /log/page`

**接口描述**: 分页查询系统操作日志列表，支持按时间范围筛选

**请求参数**:

```json
{
  "query": {
    "startTime": "2024-01-01",
    "endTime": "2024-12-31",
    "keyword": "关键字"
  },
  "current": 1,
  "size": 10
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| query | Object | 否 | {} | 查询条件对象 |
| query.startTime | String | 否 | - | 开始日期，格式：yyyy-MM-dd（会自动补充为 yyyy-MM-dd 00:00:00） |
| query.endTime | String | 否 | - | 结束日期，格式：yyyy-MM-dd（会自动补充为 yyyy-MM-dd 23:59:59） |
| query.keyword | String | 否 | - | 关键字搜索 |
| current | Long | 否 | 1 | 当前页码 |
| size | Long | 否 | 10 | 每页记录数 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "logPage": {
      "records": [
        {
          "id": "日志ID",
          "type": 1,
          "userId": "用户ID",
          "username": "用户名",
          "name": "用户姓名",
          "content": "{\"action\":\"登录\",\"ip\":\"127.0.0.1\"}",
          "createdTime": "2024-01-01T12:00:00",
          "updatedTime": "2024-01-01T12:00:00"
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
| id | String | 日志ID |
| type | Integer | 日志类型 |
| userId | String | 用户ID |
| username | String | 用户名 |
| name | String | 用户姓名 |
| content | String | 日志内容（JSON字符串） |
| createdTime | DateTime | 创建时间 |
| updatedTime | DateTime | 更新时间 |

**业务逻辑说明**:
1. 开始时间和结束时间会自动补充时分秒
2. 日志内容以JSON字符串形式存储，前端可以解析后展示

---

## 5. 菜单管理 (Menu)

**控制器路径**: `/menu`

### 5.1 获取菜单列表

**接口地址**: `POST /menu/list`

**接口描述**: 获取菜单树形列表（包含一级菜单和二级菜单）

**请求参数**: 无（自动从当前登录用户获取权限）

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "menuList": [
      {
        "id": "菜单ID",
        "parentId": null,
        "icon": "el-icon-s-home",
        "title": "首页",
        "path": "/index",
        "sortNumber": 1,
        "childList": [
          {
            "id": "子菜单ID",
            "parentId": "父菜单ID",
            "icon": "el-icon-document",
            "title": "工作台",
            "path": "/dashboard",
            "sortNumber": 1
          }
        ]
      }
    ]
  }
}
```

**字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | String | 菜单ID |
| parentId | String | 父菜单ID，一级菜单为null |
| icon | String | 图标 |
| title | String | 菜单标题 |
| path | String | 路由路径 |
| sortNumber | Integer | 排序号 |
| childList | Array | 子菜单列表 |

**业务逻辑说明**:
1. 菜单列表会根据当前登录用户的权限进行过滤
2. 只返回有权限访问的菜单
3. 菜单结构为两级：一级菜单和二级菜单

---

### 5.2 添加菜单

**接口地址**: `POST /menu/add`

**接口描述**: 添加新菜单

**请求参数**:

```json
{
  "menu": {
    "parentId": "父菜单ID（一级菜单传null）",
    "icon": "el-icon-s-home",
    "title": "菜单标题",
    "path": "/path",
    "sortNumber": 1
  }
}
```

**menu对象字段说明**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| parentId | String | 否 | 父菜单ID，一级菜单不传或传null |
| icon | String | 否 | 图标 |
| title | String | 是 | 菜单标题 |
| path | String | 是 | 路由路径 |
| sortNumber | Integer | 否 | 排序号 |

**响应数据**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "menu": {
      "id": "菜单ID",
      "parentId": "父菜单ID",
      "icon": "el-icon-s-home",
      "title": "菜单标题",
      "path": "/path",
      "sortNumber": 1,
      "createdTime": "2024-01-01T00:00:00",
      "updatedTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务逻辑说明**:
1. 菜单标题和路由路径为必填项
2. 其他字段为可选项
3. 新增的菜单会立即生效，但需要刷新页面才能看到

---

## 附录

### A. 分类类型常量说明

| 类型值 | 说明 |
|--------|------|
| 10 | 商品分类 |
| 20 | 客户分类 |
| 30 | 供应商分类 |
| 40 | 收入分类 |
| 50 | 支出分类 |

*注：具体类型值请参考后端 `Define` 常量类*

### B. 常见字典编码说明

| 字典编码 | 说明 |
|----------|------|
| unit | 计量单位 |
| settlement | 结算方式 |

### C. 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数是否符合要求 |
| 401 | 未授权 | 检查登录状态，重新登录 |
| 403 | 没有权限 | 仅在开发模式下可以修改所有字典项 |
| 500 | 服务器内部错误 | 联系后端开发人员 |

### D. 注意事项

1. **日期格式**: 所有日期字段均使用 `yyyy-MM-dd` 或 `yyyy-MM-dd HH:mm:ss` 格式
2. **权限控制**: 字典项修改在非开发模式下有限制
3. **树形结构**: 商品分类支持多级树形结构，其他分类只支持一级
4. **删除限制**: 分类删除前必须先删除子分类
5. **系统配置**: 部分系统配置字段不能通过接口修改，需要后台直接修改数据库

### E. 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| v1.0 | 2026-04-16 | 初始版本，包含分类、字典、系统配置、日志、菜单所有接口 |

---

**文档维护**: 后端开发团队  
**联系方式**: 如有问题请联系后端开发人员
