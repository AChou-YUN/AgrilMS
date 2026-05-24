import request from './request'

/**
 * 获取所有分类列表
 */
export function getCategoryList() {
  return request.get('/categories')
}

/**
 * 获取分类详情
 */
export function getCategoryDetail(id) {
  return request.get(`/categories/${id}`)
}

/**
 * 新增分类
 */
export function createCategory(data) {
  return request.post('/categories', data)
}

/**
 * 编辑分类
 */
export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}