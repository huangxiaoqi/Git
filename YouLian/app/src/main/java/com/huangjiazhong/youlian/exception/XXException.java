package com.huangjiazhong.youlian.exception;

/**
 * Created by Administrator on 2016/11/25.
 * 自定义异常
 */

public class XXException extends Exception {
    private static final long serialVersionUID = 1L;

    public XXException(String message) {
        super(message);
    }

    public XXException(String message, Throwable cause) {
        super(message, cause);
    }
}
