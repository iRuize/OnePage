-- =========================================================
-- 心动捕捉 · 用户表（达梦 DM8）
-- 表建在 SOFTDEMO 模式下（由连接参数 schema=SOFTDEMO 指定）
-- 每次应用启动执行，使用 IF NOT EXISTS 保证幂等
-- 说明：达梦使用 IDENTITY(seed, increment) 实现自增主键，
--       不支持 MySQL 的 AUTO_INCREMENT 写法。
-- =========================================================

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

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.username IS '登录用户名';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.password_hash IS 'PBKDF2 密码摘要';
COMMENT ON COLUMN sys_user.created_at IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN sys_user.last_login_at IS '最近登录时间（毫秒时间戳）';
