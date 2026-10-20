-- =========================================================
-- 一页 · 登录注册表结构（达梦 DM8）
-- 表建在 SOFTDEMO 模式下（连接参数 schema=SOFTDEMO 指定）
-- 每次应用启动执行，IF NOT EXISTS 保证幂等
-- 说明：达梦使用 IDENTITY(seed, increment) 实现自增主键，
--       不支持 MySQL 的 AUTO_INCREMENT 写法。
-- =========================================================

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

-- 表注释
COMMENT ON TABLE sys_user IS '用户表：登录注册账号信息';

-- 字段注释
COMMENT ON COLUMN sys_user.id IS '用户主键，自增';
COMMENT ON COLUMN sys_user.username IS '登录用户名，全局唯一';
COMMENT ON COLUMN sys_user.nickname IS '用户昵称，展示用';
COMMENT ON COLUMN sys_user.avatar IS '头像标识（emoji）';
COMMENT ON COLUMN sys_user.password_hash IS 'PBKDF2 密码摘要，不保存明文密码';
COMMENT ON COLUMN sys_user.created_at IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN sys_user.last_login_at IS '最近登录时间（毫秒时间戳）';
