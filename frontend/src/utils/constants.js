/**
 * 系统常量定义
 * 统一管理所有状态枚举、角色映射等常量
 */

// ===== 用户状态 =====
export const USER_STATUS = {
  ENABLED: 1,
  DISABLED: 0
}

export const USER_STATUS_MAP = {
  1: { label: '启用', type: 'success' },
  0: { label: '禁用', type: 'danger' }
}

// ===== 产品状态 =====
export const PRODUCT_STATUS = {
  ON: 1,
  OFF: 0
}

export const PRODUCT_STATUS_MAP = {
  1: { label: '上架', type: 'success' },
  0: { label: '下架', type: 'info' }
}

// ===== 进货单状态 =====
export const PURCHASE_STATUS = {
  PENDING: 0,
  CONFIRMED: 1,
  CANCELLED: 2
}

export const PURCHASE_STATUS_MAP = {
  0: { label: '待确认', type: 'warning' },
  1: { label: '已入库', type: 'success' },
  2: { label: '已取消', type: 'danger' }
}

// ===== 销售单状态 =====
export const SALES_STATUS = {
  COMPLETED: 1,
  RETURNED: 2,
  PARTIAL_RETURN: 3
}

export const SALES_STATUS_MAP = {
  1: { label: '已完成', type: 'success' },
  2: { label: '已退货', type: 'danger' },
  3: { label: '部分退货', type: 'warning' }
}

// ===== 付款方式 =====
export const PAYMENT_METHOD = {
  CASH: 1,
  WECHAT: 2,
  ALIPAY: 3,
  CREDIT: 4
}

export const PAYMENT_METHOD_MAP = {
  1: { label: '现金', type: '' },
  2: { label: '微信', type: 'success' },
  3: { label: '支付宝', type: 'primary' },
  4: { label: '赊账', type: 'warning' }
}

// ===== 库存变动类型 =====
export const INVENTORY_CHANGE_TYPE = {
  PURCHASE: 'PURCHASE',
  SALE: 'SALE',
  ADJUST: 'ADJUST',
  DAMAGE: 'DAMAGE',
  RETURN: 'RETURN'
}

export const INVENTORY_CHANGE_TYPE_MAP = {
  PURCHASE: { label: '进货入库', type: 'success' },
  SALE: { label: '销售出库', type: 'danger' },
  ADJUST: { label: '盘点调整', type: 'warning' },
  DAMAGE: { label: '报损', type: 'danger' },
  RETURN: { label: '退货入库', type: 'info' }
}

// ===== 用户角色 =====
export const ROLES = {
  ADMIN: 'ADMIN',
  DEALER: 'DEALER',
  RETAILER: 'RETAILER',
  WAREHOUSE: 'WAREHOUSE'
}

export const ROLE_MAP = {
  ADMIN: { label: '管理员', color: '#f56c6c' },
  DEALER: { label: '经销商', color: '#409eff' },
  RETAILER: { label: '零售商', color: '#67c23a' },
  WAREHOUSE: { label: '仓库管理员', color: '#e6a23c' }
}

/**
 * 获取状态标签配置
 * @param {Object} statusMap - 状态映射表
 * @param {number|string} status - 状态值
 * @returns {{ label: string, type: string }}
 */
export function getStatusConfig(statusMap, status) {
  return statusMap[status] || { label: '未知', type: 'info' }
}

/**
 * 数字格式化（保留两位小数）
 */
export function formatMoney(num) {
  if (num == null || isNaN(num)) return '0.00'
  return Number(num).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

/**
 * 日期格式化
 */
export function formatDate(date, pattern = 'YYYY-MM-DD') {
  if (!date) return ''
  // 如果是 dayjs 对象
  if (date.format) return date.format(pattern)
  // 如果是 Date 对象
  const d = new Date(date)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  if (pattern === 'YYYY-MM-DD') return `${y}-${m}-${day}`
  return `${y}-${m}-${day}`
}