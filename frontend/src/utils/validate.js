/**
 * 通用表单校验规则
 */

// 必填项
export const required = (message = '此项为必填项') => ({
  required: true,
  message,
  trigger: 'blur'
})

// 必填下拉选择
export const requiredSelect = (message = '请选择') => ({
  required: true,
  message,
  trigger: 'change'
})

// 用户名：4-20位字母数字下划线
export const usernameRule = {
  pattern: /^[a-zA-Z0-9_]{4,20}$/,
  message: '用户名需为4-20位字母、数字或下划线',
  trigger: 'blur'
}

// 手机号
export const phoneRule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入正确的手机号码',
  trigger: 'blur'
}

// 手机号（非必填，有值时校验）
export const phoneOptional = {
  validator: (rule, value, callback) => {
    if (!value || /^1[3-9]\d{9}$/.test(value)) {
      callback()
    } else {
      callback(new Error('请输入正确的手机号码'))
    }
  },
  trigger: 'blur'
}

// 密码：6-20位
export const passwordRule = {
  min: 6,
  max: 20,
  message: '密码长度需在6-20个字符之间',
  trigger: 'blur'
}

// 数字校验（正整数）
export const positiveIntRule = {
  pattern: /^[1-9]\d*$/,
  message: '请输入正整数',
  trigger: 'blur'
}

// 非负数（含小数）
export const nonNegativeRule = {
  pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
  message: '请输入非负数（最多两位小数）',
  trigger: 'blur'
}