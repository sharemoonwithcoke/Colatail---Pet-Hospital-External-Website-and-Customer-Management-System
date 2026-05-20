# Colatail — Pet Hospital Management System

宠物医院内部管理系统，用于管理客户、宠物、预约和日常任务。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 · Spring Boot 3.1 · Spring Data JPA |
| 数据库 | PostgreSQL 16 |
| 前端 | React 18 · Vite · Tailwind CSS |
| 通信 | RESTful API · Axios |

## 项目结构

```
.
├── Backend/          # Spring Boot REST API
│   ├── src/main/java/com/example/backend/
│   │   ├── Appointment/      # 预约实体、Repository
│   │   ├── Customer/         # 客户、宠物、病历实体与 Repository
│   │   ├── Controller/       # REST 控制器
│   │   ├── toDo/             # 待办任务实体与 Repository
│   │   └── config/           # CORS 配置
│   └── src/main/resources/
│       └── application.properties
└── frontend/         # React + Vite 前端
    └── src/
        ├── api.js            # API 请求封装
        ├── components/       # 公共组件（Layout、Modal）
        └── pages/            # 页面（Dashboard、Customers 等）
```

## 环境要求

- Java 17+
- Maven 3.8+
- PostgreSQL 16+
- Node.js 18+

## 启动步骤

### 1. 启动 PostgreSQL

确保 PostgreSQL 服务正在运行，然后创建数据库和用户：

```bash
# 以 postgres 用户登录
sudo -u postgres psql

# 执行以下 SQL
CREATE DATABASE colatail;
CREATE USER colatail_user WITH PASSWORD 'colatail123';
GRANT ALL PRIVILEGES ON DATABASE colatail TO colatail_user;
\q
```

> 如果已经执行过上述命令，跳过此步骤。

### 2. 启动后端

```bash
cd Backend
mvn spring-boot:run
```

后端启动后监听 `http://localhost:8080`。

首次启动时，Hibernate 会根据实体自动建表（`ddl-auto=update`），无需手动建表。

### 3. 启动前端

新开一个终端窗口：

```bash
cd frontend
npm install      # 首次运行需要安装依赖
npm run dev
```

前端启动后访问 `http://localhost:5173`。

> Vite 已配置代理，所有 `/api/*` 请求自动转发到 `localhost:8080`，无需额外配置跨域。

---

## API 端点

### 客户 `/api/customers`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/customers` | 获取所有客户，支持 `?search=` 全文搜索 |
| GET | `/api/customers/{id}` | 获取单个客户（含宠物列表） |
| POST | `/api/customers` | 创建客户 |
| PUT | `/api/customers/{id}` | 更新客户信息 |
| DELETE | `/api/customers/{id}` | 删除客户（级联删除宠物和病历） |

### 宠物 `/api/pets`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/pets` | 获取所有宠物 |
| GET | `/api/pets/owner/{ownerId}` | 获取某客户的宠物 |
| POST | `/api/pets` | 创建宠物（body 需含 `ownerId`） |
| PUT | `/api/pets/{id}` | 更新宠物信息 |
| DELETE | `/api/pets/{id}` | 删除宠物 |

### 病历 `/api/case-records`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/case-records/pet/{petId}` | 获取某宠物的病历（按日期倒序） |
| POST | `/api/case-records` | 添加病历（body 需含 `petId`） |
| DELETE | `/api/case-records/{id}` | 删除病历 |

### 预约 `/api/appointments`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/appointments` | 获取所有预约，支持 `?status=` `?date=` `?doctor=` |
| GET | `/api/appointments/{id}` | 获取单个预约 |
| POST | `/api/appointments` | 创建预约 |
| PUT | `/api/appointments/{id}` | 更新预约（含状态变更） |
| DELETE | `/api/appointments/{id}` | 删除预约 |

预约状态：`PENDING` / `COMPLETED` / `CANCELLED`  
医生选项：`Clair` / `Michell` / `Jay` / `Alex` / `Cam`

### 待办任务 `/api/todos`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/todos` | 获取所有任务（按创建时间倒序） |
| POST | `/api/todos` | 创建任务（body: `{ "title": "..." }`） |
| PUT | `/api/todos/{id}/toggle` | 切换完成状态 |
| DELETE | `/api/todos/{id}` | 删除任务 |

---

## 数据库配置

默认配置（`Backend/src/main/resources/application.properties`）：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/colatail
spring.datasource.username=colatail_user
spring.datasource.password=colatail123
```

如需修改数据库地址、用户名或密码，直接编辑该文件即可。

---

## 功能页面

| 页面 | 路径 | 功能 |
|------|------|------|
| Dashboard | `/` | 统计概览、今日预约 |
| Customers | `/customers` | 客户列表、搜索、增删改 |
| Customer Detail | `/customers/:id` | 客户详情、宠物管理、病历记录 |
| Appointments | `/appointments` | 预约管理、多维过滤、状态变更 |
| To-Do List | `/todos` | 任务管理 |
