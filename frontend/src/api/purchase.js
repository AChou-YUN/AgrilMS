import request from './request'

/**
 * 分页查询进货单列表
 */
export function getPurchaseOrderList(params) {
  return request.get('/purchase-orders', { params })
}

/**
 * 获取进货单详情
 */
export function getPurchaseOrderDetail(id) {
  return request.get(`/purchase-orders/${id}`)
}

/**
 * 创建进货单
 */
export function createPurchaseOrder(data) {
  return request.post('/purchase-orders', data)
}

/**
 * 确认入库
 */
export function confirmPurchaseOrder(id) {
  return request.put(`/purchase-orders/${id}/confirm`)
}

/**
 * 取消进货单
 */
export function cancelPurchaseOrder(id) {
  return request.put(`/purchase-orders/${id}/cancel`)
}

/**
 * 进货统计
 */
export function getPurchaseStats(params) {
  return request.get('/purchase-orders/stats', { params })
}