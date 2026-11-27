# 教育培训管理系统 API 接口文档

> 版本：v1.0
> 更新时间：2026-11-27
> 适用范围：`training-admin-system`（Spring Boot 后端）

---

## 目录

- [1. 基础说明](#1-基础说明)
- [2. 接口总览](#2-接口总览)
- [3. 登录模块](#3-登录模块)
- [4. 部门管理](#4-部门管理)
- [5. 员工管理](#5-员工管理)
- [6. 班级管理](#6-班级管理)
- [7. 学员管理](#7-学员管理)
- [8. 报表统计](#8-报表统计)
- [9. 日志与文件上传](#9-日志与文件上传)
- [10. 附录](#10-附录)

---

## 1. 基础说明

### 1.1 Base URL

```text
http://localhost:8081
```

前端开发环境通过 Vite 代理访问：

```text
/api/**  →  http://localhost:8081/**
```

### 1.2 数据格式

| 项目 | 说明 |
| --- | --- |
| 请求格式 | `application/json`（文件上传为 `multipart/form-data`） |
| 响应格式 | `application/json; charset=UTF-8` |
| 日期格式 | 请求参数：`yyyy-MM-dd`；响应日期：`yyyy-MM-dd` |
| 日期时间格式 | 响应为 ISO-8601，例如 `2023-10-20T16:35:33` |
| 字符编码 | UTF-8 |

### 1.3 认证方式

除登录接口外，所有接口均需要在请求头中携带 JWT：

```http
token: <登录接口返回的 JWT 字符串>
```

说明：

- 请求头名称为 `token`，不是 `Authorization`，也不需要 `Bearer` 前缀。
- 令牌有效期默认 12 小时，可通过配置项 `jwt.expiration` 调整。
- 令牌缺失、非法或过期时，服务端返回 HTTP `401`，响应体为空。
- 登录接口 `/login` 不需要令牌。

### 1.4 统一响应结构

所有业务接口统一返回 `Result`：

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `code` | Integer | `1` 表示成功，`0` 表示失败 |
| `msg` | String | 成功时为 `success`，失败时为错误提示 |
| `data` | Object | 业务数据，可能是对象、数组、分页对象或 `null` |

> 业务失败通常仍返回 HTTP `200`，通过 `code = 0` 和 `msg` 区分；只有认证失败返回 HTTP `401`。

### 1.5 分页响应结构

分页接口的 `data` 为 `PageResult`：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 3,
    "rows": [
      {
        "id": 1,
        "name": "示例数据"
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `total` | Long | 符合条件的总记录数 |
| `rows` | Array | 当前页数据列表 |

### 1.6 通用错误响应

**业务错误（HTTP 200）**

```json
{
  "code": 0,
  "msg": "班级不存在，删除失败",
  "data": null
}
```

**未认证或令牌非法（HTTP 401，响应体为空）**

```http
HTTP/1.1 401 Unauthorized
```

**常见错误信息**

| 场景 | HTTP 状态 | 响应 `msg` 示例 |
| --- | --- | --- |
| 用户名或密码错误 | 200 | `用户名或密码错误` |
| 班级不存在 | 200 | `班级不存在，修改失败` |
| 唯一键冲突 | 200 | `JavaEE就业163期已存在` |
| 文件类型不支持 | 200 | `仅支持 jpg、jpeg、png、gif、webp 格式的图片` |
| 未配置 OSS | 200 | `文件上传服务未配置，请联系管理员` |
| 未携带或令牌非法 | 401 | 无响应体 |
| 未预见的系统异常 | 200 | `出错啦，请联系管理员` |

### 1.7 公共枚举说明

| 枚举 | 取值 |
| --- | --- |
| 性别 `gender` | `1`：男，`2`：女 |
| 员工职位 `job` | `1`：班主任，`2`：讲师，`3`：学工主管，`4`：教研主管，`5`：咨询师，其他值统计为“其他” |
| 班级学科 `subject` | `1`：Java，`2`：前端，`3`：大数据，`4`：Python，`5`：Go，`6`：嵌入式 |
| 班级状态 `status` | `未开班`、`已开班`、`已结课`（根据当前日期实时计算，只读） |
| 学员学历 `degree` | `1`：初中，`2`：高中，`3`：大专，`4`：本科，`5`：硕士，`6`：博士 |
| 是否院校学员 `isCollege` | `1`：是，`0`：否 |

---

## 2. 接口总览

| 模块 | 方法 | 路径 | 说明 | Token |
| --- | --- | --- | --- | --- |
| 登录 | POST | `/login` | 员工登录 | 否 |
| 部门 | GET | `/depts` | 查询全部部门 | 是 |
| 部门 | GET | `/depts/{id}` | 查询部门详情 | 是 |
| 部门 | POST | `/depts` | 新增部门 | 是 |
| 部门 | PUT | `/depts` | 修改部门 | 是 |
| 部门 | DELETE | `/depts?id={id}` | 删除部门 | 是 |
| 员工 | GET | `/emps` | 分页条件查询员工 | 是 |
| 员工 | GET | `/emps/list` | 查询全部员工 | 是 |
| 员工 | GET | `/emps/{id}` | 查询员工详情（含工作经历） | 是 |
| 员工 | POST | `/emps` | 新增员工 | 是 |
| 员工 | PUT | `/emps` | 修改员工 | 是 |
| 员工 | DELETE | `/emps?ids={ids}` | 批量删除员工 | 是 |
| 班级 | GET | `/clazzs` | 分页条件查询班级 | 是 |
| 班级 | GET | `/clazzs/list` | 查询全部班级 | 是 |
| 班级 | GET | `/clazzs/{id}` | 查询班级详情 | 是 |
| 班级 | POST | `/clazzs` | 新增班级 | 是 |
| 班级 | PUT | `/clazzs` | 修改班级 | 是 |
| 班级 | DELETE | `/clazzs/{id}` | 删除班级 | 是 |
| 学员 | GET | `/students` | 分页条件查询学员 | 是 |
| 学员 | GET | `/students/{id}` | 查询学员详情 | 是 |
| 学员 | POST | `/students` | 新增学员 | 是 |
| 学员 | PUT | `/students` | 修改学员 | 是 |
| 学员 | DELETE | `/students/{id}` | 删除学员 | 是 |
| 学员 | PUT | `/students/violation/{id}/{score}` | 学员违纪处理 | 是 |
| 报表 | GET | `/report/empJobData` | 员工职位分布 | 是 |
| 报表 | GET | `/report/empGenderData` | 员工性别分布 | 是 |
| 报表 | GET | `/report/studentDegreeData` | 学员学历分布 | 是 |
| 报表 | GET | `/report/studentCountData` | 各班级人数统计 | 是 |
| 日志 | GET | `/log/page` | 分页查询操作日志 | 是 |
| 上传 | POST | `/upload` | 上传文件至 OSS | 是 |

---

## 3. 登录模块

### 3.1 员工登录

**接口说明**

使用用户名和密码登录，校验成功后签发 JWT。密码在数据库中以 BCrypt 哈希存储。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/login` |
| 是否需要 Token | 否 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 登录密码（明文传输，服务端与 BCrypt 哈希比对） |

**请求示例**

```http
POST /login HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "123456"
}
```

**成功响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "name": "张三",
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ6aGFuZ3NhbiIsImV4cCI6MTc5NTgxMDE2MX0._f6ihX71XJij8b77gtUPFecOYcbAyAcqtNIjhk5ncuITKKXgiLiXkj1qZBb5FVGdytrZJwvEaUY2UwxGHOdOIg"
  }
}
```

**失败响应示例**

```json
{
  "code": 0,
  "msg": "用户名或密码错误",
  "data": null
}
```

**响应字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 员工 ID |
| `username` | String | 用户名 |
| `name` | String | 员工姓名 |
| `token` | String | JWT，后续请求放入请求头 `token` |

---

## 4. 部门管理

### 4.1 查询全部部门

**接口说明**

查询所有部门，按 `update_time` 倒序返回。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/depts` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /depts HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 2,
      "name": "咨询部",
      "createTime": "2024-09-25T09:47:40",
      "updateTime": "2024-09-30T21:26:24"
    },
    {
      "id": 1,
      "name": "教研部",
      "createTime": "2024-09-25T09:47:40",
      "updateTime": "2024-09-25T09:47:40"
    }
  ]
}
```

### 4.2 根据 ID 查询部门

**接口说明**

根据部门 ID 查询详情，主要用于修改页面回显。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/depts/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 部门 ID，仅允许数字 |

**请求示例**

```http
GET /depts/1 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "教研部",
    "createTime": "2024-09-25T09:47:40",
    "updateTime": "2024-09-25T09:47:40"
  }
}
```

### 4.3 新增部门

**接口说明**

新增部门，`createTime`、`updateTime` 由服务端生成；部门名称唯一。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/depts` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | String | 是 | 部门名称，最大 10 个字符，不可重复 |

**请求示例**

```json
{
  "name": "教学研发部"
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**名称重复时**

```json
{
  "code": 0,
  "msg": "教学研发部已存在",
  "data": null
}
```

### 4.4 修改部门

**接口说明**

根据 ID 修改部门名称，`updateTime` 由服务端刷新。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `PUT` |
| 请求路径 | `/depts` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 部门 ID |
| `name` | String | 是 | 新的部门名称，不可与其他部门重复 |

**请求示例**

```json
{
  "id": 1,
  "name": "教研中心"
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 4.5 删除部门

**接口说明**

根据 ID 删除部门。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `DELETE` |
| 请求路径 | `/depts` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 部门 ID |

**请求示例**

```http
DELETE /depts?id=4 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

## 5. 员工管理

### 5.1 分页条件查询员工

**接口说明**

按姓名、性别、入职时间范围分页查询员工，按 `update_time` 倒序返回。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/emps` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |
| `name` | String | 否 | 姓名，模糊匹配 |
| `gender` | Integer | 否 | 性别：`1` 男，`2` 女 |
| `begin` | String | 否 | 入职时间范围开始，格式 `yyyy-MM-dd` |
| `end` | String | 否 | 入职时间范围结束，格式 `yyyy-MM-dd` |

> `begin`、`end` 需要同时传入才会生效。

**请求示例**

```http
GET /emps?page=1&pageSize=10&name=张&gender=1&begin=2019-01-01&end=2021-12-31 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      {
        "id": 1,
        "username": "zhangsan",
        "name": "张三",
        "gender": 1,
        "phone": "13309090001",
        "job": 1,
        "salary": 8000,
        "image": "https://example.com/avatar/1.jpg",
        "entryDate": "2020-01-01",
        "deptId": 1,
        "createTime": "2023-10-20T16:35:33",
        "updateTime": "2023-11-16T16:11:26",
        "deptName": "教研部",
        "exprList": null
      },
      {
        "id": 2,
        "username": "lisi",
        "name": "李四",
        "gender": 1,
        "phone": "13309090002",
        "job": 2,
        "salary": 9000,
        "image": "https://example.com/avatar/2.jpg",
        "entryDate": "2019-03-15",
        "deptId": 2,
        "createTime": "2023-10-20T16:35:33",
        "updateTime": "2023-10-20T16:35:37",
        "deptName": "咨询部",
        "exprList": null
      }
    ]
  }
}
```

> 出于安全考虑，`password` 字段不会出现在任何响应中。

**员工对象字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 员工 ID |
| `username` | String | 用户名，唯一 |
| `name` | String | 姓名 |
| `gender` | Integer | 性别 |
| `phone` | String | 手机号，唯一 |
| `job` | Integer | 职位 |
| `salary` | Integer | 薪资 |
| `image` | String | 头像 URL |
| `entryDate` | String | 入职日期 |
| `deptId` | Integer | 部门 ID |
| `deptName` | String | 部门名称（联表查询字段） |
| `createTime` | String | 创建时间 |
| `updateTime` | String | 修改时间 |
| `exprList` | Array | 工作经历列表（详情接口返回） |

### 5.2 查询全部员工

**接口说明**

查询全部员工，不分页，主要用于班级管理页面中班主任下拉框。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/emps/list` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /emps/list HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "username": "zhangsan",
      "name": "张三",
      "gender": 1,
      "phone": "13309090001",
      "job": 1,
      "salary": 8000,
      "image": "https://example.com/avatar/1.jpg",
      "entryDate": "2020-01-01",
      "deptId": 1,
      "createTime": "2023-10-20T16:35:33",
      "updateTime": "2023-11-16T16:11:26",
      "deptName": null,
      "exprList": null
    }
  ]
}
```

### 5.3 根据 ID 查询员工详情

**接口说明**

查询员工基础信息及其工作经历列表，用于修改页面回显。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/emps/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 员工 ID，仅允许数字 |

**请求示例**

```http
GET /emps/1 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "name": "张三",
    "gender": 1,
    "phone": "13309090001",
    "job": 1,
    "salary": 8000,
    "image": "https://example.com/avatar/1.jpg",
    "entryDate": "2020-01-01",
    "deptId": 1,
    "createTime": "2023-10-20T16:35:33",
    "updateTime": "2023-11-16T16:11:26",
    "deptName": null,
    "exprList": [
      {
        "id": 1,
        "empId": 1,
        "begin": "2018-07-01",
        "end": "2020-01-01",
        "company": "北京某某科技有限公司",
        "job": "Java开发工程师"
      }
    ]
  }
}
```

**工作经历字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 工作经历 ID |
| `empId` | Integer | 所属员工 ID |
| `begin` | String | 开始日期，`yyyy-MM-dd` |
| `end` | String | 结束日期，`yyyy-MM-dd` |
| `company` | String | 公司名称 |
| `job` | String | 职位 |

### 5.4 新增员工

**接口说明**

新增员工，同时保存其工作经历（一对多）。员工与工作经历在同一个事务中写入。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/emps` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `username` | String | 是 | 用户名，唯一 |
| `password` | String | 否 | 登录密码；不传或为空时使用默认密码 `123456`，服务端使用 BCrypt 加密 |
| `name` | String | 是 | 姓名 |
| `gender` | Integer | 是 | 性别：`1` 男，`2` 女 |
| `phone` | String | 是 | 手机号，唯一 |
| `job` | Integer | 否 | 职位 |
| `salary` | Integer | 否 | 薪资 |
| `image` | String | 否 | 头像 URL |
| `entryDate` | String | 否 | 入职日期，`yyyy-MM-dd` |
| `deptId` | Integer | 否 | 所属部门 ID |
| `exprList` | Array | 否 | 工作经历列表 |

**`exprList` 元素字段**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `begin` | String | 否 | 开始日期，`yyyy-MM-dd` |
| `end` | String | 否 | 结束日期，`yyyy-MM-dd` |
| `company` | String | 否 | 公司名称 |
| `job` | String | 否 | 职位 |

**请求示例**

```json
{
  "username": "zhaoliu",
  "password": "123456",
  "name": "赵六",
  "gender": 1,
  "phone": "13309090004",
  "job": 2,
  "salary": 10000,
  "image": "https://example.com/avatar/4.jpg",
  "entryDate": "2024-03-01",
  "deptId": 1,
  "exprList": [
    {
      "begin": "2019-07-01",
      "end": "2022-06-30",
      "company": "北京某某科技有限公司",
      "job": "Java开发工程师"
    },
    {
      "begin": "2022-07-01",
      "end": "2024-02-29",
      "company": "上海某某信息技术有限公司",
      "job": "高级Java开发工程师"
    }
  ]
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**用户名或手机号重复时**

```json
{
  "code": 0,
  "msg": "zhaoliu已存在",
  "data": null
}
```

### 5.5 修改员工

**接口说明**

修改员工基础信息与工作经历。工作经历采用“先删除旧数据、再批量插入新数据”的策略。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `PUT` |
| 请求路径 | `/emps` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 员工 ID |
| `username` | String | 否 | 用户名 |
| `password` | String | 否 | 新密码；不传则保持原密码，传入后由服务端 BCrypt 加密 |
| `name` | String | 否 | 姓名 |
| `gender` | Integer | 否 | 性别 |
| `phone` | String | 否 | 手机号 |
| `job` | Integer | 否 | 职位 |
| `salary` | Integer | 否 | 薪资 |
| `image` | String | 否 | 头像 URL |
| `entryDate` | String | 否 | 入职日期 |
| `deptId` | Integer | 否 | 部门 ID |
| `exprList` | Array | 否 | 新的工作经历列表 |

> 动态 SQL 只更新非空字段。`exprList` 为 `null` 或空数组时，会清空该员工原有工作经历。

**请求示例**

```json
{
  "id": 4,
  "name": "赵六",
  "gender": 1,
  "phone": "13309090004",
  "job": 2,
  "salary": 12000,
  "entryDate": "2024-03-01",
  "deptId": 2,
  "exprList": [
    {
      "begin": "2019-07-01",
      "end": "2024-02-29",
      "company": "北京某某科技有限公司",
      "job": "Java开发工程师"
    }
  ]
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 5.6 批量删除员工

**接口说明**

根据 ID 列表批量删除员工，同时删除其工作经历，两个操作在同一事务中完成。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `DELETE` |
| 请求路径 | `/emps` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `ids` | Integer[] | 是 | 员工 ID 列表，支持逗号分隔或重复参数 |

**请求示例**

```http
DELETE /emps?ids=4,5,6 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

等价写法：

```http
DELETE /emps?ids=4&ids=5&ids=6 HTTP/1.1
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

---

## 6. 班级管理

### 6.1 分页条件查询班级

**接口说明**

按班级名称、结课时间范围分页查询班级，并实时计算班级状态。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/clazzs` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |
| `name` | String | 否 | 班级名称，模糊匹配 |
| `begin` | String | 否 | 结课时间范围开始，`yyyy-MM-dd` |
| `end` | String | 否 | 结课时间范围结束，`yyyy-MM-dd` |

**请求示例**

```http
GET /clazzs?page=1&pageSize=10&name=就业&begin=2023-01-01&end=2024-12-31 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      {
        "id": 3,
        "name": "大数据就业58期",
        "room": "209",
        "beginDate": "2023-08-01",
        "endDate": "2024-02-15",
        "masterId": 3,
        "subject": 3,
        "createTime": "2023-06-01T17:51:21",
        "updateTime": "2023-06-01T17:51:21",
        "masterName": "王五",
        "status": "已结课"
      },
      {
        "id": 1,
        "name": "JavaEE就业163期",
        "room": "212",
        "beginDate": "2023-04-30",
        "endDate": "2023-06-29",
        "masterId": 1,
        "subject": 1,
        "createTime": "2023-06-01T17:08:23",
        "updateTime": "2023-06-01T17:39:58",
        "masterName": "张三",
        "status": "已结课"
      }
    ]
  }
}
```

**班级对象字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 班级 ID |
| `name` | String | 班级名称，唯一 |
| `room` | String | 班级教室 |
| `beginDate` | String | 开课日期 |
| `endDate` | String | 结课日期 |
| `masterId` | Integer | 班主任员工 ID |
| `subject` | Integer | 学科 |
| `createTime` | String | 创建时间 |
| `updateTime` | String | 修改时间 |
| `masterName` | String | 班主任姓名（联表查询字段） |
| `status` | String | 班级状态：`未开班` / `已开班` / `已结课`（只读） |

### 6.2 查询全部班级

**接口说明**

查询全部班级，不分页，用于前端下拉框。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/clazzs/list` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /clazzs/list HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "name": "JavaEE就业163期",
      "room": "212",
      "beginDate": "2023-04-30",
      "endDate": "2023-06-29",
      "masterId": 1,
      "subject": 1,
      "createTime": "2023-06-01T17:08:23",
      "updateTime": "2023-06-01T17:39:58",
      "masterName": null,
      "status": null
    }
  ]
}
```

### 6.3 根据 ID 查询班级详情

**接口说明**

根据班级 ID 查询详情，用于修改页面回显。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/clazzs/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 班级 ID，仅允许数字 |

**请求示例**

```http
GET /clazzs/1 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "JavaEE就业163期",
    "room": "212",
    "beginDate": "2023-04-30",
    "endDate": "2023-06-29",
    "masterId": 1,
    "subject": 1,
    "createTime": "2023-06-01T17:08:23",
    "updateTime": "2023-06-01T17:39:58",
    "masterName": null,
    "status": null
  }
}
```

### 6.4 新增班级

**接口说明**

新增班级，`createTime`、`updateTime` 由服务端生成；班级名称唯一。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/clazzs` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | String | 是 | 班级名称，唯一，最大 30 个字符 |
| `room` | String | 否 | 班级教室 |
| `beginDate` | String | 是 | 开课日期，`yyyy-MM-dd` |
| `endDate` | String | 是 | 结课日期，`yyyy-MM-dd` |
| `masterId` | Integer | 否 | 班主任员工 ID |
| `subject` | Integer | 是 | 学科：`1` Java，`2` 前端，`3` 大数据，`4` Python，`5` Go，`6` 嵌入式 |

**请求示例**

```json
{
  "name": "JavaEE就业164期",
  "room": "213",
  "beginDate": "2026-03-01",
  "endDate": "2026-09-30",
  "masterId": 1,
  "subject": 1
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 6.5 修改班级

**接口说明**

修改班级信息，动态更新非空字段；`updateTime` 由服务端强制刷新。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `PUT` |
| 请求路径 | `/clazzs` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 班级 ID |
| `name` | String | 否 | 班级名称 |
| `room` | String | 否 | 班级教室 |
| `beginDate` | String | 否 | 开课日期 |
| `endDate` | String | 否 | 结课日期 |
| `masterId` | Integer | 否 | 班主任员工 ID |
| `subject` | Integer | 否 | 学科 |

**请求示例**

```json
{
  "id": 4,
  "room": "215",
  "endDate": "2026-10-31",
  "masterId": 2
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**班级不存在时**

```json
{
  "code": 0,
  "msg": "班级不存在，修改失败",
  "data": null
}
```

### 6.6 删除班级

**接口说明**

根据 ID 删除班级。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `DELETE` |
| 请求路径 | `/clazzs/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 班级 ID，仅允许数字 |

**请求示例**

```http
DELETE /clazzs/4 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**班级不存在时**

```json
{
  "code": 0,
  "msg": "班级不存在，删除失败",
  "data": null
}
```

---

## 7. 学员管理

### 7.1 分页条件查询学员

**接口说明**

按姓名、学历、班级分页查询学员，并关联返回班级名称。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/students` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | Integer | 否 | 页码，默认 `1` |
| `pageSize` | Integer | 否 | 每页条数，默认 `10` |
| `name` | String | 否 | 学员姓名，模糊匹配 |
| `degree` | Integer | 否 | 学历 |
| `clazzId` | Integer | 否 | 班级 ID |

**请求示例**

```http
GET /students?page=1&pageSize=10&name=赵&degree=4&clazzId=1 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 1,
    "rows": [
      {
        "id": 1,
        "name": "赵六",
        "no": "2022000001",
        "gender": 1,
        "phone": "18800000001",
        "idCard": "110120000300200001",
        "isCollege": 1,
        "address": "北京市昌平区建材城西路1号",
        "degree": 4,
        "graduationDate": "2022-07-01",
        "clazzId": 1,
        "violationCount": 0,
        "violationScore": 0,
        "createTime": "2023-11-14T21:22:19",
        "updateTime": "2023-11-14T21:22:19",
        "clazzName": "JavaEE就业163期"
      }
    ]
  }
}
```

**学员对象字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 学员 ID |
| `name` | String | 姓名 |
| `no` | String | 学号，唯一 |
| `gender` | Integer | 性别 |
| `phone` | String | 手机号，唯一 |
| `idCard` | String | 身份证号，唯一 |
| `isCollege` | Integer | 是否院校学员 |
| `address` | String | 联系地址 |
| `degree` | Integer | 最高学历 |
| `graduationDate` | String | 毕业日期 |
| `clazzId` | Integer | 班级 ID |
| `violationCount` | Integer | 违纪次数 |
| `violationScore` | Integer | 违纪扣分 |
| `createTime` | String | 创建时间 |
| `updateTime` | String | 修改时间 |
| `clazzName` | String | 班级名称（联表查询字段） |

### 7.2 根据 ID 查询学员

**接口说明**

根据学员 ID 查询详情，用于修改页面回显。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/students/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 学员 ID |

**请求示例**

```http
GET /students/1 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "赵六",
    "no": "2022000001",
    "gender": 1,
    "phone": "18800000001",
    "idCard": "110120000300200001",
    "isCollege": 1,
    "address": "北京市昌平区建材城西路1号",
    "degree": 4,
    "graduationDate": "2022-07-01",
    "clazzId": 1,
    "violationCount": 0,
    "violationScore": 0,
    "createTime": "2023-11-14T21:22:19",
    "updateTime": "2023-11-14T21:22:19",
    "clazzName": null
  }
}
```

### 7.3 新增学员

**接口说明**

新增学员，创建时间和修改时间由数据库 `NOW()` 生成。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/students` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | String | 是 | 姓名 |
| `no` | String | 是 | 学号，唯一，最大 10 个字符 |
| `gender` | Integer | 是 | 性别 |
| `phone` | String | 是 | 手机号，唯一 |
| `idCard` | String | 是 | 身份证号，唯一，18 位 |
| `isCollege` | Integer | 是 | 是否院校学员：`1` 是，`0` 否 |
| `address` | String | 否 | 联系地址 |
| `degree` | Integer | 否 | 最高学历 |
| `graduationDate` | String | 否 | 毕业日期，`yyyy-MM-dd` |
| `clazzId` | Integer | 是 | 班级 ID |

**请求示例**

```json
{
  "name": "周九",
  "no": "2022000004",
  "gender": 1,
  "phone": "18800000004",
  "idCard": "110120000300200004",
  "isCollege": 1,
  "address": "北京市海淀区中关村大街4号",
  "degree": 4,
  "graduationDate": "2024-07-01",
  "clazzId": 1
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

**学号、手机号或身份证号重复时**

```json
{
  "code": 0,
  "msg": "2022000004已存在",
  "data": null
}
```

### 7.4 修改学员

**接口说明**

动态更新学员非空字段，`updateTime` 由 SQL 统一刷新。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `PUT` |
| 请求路径 | `/students` |
| 是否需要 Token | 是 |
| Content-Type | `application/json` |

**请求参数（Body）**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 学员 ID |
| `name` | String | 否 | 姓名 |
| `no` | String | 否 | 学号 |
| `gender` | Integer | 否 | 性别 |
| `phone` | String | 否 | 手机号 |
| `idCard` | String | 否 | 身份证号 |
| `isCollege` | Integer | 否 | 是否院校学员 |
| `address` | String | 否 | 联系地址 |
| `degree` | Integer | 否 | 最高学历 |
| `graduationDate` | String | 否 | 毕业日期 |
| `violationCount` | Integer | 否 | 违纪次数（一般通过违纪处理接口修改） |
| `violationScore` | Integer | 否 | 违纪扣分（一般通过违纪处理接口修改） |
| `clazzId` | Integer | 否 | 班级 ID |

**请求示例**

```json
{
  "id": 4,
  "phone": "18800000044",
  "address": "北京市朝阳区望京街道4号",
  "degree": 5,
  "clazzId": 2
}
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 7.5 删除学员

**接口说明**

根据 ID 删除学员。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `DELETE` |
| 请求路径 | `/students/{id}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 学员 ID，仅允许数字 |

**请求示例**

```http
DELETE /students/4 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

### 7.6 学员违纪处理

**接口说明**

对学员执行违纪处理：违纪次数 `+1`，违纪扣分累加指定分数。累加在 SQL 中完成，保证并发安全。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `PUT` |
| 请求路径 | `/students/violation/{id}/{score}` |
| 是否需要 Token | 是 |

**路径参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `id` | Integer | 是 | 学员 ID |
| `score` | Integer | 是 | 本次扣分分值 |

**请求示例**

```http
PUT /students/violation/1/2 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": null
}
```

> 处理结果可通过 `GET /students/{id}` 查询：`violationCount` 会加 `1`，`violationScore` 会累加本次传入的 `score`。
>
> 若学员 ID 不存在，SQL 会更新 0 行，接口仍返回成功；调用方应在操作前确认学员存在。

---

## 8. 报表统计

### 8.1 员工职位分布

**接口说明**

统计各职位的员工人数，返回前端图表可直接使用的两个平行数组。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/report/empJobData` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /report/empJobData HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "jobList": ["班主任", "讲师", "学工主管"],
    "dataList": [1, 1, 1]
  }
}
```

**字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `jobList` | String[] | 职位名称列表 |
| `dataList` | Integer[] | 与 `jobList` 一一对应的人数列表 |

### 8.2 员工性别分布

**接口说明**

统计男、女员工人数。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/report/empGenderData` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /report/empGenderData HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "name": "男性员工",
      "value": 2
    },
    {
      "name": "女性员工",
      "value": 1
    }
  ]
}
```

### 8.3 学员学历分布

**接口说明**

按学历统计学员人数，学历字典值已转换为中文名称。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/report/studentDegreeData` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /report/studentDegreeData HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": [
    {
      "name": "本科",
      "value": 1
    },
    {
      "name": "大专",
      "value": 1
    },
    {
      "name": "硕士",
      "value": 1
    }
  ]
}
```

### 8.4 各班级人数统计

**接口说明**

统计每个班级的学员人数，没有学员的班级也会返回 `0`。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/report/studentCountData` |
| 是否需要 Token | 是 |

**请求参数**

无。

**请求示例**

```http
GET /report/studentCountData HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "clazzList": ["大数据就业58期", "前端就业90期", "JavaEE就业163期"],
    "dataList": [1, 1, 1]
  }
}
```

**字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `clazzList` | String[] | 班级名称列表，作为图表 X 轴 |
| `dataList` | Integer[] | 与 `clazzList` 一一对应的学员人数 |

---

## 9. 日志与文件上传

### 9.1 分页查询操作日志

**接口说明**

分页查询 AOP 写入的操作审计日志，并关联员工表返回操作人姓名。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `GET` |
| 请求路径 | `/log/page` |
| 是否需要 Token | 是 |

**Query 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `page` | Integer | 是 | 页码，从 `1` 开始 |
| `pageSize` | Integer | 是 | 每页条数 |

> 按接口契约要求，调用方应显式传入 `page` 和 `pageSize`。当前服务端实现为兼容调用提供了默认值（`page=1`、`pageSize=10`），但业务代码不应依赖该默认值。

**请求示例**

```http
GET /log/page?page=1&pageSize=10 HTTP/1.1
Host: localhost:8081
token: <JWT>
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "total": 2,
    "rows": [
      {
        "id": 2,
        "operateEmpId": 1,
        "operateTime": "2026-11-27T16:09:37",
        "className": "com.trainingadmin.controller.EmpController",
        "methodName": "save",
        "methodParams": "[{\"username\":\"testuser\",\"name\":\"TestEmp\",\"gender\":1,\"phone\":\"13300009999\",\"job\":2,\"salary\":8000,\"deptId\":1}]",
        "returnValue": "{\"code\":1,\"msg\":\"success\",\"data\":null}",
        "costTime": 90,
        "operateEmpName": "张三"
      },
      {
        "id": 1,
        "operateEmpId": 1,
        "operateTime": "2026-11-27T16:09:36",
        "className": "com.trainingadmin.controller.DeptController",
        "methodName": "add",
        "methodParams": "[{\"id\":null,\"name\":\"TestDept\"}]",
        "returnValue": "{\"code\":1,\"msg\":\"success\",\"data\":null}",
        "costTime": 9,
        "operateEmpName": "张三"
      }
    ]
  }
}
```

**操作日志字段说明**

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `id` | Integer | 日志 ID |
| `operateEmpId` | Integer | 操作人员工 ID |
| `operateEmpName` | String | 操作人姓名（联表查询字段） |
| `operateTime` | String | 操作时间 |
| `className` | String | 目标 Controller 全类名 |
| `methodName` | String | 目标方法名 |
| `methodParams` | String | 方法参数 JSON 字符串，超长自动截断 |
| `returnValue` | String | 方法返回值 JSON 字符串，超长自动截断 |
| `costTime` | Long | 方法执行耗时，单位毫秒 |

> 员工密码字段为只写属性，不会出现在 `methodParams` 中。

### 9.2 上传文件

**接口说明**

将图片上传至阿里云 OSS，返回可访问的 URL。按 `yyyy/MM/UUID.扩展名` 生成对象路径。

| 项目 | 内容 |
| --- | --- |
| 请求方法 | `POST` |
| 请求路径 | `/upload` |
| 是否需要 Token | 是 |
| Content-Type | `multipart/form-data` |

**Form 参数**

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `file` | File | 是 | 上传文件，仅支持 `jpg`、`jpeg`、`png`、`gif`、`webp` |

**限制**

- 单个文件最大 `10MB`。
- 单次请求最大 `100MB`。
- 需要服务端已配置 OSS 的 `endpoint`、`bucketName`、`region` 及 AccessKey 环境变量，否则返回业务错误。

**请求示例**

```http
POST /upload HTTP/1.1
Host: localhost:8081
token: <JWT>
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="avatar.png"
Content-Type: image/png

<二进制文件内容>
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

**响应示例**

```json
{
  "code": 1,
  "msg": "success",
  "data": "https://your-bucket.oss-cn-beijing.aliyuncs.com/2026/11/3f2c1a4e-8b7d-4c1e-9f2a-123456789abc.png"
}
```

**文件类型不支持时**

```json
{
  "code": 0,
  "msg": "仅支持 jpg、jpeg、png、gif、webp 格式的图片",
  "data": null
}
```

**未配置 OSS 时**

```json
{
  "code": 0,
  "msg": "文件上传服务未配置，请联系管理员",
  "data": null
}
```

---

## 10. 附录

### 10.1 完整请求示例（curl）

**登录并获取令牌**

```bash
curl -s -X POST "http://localhost:8081/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'
```

**携带令牌访问受保护接口**

```bash
TOKEN="把登录接口返回的 token 粘贴到这里"
curl -s "http://localhost:8081/depts" -H "token: $TOKEN"
```

**分页查询员工**

```bash
curl -s "http://localhost:8081/emps?page=1&pageSize=10" -H "token: $TOKEN"
```

**新增员工**

```bash
curl -s -X POST "http://localhost:8081/emps" \
  -H "token: $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "zhaoliu",
    "password": "123456",
    "name": "赵六",
    "gender": 1,
    "phone": "13309090004",
    "job": 2,
    "salary": 10000,
    "entryDate": "2024-03-01",
    "deptId": 1,
    "exprList": [
      {
        "begin": "2019-07-01",
        "end": "2022-06-30",
        "company": "北京某某科技有限公司",
        "job": "Java开发工程师"
      }
    ]
  }'
```

### 10.2 演示账号

| 用户名 | 密码 | 姓名 |
| --- | --- | --- |
| `zhangsan` | `123456` | 张三 |
| `lisi` | `123456` | 李四 |
| `wangwu` | `123456` | 王五 |

> 数据库中的密码为 BCrypt 哈希，上表为明文登录密码。

### 10.3 接口调用注意事项

1. 除 `/login` 外，所有接口都必须携带请求头 `token`。
2. 业务失败通常返回 HTTP `200` + `code = 0`，请以 `code` 判断业务是否成功。
3. `page`、`pageSize` 建议由前端显式传递，不要依赖服务端默认值。
4. 日期查询参数统一使用 `yyyy-MM-dd` 格式。
5. 员工密码为只写字段，任何接口响应及审计日志都不会返回密码。
6. `PUT /emps` 会整体替换工作经历：传空数组或不传 `exprList` 会清空原有工作经历。
7. 文件上传依赖阿里云 OSS 配置，未配置时仅上传接口不可用，不影响其他接口。
