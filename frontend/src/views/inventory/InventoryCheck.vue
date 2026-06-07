<template>
  <div class="page-container">
    <div class="check-layout">
      <!-- 左侧：盘点录入 -->
      <div class="check-left">
        <div class="table-card">
          <div class="table-toolbar">
            <span>盘点录入</span>
          </div>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
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
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item label="系统库存">
              <el-input :model-value="systemStock" disabled placeholder="请选择产品" class="center-input" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="实际库存" prop="actualStock">
                  <el-input-number v-model="form.actualStock" :min="0" style="width: 100%;" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="备注">
              <el-input v-model="form.remark" placeholder="盘点备注（选填）" />
            </el-form-item>
          </el-form>

          <el-divider content-position="center">盘点结果</el-divider>
          <el-row :gutter="16" class="result-row">
            <el-col :span="8">
              <div class="result-field">
                <span class="result-field-label">系统库存</span>
                <span class="result-field-value">{{ systemStock }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="result-field">
                <span class="result-field-label">实际库存</span>
                <span class="result-field-value">{{ form.productId ? form.actualStock : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="result-field">
                <span class="result-field-label">差异</span>
                <span
                  v-if="form.productId"
                  class="result-field-value"
                  :class="{
                    'diff-over': diffValue > 0,
                    'diff-under': diffValue < 0,
                    'diff-even': diffValue === 0
                  }"
                >
                  {{ diffValue > 0 ? '+' : '' }}{{ diffValue }}
                  <el-tag v-if="diffValue > 0" type="success" size="small" class="diff-tag">盘盈</el-tag>
                  <el-tag v-else-if="diffValue < 0" type="danger" size="small" class="diff-tag">盘亏</el-tag>
                  <el-tag v-else type="info" size="small" class="diff-tag">一致</el-tag>
                </span>
                <span v-else class="result-field-value" style="color: var(--text-muted);">-</span>
              </div>
            </el-col>
          </el-row>

          <div class="form-actions">
            <el-button type="primary" :loading="loading" @click="handleSubmit">
              <el-icon><Check /></el-icon>确认盘点
            </el-button>
            <el-button @click="handleReset">
              <el-icon><RefreshLeft /></el-icon>重置
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧：产品信息 -->
      <div class="check-right">
        <div class="table-card">
          <div class="table-toolbar">
            <span>产品信息</span>
          </div>
          <div class="info-content">
            <el-descriptions v-if="selectedProduct" :column="1" border size="small">
              <el-descriptions-item label="产品名称">{{ selectedProduct.name }}</el-descriptions-item>
              <el-descriptions-item label="产品分类">{{ selectedProduct.categoryName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="规格">{{ selectedProduct.specification || '-' }}</el-descriptions-item>
              <el-descriptions-item label="计量单位">{{ selectedProduct.unitName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="安全库存">
                <span :class="{ 'text-warning': systemStock < (selectedProduct.safetyStock || 0) }">
                  {{ selectedProduct.safetyStock || 0 }}
                </span>
              </el-descriptions-item>
              <el-descriptions-item label="参考进价">¥{{ Number(selectedProduct.purchasePrice || 0).toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="库存价值">¥{{ Number(systemStock * (selectedProduct.purchasePrice || 0)).toFixed(2) }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="systemStock < (selectedProduct.safetyStock || 0) ? 'warning' : 'success'" size="small">
                  {{ systemStock < (selectedProduct.safetyStock || 0) ? '低于安全库存' : '正常' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <div v-else class="empty-hint">
              <el-icon :size="40" color="var(--text-placeholder)"><Box /></el-icon>
              <span>请在左侧选择产品</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部：最近盘点记录 -->
    <div class="table-card">
      <div class="table-toolbar">
        <span>最近盘点记录</span>
      </div>
      <el-table :data="recentChecks" size="small" v-loading="logsLoading">
        <el-table-column prop="productName" label="产品名称" min-width="140" />
        <el-table-column label="变动类型" min-width="100">
          <template #default="{ row }">
            <el-tag size="small" type="warning">{{ changeTypeText(row.changeType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="变动数量" min-width="100">
          <template #default="{ row }">
            <span :class="row.changeQuantity > 0 ? 'text-success' : 'text-danger'">
              {{ row.changeQuantity > 0 ? '+' : '' }}{{ row.changeQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeStock" label="变动前库存" min-width="100" />
        <el-table-column prop="afterStock" label="变动后库存" min-width="100" />
        <el-table-column prop="operatorName" label="操作人" min-width="90" />
        <el-table-column prop="createTime" label="时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!logsLoading && recentChecks.length === 0" description="暂无盘点记录" :image-size="60" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { checkInventory, getInventoryLogs } from '@/api/inventory'
import { getProductList } from '@/api/product'
import dayjs from 'dayjs'

const formRef = ref(null)
const loading = ref(false)
const logsLoading = ref(false)
const products = ref([])
const systemStock = ref(0)
const selectedProduct = ref(null)
const recentChecks = ref([])

const form = reactive({
  productId: undefined,
  actualStock: 0,
  remark: ''
})

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  actualStock: [{ required: true, message: '请输入实际库存', trigger: 'blur' }]
}

const diffValue = computed(() => {
  if (!form.productId) return 0
  return form.actualStock - systemStock.value
})

function formatTime(t) {
  return t ? dayjs(t).format('YYYY-MM-DD HH:mm:ss') : ''
}

function changeTypeText(type) {
  const map = {
    PURCHASE: '进货入库', SALE: '销售出库', ADJUST: '盘点调整', DAMAGE: '报损', RETURN: '退货入库'
  }
  return map[type] || type
}

function onProductChange(val) {
  const product = products.value.find(p => p.id === val)
  selectedProduct.value = product || null
  systemStock.value = product ? (product.currentStock || 0) : 0
  form.actualStock = systemStock.value
}

async function loadProducts() {
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 999, status: 1 })
    products.value = res.data?.list || []
  } catch (e) { /* handled */ }
}

async function loadRecentChecks() {
  logsLoading.value = true
  try {
    const res = await getInventoryLogs({ changeType: 'ADJUST', pageNum: 1, pageSize: 10 })
    recentChecks.value = res.data?.list || res.data?.records || []
  } catch (e) { /* handled */ }
  logsLoading.value = false
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
      loadRecentChecks()
    } catch (e) { /* handled */ }
    loading.value = false
  })
}

function handleReset() {
  form.productId = undefined
  form.actualStock = 0
  form.remark = ''
  systemStock.value = 0
  selectedProduct.value = null
}

onMounted(() => {
  loadProducts()
  loadRecentChecks()
})
</script>

<style scoped>
.check-layout {
  display: flex;
  gap: var(--space-5);
  align-items: stretch;
}

.check-left {
  flex: 1;
  min-width: 0;
}

.check-left .table-card {
  height: 100%;
}

.check-right {
  flex: 1;
  min-width: 0;
}

.check-right .table-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.check-right .info-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.check-right :deep(.el-descriptions) {
  flex: 1;
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.check-right :deep(.el-descriptions__body) {
  height: 100%;
}

.check-right :deep(.el-descriptions__table) {
  height: 100%;
}

.check-right :deep(.el-descriptions__cell) {
  height: auto;
  padding: 10px 16px;
  text-align: center;
}

.check-right :deep(.el-descriptions__label.el-descriptions__cell) {
  width: 100px;
  background: var(--color-info-light);
  text-align: center;
  font-weight: var(--weight-medium);
}

/* ── 选择框选中文字居中 ── */
:deep(.el-select .el-input__inner) {
  text-align: center;
}

/* ── 系统库存输入框文字居中 ── */
.center-input :deep(.el-input__inner) {
  text-align: center;
}

/* ── 盘点结果（与表单风格一致的行内布局） ── */
.result-row {
  margin-bottom: 16px;
}

.result-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  background: var(--surface-bg);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-light);
  text-align: center;
}

.result-field-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: var(--weight-medium);
}

.result-field-value {
  font-size: 18px;
  font-weight: var(--weight-bold);
  color: var(--text-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.diff-over { color: var(--color-success); }
.diff-under { color: var(--color-danger); }
.diff-even { color: var(--text-primary); }
.diff-tag { font-size: 12px; margin-left: 4px; }

/* ── 按钮区 ── */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  padding-top: 4px;
}

/* ── 右侧空状态 ── */
.empty-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 0;
  color: var(--text-muted);
  font-size: 13px;
}
</style>