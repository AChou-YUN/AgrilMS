<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="请输入客户姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-card">
      <div class="table-toolbar">
        <span>客户列表</span>
        <el-button type="primary" @click="showDialog()">新增客户</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="120" />
        <el-table-column prop="address" label="地址" min-width="180" />
        <el-table-column prop="preference" label="购买偏好" min-width="140" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除此客户？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" text size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-area">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="购买偏好"><el-input v-model="form.preference" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
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
import { getCustomerList, createCustomer, updateCustomer, deleteCustomer } from '@/api/customer'

const loading = ref(false); const submitLoading = ref(false); const list = ref([]); const total = ref(0)
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(null); const formRef = ref(null)
const query = reactive({ name: '', phone: '', pageNum: 1, pageSize: 10 })
const form = reactive({ name: '', phone: '', address: '', preference: '', remark: '' })
const rules = { name: [{ required: true, message: '请输入客户姓名', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getCustomerList(query)
    list.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { name: '', phone: '', pageNum: 1 }); loadData() }

function showDialog(row) {
  if (row) { isEdit.value = true; editId.value = row.id; Object.assign(form, { name: row.name, phone: row.phone || '', address: row.address || '', preference: row.preference || '', remark: row.remark || '' }) }
  else { isEdit.value = false; editId.value = null; Object.assign(form, { name: '', phone: '', address: '', preference: '', remark: '' }) }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) await updateCustomer(editId.value, form)
      else await createCustomer(form)
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    } catch (e) { /* handled */ }
    submitLoading.value = false
  })
}

async function handleDelete(id) {
  try { await deleteCustomer(id); ElMessage.success('删除成功'); loadData() } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>