import { ref, reactive } from 'vue'

/**
 * 通用分页查询组合式函数
 * @param {Function} apiFn - API 请求函数（接收 params 参数）
 * @param {Object} defaultQuery - 默认查询条件
 */
export function usePagedQuery(apiFn, defaultQuery = {}) {
  const loading = ref(false)
  const list = ref([])
  const total = ref(0)

  const query = reactive({
    pageNum: 1,
    pageSize: 10,
    ...defaultQuery
  })

  async function loadData() {
    loading.value = true
    try {
      const params = { ...query }
      // 清理空值参数
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === undefined || params[key] === null) {
          delete params[key]
        }
      })
      const res = await apiFn(params)
      const data = res.data
      if (Array.isArray(data)) {
        // 非分页接口返回数组
        list.value = data
        total.value = data.length
      } else {
        // 分页接口返回对象
        list.value = data.list || data.records || []
        total.value = data.total || 0
      }
    } catch (e) {
      list.value = []
      total.value = 0
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    query.pageNum = 1
    loadData()
  }

  function resetQuery(extraDefaults = {}) {
    Object.assign(query, { ...defaultQuery, pageNum: 1, pageSize: query.pageSize, ...extraDefaults })
    loadData()
  }

  function handlePageChange(page) {
    query.pageNum = page
    loadData()
  }

  function handleSizeChange(size) {
    query.pageSize = size
    query.pageNum = 1
    loadData()
  }

  return {
    loading,
    list,
    total,
    query,
    loadData,
    handleSearch,
    resetQuery,
    handlePageChange,
    handleSizeChange
  }
}