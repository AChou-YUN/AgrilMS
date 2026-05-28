<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header><span>{{ isEdit ? '编辑产品' : '新增产品' }}</span></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width: 700px;">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="产品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入产品名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产品分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%;">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规格">
              <el-input v-model="form.specification" placeholder="如 50kg/袋" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位">
              <el-select v-model="form.unitId" placeholder="请选择单位" style="width:100%;">
                <el-option v-for="u in units" :key="u.id" :label="u.name" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="参考进价">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="参考售价">
              <el-input-number v-model="form.sellingPrice" :min="0" :precision="2" style="width:100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="安全库存">
              <el-input-number v-model="form.safetyStock" :min="0" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产厂家">
              <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="批号">
              <el-input v-model="form.batchNumber" placeholder="请输入批号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产日期">
              <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="保质期至">
          <el-date-picker v-model="form.expiryDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:50%;" />
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入产品描述" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, createProduct, updateProduct } from '@/api/product'
import { getCategoryList } from '@/api/category'
import { getUnitList } from '@/api/unit'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const categories = ref([])
const units = ref([])

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '', categoryId: undefined, specification: '', unitId: undefined,
  purchasePrice: 0, sellingPrice: 0, safetyStock: 0, manufacturer: '',
  batchNumber: '', productionDate: '', expiryDate: '', description: ''
})

const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择产品分类', trigger: 'change' }]
}

async function loadOptions() {
  try {
    const [catRes, unitRes] = await Promise.all([getCategoryList(), getUnitList()])
    categories.value = catRes.data || []
    units.value = unitRes.data || []
  } catch (e) { /* handled */ }
}

async function loadProduct() {
  if (!route.params.id) return
  try {
    const res = await getProductDetail(route.params.id)
    Object.assign(form, res.data)
  } catch (e) { /* handled */ }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (isEdit.value) {
        await updateProduct(route.params.id, form)
        ElMessage.success('编辑成功')
      } else {
        await createProduct(form)
        ElMessage.success('新增成功')
      }
      router.push('/products')
    } catch (e) {}
    loading.value = false
  })
}

onMounted(() => { loadOptions(); loadProduct() })
</script>