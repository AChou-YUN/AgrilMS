<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="名称">
          <el-input v-model="query.name" placeholder="请输入供应商名称" clearable />
        </el-form-item>
        <el-form-item label="信用等级">
          <el-select v-model="query.creditLevel" placeholder="全部" clearable>
            <el-option v-for="n in 5" :key="n" :label="n + '级'" :value="n" />
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
        <span>供应商列表</span>
        <el-button type="primary" @click="showDialog()">新增供应商</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="name" label="供应商名称" min-width="140" />
        <el-table-column prop="contactPerson" label="联系人" min-width="100" />
        <el-table-column prop="phone" label="联系电话" min-width="120" />
        <el-table-column prop="address" label="地址" min-width="160" />
        <el-table-column label="信用等级" min-width="100">
          <template #default="{ row }">
            <el-rate v-model="row.creditLevel" disabled :max="5" />
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showDialog(row)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" text size="small" @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            <el-popconfirm title="确定删除此供应商？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" text size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-area">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新增供应商'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="form.contactPerson" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-form-item label="信用等级">
          <el-rate v-model="form.creditLevel" :max="5" />
        </el-form-item>
        <el-form-item label="银行账号"><el-input v-model="form.bankAccount" /></el-form-item>
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
import { getSupplierList, createSupplier, updateSupplier, deleteSupplier, updateSupplierStatus } from '@/api/supplier'

const loading = ref(false); const submitLoading = ref(false); const list = ref([]); const total = ref(0)
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(null); const formRef = ref(null)
const query = reactive({ name: '', creditLevel: undefined, pageNum: 1, pageSize: 10 })
const form = reactive({ name: '', contactPerson: '', phone: '', address: '', creditLevel: 3, bankAccount: '', remark: '' })
const rules = { name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }; if (!params.creditLevel) delete params.creditLevel
    const res = await getSupplierList(params)
    list.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { name: '', creditLevel: undefined, pageNum: 1 }); loadData() }

function showDialog(row) {
  if (row) { isEdit.value = true; editId.value = row.id; Object.assign(form, { name: row.name, contactPerson: row.contactPerson || '', phone: row.phone || '', address: row.address || '', creditLevel: row.creditLevel || 3, bankAccount: row.bankAccount || '', remark: row.remark || '' }) }
  else { isEdit.value = false; editId.value = null; Object.assign(form, { name: '', contactPerson: '', phone: '', address: '', creditLevel: 3, bankAccount: '', remark: '' }) }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) await updateSupplier(editId.value, form)
      else await createSupplier(form)
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    } catch (e) { /* handled */ }
    submitLoading.value = false
  })
}

async function toggleStatus(row) {
  try { await updateSupplierStatus(row.id, row.status === 1 ? 0 : 1); ElMessage.success('操作成功'); loadData() } catch (e) { /* handled */ }
}

async function handleDelete(id) {
  try { await deleteSupplier(id); ElMessage.success('删除成功'); loadData() } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>

<style scoped>
</style>
