package com.moder.compass.base.utils

import com.dubox.drive.kernel.i18n.getDuboxLanguage

/** 一级区域Key，受版本控制 **/
// amis key
// 问题反馈类型
const val FEED_BACK_AREA: String = "feed_back_area"

// 个人中心新运营位
const val USER_CENTER_ACTIVITY_BANNER_AREA: String = "user_center_activity_banner_area_new"

// 首页运营位
const val HOME_ACTIVITY_BANNER_AREA: String = "home_activity_banner_area"

// 首页第三方视频
const val HOME_THIRD_VIDEO_AREA: String = "home_guide_video_area"

// 文档预览区域
const val DOCUMENT_PREVIEW_AREA: String = "document_preview_area"

// 公共配置
const val COMMON_SIMPLE_SETTING_AREA: String = "common_simple_setting_area"

// 闲时备份区域
const val WORK_MANAGER_AREA: String = "work_manager_area"

// 下载sdk配置
const val DOWNLOAD_SDK_AREA: String = "download_sdk_area"

// 视频预加载配置
const val PRELOAD_VIDEO_AREA: String = "preload_video_area"

// 首页卡片配置
const val HOME_CARD: String = "home_card"

// google渠道强制升级
const val NEW_VERSION_UPDATE_AREA: String = "new_version_update"

// 首页运营位配置
const val OPEN_APP_ACTIVITY_AREA: String = "open_app_activity_area"

// 公共key
val publicConfigKeys: Array<String> = arrayOf(FEED_BACK_AREA, COMMON_SIMPLE_SETTING_AREA, NEW_VERSION_UPDATE_AREA, WORK_MANAGER_AREA)

// 私有key，包含所有的公共key
val privateConfigKeys: Array<String> = arrayOf(*publicConfigKeys, USER_CENTER_ACTIVITY_BANNER_AREA,
    DOWNLOAD_SDK_AREA, PRELOAD_VIDEO_AREA, HOME_ACTIVITY_BANNER_AREA, HOME_THIRD_VIDEO_AREA, HOME_CARD,
    OPEN_APP_ACTIVITY_AREA, DOCUMENT_PREVIEW_AREA
)


/** 二级区域Key，忽略版本控制 **/

// 是否开启评分引导
const val IS_SHOW_RATING_GUIDE = "is_show_rating_guide"

// 省电白名单开关
const val KEY_POWER_BLANK_SWITCH = "key_power_blank_switch"

// 账号同步开关 接口配置不在使用
//const val KEY_ACCOUNT_SYNC_SWITCH = "key_account_sync_switch"

// 登录是否走在线逻辑
const val IS_LOGIN_LOAD_ONLINE = "is_login_load_online"

// 备份上传并发数
const val KEY_BACKUP_CONCURRENCE_LIMIT = "key_backup_concurrence_limit"

// 下载SDK配置
const val DOWNLOAD_SDK_CONFIG = "download_sdk_config"

// 视频预加载
const val PRELOAD_VIDEO_CONFIG: String = "preload_video_config"

// 首页新用户的排序
const val KEY_HOME_CARD_SORT_USER_NEW: String = "key_home_card_sort_user_new"

// google渠道是否强制升级
const val KEY_IS_FORCE_UPDATE: String = "key_is_force_update"

// 闲时备份时间间隔范围（分钟）
const val KEY_WORK_MANAGER_INTERVAL: String = "key_work_manager_interval"

// 帐号同步时间间隔范围（分钟）
const val KEY_ACCOUNT_SYNC_INTERVAL: String = "key_account_sync_interval"

// 首页老用户的排序
const val KEY_HOME_CARD_SORT_USER_OLD: String = "key_home_card_sort_user_old"

// 开屏运营位
const val KEY_OPEN_APP_ACTIVITY: String = "key_open_app_activity"

/**
 * 获取开屏运营位缓存key
 */
fun getOpenAppActivityKey(): String {
    return "${KEY_OPEN_APP_ACTIVITY}_${getDuboxLanguage()}"
}

// 崩溃重启最小时间间隔
const val KEY_CRASH_LAUNCH_MINI_GAP_TIME_MILLIS = "key_crash_launch_mini_gap_time_millis"

// 最大崩溃次数
const val KEY_LAUNCH_MAX_CRASH_COUNT = "key_launch_max_crash_count"

// ppt预览开关
const val PPT_PREVIEW_SWITCH = "ppt_preview_switch"

// pdf预览开关
const val PDF_PREVIEW_SWITCH = "pdf_preview_switch"

// excel预览开关
const val EXCEL_PREVIEW_SWITCH = "excel_preview_switch"

// word预览开关
const val WORD_PREVIEW_SWITCH = "word_preview_switch"

// 俄罗斯宽限期
const val RUSSIA_VIP_GRACE_PERIOD: String = "russia_vip_grace_period"

// 俄罗斯宽限期 是否可以使用google pay
const val CAN_USE_GOOGLE_PAY: String = "canUseGooglePay"
// 俄罗斯宽限期 弹窗标题
const val TITLE: String = "title"
// 俄罗斯宽限期 弹窗内容
const val INFO: String = "info"
// 俄罗斯宽限期 弹窗按钮文案
const val CONFIRM_TXT: String = "confirmTxt"


