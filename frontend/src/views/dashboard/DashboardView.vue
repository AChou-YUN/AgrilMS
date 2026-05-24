<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-sales">
          <div class="stat-label">今日销售额</div>
          <div class="stat-value">¥{{ formatNumber(overview.todaySalesAmount) }}</div>
          <div class="stat-count">{{ overview.todaySalesCount }} 笔订单</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-month">
          <div class="stat-label">本月销售额</div>
          <div class="stat-value">¥{{ formatNumber(overview.monthSalesAmount) }}</div>
          <div class="stat-count">{{ overview.monthSalesCount }} 笔订单</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-warning">
          <div class="stat-label">库存预警</div>
          <div class="stat-value warning-text">{{ overview.warningCount }}</div>
          <div class="stat-count">件产品低于安全库存</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-customer">
          <div class="stat-label">本月新增客户</div>
          <div class="stat-value">{{ overview.monthNewCustomers }}</div>
          <div class="stat-count">本月订单 {{ overview.monthOrderCount }} 笔</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :sm="14">
        <el-card shadow="hover">
          <template #header>
            <span>近30天销售趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="10">
        <el-card shadow="hover">
          <template #header>
            <span>本月分类销售占比</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警列表 -->
    <el-card shadow="hover" class="warning-card">
      <template #header>
        <div class="card-header-flex">
          <span>库存预警产品</span>
          <el-button type="primary" text size="small" @click="$router.push('/inventory/warnings')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="warnings" stripe size="small" max-height="300">
        <el-table-column prop="productName" label="产品名称" min-width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="currentStock" label="当前库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.isWarning }">{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column label="差额" width="100">
          <template #default="{ row }">
            <span class="danger-text">{{ row.currentStock - row.safetyStock }}</span>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="warnings.length === 0" description="暂无预警产品" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getSalesTrend, getCategorySales, getDashboardWarnings } from '@/api/dashboard'

const overview = reactive({
  todaySalesAmount: 0,
  todaySalesCount: 0,
  monthSalesAmount: 0,
  monthSalesCount: 0,
  yearSalesAmount: 0,
  monthPurchaseAmount: 0,
  monthPurchaseCount: 0,
  totalProducts: 0,
  warningCount: 0,
  monthNewCustomers: 0,
  monthOrderCount: 0
})

const warnings = ref([])
const trendChartRef = ref(null)
const pieChartRef = ref(null)

let trendChart = null
let pieChart = null

function formatNumber(num) {
  if (num == null) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function loadOverview() {
  try {
    const res = await getOverview()
    Object.assign(overview, res.data)
  } catch (e) { /* handled */ }
}

async function loadWarnings() {
  try {
    const res = await getDashboardWarnings()
    warnings.value = res.data || []
  } catch (e) { /* handled */ }
}

async function loadSalesTrend() {
  try {
    const res = await getSalesTrend()
    const data = res.data
    if (trendChart && data) {
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.dates, axisLabel: { rotate: 45, fontSize: 11 } },
        yAxis: { type: 'value', name: '销售额(元)' },
        series: [{
          name: '销售额',
          type: 'line',
          smooth: true,
          data: data.amounts,
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102,126,234,0.3)' },
            { offset: 1, color: 'rgba(102,126,234,0.02)' }
          ])},
          lineStyle: { color: '#667eea', width: 2 },
          itemStyle: { color: '#667eea' }
        }]
      })
    }
  } catch (e) { /* handled */ }
}

async function loadCategorySales() {
  try {
    const res = await getCategorySales()
    const data = res.data || []
    if (pieChart && data.length > 0) {
      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
        legend: { bottom: '0', left: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          label: { show: false },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          data: data.map(item => ({ name: item.categoryName, value: item.amount }))
        }]
      })
    }
  } catch (e) { /* handled */ }
}

function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  loadOverview()
  loadWarnings()

  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    loadSalesTrend()
  }
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    loadCategorySales()
  }

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  pieChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100%;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
  padding: 8px 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-count {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 6px;
}

.stat-sales .stat-value { color: #409eff; }
.stat-month .stat-value { color: #67c23a; }
.stat-warning .stat-value { color: #e6a23c; }
.stat-customer .stat-value { color: #9b59b6; }

.warning-text { color: #e6a23c !important; font-weight: 600; }
.danger-text { color: #f56c6c; font-weight: 600; }

.chart-row {
  margin-bottom: 16px;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-card {
  margin-bottom: 16px;
}
</style>