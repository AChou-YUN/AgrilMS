import request from './request'

/**
 * 获取所有单位列表
 */
export function getUnitList() {
  return request.get('/units')
}

/**
 * 新增单位
 */
export function createUnit(data) {
  return request.post('/units', data)
}

/**
 * 删除单位
 */
export function deleteUnit(id) {
  return request.delete(`/units/${id}`)
}