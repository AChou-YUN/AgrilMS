# 🚀 团队快速上手指南

> 本文档面向团队成员，帮助你在 **10 分钟内** 跑通项目。

---

## 📋 一、环境要求（必须提前安装）

| 软件 | 最低版本 | 检查命令 | 安装说明 |
|------|---------|---------|---------|
| **JDK** | 17+ | `java -version` | 推荐 [Adoptium JDK 17](https://adoptium.net/) |
| **Maven** | 3.6+ | `mvn -v` | 项目自带 `mvnw`，可免安装（见下文） |
| **Node.js** | 18+ | `node -v` | 推荐 [Node.js 18 LTS](https://nodejs.org/) |
| **MySQL** | 8.0+ | `mysql --version` | 或使用 Docker（见下文） |
| **Git** | 任意 | `git --version` | [下载地址](https://git-scm.com/) |

> 💡 **IntelliJ IDEA** 用户：IDEA 自带 Maven，无需单独安装 Maven。

---

## 📥 二、克隆项目

```bash
git clone https://github.com/AChou-YUN/AgrilMS.git
cd AgrilMS
```

---

## 🗄️ 三、配置数据库

### 方式 A：本地安装 MySQL

1. **确保 MySQL 服务已启动**

2. **按顺序执行 SQL 脚本**（在终端或 Navicat/DBeaver 等工具中）：

```bash
# ① 创建表结构
mysql -u root -p < src/main/resources/sql/init.sql

# ② 初始化管理员账号和测试基础数据（含4个用户、供应商、客户、产品、库存）
mysql -u root -p agricultural_ims < src/main/resources/sql/V3_init_admin.sql

# ③ （可选）导入更多模拟测试数据
mysql -u root -p agricultural_ims < src/main/resources/sql/V4_mock_test_data.sql
```

> ⚠️ 脚本必须按顺序执行：`init.sql` → `V3_init_admin.sql` → `V4_mock_test_data.sql`（可选）

### 方式 B：使用 Docker（推荐，无需本地安装 MySQL）

```bash
docker run -d \
  --name agrilms-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=agricultural_ims \
  -e MYSQL_CHARACTER_SET_SERVER=utf8mb4 \
  mysql:8.0
```

等待容器启动完成后（约 30 秒），执行 SQL 脚本：

```bash
docker exec -i agrilms-mysql mysql -uroot -p123456 < src/main/resources/sql/init.sql
docker exec -i agrilms-mysql mysql -uroot -p123456 agricultural_ims < src/main/resources/sql/V3_init_admin.sql
```

---

## ⚙️ 四、修改数据库连接配置

编辑文件 `src/main/resources/application.yml`，将数据库密码改为**你自己的 MySQL 密码**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agricultural_ims?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: 你的MySQL密码    # ← 修改这里
```

> 💡 如果你用的是 Docker 方式，密码填 `123456`。

---

## ▶️ 五、启动项目

### 5.1 启动后端（Spring Boot）

**方式一：使用 IDEA（推荐）**

1. 用 IDEA 打开项目根目录
2. IDEA 会自动识别为 Maven 项目并下载依赖（首次需等待几分钟）
3. 找到 `src/main/java/org/example/demo222/Demo222Application.java`
4. 右键 → **Run 'Demo222Application'**

**方式二：命令行启动**

```bash
# Windows（使用项目自带的 Maven Wrapper）
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

✅ 启动成功标志：控制台出现 `Started Demo222Application in x.xx seconds`

后端运行在：`http://localhost:8080`

### 5.2 启动前端（Vue 3）

**新开一个终端窗口**：

```bash
cd frontend
npm install        # 首次运行需安装依赖
npm run dev
```

✅ 启动成功标志：终端显示 `Local: http://localhost:3000/`

---

## 🔐 六、登录测试

浏览器打开：**http://localhost:3000**

| 用户名 | 密码 | 角色 | 权限说明 |
|--------|------|------|---------|
| `admin` | `123456` | 系统管理员 | 全部功能 |
| `dealer` | `123456` | 经销商 | 产品、供应商、进货、库存查看 |
| `retailer` | `123456` | 零售商 | 客户、销售、库存查看 |
| `warehouse` | `123456` | 仓库管理员 | 库存管理 |

---

## 🔧 七、API 文档

后端启动后，访问 Swagger UI 查看所有接口：

```
http://localhost:8080/swagger-ui.html
```

---

## ❓ 常见问题排查

### Q1：后端启动报 `Communications link failure` 或数据库连接失败
- 检查 MySQL 服务是否已启动
- 检查 `application.yml` 中的密码是否正确
- 检查数据库 `agricultural_ims` 是否已创建

### Q2：后端启动报 `Table 'xxx' doesn't exist`
- 说明 SQL 脚本未执行或执行顺序不对
- 重新按顺序执行：`init.sql` → `V3_init_admin.sql`

### Q3：前端 `npm install` 报错
- 确认 Node.js 版本 ≥ 18：`node -v`
- 尝试清除缓存：`npm cache clean --force` 后重新 `npm install`
- 如果网络慢，可使用淘宝镜像：`npm install --registry=https://registry.npmmirror.com`

### Q4：前端页面空白或接口报错 404
- 确认后端是否已启动（`localhost:8080` 能否访问）
- 确认前端 `npm run dev` 正在运行
- 检查浏览器控制台的网络请求是否指向 `localhost:3000`

### Q5：`mvnw` 执行报权限错误（macOS/Linux）
```bash
chmod +x mvnw
./mvnw spring-boot:run
```

### Q6：端口被占用
- 后端 8080 端口被占：修改 `application.yml` 中 `server.port`
- 前端 3000 端口被占：修改 `frontend/vite.config.js` 中 `server.port`

---

## 📁 项目结构速览

```
AgrilMS/
├── src/                          # 后端（Spring Boot）
│   └── main/
│       ├── java/.../demo222/
│       │   ├── controller/       # REST API 接口
│       │   ├── service/          # 业务逻辑
│       │   ├── mapper/           # 数据库操作
│       │   └── entity/           # 实体类
│       └── resources/
│           ├── application.yml   # ⚙️ 后端配置文件（需改密码）
│           ├── mapper/           # MyBatis XML
│           └── sql/              # 📦 数据库初始化脚本
├── frontend/                     # 前端（Vue 3 + Element Plus）
│   ├── src/
│   │   ├── api/                  # 接口请求封装
│   │   ├── views/                # 页面组件
│   │   ├── router/               # 路由配置
│   │   └── store/                # 状态管理
│   └── package.json
└── pom.xml                       # Maven 依赖配置
```

---

## ⚡ 一键启动脚本（可选）

如果你不想每次都手动启动两个终端，可以在项目根目录创建启动脚本：

**Windows** — 创建 `start.bat`：
```bat
@echo off
echo [1/2] 启动后端服务...
start cmd /k "mvnw.cmd spring-boot:run"
echo [2/2] 启动前端服务...
timeout /t 5 /nobreak > nul
start cmd /k "cd frontend && npm install && npm run dev"
echo ✅ 项目启动中，请等待片刻后访问 http://localhost:3000
```

**macOS/Linux** — 创建 `start.sh`：
```bash
#!/bin/bash
echo "[1/2] 启动后端服务..."
./mvnw spring-boot:run &
sleep 10
echo "[2/2] 启动前端服务..."
cd frontend && npm install && npm run dev
```

---

> 📌 **第一次跑通后，后续只需执行步骤五即可启动项目。**