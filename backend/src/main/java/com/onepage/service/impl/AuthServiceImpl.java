package com.onepage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onepage.common.BizException;
import com.onepage.dto.LoginRequest;
import com.onepage.dto.LoginResponse;
import com.onepage.dto.RegisterRequest;
import com.onepage.dto.UserVO;
import com.onepage.mapper.UserMapper;
import com.onepage.po.User;
import com.onepage.service.AuthService;
import com.onepage.util.PasswordUtil;
import com.onepage.util.TokenUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 认证服务实现（服务层）
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final List<String> DEFAULT_AVATARS = Arrays.asList(
            "💘", "💖", "💗", "💕", "😊", "🐻", "🐰", "🐱", "🦊", "🍓", "🌸", "🌙"
    );

    private final UserMapper userMapper;
    private final TokenUtil tokenUtil;

    public AuthServiceImpl(UserMapper userMapper, TokenUtil tokenUtil) {
        this.userMapper = userMapper;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (existsUsername(username)) {
            throw new BizException(409, "该用户名已被注册");
        }

        long now = System.currentTimeMillis();
        User user = new User();
        user.setUsername(username);
        user.setNickname(resolveNickname(request.getNickname(), username));
        user.setAvatar(randomAvatar());
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        user.setCreatedAt(now);
        user.setLastLoginAt(now);
        userMapper.insert(user);

        return buildResponse(user, false);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = findByUsername(request.getUsername().trim());
        if (user == null || !PasswordUtil.verify(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }
        user.setLastLoginAt(System.currentTimeMillis());
        userMapper.updateById(user);
        return buildResponse(user, request.isRemember());
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(401, "登录状态已失效，请重新登录");
        }
        return UserVO.from(user);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            return false;
        }
        return !existsUsername(username.trim());
    }

    private boolean existsUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .apply("LOWER(username) = {0}", username.toLowerCase());
        return userMapper.selectCount(wrapper) > 0;
    }

    private User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .apply("LOWER(username) = {0}", username.toLowerCase())
                .last("LIMIT 1");
        return userMapper.selectOne(wrapper);
    }

    private LoginResponse buildResponse(User user, boolean remember) {
        String token = tokenUtil.create(user.getId(), remember);
        long ttl = remember ? tokenUtil.getRememberTtlMillis() : tokenUtil.getDefaultTtlMillis();
        long expiresAt = System.currentTimeMillis() + ttl;
        return new LoginResponse(token, expiresAt, UserVO.from(user));
    }

    private String resolveNickname(String nickname, String username) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return username;
        }
        return nickname.trim();
    }

    private String randomAvatar() {
        return DEFAULT_AVATARS.get(ThreadLocalRandom.current().nextInt(DEFAULT_AVATARS.size()));
    }
}
