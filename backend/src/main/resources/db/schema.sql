-- =========================================================
-- 心动捕捉 · 用户表
-- 每次应用启动执行，使用 IF NOT EXISTS 保证幂等
-- =========================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(20)  NOT NULL,
    nickname      VARCHAR(32),
    avatar        VARCHAR(16),
    password_hash VARCHAR(255) NOT NULL,
    created_at    BIGINT,
    last_login_at BIGINT,
    CONSTRAINT uk_sys_user_username UNIQUE (username)
);
