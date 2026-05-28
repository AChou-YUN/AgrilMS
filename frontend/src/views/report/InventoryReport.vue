<template>
  <div class="page-container">
    <!-- 汇总卡片 -->
    <div class="stat-cards-row">
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">总产品数</div>
        <div class="stat-value">{{ summary.totalProducts || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">总库存量</div>
        <div class="stat-value">{{ summary.totalStock || 0 }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">库存总价值</div>
        <div class="stat-value">¥{{ formatNumber(summary.totalValue) }}</div>
      </el-card>
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">预警产品数</div>
        <div class="stat-value warning-text">{{ summary.warningCount || 0 }}</div>
      </el-card>
    </div>

    <!-- 库存分布饼图 -->
    <el-card shadow="never" style="margin-bottom:0;">
      <template #header><span>各分类库存占比</span></template>
      <div ref="pieChartRef" style="width:100%;height:350px;"></div>
    </el-card>

    <!-- 库存明细表 -->
    <div class="table-card">
      <div class="table-toolbar">
        <span>库存明细</span>
      </div>
      <el-table :data="inventoryList" v-loading="loading" :row-class-name="tableRowClassName">
        <el-table-column prop="productName" label="产品名称" min-width="140" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="currentStock" label="当前库存" width="100">
          <template #default="{ row }">
            <span :class="{ 'warning-text': row.isWarning }">{{ row.currentStock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column label="库存价值" width="120">
          <template #default="{ row }">¥{{ Number(row.stockValue || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isWarning ? 'warning' : 'success'" size="small">{{ row.isWarning ? '预警' : '正常' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getInventoryReport } from '@/api/report'
import { getInventoryList } from '@/api/inventory'

const loading = ref(false)
const summary = ref({})
const inventoryList = ref([])
const pieChartRef = ref(null)
let pieChart = null

function formatNumber(num) {
  if (num == null) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function tableRowClassName({ row }) {
  return row.isWarning ? 'warning-row' : ''
}

async function loadSummary() {
  try {
    const res = await getInventoryReport()
    summary.value = res.data || {}
  } catch (e) { /* handled */ }
}

async function loadInventory() {
  loading.value = true
  try {
    const res = await getInventoryList({ pageNum: 1, pageSize: 999 })
    inventoryList.value = res.data?.list || res.data?.records || []
    renderPieChart()
  } catch (e) { /* handled */ }
  loading.value = false
}

function renderPieChart() {
  if (!pieChart || inventoryList.value.length === 0) return
  // 按分类汇总库存价值
  const categoryMap = {}
  inventoryList.value.forEach(item => {
    const name = item.categoryName || '未分类'
    if (!categoryMap[name]) categoryMap[name] = 0
    categoryMap[name] += Number(item.stockValue || 0)
  })
  const pieData = Object.entries(categoryMap).map(([name, value]) => ({ name, value: Math.round(value * 100) / 100 }))

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
      data: pieData
    }]
  })
}

function handleResize() { pieChart?.resize() }

onMounted(() => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
  }
  window.addEventListener('resize', handleResize)
  loadSummary()
  loadInventory()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
})
</script>

<style scoped>
.stat-cards-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-5);
}
@media (max-width: 768px) {
  .stat-cards-row { grid-template-columns: repeat(2, 1fr); }
}
.stat-card { text-align: center; }
.stat-label { font-size: var(--text-sm); color: var(--text-muted); margin-bottom: 8px; }
.stat-value { font-size: var(--text-2xl); font-weight: var(--weight-bold); color: var(--text-primary); font-variant-numeric: tabular-nums; }
.warning-text { color: var(--color-warning) !important; font-weight: var(--weight-semibold); }
</style>
