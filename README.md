# 教育培训管理系统

一套面向培训机构教务场景的前后端分离管理系统，围绕 **部门 — 员工 — 班级 — 学员** 主线提供人员与教学组织管理能力，并结合 JWT 认证、拦截器鉴权与 AOP 操作审计实现登录校验和操作留痕。

- **后端**：`training-admin-system`（Spring Boot 4 + MyBatis + MySQL）
- **前端**：`training-admin-web`（Vue 3 + Vite + Element Plus）
- **数据库脚本**：`training.sql`

## 1. 功能特性

### 认证与鉴权

- 用户名 / 密码登录，按用户名和密码直接查询员工（Tlias 原版演示写法）。
- 登录成功后签发 JWT，除 `/login` 外的接口统一通过拦截器校验请求头 `token`，非法或过期返回 HTTP 401。
- 操作日志切面在记录日志时从请求头 `token` 解析当前员工 ID，不使用 `ThreadLocal` 传递上下文。

### 基础数据管理

- **部门管理**：查询、新增、修改、删除。
- **员工管理**：分页与条件查询、新增、修改、批量删除，工作经历随员工信息一并维护。
- **班级管理**：分页与条件查询、新增、修改、删除，根据当前日期与开课/结课日期实时计算班级状态。
- **学员管理**：分页与条件查询、新增、修改、删除，以及违纪次数与扣分处理。

### 分页与统计

- 使用 PageHelper 实现物理分页，统一返回 `PageResult { total, rows }`。
- Mapper 使用动态 SQL 支持名称、性别、学历、班级、时间区间等条件组合查询。
- 提供员工职位分布、员工性别分布、学员学历分布、各班级人数四类图表数据。

### 操作审计

- 自定义 `@Log` 注解标记增删改接口，`OperateLogAspect` 通过环绕通知记录操作人、时间、类名、方法名、参数、返回值与耗时。
- 审计日志写入 `operate_log` 表，参数与返回值超长时自动截断；日志写入失败不影响主业务。
- `/log/page` 分页查看操作日志，并关联员工表展示操作人姓名。
- 操作人从请求头 `token` 解析得到，无需 ThreadLocal 上下文。

### 文件上传

- 支持头像等图片上传至阿里云 OSS，按 `yyyy/MM/UUID.扩展名` 生成对象路径并返回访问 URL。
- OSS 的 endpoint/bucketName/region 在 application.yml 配置，AccessKey 通过环境变量注入；未配置完整时应用仍可正常启动，仅上传接口不可用。

## 2. 技术栈

### 后端

| 分类 | 技术 |
| --- | --- |
| 开发语言 | Java 21 |
| 框架 | Spring Boot 4、Spring MVC、Spring AOP（AspectJ） |
| 持久层 | MyBatis、PageHelper 1.4.7 |
| 数据库 | MySQL 8、HikariCP |
| 认证 | JJWT 0.12.6 |
| 文件存储 | 阿里云 OSS SDK V2 |
| 构建工具 | Maven、Lombok |

### 前端

| 分类 | 技术 |
| --- | --- |
| 框架 | Vue 3（Composition API） |
| 构建工具 | Vite 3 |
| UI 组件 | Element Plus |
| 路由与状态 | Vue Router 4、Pinia |
| HTTP | Axios |
| 图表 | ECharts |

## 3. 项目结构

```text
web-ai-project02/
├── README.md
├── training.sql                     # 建库建表 + 测试数据
├── training-admin-system/           # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/trainingadmin/
│       │   ├── TrainingAdminApplication.java
│       │   ├── anno/                # 自定义 @Log 注解
│       │   ├── aop/                 # 操作日志切面
│       │   ├── config/              # Web 配置、拦截器注册
│       │   ├── controller/          # REST 接口
│       │   ├── exception/           # 业务异常与全局异常处理
│       │   ├── interceptor/         # JWT 登录拦截器
│       │   ├── mapper/              # MyBatis Mapper 接口
│       │   ├── pojo/                # 实体与查询参数
│       │   ├── service/             # 业务接口与实现
│       │   └── utils/               # JWT、OSS 工具
│       └── resources/
│           ├── application.yml
│           ├── logback.xml
│           └── com/trainingadmin/mapper/   # MyBatis XML
└── training-admin-web/              # Vue 3 前端
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                     # 接口封装
        ├── router/                  # 路由与登录守卫
        ├── utils/                   # Axios 实例与拦截器
        └── views/                   # 登录、首页与业务页面
```

## 4. 快速开始

### 4.1 环境准备

| 环境 | 版本要求 |
| --- | --- |
| JDK | 21 |
| Maven | 3.9+ |
| MySQL | 8.0+ |
| Node.js | 18+ |

### 4.2 初始化数据库

```bash
mysql --default-character-set=utf8mb4 -uroot -p < training.sql
```

脚本会创建 `training` 数据库、7 张业务表并写入测试数据。注意：脚本包含 `DROP TABLE IF EXISTS`，已有数据的环境请先备份。

内置测试账号（密码均为 `123456`，数据库中保存的是明文密码，仅用于本地演示）：

| 用户名 | 姓名 |
| --- | --- |
| `zhangsan` | 张三 |
| `lisi` | 李四 |
| `wangwu` | 王五 |

### 4.3 配置后端

`training-admin-system/src/main/resources/application.yml` 中已经填写数据库与 OSS 的基础配置，启动前请确认：

| 配置项 | 位置 | 说明 |
| --- | --- | --- |
| 数据库地址 | `DB_URL` 环境变量或 `application.yml` | 默认 `jdbc:mysql://localhost:3306/training...` |
| 数据库用户名 / 密码 | `application.yml` 的 `spring.datasource` | 按本地 MySQL 填写 |
| OSS `endpoint` / `bucketName` / `region` | `application.yml` 的 `aliyun.oss` | 仅文件上传功能需要 |
| OSS AccessKey ID / Secret | 环境变量 `OSS_ACCESS_KEY_ID`、`OSS_ACCESS_KEY_SECRET` | 由 OSS SDK 读取 |

示例（PowerShell，仅覆盖仍支持环境变量的部分）：

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/training?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
$env:OSS_ACCESS_KEY_ID = "你的AccessKeyId"
$env:OSS_ACCESS_KEY_SECRET = "你的AccessKeySecret"
```

> `application.yml` 是被 Git 跟踪的文件，不要把真实的数据库密码、AccessKey 提交到仓库。团队协作或生产环境建议改用环境变量，或新建 `application-local.yml`（已在 `.gitignore` 中忽略）并通过 `--spring.profiles.active=local` 启用。

仅在使用 `/upload` 时需要完整的 OSS 配置；未配置 OSS 时应用仍可正常启动，只是上传接口不可用。

### 4.4 启动后端

```bash
cd training-admin-system
mvn spring-boot:run
```

服务启动后监听 `http://localhost:8081`。

打包运行：

```bash
mvn clean package -DskipTests
java -jar target/training-admin-system-0.0.1-SNAPSHOT.jar
```

### 4.5 启动前端

```bash
cd training-admin-web
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。Vite 已配置 `/api` 代理到 `http://localhost:8081`。

### 4.6 验证登录

```bash
curl -s -X POST "http://localhost:8081/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'
```

返回示例：

```json
{"code":1,"msg":"success","data":{"id":1,"username":"zhangsan","name":"张三","token":"eyJ..."}}
```

携带 token 访问受保护接口：

```bash
TOKEN="把上一步返回的 token 粘贴到这里"
curl -s "http://localhost:8081/depts" -H "token: $TOKEN"
```

## 5. 接口文档

完整的请求参数、请求示例与响应示例见 [API.md](API.md)。

除 `POST /login` 外，所有接口均需在请求头中携带 `token`。统一响应结构为 `Result { code, msg, data }`，`code = 1` 表示成功；分页数据位于 `data` 中的 `PageResult { total, rows }`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/login` | 员工登录，返回 JWT，无需令牌 |
| GET | `/depts` | 查询全部部门 |
| GET | `/depts/{id}` | 查询部门详情 |
| POST | `/depts` | 新增部门 |
| PUT | `/depts` | 修改部门 |
| DELETE | `/depts?id={id}` | 删除部门 |
| GET | `/emps` | 员工分页查询 |
| GET | `/emps/list` | 查询员工下拉列表 |
| GET | `/emps/{id}` | 查询员工详情（含工作经历） |
| POST | `/emps` | 新增员工 |
| PUT | `/emps` | 修改员工 |
| DELETE | `/emps?ids=1,2` | 批量删除员工 |
| GET | `/clazzs` | 班级分页查询 |
| GET | `/clazzs/list` | 查询班级下拉列表 |
| GET | `/clazzs/{id}` | 查询班级详情 |
| POST | `/clazzs` | 新增班级 |
| PUT | `/clazzs` | 修改班级 |
| DELETE | `/clazzs/{id}` | 删除班级 |
| GET | `/students` | 学员分页查询 |
| GET | `/students/{id}` | 查询学员详情 |
| POST | `/students` | 新增学员 |
| PUT | `/students` | 修改学员 |
| DELETE | `/students/{id}` | 删除学员 |
| PUT | `/students/violation/{id}/{score}` | 学员违纪处理 |
| GET | `/report/empJobData` | 员工职位分布 |
| GET | `/report/empGenderData` | 员工性别分布 |
| GET | `/report/studentDegreeData` | 学员学历分布 |
| GET | `/report/studentCountData` | 各班级人数统计 |
| GET | `/log/page?page=1&pageSize=10` | 操作日志分页查询，参数可选 |
| POST | `/upload` | 文件上传至 OSS，表单字段名 `file` |

常用查询参数：

- 员工分页：`page`、`pageSize`、`name`、`gender`、`begin`、`end`。
- 班级分页：`page`、`pageSize`、`name`、`begin`、`end`（按结课时间筛选）。
- 学员分页：`page`、`pageSize`、`name`、`degree`、`clazzId`。
- 操作日志分页：`page`、`pageSize`，默认 `1` / `10`。

报表接口返回结构：

- `GET /report/empJobData`：`{ jobList: [], dataList: [] }`。
- `GET /report/empGenderData`：`[{ name, value }]`。
- `GET /report/studentDegreeData`：`[{ name, value }]`。
- `GET /report/studentCountData`：`{ clazzList: [], dataList: [] }`。

## 6. 架构设计

### 分层结构

```text
浏览器（Vue 3 + Element Plus）
  │  HTTP/JSON，请求头携带 token
  ▼
TokenInterceptor（JWT 校验 / 401）
  ▼
Controller（参数绑定、统一返回 Result）
  ▼
Service（业务规则、事务、分页、状态计算）
  ▼
Mapper（MyBatis XML / 注解 SQL）
  ▼
MySQL（training）
```

### 认证与操作人获取

- 登录成功后由 `JwtUtils` 签发 JWT，载荷包含员工 `id` 与 `username`；密钥和有效期使用 `JwtUtils` 中的静态常量（Tlias 原版写法）。
- `TokenInterceptor` 拦截除 `/login` 外的全部请求，校验失败返回 HTTP 401。
- `OperateLogAspect` 通过 `RequestContextHolder` 获取当前请求，读取请求头 `token` 并用 `JwtUtils.parseJWT` 解析员工 ID，作为操作人写入审计日志。

### 密码说明

- 本项目按 Tlias 原版演示实现，员工密码以明文形式存储和比对，仅用于本地学习。
- 生产环境不应使用明文密码，建议后续引入 BCrypt 或 Spring Security。

### 全局异常处理

`GlobalExceptionHandler` 使用 `@RestControllerAdvice` 统一处理异常：

- `BusinessException`：返回具体业务提示。
- `DuplicateKeyException`：安全解析唯一键冲突并返回“xxx已存在”。
- 其他异常：记录完整日志，对外返回统一提示，避免暴露内部细节。

### AOP 操作审计

- `@Log` 标记需要审计的增删改接口，`OperateLogAspect` 通过 `@Around` 环绕通知记录操作人、操作时间、类名、方法名、参数、返回值与耗时。
- 记录写入 `operate_log` 表，`/log/page` 通过 `LogMapper` 分页查询该表，并关联 `emp` 表展示操作人姓名。
- 参数与返回值序列化为 JSON 文本；超过 2000 字符自动截断；日志写入异常只打印错误，不影响业务结果。

### 事务与数据一致性

- 员工新增、修改、删除使用事务，保证 `emp` 与 `emp_expr` 两张表的数据一致。
- 修改员工时采用“先删除旧工作经历、再批量插入新经历”的策略。
- 员工新增日志使用 `REQUIRES_NEW` 独立事务，主业务回滚时日志仍可保留。
- 学员违纪处理在 SQL 中直接执行次数加一、扣分累加，保证并发场景下的原子性。

### 云存储生命周期

- `AliyunOSSOperator` 在 `@PostConstruct` 中初始化 OSS 客户端，在 `@PreDestroy` 中关闭。
- 未配置 OSS 时跳过客户端初始化，不影响应用启动；上传接口会返回明确的业务提示。
- 上传对象按年月目录与 UUID 命名，避免文件名冲突。

## 7. 项目亮点（可用于简历）

- 基于 Spring Boot 4 + MyBatis 实现分层 REST 服务，使用 PageHelper 完成物理分页与动态条件查询。
- 基于 JWT + 拦截器实现无状态登录鉴权，操作日志切面从请求头解析当前操作人。
- 基于自定义注解 + Spring AOP 实现操作审计，横切关注点与业务代码解耦，日志入库失败不影响主流程。
- 登录按 Tlias 原版直接查询用户名和密码，结构简单，便于教学演示。
- 使用 `@Transactional` 保证员工与工作经历的多表一致性，通过 `REQUIRES_NEW` 实现独立日志事务。
- 前端基于 Vue 3 + Element Plus + Axios 拦截器 + 路由守卫实现前后端分离与登录态管理。
- OSS AccessKey 通过环境变量读取；数据库与 OSS 基础配置集中在 application.yml，请勿提交真实凭证。

## 8. 后续可优化方向

- 引入 Spring Security 与 RBAC，实现菜单/按钮级权限控制，并使用 BCrypt 存储密码。
- 补充 Service 层单元测试与接口集成测试。
- 增加参数校验（Bean Validation）与统一错误码。
- 使用 Docker Compose 一键启动 MySQL、后端与前端。
