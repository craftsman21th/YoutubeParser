package com.moder.compass.util

import android.content.Context
import android.text.TextUtils
import android.util.ArrayMap
import com.moder.compass.BaseApplication
import com.moder.compass.WebMasterManager
import com.moder.compass.base.utils.GlobalConfigKey
import com.moder.compass.base.utils.PersonalConfigKey
import com.dubox.drive.basemodule.BuildConfig
import com.dubox.drive.basemodule.R
import com.moder.compass.model.EarnTabConfig
import com.moder.compass.firebase.DuboxRemoteConfig
import com.moder.compass.firebasemodel.OperationConfig
import com.moder.compass.firebasemodel.VDConfigWebsite
import com.dubox.drive.kernel.architecture.AppCommon
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.dubox.drive.kernel.i18n.getLanguageInSupported
import com.dubox.drive.kernel.util.*
import com.moder.compass.transfer.util.VideoURLUtil.*
import com.moder.compass.util.GOOGLE_PLAY_CHANNEL
import com.moder.compass.util.PAVO_WEBPAGE_CHANNEL
import com.moder.compass.util.isGoogleChannel
import com.moder.compass.data.NewVersionInfo
import com.moder.compass.versionupdate.io.model.Version
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mars.kotlin.extension.d
import com.mars.kotlin.extension.e
import com.mars.united.core.os.isMainProcess
import com.mars.united.core.util.date.isSameDateWithToday
import com.moder.compass.remoteconfig.AUTO_SAVE
import com.moder.compass.remoteconfig.AdNewbieProtectConfig
import com.moder.compass.remoteconfig.AdRemoteConfig
import com.moder.compass.remoteconfig.NOTIFY_SAVE
import com.moder.compass.remoteconfig.NotificationReadPointConfig
import com.moder.compass.remoteconfig.RadarConfig
import com.moder.compass.remoteconfig.ShareEarnInfo
import com.moder.compass.remoteconfig.StoreAuditAdaptationConfig
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/** 默认视频上传大小限制为100M */
private const val DEFAULT_UPLOAD_VIDEO_SIZE_LIMIT: Long = 100 * MB_1

/**
 * firebase 配置的统一key，默认值需要在remote_config_defaults.xml配置
 */

// 热启动弹广告时间间隔
const val AD_HOT_START_TIME_LIMIT_SECONDS = "ad_hot_start_time_limit_seconds"

// 热启动弹广告时每日次数限制
const val AD_HOT_START_TIMES_LIMIT_DAILY = "ad_hot_start_times_limit_daily"

/**
 * 冷启开屏广告展示时间限制   x 毫秒内启动才展示，避免启动时间过长还展示广告
 */
const val KEY_COLD_APP_OPEN_AD_WAITING_MAX_DURATION = "key_cold_app_open_ad_waiting_max_duration"

// 首页卡片广告开关
const val SWITCH_HOME_CARD_AD = "switch_home_card_ad"

// 首页最近tab卡片广告开关
const val SWITCH_RECENT_FEEDS_NATIVE_AD: String = "switch_recent_feeds_native_ad"

// 空间清理卡片广告开关
const val SWITCH_STORAGE_CLEAN_CARD_AD = "switch_storage_clean_card_ad"

// 激励视频清晰度切换开关
const val SWITCH_REWARD_VIDEO_QUALITY = "switch_reward_video_quality"

// 激励视频倍速播放开关
const val SWITCH_REWARD_VIDEO_SPEED_UP = "switch_reward_video_speed_up"

// h5签到激励视频开关
const val SWITCH_REWARD_VIDEO_H5_SIGN_IN = "switch_reward_video_h5_sign_in"

// 激励视频高速下载开关
const val SWITCH_REWARD_DOWNLOAD = "switch_reward_download"

// 时光轴卡片广告开关
const val SWITCH_TIMELINE_CARD_AD = "switch_timeline_card_ad"

// 空间清理成功插屏广告开关
const val SWITCH_STORAGE_CLEAN_INSERT_AD = "switch_storage_clean_insert_ad"

// 大图预览插屏广告开关
const val SWITCH_IMAGE_PAGE_PREVIEW = "ad_switch_image_preview"

// 大图预览插屏广告预览
const val AD_IMAGE_PREVIEW_ITEM_INDEXES = "ad_image_preview_item_indexes"

// 资源圈的开关
const val SHARE_RESOURCE_SWITCH_NEW = "share_resource_switch_new"

// 清理功能的开关
const val FLEXTECH_CLEANER_SWITCH = "flextech_cleaner_switch"

// 资源圈视频清晰度
const val SHARE_RESOURCE_VIDEO_QUALITY: String = "share_resource_video_quality"

// ytb视频默认清晰度
const val YOUTUBE_VIDEO_QUALITY: String = "youtube_share_resource_video_quality"

// 个人中心广告开关
const val SWITCH_USER_CENTER_AD = "switch_user_center_ad"

// 上传下载列表banner广告开关
const val SWITCH_TRANSFER_LIST_AD = "switch_translist_banner_ad"

// 视频详情banner广告开关
const val SWITCH_RESOURCE_DETAIL_PAGE_BANNER_AD = "resource_detail_page_native_banner_ads"

// 上传任务 Toast 广告开关
const val SWITCH_UPLOAD_TOAST_AD: String = "switch_upload_toast_ad"

// 备份完成 Toast 广告开关
const val SWITCH_BACKUP_TOAST_AD: String = "switch_backup_toast_ad"

// 添加下载任务后 Toast 广告开关
const val SWITCH_DOWNLOAD_TOAST_AD: String = "switch_download_toast_ad"

// 横屏视频播放暂停 广告开关
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD: String = "resource_horizontal_video_pause_ad"

// 视频贴片 广告开关
const val SWITCH_VIDEO_BONDING_MANUAL_NATIVE_AD: String = "switch_video_bonding_manual_native_ad"

// 视频贴片 开会员按钮展示开关
const val SWITCH_VIDEO_BONDING_NO_AD_SHOW: String = "switch_video_bonding_no_ad_show"

// 资源圈视频清晰度144
const val SHARE_RESOURCE_VIDEO_QUALITY_144: Long = 144

// 资源圈视频清晰度240
const val SHARE_RESOURCE_VIDEO_QUALITY_240: Long = 240

// 资源圈视频清晰度360
const val SHARE_RESOURCE_VIDEO_QUALITY_360: Long = 360

// 资源圈视频清晰度480
const val SHARE_RESOURCE_VIDEO_QUALITY_480: Long = 480

// 资源圈视频清晰度720
const val SHARE_RESOURCE_VIDEO_QUALITY_720: Long = 720

// 资源圈视频清晰度1080
const val SHARE_RESOURCE_VIDEO_QUALITY_1080: Long = 1080

// 增加 离线下载 开关
const val SHOW_OFFLINE_DOWNLOAD_FUNCTION: String = "show_offline_download_function"

// 跳转 d 站资源的链接
const val SHARE_RESOURCE_MORE_D_LINK: String = "share_resource_more_d_link"

// 清理功能开关-google之外的渠道
const val FLEXTECH_CLEANER_SWITCH_OUT_OF_GOOGLE: String = "flextech_cleaner_switch_out_of_google"

// 全网搜索开关，0:关闭 1:开启
const val NETWORK_SEARCH_SWITCH: String = "switch_whole_network_search"

// 全网搜索屏蔽网址：例如屏蔽谷歌、YouTube， ["google.com","youtube.com"]
const val NETWORK_SEARCH_SHIELD_ADDRESS: String = "shield_address_whole_network_search_new"

// 全网搜索直达网址
const val NETWORK_SEARCH_DIRECT_ADDRESS: String = "direct_address_whole_network_search"

// 流畅播开关
const val ENABLE_QUICK_PLAY: String = "enable_quick_play"

// video downloader website 列表
const val VIDEO_DOWNLOADER_WEBSITES: String = "video_downloader_websites_new"

// video downloader home card控制显示 BT/Link/More按钮的顺序和显示与否
const val VIDEO_DOWNLOADER_HOME_CARD_BUTTONS: String = "video_downloader_home_card_buttons_new"

// 首页节日icon数据配置
const val HOME_FESTIVAL_ICON_DATA = "home_festival_icon_data"

// 上传业务异常回捞错误码
const val UPLOAD_FEEDBACK_MONITOR_ERROR_NO: String = "upload_feedback_monitor_error_no"

// 下载业务异常回捞错误码
const val DOWNLOAD_FEEDBACK_MONITOR_ERROR_NO: String = "download_feedback_monitor_error_no"

// 登录注册监控错误码拦截
const val LOGIN_REGISTER_ERRNO_MONITOR_INTERCEPT: String = "login_register_errno_monitor_intercept"

// 支付监控错误码拦截
const val PAY_ERRNO_MONITOR_INTERCEPT: String = "pay_errno_monitor_intercept"

// 缩略图监控错误码拦截
const val THUMB_ERRNO_MONITOR_INTERCEPT: String = "thumb_errno_monitor_intercept"

// 资源圈视频播放拦截时长
const val VIDEO_INTERCEPTOR_DURATION = "video_interceptor_duration"

// 搜索框提示文案对应配置的 key
const val SEARCH_HINT_OBJECT: String = "search_hint"

// 全局接口业务异常监控采样率 1-100
const val MONITOR_API_RESPONSE_PV_LOST_SAMPLING_RATE: String =
    "monitor_api_response_pv_lost_sampling_rate"

// 搜索结果展示信息开关 true: 展示文件位置 false:不展示文件位置
const val SEARCH_RESULT_DISPLAY_INFO_SWITCH: String = "search_result_display_info_switch"

//首页飘条资源位配置 - 3.0 废弃
const val HOMEPAGE_FLOATINGSTRIP_LIST: String = "homePage_floatingStrip_list"

// LGE K22 常驻通知栏开关
const val K22_PERMANENT_NOTIFICATION_SWITCH: String = "k22_permanent_notification_switch"

/**
 * 是否展示新用户权益引导页面
 */
const val NEW_USER_SUBSCRIBE_PREMIUM_GUIDE: String = "new_user_subscribe_premium_guide"

// 首页tab切换插屏广告次数配置
const val KEY_MAIN_TAB_CLICK_AD_CONFIG: String = "key_main_tab_click_ad_config_after_220"

// 首页会员 icon 广告开关
const val SWITCH_HOMEPAGE_GIFT_BOX: String = "switch_homePage_gift_box"

// 首页会员 icon 广告关闭之后的广告
const val SWITCH_HOMEPAGE_AD_AFTER_GIFT_BOX: String = "switch_homepage_ad_after_gift_box"

// 退出app弹窗广告
const val SWITCH_EXIT_APP_DIALOG_AD: String = "switch_exit_app_dialog_ad"

// 退出视频播放弹窗广告
const val SWITCH_EXIT_VIDEO_PLAYER_DIALOG_AD: String = "switch_exit_video_player_dialog_ad"

// 退出清理结果页弹窗广告
const val SWITCH_EXIT_CLEAN_RESULT_DIALOG_AD: String = "switch_exit_clean_result_dialog_ad"

// 是否第一个初始化max高速插屏
const val IS_FIRST_INIT_MAX_HIGH_SPEED_INTER: String = "is_first_init_max_high_speed_inter"

// 备份完成 toast 广告展示的时间间隔
const val BACKUP_TOAST_AD_TIME_INTERVAL: String = "backup_toast_ad_time_interval"

// 备份完成 toast 广告的一天展示次数
const val BACKUP_TOAST_AD_DAY_TIMES: String = "backup_toast_ad_day_times"


/**
 * Widget 引导显示间隔时间
 */
const val WIDGET_GUIDE_SHOW_INTERVAL: String = "widget_guide_show_interval"

/**
 * 冷启动广告默认的最长等待时长 x 毫秒内启动才展示，避免启动时间过长还展示广告
 */
const val COLD_APP_OPEN_AD_WAITING_DEFAULT_DURATION: Long = 4500L

/**
 * 指纹sdk开关， 1:打开，其他：关闭
 */
const val SECURITY_FINGERPRINT_SWITCH: String = "security_fingerprint_switch"

/**
 * 指纹sdk，获取指纹等待时间，单位s
 */
const val SECURITY_FINGERPRINT_TIMEOUT: String = "security_fingerprint_timeout"

/**
 * 文件分片并发上传开关，0关闭，1开启
 */
const val CONCURRENT_UPLOAD_SWITCH: String = "concurrent_upload_switch"

/**
 * 文件分片并发上传最大并发数
 */
const val CONCURRENT_UPLOAD_MAX_POOL_SIZE: String = "concurrent_upload_max_pool_size"

/**
 * 视频下载器&资源圈 Telegram引导弹窗
 */
const val TELEGRAM_GUIDE_JOIN_LINK: String = "telegram_guide_join_link"

/**
 * 极速上传权益引导-文件大小阈值
 */
const val SPEED_UPLOAD_GUIDE_FILE_SIZE_THRESHOLD: String = "speed_upload_guide_file_size_threshold"

/**
 * 崩溃保护-Gaea SDK的配置
 */
const val GAEA_CRASH_CATCHER_CONFIG: String = "gaea_crash_catcher_config"

/**
 * 安全模式-Gaea_SafeMode SDK的配置
 */
const val GAEA_SAFE_MODE_CONFIG: String = "gaea_safe_mode_config"

/**
 * 冷启开屏广告配置
 */
const val COLD_APP_OPEN_AD_CONFIG: String = "cold_app_open_ad_config"

/**
 * 热启开屏广告配置
 */
const val HOT_APP_OPEN_AD_CONFIG: String = "hot_app_open_ad_config"

/**
 * 应用商店审核适配相关配置参数的名称
 */
const val CF_STORE_AUDIT_ADAPTATION: String = "cf_store_audit_adaptation"

// 暂时添加清理矩阵key
// 开屏广告开关
const val CLEANER_SWITCH_APP_OPEN_AD: String = "cleaner_switch_app_open_ad"

// 首页底部广告开关
const val CLEANER_SWITCH_APP_MAIN_AD: String = "cleaner_switch_app_main_ad"

// 扫描页面底部广告开关
const val CLEANER_SWITCH_APP_SCAN_AD: String = "cleaner_switch_app_scan_ad"

// 智能清理扫描结果页面
const val CLEANER_SWITCH_APP_SMART_SCAN_RESULT_AD: String =
    "cleaner_switch_app_smart_scan_result_ad"

// 智能清理，清理页面
const val CLEANER_SWITCH_APP_SMART_CLEAN_AD: String = "cleaner_switch_app_smart_clean_ad"

// 智能清理，清理结果页面
const val CLEANER_SWITCH_APP_SMART_CLEAN_RESULT_AD: String =
    "cleaner_switch_app_smart_clean_result_ad"

// 无需清理页面
const val CLEANER_SWITCH_APP_NO_NEED_CLEAN_AD: String = "cleaner_switch_app_no_need_clean_ad"

// 无需清理页面
const val CLEANER_SWITCH_APP_JUNK_PAGE_AD: String = "cleaner_switch_app_junk_page_ad"

// 缓存列表页面
const val CLEANER_SWITCH_APP_CACHE_LIST_AD: String = "cleaner_switch_app_cache_list_ad"

// 清理页面
const val CLEANER_SWITCH_APP_CLEAN_AD: String = "cleaner_switch_app_clean_ad"

// 清理结果表页面
const val CLEANER_SWITCH_APP_CLEAN_RESULT_AD: String = "cleaner_switch_app_clean_result_ad"

// 清理结果插屏广告
const val CLEANER_SWITCH_APP_CLEAN_RESULT_INSERT_AD: String =
    "cleaner_switch_app_clean_result_insert_ad"

// 智能清理结果插屏广告
const val CLEANER_SWITCH_APP_SMART_CLEAN_RESULT_INSERT_AD: String =
    "cleaner_switch_app_smart_clean_result_insert_ad"

// 无需清理结果插屏广告
const val CLEANER_SWITCH_NO_NEED_CLEAN_RESULT_INSERT_AD: String =
    "cleaner_switch_no_need_clean_result_insert_ad"

// 屏蔽冷热启开屏广告的云控
const val APP_OPEN_FROM_AD_SWITCH: String = "app_open_from_ad_switch"

// 广告单价>x上报
const val AD_IMPRESSION_ONE_DAY_REVENUE_THRESHOLD: String =
    "ad_impression_one_day_revenue_threshold"

// 首页顶部lottie微动效
const val HOME_HEAD_LOTTIE_OPEN: String = "home_head_lottie_open"

// 是否可以 7 天试用
const val IS_CAN_7_DAYS_TRIAL: String = "is_can_7_days_trial"

// 上传视频特权开关
const val UPLOAD_VIDEO_PREMIUM_SWITCH: String = "upload_video_premium_switch"

// 卡顿率监控配置
const val KEY_BLOCK_MONITOR_CONFIG: String = "key_block_monitor_config"

// 激励视频弹窗广告开关
const val SWITCH_NEW_BLESS_BAG_NATIVE_AD: String = "switch_new_bless_bag_native_ad"

// 首页1+1卡片开关
const val SHOW_OPERATE_AND_CLEAN_HOMECARD: String = "show_operate_and_clean_homeCard"

// 广告sdk异步初始化开关
const val INIT_AD_SDK_ASYNC: String = "init_ad_sdk_async"

// 外链页原生广告开关
const val SWITCH_SHARE_LINK_NATIVE_AD: String = "switch_share_link_native_ad"

/**
 * vip视频上传特权大小限制
 */
const val VIP_VIDEO_UPLOAD_SIZE_LIMIT: String = "vip_video_upload_size_limit"

// 低端机分数阈值
const val LOW_PERFORMANCE_DEVICE_SCORE_THRESHOLD: String = "low_performance_device_score_threshold"

// 清理已备份页面广告开关
const val SWITCH_CLEAN_BACKED_UP_FILE_AD: String = "switch_clean_backed_up_file_ad"

// 动态模块同步初始化，默认异步
const val SWITCH_DYNAMIC_MODULE_INIT_SYNC: String = "dynamic_module_init_sync"

/**
 * 雷达开关: 可控制展示版本、国家等纬度（一期先开启印度）。当关闭时，上传面板中变回拍照上传，视频tab中的入口消失;
 * { "radarEnable":true, "radarFlipMaxCount":7}
 */
const val RESOURCE_RADAR_INFO_CONFIG = "resource_radar_info_config"

/**
 * 资源小组开关
 */
const val RESOURCE_GROUP_SWITCH: String = "resource_group_switch"

/**
 * 雷达开关
 * */
const val RADAR_SWITCH: String = "is_radar_show"

/**
 * 小组专题开关
 */
const val RESOURCE_GROUP_TOPIC_SWITCH: String = "resource_group_topic_switch"

/**
 * 小组问答 feed 开关
 */
const val RESOURCE_GROUP_QUESTION_FEED_SWITCH: String = "resource_group_question_feed_switch"

/**
 * 小组引导开关
 */
const val RESOURCE_GROUP_GUIDE_SWITCH: String = "resource_group_guide_switch"

/**
 * 首页download tab开关
 */
const val DOWNLOADER_TAB_SHOW: String = "downloader_tab_show_new"

// 账号同步开关
const val ACCOUNT_SYNC_SWITCH = "account_sync_switch"

/**
 * 成人资源小组开关
 */
const val ADULT_GROUP_SWITCH: String = "adult_group_switch"

/**
 * 个人中心 - 金币中心开关, 1 开启，0关闭
 */
const val GOLD_CENTER_SWITCH: String = "gold_center_switch"

/**
 * APP 版本更新内容
 * */
const val APP_LAST_VERSION_INFO: String = "app_last_version_info"

/**
 * APP 版本下载信息
 * */
const val APP_LAST_VERSION_DATA: String = "app_last_version_data"

/**
 * all domains
 */
const val ALL_DOMAINS: String = "all_domains"

/**
 * 首页资源聚合 title
 */
const val RES_AGGREGATION_TITLE: String = "res_aggregation_title"

/**
 * process keep live 开关
 * */
const val PKL_SWITCHER: String = "pkl_switcher"

/**
 * 多进程守护 保活 开关
 * */
const val DUAL_PROCESS_SWITCHER: String = "dual_process_switcher"

/**
 * explore tab feed 广告开关
 */
const val EXPLORE_TAB_AD_CONFIG = "explore_tab_ad_config"

/**
 * 全部二级页列表页对应的 广告开关
 */
const val TWO_LEVEL_PAGER_FEED_AD_CONFIG = "two_level_pager_feed_ad_config"

/**
 * 视频详情页推荐 feed 广告配置
 */
const val VIDEO_DETAIL_PAGE_AD_CONFIG = "video_detail_page_ad_config"

/**
 * 首页 slide 广告配置
 */
const val HOME_SLIDE_AD_CONFIG = "home_slide_ad_config"

/**
 * 激励视频数量
 */
const val REWARD_VIDEO_COUNT: String = "reward_video_count"

/**
 * 广告新客保护
 */
const val NEWBIE_AD_PROTECT_CONFIG: String = "newbie_ad_protect_config"

/**
 * 搜索 推荐列表 广告配置 KEY
 */
const val KEY_SEARCH_REC_AD_CONFIG: String = "search_rec_ad_config"

/**
 * 搜索 结果列表 广告配置 KEY
 */
const val KEY_SEARCH_RESULT_AD_CONFIG: String = "search_result_ad_config"

/**
 * Youtube Shorts 填充 广告配置 KEY
 */
const val YOUTUBE_SHORTS_FILL_AD_CONFIG: String = "youtube_shorts_fill_ad_config"

/**
 * 视频详情 返回场景 插屏广告
 */
const val EXIT_VIDEO_DETAIL_INSERT_AD_CONFIG: String = "exit_video_detail_insert_ad_config"

/**
 * 云视频播放 返回场景 插屏广告
 */
const val EXIT_CLOUD_VIDEO_INSERT_AD_CONFIG: String = "exit_cloud_video_insert_ad_config"

/**
 * 添加bt任务成功广告配置
 */
const val BT_TASK_ADD_AD_CONFIG: String = "bt_task_add_ad_config"

/**
 * 添加magnet任务成功广告配置
 */
const val MAGNET_TASK_ADD_AD_CONFIG: String = "magnet_task_add_ad_config"

/**
 * 添加video downloader任务成功广告配置
 */
const val VIDEO_DOWNLOADER_TASK_ADD_AD_CONFIG: String = "video_downloader_task_add_ad_config"

/**
 * 资源圈视频保存弹窗广告配置
 */
const val VIDEO_SAVE_AD_CONFIG: String = "video_save_ad_config"

/**
 * 搜索结果关联资源的类型，0 - 成人&非成人_混排；1 - 非成人；2 - 成人
 */
const val SEARCH_RELATED_RESOURCE_TYPE: String = "search_related_resource_type"


/**
 * 添加广告测试切换参数：
 * value: 0: max， 1： adx , 2: max 和 adx
 */
const val KEY_AD_SDK_TYPE: String = "key_ad_sdk_type"

/**
 * max sdk select init
 */
const val MAX_SDK_SELECT_INIT: String = "max_sdk_select_init"

/**
 * 横向资源图的剪裁比例
 */
const val HORIZONTAL_RESOURCE_IMAGE_CROP_RATIO: String = "horizontal_resource_image_crop_ratio"

/**
 * 是否打开通知栏直接跳转主页面设置drawLayout
 * */
const val NOTIFICATION_BAR_JUMP_SETTINGS_SWITHCER = "notification_bar_jump_settings"

/**
 * 关闭通知栏持续时间
 */
const val NOTIFICATION_BAR_SHOW_AGAIN_TIME_KEY = "notification_bar_show_again_time"

/**
 * 资源小组专题运营区展示样式：值为1时展示长矩形样式，值为2时展示方形样式，默认为1
 */
const val HIVE_GROUP_SUBJECT_UI_SHOW_STYLE: String = "hive_group_subject_ui_show_style"

/**
 * 游戏中心配置
 * */
const val GAME_CENTER_CONFIG_KEY = "game_center_config"

/**
 * 活动中心配置
 * */
const val ACTIVITY_CENTER_CONFIG_KEY = "activity_center_config"

/**
 * 首页Banner运营位配置
 */
const val HOME_BANNER_OPERATION_CONFIG = "home_banner_operation_config"

/**
 * youtube资源是否可以转存
 * */
const val SWITCH_YOUTUBE_SAVE = "youtube_save_switch"

/**
 * 端内Youtube 默认保存方式（云空间：1、本地下载：2、同时选择：1,2，都不选：(空)）
 */
const val YOUTUBE_RES_VIDEO_DEFAULT_SAVE_TYPE = "youtube_res_video_default_save_type"

/**
 * URL Links等通用站点嗅探资源 默认保存方式（云空间-1；本地下载-2；同时选择-1,2；都不选-(空)）
 */
const val WEB_VIDEO_DEFAULT_SAVE_TYPE = "web_video_default_save_type"

/**
 * 域名小流量
 * */
const val DOMAIN_AB_TEST = "domain_ab_test"

/**
 * 控制支持显示哪些来源的内容   搜索发起页和搜索结果页
 * */
const val ENABLE_SEARCH_RES_TYPE = "enable_search_res_type"

/**
 * 外链页视频播放实验
 * 1/2/3 对应实验组：A/B/C 对应策略：弱引导保存/自动保存/试看
 */
const val SHARE_LINK_VIDEO_PLAY_TEST: String = "share_link_video_play_test"

/**
 * 屏蔽成人内容弹窗
 * */
const val BAN_ADULT_SETTING_SWITCHER: String = "enable_content_preference_popup"

/**
 * 需上传abtest配置信息
 * */
const val UPLOAD_ABTEST_INFO_CONFIG: String = "upload_abtest_info_config"

const val SEARCH_SUGGESTION_CONFIG = "search_suggestion_config"

/**
 * 根据渠道分配的搜索suggestion模块展示开关
 */
const val NEW_ENABLE_SEARCH_RESULT_SUG = "new_enable_search_result_sug"

/**
 * 用于控制关注youtube发布者时，是否要求绑定youtube账号
 */
const val ENABLE_YTB_BIND = "enable_ytb_bind"
const val ENABLE_YTB_FOLLOW = "enable_ytb_follow"

/**
 * 资源圈视频切换倍速是否需要看激励视频
 */
const val SHARE_RESOURCE_SWITCH_REWARD_VIDEO_SPEED_UP =
    "resource_circle_switch_reward_video_speed_up"

/**
 * ytb视频切换倍速是否需要看激励视频
 */
const val YOUTUBE_SWITCH_REWARD_VIDEO_SPEED_UP =
    "youtube_switch_reward_video_speed_up"


/**
 * 资源圈视频切换分辨率是否需要看激励视频
 */
const val SHARE_RESOURCE_SWITCH_REWARD_VIDEO_RESOLUTION =
    "resource_switch_reward_video_quality"

/**
 * ytb视频切换分辨率是否需要看激励视频
 */
const val YOUTUBE_SWITCH_REWARD_VIDEO_RESOLUTION =
    "youtube_switch_reward_video_quality"

/**
 * 热搜模块开关
 */
const val ENABLE_HOT_SEARCH = "enable_hot_search"

/**
 * 用于控制首页 大logo和大搜索框是否展示
 */
const val ONLINE_MODE_SEARCH = "online_mode_search"

/**
 * 用于控制首页 speed dial 是否展示
 */
const val ONLINE_MODE_SPEEDDIAL = "online_mode_speeddial"

/**
 * speed dial id 配置
 */
const val SPEED_DIAL_ID = "speed_dial_id"

/**
 * speed dial item 配置:id,name(多语言),url,scheme
 */
const val SPEED_DIAL_ITEM_CONFIG = "speed_dial_item_config"

/**
 * 搜索结果页 - 网页搜索url
 */
const val SEARCH_WEB_URL = "search_web_url"

/**
 * 搜索结果页 - 网页搜索注入js的地址
 */
const val SEARCH_WEB_INJECT_JS_URL = "search_web_inject_js_url"

/**
 * 搜索结果页 - 网页搜索 推荐视频插入的位置
 */
const val SEARCH_WEB_RECOMMEND_VIDEO_INSERT_POSITION = "full_net_search_position"

/**
 * 全网搜禁止跳转独立页的url正则规则
 */
const val SEARCH_WEB_PREVENT_OPEN_INDIVIDUAL_PAGE_URL_REGS =
    "search_web_prevent_open_individual_page_url_regs"

/**
 * 用于控制站长中心tab
 *      是否显示该入口
 *      该入口的 icon
 *      该 h5页面的地址
 */
const val ENABLE_EARN_TAB_SHOW = "enable_earn_tab_show"

/**
 * 视频标签详情页广告开关
 */
const val RESOURCE_TAG_PROFILE_LIST_AD_CONFIG = "tag_detail_page_ad_config"

/**
 * 夸克模式交互开关
 */
const val QUARK_MODE_INTERACTION_SWITCH = "quark_mode_interaction_switch"

/**
 * 启用退出时刷新feed流开关
 */
const val ENABLE_EXIT_BACK_REFRESH = "enable_exit_back_refresh"

/**
 *  桌面LOGO红点控制逻辑
 *  {
 * 	"switch": true/false,  仅在开关是开的情况下生效
 * 	"time": 1440    这是时间间隔,单位分钟
 * }
 */
const val ENBALE_LOGO_REDPOINT = "enbale_logo_redpoint"

/**
 * 赚钱元素外漏到分享开关；转存后快捷分享开关，控制是否显示快捷分享开关
 */
const val ENABLE_SHARE_EARN_INFO = "enable_share_earn_info"

/**
 * 控制新的通知栏常驻通知栏UI是否显示(0:关闭，1：显示新UI)
 */
const val ENABLE_NEW_NOTIFICATION_BAR_UI = "enable_new_notification_bar_ui"

/**
 * 用于控制是否允许显示小红点
 */
const val ENABLE_NOTIFICATION_BAR_RED_POINT = "enable_notification_bar_red_point"

/**
 * shorts视频质量
 */
const val SHORTS_VIDEO_QUALITY = "shorts_video_quality"

/**
 * 展示搜索网页结果的引导视图
 */
const val SHOW_SEARCH_WEB_RESULT_GUIDANCE = "show_search_web_result_guidance"

/**
 * 是否将video Dowloader tab的默认网址显示到书签中
 */
const val ENABLE_DEFAULT_BOOKMARK = "enable_default_bookmark"

/**
 * 探索页下拉后是否在tab上展示刷新按钮
 */
const val ENABLE_EXP_TAB_REFRESH = "enable_exp_tab_refresh"


/**
 * 默认值
 */
@SuppressWarnings("MagicNumber")
val firebaseRemoteConfigDefaultMap = mapOf<String, Any>(
    RESOURCE_GROUP_GUIDE_SWITCH to false,
    RESOURCE_GROUP_TOPIC_SWITCH to false,
    RESOURCE_GROUP_QUESTION_FEED_SWITCH to false,
    SHARE_RESOURCE_SWITCH_REWARD_VIDEO_SPEED_UP to false,
    SHARE_RESOURCE_SWITCH_REWARD_VIDEO_RESOLUTION to false,
    YOUTUBE_SWITCH_REWARD_VIDEO_SPEED_UP to false,
    YOUTUBE_SWITCH_REWARD_VIDEO_RESOLUTION to false,
    AD_HOT_START_TIME_LIMIT_SECONDS to 300,
    AD_HOT_START_TIMES_LIMIT_DAILY to 3,
    SWITCH_HOME_CARD_AD to 1,
    SWITCH_REWARD_VIDEO_QUALITY to 1,
    SWITCH_REWARD_VIDEO_SPEED_UP to 1,
    SWITCH_REWARD_VIDEO_H5_SIGN_IN to 1,
    SWITCH_REWARD_DOWNLOAD to 1,
    FLEXTECH_CLEANER_SWITCH to false,
    FLEXTECH_CLEANER_SWITCH_OUT_OF_GOOGLE to false,
    SHOW_OFFLINE_DOWNLOAD_FUNCTION to false,
    NETWORK_SEARCH_SHIELD_ADDRESS to "[\"google.com\",\"youtube.com\",\"google.com\"]",
    SWITCH_USER_CENTER_AD to 1,
    SWITCH_STORAGE_CLEAN_INSERT_AD to 1,
    SWITCH_TIMELINE_CARD_AD to 1,
    SWITCH_STORAGE_CLEAN_CARD_AD to 1,
    NETWORK_SEARCH_DIRECT_ADDRESS to "[\"tiktok.com\",\"facebook.com\",\"instagram.com\"]",
    NEW_USER_SUBSCRIBE_PREMIUM_GUIDE to 0,
    SWITCH_UPLOAD_TOAST_AD to 0,
    KEY_MAIN_TAB_CLICK_AD_CONFIG to "",
    SWITCH_BACKUP_TOAST_AD to 0,
    SWITCH_HOMEPAGE_GIFT_BOX to 0,
    RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD to 1,
    SWITCH_VIDEO_BONDING_MANUAL_NATIVE_AD to 1,
    SWITCH_VIDEO_BONDING_NO_AD_SHOW to 0,
    SWITCH_HOMEPAGE_AD_AFTER_GIFT_BOX to 0,
    BACKUP_TOAST_AD_TIME_INTERVAL to 60,
    SWITCH_EXIT_APP_DIALOG_AD to 0,
    KEY_COLD_APP_OPEN_AD_WAITING_MAX_DURATION to COLD_APP_OPEN_AD_WAITING_DEFAULT_DURATION,
    SHARE_LINK_VIDEO_PLAY_TEST to AUTO_SAVE,
    // 大图预览ad
    SWITCH_IMAGE_PAGE_PREVIEW to 1,
    AD_IMAGE_PREVIEW_ITEM_INDEXES to "[3,10]",
    SWITCH_DOWNLOAD_TOAST_AD to 0,
    SECURITY_FINGERPRINT_SWITCH to 0,
    SECURITY_FINGERPRINT_TIMEOUT to 5,
    SWITCH_TRANSFER_LIST_AD to 0,
    CONCURRENT_UPLOAD_SWITCH to 0,
    TELEGRAM_GUIDE_JOIN_LINK to "",
    SPEED_UPLOAD_GUIDE_FILE_SIZE_THRESHOLD to 200,
    IS_FIRST_INIT_MAX_HIGH_SPEED_INTER to 1,
    CONCURRENT_UPLOAD_MAX_POOL_SIZE to 4,
    COLD_APP_OPEN_AD_CONFIG to "",
    HOT_APP_OPEN_AD_CONFIG to "",
    AD_IMPRESSION_ONE_DAY_REVENUE_THRESHOLD to -1.0,
    SWITCH_NEW_BLESS_BAG_NATIVE_AD to 0,
    KEY_AD_SDK_TYPE to 0,
    MAX_SDK_SELECT_INIT to 0,
    HORIZONTAL_RESOURCE_IMAGE_CROP_RATIO to 0.3f,

    // 矩阵清理app的广告开关默认都是开的
    CLEANER_SWITCH_APP_OPEN_AD to true,
    CLEANER_SWITCH_APP_MAIN_AD to true,
    CLEANER_SWITCH_APP_SCAN_AD to true,
    CLEANER_SWITCH_APP_SMART_SCAN_RESULT_AD to true,
    CLEANER_SWITCH_APP_SMART_CLEAN_AD to true,
    CLEANER_SWITCH_APP_SMART_CLEAN_RESULT_AD to true,
    CLEANER_SWITCH_APP_NO_NEED_CLEAN_AD to true,
    CLEANER_SWITCH_APP_JUNK_PAGE_AD to true,
    CLEANER_SWITCH_APP_CACHE_LIST_AD to true,
    CLEANER_SWITCH_APP_CLEAN_AD to true,
    CLEANER_SWITCH_APP_CLEAN_RESULT_AD to true,
    CLEANER_SWITCH_APP_CLEAN_RESULT_INSERT_AD to true,
    CLEANER_SWITCH_APP_SMART_CLEAN_RESULT_INSERT_AD to true,
    CLEANER_SWITCH_NO_NEED_CLEAN_RESULT_INSERT_AD to true,
    APP_OPEN_FROM_AD_SWITCH to "",
    HOME_HEAD_LOTTIE_OPEN to true,
    IS_CAN_7_DAYS_TRIAL to false,
    UPLOAD_VIDEO_PREMIUM_SWITCH to false,
    KEY_BLOCK_MONITOR_CONFIG to "",
    SHOW_OPERATE_AND_CLEAN_HOMECARD to true,
    INIT_AD_SDK_ASYNC to false,
    SWITCH_SHARE_LINK_NATIVE_AD to 1,
    SWITCH_CLEAN_BACKED_UP_FILE_AD to 0,
    LOW_PERFORMANCE_DEVICE_SCORE_THRESHOLD to 30,
    // radar 开关
    RESOURCE_RADAR_INFO_CONFIG to "{ \"radarEnable\":false, \"radarFlipMaxCount\":6}",
    RESOURCE_GROUP_SWITCH to 0,
    DOWNLOADER_TAB_SHOW to 0,
    ADULT_GROUP_SWITCH to 0,
    GOLD_CENTER_SWITCH to 1,
    SWITCH_DYNAMIC_MODULE_INIT_SYNC to false,
    VIDEO_DOWNLOADER_WEBSITES to "[{\"name\":\"YouTube\", \"url\": \"https://www.youtube.com\", " +
            "\"icon_url\":\"https://data.${BuildConfig.DEFAULT_DOMAIN}/issue/pavobox/%2Fmoder/youtube_icon.png\"}," +
            " {\"name\":\"Facebook\", \"url\": \"https://www.facebook.com\", " +
            "\"icon_url\":\"https://data.${BuildConfig.DEFAULT_DOMAIN}/issue/pavobox/%2Fmoder/facebook_icon.png\"}," +
            " {\"name\":\"Instagram\", \"url\": \"https://www.instagram.com\", " +
            "\"icon_url\":\"https://data.${BuildConfig.DEFAULT_DOMAIN}/issue/pavobox/%2Fmoder/ins_icon.png\"}," +
            " {\"name\":\"TikTok\", \"url\": \"https://www.tiktok.com\", " +
            "\"icon_url\":\"https://data.${BuildConfig.DEFAULT_DOMAIN}/issue/pavobox/%2Fmoder/tiktok_icon.png\"}," +
            " {\"name\":\"Twitter\", \"url\": \"https://www.twitter.com\", " +
            "\"icon_url\":\"https://data.${BuildConfig.DEFAULT_DOMAIN}/issue/pavobox/%2Fmoder/twitter_icon.png\"}]",
    VIDEO_DOWNLOADER_HOME_CARD_BUTTONS to "[\"bt\",\"link\",\"more\"]",
    APP_LAST_VERSION_INFO to "",
    APP_LAST_VERSION_DATA to "",
    PKL_SWITCHER to false,
    CF_STORE_AUDIT_ADAPTATION to "[]", //应用商店配置相关参数
    REWARD_VIDEO_COUNT to "",
    BT_TASK_ADD_AD_CONFIG to "{\"switch\":true,\"protect_times\":1,\"time_limited\":99}",
    MAGNET_TASK_ADD_AD_CONFIG to "{\"switch\":true,\"protect_times\":1,\"time_limited\":99}",
    VIDEO_DOWNLOADER_TASK_ADD_AD_CONFIG to "{\"switch\":true,\"protect_times\":1,\"time_limited\":99}",
    VIDEO_SAVE_AD_CONFIG to "{\"switch\":true,\"protect_times\":1,\"time_limited\":99}",
    SEARCH_RELATED_RESOURCE_TYPE to 0,
    NOTIFICATION_BAR_JUMP_SETTINGS_SWITHCER to false,
    NOTIFICATION_BAR_SHOW_AGAIN_TIME_KEY to 0,
    ACCOUNT_SYNC_SWITCH to true,
    HIVE_GROUP_SUBJECT_UI_SHOW_STYLE to 1,
    SWITCH_YOUTUBE_SAVE to "",
    DUAL_PROCESS_SWITCHER to false,
    YOUTUBE_RES_VIDEO_DEFAULT_SAVE_TYPE to "1",
    WEB_VIDEO_DEFAULT_SAVE_TYPE to "1",
    DOMAIN_AB_TEST to -1,
    BAN_ADULT_SETTING_SWITCHER to "{\"diff_channel_config\":{ \"google_play\":1,\"pavo_webpage\":0}}",
    UPLOAD_ABTEST_INFO_CONFIG to "[]",
    ENABLE_HOT_SEARCH to "{\"diff_channel_config\":{ \"google_play\":0,\"pavo_webpage\":0}}",
    ONLINE_MODE_SEARCH to "{\"diff_channel_config\":{ \"google_play\":0,\"pavo_webpage\":0}}",
    ONLINE_MODE_SPEEDDIAL to "{\"diff_channel_config\":{ \"google_play\":0,\"pavo_webpage\":0}}",
    YOUTUBE_VIDEO_QUALITY to 480,
    SHARE_RESOURCE_VIDEO_QUALITY to 480,
    SHORTS_VIDEO_QUALITY to 480,
    SEARCH_WEB_URL to "https://duckduckgo.com/?q=<<searchText>>&ia=web",
    SEARCH_WEB_RECOMMEND_VIDEO_INSERT_POSITION to 1,
    SEARCH_WEB_PREVENT_OPEN_INDIVIDUAL_PAGE_URL_REGS to """
        {
          "diff_channel_config": {
            "google_play": [
              "^(https://|http://)?(m|www)\.bing\.com/search\?.*\$}"
            ],
            "pavo_webpage": [
              "^(https://|http://)?(m|www)\.bing\.com/search\?.*\&"
            ]
          }
        }
    """.trimIndent(),
    QUARK_MODE_INTERACTION_SWITCH to false,
    ENBALE_LOGO_REDPOINT to """
        {
        	"switch": false,
        	"time": 0 
        }
    """.trimIndent(),
    ENABLE_NEW_NOTIFICATION_BAR_UI to 1L,
    ENABLE_NOTIFICATION_BAR_RED_POINT to """
        {
        	"switch": true,
	        "time": 1440,
            "times":-1
        }
    """.trimIndent(),
    ENABLE_EXP_TAB_REFRESH to INT_1,
    SEARCH_WEB_PREVENT_OPEN_INDIVIDUAL_PAGE_URL_REGS to """^(https://|http://)?([^.]*\.)?bing\.com(?!/videos/search).*$""",
    ENABLE_DEFAULT_BOOKMARK to false
)

/**
 * 获取继续分享赚钱开关是否打开
 */
fun getenableDefaultBookmark(): Boolean {
    // 需要等待[分享入口改造]开关完成共用一个。暂时没有判断逻辑
    return DuboxRemoteConfig.getBoolean(ENABLE_DEFAULT_BOOKMARK)
}

/**
 * 获取继续分享赚钱开关是否打开
 */
fun getEnableShareEarnInfo(): Boolean {
    // 需要等待[分享入口改造]开关完成共用一个。
    return WebMasterManager.needShowEarnPlanInShare()
}

/**
 * 获取是否显示新的常驻通知栏UI
 * @return T:显示新UI，F:显示旧的UI
 */
fun getIsShowNewNotifyBarUI(): Boolean {
    return if (BaseApplication.getContext().isMainProcess) {
        val conf: Long = DuboxRemoteConfig.getLong(ENABLE_NEW_NOTIFICATION_BAR_UI)
        GlobalConfig.getInstance().putLong(ENABLE_NEW_NOTIFICATION_BAR_UI, conf)
        conf
    } else {
        GlobalConfig.getInstance().getLong(ENABLE_NEW_NOTIFICATION_BAR_UI, 0L)
    } == 1L
}

/**
 * 获取通知栏小红点得配置
 * @return
 */
fun getNotificationRedPointConfig(configUpdateListener: () -> Unit): NotificationReadPointConfig {
    return runCatching {
        Gson().fromJson(
            if (BaseApplication.getContext().isMainProcess) {
                val conf = DuboxRemoteConfig.getString(ENABLE_NOTIFICATION_BAR_RED_POINT)
                val localConf =
                    GlobalConfig.getInstance().getString(ENABLE_NOTIFICATION_BAR_RED_POINT)
                if (conf != localConf) {
                    // 配置发生了变化,清除本地统计信息
                    configUpdateListener.invoke()
                }
                GlobalConfig.getInstance().putString(ENABLE_NOTIFICATION_BAR_RED_POINT, conf)
                conf
            } else {
                GlobalConfig.getInstance().getString(ENABLE_NOTIFICATION_BAR_RED_POINT)
            },
            NotificationReadPointConfig::class.java
        )
    }.getOrNull() ?: NotificationReadPointConfig()
}

/**
 * 资源圈开关
 */
fun isShareResourceOpen(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    return DuboxRemoteConfig.getLong(SHARE_RESOURCE_SWITCH_NEW).toInt() == INT_1
}

/**
 * 清理功能开关
 */
@Suppress("FunctionOnlyReturningConstant")
fun isFlexTechCleanerOpen(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    if (AppCommon.isGoogleChannel()) {
        return DuboxRemoteConfig.getBoolean(FLEXTECH_CLEANER_SWITCH)
    }
    return DuboxRemoteConfig.getBoolean(FLEXTECH_CLEANER_SWITCH_OUT_OF_GOOGLE)
}

/**
 * 远程下载开关
 */
fun isOfflineDownloadOpen(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    return DuboxRemoteConfig.getBoolean(SHOW_OFFLINE_DOWNLOAD_FUNCTION)
}

/**
 * 获取资源圈视频播放清晰度配置
 */
fun getShareResourceVideoQuality(): Long {
    return DuboxRemoteConfig.getLong(SHARE_RESOURCE_VIDEO_QUALITY)
}

/**
 * 获取ytb中视频播放清晰度配置
 */
fun getYtbVideoQuality(): Long {
    return DuboxRemoteConfig.getLong(YOUTUBE_VIDEO_QUALITY)
}

/**
 * 资源圈视频分辨率。默认播放360p
 */
fun getShareResourceVideoPlayType(): String {
    return when (getShareResourceVideoQuality()) {
        SHARE_RESOURCE_VIDEO_QUALITY_360 -> M3U8_AUTO_360
        SHARE_RESOURCE_VIDEO_QUALITY_480 -> M3U8_AUTO_480
        SHARE_RESOURCE_VIDEO_QUALITY_720 -> M3U8_AUTO_720
        SHARE_RESOURCE_VIDEO_QUALITY_1080 -> M3U8_AUTO_1080
        else -> M3U8_AUTO_360
    }
}

/**
 * 资源圈视频分辨率。默认播放360p
 */
fun getShareResourceVideoPlayType(resolution: Long): String {
    return when (resolution) {
        SHARE_RESOURCE_VIDEO_QUALITY_360 -> M3U8_AUTO_360
        SHARE_RESOURCE_VIDEO_QUALITY_480 -> M3U8_AUTO_480
        SHARE_RESOURCE_VIDEO_QUALITY_720 -> M3U8_AUTO_720
        SHARE_RESOURCE_VIDEO_QUALITY_1080 -> M3U8_AUTO_1080
        else -> M3U8_AUTO_360
    }
}

/**
 * 是否打开全网搜索
 */
fun isOpenNetworkSearch(): Boolean {
    return DuboxRemoteConfig.getLong(NETWORK_SEARCH_SWITCH) == 1L
}

/**
 * 全网搜索屏蔽网址数组
 */
fun getNetworkSearchShieldAddress(): Array<String> {
    val urlsStr = DuboxRemoteConfig.getString(NETWORK_SEARCH_SHIELD_ADDRESS)
    if (urlsStr.isNullOrBlank())
        return emptyArray()
    return runCatching<Array<String>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<String>>() {}.type)
    }.getOrNull()?.map {
        it.trim()
    }?.toTypedArray() ?: emptyArray()
}

/**
 * 全网搜索直达网址数组
 */
fun getNetworkSearchDirectAddress(): Array<String> {
    val urlsStr = DuboxRemoteConfig.getString(NETWORK_SEARCH_DIRECT_ADDRESS)
    if (urlsStr.isNullOrBlank())
        return emptyArray()
    return runCatching<Array<String>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<String>>() {}.type)
    }.getOrNull() ?: emptyArray()
}

/**
 * 获取video downloader网站列表
 */
fun getVideoDownloaderWebsites(): Array<VDConfigWebsite> {
    val urlsStr = DuboxRemoteConfig.getString(VIDEO_DOWNLOADER_WEBSITES)
    if (urlsStr.isBlank()) {
        return emptyArray()
    }
    return runCatching<Array<VDConfigWebsite>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<VDConfigWebsite>>() {}.type)
    }.getOrNull() ?: emptyArray()
}

/**
 * 获取活动中心配置
 * */
fun getActivityCenterConfig(): Array<OperationConfig> {
    val urlsStr = DuboxRemoteConfig.getString(ACTIVITY_CENTER_CONFIG_KEY)
    if (urlsStr.isBlank()) {
        return emptyArray()
    }
    return runCatching<Array<OperationConfig>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<OperationConfig>>() {}.type)
    }.getOrNull() ?: emptyArray()
}

/**
 * 获取游戏中心配置
 * */
fun getGameCenterConfig(): Array<OperationConfig> {
    val urlsStr = DuboxRemoteConfig.getString(GAME_CENTER_CONFIG_KEY)
    if (urlsStr.isBlank()) {
        return emptyArray()
    }
    return runCatching<Array<OperationConfig>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<OperationConfig>>() {}.type)
    }.getOrNull() ?: emptyArray()
}

/**
 * 获取video downloader home card 配置。(bt link more的显示)
 */
fun getVideoDownloaderHomeCardConfigs(): Array<String> {
    val urlsStr = DuboxRemoteConfig.getString(VIDEO_DOWNLOADER_HOME_CARD_BUTTONS)
    if (urlsStr.isBlank()) {
        return emptyArray()
    }
    return runCatching<Array<String>> {
        Gson().fromJson(urlsStr, object : TypeToken<Array<String>>() {}.type)
    }.getOrNull() ?: emptyArray()
}

/***
 * 获取视频播放拦截时长
 * 如果 Firebase 配置 0L 则认为不显示转存逻辑，但是 Firebase 取配置的值时，如果没有配置返回的是 0L, 这样就
 * 会有歧义，所以配置成 string 类型，和 IOS 已同步
 */
fun getVideoInterceptorDuration(): Long {
    var duration = DuboxRemoteConfig.getString(VIDEO_INTERCEPTOR_DURATION)
    if (TextUtils.isEmpty(duration)) {
        return TimeUnit.MINUTES.toMillis(CONSTANT_5) / TIME_UNIT_1000
    }
    return try {
        duration.toLong()
    } catch (e: Exception) {
        TimeUnit.MINUTES.toMillis(CONSTANT_5) / TIME_UNIT_1000
    }
}

/***
 * 获取视频播放拦截时长
 * 如果 Firebase 配置 0L 则认为不显示转存逻辑，但是 Firebase 取配置的值时，如果没有配置返回的是 0L, 这样就
 * 会有歧义，所以配置成 string 类型，和 IOS 已同步
 */
fun getVideoInterceptorMinute(): Int {
    var duration = DuboxRemoteConfig.getString(VIDEO_INTERCEPTOR_DURATION)
    if (TextUtils.isEmpty(duration)) {
        return INT_5
    }
    return try {
        (duration.toLong() / INT_60).toInt()
    } catch (e: Exception) {
        INT_5
    }
}

/**
 * 获取搜索框提示文案
 */
fun getSearchHintContent(context: Context): ArrayList<String> {
    return arrayListOf(context.getString(R.string.header_search_hint))
}

/**
 * 防止运行时firebase config更新
 */
//val isOpenTradPlusAdSdk by lazy { false }


/**
 * 搜索结果展示信息开关状态
 * true: 展示文件位置 false:不展示文件位置
 */
fun isSearchResultShowFileLocation(): Boolean {
    return DuboxRemoteConfig.getBoolean(SEARCH_RESULT_DISPLAY_INFO_SWITCH)
}

/**
 * LGE K22是否打开常驻通知栏
 */
fun isK22OpenPermanentNotification(): Boolean {
    return DuboxRemoteConfig.getBoolean(K22_PERMANENT_NOTIFICATION_SWITCH)
}

/**
 * 新用户是否需要展示用户权益引导页面
 */
fun isShowSubscribePage(): Boolean {
    return DuboxRemoteConfig.getBoolean(NEW_USER_SUBSCRIBE_PREMIUM_GUIDE)
}

/**
 * 是否展示首页顶部会员 icon 广告
 */
fun isShowHomeVipIconAd(): Boolean {
    return DuboxRemoteConfig.getBoolean(SWITCH_HOMEPAGE_GIFT_BOX)
}

/**
 * 自动备份广告展示间隔, 默认间隔时间 1 分钟
 * 配置时以秒为单位
 */
fun getBackupAdInterval(): Long {
    val time = DuboxRemoteConfig.getString(BACKUP_TOAST_AD_TIME_INTERVAL)
    val minute = try {
        time.toInt()
    } catch (e: Exception) {
        INT_60
    }
    return TimeUnit.SECONDS.toMillis(minute.toLong())
}

/**
 * 自动备份广告展示次数，一天最多展示两次
 */
fun getBackupAdTimes(): Int {
    val time = DuboxRemoteConfig.getString(BACKUP_TOAST_AD_DAY_TIMES)
    return try {
        time.toInt()
    } catch (e: Exception) {
        CONSTANT_2
    }
}

/**
 * Widget 引导时间间隔，默认 72 小时
 * 配置时以小时为单位
 */
fun getWidgetGuideInterval(): Long {
    val time = DuboxRemoteConfig.getString(WIDGET_GUIDE_SHOW_INTERVAL)
    val minute = try {
        time.toInt()
    } catch (e: Exception) {
        INT_72
    }
    return TimeUnit.HOURS.toMillis(minute.toLong())
}

/**
 * 外链页视频播放实验
 * 1/2/3 对应实验组：A/B/C 对应策略：弱引导保存/自动保存/试看
 */
fun getShareLinkVideoPlayStrategy(): Long {
    var group = DuboxRemoteConfig.getLong(SHARE_LINK_VIDEO_PLAY_TEST)
    if (group == AUTO_SAVE) {
        if (PersonalConfig.getInstance()
                .getBoolean(PersonalConfigKey.SHARE_LINK_PLAY_VIDEO_DOWNGRADE_TO_A, false)
        ) {
            return NOTIFY_SAVE
        }
    }
    return group
}

/**
 * 获取商店审核适配的配置参数
 * @return 是一个集合，是为了适配一个版本多个渠道存在差异化审核结果
 */
fun getStoreAuditAdaptationConfig(): MutableList<StoreAuditAdaptationConfig> {
    var result = arrayOf<StoreAuditAdaptationConfig>()
    try {
        result = Gson().fromJson(
            DuboxRemoteConfig.getString(CF_STORE_AUDIT_ADAPTATION).apply {
                "[远程配置]-商店审核配置为json：$this".d()
            },
            result.javaClass
        )
        "[远程配置]-商店审核配置解析成功,长度为${result.size}".d()
    } catch (e: Exception) {
        "[远程配置]-商店审核配置解析处理异常,e=$e".d()
    }
    return result.toMutableList()
}

/**
 * 上传视频会员特权开关是否打开
 */
fun isUploadVideoPremiumSwitchOpen(): Boolean {
    return DuboxRemoteConfig.getBoolean(UPLOAD_VIDEO_PREMIUM_SWITCH)
}

/**
 * 视频上传尺寸限制触发的阈值，单位B
 */
fun videoUploadSizeLimit(): Long {
    return try {
        DuboxRemoteConfig.getString(VIP_VIDEO_UPLOAD_SIZE_LIMIT).toLong() * MB_1
    } catch (e: Exception) {
        DEFAULT_UPLOAD_VIDEO_SIZE_LIMIT
    }
}

/**
 * 雷达配置
 */
fun getRadarConfig(): RadarConfig? {
    return try {
        Gson().fromJson(
            DuboxRemoteConfig.getString(RESOURCE_RADAR_INFO_CONFIG),
            RadarConfig::class.java
        )
    } catch (e: Exception) {
        null
    }
}

/**
 * 雷达开关
 */
fun getRadarSwitch(): Boolean {
    return (getRadarConfig()?.enable ?: false) && !StoreAuditAdaptationUtil.getIns()
        .checkCurrentChannelNoIsUnderReview()
}

/**
 * 账号保活开关
 */
fun accountSyncKeepAliveSwitch(): Boolean {
    return DuboxRemoteConfig.getBoolean(ACCOUNT_SYNC_SWITCH)
}

/**
 * 资源小组开关
 */
fun resourceGroupSwitch(): Boolean {
    return !StoreAuditAdaptationUtil.getIns().checkCurrentChannelNoIsUnderReview()
            && DuboxRemoteConfig.getBoolean(RESOURCE_GROUP_SWITCH)
//    return true
}

/**
 * 雷达开关
 * */
fun radarSwitch(): Boolean {
    return DuboxRemoteConfig.getBoolean(RADAR_SWITCH)
}

/**
 * Download Tab开关
 * downloader_tab_show=true：跳转到 Downloader tab；
 * downloader_tab_show=false：跳转到 原来的 Video Downloader页；
 */
fun downloadTabShow(): Boolean {
    return DuboxRemoteConfig.getBoolean(DOWNLOADER_TAB_SHOW)
//    return true
}

/**
 * 关闭通知栏是否进入主页面设置
 * */
fun notificationEnterMainSetting(): Boolean {
    return DuboxRemoteConfig.getBoolean(NOTIFICATION_BAR_JUMP_SETTINGS_SWITHCER)
}


/**
 * 资源小组成人开关
 */
fun resourceGroupAduSwitch(): Boolean {
    return DuboxRemoteConfig.getBoolean(ADULT_GROUP_SWITCH)
}

/**
 * 首页slide标题是否获取成功，它获取失败的话，首页下面那些视频slide不会展示
 */
fun isResAggregationLoadSuccess(): Boolean {
    val config = DuboxRemoteConfig.getString(RES_AGGREGATION_TITLE)
    return config.isNotEmpty()
}

/**
 * 搜索suggestion词条配置
 */
fun getSearchSuggestions(): List<String> {
    val config = DuboxRemoteConfig.getString(SEARCH_SUGGESTION_CONFIG)
    if (config.isEmpty()) {
        return emptyList()
    }
    return try {
        val jsonObject = JSONObject(config).getJSONObject("suggestion")
        val suggestions =
            jsonObject.getJSONObject(getLanguageInSupported(true)).getJSONArray("list")
        val list = mutableListOf<String>()
        for (i in 0 until suggestions.length()) {
            list.add(suggestions.getString(i))
        }
        list
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

/**
 * 判断一个文案是不是搜索推荐的关键词
 * @param text String
 * @return Boolean
 */
fun isSearchSuggestionKeyword(text: String): Boolean {
    return getSearchSuggestions().contains(text)
            && enableSearchResultSuggestion()
}

/**
 * 是否开启搜索suggestion模块展示开关
 */
fun enableSearchResultSuggestion(): Boolean {
    return DuboxRemoteConfig.getLong(NEW_ENABLE_SEARCH_RESULT_SUG).toInt() == INT_1
}

/**
 * 获取搜索框提示配置
 * @return 返回的集合不为空，但可能只有一句文案
 */
fun getResAggregationTitle(): ArrayMap<String, String>? {
    val config = DuboxRemoteConfig.getString(RES_AGGREGATION_TITLE)
    if (config.isEmpty()) {
        return null
    }
    try {
        val jsonObject = JSONObject(config).getJSONObject("aggregation")
        val localData =
            jsonObject.getJSONObject(getLanguageInSupported(true)).toString()
                .replace("{", "")
                .replace("}", "")

        val split = localData.split(",")
        val map = ArrayMap<String, String>()
        split.forEach {
            val item = it.replace("\"", "").split(":")
            map[item[0]] = item[1]
        }
        return map
    } catch (e: Exception) {
        e.e()
    }
    return null
}


fun getPKLSwitcher(): Boolean {
    return DuboxRemoteConfig.getBoolean(PKL_SWITCHER)
}

/**
 * youtube转存开关 在列表中的是禁止转存的渠道
 * @return true 代表打开开关可以转存 false 代表关闭开关不可转存
 * */
fun getYtbSwitcher(): Boolean {
    val list = try {
        Gson().fromJson(
            DuboxRemoteConfig.getString(SWITCH_YOUTUBE_SAVE),
            ArrayList<String>().javaClass
        ) ?: ArrayList<String>()
    } catch (e: JsonSyntaxException) {
        ArrayList<String>()
    }
    return !list.contains(AppCommon.CHANNEL_NUM)
}

fun getDualProcessRemoteSwitcher(): Boolean {
    return DuboxRemoteConfig.getBoolean(DUAL_PROCESS_SWITCHER)
}

/**
 * 获取升级弹窗信息配置
 */
fun getNewVersionInfoConfig(): NewVersionInfo? {
    val defaultInfo = NewVersionInfo(
        AppCommon.VERSION_DEFINED,
        "",
        listOf(
            "$INT_1." + BaseApplication.getInstance().getString(R.string.new_version_msg_bug_fixes),
            "$INT_2." + BaseApplication.getInstance()
                .getString(R.string.new_version_msg_optimized_for_better_experience)
        ),
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
    return try {
        Gson().fromJson(
            DuboxRemoteConfig.getString(APP_LAST_VERSION_INFO),
            NewVersionInfo::class.java
        ) ?: defaultInfo
    } catch (e: JsonSyntaxException) {
        defaultInfo
    }
}


/**
 * 获取新版本下载信息配置
 */
fun getNewVersionDownloadDataConfig(): Version? {
    return try {
        Gson().fromJson(
            DuboxRemoteConfig.getString(APP_LAST_VERSION_DATA),
            Version::class.java
        ) ?: null
    } catch (e: JsonSyntaxException) {
        null
    }
}

/**
 * 根据远程配置判断是否显示广告
 */
fun isShowAdByConfig(key: String): Boolean {
    val config = getAdFirebaseConfig(key) ?: return false
    val launchTime = GlobalConfig.getInstance().getInt(GlobalConfigKey.LAUNCH_APP_TIMES, 0)
    return config.switch && launchTime > config.protectTimes || BuildConfig.DEBUG
}

/**
 * 获取广告 firebase 配置
 */
fun getAdFirebaseConfig(config: String): AdRemoteConfig? {
    return try {
        Gson().fromJson(DuboxRemoteConfig.getString(config), AdRemoteConfig::class.java) ?: null
    } catch (e: Exception) {
        null
    }
}

/**
 * 根据远程配置的每日显示上限，判断否显示广告
 * @since 3.7.0 bt\videoDownaloder\资源圈转存的场景广告使用
 */
fun isShowAdByFrequencyControl(key: String): Boolean {
    val config = getAdFirebaseConfig(key) ?: return false
    val times: List<String> =
        GlobalConfig.getInstance().getString(key)
            .split(",")
            .filter {
                it.toLongOrNull()?.isSameDateWithToday() ?: false
            }.toMutableList()
    "$key AD show times ${times.size}".d("isShowAdByFrequencyControl")
    return times.size < config.timeLimited
}

/**
 * 记录key对应的广告的显示时间
 * @since 3.7.0 bt\videoDownaloder\资源圈转存的场景广告使用
 */
fun recodeAdShowTime(key: String) {
    if (key.isEmpty()) return
    val times: List<String> =
        GlobalConfig.getInstance().getString(key)
            .split(",")
            .filter {
                it.toLongOrNull()?.isSameDateWithToday() ?: false
            }.toMutableList()
            .plus(System.currentTimeMillis().toString())
    val timeStamps = times.reduce { acc, s -> "$acc,$s" }
    "$key AD show time: $timeStamps".d("recodeAdShowTime")
    GlobalConfig.getInstance().putString(key, timeStamps)
}

/**
 * AD-新客保护
 */
fun getNewbieAdProtectConfig(): AdNewbieProtectConfig {
    return runCatching {
        Gson().fromJson(
            DuboxRemoteConfig.getString(NEWBIE_AD_PROTECT_CONFIG),
            AdNewbieProtectConfig::class.java
        )
    }.getOrNull() ?: AdNewbieProtectConfig()
}

fun getDomainABTestRange(): Long {
    return DuboxRemoteConfig.getLong(DOMAIN_AB_TEST)
}

/**
 * 获取当前渠道搜索页应该展示哪些来源的内容
 */
fun getFirebaseSearchResType(): List<Int> {
    /*
    * json格式:
    * {
        "gp":[1,2,3,4],
        "web":[1,2,3,4]
        }
    * */
    return runCatching {
        val searchResTypeConfig = DuboxRemoteConfig.getString(ENABLE_SEARCH_RES_TYPE)

        if (searchResTypeConfig.isEmpty()) {
            return emptyList()
        }
        val jsonConfig = JSONObject(searchResTypeConfig)

        fun getConfigList(jsonConfig: JSONObject, key: String): List<Int> {
            val jsonArray = jsonConfig.optJSONArray(key) ?: return emptyList()
            val jsonArraySize = jsonArray.length()
            val listResult = mutableListOf<Int>()
            for (i in 0 until jsonArraySize) {
                listResult.add(jsonArray.optInt(i))
            }
            return listResult
        }

        getConfigList(
            jsonConfig,
            if (isGoogleChannel()) GOOGLE_PLAY_CHANNEL else PAVO_WEBPAGE_CHANNEL
        )
    }.getOrDefault(emptyList())
}

/**
 * 获取成人开关 1是开，0是关
 * */
fun getBanAdultSwitcher(): Boolean {
    val res = DuboxRemoteConfig.getLong(BAN_ADULT_SETTING_SWITCHER).toInt()
    return res == INT_1
}

/**
 * 获取需要上传的abtest实验配置
 * */
fun getAbTestUploadInfo(): List<String> {
    val jsonStr = DuboxRemoteConfig.getString(UPLOAD_ABTEST_INFO_CONFIG)
    return if (jsonStr.isEmpty()) {
        emptyList()
    } else {
        runCatching {
            Gson().fromJson(jsonStr, ArrayList<String>().javaClass)
        }.getOrNull() ?: emptyList()
    }
}

/**
 * 获取: 关注youtube发布者时，是否要求绑定youtube账号
 */
fun getEnableYtbBind(): Boolean {
    return DuboxRemoteConfig.getLong(ENABLE_YTB_BIND).toInt() == INT_1
}

/**
 * 热搜模块开关
 */
fun isEnableHotSearch(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    return DuboxRemoteConfig.getLong(ENABLE_HOT_SEARCH).toInt() == INT_1
}

/**
 * 允许Youtube视频关注功能
 * @return Boolean
 */
fun enableYtbFollow(): Boolean {
    return getEnableYtbBind() && DuboxRemoteConfig.getLong(ENABLE_YTB_FOLLOW).toInt() == INT_1
}

/**
 * 用于控制首页 大logo和大搜索框是否展示
 */
fun isEnableOnlineModeSearch(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    return DuboxRemoteConfig.getLong(ONLINE_MODE_SEARCH).toInt() == INT_1
}

/**
 * 用于控制首页 speed dial 是否展示
 */
fun isEnableOnlineModeSpeedDial(): Boolean {
    if (GlobalConfig.getInstance().getBoolean(GlobalConfigKey.IS_TEST_MODEL, false)) {
        return true
    }
    return DuboxRemoteConfig.getLong(ONLINE_MODE_SPEEDDIAL).toInt() == INT_1
}

/**
 * 获取speed dial id 配置(已区分渠道)
 */
fun getSpeedDialIdConfig(): String {
    return DuboxRemoteConfig.getString(SPEED_DIAL_ID)
}

/**
 * 线下渠道speed dial item 配置
 */
fun getSpeedDialItemConfig(): String {
    return DuboxRemoteConfig.getString(SPEED_DIAL_ITEM_CONFIG)
}

/**
 * 流畅播开关
 */
fun getEnableQuickPlay(): Boolean {
    return DuboxRemoteConfig.getLong(ENABLE_QUICK_PLAY).toInt() == INT_1
}

/**
 * 获取站长中心配置
 * @return EarnTabConfig
 */
fun getEarnTabConfig(): EarnTabConfig {
    return runCatching {
        Gson().fromJson(
            DuboxRemoteConfig.getString(ENABLE_EARN_TAB_SHOW),
            EarnTabConfig::class.java
        )
    }.getOrNull() ?: EarnTabConfig()
}

/**
 * 夸克模式交互开关 true: 用新的夸克模式的交互方式  false: 用旧的交互方式
 */
fun getQuarkModeInteractionSwitcher(): Boolean {
    // PM: 只有当online_mode_search =开时，新交互才会生效
    // PM: 所以online_mode_search =false 时，新交互都是false
    return if (isEnableOnlineModeSearch()) {
        isEnableQuarkMode()
    } else {
        false
    }
}

/**
 * 夸克模式开关
 * @return Boolean
 */
fun isEnableQuarkMode(): Boolean {
    return DuboxRemoteConfig.getBoolean(QUARK_MODE_INTERACTION_SWITCH)
}

/**
 * 退出时刷新功能是否启用
 */
fun isEnableExitBackRefresh(): Boolean {
    return DuboxRemoteConfig.getBoolean(ENABLE_EXIT_BACK_REFRESH)
}

private const val SWITCH = "switch"
private const val TIME = "time"

/**
 * 获取logo红点配置
 * 第一个是开关，第二个是时长(单位:分钟)
 */
fun getEnableLogoRedPointConfig(): Pair<Boolean, Long> {
    return runCatching {
        val jsonObject = JSONObject(DuboxRemoteConfig.getString(ENBALE_LOGO_REDPOINT))
        Pair(jsonObject.optBoolean(SWITCH, false), jsonObject.optLong(TIME, LONG_0))
    }.getOrDefault(Pair(false, LONG_0))
}

/**
 * 获取赚钱元素外漏到分享开关配置；转存后快捷分享开关配置。
 */
fun getEnableShareEarnInfoConfig(): ShareEarnInfo {
    return runCatching {
        GsonUtils.gson.fromJson<ShareEarnInfo>(
            DuboxRemoteConfig.getString(ENABLE_SHARE_EARN_INFO)
        )
    }.onFailure { it.e("DDDD") }.getOrNull() ?: ShareEarnInfo()
}

/**
 * 探索页下拉后是否在tab上展示刷新按钮
 * @return Boolean
 */
fun showRefreshOnExploreTab(): Boolean {
    return DuboxRemoteConfig.getBoolean(ENABLE_EXP_TAB_REFRESH)
}