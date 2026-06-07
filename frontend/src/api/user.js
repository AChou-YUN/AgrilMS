import request from './request'

/**
 * 获取所有角色列表
 */
export function getAllRoles() {
  return request.get('/users/roles')
}

/**
 * 分页查询用户列表
 */
export function getUserList(params) {
  return request.get('/users', { params })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id) {
  return request.get(`/users/${id}`)
}

/**
 * 新增用户
 */
export function createUser(data) {
  return request.post('/users', data)
}

/**
 * 编辑用户
 */
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request.delete(`/users/${id}`)
}

/**
 * 启用/禁用用户
 */
export function updateUserStatus(id, status) {
  return request.put(`/users/${id}/status`, { status })
}

/**
 * 分配角色
 */
export function assignRoles(id, roleIds) {
  return request.put(`/users/${id}/roles`, { roleIds })
}