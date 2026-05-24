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
        <el-form-item label="仅显示预警">
          <el-switch v-model="query.warningOnly" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-card">
      <div class="table-toolbar">
        <span>库存列表</span>
      </div>
      <el-table :data="list" v-loading="loading" stripe :row-class-name="tableRowClassName">
        <el-table-column prop="productName" label="产品名称" min-width="120" />
        <el-table-column prop="categoryName" label="分类" min-width="100" />
        <el-table-column prop="specification" label="规格" min-width="100" />
        <el-table-column prop="unitName" label="单位" min-width="70" />
        <el-table-column prop="currentStock" label="当前库存" min-width="90">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.isWarning }">{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" min-width="90" />
        <el-table-column label="库存价值" min-width="100">
          <template #default="{ row }">¥{{ Number(row.stockValue || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.isWarning ? 'warning' : 'success'" size="small">{{ row.isWarning ? '预警' : '正常' }}</el-tag>
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
import { getInventoryList } from '@/api/inventory'
import { getCategoryList } from '@/api/category'

const loading = ref(false); const list = ref([]); const total = ref(0); const categories = ref([])
const query = reactive({ productName: '', categoryId: undefined, warningOnly: 0, pageNum: 1, pageSize: 10 })

function tableRowClassName({ row }) { return row.isWarning ? 'warning-row' : '' }

async function loadCategories() {
  try { const res = await getCategoryList(); categories.value = res.data || [] } catch (e) { /* handled */ }
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.categoryId) delete params.categoryId
    const res = await getInventoryList(params)
    list.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { productName: '', categoryId: undefined, warningOnly: 0, pageNum: 1 }); loadData() }

onMounted(() => { loadCategories(); loadData() })
</script>

<style scoped>
/* 样式已由全局样式统一处理 */
</style>
