import request from './request'

/**
 * 分页查询供应商列表
 */
export function getSupplierList(params) {
  return request.get('/suppliers', { params })
}

/**
 * 获取供应商详情
 */
export function getSupplierDetail(id) {
  return request.get(`/suppliers/${id}`)
}

/**
 * 获取所有启用供应商（不分页，用于下拉选择）
 */
export function getAllActiveSuppliers() {
  return request.get('/suppliers/all')
}

/**
 * 新增供应商
 */
export function createSupplier(data) {
  return request.post('/suppliers', data)
}

/**
 * 编辑供应商
 */
export function updateSupplier(id, data) {
  return request.put(`/suppliers/${id}`, data)
}

/**
 * 删除供应商
 */
export function deleteSupplier(id) {
  return request.delete(`/suppliers/${id}`)
}

/**
 * 供应商状态变更（启用/禁用）
 */
export function updateSupplierStatus(id, status) {
  return request.put(`/suppliers/${id}/status`, { status })
}