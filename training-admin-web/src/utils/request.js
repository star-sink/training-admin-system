import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";

// 创建 axios 实例：开发环境通过 Vite 代理把 /api 转发到后端
const request = axios.create({
  baseURL: "/api",
  timeout: 15000,
});

// 请求拦截器：从本地存储读取登录信息，自动携带 token
request.interceptors.request.use((config) => {
  const raw = localStorage.getItem("loginUser");
  if (raw) {
    try {
      const loginUser = JSON.parse(raw);
      if (loginUser?.token) {
        config.headers.token = loginUser.token;
      }
    } catch {
      localStorage.removeItem("loginUser");
    }
  }
  return config;
});

// 响应拦截器：直接返回业务数据；401 时跳转登录页
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      ElMessage.error("登录已失效，请重新登录");
      router.push("/login");
    } else if (!error.response) {
      ElMessage.error("网络异常，请稍后重试");
    }
    return Promise.reject(error);
  }
);

export default request;
