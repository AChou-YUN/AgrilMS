<template>
  <div class="page-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header-flex">
          <span>销售单详情</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="销售单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName || '散客' }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ Number(detail.totalAmount || 0).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="付款方式">{{ detail.paymentMethodText }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === 1 ? 'success' : detail.status === 2 ? 'danger' : 'warning'" size="small">{{ detail.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="销售日期">{{ detail.orderDate }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detail.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">销售明细</el-divider>
      <el-table :data="detail.items || []" border size="small">
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="specification" label="规格" width="100" />
        <el-table-column prop="unitName" label="单位" width="60" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="returnQuantity" label="已退货" width="80">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.returnQuantity > 0 }">{{ row.returnQuantity || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="售价" width="100">
          <template #default="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="subtotal" label="小计" width="100">
          <template #default="{ row }">¥{{ Number(row.subtotal).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getSalesOrderDetail } from '@/api/sales'

const route = useRoute()
const detail = ref({})

async function loadDetail() {
  try {
    const res = await getSalesOrderDetail(route.params.id)
    detail.value = res.data || {}
  } catch (e) { /* handled */ }
}

onMounted(loadDetail)
</script>

<style scoped>
.card-header-flex { display: flex; justify-content: space-between; align-items: center; }
</style>