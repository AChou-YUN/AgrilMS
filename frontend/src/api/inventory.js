import request from './request'

/**
 * 分页查询库存列表
 */
export function getInventoryList(params) {
  return request.get('/inventory', { params })
}

/**
 * 获取产品库存详情
 */
export function getProductInventory(productId) {
  return request.get(`/inventory/${productId}`)
}

/**
 * 获取库存预警列表
 */
export function getWarningList(categoryId) {
  return request.get('/inventory/warnings', { params: { categoryId } })
}

/**
 * 获取预警数量
 */
export function getWarningCount() {
  return request.get('/inventory/warnings/count')
}

/**
 * 库存盘点
 */
export function checkInventory(data) {
  return request.post('/inventory/check', data)
}

/**
 * 报损处理
 */
export function processDamage(data) {
  return request.post('/inventory/damage', data)
}

/**
 * 获取库存变动日志
 */
export function getInventoryLogs(params) {
  return request.get('/inventory/logs', { params })
}