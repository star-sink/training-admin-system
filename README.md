# 教育培训管理系统（后端）

## 1. 📖 项目简介

教育培训管理系统是一套面向培训机构教务场景的后端服务，围绕“部门—员工—班级—学员”主线提供人员与教学组织管理能力，并结合 JWT 认证与 AOP 操作日志实现安全访问和操作审计。

系统采用前后端分离架构，本仓库仅包含 Spring Boot 后端服务，不包含 Vue 前端工程。核心业务模块包括：

- **部门管理**：部门的查询、新增、修改与删除。
- **员工管理**：员工分页与条件查询、新增、修改、删除，工作经历随员工信息一并维护。
- **班级管理**：班级分页与条件查询、新增、修改、删除，按开课/结课日期实时计算班级状态。
- **学员管理**：学员分页与条件查询、新增、修改、删除，以及违纪次数与扣分处理。
- **认证鉴权**：登录成功后签发 JWT，后续请求通过请求头 `token` 访问受保护接口。
- **操作审计**：自定义 `@Log` 注解结合 AOP，将增删改操作记录到 `operate_log` 表。
- **报表统计与文件上传**：提供员工、学员统计图表数据，并支持头像等文件上传至阿里云 OSS。

## 2. 🛠 技术栈

| 分类 | 技术 |
| --- | --- |
| 开发语言 | Java 21 |
| 后端框架 | Spring Boot 4.0.8、Spring MVC、Spring AOP（AspectJ） |
| 持久层 | MyBatis（mybatis-spring-boot-starter 4.0.1）、PageHelper 1.4.7 |
| 数据库 | MySQL 8、JDBC、HikariCP |
| 认证 | JJWT 0.12.6（JWT 生成与校验） |
| JSON | Jackson（`tools.jackson`） |
| 文件存储 | 阿里云 OSS SDK V2 0.6.0 |
| 日志 | SLF4J、Logback、MyBatis 标准输出 |
| 构建与工具 | Maven、Lombok |
| 前端 | Vue（独立工程，本仓库不包含） |

## 3. 📁 目录结构

```text
web-ai-project02/
├── README.md
├── training.sql
├── .gitignore
└── tlias-web-management/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/trainingadmin/
        │   │   ├── TliasWebManagementApplication.java
        │   │   ├── anno/Log.java
        │   │   ├── aop/OperateLogAspect.java
        │   │   ├── config/WebConfig.java
        │   │   ├── controller/
        │   │   │   ├── LoginController.java
        │   │   │   ├── DeptController.java
        │   │   │   ├── EmpController.java
        │   │   │   ├── ClassController.java
        │   │   │   ├── StudentController.java
        │   │   │   ├── ReportController.java
        │   │   │   ├── LogController.java
        │   │   │   ├── UploadController.java
        │   │   │   └── SessionController.java
        │   │   ├── service/
        │   │   │   ├── DeptService.java
        │   │   │   ├── EmpService.java
        │   │   │   ├── ClassService.java
        │   │   │   ├── StudentService.java
        │   │   │   ├── ReportService.java
        │   │   │   ├── LogService.java
        │   │   │   ├── EmpLogService.java
        │   │   │   └── impl/
        │   │   ├── mapper/
        │   │   │   ├── DeptMapper.java
        │   │   │   ├── EmpMapper.java
        │   │   │   ├── EmpExprMapper.java
        │   │   │   ├── ClassMapper.java
        │   │   │   ├── StudentMapper.java
        │   │   │   ├── LogMapper.java
        │   │   │   ├── EmpLogMapper.java
        │   │   │   └── OperateLogMapper.java
        │   │   ├── pojo/
        │   │   ├── exception/
        │   │   ├── filter/
        │   │   ├── interceptor/
        │   │   └── utils/
        │   └── resources/
        │       ├── application.yml
        │       ├── logback.xml
        │       ├── com/trainingadmin/mapper/
        │       │   ├── EmpMapper.xml
        │       │   ├── EmpExprMapper.xml
        │       │   ├── ClassMapper.xml
        │       │   ├── StudentMapper.xml
        │       │   └── LogMapper.xml
        │       └── static/upload.html
        └── test/java/com/trainingadmin/
            ├── TliasWebManagementApplicationTests.java
            ├── JwtTest.java
            ├── LogTest.java
            ├── ThreadLocalTest.java
            ├── UUIDTest.java
            └── Demo.java
```

说明：`target/`、`.idea/` 等构建产物与 IDE 配置目录未列入目录树。

## 4. ✨ 功能特性

### 认证与鉴权

- 员工使用用户名、密码登录，服务端签发有效期 12 小时的 JWT。
- 除 `/login` 外，所有接口统一经过 `TokenInterceptor` 校验请求头 `token`；令牌缺失或非法返回 HTTP 401。
- 校验通过后将当前员工 ID 写入 `ThreadLocal`，请求结束后自动清理，避免线程复用导致操作人串号。

### 基础数据管理

- 部门、员工、班级、学员均提供分页查询、条件筛选、新增、修改与删除能力。
- 员工支持工作经历的一对多维护；新增、修改、批量删除均在事务中完成。
- 班级根据当前日期与开课/结课日期实时计算“未开班 / 已开班 / 已结课”状态。
- 学员违纪处理在 SQL 中原子累加违纪次数与扣分，避免并发覆盖。

### 分页与查询

- 使用 PageHelper 实现物理分页，统一返回 `PageResult { total, rows }`。
- Mapper 使用动态 SQL 支持按名称、性别、学历、班级、时间区间等条件组合查询。

### 操作审计

- 通过自定义 `@Log` 注解标记增删改接口，`OperateLogAspect` 以环绕通知记录操作人、时间、类名、方法名、参数、返回值与耗时。
- 审计日志写入 `operate_log` 表；超长文本按字段长度截断，日志入库失败不影响主业务。
- `/log/page` 支持分页查看操作日志，并关联员工表返回操作人姓名。

### 报表与文件上传

- 提供员工职位分布、员工性别分布、学员学历分布、各班级人数四类统计数据。
- 文件上传至阿里云 OSS，按 `yyyy/MM/UUID.扩展名` 生成对象路径并返回访问 URL。

## 5. 🚀 快速开始

### 5.1 环境准备

| 环境 | 版本要求 |
| --- | --- |
| JDK | 21 |
| Maven | 3.9+ |
| MySQL | 8.0+ |

```bash
java -version
mvn -v
mysql --version
```

### 5.2 初始化数据库

项目根目录的 `training.sql` 包含建库、建表语句及测试数据。执行前请注意：

- 脚本会先执行 `DROP TABLE IF EXISTS` 再重建表，已有数据的环境请先备份。
- 脚本当前创建并使用的数据库名为 `training`，请打开脚本首行确认建库名称，并保证后续 `application.yml` 中的数据库名与之一致。

```bash
mysql --default-character-set=utf8mb4 -uroot -p < training.sql
```

脚本内置测试账号：`zhangsan`、`lisi`、`wangwu`，密码均为 `123456`。

### 5.3 修改配置

根据本机环境调整 `tlias-web-management/src/main/resources/application.yml` 中的数据库连接信息：

| 配置项 | 说明 |
| --- | --- |
| `spring.datasource.url` | MySQL 地址及库名，库名需与 `training.sql` 创建的库名一致 |
| `spring.datasource.username` | 数据库用户名 |
| `spring.datasource.password` | 数据库密码 |

文件上传功能依赖阿里云 OSS。使用 `/upload` 前，需要配置 `aliyun.oss.endpoint`、`aliyun.oss.bucketName`、`aliyun.oss.region`，并设置凭证环境变量：

```bash
export OSS_ACCESS_KEY_ID="你的 AccessKey ID"
export OSS_ACCESS_KEY_SECRET="你的 AccessKey Secret"
```

PowerShell：

```powershell
$env:OSS_ACCESS_KEY_ID = "你的 AccessKey ID"
$env:OSS_ACCESS_KEY_SECRET = "你的 AccessKey Secret"
```

仅在使用 `/upload` 时需要有效的 OSS 配置与访问凭证。

### 5.4 启动项目

在项目根目录执行：

```bash
cd tlias-web-management
mvn spring-boot:run
```

服务启动后监听 `http://localhost:8081`。

也可以打包后运行：

```bash
mvn clean package -DskipTests
java -jar target/training-admin-system-0.0.1-SNAPSHOT.jar
```

### 5.5 测试验证

使用 `training.sql` 内置的 `zhangsan` 账号登录：

```bash
curl -s -X POST "http://localhost:8081/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'
```

返回示例：

```json
{"code":1,"msg":"success","data":{"id":1,"username":"zhangsan","name":"张三","token":"eyJ..."}}
```

携带返回的 `token` 访问受保护接口：

```bash
TOKEN="把上一步返回的 token 粘贴到这里"
curl -s "http://localhost:8081/depts" -H "token: $TOKEN"
```

## 6. 📝 接口文档

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
| GET | `/log/page?page=1&pageSize=10` | 操作日志分页查询，`page`、`pageSize` 必传 |
| POST | `/upload` | 文件上传至 OSS，表单字段名 `file` |

常用查询参数：

- 员工分页：`page`、`pageSize`、`name`、`gender`、`begin`、`end`。
- 班级分页：`page`、`pageSize`、`name`、`begin`、`end`。
- 学员分页：`page`、`pageSize`、`name`、`degree`、`clazzId`。
- 操作日志分页：`page`、`pageSize`，两个参数均需传入。

报表接口返回结构：

- `GET /report/empJobData`：`{ jobList: [], dataList: [] }`。
- `GET /report/empGenderData`：`[{ name, value }]`。
- `GET /report/studentDegreeData`：`[{ name, value }]`。
- `GET /report/studentCountData`：`{ clazzList: [], dataList: [] }`。

## 7. 🧠 架构设计

### 分层结构

```text
客户端
  │  HTTP/JSON，请求头携带 token
  ▼
TokenInterceptor（JWT 校验 / 401 / 写入 ThreadLocal）
  ▼
Controller（参数绑定、统一返回 Result）
  ▼
Service（业务规则、事务、分页、状态计算）
  ▼
Mapper（MyBatis XML / 注解 SQL）
  ▼
MySQL（training）
```

- **Controller**：负责请求接收、参数绑定与 `Result` 封装。
- **Service**：承载业务规则、事务控制、PageHelper 分页与班级状态计算。
- **Mapper**：通过 MyBatis 接口与 XML 完成数据访问，动态 SQL 处理条件查询与选择性更新。

### 认证与上下文传递

- 登录成功后由 `JwtUtils` 签发有效期 12 小时的 JWT，载荷包含员工 `id` 与 `username`。
- `TokenInterceptor` 拦截除 `/login` 外的全部请求，校验失败返回 HTTP 401。
- 校验通过后将员工 ID 写入 `CurrentHolder` 的 `ThreadLocal`，供 AOP 获取当前操作人；请求结束后在 `afterCompletion` 中清理。

### 全局异常处理

`GlobaExceptionHandler` 使用 `@RestControllerAdvice` 统一处理异常：

- `BusinessException`：返回具体业务提示。
- `DuplicateKeyException`：解析唯一键冲突并返回“xxx已存在”。
- 其他异常：记录完整日志，对外返回统一提示，避免暴露内部细节。

### AOP 操作审计

- `@Log` 标记需要审计的增删改接口，`OperateLogAspect` 通过 `@Around` 环绕通知记录操作人、操作时间、类名、方法名、参数、返回值与耗时。
- 记录写入 `operate_log` 表，`/log/page` 通过 `LogMapper` 分页查询该表，并关联 `emp` 表展示操作人姓名。
- 参数与返回值序列化为 JSON 文本；超过 2000 字符自动截断；日志写入异常只打印错误，不影响业务结果。

### 事务、分页与数据一致性

- 员工新增、修改、删除使用事务，保证 `emp` 与 `emp_expr` 两张表的数据一致。
- 修改员工时采用“先删除旧工作经历、再批量插入新经历”的策略。
- PageHelper 负责物理分页，Service 统一封装 `PageResult`。
- 学员违纪处理在 SQL 中直接执行次数加一、扣分累加，保证并发场景下的原子性。

### 云存储生命周期

- `AliyunOSSOperator` 在 `@PostConstruct` 中初始化 OSS 客户端，在 `@PreDestroy` 中关闭。
- 访问凭证通过环境变量注入，上传对象按年月目录与 UUID 命名，避免文件名冲突。

> 说明：本项目为教学示例，密码采用明文存储与比对。
