<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="query.realName" placeholder="请输入真实姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="table-toolbar">
        <span>用户列表</span>
        <el-button type="primary" @click="showAddDialog">新增用户</el-button>
      </div>

      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column label="角色" min-width="130">
          <template #default="{ row }">
            <el-tag v-for="role in row.roles" :key="role.id" size="small" class="role-tag">
              {{ role.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" text size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确定删除此用户？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" text size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUsers"
          @current-change="loadUsers"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, updateUserStatus, assignRoles } from '@/api/user'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const userList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const query = reactive({ username: '', realName: '', status: undefined, pageNum: 1, pageSize: 10 })
const form = reactive({ username: '', password: '', realName: '', phone: '', email: '', roleIds: [] })
const roleOptions = ref([])

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function formatTime(t) { return t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '' }

async function loadUsers() {
  loading.value = true
  try {
    const params = { ...query }
    if (params.status === undefined || params.status === '') delete params.status
    const res = await getUserList(params)
    userList.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadUsers() }
function resetQuery() { query.username = ''; query.realName = ''; query.status = undefined; query.pageNum = 1; loadUsers() }

function showAddDialog() {
  isEdit.value = false; editId.value = null
  Object.assign(form, { username: '', password: '', realName: '', phone: '', email: '', roleIds: [] })
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true; editId.value = row.id
  Object.assign(form, { username: row.username, password: '', realName: row.realName, phone: row.phone || '', email: row.email || '', roleIds: [] })
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateUser(editId.value, { realName: form.realName, phone: form.phone, email: form.email })
        ElMessage.success('编辑成功')
      } else {
        await createUser(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadUsers()
    } catch (e) { /* handled */ }
    submitLoading.value = false
  })
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateUserStatus(row.id, newStatus)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (e) { /* handled */ }
}

async function handleDelete(id) {
  try {
    await deleteUser(id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (e) { /* handled */ }
}

onMounted(() => { loadUsers() })
</script>

<style scoped>
.role-tag { margin-right: 4px; }
</style>
