import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/RegisterView.vue'),
    meta: { public: true, title: '注册' }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      // 首页仪表盘
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },

      // 用户管理
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['ADMIN'] }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/UserProfile.vue'),
        meta: { title: '个人中心', hidden: true }
      },

      // 农资管理
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/product/ProductList.vue'),
        meta: { title: '农资产品', icon: 'Goods', roles: ['ADMIN', 'DEALER'] }
      },
      {
        path: 'products/add',
        name: 'ProductAdd',
        component: () => import('@/views/product/ProductForm.vue'),
        meta: { title: '新增产品', hidden: true, roles: ['ADMIN', 'DEALER'] }
      },
      {
        path: 'products/edit/:id',
        name: 'ProductEdit',
        component: () => import('@/views/product/ProductForm.vue'),
        meta: { title: '编辑产品', hidden: true, roles: ['ADMIN', 'DEALER'] }
      },
      {
        path: 'categories',
        name: 'CategoryList',
        component: () => import('@/views/product/CategoryList.vue'),
        meta: { title: '农资分类', roles: ['ADMIN'] }
      },
      {
        path: 'units',
        name: 'UnitList',
        component: () => import('@/views/product/UnitList.vue'),
        meta: { title: '计量单位', roles: ['ADMIN'] }
      },

      // 供应商管理
      {
        path: 'suppliers',
        name: 'SupplierList',
        component: () => import('@/views/supplier/SupplierList.vue'),
        meta: { title: '供应商管理', icon: 'Van', roles: ['ADMIN', 'DEALER'] }
      },

      // 进货管理
      {
        path: 'purchase-orders',
        name: 'PurchaseOrderList',
        component: () => import('@/views/purchase/PurchaseOrderList.vue'),
        meta: { title: '进货管理', icon: 'ShoppingCart', roles: ['ADMIN', 'DEALER'] }
      },
      {
        path: 'purchase-orders/add',
        name: 'PurchaseOrderAdd',
        component: () => import('@/views/purchase/PurchaseOrderForm.vue'),
        meta: { title: '新增进货单', hidden: true, roles: ['ADMIN', 'DEALER'] }
      },
      {
        path: 'purchase-orders/:id',
        name: 'PurchaseOrderDetail',
        component: () => import('@/views/purchase/PurchaseOrderDetail.vue'),
        meta: { title: '进货单详情', hidden: true, roles: ['ADMIN', 'DEALER'] }
      },

      // 客户管理
      {
        path: 'customers',
        name: 'CustomerList',
        component: () => import('@/views/customer/CustomerList.vue'),
        meta: { title: '客户管理', icon: 'UserFilled', roles: ['ADMIN', 'RETAILER'] }
      },

      // 销售管理
      {
        path: 'sales-orders',
        name: 'SalesOrderList',
        component: () => import('@/views/sales/SalesOrderList.vue'),
        meta: { title: '销售管理', icon: 'Sell', roles: ['ADMIN', 'RETAILER'] }
      },
      {
        path: 'sales-orders/add',
        name: 'SalesOrderAdd',
        component: () => import('@/views/sales/SalesOrderForm.vue'),
        meta: { title: '销售开单', hidden: true, roles: ['ADMIN', 'RETAILER'] }
      },
      {
        path: 'sales-orders/:id',
        name: 'SalesOrderDetail',
        component: () => import('@/views/sales/SalesOrderDetail.vue'),
        meta: { title: '销售单详情', hidden: true, roles: ['ADMIN', 'RETAILER'] }
      },

      // 库存管理
      {
        path: 'inventory',
        name: 'InventoryList',
        component: () => import('@/views/inventory/InventoryList.vue'),
        meta: { title: '库存管理', icon: 'Box', roles: ['ADMIN', 'DEALER', 'RETAILER', 'WAREHOUSE'] }
      },
      {
        path: 'inventory/warnings',
        name: 'InventoryWarning',
        component: () => import('@/views/inventory/InventoryWarning.vue'),
        meta: { title: '库存预警', roles: ['ADMIN', 'DEALER', 'RETAILER', 'WAREHOUSE'] }
      },
      {
        path: 'inventory/check',
        name: 'InventoryCheck',
        component: () => import('@/views/inventory/InventoryCheck.vue'),
        meta: { title: '库存盘点', roles: ['ADMIN', 'WAREHOUSE'] }
      },
      {
        path: 'inventory/logs',
        name: 'InventoryLog',
        component: () => import('@/views/inventory/InventoryLog.vue'),
        meta: { title: '库存日志', roles: ['ADMIN', 'DEALER', 'RETAILER', 'WAREHOUSE'] }
      },

      // 统计报表
      {
        path: 'reports',
        name: 'Reports',
        meta: { title: '统计报表', icon: 'DataAnalysis' },
        redirect: '/reports/sales',
        children: [
          {
            path: 'sales',
            name: 'SalesReport',
            component: () => import('@/views/report/SalesReport.vue'),
            meta: { title: '销售报表' }
          },
          {
            path: 'purchase',
            name: 'PurchaseReport',
            component: () => import('@/views/report/PurchaseReport.vue'),
            meta: { title: '进货报表' }
          },
          {
            path: 'inventory',
            name: 'InventoryReport',
            component: () => import('@/views/report/InventoryReport.vue'),
            meta: { title: '库存报表' }
          },
          {
            path: 'profit',
            name: 'ProfitReport',
            component: () => import('@/views/report/ProfitReport.vue'),
            meta: { title: '利润报表' }
          }
        ]
      }
    ]
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = getToken()

  // 设置页面标题
  const title = to.meta.title ? `${to.meta.title} - 农资进销存管理系统` : '农资进销存管理系统'
  document.title = title

  // 公开页面（登录、注册）无需 Token
  if (to.meta.public) {
    // 已登录用户访问登录页，重定向到首页
    if (token && (to.path === '/login' || to.path === '/register')) {
      next('/dashboard')
    } else {
      next()
    }
    return
  }

  // 非公开页面需要 Token
  if (!token) {
    next('/login')
    return
  }

  next()
})

export default router