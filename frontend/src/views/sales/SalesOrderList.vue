<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="销售单号">
          <el-input v-model="query.orderNo" placeholder="请输入单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="已完成" :value="1" />
            <el-option label="已退货" :value="2" />
            <el-option label="部分退货" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="付款方式">
          <el-select v-model="query.paymentMethod" placeholder="全部" clearable>
            <el-option label="现金" :value="1" />
            <el-option label="微信" :value="2" />
            <el-option label="支付宝" :value="3" />
            <el-option label="赊账" :value="4" />
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
        <span>销售单列表</span>
        <el-button type="primary" @click="$router.push('/sales-orders/add')">销售开单</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="销售单号" min-width="140" />
        <el-table-column prop="customerName" label="客户" min-width="100" />
        <el-table-column prop="totalAmount" label="总金额" min-width="100">
          <template #default="{ row }">¥{{ Number(row.totalAmount).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="paymentMethodText" label="付款方式" min-width="90" />
        <el-table-column label="状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderDate" label="销售日期" min-width="110" />
        <el-table-column prop="operatorName" label="操作人" min-width="90" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="$router.push('/sales-orders/' + row.id)">详情</el-button>
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
import { getSalesOrderList } from '@/api/sales'

const loading = ref(false); const list = ref([]); const total = ref(0); const dateRange = ref(null)
const query = reactive({ orderNo: '', status: undefined, paymentMethod: undefined, startDate: '', endDate: '', pageNum: 1, pageSize: 10 })

function onDateChange(val) { query.startDate = val ? val[0] : ''; query.endDate = val ? val[1] : '' }

async function loadData() {
  loading.value = true
  try {
    const params = { ...query }
    if (params.status === undefined || params.status === '') delete params.status
    if (params.paymentMethod === undefined || params.paymentMethod === '') delete params.paymentMethod
    const res = await getSalesOrderList(params)
    list.value = res.data.list || res.data.records || []
    total.value = res.data.total || 0
  } catch (e) { /* handled */ }
  loading.value = false
}

function handleSearch() { query.pageNum = 1; loadData() }
function resetQuery() { Object.assign(query, { orderNo: '', status: undefined, paymentMethod: undefined, startDate: '', endDate: '', pageNum: 1 }); dateRange.value = null; loadData() }

onMounted(loadData)
</script>