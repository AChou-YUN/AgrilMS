<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true">
        <el-form-item label="日期范围">
          <el-date-picker v-model="dateRange" type="daterange" value-format="YYYY-MM-DD" start-placeholder="开始日期" end-placeholder="结束日期" @change="onDateChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="never" style="margin-bottom:0;">
      <template #header><span>客户消费排行</span></template>
      <div ref="chartRef" style="width:100%;height:350px;"></div>
    </el-card>

    <div class="table-card">
      <div class="table-toolbar">
        <span>客户消费数据明细</span>
      </div>
      <el-table :data="dataList" v-loading="loading">
        <el-table-column label="排名" width="70">
          <template #default="{ $index }">
            <span :class="{ 'rank-top': $index < 3 }">{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="客户名称" min-width="150" />
        <el-table-column prop="categoryName" label="联系方式" width="120" />
        <el-table-column prop="totalQuantity" label="订单数" width="100" />
        <el-table-column prop="totalAmount" label="消费金额" width="120">
          <template #default="{ row }">¥{{ Number(row.totalAmount || 0).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getCustomerReport } from '@/api/report'
import dayjs from 'dayjs'

const loading = ref(false)
const dataList = ref([])
const chartRef = ref(null)
let chart = null

const now = dayjs()
const startDate = ref(now.startOf('month').format('YYYY-MM-DD'))
const endDate = ref(now.format('YYYY-MM-DD'))
const dateRange = ref([startDate.value, endDate.value])

function onDateChange(val) {
  if (val && val.length === 2) {
    startDate.value = val[0]
    endDate.value = val[1]
  }
}

async function loadData() {
  loading.value = true
  try {
    const res = await getCustomerReport({ startDate: startDate.value, endDate: endDate.value })
    dataList.value = res.data || []
    renderChart()
  } catch (e) { /* handled */ }
  loading.value = false
}

function renderChart() {
  if (!chart) return
  if (dataList.value.length === 0) {
    chart.clear()
    return
  }
  const top10 = dataList.value.slice(0, 10)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: top10.map(d => d.productName || '散客'),
      axisLabel: { rotate: 30, fontSize: 11 }
    },
    yAxis: [
      { type: 'value', name: '消费金额(元)', position: 'left' },
      { type: 'value', name: '订单数', position: 'right' }
    ],
    series: [
      {
        name: '消费金额',
        type: 'bar',
        data: top10.map(d => d.totalAmount || 0),
        itemStyle: { color: '#67C23A' },
        barMaxWidth: 40
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        data: top10.map(d => d.totalQuantity || 0),
        smooth: true,
        lineStyle: { color: '#E6A23C' },
        itemStyle: { color: '#E6A23C' }
      }
    ]
  })
}

function handleResize() { chart?.resize() }

onMounted(() => {
  if (chartRef.value) {
    chart = echarts.init(chartRef.value)
  }
  window.addEventListener('resize', handleResize)
  loadData()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.rank-top { color: #f56c6c; font-weight: bold; font-size: 16px; }
</style>