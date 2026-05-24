import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { getToken, setToken, removeToken, setUserInfo, getUserInfo } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userId = ref(null)
  const username = ref('')
  const realName = ref('')
  const roles = ref([])

  /**
   * 登录
   */
  async function login(loginForm) {
    const res = await loginApi(loginForm)
    const data = res.data
    token.value = data.token
    userId.value = data.userId
    username.value = data.username
    realName.value = data.realName
    roles.value = data.roles || []
    setToken(data.token)
    setUserInfo({
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      roles: data.roles
    })
    return data
  }

  /**
   * 获取用户信息
   */
  async function getInfo() {
    const res = await getUserInfoApi()
    const data = res.data
    userId.value = data.id
    username.value = data.username
    realName.value = data.realName
    roles.value = (data.roles || []).map(r => r.roleKey)
    setUserInfo({
      userId: data.id,
      username: data.username,
      realName: data.realName,
      roles: roles.value
    })
    return data
  }

  /**
   * 登出
   */
  function logout() {
    token.value = ''
    userId.value = null
    username.value = ''
    realName.value = ''
    roles.value = []
    removeToken()
  }

  /**
   * 从缓存恢复用户信息
   */
  function restoreFromCache() {
    const cached = getUserInfo()
    if (cached) {
      userId.value = cached.userId
      username.value = cached.username
      realName.value = cached.realName
      roles.value = cached.roles || []
    }
  }

  // 初始化时从缓存恢复
  if (token.value && !username.value) {
    restoreFromCache()
  }

  return {
    token,
    userId,
    username,
    realName,
    roles,
    login,
    getInfo,
    logout,
    restoreFromCache
  }
})