package com.onepage.controller;

import com.onepage.common.ApiResult;
import com.onepage.dto.LoginRequest;
import com.onepage.dto.LoginResponse;
import com.onepage.dto.RegisterRequest;
import com.onepage.dto.UserVO;
import com.onepage.service.AuthService;
import com.onepage.util.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证接口
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResult<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResult.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResult<UserVO> me() {
        return ApiResult.ok(authService.getCurrentUser(UserContext.get()));
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        // Token 为无状态签名凭证，客户端清除即可
        return ApiResult.ok();
    }

    @GetMapping("/check-username")
    public ApiResult<Boolean> checkUsername(@RequestParam("username") String username) {
        return ApiResult.ok(authService.isUsernameAvailable(username));
    }
}
