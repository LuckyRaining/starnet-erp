# 星络ERP收银小程序 (erp-app)

## 📱 项目简介

面向中小型零售场景的微信收银ERP小程序,基于 **uni-app** 架构开发,对接星络 erp-api 后端系统。

### 核心功能模块

1. **首页** - 欢迎语、广告轮播、快捷入口、今日数据概览
2. **商品** - 分类浏览、商品列表、扫码添加、搜索(待完善)
3. **收银** - 智能购物车、挂单取单、结算功能
4. **统计** - 销售数据分析、Canvas图表展示
5. **我的** - 用户登录/注册、登出、关于系统

---

## 📁 项目结构

```
erp-app/
├── pages/                    # 主包页面
│   ├── index/               # 首页
│   ├── product/             # 商品页
│   ├── checkout/            # 收银页
│   ├── statistics/          # 统计页
│   └── mine/                # 我的页
├── subpackages/             # 分包页面
│   ├── auth/                # 认证相关
│   │   └── login.vue        # 登录页
│   ├── search/              # 搜索相关
│   │   └── search.vue       # 搜索页(待完善)
│   ├── business/            # 业务相关
│   │   └── holdorder.vue    # 挂单页
│   └── system/              # 系统相关
│       └── about.vue        # 关于系统
├── utils/                   # 工具类
│   ├── config.js            # 配置常量
│   ├── request.js           # HTTP请求封装
│   ├── api.js               # API接口定义
│   └── user.js              # 用户状态管理
├── static/                  # 静态资源
│   └── tabbar/              # TabBar图标(需自行替换)
├── components/              # 公共组件(可扩展)
├── pages.json               # 页面配置
├── App.vue                  # 应用入口
└── main.js                  # 主入口文件
```

---

## 🚀 快速开始

### 前置要求

- HBuilderX 3.1.0+ (推荐)
- 微信开发者工具
- 已启动的后端服务 (erp-api,默认端口9090)

### 安装步骤

1. **准备TabBar图标**
   
   在 `static/tabbar/` 目录下放置以下图标文件(81x81像素):
   - `home.png` / `home-active.png` - 首页图标
   - `product.png` / `product-active.png` - 商品图标
   - `checkout.png` / `checkout-active.png` - 收银图标
   - `statistics.png` / `statistics-active.png` - 统计图标
   - `mine.png` / `mine-active.png` - 我的图标
   
   > ⚠️ 当前目录中只有 `.placeholder` 占位文件,请替换为实际PNG图片

2. **配置后端API地址**
   
   修改 `utils/config.js` 中的 `BASE_URL`:
   ```javascript
   BASE_URL: 'http://localhost:9090'  // 根据实际部署环境修改
   ```

3. **编译运行**
   
   - 在HBuilderX中打开项目
   - 选择"运行" -> "运行到小程序模拟器" -> "微信开发者工具"
   - 或使用命令行: `npm run dev:mp-weixin`

---

## 🔧 主要功能说明

### 1. 首页 (pages/index/index.vue)

- **智能问候语**: 根据时间段显示不同问候语
- **广告轮播**: 预留广告位,提示"广告招租中"
- **快捷入口**: 商品管理、智能收银、数据统计、挂单取单
- **今日概况**: 显示今日订单数、销售额、商品数
- **最近销售**: 展示最近5条销售记录

### 2. 商品页 (pages/product/product.vue)

- **顶部操作栏**: 搜索框 + 扫码按钮
- **左侧分类**: 商品分类侧边栏,支持切换
- **右侧商品**: 网格布局展示商品,支持下拉加载更多
- **添加到购物车**: 点击商品卡片直接加入购物车
- **扫码功能**: 调用摄像头扫描商品条码

### 3. 收银页 (pages/checkout/checkout.vue)

- **购物车管理**: 
  - 增加/减少数量
  - 删除商品
  - 清空购物车
- **挂单功能**: 将当前购物车保存为挂单,稍后恢复
- **结算功能**: 
  - 生成出库单
  - 调用后端API保存订单
  - 显示结算成功信息

### 4. 统计页 (pages/statistics/statistics.vue)

- **日期筛选**: 选择起止日期查询统计数据
- **数据卡片**: 总销售额、订单数、客单价
- **商品排行**: Canvas绘制柱状图展示TOP10商品
- **销售趋势**: Canvas绘制折线图展示销售趋势

### 5. 我的页 (pages/mine/mine.vue)

- **用户信息**: 显示头像、姓名、手机号
- **功能菜单**: 
  - 挂单记录
  - 注册账号(暂未开放,显示提示)
  - 关于系统
  - 退出登录
- **版本信息**: 显示系统版本号

---

## 📊 后端API对接

### 已对接的API

| 模块 | 接口 | 说明 |
|------|------|------|
| 用户 | `/user/login` | 用户登录 |
| 商品 | `/product/page` | 商品分页列表 |
| 商品 | `/product/detail` | 商品详情 |
| 分类 | `/category/list` | 分类列表(type=30) |
| 分析 | `/analysis/sale/detailList` | 销售明细 |
| 分析 | `/analysis/sale/productSummaryList` | 销售汇总(按商品) |
| 出库 | `/checkout/createCode` | 创建出库单号 |
| 出库 | `/checkout/save` | 保存出库单 |
| 字典 | `/dict/itemList` | 字典项列表 |

### 待完善的API

- **商品搜索**: 后端需提供搜索接口
- **用户详情**: 获取完整用户信息
- **库存查询**: 实时库存数据

---

## 🎨 UI设计特点

- **渐变色主题**: 紫色渐变(#667eea → #764ba2)作为主色调
- **卡片式布局**: 圆角卡片 + 阴影效果
- **Emoji图标**: 使用Emoji代替图片图标,简洁美观
- **响应式设计**: 适配不同屏幕尺寸
- **交互动效**: 按钮点击缩放反馈

---

## ⚙️ 技术栈

- **前端框架**: uni-app + Vue.js
- **UI组件**: 原生view + scss
- **图表绘制**: Canvas API
- **状态管理**: uni.getStorageSync/setStorageSync
- **HTTP请求**: uni.request 封装
- **后端对接**: RESTful API (Spring Boot)

---

## 📝 注意事项

1. **TabBar图标**: 必须提供实际的PNG图片文件,否则TabBar无法正常显示
2. **后端服务**: 确保erp-api服务已启动且网络可达
3. **登录状态**: 部分功能(如结算)需要登录后才能使用
4. **扫码权限**: 需在微信小程序后台配置扫码权限
5. **数据缓存**: 购物车和挂单数据存储在本地Storage中

---

## 🔐 测试账号

```
用户名: admin
密码: 123456
```

---

## 🐛 已知问题

1. **搜索功能**: 后端未提供搜索API,当前仅显示提示信息
2. **注册功能**: 暂未开放,需联系管理员注册
3. **图表库**: 使用Canvas原生绘制,未集成ECharts(因小程序限制)
4. **图片上传**: 商品暂无图片,使用Emoji占位

---

## 📞 联系方式

如有问题,请联系星络ERP开发团队或系统管理员。

---

## 📄 License

本项目遵循MIT许可证。

---

**最后更新**: 2024-01-15  
**版本**: v1.0.0
