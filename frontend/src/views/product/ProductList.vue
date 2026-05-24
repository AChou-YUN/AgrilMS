<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="产品名称">
          <el-input v-model="query.name" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
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
        <span>农资产品列表</span>
        <el-button type="primary" @click="$router.push('/products/add')">新增产品</el-button>
      </div>
      <el-table :data="productList" v-loading="loading" stripe>
        <el-table-column prop="name" label="产品名称" min-width="120" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column prop="specification" label="规格" min-width="100" />
        <el-table-column prop="unitName" label="单位" min-width="70" />
        <el-table-column prop="currentStock" label="当前库存" min-width="90">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.currentStock < row.safetyStock }">{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" min-width="90" />
        <el-table-column prop="purchasePrice" label="参考进价" min-width="90" />
        <el-table-column prop="sellingPrice" label="参考售价" min-width="90" />
        <el-table-column label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="$router.push('/products/edit/' + row.id)">编辑</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" text size="small" @click="toggleStatus(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
            <el-popconfirm title="确定删除此产品？" @confirm="handleDelete(row.id)">
              <template #reference><el-button type="danger" text size="small">删除</el-button></template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-area">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductList, deleteProduct, updateProductStatus } from '@/api/product'
import { getCategoryList } from '@/api/category'

const loading = ref(false)
const productList = ref([])
const categories = ref([])
const total = ref(0)
const query = reactive({ name: '', categoryId: undefined, status: undefined, pageNum: 1, pageSize: 10 })

async function loadCategories() {
  try { const res = await getCategoryList(); categories.value = res.data || [] } catch (e) { /* handled */ }
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.categoryId) delete params.categoryId
    if (params.status === undefined || params.status === '') delete params.status
    const res = await getProductList(params)
    productList.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { name: '', categoryId: undefined, status: undefined, pageNum: 1 }); loadData() }

async function toggleStatus(row) {
  try {
    await updateProductStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) { /* handled */ }
}

async function handleDelete(id) {
  try {
    await deleteProduct(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { /* handled */ }
}

onMounted(() => { loadCategories(); loadData() })
</script>

<style scoped>
</style>
