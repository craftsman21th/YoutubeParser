package com.moder.compass.statistics.activation;

/**
 *
 * 设备激活相关的Config键
 * @author tianzengming<br/>
 *         Created by tianzengming on 2015/8/27.
 */
public class ActivationConfigKey {
    /**
     * 设备激活的时间
     */
    public static final String KEY_IS_ACTIVITED_TIME = "KEY_IS_ACTIVITED_TIME";
    /**
     * 点对点是否激活过
     **/
    public static final String KEY_P2PSHARE_IS_ACTIVIED = "KEY_P2PSHARE_IS_ACTIVIED";
    /**
     * 需要发送p2pshare激活
     **/
    public static final String KEY_P2PSHARE_IS_NEED_SEND_ACTIVIED = "KEY_P2PSHARE_IS_NEED_SEND_ACTIVIED";
    /**
     * 设备上是否激活过客户端的key
     *
     * @author libin09 2013-2-26
     */
    public static final String KEY_IS_ACTIVITED = "KEY_IS_ACTIVITED";
    /**
     * 应用启动时，未登录状态时统计时间记录
     */
    public static final String KEY_REPORT_LOGOUT_ANALYTICS = "key_report_logout_analytics";

    private static final String TAG = "ActiveConfigKey";

    /**
     * 注册设备返回的设备ID
     */
    public static final String DSS_DEVICE_ID = "dss_device_id";

    /**
     * 注册设备返回的设备Token
     */
    public static final String DSS_DEVICE_TOKEN = "dss_device_token";

    /**
     * 统计 fcm_token 获取异常信息
     * 1、fcm_error_0 表示没有获取到 token
     * 2、fcm_error_java.util.concurrent.ExecutionException: java.io.IOException: SERVICE_NOT_AVAILABLE
     *   代表 fcm_token 返回异常
     */
    public static final String FCM_TOKEN_ERROR = "fcm_error_";
}
