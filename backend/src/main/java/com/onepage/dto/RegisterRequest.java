package com.onepage.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 注册请求
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "请输入用户名")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名为 3-20 位字母、数字或下划线")
    private String username;

    @NotBlank(message = "请输入密码")
    @Size(min = 6, max = 32, message = "密码长度为 6-32 位")
    private String password;

    @Size(max = 16, message = "昵称最多 16 个字符")
    private String nickname;
}
