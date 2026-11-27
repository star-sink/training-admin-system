<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryAllApi, addDeptApi, queryInfoApi, updateDeptApi, deleteDeptApi } from '@/api/dept'

// 声明列表展示数据
let tableData = ref([])

// 动态加载数据 - 查询部门
const queryAll = async () => {
  const result = await queryAllApi()
  tableData.value = result.data
}

// 钩子函数
onMounted(() => {
  queryAll()
})

const formTitle = ref('')
//新增部门
const add = () => {
  formTitle.value = '新增部门'
  showDialog.value = true
  deptForm.value = {name: ''}
}

// 编辑部门 - 根据ID查询回显数据
const handleEdit = async (row) => {
  console.log(`Edit item with ID ${row.id}`);
  formTitle.value = '修改部门'
  showDialog.value = true
  deptForm.value = {name: ''}

  const result = await queryInfoApi(row.id)
  if(result.code){
    deptForm.value = result.data
  }
};

// 删除部门 - 根据ID删除部门
const handleDelete = (row) => {
  console.log(`Delete item with ID ${row.id}`);
  //删除部门时, 需要弹出一个确认框, 如果是确认, 则删除部门
  ElMessageBox.confirm('此操作将永久删除该部门, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    // 删除部门
    const result = await deleteDeptApi(row.id)
    if(result.code){
      ElMessage.success('删除成功')
      queryAll()
    }
  })
};

// 新增部门对话框的状态
const showDialog = ref(false)
// 表单数据
const deptForm = ref({name: ''})
// 表单验证规则
const formRules = ref({
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
  ]
})

// 表单引用
const deptFormRef = ref(null)

// 重置表单
const resetForm = () => {
  deptFormRef.value.resetFields()
}

// 提交表单
const save = async () => {
  await deptFormRef.value.validate(async valid => {
    if (!valid) return
    // 提交表单
    let result = null;
    if(deptForm.value.id){
      result = await updateDeptApi(deptForm.value) // 修改
    }else {
      result = await addDeptApi(deptForm.value) // 新增
    } 
    if(result.code){
      ElMessage.success('操作成功')
      // 关闭对话框
      showDialog.value = false
      // 重置表单
      resetForm()
      // 重新加载数据
      queryAll()
    }else {
      ElMessage.error(result.msg)
    }
  })
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">部门管理</h1>
    </header>

    <div class="toolbar">
      <el-button class="btn-primary" @click="add()">
        <el-icon><Plus /></el-icon>新增部门
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column type="index" label="序号" width="64" class-name="cell-muted" />
        <el-table-column prop="name" label="部门名称" min-width="240" />
        <el-table-column
          prop="updateTime"
          label="最后修改时间"
          min-width="180"
          class-name="cell-muted"
        />
        <el-table-column label="操作" width="140" align="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button class="row-action" @click="handleEdit(row)">修改</el-button>
              <el-button class="row-action row-action-danger" @click="handleDelete(row)">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDialog" :title="formTitle" width="480px" @close="resetForm">
      <el-form :model="deptForm" :rules="formRules" ref="deptFormRef" class="dialog-form">
        <el-form-item label="部门名称" prop="name" label-width="80px">
          <el-input v-model="deptForm.name" autocomplete="off" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button class="btn-text" @click="showDialog = false">取消</el-button>
          <el-button class="btn-primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* layout 的白色主面板内边距为 24px；用负外边距把画布铺满，
   再以 32px 内边距承载内容，形成 #f5f5f2 画布 + 白色容器 */
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

.toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

/* 按钮 */
.btn-primary.el-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 36px;
  padding: 0 16px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  --el-button-bg-color: #1d1d1f;
  --el-button-border-color: #1d1d1f;
  --el-button-text-color: #fff;
  --el-button-hover-bg-color: #333336;
  --el-button-hover-border-color: #333336;
  --el-button-hover-text-color: #fff;
  --el-button-active-bg-color: #000;
  --el-button-active-border-color: #000;
}

.btn-text.el-button {
  height: 36px;
  margin-left: 0;
  padding: 0 12px;
  border-radius: 8px;
  font-size: 13px;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-text-color: #6e6e73;
  --el-button-hover-bg-color: #ecece7;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #e7e7e2;
  --el-button-active-border-color: transparent;
}

/* 列表容器 */
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
  padding: 13px 0;
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

.table-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 14px;
}

.row-action.el-button {
  height: 28px;
  margin-left: 0;
  padding: 0 2px;
  border-radius: 0;
  font-size: 13px;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-text-color: #6e6e73;
  --el-button-hover-bg-color: transparent;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: transparent;
}

/* 删除：浅红文字，不做色块 */
.row-action-danger.el-button {
  --el-button-text-color: #a85b55;
  --el-button-hover-text-color: #8a443f;
}

/* 弹窗 */
.page :deep(.el-overlay) {
  background-color: rgb(245 245 242 / 82%);
}

.page :deep(.el-dialog) {
  --el-dialog-padding-primary: 24px;
  --el-dialog-border-radius: 14px;
  --el-dialog-box-shadow: none;
  border: 1px solid #e8e8e3;
  border-radius: 14px;
  box-shadow: none;
}

.page :deep(.el-dialog__header) {
  padding-bottom: 8px;
  margin-right: 0;
}

.page :deep(.el-dialog__title) {
  color: #1d1d1f;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.page :deep(.el-dialog__body) {
  padding-top: 20px;
  padding-bottom: 8px;
}

.page :deep(.el-dialog__footer) {
  padding-top: 8px;
}

.page :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: #a1a1a6;
}

.page :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #1d1d1f;
}

.dialog-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.dialog-form :deep(.el-form-item__label) {
  color: #86868b;
  font-size: 13px;
  font-weight: 400;
}

.dialog-form :deep(.el-input__wrapper) {
  padding: 1px 12px;
  background-color: #fafaf8;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
  transition: background-color 0.15s, box-shadow 0.15s;
}

.dialog-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #d8d8d2 inset;
}

.dialog-form :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #1d1d1f inset;
}

.dialog-form :deep(.el-input__inner) {
  height: 34px;
  color: #1d1d1f;
  font-size: 13px;
}

.dialog-form :deep(.el-input__inner::placeholder) {
  color: #a1a1a6;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.dialog-footer .el-button {
  margin-left: 0;
}

@media (max-width: 720px) {
  .page {
    padding: 24px 20px 32px;
  }

  .toolbar .btn-primary {
    width: 100%;
    justify-content: center;
  }
}
</style>
