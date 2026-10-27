package com.onepage.common;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author 邸睿泽
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 默认业务错误码
     */
    public static final int DEFAULT_CODE = 400;

    private final int code;

    public BizException(String message) {
        this(DEFAULT_CODE, message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
