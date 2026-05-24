<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-sales">
          <div class="stat-icon">
            <el-icon :size="24"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">今日销售额</div>
            <div class="stat-value">¥{{ formatNumber(overview.todaySalesAmount) }}</div>
            <div class="stat-count">{{ overview.todaySalesCount }} 笔订单</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-month">
          <div class="stat-icon">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">本月销售额</div>
            <div class="stat-value">¥{{ formatNumber(overview.monthSalesAmount) }}</div>
            <div class="stat-count">{{ overview.monthSalesCount }} 笔订单</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-warning">
          <div class="stat-icon">
            <el-icon :size="24"><WarningFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">库存预警</div>
            <div class="stat-value">{{ overview.warningCount }}</div>
            <div class="stat-count">件产品低于安全库存</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-customer">
          <div class="stat-icon">
            <el-icon :size="24"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">本月新增客户</div>
            <div class="stat-value">{{ overview.monthNewCustomers }}</div>
            <div class="stat-count">本月订单 {{ overview.monthOrderCount }} 笔</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :sm="14">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">近30天销售趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="10">
        <el-card shadow="never" class="chart-card">
          <template #header>
            <span class="card-title">本月分类销售占比</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 库存预警列表 -->
    <el-card shadow="never" class="warning-card">
      <template #header>
        <div class="card-header-flex">
          <span class="card-title">库存预警产品</span>
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

// 有机色系调色板
const organicPalette = ['#8B9D83', '#B08B6E', '#C66B3D', '#C08E3A', '#606C38', '#a3b39c', '#c4a48a']

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
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255,255,255,0.95)',
          borderColor: '#d4c8b4',
          textStyle: { color: '#3d3225' }
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.dates,
          axisLabel: { rotate: 45, fontSize: 11, color: '#9a8b7a' },
          axisLine: { lineStyle: { color: '#d4c8b4' } }
        },
        yAxis: {
          type: 'value',
          name: '销售额(元)',
          axisLabel: { color: '#9a8b7a' },
          splitLine: { lineStyle: { color: '#ece4d8' } }
        },
        series: [{
          name: '销售额',
          type: 'line',
          smooth: true,
          data: data.amounts,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(139, 157, 131, 0.35)' },
              { offset: 1, color: 'rgba(139, 157, 131, 0.02)' }
            ])
          },
          lineStyle: { color: '#8B9D83', width: 2.5 },
          itemStyle: { color: '#8B9D83' },
          symbol: 'circle',
          symbolSize: 6
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
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)',
          backgroundColor: 'rgba(255,255,255,0.95)',
          borderColor: '#d4c8b4',
          textStyle: { color: '#3d3225' }
        },
        legend: {
          bottom: '0',
          left: 'center',
          textStyle: { color: '#6b5d4f' }
        },
        series: [{
          type: 'pie',
          radius: ['40%', '65%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: false,
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold', color: '#3d3225' }
          },
          itemStyle: {
            borderRadius: 6,
            borderColor: '#fff',
            borderWidth: 2
          },
          data: data.map((item, index) => ({
            name: item.categoryName,
            value: item.amount,
            itemStyle: { color: organicPalette[index % organicPalette.length] }
          }))
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
  margin-bottom: var(--space-4);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #ffffff;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-sales .stat-icon {
  background: linear-gradient(135deg, #8B9D83 0%, #606C38 100%);
  color: #fff;
}

.stat-month .stat-icon {
  background: linear-gradient(135deg, #B08B6E 0%, #967458 100%);
  color: #fff;
}

.stat-warning .stat-icon {
  background: linear-gradient(135deg, #C08E3A 0%, #a07530 100%);
  color: #fff;
}

.stat-customer .stat-icon {
  background: linear-gradient(135deg, #7a8a80 0%, #5a6a60 100%);
  color: #fff;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin-bottom: 4px;
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--weight-bold);
  color: var(--text-primary);
  line-height: 1.2;
  font-family: var(--font-display);
  font-variant-numeric: tabular-nums;
}

.stat-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin-top: 4px;
}

.stat-sales .stat-value { color: var(--color-moss); }
.stat-month .stat-value { color: var(--color-clay); }
.stat-warning .stat-value { color: var(--color-ochre); }
.stat-customer .stat-value { color: var(--color-info); }

.warning-text { color: var(--color-warning); font-weight: var(--weight-semibold); }
.danger-text { color: var(--color-danger); font-weight: var(--weight-semibold); }

.chart-row {
  margin-bottom: var(--space-4);
}

.chart-card {
  border: 1px solid var(--border-light);
}

.card-title {
  font-family: var(--font-display);
  font-weight: var(--weight-semibold);
  color: var(--text-primary);
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
  margin-bottom: var(--space-4);
  border: 1px solid var(--border-light);
}
</style>