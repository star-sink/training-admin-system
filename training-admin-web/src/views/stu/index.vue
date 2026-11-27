<script setup>
import { onMounted, ref } from "vue";
import {
  queryPageApi,
  addApi,
  queryInfoApi,
  updateApi,
  deleteApi,
  handleViolationApi,
} from "@/api/stu";
import { queryAllApi as queryAllClazzApi } from "@/api/clazz";
import { ElMessage, ElMessageBox } from "element-plus";

//学历列表数据
const degrees = ref([
  { name: "初中", value: 1 },
  { name: "高中", value: 2 },
  { name: "大专", value: 3 },
  { name: "本科", value: 4 },
  { name: "硕士", value: 5 },
  { name: "博士", value: 6 },
]);
//性别列表数据
const genders = ref([
  { name: "男", value: 1 },
  { name: "女", value: 2 },
]);
//是否是院校学员列表数据
const whethers = ref([
  { name: "是", value: 1 },
  { name: "否", value: 0 },
]);
//搜索表单对象
let searchStu = ref({ clazzId: "", degree: "", name: "" });
//列表展示数据
let tableData = ref([]);

//钩子函数 - 页面加载时触发
onMounted(() => {
  queryPage();
  queryAllClazz();
});

//所有的班级数据
let clazzs = ref([]);
//加载所有的班级数据
const queryAllClazz = async () => {
  let result = await queryAllClazzApi();
  if (result.code) {
    clazzs.value = result.data;
  }
};

//复选框
let selectIds = ref([]);
const handleSelectionChange = (selection) => {
  selectIds.value = selection.map((item) => item.id);
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
    searchStu.value.clazzId,
    searchStu.value.degree,
    searchStu.value.name,
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
  searchStu.value = { clazzId: "", degree: "", name: "" };
  queryPage();
};

//----------- 新增 / 修改 ---------------------------
let dialogFormVisible = ref(false); //控制新增/修改的对话框的显示与隐藏
let labelWidth = ref(100); //form表单label的宽度
let formTitle = ref(""); //表单的标题
let stu = ref({
  //学生对象-表单数据绑定
  name: "",
  no: "",
  gender: "",
  phone: "",
  idCard: "",
  isCollege: "",
  address: "",
  degree: "",
  graduationDate: "",
  clazzId: "",
});

//清空表单
const clearStu = () => {
  stu.value = {
    name: "",
    no: "",
    gender: "",
    phone: "",
    idCard: "",
    isCollege: "",
    address: "",
    degree: "",
    graduationDate: "",
    clazzId: "",
  };
};

//新增学员
const addStu = () => {
  dialogFormVisible.value = true;
  formTitle.value = "新增学员";
  clearStu();
};

//修改学生
const updateStu = async (id) => {
  clearStu();
  dialogFormVisible.value = true;
  formTitle.value = "修改学员";
  let result = await queryInfoApi(id);
  if (result.code) {
    stu.value = result.data;
  }
};

//表单校验规则
const stuFormRef = ref();
const rules = ref({
  name: [
    { required: true, message: "姓名为必填项", trigger: "blur" },
    { min: 2, max: 10, message: "姓名长度为2-20个字", trigger: "blur" },
  ],
  no: [
    { required: true, message: "学号为必填项", trigger: "blur" },
    { min: 10, max: 10, message: "学号长度为10个字", trigger: "blur" },
  ],
  gender: [{ required: true, message: "性别为必填项", trigger: "change" }],
  phone: [
    { required: true, message: "手机号为必填项", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/g,
      message: "请输入合法的手机号",
      trigger: "blur",
    },
  ],
  idCard: [
    { required: true, message: "身份证号为必填项", trigger: "blur" },
    { pattern: /^\d{18}$/g, message: "请输入合法的身份证号", trigger: "blur" },
  ],
  isCollege: [
    { required: true, message: "是否院校学员为必填项", trigger: "change" },
  ],
});

//重置表单
const resetForm = (stuForm) => {
  if (!stuForm) return;
  stuForm.resetFields();
};

//-------------保存学生信息
const save = (stuForm) => {
  //表单校验
  if (!stuForm) return;
  stuForm.validate(async (valid) => {
    if (valid) {
      let api;
      if (stu.value.id) {
        api = updateApi(stu.value);
      } else {
        api = addApi(stu.value);
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

//------- 删除学生
//根据ID删除单个学生
const delById = async (id) => {
  ElMessageBox.confirm("您确认删除此数据吗?", "删除学生", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      let result = await deleteApi(`${id}`);
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

//批量删除员工
const delByIds = async () => {
  ElMessageBox.confirm("您确认删除此数据吗?", "删除学生", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      let result = await deleteApi(selectIds.value.join(","));
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

const openViolation = (id) => {
  ElMessageBox.prompt("请输入违纪扣分", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
    inputPattern: /\d?/,
    inputErrorMessage: "数据无效",
  }).then(async (val) => {
    let result = await handleViolationApi(id, val.value);
    if (result.code) {
      ElMessage.success("处理成功");
      queryPage();
    } else {
      ElMessage.error(result.msg);
    }
  });
};
</script>

<template>
  <div class="stu-page">
    <!-- ① 页面标题 -->
    <header class="page-header">
      <h1 class="page-title">学员管理</h1>
    </header>

    <!-- ② 查询区域 -->
    <el-form :inline="true" :model="searchStu" class="filter-form">
      <el-form-item label="姓名">
        <el-input v-model="searchStu.name" placeholder="输入姓名" />
      </el-form-item>

      <el-form-item label="最高学历">
        <el-select v-model="searchStu.degree" placeholder="全部">
          <el-option
            v-for="degree in degrees"
            :key="degree.value"
            :label="degree.name"
            :value="degree.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="所属班级">
        <el-select v-model="searchStu.clazzId" placeholder="全部">
          <el-option
            v-for="clazz in clazzs"
            :key="clazz.id"
            :label="clazz.name"
            :value="clazz.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item class="filter-actions">
        <el-button class="filter-submit" @click="queryPage()">查询</el-button>
        <el-button text class="filter-reset" @click="clear()">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- ③ 操作按钮 -->
    <div class="toolbar">
      <el-button
        class="tool-btn"
        @click="
          addStu();
          resetForm(stuFormRef);
        "
      >
        <el-icon><Plus /></el-icon>
        <span>新增学员</span>
      </el-button>
      <el-button class="tool-btn tool-btn-danger" @click="delByIds()">
        <el-icon><Delete /></el-icon>
        <span>批量删除</span>
      </el-button>
    </div>

    <!-- ④ 列表：去掉 border，仅保留行底细线 -->
    <el-table
      :data="tableData"
      class="stu-table"
      style="width: 100%"
      fit
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" align="center" width="40" />
      <el-table-column prop="name" label="姓名" align="left" min-width="90" />
      <el-table-column prop="no" label="学号" align="left" min-width="120" />
      <el-table-column
        prop="clazzName"
        label="班级"
        align="left"
        min-width="140"
      />
      <el-table-column prop="gender" label="性别" align="left" min-width="60">
        <template #default="scope">
          {{ scope.row.gender == 1 ? "男" : "女" }}
        </template>
      </el-table-column>
      <el-table-column
        prop="phone"
        label="手机号"
        align="left"
        min-width="120"
      />
      <el-table-column
        prop="degree"
        label="最高学历"
        align="left"
        min-width="80"
      >
        <template #default="scope">
          <span v-if="scope.row.degree == 1">初中</span>
          <span v-else-if="scope.row.degree == 2">高中</span>
          <span v-else-if="scope.row.degree == 3">大专</span>
          <span v-else-if="scope.row.degree == 4">本科</span>
          <span v-else-if="scope.row.degree == 5">硕士</span>
          <span v-else-if="scope.row.degree == 6">博士</span>
          <span v-else>其他</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="violationCount"
        label="违纪次数"
        align="left"
        min-width="80"
        class-name="cell-muted"
      />
      <el-table-column
        prop="violationScore"
        label="违纪扣分"
        align="left"
        min-width="80"
        class-name="cell-muted"
      />
      <el-table-column
        prop="updateTime"
        label="最后修改时间"
        align="left"
        min-width="160"
        class-name="cell-muted"
      />
      <el-table-column label="操作" align="right" width="136">
        <template #default="scope">
          <div class="row-actions">
            <el-button
              text
              class="row-action"
              @click="
                updateStu(scope.row.id);
                resetForm(stuFormRef);
              "
              >编辑</el-button
            >
            <el-button
              text
              class="row-action"
              @click="openViolation(scope.row.id)"
              >违纪</el-button
            >
            <el-button
              text
              class="row-action row-action-danger"
              @click="delById(scope.row.id)"
              >删除</el-button
            >
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- ⑤ 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[5, 10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="stu-pagination"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- ⑥ 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogFormVisible"
      :title="formTitle"
      width="50%"
      class="stu-dialog"
    >
      <el-form :model="stu" ref="stuFormRef" :rules="rules" class="dialog-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" :label-width="labelWidth" prop="name">
              <el-input v-model="stu.name" placeholder="请输入学员姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" :label-width="labelWidth" prop="no">
              <el-input v-model="stu.no" placeholder="请输入学员学号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别" :label-width="labelWidth" prop="gender">
              <el-select
                v-model="stu.gender"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option
                  v-for="gender in genders"
                  :key="gender.value"
                  :label="gender.name"
                  :value="gender.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" :label-width="labelWidth" prop="phone">
              <el-input v-model="stu.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item
              label="身份证号"
              :label-width="labelWidth"
              prop="idCard"
            >
              <el-input v-model="stu.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="是否院校"
              :label-width="labelWidth"
              prop="isCollege"
            >
              <el-select
                v-model="stu.isCollege"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option
                  v-for="w in whethers"
                  :key="w.value"
                  :label="w.name"
                  :value="w.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系地址" :label-width="labelWidth">
              <el-input v-model="stu.address" placeholder="请输入联系地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高学历" :label-width="labelWidth">
              <el-select
                v-model="stu.degree"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option
                  v-for="degree in degrees"
                  :key="degree.value"
                  :label="degree.name"
                  :value="degree.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="毕业时间" :label-width="labelWidth">
              <el-date-picker
                v-model="stu.graduationDate"
                type="date"
                placeholder="请选择毕业时间"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属班级" :label-width="labelWidth">
              <el-select
                v-model="stu.clazzId"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option
                  v-for="clazz in clazzs"
                  :key="clazz.id"
                  :label="clazz.name"
                  :value="clazz.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button
            class="dialog-cancel"
            @click="
              dialogFormVisible = false;
              resetForm(stuFormRef);
            "
            >取消</el-button
          >
          <el-button class="dialog-save" @click="save(stuFormRef)"
            >保存</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* ① 页面容器：无卡片边框，四周增加留白 */
.stu-page {
  padding: 8px 8px 32px;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI",
    "PingFang SC", "Microsoft YaHei", sans-serif;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

/* ② 查询区域：统一行布局，用 gap 拉开间距，仅留一条极淡分隔线 */
.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 28px;
  padding-bottom: 20px;
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0ec;
}

.filter-form :deep(.el-form-item) {
  margin: 0;
}

.filter-form :deep(.el-form-item__label) {
  width: 68px;
  padding-right: 12px;
  justify-content: flex-start;
  font-size: 13px;
  font-weight: 400;
  color: #86868b;
}

/* 输入项固定宽度，保证三个筛选项视觉对齐 */
.filter-form :deep(.el-input),
.filter-form :deep(.el-select) {
  width: 200px;
}

.filter-actions {
  margin-left: 4px;
}

.filter-form :deep(.el-input__wrapper),
.dialog-form :deep(.el-input__wrapper) {
  padding: 1px 12px;
  background: #fafaf8;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
  transition: background-color 0.15s, box-shadow 0.15s;
}

.filter-form :deep(.el-input__wrapper:hover),
.dialog-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #dcdcd6 inset;
}

.filter-form :deep(.el-input__wrapper.is-focus),
.dialog-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: 0 0 0 1px #1d1d1f inset;
}

.filter-form :deep(.el-input__inner),
.dialog-form :deep(.el-input__inner) {
  height: 34px;
  font-size: 13px;
  color: #1d1d1f;
}

.filter-form :deep(.el-input__inner::placeholder),
.dialog-form :deep(.el-input__inner::placeholder) {
  color: #a1a1a6;
}

.filter-submit.el-button {
  height: 36px;
  padding: 0 18px;
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

.filter-reset.el-button {
  height: 36px;
  margin-left: 4px;
  padding: 0 10px;
  border-radius: 8px;
  font-size: 13px;
  color: #6e6e73;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-hover-bg-color: #efefeb;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #e8e8e3;
  --el-button-active-border-color: transparent;
}

/* ③ 操作按钮：低对比度浅底，保留图标 + 文字 */
.toolbar {
  display: flex;
  gap: 8px;
  margin: 0 0 16px;
}

.tool-btn.el-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 34px;
  margin-left: 0;
  padding: 0 12px;
  border-radius: 8px;
  font-size: 13px;
  --el-button-bg-color: #f4f4f0;
  --el-button-border-color: transparent;
  --el-button-text-color: #4a4a4d;
  --el-button-hover-bg-color: #ecece8;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #e5e5e0;
  --el-button-active-border-color: transparent;
}

.tool-btn-danger.el-button {
  --el-button-bg-color: #f7f1ef;
  --el-button-text-color: #8a625c;
  --el-button-hover-bg-color: #f1e8e5;
  --el-button-hover-text-color: #7c4f48;
  --el-button-active-bg-color: #ebdfdb;
}

/* ④ 表格：无竖线、表头无底色、行底细线、极淡 hover */
.stu-table {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-row-hover-bg-color: #fafaf7;
  --el-table-header-text-color: #8a8a8f;
  --el-table-text-color: #3d3d40;
  --el-table-border-color: transparent;
  font-size: 13px;
}

.stu-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.stu-table :deep(th.el-table__cell) {
  padding: 10px 0;
  background: transparent;
  border-right: 0;
  border-bottom: 1px solid #e9e9e4;
  color: #8a8a8f;
  font-weight: 500;
}

.stu-table :deep(td.el-table__cell) {
  padding: 11px 0;
  background: transparent;
  border-right: 0;
  border-bottom: 1px solid #f2f2ee;
}

.stu-table :deep(.el-table__body tr:last-child td.el-table__cell) {
  border-bottom: 0;
}

.stu-table :deep(.el-table__body tr:hover > td.el-table__cell) {
  background: #fafaf7;
}

/* 次要字段：降低文字透明度，视觉降级 */
.stu-table :deep(td.cell-muted) {
  color: rgba(29, 29, 31, 0.42);
}

.stu-table :deep(td.cell-muted .cell) {
  font-variant-numeric: tabular-nums;
}

.stu-table :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.stu-table :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: #1d1d1f;
  border-color: #1d1d1f;
}

.stu-table :deep(.el-table__empty-text) {
  color: #9a9a9f;
  font-size: 13px;
}

.row-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
}

.row-action.el-button {
  height: auto;
  margin-left: 0;
  padding: 2px 0;
  border-radius: 0;
  font-size: 13px;
  color: #6e6e73;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-hover-bg-color: transparent;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: transparent;
}

.row-action-danger.el-button {
  color: #9a6a63;
  --el-button-hover-text-color: #7c4f48;
}

/* ⑤ 分页：轻量化，无描边按钮 */
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.stu-pagination {
  --el-pagination-bg-color: transparent;
  --el-pagination-button-bg-color: transparent;
  --el-pagination-hover-color: #1d1d1f;
  --el-pagination-font-size: 13px;
  font-size: 13px;
  color: #6e6e73;
}

.stu-pagination :deep(.el-pagination__total),
.stu-pagination :deep(.el-pagination__jump) {
  font-size: 12.5px;
  color: #a1a1a6;
}

.stu-pagination :deep(.el-pager li) {
  min-width: 28px;
  height: 28px;
  margin: 0 2px;
  border-radius: 6px;
  background: transparent;
  font-weight: 400;
}

.stu-pagination :deep(.el-pager li:hover) {
  background: #f2f2ee;
}

.stu-pagination :deep(.el-pager li.is-active) {
  background: #e7e7e2;
  color: #1d1d1f;
  font-weight: 500;
}

.stu-pagination :deep(.btn-prev),
.stu-pagination :deep(.btn-next) {
  height: 28px;
  background: transparent;
}

.stu-pagination :deep(.el-pagination__sizes .el-select .el-input__wrapper) {
  padding: 0 8px;
  background: transparent;
  box-shadow: none;
}

.stu-pagination :deep(.el-pagination__jump .el-input__wrapper) {
  padding: 0 8px;
  background: transparent;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
}

/* ⑥ 弹窗：无厚重阴影，仅留 1px 细描边与大圆角 */
.stu-page :deep(.el-overlay) {
  background-color: rgb(245 245 242 / 82%);
}

.stu-page :deep(.el-dialog) {
  --el-dialog-padding-primary: 24px;
  --el-dialog-box-shadow: none;
  border: 1px solid rgb(29 29 31 / 8%);
  border-radius: 14px;
  box-shadow: none;
}

.stu-page :deep(.el-dialog__header) {
  padding-bottom: 8px;
  margin-right: 0;
}

.stu-page :deep(.el-dialog__title) {
  font-size: 17px;
  font-weight: 600;
  color: #1d1d1f;
}

.stu-page :deep(.el-dialog__body) {
  padding-top: 20px;
  padding-bottom: 8px;
}

.stu-page :deep(.el-dialog__footer) {
  padding-top: 8px;
}

.dialog-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.dialog-form :deep(.el-form-item__label) {
  font-size: 13px;
  color: #6e6e73;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.dialog-footer :deep(.el-button) {
  height: 36px;
  margin-left: 0;
  padding: 0 18px;
  border-radius: 8px;
  font-size: 13px;
}

.dialog-cancel.el-button {
  --el-button-bg-color: transparent;
  --el-button-border-color: #e4e4df;
  --el-button-text-color: #6e6e73;
  --el-button-hover-bg-color: #f4f4f0;
  --el-button-hover-border-color: #dcdcd6;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #ecece7;
}

.dialog-save.el-button {
  --el-button-bg-color: #1d1d1f;
  --el-button-border-color: #1d1d1f;
  --el-button-text-color: #fff;
  --el-button-hover-bg-color: #333336;
  --el-button-hover-border-color: #333336;
  --el-button-hover-text-color: #fff;
  --el-button-active-bg-color: #000;
  --el-button-active-border-color: #000;
}
</style>
