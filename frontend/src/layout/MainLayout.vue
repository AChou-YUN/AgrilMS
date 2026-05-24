<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="aside-header">
        <div class="logo-icon">
          <el-icon :size="22" color="#E8DCC7"><Management /></el-icon>
        </div>
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
            <template #title>
              <el-icon><Goods /></el-icon>
              <span>农资管理</span>
            </template>
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
            <template #title>
              <el-icon><Box /></el-icon>
              <span>库存管理</span>
            </template>
            <el-menu-item index="/inventory">库存列表</el-menu-item>
            <el-menu-item index="/inventory/warnings">库存预警</el-menu-item>
            <el-menu-item v-if="hasRole(['ADMIN', 'WAREHOUSE'])" index="/inventory/check">库存盘点</el-menu-item>
            <el-menu-item index="/inventory/logs">库存日志</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="report-menu">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>统计报表</span>
            </template>
            <el-menu-item index="/reports/sales">销售报表</el-menu-item>
            <el-menu-item index="/reports/purchase">进货报表</el-menu-item>
            <el-menu-item index="/reports/inventory">库存报表</el-menu-item>
            <el-menu-item index="/reports/profit">利润报表</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 右侧主体 -->
    <el-container class="layout-main">
      <!-- 顶栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            :size="20"
            @click="isCollapse = !isCollapse"
          >
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>

          <!-- 面包屑 -->
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" class="user-avatar">
                {{ userStore.realName ? userStore.realName[0] : 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.realName || userStore.username }}</span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
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

const activeMenu = computed(() => {
  return route.path
})

const currentRoute = computed(() => route)

/**
 * 角色检查
 */
function hasRole(requiredRoles) {
  if (!requiredRoles || requiredRoles.length === 0) return true
  const userRoles = userStore.roles || []
  return requiredRoles.some(role => userRoles.includes(role))
}

/**
 * 下拉菜单命令
 */
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
  background-color: var(--color-moss-dark);
  transition: width 0.3s ease;
  overflow: hidden;
}

.aside-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background-color: var(--color-moss);
  overflow: hidden;
  border-bottom: 1px solid rgba(232, 220, 199, 0.1);
}

.logo-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(232, 220, 199, 0.15);
  border-radius: 8px;
}

.logo-text {
  color: var(--text-on-dark);
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  margin-left: 10px;
  letter-spacing: 1px;
  font-family: var(--font-display);
}

.aside-scroll {
  height: calc(100vh - 60px);
}

.aside-menu {
  border-right: none;
}

.aside-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 侧边栏菜单样式覆盖 */
.aside-menu {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(232, 220, 199, 0.7);
  --el-menu-active-color: #E8DCC7;
  --el-menu-hover-bg-color: rgba(232, 220, 199, 0.1);
}

.aside-menu .el-menu-item {
  color: rgba(232, 220, 199, 0.7);
  font-size: 14px;
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.aside-menu .el-menu-item:hover {
  background: rgba(232, 220, 199, 0.12) !important;
  color: #E8DCC7;
}

.aside-menu .el-menu-item.is-active {
  background: rgba(139, 157, 131, 0.3) !important;
  color: #E8DCC7;
  font-weight: 500;
}

.aside-menu .el-sub-menu__title {
  color: rgba(232, 220, 199, 0.7);
  font-size: 14px;
  height: 48px;
  line-height: 48px;
  margin: 2px 8px;
  border-radius: 8px;
}

.aside-menu .el-sub-menu__title:hover {
  background: rgba(232, 220, 199, 0.12) !important;
  color: #E8DCC7;
}

.layout-main {
  flex: 1;
  overflow: hidden;
  background-color: var(--surface-sand);
}

.layout-header {
  height: 60px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(96, 108, 56, 0.06);
  z-index: 10;
  border-bottom: 1px solid var(--border-light);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: var(--text-secondary);
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: var(--color-sage);
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
  gap: 8px;
  color: var(--text-secondary);
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.3s;
}

.user-dropdown:hover {
  color: var(--color-sage);
  background: var(--fill-color-lighter);
}

.user-avatar {
  background: linear-gradient(135deg, var(--color-sage) 0%, var(--color-moss) 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: var(--font-body);
}

.layout-content {
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
  background-color: var(--surface-sand);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>