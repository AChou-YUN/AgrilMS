<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header-flex">
          <span>进货单详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="进货单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detail.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ Number(detail.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 0 ? 'warning' : detail.status === 1 ? 'success' : 'danger'" size="small">{{ detail.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="进货日期">{{ detail.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="3">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">进货明细</el-divider>
      <el-table :data="detail.items || []" border size="small">
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="specification" label="规格" width="100" />
        <el-table-column prop="unitName" label="单位" width="60" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="进价" width="100">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" width="100">
          <template #default="{ row }">¥{{ Number(row.subtotal).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div v-if="detail.status === 0" style="margin-top:16px;text-align:right;">
        <el-button type="success" @click="handleConfirm">确认入库</el-button>
        <el-button type="warning" @click="handleCancel">取消进货单</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPurchaseOrderDetail, confirmPurchaseOrder, cancelPurchaseOrder } from '@/api/purchase'

const route = useRoute()
const detail = ref({})

async function loadDetail() {
  try {
    const res = await getPurchaseOrderDetail(route.params.id)
    detail.value = res.data || {}
  } catch (e) { /* handled */ }
}

async function handleConfirm() {
  try {
    await ElMessageBox.confirm('确认入库后将增加库存，确定继续？', '确认入库', { type: 'warning' })
  } catch { return }
  try {
    await confirmPurchaseOrder(route.params.id)
    ElMessage.success('确认入库成功')
    loadDetail()
  } catch (e) { /* handled */ }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm('确定取消此进货单？', '取消', { type: 'warning' })
  } catch { return }
  try {
    await cancelPurchaseOrder(route.params.id)
    ElMessage.success('已取消')
    loadDetail()
  } catch (e) { /* handled */ }
}

onMounted(loadDetail)
</script>

<style scoped>
.card-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>