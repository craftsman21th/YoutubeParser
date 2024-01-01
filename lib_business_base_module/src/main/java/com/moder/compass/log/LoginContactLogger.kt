package com.moder.compass.log

import com.baidu.android.common.util.DeviceId
import com.moder.compass.BaseApplication
import com.moder.compass.statistics.statisticViewEvent
import com.mars.kotlin.extension.e

/**
 * @author sunmeng
 * create at 2021-04-30
 * Email: sunmeng12@moder.com
 * 登录日志串联
 */

private const val KEY = "login_log_android"

/**
 * 登录方式
 */
const val LOGIN_TYPE_NOT_LOGIN: String = "0"
const val LOGIN_TYPE_CURRENT: String = "1"
const val LOGIN_TYPE_GOOGLE: String = "2"
const val LOGIN_TYPE_FACEBOOK: String = "3"
const val LOGIN_TYPE_KAKAO: String = "4"
const val LOGIN_TYPE_APPLE: String = "5"
const val LOGIN_TYPE_LINE: String = "6"

/**
 * 登录行为和步骤
 */

const val LOGIN_EVENT_PAGE_SHOW = "login_event_page_show" // 进入登录页面
const val LOGIN_EVENT_PAGE_SHOW_SUCCESS = "login_event_page_show_success" // 登录页加载成功
const val LOGIN_EVENT_GET_MD5LIST = "login_event_get_md5list" // 登录前获取  md5 list
const val LOGIN_EVENT_LOAD_URL = "login_event_load_url" // 开始加载网页
const val LOGIN_EVENT_HYBRID_ACTION = "login_event_hybrid_action"  // Hybrid  通信
const val LOGIN_EVENT_GOOGLE_LOGIN = "login_event_google_login" // google 登录
const val LOGIN_EVENT_FACEBOOK_LOGIN = "login_event_facebook_login" // facebook 登录
const val LOGIN_EVENT_LOGIN_RESULT_SUCCESS = "login_event_login_event_success" // 登录 成功
const val LOGIN_EVENT_LOGIN_RESULT_FAILED = "login_event_login_event_failed" // 登录  失败
const val LOGIN_EVENT_VERIFY_FAILED = "login_event_verify_failed" // 校验失败
const val LOGIN_EVENT_CANCEL = "login_event_cancel" // 取消登录
const val LOGIN_EVENT_GET_PSIGN = "login_event_get_psign" //
const val LOGIN_EVENT_KAOKAO_LOGIN = "login_event_kaokao_login" // kaokao 登录结果
const val LOGIN_EVENT_LINE_LOGIN = "login_event_line_login" // kaokao 登录结果
const val LOGIN_EVENT_LOGIN_ERROR_MONITOR = "login_error_monitor" // 登录异常监控错误码
const val LOGIN_EVENT_REGISTER_ERROR_MONITOR = "register_error_monitor" // 注册异常监控错误码
const val LOGIN_EVENT_REGISTER_SUCCESS_MONITOR = "register_success_monitor" // 注册成功监控错误码

/**
 * 登录日志串联
 */

private var logId = DeviceId.getDeviceID(BaseApplication.getContext()) + System.currentTimeMillis()
private var loginType = LOGIN_TYPE_NOT_LOGIN

/**
 * op = KEY: login_log_android
 * other0: logId - "cuid+时间戳"
 * other1: 登录方式: moder 1, Google 2, Facebook 3, kakao 4, apple 5
 * other2: 登录账号：有则传邮箱，无则传空字符串
 * other3: event: 行为、步骤
 * other4: reason: requestId,yme、其他原因等
 */
fun loginEvent(event: String, reason: String = "", account: String = "") {
    // todo @mali06
    "Login Event event: $event reason: $reason account: $account KEY: $KEY logID: $logId".e("LoginContact")
    statisticViewEvent(KEY, logId, loginType, account, event, reason)
}

/**
 * 初始化logId, 每次登录行为的 logId 唯一
 */
fun updateLogId() {
    logId = DeviceId.getDeviceID(BaseApplication.getContext()) + System.currentTimeMillis()
}

/**
 *
 */
fun getLogId() = logId

/**
 *
 */
fun setLoginType(type: String) {
    loginType = type
}

