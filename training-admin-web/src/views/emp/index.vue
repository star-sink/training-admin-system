<script setup>
import { ref, watch, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  queryPageApi,
  addEmpApi,
  queryInfoApi,
  updateEmpApi,
  deleteEmpApi,
} from "@/api/emp";
import { queryAllApi as queryAllDeptApi } from "@/api/dept";

//职位列表数据
const jobs = ref([
  { name: "班主任", value: 1 },
  { name: "讲师", value: 2 },
  { name: "学工主管", value: 3 },
  { name: "教研主管", value: 4 },
  { name: "咨询师", value: 5 },
  { name: "其他", value: 6 },
]);
//性别列表数据
const genders = ref([
  { name: "男", value: 1 },
  { name: "女", value: 2 },
]);
//部门列表数据
const deptList = ref([]);

const searchEmp = ref({
  name: "",
  gender: "",
  date: [],
  begin: "",
  end: "",
});

//侦听searchEmp中的date属性
watch(
  () => searchEmp.value.date,
  (newValue) => {
    if (newValue.length == 2) {
      searchEmp.value.begin = newValue[0];
      searchEmp.value.end = newValue[1];
    } else {
      searchEmp.value.begin = "";
      searchEmp.value.end = "";
    }
  }
);

//声明token
const token = ref("");

onMounted(async () => {
  handleSearch();

  //加载所有部门数据
  const result = await queryAllDeptApi();
  if (result.code) {
    deptList.value = result.data;
  }

  //加载localStorage存储的员工登录信息
  const loginUser = localStorage.getItem("loginUser");
  if (loginUser) {
    token.value = JSON.parse(loginUser).token;
  }
});

//查询员工
const handleSearch = async () => {
  const result = await queryPageApi(
    searchEmp.value.name,
    searchEmp.value.gender,
    searchEmp.value.begin,
    searchEmp.value.end,
    currentPage.value,
    pageSize.value
  );
  if (result.code) {
    empList.value = result.data.rows;
    total.value = result.data.total;
  }
};

const handleReset = () => {
  // 清空表单
  searchEmp.value = {
    name: "",
    gender: "",
    date: [],
    begin: "",
    end: "",
  };
  handleSearch();
};

// 示例数据
const empList = ref([]);

// 分页配置
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 职位转换函数
const getJobTitle = (job) => {
  switch (job) {
    case 1:
      return "班主任";
    case 2:
      return "讲师";
    case 3:
      return "学工主管";
    case 4:
      return "教研主管";
    case 5:
      return "咨询师";
    default:
      return "其他";
  }
};

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  handleSearch();
};
const handleCurrentChange = (val) => {
  currentPage.value = val;
  handleSearch();
};

// 操作处理
const handleEdit = async (id) => {
  const result = await queryInfoApi(id);
  if (result.code) {
    dialogVisible.value = true;
    dialogTitle.value = "修改员工";
    employee.value = result.data;

    //处理返回的工作经历信息
    let exprList = employee.value.exprList;
    if (exprList && exprList.length > 0) {
      exprList.forEach((expr) => {
        expr.exprDate = [expr.begin, expr.end];
      });
    }
  }
};

// 删除单个员工
const handleDelete = async (id) => {
  //弹出一个确认框, 如果确认, 就删除;
  ElMessageBox.confirm("确定删除该员工吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    // 删除员工
    const result = await deleteEmpApi(id);
    if (result.code) {
      ElMessage.success("删除员工成功");
      handleSearch();
    } else {
      ElMessage.error(result.msg);
    }
  });
};

//新增员工
const addEmp = () => {
  dialogVisible.value = true;
  dialogTitle.value = "新增员工";
  employee.value = {
    username: "",
    name: "",
    gender: "",
    phone: "",
    job: "",
    salary: "",
    deptId: "",
    entryDate: "",
    image: "",
    exprList: [],
  };
  employeeFormRef.value.resetFields();
};

//新增/修改表单
const employeeFormRef = ref(null);
const employee = ref({
  username: "",
  name: "",
  gender: "",
  phone: "",
  job: "",
  salary: "",
  deptId: "",
  entryDate: "",
  image: "",
  exprList: [],
});

//表单校验规则
// 验证规则
const rules = ref({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 2,
      max: 20,
      message: "用户名长度应在2到20个字符之间",
      trigger: "blur",
    },
  ],
  name: [
    { required: true, message: "请输入姓名", trigger: "blur" },
    {
      min: 2,
      max: 10,
      message: "姓名长度应在2到10个字符之间",
      trigger: "blur",
    },
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { pattern: /^1\d{10}$/, message: "请输入有效的手机号", trigger: "blur" },
  ],
});

// 控制弹窗
const dialogVisible = ref(false);
const dialogTitle = ref("新增员工");

// 图片上传成功后触发
const handleAvatarSuccess = (response) => {
  employee.value.image = response.data;
};
// 文件上传之前触发
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== "image/jpeg" && rawFile.type !== "image/png") {
    ElMessage.error("只支持上传图片");
    return false;
  } else if (rawFile.size / 1024 / 1024 > 10) {
    ElMessage.error("只能上传10M以内图片");
    return false;
  }
  return true;
};

//工作经历
//动态添加工作经历 .
const addExprItem = () => {
  employee.value.exprList.push({
    exprDate: [],
    begin: "",
    end: "",
    company: "",
    job: "",
  });
};

//动态删除工作经历 .
const delExprItem = (index) => {
  employee.value.exprList.splice(index, 1);
};

//监听-employee员工对象中的工作经历数据
watch(
  employee,
  () => {
    if (employee.value.exprList) {
      employee.value.exprList.forEach((expr) => {
        expr.begin = expr.exprDate[0];
        expr.end = expr.exprDate[1];
      });
    }
  },
  { deep: true }
);

//保存员工信息
const save = async () => {
  employeeFormRef.value.validate(async (valid) => {
    if (valid) {
      // 校验通过
      let result;
      if (employee.value.id) {
        //存在ID, 修改
        result = await updateEmpApi(employee.value);
      } else {
        //不存在ID, 新增
        result = await addEmpApi(employee.value);
      }
      if (result.code) {
        ElMessage.success("新增员工成功");
        dialogVisible.value = false;
        handleSearch();
      } else {
        ElMessage.error(result.msg);
      }
    }
  });
};

// 存储选中的 ID
const selectedIds = ref([]);

// 处理复选框选择变化的函数
function handleSelectionChange(selection) {
  const ids = selection.map((item) => item.id);
  selectedIds.value = ids;
}

//批量删除
const deleteByIds = async () => {
  //弹出一个确认框, 如果确认, 就删除;
  ElMessageBox.confirm("确定删除选中员工吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    // 删除员工
    const result = await deleteEmpApi(selectedIds.value);
    if (result.code) {
      ElMessage.success("删除员工成功");
      handleSearch();
    } else {
      ElMessage.error(result.msg);
    }
  });
};
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1 class="page-title">员工管理</h1>
    </header>

    <div class="filter-bar">
      <el-form :inline="true" :model="searchEmp" class="filter-form">
        <el-form-item label="姓名">
          <el-input v-model="searchEmp.name" />
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="searchEmp.gender" class="filter-select">
            <el-option label="男" value="1" />
            <el-option label="女" value="2" />
          </el-select>
        </el-form-item>

        <el-form-item label="入职日期">
          <el-date-picker
            v-model="searchEmp.date"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item class="filter-actions">
          <el-button class="btn-primary" @click="handleSearch">查询</el-button>
          <el-button class="btn-text" @click="handleReset">清空</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="action-bar">
      <el-button class="btn-text btn-text-danger" @click="deleteByIds"
        >批量删除</el-button
      >
      <el-button class="btn-primary" @click="addEmp">
        <el-icon><Plus /></el-icon>新增员工
      </el-button>
    </div>

    <div class="table-card">
      <el-table
        :data="empList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="44" align="center" />
        <el-table-column prop="name" label="姓名" min-width="96" />
        <el-table-column label="性别" width="64">
          <template #default="scope">
            {{ scope.row.gender == 1 ? "男" : "女" }}
          </template>
        </el-table-column>
        <el-table-column label="头像" width="80" align="center">
          <template #default="scope">
            <img :src="scope.row.image" alt="Avatar" class="avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="部门" min-width="120" />
        <el-table-column label="职位" min-width="96">
          <template #default="scope">
            {{ getJobTitle(scope.row.job) }}
          </template>
        </el-table-column>
        <el-table-column prop="entryDate" label="入职日期" min-width="112" />
        <el-table-column
          prop="updateTime"
          label="最后操作时间"
          min-width="150"
          class-name="cell-muted"
        />
        <el-table-column label="操作" width="130" align="right">
          <template #default="scope">
            <div class="table-actions">
              <el-button class="row-action" @click="handleEdit(scope.row.id)"
                >编辑</el-button
              >
              <el-button
                class="row-action row-action-danger"
                @click="handleDelete(scope.row.id)"
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      >
      </el-pagination>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px">
      <el-form
        ref="employeeFormRef"
        :model="employee"
        :rules="rules"
        label-width="80px"
        class="dialog-form"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="employee.username" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="employee.name" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="employee.gender" style="width: 100%">
                <el-option
                  v-for="gender in genders"
                  :key="gender.name"
                  :label="gender.name"
                  :value="gender.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="employee.phone" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位">
              <el-select v-model="employee.job" style="width: 100%">
                <el-option
                  v-for="job in jobs"
                  :key="job.name"
                  :label="job.name"
                  :value="job.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="薪资" prop="salary">
              <el-input v-model="employee.salary" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-select v-model="employee.deptId" style="width: 100%">
                <el-option
                  v-for="dept in deptList"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker
                v-model="employee.entryDate"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="头像">
              <el-upload
                class="avatar-uploader"
                action="/api/upload"
                :headers="{ token: token }"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img
                  v-if="employee.image"
                  :src="employee.image"
                  class="avatar"
                />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="expr-section">
          <el-button class="btn-text" @click="addExprItem">
            <el-icon><Plus /></el-icon>添加工作经历
          </el-button>

          <div
            class="expr-row"
            v-for="(expr, index) in employee.exprList"
            :key="index"
          >
            <el-form-item label-width="0">
              <el-date-picker
                v-model="expr.exprDate"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>

            <el-form-item label-width="0">
              <el-input v-model="expr.company" placeholder="公司名称" />
            </el-form-item>

            <el-form-item label-width="0">
              <el-input v-model="expr.job" placeholder="职位" />
            </el-form-item>

            <el-button
              class="btn-text btn-text-danger expr-remove"
              @click="delExprItem(index)"
            >
              删除
            </el-button>
          </div>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button class="btn-text" @click="dialogVisible = false"
            >取消</el-button
          >
          <el-button class="btn-primary" @click="save">保存</el-button>
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

/* 搜索区 */
.filter-bar {
  margin-bottom: 12px;
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

.filter-form :deep(.filter-select) {
  width: 160px;
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

/* 操作栏 */
.action-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.action-bar .el-button {
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
  display: inline-flex;
  align-items: center;
  gap: 4px;
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

.btn-text-danger.el-button {
  --el-button-text-color: #a85b55;
  --el-button-hover-text-color: #8a443f;
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

/* 表格头像：32px 方形、4px 轻圆角，src 绑定不变 */
.avatar {
  display: block;
  width: 32px;
  height: 32px;
  margin: 0 auto;
  border-radius: 4px;
  background-color: #f5f5f2;
  object-fit: cover;
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
  --el-button-text-color: #a85b55;
  --el-button-hover-text-color: #8a443f;
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

/* 上传区域：保持原有结构与接口，仅做圆形极简外观 */
.avatar-uploader.el-upload {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  overflow: hidden;
  border: 1px dashed #dcdcd6;
  border-radius: 50%;
  background-color: #fafaf8;
  cursor: pointer;
  transition: border-color 0.15s;
}

.avatar-uploader.el-upload:hover {
  border-color: #1d1d1f;
}

.avatar-uploader .avatar {
  width: 64px;
  height: 64px;
  margin: 0;
}

.avatar-uploader-icon {
  color: #a1a1a6;
  font-size: 18px;
}

/* 工作经历 */
.expr-section {
  margin-top: 4px;
  padding-top: 16px;
  border-top: 1px solid #f0f0ec;
}

.expr-row {
  display: grid;
  grid-template-columns:
    minmax(220px, 1.6fr) minmax(120px, 1fr) minmax(100px, 0.8fr)
    auto;
  gap: 12px;
  align-items: center;
  padding-top: 12px;
}

.dialog-form :deep(.expr-row .el-form-item) {
  margin-bottom: 0;
}

.dialog-form :deep(.expr-row .el-date-editor) {
  width: 100%;
}

.expr-remove {
  justify-self: end;
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

  .filter-form :deep(.filter-select) {
    width: 140px;
  }

  .filter-form :deep(.el-date-editor) {
    width: 260px;
  }
}

@media (max-width: 720px) {
  .page {
    padding: 24px 20px 32px;
  }

  .filter-form :deep(.el-form-item) {
    width: 100%;
  }

  .filter-form :deep(.el-input),
  .filter-form :deep(.el-select),
  .filter-form :deep(.el-date-editor) {
    width: 100%;
  }

  .action-bar .btn-primary {
    flex: 1;
    justify-content: center;
  }

  .page :deep(.el-dialog) {
    width: calc(100% - 32px);
  }

  .expr-row {
    grid-template-columns: 1fr;
  }

  .expr-remove {
    justify-self: start;
  }
}
</style>
