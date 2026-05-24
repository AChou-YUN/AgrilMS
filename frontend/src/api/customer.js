import request from './request'

/**
 * 分页查询客户列表
 */
export function getCustomerList(params) {
  return request.get('/customers', { params })
}

/**
 * 获取客户详情
 */
export function getCustomerDetail(id) {
  return request.get(`/customers/${id}`)
}

/**
 * 获取所有客户（不分页，用于下拉选择）
 */
export function getAllCustomers() {
  return request.get('/customers/all')
}

/**
 * 获取客户订单列表
 */
export function getCustomerOrders(id, params) {
  return request.get(`/customers/${id}/orders`, { params })
}

/**
 * 新增客户
 */
export function createCustomer(data) {
  return request.post('/customers', data)
}

/**
 * 编辑客户
 */
export function updateCustomer(id, data) {
  return request.put(`/customers/${id}`, data)
}

/**
 * 删除客户
 */
export function deleteCustomer(id) {
  return request.delete(`/customers/${id}`)
}