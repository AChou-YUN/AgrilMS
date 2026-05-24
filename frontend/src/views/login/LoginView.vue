<template>
  <div class="login-container">
    <!-- 动态浮动装饰元素 -->
    <div class="floating-elements" :class="{ visible: phase === 'ready' }">
      <div class="float-leaf float-1"></div>
      <div class="float-leaf float-2"></div>
      <div class="float-leaf float-3"></div>
      <div class="float-leaf float-4"></div>
      <div class="float-circle float-5"></div>
      <div class="float-circle float-6"></div>
    </div>

    <!-- 背景光晕 -->
    <div class="bg-glow" :class="{ expand: phase !== 'splash' }"></div>

    <!-- 阶段一+二：欢迎词动画层 -->
    <div class="splash-layer" :class="phase">
      <div class="splash-content">
        <!-- Logo -->
        <div class="splash-logo">
          <div class="splash-logo-ring"></div>
          <el-icon :size="36" color="#fff"><Management /></el-icon>
        </div>
        <!-- 中文逐字 -->
        <h1 class="splash-title-cn">
          <span
            v-for="(char, i) in titleChars"
            :key="i"
            class="char"
            :style="{ animationDelay: (0.3 + i * 0.08) + 's' }"
          >{{ char }}</span>
        </h1>
        <!-- 英文 -->
        <p class="splash-title-en">Agricultural Inventory Management System</p>
      </div>
    </div>

    <!-- 阶段三：登录卡片 -->
    <div class="login-card" v-show="phase === 'ready'" :class="{ 'card-enter': phase === 'ready' }">
      <div class="login-header">
        <div class="logo-area">
          <div class="logo-ring"></div>
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
import { ref, reactive, onMounted } from 'vue'
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

// 动画阶段控制
const phase = ref('splash') // splash → transition → ready
const titleChars = '欢迎进行农资进销存管理系统'.split('')

onMounted(() => {
  // 阶段一：splash 显示 2s
  setTimeout(() => {
    phase.value = 'transition'
  }, 2000)

  // 阶段二：transition 持续 1s
  setTimeout(() => {
    phase.value = 'ready'
  }, 3000)
})

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
  background: linear-gradient(135deg, #0F1A2A 0%, #1D2B3D 40%, #2C4A6E 100%);
  position: relative;
  overflow: hidden;
}

/* SVG 颗粒纹理 */
.login-container::before {
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

/* ===== 背景光晕 ===== */
.bg-glow {
  position: absolute;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(75, 110, 175, 0.4) 0%, rgba(75, 110, 175, 0) 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 2;
  transition: all 2s cubic-bezier(0.22, 1, 0.36, 1);
  pointer-events: none;
}

.bg-glow.expand {
  width: 200vw;
  height: 200vh;
  opacity: 0.3;
}

/* ===== 浮动装饰元素 ===== */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 3;
  opacity: 0;
  transition: opacity 1s ease;
}

.floating-elements.visible {
  opacity: 1;
}

.float-leaf {
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50% 0 50% 0;
  opacity: 0.08;
}

.float-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
}

.float-1 { background: #6B8EC5; top: 10%; left: 8%; width: 60px; height: 60px; animation: floatUp 8s ease-in-out infinite; }
.float-2 { background: #529B5A; top: 60%; left: 12%; width: 35px; height: 35px; animation: floatUp 10s ease-in-out 2s infinite; }
.float-3 { background: #4B6EAF; top: 20%; right: 10%; width: 50px; height: 50px; animation: floatUp 9s ease-in-out 1s infinite; }
.float-4 { background: #6B8EC5; bottom: 15%; right: 15%; width: 45px; height: 45px; animation: floatUp 11s ease-in-out 3s infinite; }
.float-5 { background: #4B6EAF; top: 70%; left: 50%; width: 80px; height: 80px; animation: floatUp 12s ease-in-out 4s infinite; }
.float-6 { background: #6B8EC5; top: 35%; left: 30%; width: 25px; height: 25px; animation: floatUp 7s ease-in-out 1.5s infinite; }

@keyframes floatUp {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(15deg); }
}

/* ===== 欢迎词动画层 ===== */
.splash-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  pointer-events: none;
}

.splash-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Logo 动画 */
.splash-logo {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #4B6EAF 0%, #3A5A95 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 12px 40px rgba(75, 110, 175, 0.5);
  margin-bottom: 32px;
  animation: logoAppear 0.6s ease-out 0.1s both;
}

.splash-logo-ring {
  position: absolute;
  width: 96px;
  height: 96px;
  border: 2px solid rgba(75, 110, 175, 0.3);
  border-radius: 26px;
  animation: splashRingPulse 2s ease-in-out infinite;
}

@keyframes splashRingPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0; }
}

@keyframes logoAppear {
  from { opacity: 0; transform: scale(0.5); }
  to { opacity: 1; transform: scale(1); }
}

/* 中文逐字闪出 */
.splash-title-cn {
  font-size: 28px;
  font-weight: 700;
  color: #FFFFFF;
  margin: 0 0 16px;
  letter-spacing: 2px;
}

.splash-title-cn .char {
  display: inline-block;
  opacity: 0;
  animation: charGlow 0.4s ease-out both;
}

@keyframes charGlow {
  0% {
    opacity: 0;
    transform: translateY(10px) scale(0.8);
    text-shadow: 0 0 30px rgba(75, 110, 175, 0.8), 0 0 60px rgba(75, 110, 175, 0.4);
  }
  50% {
    text-shadow: 0 0 20px rgba(75, 110, 175, 0.6), 0 0 40px rgba(75, 110, 175, 0.2);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
    text-shadow: 0 0 8px rgba(75, 110, 175, 0.3);
  }
}

/* 英文淡入 */
.splash-title-en {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  letter-spacing: 1.5px;
  opacity: 0;
  animation: enFadeIn 0.8s ease-out 1.8s both;
}

@keyframes enFadeIn {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 阶段二：文字上移缩小 */
.splash-layer.transition .splash-content {
  animation: contentShrink 1s cubic-bezier(0.22, 1, 0.36, 1) forwards;
}

@keyframes contentShrink {
  0% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
  100% {
    transform: translateY(-180px) scale(0.6);
    opacity: 0;
  }
}

/* 欢迎层在 ready 后隐藏 */
.splash-layer.ready {
  display: none;
}

/* ===== 登录卡片 ===== */
.login-card {
  width: 440px;
  padding: 44px;
  background: #FFFFFF;
  border-radius: 20px;
  box-shadow: 0 32px 80px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 5;
}

/* 卡片入场动画 */
.card-enter .login-card,
.login-card.card-enter {
  animation: cardSlideIn 0.8s cubic-bezier(0.22, 1, 0.36, 1) forwards;
  opacity: 0;
  transform: translateY(50px) scale(0.95);
}

@keyframes cardSlideIn {
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

/* Logo 区域带光环 */
.logo-area {
  width: 64px;
  height: 64px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #4B6EAF 0%, #3A5A95 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  box-shadow: 0 8px 24px rgba(75, 110, 175, 0.4);
}

.logo-ring {
  position: absolute;
  width: 80px;
  height: 80px;
  border: 2px solid rgba(75, 110, 175, 0.2);
  border-radius: 22px;
  animation: ringPulse 3s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.6; }
  50% { transform: scale(1.15); opacity: 0; }
}

.login-title {
  font-size: 22px;
  font-weight: 700;
  color: #1F2D3D;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 13px;
  color: #697B8C;
  margin: 0;
  letter-spacing: 0.5px;
}

/* ===== 输入框样式 ===== */
.login-card :deep(.el-form-item .el-input__wrapper) {
  background-color: #F8F9FB;
  border: 1px solid #E4E8ED;
  border-radius: 12px;
  padding: 8px 16px;
  box-shadow: none !important;
  transition: all 0.3s ease;
  height: 46px;
}

.login-card :deep(.el-form-item .el-input__wrapper:hover) {
  border-color: #B8C8DC;
  background-color: #FFFFFF;
}

.login-card :deep(.el-form-item .el-input__wrapper.is-focus) {
  border-color: #4B6EAF;
  background-color: #FFFFFF;
  box-shadow: 0 0 0 3px rgba(75, 110, 175, 0.12) !important;
}

.login-card :deep(.el-form-item .el-input__prefix) {
  color: #78909C;
}

.login-card :deep(.el-form-item .el-input__inner) {
  color: #1F2D3D;
  font-size: 14px;
  height: 100%;
}

.login-card :deep(.el-form-item .el-input__inner::placeholder) {
  color: #9DAAB6;
}

/* ===== 登录按钮 ===== */
.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 3px;
  background: linear-gradient(135deg, #4B6EAF 0%, #3A5A95 100%);
  border: none;
  border-radius: 12px;
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1);
  margin-top: 8px;
  position: relative;
  overflow: hidden;
}

.login-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5B7EBF 0%, #4A6AA5 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(75, 110, 175, 0.5);
}

.login-btn:hover::after {
  left: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #697B8C;
}

.login-footer a {
  color: #4B6EAF;
  margin-left: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.login-footer a:hover {
  color: #3A5A95;
  text-decoration: underline;
}
</style>