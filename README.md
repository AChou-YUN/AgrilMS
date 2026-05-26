# 🌾 AgrilMS - 农资进销存管理系统

一个面向农资经营企业的全栈进销存管理系统，涵盖进货、销售、库存、报表等核心业务模块，支持多角色权限管控。

## ✨ 功能特性

| 模块 | 功能说明 |
|------|---------|
| **📊 首页仪表盘** | 数据概览、销售趋势、库存预警、快捷操作 |
| **👤 用户管理** | 用户注册/登录、角色权限（管理员/经销商/零售商/仓库管理员）、个人中心 |
| **📦 农资产品管理** | 产品增删改查、分类管理、计量单位管理 |
| **🚚 供应商管理** | 供应商信息维护、信用等级管理 |
| **🛒 进货管理** | 进货单创建、进货明细、入库确认 |
| **💰 销售管理** | 销售开单、销售明细、退货处理、多种付款方式 |
| **📋 库存管理** | 实时库存查询、库存预警、库存盘点、变动日志追踪 |
| **📈 统计报表** | 销售报表、进货报表、库存报表、利润分析（ECharts 可视化） |

## 🛠️ 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| Spring Boot 4.0 | 应用框架 |
| MyBatis | ORM 持久层框架 |
| MySQL | 关系型数据库 |
| JWT (jjwt) | Token 认证授权 |
| Lombok | 简化实体类代码 |
| SpringDoc OpenAPI | API 文档（Swagger UI） |
| Java 17 | 编程语言 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式 JavaScript 框架 |
| Vue Router 4 | 前端路由管理 |
| Pinia | 状态管理 |
| Element Plus | UI 组件库 |
| Axios | HTTP 请求库 |
| ECharts | 数据可视化图表 |
| Vite 6 | 构建工具 |

## 📁 项目结构

```
AgrilMS/
├── src/                              # 后端源码
│   └── main/
│       ├── java/org/example/demo222/
│       │   ├── config/               # 配置类（CORS、MyBatis、Swagger 等）
│       │   ├── controller/           # 控制器层（REST API）
│       │   ├── service/              # 业务逻辑层
│       │   ├── mapper/               # MyBatis Mapper 接口
│       │   ├── entity/               # 实体类
│       │   ├── dto/                  # 数据传输对象
│       │   ├── common/               # 通用工具（Result、PageResult 等）
│       │   ├── interceptor/          # 拦截器（登录校验、权限校验）
│       │   ├── exception/            # 全局异常处理
│       │   └── util/                 # 工具类
│       └── resources/
│           ├── application.yml       # 应用配置
│           ├── mapper/               # MyBatis XML 映射文件
│           └── sql/                  # 数据库初始化脚本
├── frontend/                         # 前端源码
│   ├── src/
│   │   ├── api/                      # API 请求封装
│   │   ├── assets/styles/            # 全局样式与主题
│   │   ├── composables/              # 组合式函数（通用 CRUD、分页等）
│   │   ├── layout/                   # 布局组件
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia 状态管理
│   │   ├── utils/                    # 工具函数
│   │   └── views/                    # 页面视图
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── pom.xml                           # Maven 依赖配置
```

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.6+
- **Node.js** 18+
- **MySQL** 8.0+

### 1. 初始化数据库

```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

可选：导入测试数据

```bash
mysql -u root -p agricultural_ims < src/main/resources/sql/V4_mock_test_data.sql
mysql -u root -p agricultural_ims < src/main/resources/sql/V3_init_admin.sql
```

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agricultural_ims?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shallow
    username: your_username
    password: your_password
```

### 3. 启动后端服务

```bash
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 4. 启动前端开发服务器

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`

## 👥 角色权限

| 角色 | 标识 | 权限范围 |
|------|------|---------|
| 系统管理员 | `ADMIN` | 全部功能，包括用户管理、基础数据配置 |
| 经销商 | `DEALER` | 产品管理、供应商管理、进货管理、库存查看 |
| 零售商 | `RETAILER` | 客户管理、销售管理、库存查看 |
| 仓库管理员 | `WAREHOUSE` | 库存管理（查看、盘点、预警、日志） |

## 📖 API 文档

启动后端服务后，访问 Swagger UI 查看完整 API 文档：

```
http://localhost:8080/swagger-ui.html
```

## 📄 License

MIT License