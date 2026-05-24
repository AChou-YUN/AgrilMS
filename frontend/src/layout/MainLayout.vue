<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="aside-header">
        <div class="logo-icon">
          <el-icon :size="24" color="#fff"><Management /></el-icon>
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
          background-color="#001529"
          text-color="rgba(255,255,255,0.65)"
          active-text-color="#fff"
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
  background-color: #001529;
  transition: width 0.3s ease;
  overflow: hidden;
}

.aside-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background-color: #002140;
  overflow: hidden;
}

.logo-icon {
  flex-shrink: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  margin-left: 10px;
  letter-spacing: 1px;
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

.layout-main {
  flex: 1;
  overflow: hidden;
  background-color: #f0f2f5;
}

.layout-header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: #606266;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
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
  color: #606266;
}

.user-dropdown:hover {
  color: #409eff;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
}

.layout-content {
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
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