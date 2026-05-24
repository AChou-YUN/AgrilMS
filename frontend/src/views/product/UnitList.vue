<template>
  <div class="page-container">
    <div class="table-card">
      <div class="table-toolbar">
        <span>计量单位</span>
        <el-button type="primary" @click="showDialog()">新增单位</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="单位名称" min-width="150" />
        <el-table-column prop="abbreviation" label="缩写" width="100" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-popconfirm title="确定删除此单位？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" text size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dialogVisible" title="新增单位" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="缩写"><el-input v-model="form.abbreviation" /></el-form-item>
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
import { getUnitList, createUnit, deleteUnit } from '@/api/unit'

const loading = ref(false); const submitLoading = ref(false); const list = ref([])
const dialogVisible = ref(false); const formRef = ref(null)
const form = reactive({ name: '', abbreviation: '' })
const rules = { name: [{ required: true, message: '请输入单位名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try { const res = await getUnitList(); list.value = res.data || [] } catch (e) { /* handled */ }
  loading.value = false
}

function showDialog() { Object.assign(form, { name: '', abbreviation: '' }); dialogVisible.value = true }

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try { await createUnit(form); ElMessage.success('新增成功'); dialogVisible.value = false; loadData() } catch (e) { /* handled */ }
    submitLoading.value = false
  })
}

async function handleDelete(id) {
  try { await deleteUnit(id); ElMessage.success('删除成功'); loadData() } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>