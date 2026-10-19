package com.love.util;

/**
 * 当前请求登录用户上下文
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public final class UserContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<Long>();

    private UserContext() {
    }

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
