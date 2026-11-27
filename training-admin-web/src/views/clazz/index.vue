<script setup>
import { onMounted, ref, watch } from "vue";
import {
  queryPageApi,
  addApi,
  queryInfoApi,
  updateApi,
  deleteApi,
} from "@/api/clazz";
import { queryAllApi as queryAllEmpApi } from "@/api/emp";
import { ElMessage, ElMessageBox } from "element-plus";

//学科列表数据
const subjects = ref([
  { name: "Java", value: 1 },
  { name: "前端", value: 2 },
  { name: "大数据", value: 3 },
  { name: "Python", value: 4 },
  { name: "Go", value: 5 },
  { name: "嵌入式", value: 6 },
]);
//搜索表单对象
let searchClazz = ref({ begin: "", end: "", date: [], name: "" });
//列表展示数据
let tableData = ref([]);

//钩子函数 - 页面加载时触发
onMounted(() => {
  queryPage();
  queryAllEmp();
});

//所有的员工数据
let emps = ref([]);
//加载所有的员工数据
const queryAllEmp = async () => {
  let result = await queryAllEmpApi();
  if (result.code) {
    emps.value = result.data;
  }
};

//分页组件
const pagination = ref({ currentPage: 1, pageSize: 10, total: 0 });
//每页展示记录数发生变化时触发
const handleSizeChange = (pageSize) => {
  pagination.value.pageSize = pageSize;
  queryPage();
};
//当前页码发生变化时触发
const handleCurrentChange = (page) => {
  pagination.value.currentPage = page;
  queryPage();
};

//分页条件查询
const queryPage = async () => {
  const result = await queryPageApi(
    searchClazz.value.begin,
    searchClazz.value.end,
    searchClazz.value.name,
    pagination.value.currentPage,
    pagination.value.pageSize
  );

  if (result.code) {
    tableData.value = result.data.rows;
    pagination.value.total = result.data.total;
  }
};

//清空搜索栏
const clear = () => {
  searchClazz.value = { begin: "", end: "", date: [], name: "" };
  queryPage();
};

// 监听日期范围变化，拆分出开始、结束日期
watch(
  () => searchClazz.value.date,
  (newVal) => {
    if (newVal.length > 0) {
      searchClazz.value.begin = newVal[0];
      searchClazz.value.end = newVal[1];
    } else {
      searchClazz.value.begin = "";
      searchClazz.value.end = "";
    }
  }
);

//----------- 新增 / 修改 ---------------------------
let dialogFormVisible = ref(false); //控制新增/修改的对话框的显示与隐藏
let labelWidth = ref(80); //form表单label的宽度
let formTitle = ref(""); //表单的标题
//员工对象-表单数据绑定
let clazz = ref({
  id: "",
  name: "",
  room: "",
  beginDate: "",
  endDate: "",
  subject: "",
  masterId: "",
});

//清空表单
const clearClazz = () => {
  clazz.value = {
    id: "",
    name: "",
    room: "",
    beginDate: "",
    endDate: "",
    subject: "",
    masterId: "",
  };
};

//新增班级
const addClazz = () => {
  dialogFormVisible.value = true;
  formTitle.value = "新增班级";
  clearClazz();
};

//修改班级
const updateClazz = async (id) => {
  clearClazz();
  dialogFormVisible.value = true;
  let result = await queryInfoApi(id);
  if (result.code) {
    clazz.value = result.data;
  }
};

//表单校验规则
const clazzFormRef = ref();
const rules = ref({
  name: [
    { required: true, message: "班级名称为必填项", trigger: "blur" },
    { min: 4, max: 30, message: "用户名长度为4-30个字", trigger: "blur" },
  ],
  room: [
    { min: 1, max: 20, message: "班级教室长度为1-20个字", trigger: "blur" },
  ],
  beginDate: [
    { required: true, message: "开课时间为必填项", trigger: "change" },
  ],
  endDate: [{ required: true, message: "结课时间为必填项", trigger: "change" }],
  subject: [{ required: true, message: "学科为必填项", trigger: "change" }],
});

//重置表单
const resetForm = (clazzForm) => {
  if (!clazzForm) return;
  clazzForm.resetFields();
};

//-------------保存班级信息
const save = (clazzForm) => {
  //表单校验
  if (!clazzForm) return;
  clazzForm.validate(async (valid) => {
    if (valid) {
      let api;
      if (clazz.value.id) {
        api = updateApi(clazz.value);
      } else {
        api = addApi(clazz.value);
      }

      let result = await api;
      if (result.code) {
        ElMessage.success("操作成功");
        dialogFormVisible.value = false;
        queryPage();
      } else {
        ElMessage.error(result.msg);
      }
    } else {
      return false;
    }
  });
};

//------- 删除班级
//根据ID删除单个班级
const delById = async (id) => {
  ElMessageBox.confirm("您确认删除此数据吗?", "删除班级", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      let result = await deleteApi(id);
      if (result.code) {
        ElMessage.success("删除成功");
        queryPage();
      } else {
        ElMessage.error(result.msg);
      }
    })
    .catch(() => {
      ElMessage.info("取消删除");
    });
};
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">班级管理</h1>
    </header>

    <div class="toolbar">
      <el-form :inline="true" :model="searchClazz" class="filter-form">
        <el-form-item label="班级名称">
          <el-input v-model="searchClazz.name" />
        </el-form-item>

        <el-form-item label="结课时间">
          <el-date-picker
            v-model="searchClazz.date"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item class="filter-actions">
          <el-button class="btn-primary" @click="queryPage()">查询</el-button>
          <el-button class="btn-text" @click="clear()">清空</el-button>
        </el-form-item>
      </el-form>

      <el-button
        class="btn-primary"
        @click="
          addClazz();
          resetForm(clazzFormRef);
        "
      >
        <el-icon><Plus /></el-icon>新增班级
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" style="width: 100%" fit>
        <el-table-column
          type="index"
          label="序号"
          width="56"
          class-name="cell-muted"
        />
        <el-table-column prop="name" label="班级名称" min-width="150" />
        <el-table-column prop="room" label="教室" min-width="90" />
        <el-table-column prop="masterName" label="班主任" min-width="90" />
        <el-table-column prop="beginDate" label="开课时间" min-width="112" />
        <el-table-column prop="endDate" label="结课时间" min-width="112" />
        <el-table-column prop="status" label="状态" min-width="90" />
        <el-table-column
          prop="updateTime"
          label="最后修改时间"
          min-width="150"
          class-name="cell-muted"
        />
        <el-table-column label="操作" width="130" align="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button
                class="row-action"
                @click="
                  updateClazz(scope.row.id);
                  resetForm(clazzFormRef);
                "
              >
                编辑
              </el-button>
              <el-button
                class="row-action row-action-danger"
                @click="delById(scope.row.id)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[5, 10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogFormVisible" :title="formTitle" width="520px">
      <el-form
        :model="clazz"
        ref="clazzFormRef"
        :rules="rules"
        class="dialog-form"
      >
        <el-form-item label="班级名称" :label-width="labelWidth" prop="name">
          <el-input v-model="clazz.name" />
        </el-form-item>

        <el-form-item label="班级教室" :label-width="labelWidth" prop="room">
          <el-input v-model="clazz.room" />
        </el-form-item>

        <el-form-item
          label="开课时间"
          :label-width="labelWidth"
          prop="beginDate"
        >
          <el-date-picker
            v-model="clazz.beginDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="结课时间" :label-width="labelWidth" prop="endDate">
          <el-date-picker
            v-model="clazz.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="班主任" :label-width="labelWidth">
          <el-select v-model="clazz.masterId" style="width: 100%">
            <el-option
              v-for="emp in emps"
              :key="emp.id"
              :label="emp.name"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="学科" :label-width="labelWidth" prop="subject">
          <el-select v-model="clazz.subject" style="width: 100%">
            <el-option
              v-for="sub in subjects"
              :key="sub.value"
              :label="sub.name"
              :value="sub.value"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button
            class="btn-text"
            @click="
              dialogFormVisible = false;
              resetForm(clazzFormRef);
            "
          >
            取消
          </el-button>
          <el-button class="btn-primary" @click="save(clazzFormRef)"
            >保存</el-button
          >
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

/* 搜索区 + 新增按钮 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px 24px;
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px 20px;
}

.filter-form :deep(.el-form-item) {
  margin: 0;
}

.filter-form :deep(.el-form-item__label) {
  padding-right: 8px;
  color: #86868b;
  font-size: 13px;
  font-weight: 400;
}

.filter-form :deep(.el-input),
.filter-form :deep(.el-select) {
  width: 200px;
}

.filter-form :deep(.el-date-editor) {
  width: 280px;
  height: 36px;
}

.filter-form :deep(.el-range-input) {
  font-size: 13px;
}

.filter-actions :deep(.el-form-item__content) {
  gap: 8px;
}

.filter-actions :deep(.el-button) {
  margin-left: 0;
}

/* 输入控件（只作用于搜索区与弹窗，避免影响分页控件） */
.filter-form :deep(.el-input__wrapper),
.dialog-form :deep(.el-input__wrapper) {
  padding: 1px 12px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
  transition: box-shadow 0.15s, background-color 0.15s;
}

.filter-form :deep(.el-input__wrapper:hover),
.dialog-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #d8d8d2 inset;
}

.filter-form :deep(.el-input__wrapper.is-focus),
.dialog-form :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px #1d1d1f inset;
}

.filter-form :deep(.el-input__inner),
.dialog-form :deep(.el-input__inner) {
  height: 34px;
  color: #1d1d1f;
  font-size: 13px;
}

.filter-form :deep(.el-input__inner::placeholder),
.dialog-form :deep(.el-input__inner::placeholder) {
  color: #a1a1a6;
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

.row-action-danger.el-button {
  --el-button-text-color: #9a6a63;
  --el-button-hover-text-color: #7c4f48;
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
  background-color: #fafaf8;
}

.dialog-form :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.dialog-footer .el-button {
  margin-left: 0;
}

@media (max-width: 1080px) {
  .filter-form :deep(.el-input),
  .filter-form :deep(.el-select) {
    width: 180px;
  }

  .filter-form :deep(.el-date-editor) {
    width: 260px;
  }
}

@media (max-width: 720px) {
  .page {
    padding: 24px 20px 32px;
  }

  .filter-form {
    width: 100%;
  }

  .filter-form :deep(.el-form-item) {
    width: 100%;
  }

  .filter-form :deep(.el-input),
  .filter-form :deep(.el-select),
  .filter-form :deep(.el-date-editor) {
    width: 100%;
  }
}
</style>
