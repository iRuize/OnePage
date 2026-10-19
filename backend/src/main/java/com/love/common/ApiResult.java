package com.love.common;

/**
 * 统一响应结构
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
public class ApiResult<T> {

    /** 业务状态码，0 表示成功 */
    private int code;

    private String message;

    private T data;

    public ApiResult() {
    }

    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<T>(0, "success", data);
    }

    public static <T> ApiResult<T> ok() {
        return new ApiResult<T>(0, "success", null);
    }

    public static <T> ApiResult<T> fail(int code, String message) {
        return new ApiResult<T>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
