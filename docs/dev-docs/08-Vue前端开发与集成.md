# 阶段7：Vue前端开发与集成

## 一、阶段目标

搭建Vue 3前端项目，完成所有页面的开发，包括登录注册、布局框架、各业务模块页面、图表展示等，并与后端API完成联调。

**预计工期：3天**

**前置条件：** 阶段6完成后端所有API接口已就绪。

---

## 二、技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vite | 最新 | 构建工具 |
| Element Plus | 最新 | UI组件库 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 最新 | 状态管理 |
| Axios | 最新 | HTTP请求 |
| ECharts | 5.x | 图表库 |
| Day.js | 最新 | 日期处理 |

---

## 三、项目搭建

### 3.1 创建Vue项目

```bash
# 在项目根目录下创建前端项目
npm create vite@latest frontend -- --template vue

# 安装依赖
cd frontend
npm install element-plus @element-plus/icons-vue
npm install vue-router@4 pinia
npm install axios
npm install echarts
npm install dayjs
```

### 3.2 项目结构

```
frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                  # API请求封装
│   │   ├── request.js        # Axios实例配置
│   │   ├── auth.js           # 认证相关API
│   │   ├── user.js           # 用户管理API
│   │   ├── product.js        # 产品管理API
│   │   ├── category.js       # 分类管理API
│   │   ├── unit.js           # 单位管理API
│   │   ├── supplier.js       # 供应商API
│   │   ├── purchase.js       # 进货管理API
│   │   ├── customer.js       # 客户管理API
│   │   ├── sales.js          # 销售管理API
│   │   ├── inventory.js      # 库存管理API
│   │   ├── dashboard.js      # 仪表盘API
│   │   └── report.js         # 报表API
│   ├── assets/               # 静态资源
│   │   └── styles/           # 全局样式
│   ├── components/           # 公共组件
│   │   ├── AppHeader.vue     # 顶部导航
│   │   ├── AppSidebar.vue    # 侧边栏菜单
│   │   ├── Breadcrumb.vue    # 面包屑
│   │   ├── Pagination.vue    # 分页组件
│   │   └── PrintDialog.vue   # 打印弹窗
│   ├── layout/               # 布局组件
│   │   └── MainLayout.vue    # 主布局
│   ├── router/               # 路由配置
│   │   └── index.js
│   ├── store/                # 状态管理
│   │   ├── user.js           # 用户状态
│   │   └── app.js            # 应用状态
│   ├── utils/                # 工具函数
│   │   ├── auth.js           # Token管理
│   │   └── validate.js       # 表单校验规则
│   ├── views/                # 页面组件
│   │   ├── login/
│   │   │   └── LoginView.vue
│   │   ├── register/
│   │   │   └── RegisterView.vue
│   │   ├── dashboard/
│   │   │   └── DashboardView.vue
│   │   ├── user/
│   │   │   ├── UserList.vue
│   │   │   └── UserProfile.vue
│   │   ├── product/
│   │   │   ├── ProductList.vue
│   │   │   ├── ProductForm.vue
│   │   │   ├── CategoryList.vue
│   │   │   └── UnitList.vue
│   │   ├── supplier/
│   │   │   ├── SupplierList.vue
│   │   │   └── SupplierForm.vue
│   │   ├── purchase/
│   │   │   ├── PurchaseOrderList.vue
│   │   │   ├── PurchaseOrderForm.vue
│   │   │   └── PurchaseOrderDetail.vue
│   │   ├── customer/
│   │   │   ├── CustomerList.vue
│   │   │   └── CustomerForm.vue
│   │   ├── sales/
│   │   │   ├── SalesOrderList.vue
│   │   │   ├── SalesOrderForm.vue
│   │   │   ├── SalesOrderDetail.vue
│   │   │   └── SalesPrintView.vue
│   │   ├── inventory/
│   │   │   ├── InventoryList.vue
│   │   │   ├── InventoryWarning.vue
│   │   │   ├── InventoryCheck.vue
│   │   │   ├── InventoryDamage.vue
│   │   │   └── InventoryLog.vue
│   │   └── report/
│   │       ├── SalesReport.vue
│   │       ├── PurchaseReport.vue
│   │       ├── InventoryReport.vue
│   │       ├── ProfitReport.vue
│   │       └── CustomerReport.vue
│   ├── App.vue
│   └── main.js
├── .env.development          # 开发环境变量
├── .env.production           # 生产环境变量
├── vite.config.js            # Vite配置
├── package.json
└── index.html
```

---

## 四、核心配置

### 4.1 Axios请求封装 (`api/request.js`)

```javascript
import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      if (res.code === 401) {
        removeToken()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    if (error.response?.status === 401) {
      removeToken()
      router.push('/login')
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
```

### 4.2 路由配置 (`router/index.js`)

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/login/LoginView.vue'), meta: { public: true } },
  { path: '/register', name: 'Register', component: () => import('@/views/register/RegisterView.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/DashboardView.vue'), meta: { title: '首页' } },
      
      // 用户管理
      { path: 'users', name: 'UserList', component: () => import('@/views/user/UserList.vue'), meta: { title: '用户管理', roles: ['ADMIN'] } },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/user/UserProfile.vue'), meta: { title: '个人中心' } },
      
      // 农资管理
      { path: 'products', name: 'ProductList', component: () => import('@/views/product/ProductList.vue'), meta: { title: '农资产品' } },
      { path: 'products/add', name: 'ProductAdd', component: () => import('@/views/product/ProductForm.vue'), meta: { title: '新增产品', roles: ['ADMIN', 'DEALER'] } },
      { path: 'products/edit/:id', name: 'ProductEdit', component: () => import('@/views/product/ProductForm.vue'), meta: { title: '编辑产品', roles: ['ADMIN', 'DEALER'] } },
      { path: 'categories', name: 'CategoryList', component: () => import('@/views/product/CategoryList.vue'), meta: { title: '农资分类', roles: ['ADMIN'] } },
      { path: 'units', name: 'UnitList', component: () => import('@/views/product/UnitList.vue'), meta: { title: '计量单位', roles: ['ADMIN'] } },
      
      // 供应商管理
      { path: 'suppliers', name: 'SupplierList', component: () => import('@/views/supplier/SupplierList.vue'), meta: { title: '供应商管理', roles: ['ADMIN', 'DEALER'] } },
      
      // 进货管理
      { path: 'purchase-orders', name: 'PurchaseOrderList', component: () => import('@/views/purchase/PurchaseOrderList.vue'), meta: { title: '进货管理', roles: ['ADMIN', 'DEALER'] } },
      { path: 'purchase-orders/add', name: 'PurchaseOrderAdd', component: () => import('@/views/purchase/PurchaseOrderForm.vue'), meta: { title: '新增进货单', roles: ['ADMIN', 'DEALER'] } },
      { path: 'purchase-orders/:id', name: 'PurchaseOrderDetail', component: () => import('@/views/purchase/PurchaseOrderDetail.vue'), meta: { title: '进货单详情', roles: ['ADMIN', 'DEALER'] } },
      
      // 客户管理
      { path: 'customers', name: 'CustomerList', component: () => import('@/views/customer/CustomerList.vue'), meta: { title: '客户管理', roles: ['ADMIN', 'RETAILER'] } },
      
      // 销售管理
      { path: 'sales-orders', name: 'SalesOrderList', component: () => import('@/views/sales/SalesOrderList.vue'), meta: { title: '销售管理', roles: ['ADMIN', 'RETAILER'] } },
      { path: 'sales-orders/add', name: 'SalesOrderAdd', component: () => import('@/views/sales/SalesOrderForm.vue'), meta: { title: '销售开单', roles: ['ADMIN', 'RETAILER'] } },
      { path: 'sales-orders/:id', name: 'SalesOrderDetail', component: () => import('@/views/sales/SalesOrderDetail.vue'), meta: { title: '销售单详情', roles: ['ADMIN', 'RETAILER'] } },
      
      // 库存管理
      { path: 'inventory', name: 'InventoryList', component: () => import('@/views/inventory/InventoryList.vue'), meta: { title: '库存管理' } },
      { path: 'inventory/warnings', name: 'InventoryWarning', component: () => import('@/views/inventory/InventoryWarning.vue'), meta: { title: '库存预警' } },
      { path: 'inventory/check', name: 'InventoryCheck', component: () => import('@/views/inventory/InventoryCheck.vue'), meta: { title: '库存盘点', roles: ['ADMIN', 'WAREHOUSE'] } },
      { path: 'inventory/logs', name: 'InventoryLog', component: () => import('@/views/inventory/InventoryLog.vue'), meta: { title: '库存日志' } },
      
      // 报表
      { path: 'reports/sales', name: 'SalesReport', component: () => import('@/views/report/SalesReport.vue'), meta: { title: '销售报表' } },
      { path: 'reports/purchase', name: 'PurchaseReport', component: () => import('@/views/report/PurchaseReport.vue'), meta: { title: '进货报表' } },
      { path: 'reports/inventory', name: 'InventoryReport', component: () => import('@/views/report/InventoryReport.vue'), meta: { title: '库存报表' } },
      { path: 'reports/profit', name: 'ProfitReport', component: () => import('@/views/report/ProfitReport.vue'), meta: { title: '利润报表' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (!to.meta.public && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

### 4.3 Vite配置 (`vite.config.js`)

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 五、页面开发详细说明

### 5.1 登录/注册页面

**登录页面功能：**
- 用户名/密码输入表单
- 表单验证（非空、长度）
- 登录按钮，调用 `/api/auth/login`
- 登录成功后存储Token到localStorage
- 跳转到首页
- 注册链接跳转

**注册页面功能：**
- 用户名、密码、确认密码、真实姓名、手机号表单
- 表单验证（用户名唯一、密码一致性）
- 注册成功后跳转登录页

### 5.2 主布局 (`MainLayout.vue`)

```
┌─────────────────────────────────────────┐
│              顶部导航栏                   │
│  [Logo] 系统名称        [用户名] [退出]   │
├──────────┬──────────────────────────────┤
│          │                              │
│  侧边栏   │         内容区域              │
│  菜单     │    ┌──────────────────┐      │
│          │    │    面包屑导航      │      │
│  - 首页   │    ├──────────────────┤      │
│  - 用户   │    │                  │      │
│  - 农资   │    │    页面内容       │      │
│  - 供应商  │    │                  │      │
│  - 进货   │    │                  │      │
│  - 客户   │    └──────────────────┘      │
│  - 销售   │                              │
│  - 库存   │                              │
│  - 报表   │                              │
│          │                              │
└──────────┴──────────────────────────────┘
```

**侧边栏菜单根据用户角色动态显示：**

| 菜单项 | ADMIN | DEALER | RETAILER | WAREHOUSE |
|--------|:---:|:---:|:---:|:---:|
| 首页仪表盘 | ✅ | ✅ | ✅ | ✅ |
| 用户管理 | ✅ | ❌ | ❌ | ❌ |
| 农资管理（产品/分类/单位） | ✅ | ✅ | ❌ | ❌ |
| 供应商管理 | ✅ | ✅ | ❌ | ❌ |
| 进货管理 | ✅ | ✅ | ❌ | ❌ |
| 客户管理 | ✅ | ❌ | ✅ | ❌ |
| 销售管理 | ✅ | ❌ | ✅ | ❌ |
| 库存管理 | ✅ | ✅ | ✅ | ✅ |
| 统计报表 | ✅ | ✅ | ✅ | ✅ |
| 个人中心 | ✅ | ✅ | ✅ | ✅ |

### 5.3 首页仪表盘 (`DashboardView.vue`)

**页面布局：**
```
┌─────────────┬─────────────┬─────────────┬─────────────┐
│  今日销售额   │  本月销售额   │  库存预警数   │  本月新增客户  │
│  ¥12,580    │  ¥158,600   │    5        │    12       │
└─────────────┴─────────────┴─────────────┴─────────────┘
┌───────────────────────────┬───────────────────────────┐
│                           │                           │
│     近30天销售趋势          │    产品分类销售占比          │
│     (折线图 - ECharts)     │    (饼图 - ECharts)        │
│                           │                           │
└───────────────────────────┴───────────────────────────┘
┌───────────────────────────────────────────────────────┐
│                     库存预警列表                        │
│  产品名称  │  分类  │  当前库存  │  安全库存  │  差额    │
│  尿素      │  化肥  │    50     │   100    │   -50   │
│  ...       │  ...   │   ...     │   ...    │   ...   │
└───────────────────────────────────────────────────────┘
```

**实现要点：**
- 使用 `el-row` 和 `el-col` 栅格布局
- 顶部四个统计卡片使用 `el-card`
- ECharts折线图展示销售趋势
- ECharts饼图展示分类占比
- 预警列表使用 `el-table`，低库存行高亮

### 5.4 用户管理页面

**用户列表页：**
- 搜索栏：用户名、真实姓名、状态筛选
- 表格列：用户名、真实姓名、手机号、角色、状态、创建时间、操作
- 操作按钮：编辑、启用/禁用、分配角色、删除
- 新增用户弹窗/对话框
- 分页

**个人中心页：**
- 基本信息展示和编辑
- 修改密码功能
- 头像上传（可选）

### 5.5 农资产品管理页面

**产品列表页：**
- 搜索栏：产品名称、分类、状态、低库存筛选
- 表格列：产品名称、分类、规格、单位、当前库存、安全库存、参考进价、参考售价、状态、操作
- 低库存行红色高亮
- 操作按钮：编辑、上架/下架、删除
- 新增产品按钮

**产品表单页（新增/编辑）：**
- 表单字段：名称、分类（下拉）、规格、单位（下拉）、安全库存阈值、生产厂家、批号、生产日期、保质期、参考进价、参考售价、描述
- 表单验证
- 提交/取消按钮

**分类管理页：**
- 分类列表（表格）
- 新增/编辑/删除操作

**单位管理页：**
- 单位列表
- 新增/删除操作

### 5.6 供应商管理页面

**供应商列表页：**
- 搜索栏：名称、信用等级
- 表格列：名称、联系人、电话、地址、信用等级（星级显示）、状态、操作
- 新增/编辑/删除操作

**供应商表单（弹窗/对话框）：**
- 表单字段：名称、联系人、电话、地址、信用等级（1-5选择器）、银行账号、备注

### 5.7 进货管理页面

**进货单列表页：**
- 搜索栏：进货单号、供应商、状态、日期范围
- 表格列：单号、供应商、总金额、状态（标签）、进货日期、操作人、操作
- 操作按钮：查看详情、确认入库（待确认状态）、取消
- 新增进货单按钮

**新增进货单页：**
- 顶部：选择供应商（下拉）、进货日期（日期选择器）
- 中部：进货明细表格
  - 动态添加行：选择产品（下拉）、数量、进价、小计（自动计算）
  - 支持添加/删除行
- 底部：总金额、备注、提交按钮

**进货单详情页：**
- 基本信息展示
- 明细表格
- 操作按钮（确认入库/取消，根据状态显示）

### 5.8 客户管理页面

**客户列表页：**
- 搜索栏：姓名、手机号
- 表格列：姓名、电话、地址、购买偏好、操作
- 操作按钮：编辑、查看购买历史、删除
- 新增客户按钮

**客户表单（弹窗）：**
- 表单字段：姓名、电话、地址、购买偏好、备注

### 5.9 销售管理页面

**销售单列表页：**
- 搜索栏：销售单号、客户、状态、付款方式、日期范围
- 表格列：单号、客户、总金额、付款方式（标签）、状态（标签）、销售日期、操作人、操作
- 操作按钮：查看详情、退货、打印
- 销售开单按钮

**销售开单页：**
- 顶部：选择客户（下拉，可选）、销售日期、付款方式（单选）
- 中部：销售明细表格
  - 动态添加行：选择产品（下拉，显示库存）、数量、售价（默认参考售价）、小计
  - 库存不足时红色提示
- 底部：总金额、备注、提交按钮

**销售单详情页：**
- 基本信息展示
- 明细表格
- 打印按钮
- 退货按钮（根据状态显示）

**销售单打印页/弹窗：**
- 模拟纸质单据样式
- 包含：单号、日期、客户信息、商品明细表、合计金额（大写）、付款方式
- 打印按钮（调用 `window.print()`）

### 5.10 库存管理页面

**库存列表页：**
- 搜索栏：产品名称、分类、仅显示预警
- 表格列：产品名称、分类、规格、单位、当前库存、安全库存、库存价值、状态
- 低于安全库存的行使用红色/橙色高亮
- 库存盘点按钮

**库存预警页：**
- 预警产品列表
- 按分类筛选
- 显示当前库存与安全库存的差额

**库存盘点页：**
- 选择产品
- 显示系统库存
- 输入实际盘点数量
- 自动计算差异（盈亏）
- 确认盘点按钮

**报损页面：**
- 选择产品
- 输入报损数量
- 填写报损原因
- 确认报损按钮

**库存日志页：**
- 搜索栏：产品、变动类型、日期范围
- 表格列：产品名称、变动类型（标签）、变动数量、变动前库存、变动后库存、关联单号、操作人、时间

### 5.11 统计报表页面

**销售报表页：**
- 查询条件：按日/月/年，选择日期
- 统计图表（ECharts柱状图/折线图）
- 数据表格

**进货报表页：**
- 查询条件：日期范围
- 进货额趋势图
- 分类进货占比饼图
- 数据表格

**库存报表页：**
- 库存汇总数据：总产品数、总库存量、总库存价值、预警数量
- 各产品库存明细表

**利润报表页：**
- 查询条件：按月/年
- 销售额、进货成本、毛利润、利润率
- 利润趋势图

**客户报表页：**
- 客户购买金额排行
- 柱状图展示

---

## 六、公共组件说明

### 6.1 状态标签组件

用于统一展示各类状态的标签样式：

```vue
<!-- 状态标签映射 -->
<el-tag :type="statusType">{{ statusText }}</el-tag>
```

状态映射表：

| 类型 | 值 | 文本 | 标签颜色 |
|------|-----|------|----------|
| 用户状态 | 1/0 | 启用/禁用 | success/danger |
| 产品状态 | 1/0 | 上架/下架 | success/info |
| 进货单状态 | 0/1/2 | 待确认/已入库/已取消 | warning/success/danger |
| 销售单状态 | 1/2/3 | 已完成/已退货/部分退货 | success/danger/warning |
| 付款方式 | 1/2/3/4 | 现金/微信/支付宝/赊账 | -/-/-/warning |
| 库存变动类型 | PURCHASE/SALE/ADJUST/DAMAGE/RETURN | 入库/出库/盘点/报损/退货 | success/danger/warning/danger/info |

### 6.2 分页组件封装

封装 Element Plus 的分页组件，统一处理分页逻辑。

### 6.3 打印组件

封装销售单打印功能，使用 CSS `@media print` 控制打印样式。

---

## 七、状态管理设计

### 7.1 用户状态 (`store/user.js`)

```javascript
export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userId: null,
    username: '',
    realName: '',
    roles: [],
    avatar: ''
  }),
  actions: {
    async login(loginForm) { ... },
    async getInfo() { ... },
    logout() { ... }
  }
})
```

---

## 八、前后端联调要点

| 要点 | 说明 |
|------|------|
| API基础路径 | 开发环境通过Vite proxy代理到 `http://localhost:8080` |
| Token传递 | 请求头 `Authorization: Bearer {token}` |
| 错误处理 | 响应拦截器统一处理 `code !== 200` 的情况 |
| 分页参数 | 统一使用 `pageNum` 和 `pageSize` |
| 日期格式 | 前端使用 `YYYY-MM-DD` 格式传递日期 |
| 跨域 | 后端已配置CORS，前端通过proxy代理 |

---

## 九、验证标准

| 验证项 | 验证方法 |
|--------|----------|
| 登录注册 | 可正常注册和登录，Token正确存储 |
| 权限控制 | 不同角色登录后看到不同的菜单和页面 |
| 路由守卫 | 未登录访问受保护页面跳转到登录页 |
| 用户管理 | 管理员可正常管理用户 |
| 产品管理 | 产品CRUD正常，搜索和筛选功能正常 |
| 进货流程 | 创建进货单 → 确认入库 → 库存增加 |
| 销售流程 | 销售开单 → 库存扣减 → 查看详情 → 打印 |
| 退货流程 | 退货处理 → 库存回退 |
| 库存预警 | 低库存产品在首页和预警页正确显示 |
| 库存盘点 | 盘点后库存正确更新 |
| 仪表盘 | 经营概况数据正确，图表正常渲染 |
| 统计报表 | 各报表数据与实际业务数据一致 |
| 页面响应 | 页面在不同屏幕尺寸下正常显示 |
| 表单验证 | 各表单的前端校验正常工作 |

---

## 十、产出文件清单

| 文件类型 | 数量 | 说明 |
|----------|------|------|
| Vue页面组件 | 约25个 | 各业务模块页面 |
| 公共组件 | 约5个 | 布局、导航、分页等 |
| API请求模块 | 约14个 | 各模块API封装 |
| 路由配置 | 1个 | 路由表和守卫 |
| Store模块 | 2个 | 用户状态和应用状态 |
| 工具模块 | 约3个 | Token管理、表单校验等 |
| 配置文件 | 约5个 | vite.config、环境变量等 |
| 全局样式 | 约2个 | 变量、通用样式 |

---

## 十一、注意事项

1. Element Plus 按需引入可减小打包体积，建议使用 `unplugin-vue-components` 插件。
2. ECharts 图表建议封装为独立组件，方便复用。
3. 表单提交前进行前端校验，减少无效请求。
4. 列表页面统一处理空状态（无数据时显示提示）。
5. 打印功能需要特殊处理样式，使用 `@media print` 隐藏非打印区域。
6. 前端路由权限控制基于用户角色，侧边栏菜单动态渲染。
7. 开发环境使用 Vite proxy 解决跨域，生产环境通过 Nginx 反向代理。
8. 建议使用 Vue 3 Composition API（`<script setup>`）编写组件。