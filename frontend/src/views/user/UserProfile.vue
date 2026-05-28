<template>
  <div class="page-container">
    <div class="profile-grid">
      <el-card shadow="never" class="profile-card">
        <template #header><span class="card-title">个人信息</span></template>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 480px;">
          <el-form-item label="用户名">
            <el-input :model-value="userStore.username" disabled />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleSave">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card shadow="never" class="profile-card">
        <template #header><span class="card-title">修改密码</span></template>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width: 400px;">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { updateProfile, changePassword, getUserInfo } from '@/api/auth'

const userStore = useUserStore()
const formRef = ref(null)
const pwdFormRef = ref(null)
const loading = ref(false)
const pwdLoading = ref(false)

const form = reactive({ realName: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '' })

const rules = { realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }] }
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }]
}

onMounted(async () => {
  try {
    const res = await getUserInfo()
    const data = res.data
    form.realName = data.realName || ''
    form.phone = data.phone || ''
    form.email = data.email || ''
  } catch (e) { /* handled */ }
})

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await updateProfile(form)
      ElMessage.success('保存成功')
      userStore.getInfo()
    } catch (e) { /* handled */ }
    loading.value = false
  })
}

async function handleChangePwd() {
  if (!pwdFormRef.value) return
  await pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    pwdLoading.value = true
    try {
      await changePassword(pwdForm)
      ElMessage.success('密码修改成功')
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
    } catch (e) { /* handled */ }
    pwdLoading.value = false
  })
}
</script>

<style scoped>
.profile-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: var(--space-5);
}
@media (max-width: 768px) {
  .profile-grid { grid-template-columns: 1fr; }
}
.card-title {
  font-weight: var(--weight-semibold);
  color: var(--text-primary);
  font-size: var(--text-lg);
}
</style>
