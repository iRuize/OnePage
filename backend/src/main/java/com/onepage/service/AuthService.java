package com.onepage.service;

import com.onepage.dto.LoginRequest;
import com.onepage.dto.LoginResponse;
import com.onepage.dto.RegisterRequest;
import com.onepage.dto.UserVO;

/**
 * 认证服务（服务层接口）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 注册并返回登录凭证
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 登录并返回登录凭证
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户 ID 查询当前登录用户
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 判断用户名是否可用
     */
    boolean isUsernameAvailable(String username);
}
