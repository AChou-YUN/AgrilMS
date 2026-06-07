import request from './request'

/**
 * 销售报表
 */
export function getSalesReport(params) {
  return request.get('/reports/sales', { params })
}

/**
 * 进货报表
 */
export function getPurchaseReport(params) {
  return request.get('/reports/purchase', { params })
}

/**
 * 库存报表
 */
export function getInventoryReport() {
  return request.get('/reports/inventory')
}

/**
 * 利润报表
 */
export function getProfitReport(params) {
  return request.get('/reports/profit', { params })
}

/**
 * 销售概况（今日/本月/本年汇总）
 */
export function getSalesSummary() {
  return request.get('/sales-stats/summary')
}

/**
 * 每日销售统计
 */
export function getDailySalesStats(params) {
  return request.get('/sales-stats/daily', { params })
}

/**
 * 月度销售统计
 */
export function getMonthlySalesStats(params) {
  return request.get('/sales-stats/monthly', { params })
}

/**
 * 年度销售统计
 */
export function getYearlySalesStats() {
  return request.get('/sales-stats/yearly')
}

/**
 * 产品销售排行
 */
export function getProductRanking(params) {
  return request.get('/sales-stats/ranking', { params })
}