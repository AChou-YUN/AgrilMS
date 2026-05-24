<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="aside-header">
        <el-icon :size="20" color="#fff"><Management /></el-icon>
        <transition name="fade">
          <span v-show="!isCollapse" class="logo-text">农资管理系统</span>
        </transition>
      </div>
      <el-scrollbar class="aside-scroll">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          class="aside-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          <el-menu-item v-if="hasRole(['ADMIN'])" index="/users">
            <el-icon><User /></el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-sub-menu v-if="hasRole(['ADMIN', 'DEALER'])" index="product-menu">
            <template #title><el-icon><Goods /></el-icon><span>农资管理</span></template>
            <el-menu-item index="/products">农资产品</el-menu-item>
            <el-menu-item v-if="hasRole(['ADMIN'])" index="/categories">农资分类</el-menu-item>
            <el-menu-item v-if="hasRole(['ADMIN'])" index="/units">计量单位</el-menu-item>
          </el-sub-menu>
          <el-menu-item v-if="hasRole(['ADMIN', 'DEALER'])" index="/suppliers">
            <el-icon><Van /></el-icon>
            <template #title>供应商管理</template>
          </el-menu-item>
          <el-menu-item v-if="hasRole(['ADMIN', 'DEALER'])" index="/purchase-orders">
            <el-icon><ShoppingCart /></el-icon>
            <template #title>进货管理</template>
          </el-menu-item>
          <el-menu-item v-if="hasRole(['ADMIN', 'RETAILER'])" index="/customers">
            <el-icon><UserFilled /></el-icon>
            <template #title>客户管理</template>
          </el-menu-item>
          <el-menu-item v-if="hasRole(['ADMIN', 'RETAILER'])" index="/sales-orders">
            <el-icon><Sell /></el-icon>
            <template #title>销售管理</template>
          </el-menu-item>
          <el-sub-menu index="inventory-menu">
            <template #title><el-icon><Box /></el-icon><span>库存管理</span></template>
            <el-menu-item index="/inventory">库存列表</el-menu-item>
            <el-menu-item index="/inventory/warnings">库存预警</el-menu-item>
            <el-menu-item v-if="hasRole(['ADMIN', 'WAREHOUSE'])" index="/inventory/check">库存盘点</el-menu-item>
            <el-menu-item index="/inventory/logs">库存日志</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="report-menu">
            <template #title><el-icon><DataAnalysis /></el-icon><span>统计报表</span></template>
            <el-menu-item index="/reports/sales">销售报表</el-menu-item>
            <el-menu-item index="/reports/purchase">进货报表</el-menu-item>
            <el-menu-item index="/reports/inventory">库存报表</el-menu-item>
            <el-menu-item index="/reports/profit">利润报表</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>
    <el-container class="layout-main">
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="isCollapse = !isCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">{{ currentRoute.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="30" class="user-avatar">{{ userStore.realName ? userStore.realName[0] : 'U' }}</el-avatar>
              <span class="user-name">{{ userStore.realName || userStore.username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)

function hasRole(requiredRoles) {
  if (!requiredRoles || requiredRoles.length === 0) return true
  const userRoles = userStore.roles || []
  return requiredRoles.some(role => userRoles.includes(role))
}

function handleCommand(command) {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background-color: var(--sidebar-bg);
  transition: width 0.3s ease;
  overflow: hidden;
}

.aside-header {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 16px;
  background-color: var(--sidebar-header-bg);
  overflow: hidden;
}

.logo-text {
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
}

.aside-scroll {
  height: calc(100vh - 50px);
}

.aside-menu {
  border-right: none;
}

.aside-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 侧边栏菜单样式 */
.aside-menu {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: var(--sidebar-text);
  --el-menu-active-color: var(--sidebar-text-active);
  --el-menu-hover-bg-color: var(--sidebar-hover);
  --el-menu-item-height: 42px;
  --el-menu-sub-menu-title-height: 42px;
}

/* ===== Element Plus 内部样式穿透 ===== */

/* 一级菜单项 */
:deep(.aside-menu) .el-menu-item {
  padding-left: 20px !important;
  height: 42px !important;
  line-height: 42px !important;
  margin: 2px 8px !important;
  border-radius: 4px;
  font-size: 14px;
}

/* 二级菜单标题 */
:deep(.aside-menu) .el-sub-menu__title {
  display: flex !important;
  justify-content: flex-start !important;
  align-items: center;
  padding-left: 20px !important;
  padding-right: 36px !important;
  height: 42px !important;
  line-height: 42px !important;
  margin: 2px 8px !important;
  border-radius: 4px;
  position: relative;
}

/* 图标统一样式 */
:deep(.aside-menu) .el-menu-item .el-icon,
:deep(.aside-menu) .el-sub-menu__title .el-icon {
  margin-right: 8px;
  font-size: 16px;
  width: 16px;
  flex-shrink: 0;
}

/* 展开箭头绝对定位到最右侧 */
:deep(.aside-menu) .el-sub-menu__icon-arrow {
  position: absolute !important;
  right: 14px !important;
  top: 50% !important;
  transform: translateY(-50%);
  font-size: 12px;
  width: auto !important;
  margin: 0 !important;
}

/* 子菜单子项缩进 */
:deep(.aside-menu) .el-sub-menu .el-menu-item {
  padding-left: 50px !important;
}

/* hover 状态 */
:deep(.aside-menu) .el-menu-item:hover,
:deep(.aside-menu) .el-sub-menu__title:hover {
  background: var(--sidebar-hover) !important;
  color: var(--sidebar-text-active) !important;
}

/* active 状态 */
:deep(.aside-menu) .el-menu-item.is-active {
  background: var(--sidebar-active) !important;
  color: var(--sidebar-text-active) !important;
  font-weight: 500;
}

/* ===== 折叠状态 ===== */
:deep(.el-menu--collapse) .aside-menu .el-menu-item,
:deep(.el-menu--collapse) .aside-menu .el-sub-menu__title {
  justify-content: center !important;
  padding-left: 0 !important;
  padding-right: 0 !important;
  margin: 2px 6px !important;
}

:deep(.el-menu--collapse) .aside-menu .el-menu-item .el-icon,
:deep(.el-menu--collapse) .aside-menu .el-sub-menu__title .el-icon {
  margin: 0 !important;
  font-size: 18px;
}

:deep(.el-menu--collapse) .aside-menu .el-sub-menu__icon-arrow {
  display: none;
}

.layout-main {
  flex: 1;
  overflow: hidden;
  background-color: var(--surface-bg);
}

.layout-header {
  height: 50px;
  background: var(--surface-card);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: var(--shadow-sm);
  z-index: 10;
  border-bottom: 1px solid var(--border-light);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: var(--color-primary);
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 6px;
  color: var(--text-regular);
  padding: 4px 8px;
  border-radius: 4px;
}

.user-dropdown:hover {
  color: var(--color-primary);
}

.user-avatar {
  background: var(--color-primary);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.layout-content {
  padding: 16px;
  overflow-y: auto;
  height: calc(100vh - 50px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>