# 数据库说明

项目使用本地达梦数据库 DM8，默认模式 `SOFTDEMO`，通过 MyBatis-Plus 访问。

## 连接信息

| 项 | 值 |
|----|----|
| 驱动类 | `dm.jdbc.driver.DmDriver` |
| JDBC URL | `jdbc:dm://localhost:5236?schema=SOFTDEMO` |
| 用户名 | `SYSDBA` |
| 密码 | `Duohereshui1` |
| 模式 | `SOFTDEMO` |

配置位于 `backend/src/main/resources/application.yml`，可通过环境变量 `DM_URL`、`DM_USERNAME`、`DM_PASSWORD` 覆盖。

## 初始化

1. 方式一：执行 `init-dm.sql`，创建 `SOFTDEMO` 模式与用户表（已存在则跳过）；
2. 方式二：直接启动应用，由 `schema.sql` 自动建表（`IF NOT EXISTS` 幂等）。

## 表结构

`schema.sql`：

```sql
CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT       IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 用户主键，自增
    username      VARCHAR(20)  NOT NULL,                            -- 登录用户名，全局唯一
    nickname      VARCHAR(32),                                      -- 用户昵称，展示用
    avatar        VARCHAR(16),                                      -- 头像标识（emoji）
    password_hash VARCHAR(255) NOT NULL,                            -- PBKDF2 密码摘要，不存明文
    created_at    BIGINT,                                           -- 创建时间，毫秒时间戳
    last_login_at BIGINT,                                           -- 最近登录时间，毫秒时间戳
    CONSTRAINT uk_sys_user_username UNIQUE (username)               -- 用户名唯一约束
);
```

每个字段均通过 `COMMENT ON COLUMN` 写入数据库注释，可在达梦管理工具中查看。

### 日记表 sys_diary

```sql
CREATE TABLE IF NOT EXISTS sys_diary (
    id          BIGINT        IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 日记主键，自增
    user_id     BIGINT        NOT NULL,                            -- 所属用户 ID
    title       VARCHAR(100)  NOT NULL,                            -- 日记标题
    content     CLOB,                                              -- 日记正文
    mood        VARCHAR(20),                                       -- 心情标识
    images      VARCHAR(2000),                                     -- 图片地址，逗号分隔
    created_at  BIGINT        NOT NULL,                            -- 创建时间，毫秒时间戳
    updated_at  BIGINT                                             -- 更新时间，毫秒时间戳
);
```

- 图片保存在 `backend/data/uploads/yyyyMM/` 下，通过 `/uploads/**` 静态映射访问；
- 单张上限 `10MB`，仅支持 `jpg`、`jpeg`、`png`、`gif`、`webp`；
- 列表查询使用索引 `IDX_DIARY_USER_CREATED (USER_ID, CREATED_AT)`。

### 账本表 sys_bill

```sql
CREATE TABLE IF NOT EXISTS sys_bill (
    id          BIGINT         IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 账单主键，自增
    user_id     BIGINT         NOT NULL,                            -- 所属用户 ID
    type        SMALLINT       NOT NULL,                            -- 类型：1 支出，2 收入
    amount      DECIMAL(12, 2) NOT NULL,                            -- 金额
    category    VARCHAR(32),                                        -- 分类
    remark      VARCHAR(255),                                       -- 备注
    bill_date   BIGINT         NOT NULL,                            -- 账单日期，毫秒时间戳
    created_at  BIGINT         NOT NULL,                            -- 创建时间，毫秒时间戳
    updated_at  BIGINT                                              -- 更新时间，毫秒时间戳
);
```

- `type` 使用 `1` 表示支出、`2` 表示收入；
- 列表查询使用索引 `IDX_BILL_USER_DATE (USER_ID, BILL_DATE)`。

### 随笔表 sys_essay

```sql
CREATE TABLE IF NOT EXISTS sys_essay (
    id          BIGINT        IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 随笔主键，自增
    user_id     BIGINT        NOT NULL,                            -- 所属用户 ID
    title       VARCHAR(100)  NOT NULL,                            -- 随笔标题
    content     CLOB,                                              -- 随笔正文
    created_at  BIGINT        NOT NULL,                            -- 创建时间，毫秒时间戳
    updated_at  BIGINT                                             -- 更新时间，毫秒时间戳
);
```

- 列表查询使用索引 `IDX_ESSAY_USER_CREATED (USER_ID, CREATED_AT)`。

> 达梦使用 `IDENTITY(1, 1)` 实现自增主键，不兼容 MySQL 的 `AUTO_INCREMENT`。
> 若 `SOFTDEMO` 模式尚未创建，应用连接会失败，请先执行 `init-dm.sql`。

## 配置项

`backend/src/main/resources/application.yml`：

| 配置 | 默认值 | 说明 |
|------|--------|------|
| `server.port` | `8080` | 服务端口 |
| `spring.datasource.url` | `jdbc:dm://localhost:5236?schema=SOFTDEMO` | 达梦连接地址（可用 `DM_URL` 覆盖） |
| `spring.datasource.username` | `SYSDBA` | 数据库用户名（可用 `DM_USERNAME` 覆盖） |
| `spring.datasource.password` | `Duohereshui1` | 数据库密码（可用 `DM_PASSWORD` 覆盖） |
| `spring.sql.init.schema-locations` | `classpath:db/schema.sql` | 建表脚本 |
| `onepage.auth.secret` | 演示密钥 | Token 签名密钥，生产环境务必替换 |
| `onepage.auth.token-ttl-hours` | `168` | Token 有效期（小时），勾选「记住我」为 4 倍 |

前端 API 地址在 `frontend/assets/js/config.js` 中修改。

## 切换其他数据库

数据库访问已隔离在 `mapper` 层，切换成本较低：

1. `pom.xml` 替换 JDBC 驱动依赖；
2. `application.yml` 修改 `spring.datasource` 的驱动类、URL、账号与连接检测语句；
3. 调整 `schema.sql` 中与达梦方言相关的部分（主要是 `IDENTITY(1,1)` 自增写法）。

Controller、Service、Mapper 均无需改动。
