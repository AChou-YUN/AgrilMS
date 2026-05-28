<template>
  <div class="dashboard-container">
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-icon icon-primary"><el-icon :size="22"><Money /></el-icon></div>
        <div class="stat-info">
          <div class="stat-label">今日销售额</div>
          <div class="stat-value">¥{{ formatNumber(overview.todaySalesAmount) }}</div>
          <div class="stat-count">{{ overview.todaySalesCount }} 笔订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon icon-success"><el-icon :size="22"><TrendCharts /></el-icon></div>
        <div class="stat-info">
          <div class="stat-label">本月销售额</div>
          <div class="stat-value">¥{{ formatNumber(overview.monthSalesAmount) }}</div>
          <div class="stat-count">{{ overview.monthSalesCount }} 笔订单</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon icon-warning"><el-icon :size="22"><WarningFilled /></el-icon></div>
        <div class="stat-info">
          <div class="stat-label">库存预警</div>
          <div class="stat-value warning-text">{{ overview.warningCount }}</div>
          <div class="stat-count">件产品低于安全库存</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon icon-info"><el-icon :size="22"><User /></el-icon></div>
        <div class="stat-info">
          <div class="stat-label">本月新增客户</div>
          <div class="stat-value">{{ overview.monthNewCustomers }}</div>
          <div class="stat-count">本月订单 {{ overview.monthOrderCount }} 笔</div>
        </div>
      </div>
    </div>

    <div class="chart-row">
      <el-card shadow="never">
        <template #header><span class="card-title">近30天销售趋势</span></template>
        <div ref="trendChartRef" class="chart-container"></div>
      </el-card>
      <el-card shadow="never">
        <template #header><span class="card-title">本月分类销售占比</span></template>
        <div ref="pieChartRef" class="chart-container"></div>
      </el-card>
    </div>

    <el-card shadow="never" class="warning-card">
      <template #header>
        <div class="card-header-flex">
          <span class="card-title">库存预警产品</span>
          <el-button type="primary" text size="small" @click="$router.push('/inventory/warnings')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="warnings" size="small" max-height="300">
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
  todaySalesAmount: 0, todaySalesCount: 0, monthSalesAmount: 0, monthSalesCount: 0,
  yearSalesAmount: 0, monthPurchaseAmount: 0, monthPurchaseCount: 0,
  totalProducts: 0, warningCount: 0, monthNewCustomers: 0, monthOrderCount: 0
})
const warnings = ref([])
const trendChartRef = ref(null)
const pieChartRef = ref(null)
let trendChart = null, pieChart = null

const chartColors = ['#4F6EF7', '#34C759', '#F5A623', '#FF3B30', '#8E8E93', '#7B93FA', '#A3B5FB']

function formatNumber(num) {
  if (num == null) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

async function loadOverview() {
  try { const res = await getOverview(); Object.assign(overview, res.data) } catch (e) {}
}
async function loadWarnings() {
  try { const res = await getDashboardWarnings(); warnings.value = res.data || [] } catch (e) {}
}
async function loadSalesTrend() {
  try {
    const res = await getSalesTrend(), data = res.data
    if (trendChart && data) {
      trendChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E4E8ED', textStyle: { color: '#1F2D3D' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: data.dates, axisLabel: { rotate: 45, fontSize: 11, color: '#9CA3B0' }, axisLine: { lineStyle: { color: '#ECEEF3' } }, axisTick: { show: false } },
        yAxis: { type: 'value', name: '销售额(元)', axisLabel: { color: '#9CA3B0' }, splitLine: { lineStyle: { color: '#F0F1F5' } } },
        series: [{
          name: '销售额', type: 'line', smooth: true, data: data.amounts,
          areaStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1,[{offset:0,color:'rgba(79,110,247,0.2)'},{offset:1,color:'rgba(79,110,247,0.01)'}]) },
          lineStyle: { color: '#4F6EF7', width: 2.5 }, itemStyle: { color: '#4F6EF7' }, symbol: 'circle', symbolSize: 6
        }]
      })
    }
  } catch (e) {}
}
async function loadCategorySales() {
  try {
    const res = await getCategorySales(), data = res.data || []
    if (pieChart && data.length > 0) {
      pieChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)', backgroundColor: '#fff', borderColor: '#E4E8ED', textStyle: { color: '#1F2D3D' } },
        legend: { bottom: '0', left: 'center', textStyle: { color: '#6B7080' } },
        series: [{
          type: 'pie', radius: ['40%', '65%'], center: ['50%', '45%'],
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 3 },
          data: data.map((item, i) => ({ name: item.categoryName, value: item.amount, itemStyle: { color: chartColors[i % chartColors.length] } }))
        }]
      })
    }
  } catch (e) {}
}

function handleResize() { trendChart?.resize(); pieChart?.resize() }

onMounted(() => {
  loadOverview(); loadWarnings()
  if (trendChartRef.value) { trendChart = echarts.init(trendChartRef.value); loadSalesTrend() }
  if (pieChartRef.value) { pieChart = echarts.init(pieChartRef.value); loadCategorySales() }
  window.addEventListener('resize', handleResize)
})
onUnmounted(() => { window.removeEventListener('resize', handleResize); trendChart?.dispose(); pieChart?.dispose() })
</script>

<style scoped>
.dashboard-container {
  min-height: 100%;
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-5);
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-6);
  background: var(--surface-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xs);
  border: 1px solid var(--border-light);
  transition: all var(--duration-normal) var(--ease-spring);
  cursor: default;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
  border-color: transparent;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-primary { background: var(--color-primary-soft); color: var(--color-primary); }
.icon-success { background: var(--color-success-soft); color: var(--color-success); }
.icon-warning { background: var(--color-warning-soft); color: var(--color-warning); }
.icon-info { background: var(--color-info-soft); color: var(--color-info); }

.stat-info { flex: 1; min-width: 0; }

.stat-label {
  font-size: var(--text-sm);
  color: var(--text-muted);
  margin-bottom: 6px;
  font-weight: var(--weight-medium);
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--weight-bold);
  color: var(--text-primary);
  line-height: 1.1;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.02em;
}

.stat-count {
  font-size: var(--text-xs);
  color: var(--text-muted);
  margin-top: 6px;
}

.warning-text { color: var(--color-warning); }
.danger-text { color: var(--color-danger); font-weight: var(--weight-semibold); }

.chart-row {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: var(--space-5);
}

@media (max-width: 768px) {
  .chart-row {
    grid-template-columns: 1fr;
  }
}

.card-title {
  font-weight: var(--weight-semibold);
  color: var(--text-primary);
  font-size: var(--text-lg);
  letter-spacing: -0.01em;
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
</style>
