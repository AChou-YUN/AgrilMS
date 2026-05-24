import request from './request'

/**
 * 分页查询产品列表
 */
export function getProductList(params) {
  return request.get('/products', { params })
}

/**
 * 获取产品详情
 */
export function getProductDetail(id) {
  return request.get(`/products/${id}`)
}

/**
 * 按分类查询产品
 */
export function getProductsByCategory(categoryId) {
  return request.get(`/products/category/${categoryId}`)
}

/**
 * 新增产品
 */
export function createProduct(data) {
  return request.post('/products', data)
}

/**
 * 编辑产品
 */
export function updateProduct(id, data) {
  return request.put(`/products/${id}`, data)
}

/**
 * 删除产品
 */
export function deleteProduct(id) {
  return request.delete(`/products/${id}`)
}

/**
 * 产品上下架
 */
export function updateProductStatus(id, status) {
  return request.put(`/products/${id}/status`, { status })
}

/**
 * 更新安全库存
 */
export function updateSafetyStock(id, safetyStock) {
  return request.put(`/products/${id}/safety-stock`, { safetyStock })
}