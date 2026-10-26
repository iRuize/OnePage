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

-- =========================================================
-- 日记表
-- =========================================================

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

COMMENT ON TABLE sys_diary IS '日记表：用户日记内容与图片';

COMMENT ON COLUMN sys_diary.id IS '日记主键，自增';
COMMENT ON COLUMN sys_diary.user_id IS '所属用户 ID，关联 sys_user.id';
COMMENT ON COLUMN sys_diary.title IS '日记标题';
COMMENT ON COLUMN sys_diary.content IS '日记正文';
COMMENT ON COLUMN sys_diary.mood IS '心情标识（calm/happy/down/tired/hope）';
COMMENT ON COLUMN sys_diary.images IS '图片地址，多个以英文逗号分隔';
COMMENT ON COLUMN sys_diary.created_at IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN sys_diary.updated_at IS '更新时间（毫秒时间戳）';

-- =========================================================
-- 账本表
-- =========================================================

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

COMMENT ON TABLE sys_bill IS '账本表：用户收支记录';

COMMENT ON COLUMN sys_bill.id IS '账单主键，自增';
COMMENT ON COLUMN sys_bill.user_id IS '所属用户 ID，关联 sys_user.id';
COMMENT ON COLUMN sys_bill.type IS '类型：1 支出，2 收入';
COMMENT ON COLUMN sys_bill.amount IS '金额';
COMMENT ON COLUMN sys_bill.category IS '分类';
COMMENT ON COLUMN sys_bill.remark IS '备注';
COMMENT ON COLUMN sys_bill.bill_date IS '账单日期（毫秒时间戳）';
COMMENT ON COLUMN sys_bill.created_at IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN sys_bill.updated_at IS '更新时间（毫秒时间戳）';

-- =========================================================
-- 随笔表
-- =========================================================

CREATE TABLE IF NOT EXISTS sys_essay (
    id          BIGINT        IDENTITY(1, 1) NOT NULL PRIMARY KEY, -- 随笔主键，自增
    user_id     BIGINT        NOT NULL,                            -- 所属用户 ID
    title       VARCHAR(100)  NOT NULL,                            -- 随笔标题
    content     CLOB,                                              -- 随笔正文
    created_at  BIGINT        NOT NULL,                            -- 创建时间，毫秒时间戳
    updated_at  BIGINT                                             -- 更新时间，毫秒时间戳
);

COMMENT ON TABLE sys_essay IS '随笔表：用户的短篇随笔';

COMMENT ON COLUMN sys_essay.id IS '随笔主键，自增';
COMMENT ON COLUMN sys_essay.user_id IS '所属用户 ID，关联 sys_user.id';
COMMENT ON COLUMN sys_essay.title IS '随笔标题';
COMMENT ON COLUMN sys_essay.content IS '随笔正文';
COMMENT ON COLUMN sys_essay.created_at IS '创建时间（毫秒时间戳）';
COMMENT ON COLUMN sys_essay.updated_at IS '更新时间（毫秒时间戳）';
