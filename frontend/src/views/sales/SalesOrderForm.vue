<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header><span>销售开单</span></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-form-item label="客户">
              <el-select v-model="form.customerId" placeholder="选择客户(可选)" clearable filterable style="width:100%;">
                <el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="销售日期" prop="orderDate">
              <el-date-picker v-model="form.orderDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="付款方式" prop="paymentMethod">
              <el-select v-model="form.paymentMethod" placeholder="请选择" style="width:100%;">
                <el-option label="现金" :value="1" />
                <el-option label="微信" :value="2" />
                <el-option label="支付宝" :value="3" />
                <el-option label="赊账" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="备注"><el-input v-model="form.remark" placeholder="备注" /></el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">销售明细</el-divider>
        <el-table :data="form.items" border size="small">
          <el-table-column label="产品" min-width="200">
            <template #default="{ row, $index }">
              <el-select v-model="row.productId" placeholder="选择产品" filterable style="width:100%;" @change="(val) => onProductChange(val, $index)">
                <el-option v-for="p in products" :key="p.id" :label="p.name + (p.specification ? ' (' + p.specification + ')' : '') + ' 库存:' + (p.currentStock || 0)" :value="p.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120">
            <template #default="{ row }"><el-input-number v-model="row.quantity" :min="1" size="small" style="width:100%;" /></template>
          </el-table-column>
          <el-table-column label="售价(元)" width="130">
            <template #default="{ row }"><el-input-number v-model="row.unitPrice" :min="0.01" :precision="2" size="small" style="width:100%;" /></template>
          </el-table-column>
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ (row.quantity * row.unitPrice).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template #default="{ $index }"><el-button type="danger" text size="small" @click="removeItem($index)">删除</el-button></template>
          </el-table-column>
        </el-table>
        <div style="margin:12px 0;"><el-button type="primary" plain @click="addItem">添加明细</el-button></div>
        <div class="total-amount">
          总金额：<span class="danger-text">¥{{ totalAmount.toFixed(2) }}</span>
        </div>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createSalesOrder } from '@/api/sales'
import { getAllCustomers } from '@/api/customer'
import { getProductList } from '@/api/product'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const customers = ref([])
const products = ref([])
const form = reactive({
  customerId: undefined, orderDate: '', paymentMethod: 1, remark: '',
  items: [{ productId: undefined, quantity: 1, unitPrice: 0 }]
})
const rules = {
  orderDate: [{ required: true, message: '请选择销售日期', trigger: 'change' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', trigger: 'change' }]
}
const totalAmount = computed(() => form.items.reduce((s, i) => s + (i.quantity || 0) * (i.unitPrice || 0), 0))

function addItem() { form.items.push({ productId: undefined, quantity: 1, unitPrice: 0 }) }
function removeItem(i) { if (form.items.length > 1) form.items.splice(i, 1) }
function onProductChange(val, i) {
  const p = products.value.find(x => x.id === val)
  if (p) form.items[i].unitPrice = p.sellingPrice || 0
}

async function loadOptions() {
  try {
    const [cr, pr] = await Promise.all([
      getAllCustomers(),
      getProductList({ pageNum: 1, pageSize: 999, status: 1 })
    ])
    customers.value = cr.data || []
    products.value = pr.data?.list || []
  } catch (e) { /* handled */ }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    const items = form.items.filter(i => i.productId && i.quantity > 0 && i.unitPrice > 0)
    if (items.length === 0) { ElMessage.warning('请至少添加一条销售明细'); return }
    loading.value = true
    try {
      await createSalesOrder({
        customerId: form.customerId, orderDate: form.orderDate,
        paymentMethod: form.paymentMethod, remark: form.remark, items
      })
      ElMessage.success('销售单创建成功')
      router.push('/sales-orders')
    } catch (e) {}
    loading.value = false
  })
}

onMounted(loadOptions)
</script>

<style scoped>
.total-amount {
  text-align: right;
  font-size: 16px;
  margin: 16px 0;
  font-family: var(--font-display);
}
</style>
