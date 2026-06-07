<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header-flex">
          <span>销售单详情</span>
          <div>
            <el-button type="primary" plain @click="handlePrint" v-if="detail.status === 1">
              <el-icon><Printer /></el-icon> 打印
            </el-button>
            <el-button type="warning" plain @click="openReturnDialog" v-if="detail.status === 1 || detail.status === 3">
              <el-icon><RefreshLeft /></el-icon> 退货
            </el-button>
            <el-button @click="$router.back()">返回</el-button>
          </div>
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
      <el-table :data="detail.items || []" size="default">
        <el-table-column prop="productName" label="产品名称" min-width="150" />
        <el-table-column prop="specification" label="规格" width="100" />
        <el-table-column prop="unitName" label="单位" width="60" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="可退数量" width="90">
          <template #default="{ row }">
            {{ (row.quantity || 0) - (row.returnQuantity || 0) }}
          </template>
        </el-table-column>
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

    <!-- 退货弹窗 -->
    <el-dialog v-model="returnDialogVisible" title="退货处理" width="700px" :close-on-click-modal="false">
      <el-alert type="info" :closable="false" style="margin-bottom: 16px;">
        选择退货类型：整单退货将退回所有商品，部分退货可选择具体商品和数量。
      </el-alert>

      <el-radio-group v-model="returnType" style="margin-bottom: 16px;">
        <el-radio value="full">整单退货</el-radio>
        <el-radio value="partial">部分退货</el-radio>
      </el-radio-group>

      <el-table :data="returnItems" size="small" v-if="returnType === 'partial'" border>
        <el-table-column prop="productName" label="产品名称" min-width="120" />
        <el-table-column prop="quantity" label="销售数量" width="80" />
        <el-table-column prop="returnQuantity" label="已退数量" width="80" />
        <el-table-column label="可退数量" width="80">
          <template #default="{ row }">{{ row.returnable }}</template>
        </el-table-column>
        <el-table-column label="本次退货" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.returnQty" :min="0" :max="row.returnable" size="small" :disabled="row.returnable <= 0" />
          </template>
        </el-table-column>
      </el-table>

      <el-input v-model="returnRemark" type="textarea" :rows="2" placeholder="退货原因（选填）" style="margin-top: 16px;" />

      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="returnLoading" @click="handleReturn">确认退货</el-button>
      </template>
    </el-dialog>

    <!-- 打印预览弹窗 -->
    <el-dialog v-model="printDialogVisible" title="销售单打印预览" width="750px">
      <div class="print-content" v-if="printData">
        <div class="print-header">
          <h2>销售单</h2>
          <div class="print-info-row">
            <span>单号：{{ printData.orderNo }}</span>
            <span>日期：{{ printData.orderDate }}</span>
          </div>
          <div class="print-info-row">
            <span>客户：{{ printData.customerName || '散客' }}</span>
            <span>电话：{{ printData.customerPhone || '-' }}</span>
          </div>
          <div class="print-info-row" v-if="printData.customerAddress">
            <span>地址：{{ printData.customerAddress }}</span>
          </div>
        </div>
        <table class="print-table">
          <thead>
            <tr>
              <th>序号</th>
              <th>产品名称</th>
              <th>规格</th>
              <th>单位</th>
              <th>数量</th>
              <th>单价</th>
              <th>小计</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in printData.items" :key="item.id">
              <td>{{ index + 1 }}</td>
              <td>{{ item.productName }}</td>
              <td>{{ item.specification }}</td>
              <td>{{ item.unitName }}</td>
              <td>{{ item.quantity }}</td>
              <td>¥{{ Number(item.unitPrice).toFixed(2) }}</td>
              <td>¥{{ Number(item.subtotal).toFixed(2) }}</td>
            </tr>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="6" style="text-align: right; font-weight: bold;">合计：</td>
              <td style="font-weight: bold;">¥{{ Number(printData.totalAmount).toFixed(2) }}</td>
            </tr>
          </tfoot>
        </table>
        <div class="print-footer">
          <span>付款方式：{{ printData.paymentMethodText }}</span>
          <span v-if="printData.amountInChinese">大写金额：{{ printData.amountInChinese }}</span>
        </div>
        <div class="print-footer">
          <span>操作人：{{ printData.operatorName || '-' }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="printDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="doPrint">打印</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Printer, RefreshLeft } from '@element-plus/icons-vue'
import { getSalesOrderDetail, processReturn, getSalesPrintData } from '@/api/sales'

const route = useRoute()
const detail = ref({})

// 退货相关
const returnDialogVisible = ref(false)
const returnType = ref('partial')
const returnItems = ref([])
const returnRemark = ref('')
const returnLoading = ref(false)

// 打印相关
const printDialogVisible = ref(false)
const printData = ref(null)

async function loadDetail() {
  try {
    const res = await getSalesOrderDetail(route.params.id)
    detail.value = res.data || {}
  } catch (e) { /* handled */ }
}

/** 打开退货弹窗 */
function openReturnDialog() {
  returnRemark.value = ''
  returnType.value = 'partial'
  // 构建退货明细列表
  returnItems.value = (detail.value.items || []).map(item => ({
    itemId: item.id,
    productName: item.productName,
    quantity: item.quantity,
    returnQuantity: item.returnQuantity || 0,
    returnable: (item.quantity || 0) - (item.returnQuantity || 0),
    returnQty: 0
  }))
  returnDialogVisible.value = true
}

/** 确认退货 */
async function handleReturn() {
  try {
    const confirmMsg = returnType.value === 'full'
      ? '确定要整单退货吗？退货后库存将恢复，此操作不可撤销。'
      : '确定要退货吗？退货后库存将恢复，此操作不可撤销。'

    await ElMessageBox.confirm(confirmMsg, '退货确认', { type: 'warning' })

    returnLoading.value = true
    let requestData = { remark: returnRemark.value || '' }

    if (returnType.value === 'partial') {
      const items = returnItems.value
        .filter(i => i.returnQty > 0)
        .map(i => ({ itemId: i.itemId, quantity: i.returnQty }))

      if (items.length === 0) {
        ElMessage.warning('请至少选择一项退货商品并填写退货数量')
        returnLoading.value = false
        return
      }
      requestData.items = items
    } else {
      // 整单退货：items 为空则整单退货
      requestData.items = []
    }

    await processReturn(detail.value.id, requestData)
    ElMessage.success('退货处理成功')
    returnDialogVisible.value = false
    loadDetail() // 刷新详情
  } catch (e) { /* handled */ }
  returnLoading.value = false
}

/** 打印预览 */
async function handlePrint() {
  try {
    const res = await getSalesPrintData(detail.value.id)
    printData.value = res.data || {}
    printDialogVisible.value = true
  } catch (e) { /* handled */ }
}

/** 执行打印 */
function doPrint() {
  const printWindow = window.open('', '_blank')
  const content = document.querySelector('.print-content')
  if (!content) return

  printWindow.document.write(`
    <html>
    <head>
      <title>销售单 - ${printData.value.orderNo || ''}</title>
      <style>
        body { font-family: SimSun, serif; padding: 20px; font-size: 14px; }
        .print-header h2 { text-align: center; margin-bottom: 10px; }
        .print-info-row { display: flex; justify-content: space-between; margin-bottom: 5px; }
        .print-table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        .print-table th, .print-table td { border: 1px solid #000; padding: 6px 8px; text-align: center; }
        .print-table th { background: #f0f0f0; }
        .print-footer { margin-top: 10px; display: flex; justify-content: space-between; }
      </style>
    </head>
    <body>
      ${content.innerHTML}
      <script>setTimeout(function(){ window.print(); window.close(); }, 500);<\/script>
    </body>
    </html>
  `)
  printWindow.document.close()
}

onMounted(loadDetail)
</script>

<style scoped>
.card-header-flex { display: flex; justify-content: space-between; align-items: center; }
.text-danger { color: #f56c6c; font-weight: bold; }

/* 打印预览样式 */
.print-content { padding: 10px; }
.print-header h2 { text-align: center; margin-bottom: 12px; font-size: 20px; }
.print-info-row { display: flex; justify-content: space-between; margin-bottom: 6px; font-size: 14px; }
.print-table { width: 100%; border-collapse: collapse; margin: 16px 0; }
.print-table th, .print-table td { border: 1px solid #dcdfe6; padding: 8px 10px; text-align: center; font-size: 13px; }
.print-table th { background: #f5f7fa; font-weight: 600; }
.print-footer { margin-top: 12px; display: flex; justify-content: space-between; font-size: 14px; }
</style>