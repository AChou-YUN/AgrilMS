import request from './request'

/**
 * 分页查询销售单列表
 */
export function getSalesOrderList(params) {
  return request.get('/sales-orders', { params })
}

/**
 * 获取销售单详情
 */
export function getSalesOrderDetail(id) {
  return request.get(`/sales-orders/${id}`)
}

/**
 * 创建销售单
 */
export function createSalesOrder(data) {
  return request.post('/sales-orders', data)
}

/**
 * 退货处理
 */
export function processReturn(orderId, data) {
  return request.put(`/sales-orders/${orderId}/return`, data)
}

/**
 * 获取销售单打印数据
 */
export function getSalesPrintData(id) {
  return request.get(`/sales-orders/${id}/print`)
}