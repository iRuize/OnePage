package com.love.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.love.common.BizException;
import com.love.dao.UserDao;
import com.love.dto.LoginRequest;
import com.love.dto.LoginResponse;
import com.love.dto.RegisterRequest;
import com.love.dto.UserVO;
import com.love.entity.User;
import com.love.service.AuthService;
import com.love.util.PasswordUtil;
import com.love.util.TokenUtil;
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

    private final UserDao userDao;
    private final TokenUtil tokenUtil;

    public AuthServiceImpl(UserDao userDao, TokenUtil tokenUtil) {
        this.userDao = userDao;
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
        userDao.insert(user);

        return buildResponse(user, false);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = findByUsername(request.getUsername().trim());
        if (user == null || !PasswordUtil.verify(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(401, "用户名或密码错误");
        }
        user.setLastLoginAt(System.currentTimeMillis());
        userDao.updateById(user);
        return buildResponse(user, request.isRemember());
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userDao.selectById(userId);
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
        return userDao.selectCount(wrapper) > 0;
    }

    private User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .apply("LOWER(username) = {0}", username.toLowerCase())
                .last("LIMIT 1");
        return userDao.selectOne(wrapper);
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
