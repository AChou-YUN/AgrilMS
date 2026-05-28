<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="产品名称">
          <el-input v-model="query.productName" placeholder="请输入产品名称" clearable />
        </el-form-item>
        <el-form-item label="变动类型">
          <el-select v-model="query.changeType" placeholder="全部" clearable>
            <el-option label="进货入库" value="PURCHASE" />
            <el-option label="销售出库" value="SALE" />
            <el-option label="盘点调整" value="ADJUST" />
            <el-option label="报损" value="DAMAGE" />
            <el-option label="退货入库" value="RETURN" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="onDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-card">
      <div class="table-toolbar">
        <span>库存变动日志</span>
      </div>
      <el-table :data="list" v-loading="loading">
        <el-table-column prop="productName" label="产品名称" min-width="140" />
        <el-table-column label="变动类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="changeTypeTag(row.changeType)" size="small">{{ changeTypeText(row.changeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动数量" min-width="100">
          <template #default="{ row }">
            <span :class="row.changeQuantity > 0 ? 'text-success' : 'text-danger'">
              {{ row.changeQuantity > 0 ? '+' : '' }}{{ row.changeQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeStock" label="变动前库存" min-width="100" />
        <el-table-column prop="afterStock" label="变动后库存" min-width="100" />
        <el-table-column prop="relatedOrderNo" label="关联单号" min-width="140" />
        <el-table-column prop="operatorName" label="操作人" min-width="90" />
        <el-table-column prop="createTime" label="时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getInventoryLogs } from '@/api/inventory'
import dayjs from 'dayjs'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const dateRange = ref(null)
const query = reactive({
  productName: '',
  changeType: '',
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm:ss') : ''
}

function onDateChange(val) {
  query.startDate = val ? val[0] : ''
  query.endDate = val ? val[1] : ''
}

function changeTypeText(type) {
  const map = {
    PURCHASE: '进货入库',
    SALE: '销售出库',
    ADJUST: '盘点调整',
    DAMAGE: '报损',
    RETURN: '退货入库'
  }
  return map[type] || type
}

function changeTypeTag(type) {
  const map = {
    PURCHASE: 'success',
    SALE: 'danger',
    ADJUST: 'warning',
    DAMAGE: 'danger',
    RETURN: 'info'
  }
  return map[type] || ''
}

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (!params.changeType) delete params.changeType
    if (!params.productName) delete params.productName
    const res = await getInventoryLogs(params)
    list.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() {
  Object.assign(query, { productName: '', changeType: '', startDate: '', endDate: '', pageNum: 1 })
  dateRange.value = null
  loadData()
}

onMounted(loadData)
</script>