<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

let router = useRouter();

const loginName = ref("");
//定义钩子函数, 获取登录用户名
onMounted(() => {
  //获取登录用户名
  let loginUser = JSON.parse(localStorage.getItem("loginUser"));
  if (loginUser) {
    loginName.value = loginUser.name;
  }
});

const logout = () => {
  //弹出确认框, 如果确认, 则退出登录, 跳转到登录页面
  ElMessageBox.confirm("确认退出登录吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    //确认, 则清空登录信息
    ElMessage.success("退出登录成功");
    localStorage.removeItem("loginUser");
    router.push("/login"); //跳转到登录页面
  });
};
</script>

<template>
  <div class="common-layout">
    <el-container class="app-shell">
      <el-header class="header" height="64px">
        <span class="brand">教育培训管理系统</span>

        <div class="user-area">
          <span class="user-avatar" aria-hidden="true">
            <el-icon><User /></el-icon>
          </span>
          <span class="user-name">{{ loginName }}</span>
          <el-button class="logout" text @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-container class="app-body">
        <el-aside width="224px" class="aside">
          <el-menu router :default-active="$route.path" class="nav-menu">
            <el-menu-item index="/index">
              <el-icon><Promotion /></el-icon>
              <span>首页</span>
            </el-menu-item>

            <el-sub-menu index="/manage">
              <template #title>
                <el-icon><Menu /></el-icon>
                <span>班级学员管理</span>
              </template>
              <el-menu-item index="/clazz">
                <el-icon><HomeFilled /></el-icon>
                <span>班级管理</span>
              </el-menu-item>
              <el-menu-item index="/stu">
                <el-icon><UserFilled /></el-icon>
                <span>学员管理</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/system">
              <template #title>
                <el-icon><Tools /></el-icon>
                <span>系统信息管理</span>
              </template>
              <el-menu-item index="/dept">
                <el-icon><HelpFilled /></el-icon>
                <span>部门管理</span>
              </el-menu-item>
              <el-menu-item index="/emp">
                <el-icon><Avatar /></el-icon>
                <span>员工管理</span>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="/report">
              <template #title>
                <el-icon><Histogram /></el-icon>
                <span>数据统计管理</span>
              </template>
              <el-menu-item index="/report/emp">
                <el-icon><InfoFilled /></el-icon>
                <span>员工信息统计</span>
              </el-menu-item>
              <el-menu-item index="/report/stu">
                <el-icon><Share /></el-icon>
                <span>学员信息统计</span>
              </el-menu-item>
              <el-menu-item index="/log">
                <el-icon><Document /></el-icon>
                <span>日志信息统计</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>

        <el-main class="main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout {
  height: 100vh;
}

.app-shell {
  height: 100%;
  background: #f5f5f2;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI",
    "PingFang SC", "Microsoft YaHei", sans-serif;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: transparent;
}

.brand {
  font-size: 15px;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  display: grid;
  width: 28px;
  height: 28px;
  place-items: center;
  border-radius: 50%;
  background: #e9e9e4;
  color: #6e6e73;
  font-size: 14px;
}

.user-name {
  max-width: 140px;
  overflow: hidden;
  font-size: 13px;
  line-height: 1.4;
  color: #48484a;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout.el-button {
  height: 30px;
  margin-left: 4px;
  padding: 0 8px;
  border-radius: 7px;
  font-size: 13px;
  color: #6e6e73;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-hover-bg-color: #ecece8;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #e5e5e0;
  --el-button-active-border-color: transparent;
}

.app-body {
  min-height: 0;
  padding: 0 16px 16px 8px;
}

.aside {
  overflow-y: auto;
  background: transparent;
}

.nav-menu {
  padding: 0 4px 8px;
  border-right: 0;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #5f6066;
  --el-menu-active-color: #1d1d1f;
  --el-menu-hover-bg-color: #ecece8;
  --el-menu-item-height: 38px;
  --el-menu-sub-item-height: 36px;
}

.nav-menu :deep(.el-menu-item),
.nav-menu :deep(.el-sub-menu__title) {
  margin: 2px 0;
  border-radius: 8px;
  font-size: 13.5px;
}

.nav-menu :deep(.el-menu-item > .el-icon),
.nav-menu :deep(.el-sub-menu__title > .el-icon:not(.el-sub-menu__icon-arrow)) {
  margin-right: 10px;
  font-size: 16px;
  color: #86868b;
}

.nav-menu :deep(.el-menu-item.is-active) {
  background: #e7e7e2;
  font-weight: 500;
}

.nav-menu :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #1d1d1f;
}

.nav-menu :deep(.el-menu-item.is-active > .el-icon) {
  color: #1d1d1f;
}

.main {
  padding: 24px;
  overflow-y: auto;
  background: #fff;
  border: 1px solid rgb(29 29 31 / 6%);
  border-radius: 14px;
}
</style>
