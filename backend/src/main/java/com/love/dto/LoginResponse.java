package com.love.dto;

/**
 * 登录 / 注册响应
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public class LoginResponse {

    private String token;

    private long expiresAt;

    private UserVO user;

    public LoginResponse() {
    }

    public LoginResponse(String token, long expiresAt, UserVO user) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }
}
