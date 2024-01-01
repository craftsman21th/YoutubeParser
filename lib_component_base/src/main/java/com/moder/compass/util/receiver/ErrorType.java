package com.moder.compass.util.receiver;

/**
 * Created by manyongqiang on 2017/10/23.
 * Server请求错误类型
 */

public enum  ErrorType {
    /**
     * 网络错误
     */
    NETWORK_ERROR,
    /**
     * 业务错误
     */
    SERVER_ERROR,
    /**
     * 账户封禁
     */
    ACCOUNT_BAN_ERROR,
    /**
     * 账户通用
     */
    ACCOUNT_COMMON_ERROR
}
