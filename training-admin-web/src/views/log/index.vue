<script setup>
import { onMounted, ref } from 'vue'
import { queryPageApi} from '@/api/log'

//列表展示数据
let tableData = ref([])

//钩子函数 - 页面加载时触发
onMounted(() => {
  queryPage()
})

//分页组件
const pagination = ref({currentPage: 1, pageSize: 15, total: 0})
//每页展示记录数发生变化时触发
const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize
  queryPage()
}
//当前页码发生变化时触发
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page
  queryPage()
}

//分页条件查询
const queryPage = async () => {
  const result = await queryPageApi(pagination.value.currentPage,pagination.value.pageSize);

  if(result.code) {
    tableData.value = result.data.rows
    pagination.value.total = result.data.total
  }
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">日志管理</h1>
    </header>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" fit>
        <el-table-column prop="operateEmpName" label="操作人" min-width="80" />
        <el-table-column
          prop="operateTime"
          label="操作时间"
          min-width="150"
          class-name="cell-muted"
        />
        <el-table-column prop="className" label="类名" min-width="180" />
        <el-table-column prop="methodName" label="方法名" min-width="100" />
        <el-table-column
          prop="costTime"
          label="操作耗时(ms)"
          min-width="110"
          class-name="cell-muted"
        />
        <el-table-column prop="methodParams" label="请求参数" min-width="280">
          <template #default="scope">
            <el-popover
              effect="light"
              trigger="hover"
              placement="top"
              width="auto"
              popper-style="font-size:12px"
            >
              <template #default>
                <div>参数: {{ scope.row.methodParams }}</div>
              </template>
              <template #reference>
                <el-tag v-if="scope.row.methodParams.length <= 30">{{ scope.row.methodParams }}</el-tag>
                <el-tag v-else>{{ scope.row.methodParams.substring(0, 30) + '...' }}</el-tag>
              </template>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="returnValue" label="返回值" min-width="150" />
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
/* layout 的白色主面板内边距为 24px；用负外边距把画布铺满，
   再以 32px 内边距承载内容，形成 #f5f5f2 画布 + 白色表格容器 */
.page {
  box-sizing: border-box;
  min-height: calc(100% + 48px);
  margin: -24px;
  padding: 32px 32px 40px;
  background-color: #f5f5f2;
  border-radius: 14px;
  color: #1d1d1f;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

/* 表格容器 */
.table-card {
  overflow: hidden;
  background-color: #fff;
  border: 1px solid #e8e8e3;
  border-radius: 14px;
}

.table-card :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-row-hover-bg-color: #fafaf8;
  --el-table-header-text-color: #86868b;
  --el-table-text-color: #1d1d1f;
  --el-table-border-color: transparent;
  font-size: 13px;
}

.table-card :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.table-card :deep(th.el-table__cell.is-leaf) {
  padding: 11px 0;
  background: transparent;
  border-right: 0;
  border-bottom: 1px solid #f0f0ec;
  color: #86868b;
  font-size: 12.5px;
  font-weight: 500;
}

.table-card :deep(td.el-table__cell) {
  padding: 12px 0;
  border-right: 0;
  border-bottom: 1px solid #f2f2ee;
}

.table-card :deep(.el-table__body tr:last-child td.el-table__cell) {
  border-bottom: 0;
}

.table-card :deep(.el-table__body tr:hover > td.el-table__cell) {
  background: #fafaf8;
}

.table-card :deep(td.cell-muted) {
  color: #86868b;
  font-variant-numeric: tabular-nums;
}

.table-card :deep(.el-table .cell) {
  padding: 0 16px;
  line-height: 1.5;
}

.table-card :deep(.el-table__empty-text) {
  color: #86868b;
  font-size: 13px;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.pagination :deep(.el-pagination) {
  --el-pagination-font-size: 13px;
  --el-pagination-bg-color: transparent;
  --el-pagination-button-bg-color: transparent;
  --el-pagination-text-color: #6e6e73;
  --el-pagination-button-color: #6e6e73;
  --el-pagination-button-width: 28px;
  --el-pagination-button-height: 28px;
  --el-pagination-button-width-small: 28px;
  --el-pagination-button-height-small: 28px;
  --el-pagination-hover-color: #1d1d1f;
  --el-pagination-border-radius: 6px;
  --el-pagination-item-gap: 12px;
  color: #6e6e73;
  font-size: 13px;
  font-weight: 400;
}

.pagination :deep(.el-pager li) {
  margin: 0 2px;
  background: transparent;
  font-weight: 400;
}

.pagination :deep(.el-pager li:hover) {
  background: #ecece7;
}

.pagination :deep(.el-pager li.is-active) {
  background: #e7e7e2;
  color: #1d1d1f;
  font-weight: 500;
}

.pagination :deep(button:hover) {
  background: #ecece7;
}

.pagination :deep(.el-pagination__total),
.pagination :deep(.el-pagination__jump) {
  color: #86868b;
  font-size: 12.5px;
}

.pagination :deep(.el-select .el-input) {
  width: 100px;
}

.pagination :deep(.el-input__inner) {
  height: 26px;
  font-size: 12.5px;
}

.pagination :deep(.el-pagination__sizes .el-input__wrapper) {
  padding: 0 8px;
  background: transparent;
  box-shadow: none;
}

.pagination :deep(.el-pagination__jump .el-input__wrapper) {
  background: transparent;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
}

@media (max-width: 720px) {
  .page {
    padding: 24px 20px 32px;
  }
}
</style>
