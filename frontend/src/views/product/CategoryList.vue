<template>
  <div class="page-container">
    <div class="table-card">
      <div class="table-toolbar">
        <span>农资分类</span>
        <el-button type="primary" @click="showDialog()">新增分类</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" min-width="60" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" min-width="70" />
        <el-table-column prop="createTime" label="创建时间" min-width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="showDialog(row)">编辑</el-button>
            <el-popconfirm title="确定删除此分类？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" text size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
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
import { getCategoryList, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { formatDate } from '@/utils/constants'

const loading = ref(false); const submitLoading = ref(false); const list = ref([])
const dialogVisible = ref(false); const isEdit = ref(false); const editId = ref(null); const formRef = ref(null)
const form = reactive({ name: '', description: '', sortOrder: 0 })
const rules = { name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try { const res = await getCategoryList(); list.value = res.data || [] } catch (e) { /* handled */ }
  loading.value = false
}

function showDialog(row) {
  if (row) { isEdit.value = true; editId.value = row.id; Object.assign(form, { name: row.name, description: row.description || '', sortOrder: row.sortOrder || 0 }) }
  else { isEdit.value = false; editId.value = null; Object.assign(form, { name: '', description: '', sortOrder: 0 }) }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value) await updateCategory(editId.value, form)
      else await createCategory(form)
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    } catch (e) { /* handled */ }
    submitLoading.value = false
  })
}

async function handleDelete(id) {
  try { await deleteCategory(id); ElMessage.success('删除成功'); loadData() } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>