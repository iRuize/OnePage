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

1. 首次部署执行 `init-dm.sql`，创建 `SOFTDEMO` 模式（已存在则跳过）；
2. 启动应用，自动执行 `schema.sql` 建表（`IF NOT EXISTS` 幂等）。

## 表结构

`schema.sql`：

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
| `love.auth.secret` | 演示密钥 | Token 签名密钥，生产环境务必替换 |
| `love.auth.token-ttl-hours` | `168` | Token 有效期（小时），勾选「记住我」为 4 倍 |

前端 API 地址在 `frontend/assets/js/config.js` 中修改。

## 切换其他数据库

数据库访问已隔离在 `mapper` 层，切换成本较低：

1. `pom.xml` 替换 JDBC 驱动依赖；
2. `application.yml` 修改 `spring.datasource` 的驱动类、URL、账号与连接检测语句；
3. 调整 `schema.sql` 中与达梦方言相关的部分（主要是 `IDENTITY(1,1)` 自增写法）。

Controller、Service、Mapper 均无需改动。
