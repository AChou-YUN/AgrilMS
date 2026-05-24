<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-area">
          <el-icon :size="28" color="#fff"><Management /></el-icon>
        </div>
        <h2 class="login-title">农资进销存管理系统</h2>
        <p class="login-subtitle">Agricultural Inventory Management System</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password clearable />
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
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1D2B3D 0%, #2C4A6E 50%, #4B6EAF 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-lg);
}
.login-header { text-align: center; margin-bottom: 32px; }
.logo-area {
  width: 56px; height: 56px; margin: 0 auto 16px;
  background: var(--color-primary);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.login-title {
  font-size: 20px; font-weight: 700; color: var(--text-primary); margin: 0 0 6px;
}
.login-subtitle { font-size: 12px; color: var(--text-muted); margin: 0; }
.login-btn {
  width: 100%; height: 42px; font-size: 15px; letter-spacing: 2px;
}
.login-footer { text-align: center; margin-top: 16px; font-size: 13px; color: var(--text-secondary); }
.login-footer a { color: var(--color-primary); margin-left: 4px; }
.login-footer a:hover { color: var(--color-primary-light); }
</style>