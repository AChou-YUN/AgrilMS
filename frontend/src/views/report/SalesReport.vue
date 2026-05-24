<template>
  <div class="page-container">
    <div class="search-bar">
      <el-form :inline="true" :model="query">
        <el-form-item label="统计维度">
          <el-radio-group v-model="query.dimension" @change="onDimensionChange">
            <el-radio-button value="daily">按日</el-radio-button>
            <el-radio-button value="monthly">按月</el-radio-button>
            <el-radio-button value="yearly">按年</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="query.dimension === 'daily'" label="年月">
          <el-date-picker v-model="yearMonth" type="month" value-format="YYYY-MM" placeholder="选择年月" @change="onMonthChange" />
        </el-form-item>
        <el-form-item v-if="query.dimension === 'monthly'" label="年份">
          <el-date-picker v-model="year" type="year" value-format="YYYY" placeholder="选择年份" @change="onYearChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-row :gutter="16" style="margin-bottom:16px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header><span>销售趋势</span></template>
          <div ref="chartRef" style="width:100%;height:350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <div class="table-card">
      <div class="table-toolbar">
        <span>销售数据明细</span>
      </div>
      <el-table :data="dataList" v-loading="loading" stripe>
        <el-table-column prop="date" label="时间" min-width="120" />
        <el-table-column prop="count" label="订单数" width="100" />
        <el-table-column prop="amount" label="销售额" width="120">
          <template #default="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getDailySalesStats, getMonthlySalesStats, getYearlySalesStats } from '@/api/report'
import dayjs from 'dayjs'

const loading = ref(false)
const dataList = ref([])
const chartRef = ref(null)
let chart = null

const now = dayjs()
const yearMonth = ref(now.format('YYYY-MM'))
const year = ref(now.format('YYYY'))
const query = ref({
  dimension: 'monthly',
  year: now.year(),
  month: now.month() + 1
})

function onDimensionChange(val) {
  dataList.value = []
  if (val === 'daily') {
    const [y, m] = yearMonth.value.split('-')
    query.value.year = parseInt(y)
    query.value.month = parseInt(m)
  } else if (val === 'monthly') {
    query.value.year = parseInt(year.value)
  }
  loadData()
}

function onMonthChange(val) {
  if (val) {
    const [y, m] = val.split('-')
    query.value.year = parseInt(y)
    query.value.month = parseInt(m)
    loadData()
  }
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
    let res
    if (query.value.dimension === 'daily') {
      res = await getDailySalesStats({ year: query.value.year, month: query.value.month })
    } else if (query.value.dimension === 'monthly') {
      res = await getMonthlySalesStats({ year: query.value.year })
    } else {
      res = await getYearlySalesStats()
    }
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
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: dataList.value.map(d => d.date),
      axisLabel: { rotate: 45, fontSize: 11 }
    },
    yAxis: [
      { type: 'value', name: '销售额(元)', position: 'left' },
      { type: 'value', name: '订单数', position: 'right' }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: dataList.value.map(d => d.amount || 0),
        itemStyle: { color: '#8B9D83' },
        barMaxWidth: 40
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        data: dataList.value.map(d => d.count || 0),
        smooth: true,
        lineStyle: { color: '#C08E3A' },
        itemStyle: { color: '#C08E3A' }
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