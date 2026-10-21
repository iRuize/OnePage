-- =========================================================
-- 一页 · 达梦数据库手动初始化脚本
-- 以 SYSDBA 身份在达梦管理工具（DM Manager）中执行一次
-- 作用：创建 SOFTDEMO 模式 + 登录注册表 + 完整字段注释
-- 说明：脚本按顺序逐条执行；若模式已存在，跳过第 1 步即可
-- =========================================================

-- 1. 创建业务模式（已存在时本行会报“模式已存在”，可忽略）
CREATE SCHEMA SOFTDEMO AUTHORIZATION SYSDBA;

-- 2. 切换到业务模式，后续建表落在 SOFTDEMO 下
SET SCHEMA SOFTDEMO;

-- 3. 用户表
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

-- 4. 表注释
COMMENT ON TABLE SOFTDEMO.SYS_USER IS '用户表：登录注册账号信息';

-- 5. 字段注释
COMMENT ON COLUMN SOFTDEMO.SYS_USER.ID IS '用户主键，自增';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.USERNAME IS '登录用户名，全局唯一';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.NICKNAME IS '用户昵称，展示用';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.AVATAR IS '头像标识（emoji）';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.PASSWORD_HASH IS 'PBKDF2 密码摘要，不保存明文密码';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.CREATED_AT IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN SOFTDEMO.SYS_USER.LAST_LOGIN_AT IS '最近登录时间（毫秒时间戳）';

-- 6. 日记表
CREATE TABLE IF NOT EXISTS SOFTDEMO.SYS_DIARY (
    id          BIGINT        IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 日记主键，自增
    user_id     BIGINT        NOT NULL,                            -- 所属用户 ID
    title       VARCHAR(100)  NOT NULL,                            -- 日记标题
    content     CLOB,                                              -- 日记正文
    mood        VARCHAR(20),                                       -- 心情标识
    images      VARCHAR(2000),                                     -- 图片地址，逗号分隔
    created_at  BIGINT        NOT NULL,                            -- 创建时间，毫秒时间戳
    updated_at  BIGINT                                             -- 更新时间，毫秒时间戳
);

COMMENT ON TABLE SOFTDEMO.SYS_DIARY IS '日记表：用户日记内容与图片';

COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.ID IS '日记主键，自增';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.USER_ID IS '所属用户 ID，关联 sys_user.id';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.TITLE IS '日记标题';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.CONTENT IS '日记正文';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.MOOD IS '心情标识（calm/happy/down/tired/hope）';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.IMAGES IS '图片地址，多个以英文逗号分隔';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.CREATED_AT IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN SOFTDEMO.SYS_DIARY.UPDATED_AT IS '更新时间（毫秒时间戳）';

-- 7. 日记列表查询索引（用户 + 创建时间）
CREATE INDEX IDX_DIARY_USER_CREATED ON SOFTDEMO.SYS_DIARY (USER_ID, CREATED_AT);
