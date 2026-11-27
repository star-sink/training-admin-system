<script setup>
import { ref } from 'vue'
import { loginApi } from '@/api/login'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

let loginForm = ref({username:'', password:''})
let router = useRouter()

//登录
const login = async () => {
  const result = await loginApi(loginForm.value)
  if (result.code) {// 登录成功
    ElMessage.success('登录成功')
    localStorage.setItem('loginUser', JSON.stringify(result.data))
    router.push('/')// 跳转
  }else {
    ElMessage.error(result.msg)
  }
}

//取消
const cancel = () => {
  loginForm.value = {
    username: '',
    password: ''
  }
}

</script>

<template>
  <main class="login-page">
    <section class="login-panel" aria-labelledby="login-title">
      <span class="brand">教育培训管理系统</span>
      <h1 id="login-title" class="title">登录</h1>

      <el-form class="login-form" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            autocomplete="username"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            autocomplete="current-password"
          />
        </el-form-item>

        <div class="actions">
          <el-button class="submit" type="primary" size="large" @click="login">登录</el-button>
          <el-button class="reset" size="large" @click="cancel">重置</el-button>
        </div>
      </el-form>
    </section>
  </main>
</template>

<style scoped>
.login-page {
  box-sizing: border-box;
  display: grid;
  min-height: 100vh;
  padding: 24px;
  place-items: center;
  background: #f5f5f2;
  color: #1d1d1f;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Segoe UI',
    'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.login-panel {
  width: 100%;
  max-width: 380px;
  padding: 40px 36px;
  background: #fff;
  border: 1px solid rgb(29 29 31 / 6%);
  border-radius: 14px;
}

.brand {
  display: block;
  font-size: 13px;
  line-height: 1.4;
  color: #86868b;
}

.title {
  margin: 16px 0 32px;
  font-size: 28px;
  font-weight: 600;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item__label) {
  padding: 0 0 8px;
  font-size: 13px;
  font-weight: 500;
  line-height: 1.4;
  color: #48484a;
}

.login-form :deep(.el-input__wrapper) {
  padding: 2px 12px;
  background: #fafaf8;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e8e8e3 inset;
  transition: background-color 0.15s, box-shadow 0.15s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #dcdcd6 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: 0 0 0 1px #1d1d1f inset;
}

.login-form :deep(.el-input__inner) {
  height: 42px;
  font-size: 14px;
  color: #1d1d1f;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #a1a1a6;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 28px;
}

.submit.el-button {
  flex: 1;
  height: 44px;
  border-radius: 10px;
  font-size: 14px;
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

.reset.el-button {
  width: 84px;
  height: 44px;
  margin-left: 0;
  border-radius: 10px;
  font-size: 14px;
  color: #6e6e73;
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-hover-bg-color: #f4f4f0;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: #1d1d1f;
  --el-button-active-bg-color: #ecece7;
  --el-button-active-border-color: transparent;
}

@media (max-width: 420px) {
  .login-page {
    padding: 16px;
  }

  .login-panel {
    padding: 32px 24px;
  }
}
</style>