import request from './request'

/**
 * 获取概览数据（今日/本月销售、库存预警等）
 */
export function getOverview() {
  return request.get('/dashboard/overview')
}

/**
 * 获取近30天销售趋势
 */
export function getSalesTrend() {
  return request.get('/dashboard/sales-trend')
}

/**
 * 获取本月分类销售占比
 */
export function getCategorySales() {
  return request.get('/dashboard/category-sales')
}

/**
 * 获取库存预警列表（仪表盘用）
 */
export function getDashboardWarnings() {
  return request.get('/dashboard/warnings')
}