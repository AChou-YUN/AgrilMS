import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * 通用 CRUD 操作组合式函数
 * @param {Object} options
 * @param {Function} options.createFn - 新增 API
 * @param {Function} options.updateFn - 编辑 API
 * @param {Function} options.deleteFn - 删除 API
 * @param {Function} [options.onSuccess] - 操作成功回调（如刷新列表）
 * @param {string} [options.entityName] - 实体名称（用于提示信息）
 */
export function useCrud({ createFn, updateFn, deleteFn, onSuccess, entityName = '记录' }) {
  const submitLoading = ref(false)
  const deleteLoading = ref(false)

  /**
   * 提交表单（新增或编辑）
   * @param {Object} data - 表单数据
   * @param {number|string|null} editId - 编辑时传入 ID，新增时传 null
   */
  async function submit(data, editId = null) {
    submitLoading.value = true
    try {
      if (editId != null) {
        await updateFn(editId, data)
        ElMessage.success(`${entityName}编辑成功`)
      } else {
        await createFn(data)
        ElMessage.success(`${entityName}创建成功`)
      }
      onSuccess && onSuccess()
      return true
    } catch (e) {
      return false
    } finally {
      submitLoading.value = false
    }
  }

  /**
   * 删除记录（带确认弹窗）
   * @param {number|string} id - 记录 ID
   * @param {string} [confirmMessage] - 自定义确认提示
   */
  async function handleDelete(id, confirmMessage) {
    try {
      await ElMessageBox.confirm(
        confirmMessage || `确定删除此${entityName}？删除后不可恢复。`,
        '删除确认',
        { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
      )
    } catch {
      return false
    }

    deleteLoading.value = true
    try {
      await deleteFn(id)
      ElMessage.success(`${entityName}删除成功`)
      onSuccess && onSuccess()
      return true
    } catch (e) {
      return false
    } finally {
      deleteLoading.value = false
    }
  }

  return {
    submitLoading,
    deleteLoading,
    submit,
    handleDelete
  }
}