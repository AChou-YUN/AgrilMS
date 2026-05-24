import { ref, reactive, nextTick } from 'vue'

/**
 * 表单弹窗组合式函数
 * 封装弹窗的打开、编辑模式切换、表单重置、提交、关闭逻辑
 * @param {Object} options
 * @param {Function} options.createFn - 新增 API 函数
 * @param {Function} options.updateFn - 编辑 API 函数
 * @param {Function} [options.onSuccess] - 提交成功后的回调（如刷新列表）
 */
export function useFormDialog({ createFn, updateFn, onSuccess }) {
  const dialogVisible = ref(false)
  const isEdit = ref(false)
  const editId = ref(null)
  const submitLoading = ref(false)
  const formRef = ref(null)

  /**
   * 打开新增弹窗
   * @param {Object} defaultForm - 表单默认值
   */
  function openCreate(defaultForm = {}) {
    isEdit.value = false
    editId.value = null
    dialogVisible.value = true
    nextTick(() => {
      if (formRef.value) {
        formRef.value.resetFields()
      }
    })
    return defaultForm
  }

  /**
   * 打开编辑弹窗
   * @param {Object} row - 要编辑的行数据
   * @param {Function} [mapFn] - 将行数据映射为表单数据的函数
   */
  function openEdit(row, mapFn) {
    isEdit.value = true
    editId.value = row.id
    dialogVisible.value = true
    nextTick(() => {
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    })
    if (mapFn) {
      return mapFn(row)
    }
    return { ...row }
  }

  /**
   * 提交表单
   * @param {Object} formData - 表单数据
   * @param {Object} [extraData] - 额外附加的数据
   */
  async function submit(formData, extraData = {}) {
    if (!formRef.value) return false
    
    let valid = false
    try {
      valid = await formRef.value.validate()
    } catch {
      valid = false
    }
    if (!valid) return false

    submitLoading.value = true
    try {
      const data = { ...formData, ...extraData }
      if (isEdit.value && editId.value != null) {
        await updateFn(editId.value, data)
      } else {
        await createFn(data)
      }
      dialogVisible.value = false
      onSuccess && onSuccess()
      return true
    } catch (e) {
      return false
    } finally {
      submitLoading.value = false
    }
  }

  /**
   * 关闭弹窗
   */
  function close() {
    dialogVisible.value = false
  }

  return {
    dialogVisible,
    isEdit,
    editId,
    submitLoading,
    formRef,
    openCreate,
    openEdit,
    submit,
    close
  }
}