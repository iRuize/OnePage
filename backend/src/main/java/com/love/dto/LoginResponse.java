package com.love.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录 / 注册响应
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private long expiresAt;

    private UserVO user;
}
