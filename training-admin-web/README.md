# 教育培训管理系统 · 前端

基于 Vue 3 + Vite + Element Plus 的教育培训管理系统前端工程，对接同仓库的 Spring Boot 后端服务。

## 技术栈

| 分类 | 技术 |
| --- | --- |
| 框架 | Vue 3（Composition API） |
| 构建工具 | Vite 3 |
| UI 组件 | Element Plus |
| 路由 | Vue Router 4 |
| 状态管理 | Pinia |
| HTTP | Axios |
| 图表 | ECharts |

## 功能模块

- 登录与 JWT 鉴权、路由守卫
- 部门管理
- 员工管理（含工作经历）
- 班级管理（开课/结课状态）
- 学员管理与违纪处理
- 员工、学员数据统计图表
- 操作日志查询

## 本地运行

1. 先启动后端服务（默认 `http://localhost:8081`）。
2. 安装依赖并启动开发服务器：

```bash
npm install
npm run dev
```

开发服务器默认地址为 `http://localhost:5173`，`/api` 请求会通过 Vite 代理转发到后端，无需额外配置 CORS。

## 构建

```bash
npm run build
```

构建产物输出到 `dist/`，可通过 Nginx 等静态服务器部署，并把 `/api` 反向代理到后端。

## 目录结构

```text
src/
├── api/          # 后端接口封装
├── assets/       # 静态资源
├── router/       # 路由与登录守卫
├── utils/        # axios 实例与拦截器
└── views/        # 登录、首页及各业务页面
```
