package com.love.dto;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public class LoginRequest {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    /** 是否记住登录状态（延长 Token 有效期） */
    private boolean remember;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}
