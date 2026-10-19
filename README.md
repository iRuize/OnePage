# 心动捕捉 · HeartCatch

一个前后端分离的恋爱主题小游戏项目：包含华丽的登录 / 注册界面，以及一款单人向的「心动捕捉」小游戏。

## 项目结构

```
love/
├── pom.xml                           # 聚合 POM（IDEA 导入根目录即可识别 Maven 多模块）
│
├── backend/                          # 后端服务（Spring Boot）
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/love/
│       │   ├── LoveApplication.java      启动入口
│       │   ├── controller/               【控制层】REST 接口
│       │   ├── service/                  【服务层】业务接口
│       │   ├── service/impl/             【服务层】业务实现
│       │   ├── mapper/                   【数据访问层】MyBatis-Plus Mapper
│       │   ├── po/                       持久化对象（PO，对应数据库表）
│       │   ├── dto/                      请求 / 响应对象
│       │   ├── config/                   登录拦截器、CORS / MVC 配置
│       │   ├── common/                   统一响应、业务异常、全局异常处理
│       │   └── util/                     密码摘要、Token、用户上下文
│       └── resources/
│           ├── application.yml           端口、数据源、MyBatis-Plus 配置
│           └── db/schema.sql             建表脚本（启动时自动执行）
│
├── frontend/                         # 前端静态站点（零构建，原生 HTML/CSS/JS）
│   ├── index.html                        登录 / 注册页
│   ├── assets/
│   │   ├── css/base.css                  基础样式与背景
│   │   ├── css/login.css                 登录页样式
│   │   └── js/
│   │       ├── config.js                 API 地址与跳转配置
│   │       ├── api.js                    接口封装
│   │       ├── bg.js                     粒子背景
│   │       └── login.js                  登录页交互
│   └── game/heart-catch.html             心动捕捉小游戏
│
└── README.md
```

## 三层架构

后端采用 Java 最经典的三层结构，调用方向单向、职责清晰：

```
Controller（控制层）
    ↓ 接收请求、参数校验、返回统一响应
Service（服务层，接口 + 实现）
    ↓ 业务规则：注册查重、密码校验、Token 签发
Mapper（数据访问层）
    ↓ MyBatis-Plus BaseMapper，访问达梦数据库
Database（SOFTDEMO.sys_user 表）
```

| 层 | 代码位置 | 职责 |
|----|----------|------|
| 控制层 | `controller/AuthController` | 路由、`@Valid` 校验、响应封装 |
| 服务层 | `service/AuthService` + `service/impl/AuthServiceImpl` | 认证业务逻辑 |
| 数据访问层 | `mapper/UserMapper` | 继承 `BaseMapper<User>`，由框架生成 CRUD |

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | JDK 8、Spring Boot 2.7.17、Spring MVC、Bean Validation、Lombok |
| 持久层 | MyBatis-Plus 3.5.4 + 达梦 DM8（DmJdbcDriver18） |
| 认证 | PBKDF2WithHmacSHA256 密码摘要 + HMAC-SHA256 无状态 Token |
| 前端 | 原生 HTML / CSS / JavaScript（无构建、无框架） |

## 启动方式

### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

服务默认监听 `http://localhost:8080`。

后端连接本地达梦数据库（`jdbc:dm://localhost:5236`，默认模式 `SOFTDEMO`），启动时执行 `db/schema.sql` 自动建表。

**首次部署前置条件**（以 SYSDBA 在达梦管理工具中执行一次）：

```sql
-- 见 backend/src/main/resources/db/init-dm.sql
CREATE SCHEMA SOFTDEMO AUTHORIZATION SYSDBA;
```

若 `SOFTDEMO` 模式已存在，可忽略此步骤。

### 2. 启动前端

前端为纯静态站点，任选一种方式：

```bash
# 方式一：Python
cd frontend
python -m http.server 5173

# 方式二：Node
cd frontend
npx serve -l 5173
```

浏览器访问 `http://localhost:5173`。

> 也可直接双击 `frontend/index.html` 打开；后端已配置 CORS，允许跨域访问。

### 常见问题

**端口被环境变量覆盖**

若系统存在 `SERVER__PORT` / `SERVER_PORT` 环境变量，会覆盖 `application.yml` 中的 `server.port`。可显式指定端口启动：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
```

**IntelliJ IDEA 导入**

根目录已提供聚合 `pom.xml`。用 IDEA 打开项目根目录，等待 Maven 自动导入；若未自动触发，点击 Maven 面板的 **Reload All Maven Projects** 即可。

**Lombok 注解未生效**

项目使用 Lombok 的 `@Data` / `@Getter` 等注解生成 getter、setter。IDEA 需要安装 **Lombok** 插件，并开启注解处理：

`Settings → Build, Execution, Deployment → Compiler → Annotation Processors → 勾选 Enable annotation processing`

**端口不一致**

后端默认 `8080`，前端示例使用 `5173`。若修改后端端口，请同步修改 `frontend/assets/js/config.js` 中的 `apiBase`。

## 接口说明

| 方法 | 路径 | 说明 | 鉴权 |
|------|------|------|------|
| POST | `/api/auth/register` | 注册 | 否 |
| POST | `/api/auth/login` | 登录 | 否 |
| GET | `/api/auth/me` | 获取当前用户 | 是 |
| POST | `/api/auth/logout` | 退出登录 | 是 |
| GET | `/api/auth/check-username` | 校验用户名是否可用 | 否 |

### 请求示例

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"heart01","password":"123456","nickname":"小鹿"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"heart01","password":"123456","remember":true}'

# 获取当前用户
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <token>"
```

统一响应结构：

```json
{ "code": 0, "message": "success", "data": { } }
```

`code` 为 `0` 表示成功；非 `0` 时 `message` 为可直接展示的错误提示。

## 数据库

项目连接本地达梦数据库 DM8，默认模式 `SOFTDEMO`。

**连接信息**（详见 `backend/src/main/resources/application.yml`）：

| 项 | 值 |
|----|----|
| 驱动类 | `dm.jdbc.driver.DmDriver` |
| JDBC URL | `jdbc:dm://localhost:5236?schema=SOFTDEMO` |
| 用户名 | `SYSDBA` |
| 密码 | `Duohereshui1` |
| 模式 | `SOFTDEMO` |

**初始化步骤**：

1. 首次部署执行 `backend/src/main/resources/db/init-dm.sql`，创建 `SOFTDEMO` 模式（已存在则跳过）；
2. 启动应用，自动执行 `db/schema.sql` 建表（`IF NOT EXISTS` 幂等）。

建表脚本：`backend/src/main/resources/db/schema.sql`

```sql
CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT       IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    username      VARCHAR(20)  NOT NULL,
    nickname      VARCHAR(32),
    avatar        VARCHAR(16),
    password_hash VARCHAR(255) NOT NULL,
    created_at    BIGINT,
    last_login_at BIGINT,
    CONSTRAINT uk_sys_user_username UNIQUE (username)
);
```

> 达梦使用 `IDENTITY(1,1)` 实现自增主键，不兼容 MySQL 的 `AUTO_INCREMENT`。
> 若 `SOFTDEMO` 模式尚未创建，应用连接会失败，请先执行 `db/init-dm.sql`。

## 配置项

`backend/src/main/resources/application.yml`：

| 配置 | 默认值 | 说明 |
|------|--------|------|
| `server.port` | `8080` | 服务端口 |
| `spring.datasource.url` | `jdbc:dm://localhost:5236?schema=SOFTDEMO` | 达梦连接地址（可用 `DM_URL` 覆盖） |
| `spring.datasource.username` | `SYSDBA` | 数据库用户名（可用 `DM_USERNAME` 覆盖） |
| `spring.datasource.password` | `Duohereshui1` | 数据库密码（可用 `DM_PASSWORD` 覆盖） |
| `spring.sql.init.schema-locations` | `classpath:db/schema.sql` | 建表脚本 |
| `love.auth.secret` | 演示密钥 | Token 签名密钥，生产环境务必替换 |
| `love.auth.token-ttl-hours` | `168` | Token 有效期（小时），勾选「记住我」为 4 倍 |

前端 API 地址在 `frontend/assets/js/config.js` 中修改。

## 设计说明

- **三层分离**：Controller 不直接访问 DAO，Service 统一承载业务规则，DAO 只负责数据存取。
- **密码安全**：PBKDF2WithHmacSHA256、12 万次迭代、随机盐，数据库中不保存明文密码。
- **无状态 Token**：`base64Url(userId:expireAt).base64Url(HMAC-SHA256)`，服务端无需存储会话，重启后登录态仍有效。
- **登录态传递**：前端将 Token 存入 `localStorage`，通过 `Authorization: Bearer` 请求头传递。
- **游戏联动**：登录成功后，前端会把昵称与头像同步到游戏档案，进入游戏即为当前账号身份。

## 切换为其他数据库

三层结构已把数据库访问隔离在 `mapper` 层，切换成本很低：

1. `pom.xml` 替换 JDBC 驱动依赖；
2. `application.yml` 修改 `spring.datasource` 的驱动类、URL、账号与连接检测语句；
3. 调整 `db/schema.sql` 中与达梦方言相关的部分（主要是 `IDENTITY(1,1)` 自增写法）。

Controller、Service、Entity 均无需改动。

## 后续可扩展

- 将游戏战绩上传至后端，实现跨设备同步与排行榜。
- 增加头像上传、找回密码、第三方登录。
