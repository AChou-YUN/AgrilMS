<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="进货单号">
          <el-input v-model="query.orderNo" placeholder="请输入单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="待确认" :value="0" />
            <el-option label="已入库" :value="1" />
            <el-option label="已取消" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" @change="onDateChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-card">
      <div class="table-toolbar">
        <span>进货单列表</span>
        <el-button type="primary" @click="$router.push('/purchase-orders/add')">新增进货单</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="进货单号" min-width="140" />
        <el-table-column prop="supplierName" label="供应商" min-width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ Number(row.totalAmount).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="进货日期" width="110" />
        <el-table-column prop="operatorName" label="操作人" width="90" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="$router.push('/purchase-orders/' + row.id)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" text size="small" @click="handleConfirm(row.id)">确认入库</el-button>
            <el-button v-if="row.status === 0" type="warning" text size="small" @click="handleCancel(row.id)">取消</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPurchaseOrderList, confirmPurchaseOrder, cancelPurchaseOrder } from '@/api/purchase'

const loading = ref(false); const list = ref([]); const total = ref(0); const dateRange = ref(null)
const query = reactive({ orderNo: '', status: undefined, startDate: '', endDate: '', pageNum: 1, pageSize: 10 })

function onDateChange(val) { query.startDate = val ? val[0] : ''; query.endDate = val ? val[1] : '' }

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (params.status === undefined || params.status === '') delete params.status
    const res = await getPurchaseOrderList(params)
    list.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { orderNo: '', status: undefined, startDate: '', endDate: '', pageNum: 1 }); dateRange.value = null; loadData() }

async function handleConfirm(id) {
  try { await ElMessageBox.confirm('确认入库后将增加库存，确定继续？', '确认入库', { type: 'warning' }) } catch { return }
  try { await confirmPurchaseOrder(id); ElMessage.success('确认入库成功'); loadData() } catch (e) { /* handled */ }
}

async function handleCancel(id) {
  try { await ElMessageBox.confirm('确定取消此进货单？', '取消进货单', { type: 'warning' }) } catch { return }
  try { await cancelPurchaseOrder(id); ElMessage.success('已取消'); loadData() } catch (e) { /* handled */ }
}

onMounted(loadData)
</script>