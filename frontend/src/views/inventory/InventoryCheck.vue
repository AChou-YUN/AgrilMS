<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header><span>库存盘点</span></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 600px;">
        <el-form-item label="选择产品" prop="productId">
          <el-select
            v-model="form.productId"
            placeholder="请选择要盘点的产品"
            filterable
            style="width: 100%;"
            @change="onProductChange"
          >
            <el-option
              v-for="p in products"
              :key="p.id"
              :label="p.name + (p.specification ? ' (' + p.specification + ')' : '')"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="系统库存">
          <el-input :model-value="systemStock" disabled />
        </el-form-item>
        <el-form-item label="实际库存" prop="actualStock">
          <el-input-number v-model="form.actualStock" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="差异">
          <el-input :model-value="diffText" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="盘点备注" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">确认盘点</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { checkInventory } from '@/api/inventory'
import { getProductList } from '@/api/product'

const formRef = ref(null)
const loading = ref(false)
const products = ref([])
const systemStock = ref(0)

const form = reactive({
  productId: undefined,
  actualStock: 0,
  remark: ''
})

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  actualStock: [{ required: true, message: '请输入实际库存', trigger: 'blur' }]
}

const diffText = computed(() => {
  if (!form.productId) return ''
  const diff = form.actualStock - systemStock.value
  if (diff > 0) return `+${diff} (盘盈)`
  if (diff < 0) return `${diff} (盘亏)`
  return '0 (一致)'
})

function onProductChange(val) {
  const product = products.value.find(p => p.id === val)
  systemStock.value = product ? (product.currentStock || 0) : 0
  form.actualStock = systemStock.value
}

async function loadProducts() {
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 999, status: 1 })
    products.value = res.data?.list || []
  } catch (e) { /* handled */ }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await checkInventory({
        productId: form.productId,
        actualStock: form.actualStock,
        remark: form.remark
      })
      ElMessage.success('盘点成功')
      handleReset()
    } catch (e) { /* handled */ }
    loading.value = false
  })
}

function handleReset() {
  form.productId = undefined
  form.actualStock = 0
  form.remark = ''
  systemStock.value = 0
}

onMounted(loadProducts)
</script>