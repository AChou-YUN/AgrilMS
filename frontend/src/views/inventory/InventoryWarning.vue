<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="产品名称">
          <el-input v-model="query.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
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
        <span>库存预警产品</span>
        <el-tag type="danger" size="small">共 {{ list.length }} 件产品需要补货</el-tag>
      </div>
      <el-table :data="list" v-loading="loading" stripe row-class-name="warning-row">
        <el-table-column prop="productName" label="产品名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column prop="specification" label="规格" min-width="100" />
        <el-table-column prop="unitName" label="单位" min-width="60" />
        <el-table-column prop="currentStock" label="当前库存" min-width="100">
          <template #default="{ row }">
            <span class="danger-text">{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" min-width="100" />
        <el-table-column label="缺口数量" min-width="100">
          <template #default="{ row }">
            <span class="danger-text">{{ row.safetyStock - row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="$router.push('/purchase-orders/add')">去进货</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && list.length === 0" description="暂无预警产品，库存状况良好" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getWarningList } from '@/api/inventory'
import { getCategoryList } from '@/api/category'

const loading = ref(false)
const list = ref([])
const categories = ref([])
const query = reactive({ productName: '', categoryId: undefined })

async function loadCategories() {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) { /* handled */ }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getWarningList(query.categoryId)
    let data = res.data || []
    // 前端过滤产品名称
    if (query.productName) {
      data = data.filter(item =>
        item.productName && item.productName.includes(query.productName)
      )
    }
    list.value = data
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { loadData() }
function resetQuery() { query.productName = ''; query.categoryId = undefined; loadData() }

onMounted(() => { loadCategories(); loadData() })
</script>

<style scoped>
/* 样式已由全局样式统一处理 */
</style>
