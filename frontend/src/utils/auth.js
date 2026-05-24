const TOKEN_KEY = 'agrilms_token'
const USER_KEY = 'agrilms_user'

/**
 * 获取 Token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置 Token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除 Token
 */
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

/**
 * 获取缓存的用户信息
 */
export function getUserInfo() {
  const data = localStorage.getItem(USER_KEY)
  return data ? JSON.parse(data) : null
}

/**
 * 设置用户信息
 */
export function setUserInfo(info) {
  localStorage.setItem(USER_KEY, JSON.stringify(info))
}