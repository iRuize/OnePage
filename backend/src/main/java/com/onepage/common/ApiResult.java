package com.onepage.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结构
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    /**
     * 业务状态码，0 表示成功
     */
    private int code;

    private String message;

    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<T>(0, "success", data);
    }

    public static <T> ApiResult<T> ok() {
        return new ApiResult<T>(0, "success", null);
    }

    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<T>(code, message, null);
    }
}
