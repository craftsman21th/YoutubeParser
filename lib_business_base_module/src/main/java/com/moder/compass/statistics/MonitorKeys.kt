package com.moder.compass.statistics

/**
 * Created by yeliangliang on 2021/11/9
 */


// 登录页的加载时长-监控
const val MONITOR_LOGIN_ACTIVITY_LOADING_DURATION: String = "monitor_login_activity_loading_duration_v2"

// 卡顿率-监控-页面级的丢帧卡顿监控
const val BLOCK_RATE_MONITOR_V1: String = "block_rate_monitor_v1_v2"
// 卡顿率-监控-全局卡顿监控
const val BLOCK_RATE_MONITOR_V2: String = "block_rate_monitor_v2_v2"

// 登录页的url加载时长-监控
const val MONITOR_LOGIN_URL_LOADING_DURATION: String = "monitor_login_url_loading_duration_v2"

// 内存监控
const val MEMORY_MONITOR_COLD: String = "memory_monitor_cold_v2"
const val MEMORY_MONITOR_COLD_FIRST_USE: String = "memory_monitor_cold_first_use_v2"
const val MEMORY_MONITOR_HOT: String = "memory_monitor_hot_v2"
const val MEMORY_MONITOR_COLD_NATIVE: String = "memory_monitor_cold_native_v2"
const val MEMORY_MONITOR_COLD_NATIVE_FIRST_USE: String = "memory_monitor_cold_first_use_native_v2"
const val MEMORY_MONITOR_PEEK: String = "memory_monitor_peek_v2"

// 电池监控
const val BATTERY_MONITOR_COLD: String = "battery_monitor_cold_v2"
const val BATTERY_MONITOR_COLD_FIRST_USE: String = "battery_monitor_cold_first_use_v2"
const val BATTERY_MONITOR_HOT: String = "battery_monitor_hot_v2"

// 温度监控
const val TEMPERATURE_MONITOR_COLD: String = "temperature_monitor_cold_v2"
const val TEMPERATURE_MONITOR_BRAND_COLD: String = "temperature_monitor_brand_cold_v2"
const val TEMPERATURE_MONITOR_COLD_FIRST_USE: String = "temperature_monitor_cold_first_use_v2"
const val TEMPERATURE_MONITOR_BRAND_COLD_FIRST_USE: String = "temperature_monitor_brand_cold_first_use_v2"
const val TEMPERATURE_MONITOR_HOT: String = "temperature_monitor_hot_v2"

// 冷启监控
const val COLD_LAUNCH_MONITOR: String = "cold_launch_monitor_v2"
const val HOT_LAUNCH_MONITOR: String = "hot_launch_monitor_v2"
// 冷启到登录页加载完成的时长
const val COLD_LAUNCH_LOGIN_VIEW_DURATION_MONITOR: String = "cold_launch_login_view_duration_monitor_v2"
// 首页启动时长
const val HOME_LAUNCH_DURATION_MONITOR: String = "home_launch_duration_monitor_v2"

// 会员支付监控
const val VIP_PAY_MONITOR: String = "vip_pay_monitor_v2"

// 缩略图监控
const val THUMB_MONITOR: String = "thumb_monitor_v2"

// 登录成功率监控
const val LOGIN_CODE_MONITOR: String = "login_code_monitor_v2"

// 注册成功率监控
const val REGISTER_CODE_MONITOR: String = "register_code_monitor_v2"

// 全局接口的pvlost监控
const val MONITOR_API_RESPONSE_PV_LOST: String = "monitor_api_response_pv_lost_v2"

// 首页弹窗的展示率-新用户蒙层引导
const val MONITOR_HOME_DIALOG_NEW_USER_GUIDE: String = "monitor_home_dialog_new_user_guide_v2"
// 首页弹窗的展示率-评分引导
const val MONITOR_HOME_DIALOG_RATING_GUIDE: String = "monitor_home_dialog_rating_guide_v2"
// 首页弹窗的展示率-首屏订阅引导
const val MONITOR_HOME_DIALOG_VIP_SUB_GUIDE: String = "monitor_home_dialog_vip_sub_guide_v2"
// 首页弹窗的展示率-备份引导
const val MONITOR_HOME_DIALOG_BACKUP_GUIDE: String = "monitor_home_dialog_backup_guide_v2"

// 文档模块动态下发尺寸
const val DYNAMIC_FEATURE_MODULE_DOCUMENT_DOWNLOAD_SIZE = "dynamic_feature_module_document_download_size_v2"
// 清理模块动态下发尺寸
const val DYNAMIC_FEATURE_MODULE_CLEANER_DOWNLOAD_SIZE = "dynamic_feature_module_cleaner_download_size_v2"

// 最大线程数监控
const val THREAD_COUNT_MONITOR_MAX: String = "thread_count_monitor_max"
// 最大句柄数监控
const val FD_COUNT_MONITOR_MAX: String = "fd_count_monitor_max"
// 首屏时长
const val VIEW_PAGE_DURATION_MONITOR: String = "view_page_duration_monitor"
// 新手任务完成系数 = 新手任务最后一个任务点击拉新的pv/新手首屏引导弹窗
const val MONITOR_NEWBIE_TASK_GUIDE: String = "monitor_newbie_task_guide"
// 外链承接页弹出率
const val MONITOR_SHARE_LINK_BEFORE_LOGIN_RATE: String = "monitor_share_link_before_login_rate"
// 福袋入口弹出率
const val MONITOR_BONUS_BAG_ENTRY_RATE: String = "monitor_bonus_bag_entry_rate"
// 热启开屏弹出率
const val MONITOR_HOT_OPEN_AD_RATE: String = "monitor_hot_open_ad_rate"
// 冷启开屏弹出率
const val MONITOR_COLD_OPEN_AD_RATE: String = "monitor_cold_open_ad_rate"
// 福袋广告弹出系数
const val MONITOR_BONUS_BAG_AD_SHOW_RATE: String = "monitor_bonus_bag_ad_show_rate"
// 视频贴片广告弹出系数
const val MONITOR_VIDEO_BONDING_AD_SHOW_RATE: String = "monitor_video_bonding_ad_show_rate"
/**
 * 目前有的key后缀组合：
 * lib_business_document_download_result
 * lib_dynamic_flextech_cleaner_download_result
 */
const val DYNAMIC_FEATURE_DOWNLOAD_RESULT_SUFFIX: String = "_download_result"
