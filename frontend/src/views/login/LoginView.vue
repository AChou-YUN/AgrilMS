<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-area">
          <el-icon :size="24" color="var(--color-primary)"><Management /></el-icon>
        </div>
        <h2 class="login-title">农资进销存管理系统</h2>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login(loginForm)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (e) {} finally { loading.value = false }
  })
}
</script>

<style scoped>
/* ===== 容器 ===== */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--surface-bg);
}

/* ===== 登录卡片 ===== */
.login-card {
  width: 420px;
  padding: var(--space-10) var(--space-8);
  background: var(--surface-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-light);
  animation: cardEntrance 0.5s var(--ease-spring) both;
}

@keyframes cardEntrance {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 头部 ===== */
.login-header {
  text-align: center;
  margin-bottom: var(--space-8);
}

.logo-area {
  width: 48px;
  height: 48px;
  margin: 0 auto var(--space-4);
  background: var(--color-primary-soft);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background var(--duration-normal) var(--ease-smooth);
}

.logo-area:hover {
  background: var(--color-primary-bg);
}

.login-title {
  font-size: var(--text-xl);
  font-weight: var(--weight-bold);
  color: var(--text-primary);
  margin: 0;
  letter-spacing: -0.01em;
}

/* ===== 输入框 ===== */
.login-card :deep(.el-form-item) {
  margin-bottom: var(--space-5);
}

.login-card :deep(.el-form-item:last-of-type) {
  margin-bottom: 0;
}

.login-card :deep(.el-form-item .el-input__wrapper) {
  background-color: var(--surface-card);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-sm);
  padding: 4px 12px;
  box-shadow: none !important;
  transition: all var(--duration-normal) var(--ease-smooth);
  height: 44px;
  width: 100%;
}

.login-card :deep(.el-form-item .el-input__wrapper:hover) {
  border-color: var(--border-dark);
}

.login-card :deep(.el-form-item .el-input__wrapper.is-focus) {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-soft) !important;
}

.login-card :deep(.el-form-item .el-input__prefix) {
  color: var(--text-muted);
}

.login-card :deep(.el-form-item .el-input__inner) {
  color: var(--text-primary);
  font-size: var(--text-base);
}

.login-card :deep(.el-form-item .el-input__inner::placeholder) {
  color: var(--text-placeholder);
}

/* ===== 登录按钮 ===== */
.login-btn {
  width: 100%;
  height: 44px;
  font-size: var(--text-base);
  font-weight: var(--weight-semibold);
  letter-spacing: 0.5em;
  border-radius: var(--radius-sm);
  transition: all var(--duration-normal) var(--ease-spring);
  margin-top: var(--space-1);
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(79, 110, 247, 0.35);
}

.login-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.25);
}

/* ===== 底部 ===== */
.login-footer {
  text-align: center;
  margin-top: var(--space-6);
  font-size: var(--text-sm);
  color: var(--text-secondary);
}

.login-footer a {
  color: var(--color-primary);
  margin-left: var(--space-1);
  font-weight: var(--weight-medium);
  transition: color var(--duration-fast) var(--ease-smooth);
}

.login-footer a:hover {
  color: var(--color-primary-dark);
}
</style>