# 星络收银系统 · 赋能中国制造2026
星络收银系统 包括前端工程erp-web，后端工程erp-api和移动端小程序工程erp-app。应用框架自主研发，简单易用，性能极致，代码完全开源，持续更新！

[![Spring Boot](https://img.shields.io/badge/spring--boot-2.3.2.RELEASE-brightgreen)](https://github.com/spring-projects/spring-boot)
[![MyBatis3](https://img.shields.io/badge/mybatis-3.5.5-brightgreen)](http://www.mybatis.org/mybatis-3/zh/index.html)
[![MySQL](https://img.shields.io/badge/mysql-8.0.12-brightgreen)](https://www.mysql.com/)
[![vue](https://img.shields.io/badge/vue-2.5.22-brightgreen.svg)](https://github.com/vuejs/vue)
[![element-ui](https://img.shields.io/badge/element--ui-2.4.5-brightgreen.svg)](https://github.com/ElemeFE/element)
[![license](https://img.shields.io/badge/license-GPL-blue)](https://gitee.com/LuckyRaining/starnet-erp/blob/master/LICENSE)
[![Gitee stars](https://gitee.com/LuckyRaining/starnet-erp/badge/star.svg?theme=social)](https://gitee.com/LuckyRaining/starnet-erp)

## [在线演示](http://erp.starnet.net/)

## 视频教程
- [Java开发实战之星络ERP（1）：ERP快速开始，安装与运行](https://www.bilibili.com/video/BV1Rr4y1k7Ag/)
- [Java开发实战之星络ERP（2）：代码架构讲解，前端后端及数据库](https://www.bilibili.com/video/BV19S4y1D7NM/)
- [Java开发实战之星络ERP（3）：完善登录状态时跳转（2026-3-16更新）](https://www.bilibili.com/video/BV16g411w7vJ/)
- [Java开发实战之星络ERP（4）：通过看板进行任务管理，利用Jenkins实现自动化部署，修复一些bug（2021-12-14更新）](https://www.bilibili.com/video/BV1dF411z78F/)
## 开源地址
https://gitee.com/LuckyRaining/starnet-erp

## 开发计划
[总开发计划](https://gitee.com/LuckyRaining/starnet-erp/wikis/Home)

2020-08-17~2020-08-23

- [x] ~~[计量单位](https://note.youdao.com/ynoteshare1/index.html?id=629053015da491fbd0d52b68dd8ef5d3&type=note)~~
- [x] ~~[结算方式](https://note.youdao.com/ynoteshare1/index.html?id=bede9474209d8af1734598bb71bd6b43&type=note)~~
- [x] ~~[客户管理](https://note.youdao.com/ynoteshare1/index.html?id=5e15f1483e0eab4c1b489b630439a3f4&type=note)~~
- [x] ~~[供应商管理](https://note.youdao.com/ynoteshare1/index.html?id=e3c918124b7abfd7c905477e51f6664a&type=note)~~
- [x] ~~[商品管理](https://note.youdao.com/ynoteshare1/index.html?id=af55dd797b48dfc50479087efe46cbd3&type=note)~~
- [x] ~~[仓库管理](https://note.youdao.com/ynoteshare1/index.html?id=dda5df71fd7e5963d1756a5bb16f3086&type=note)~~
- [x] ~~[职员管理](https://note.youdao.com/ynoteshare1/index.html?id=effe1ec4f70b7e9601c6d004fbaba5f9&type=note)~~
- [x] ~~[账户管理](https://note.youdao.com/ynoteshare1/index.html?id=22973c24679ecc3c4b68c41bafe082b3&type=note)~~

## 已完成的功能

## 操作文档
- [官方文档](http://LuckyRaining.gitee.io/starnet-erp/)
- 视频教程

## 开发指南
- 分层规范（视频教程）
- 接口规范（视频教程）
- 数据库设计规范（视频教程）
- 命名规范（视频教程）

## 快速开始
### 运行
-  **安装数据库** 
    - 创建数据库 erp
    - 将 /databases 下的 erp.sql 导入到新建的数据库中
- **运行服务端工程 /erp-api**
    - 修改 application-dev.yml 里面关于数据库的配置
    - 直接运行 Application.java 里面的 main() 方法
- **运行前端工程 erp-web**
    - 进入文件夹

    ```
    cd erp-web
    ```
    - 安装依赖
    ```
    npm install
    ```
    - 启动
    ```
    npm run serve
    ```
    - （可选）通过Vue的可视化界面操作
    ```
    vue ui
    ```
    - 访问http://localhost:8080

    - 账号密码

    > 账号：admin
    > 密码：123456

### 安装部署
    - Idea上安装运行（视频教程）
    - Eclipse上安装运行（视频教程）
### ~~Docker镜像（视频教程）~~
### ~~VMWare镜像（视频教程）~~

## 技术框架
### 后端技术
|技术|名称|版本|
|---|---|---|
|[SpringBoot](http://spring.io/projects/spring-boot)|核心框架|2.3.2.RELEASE|
|[MyBatis](http://www.mybatis.org/mybatis-3/zh/index.html)|ORM框架|
|[MyBatisPlus](https://mybatis.plus/)|MyBatis加强组件|
|[Druid](https://github.com/alibaba/druid)|数据库连接池||
|[Maven](http://maven.apache.org/)|项目构建管理||
|Java|Java|1.8|
|MySQL|数据库|8.0.12|
|SpringSecurity|安全框架|

## 产品原型与历史开发计划
[开发计划与产品原型](https://gitee.com/LuckyRaining/starnet-erp/wikis/Home)

## API接口说明

## 官方网站
- [星络科技官网](https://www.starnet.net)
- 产品讨论
- 技术讨论

## 产品截图
![整体页面](https://images.gitee.com/uploads/images/2020/0821/171856_28d0ab48_348921.png "module.png")
![分类管理](https://images.gitee.com/uploads/images/2020/0812/210416_2d5d0089_348921.png "分类管理.png")
![系统设置](https://images.gitee.com/uploads/images/2020/0812/094304_dff29b60_348921.png "截图1.png")
![用户管理](https://images.gitee.com/uploads/images/2020/0812/094429_6a8aa055_348921.png "用户管理.png")
![日志管理](https://images.gitee.com/uploads/images/2020/0812/094504_c3aacc77_348921.png "日志管理.png")

## 待开发模块
- ~~用户注册登录、找回密码、登出~~
- ~~类别管理：客户类别、供应商类别、商品类别、支出类别、收入类别、计量单位、结算方式~~
- ~~客户管理~~
- ~~供应商管理~~
- ~~商品管理~~
- ~~仓库管理~~
- ~~职员管理~~
- ~~账户管理~~
- 购货
    - 购货单
    - 购货退货单
- 销货
    - 客户订单
    - 销货单
    - 销货退货单
- 仓库
    - 调拨单
    - 库存查询
    - 其他入库单
    - 其他出库单
- 资金
    - 收款单
    - 付款单
    - 其他收入单
    - 其他支出单
- 采购报表
    - 采购明细表
    - 采购汇总表（按商品）
    - 采购汇总表（按供应商）
- 销售报表
    - 销售明细表
    - 销售汇总表（按商品）
    - 销售汇总表（按客户）
    - 往来单位欠款表
    - 销售利润表
- 库存报表
    - 商品库存余额表
    - 商品收发明细表
    - 商品收发汇总表
- 资金报表
    - 现金银行报表
    - 应付账款明细表
    - 应收账款明细表
    - 客户对账单
    - 供应商对账单
    - 其他收支明细表
- 高级设置
    - ~~系统参数~~
    - ~~权限设置~~
    - ~~操作日志~~
    - 备份与恢复
- 在线演示
- SaaS化
- SpringCloud微服务化

## 入群交流
 **微信扫描下面二维码加我微信，请注明“来自星络ERP”** 

![我的微信]
(https://images.gitee.com/uploads/images/2020/0812/111204_9f81444b_348921.png "wechat.png")




