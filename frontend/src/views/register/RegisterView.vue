<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <div class="logo-area">
          <el-icon :size="28" color="#fff"><Management /></el-icon>
        </div>
        <h2 class="register-title">注册新账号</h2>
        <p class="register-subtitle">农资进销存管理系统</p>
      </div>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large" label-position="top" @keyup.enter="handleRegister">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="4-20位字母、数字或下划线" prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="Postcard" clearable />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号（选填）" prefix-icon="Phone" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="6-20位密码" prefix-icon="Lock" show-password clearable />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" prefix-icon="Lock" show-password clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" class="register-btn" @click="handleRegister">
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const registerForm = reactive({ username: '', realName: '', phone: '', password: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== registerForm.password) callback(new Error('两次输入的密码不一致'))
  else callback()
}
const validatePhone = (rule, value, callback) => {
  if (!value || /^1[3-9]\d{9}$/.test(value)) callback()
  else callback(new Error('请输入正确的手机号码'))
}
const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 50, message: '用户名长度为3-50个字符', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

async function handleRegister() {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await register({ username: registerForm.username, password: registerForm.password, realName: registerForm.realName, phone: registerForm.phone || undefined })
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } catch (e) {} finally { loading.value = false }
  })
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #4F6EF7 0%, #7B93FA 50%, #A3B5FB 100%);
  position: relative;
}
.register-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.65' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 1;
}
.register-card {
  width: 420px;
  padding: 40px 44px;
  background: #fff;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  z-index: 2;
}
.register-header { text-align: center; margin-bottom: 28px; }
.logo-area {
  width: 56px; height: 56px; margin: 0 auto 16px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border-radius: var(--radius-md);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 8px 24px rgba(79, 110, 247, 0.3);
}
.register-title { font-size: 22px; font-weight: var(--weight-bold); color: var(--text-primary); margin: 0 0 6px; letter-spacing: -0.01em; }
.register-subtitle { font-size: 13px; color: var(--text-muted); margin: 0; }
.register-btn { width: 100%; height: 44px; font-size: 15px; font-weight: var(--weight-semibold); letter-spacing: 1px; margin-top: 4px; }
.register-footer { text-align: center; margin-top: 16px; font-size: 14px; color: var(--text-secondary); }
.register-footer a { color: var(--color-primary); margin-left: 4px; font-weight: var(--weight-medium); }
.register-footer a:hover { color: var(--color-primary-dark); }
</style>