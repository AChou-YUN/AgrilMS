<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="统计维度">
          <el-radio-group v-model="query.dimension" @change="onDimensionChange">
            <el-radio-button value="monthly">按月</el-radio-button>
            <el-radio-button value="yearly">按年</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="query.dimension === 'monthly'" label="年份">
          <el-date-picker v-model="year" type="year" value-format="YYYY" placeholder="选择年份" @change="onYearChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 汇总卡片 -->
    <el-row :gutter="16" style="margin-bottom:16px;">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">总销售额</div>
          <div class="stat-value sales-text">¥{{ formatNumber(totalSales) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">总进货成本</div>
          <div class="stat-value purchase-text">¥{{ formatNumber(totalPurchase) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">毛利润</div>
          <div class="stat-value" :class="totalProfit >= 0 ? 'profit-text' : 'loss-text'">¥{{ formatNumber(totalProfit) }}</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">利润率</div>
          <div class="stat-value" :class="profitRate >= 0 ? 'profit-text' : 'loss-text'">{{ profitRate.toFixed(1) }}%</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 利润趋势图 -->
    <el-card shadow="hover" style="margin-bottom:16px;">
      <template #header><span>利润趋势</span></template>
      <div ref="chartRef" style="width:100%;height:350px;"></div>
    </el-card>

    <!-- 数据明细 -->
    <div class="table-card">
      <div class="table-toolbar">
        <span>利润数据明细</span>
      </div>
      <el-table :data="dataList" v-loading="loading" stripe>
        <el-table-column prop="period" label="时间" min-width="120" />
        <el-table-column prop="salesAmount" label="销售额" width="120">
          <template #default="{ row }">¥{{ Number(row.salesAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="purchaseAmount" label="进货成本" width="120">
          <template #default="{ row }">¥{{ Number(row.purchaseAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="毛利润" width="120">
          <template #default="{ row }">
            <span :class="(row.salesAmount - row.purchaseAmount) >= 0 ? 'text-success' : 'text-danger'">
              ¥{{ Number((row.salesAmount || 0) - (row.purchaseAmount || 0)).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="利润率" width="100">
          <template #default="{ row }">
            {{ row.salesAmount > 0 ? (((row.salesAmount - row.purchaseAmount) / row.salesAmount) * 100).toFixed(1) : '0.0' }}%
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getProfitReport } from '@/api/report'
import dayjs from 'dayjs'

const loading = ref(false)
const dataList = ref([])
const chartRef = ref(null)
let chart = null

const year = ref(dayjs().format('YYYY'))
const query = ref({ dimension: 'monthly', year: dayjs().year() })

const totalSales = computed(() => dataList.value.reduce((s, d) => s + (d.salesAmount || 0), 0))
const totalPurchase = computed(() => dataList.value.reduce((s, d) => s + (d.purchaseAmount || 0), 0))
const totalProfit = computed(() => totalSales.value - totalPurchase.value)
const profitRate = computed(() => totalSales.value > 0 ? (totalProfit.value / totalSales.value) * 100 : 0)

function formatNumber(num) {
  if (num == null) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function onDimensionChange(val) {
  dataList.value = []
  if (val === 'monthly') {
    query.value.year = parseInt(year.value)
  }
  loadData()
}

function onYearChange(val) {
  if (val) {
    query.value.year = parseInt(val)
    loadData()
  }
}

async function loadData() {
  loading.value = true
  try {
    const params = {}
    if (query.value.dimension === 'monthly') {
      params.year = query.value.year
    }
    const res = await getProfitReport(params)
    dataList.value = res.data || []
    renderChart()
  } catch (e) { /* handled */ }
  loading.value = false
}

function renderChart() {
  if (!chart || dataList.value.length === 0) {
    if (chart) chart.clear()
    return
  }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '进货成本', '毛利润'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dataList.value.map(d => d.period),
      axisLabel: { rotate: 45, fontSize: 11 }
    },
    yAxis: { type: 'value', name: '金额(元)' },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: dataList.value.map(d => d.salesAmount || 0),
        itemStyle: { color: '#8B9D83' },
        barMaxWidth: 30
      },
      {
        name: '进货成本',
        type: 'bar',
        data: dataList.value.map(d => d.purchaseAmount || 0),
        itemStyle: { color: '#C08E3A' },
        barMaxWidth: 30
      },
      {
        name: '毛利润',
        type: 'line',
        data: dataList.value.map(d => (d.salesAmount || 0) - (d.purchaseAmount || 0)),
        smooth: true,
        lineStyle: { color: '#606C38', width: 3 },
        itemStyle: { color: '#606C38' }
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
.stat-card { text-align: center; }
.stat-label { font-size: var(--text-sm); color: var(--text-muted); margin-bottom: 8px; }
.stat-value { font-size: var(--text-2xl); font-weight: var(--weight-bold); font-family: var(--font-display); font-variant-numeric: tabular-nums; }
.sales-text { color: var(--color-sage); }
.purchase-text { color: var(--color-ochre); }
.profit-text { color: var(--color-moss); }
.loss-text { color: var(--color-danger); }
</style>