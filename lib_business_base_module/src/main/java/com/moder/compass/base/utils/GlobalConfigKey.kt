package com.moder.compass.base.utils;

/**
 * 常量
 */
object GlobalConfigKey {

    /**
     * 百度网盘视频播放屏幕亮度设置key
     */
    const val KEY_SCREEN_BRITNESS = "KEY_SCREEN_BRITNESS"

    /**
     * 本地IP
     */
    const val CLIENT_IP = "client_ip"

    /**
     * 仅在wifi下上传下载的引导提示，一次登录只提醒一次（wifi时的引导）
     */
    const val ON_WIFI_CONFIG_TIPS = "on_wifi_config_switch_tips"


    /**
     * 视频贴片广告Token
     */
    const val LAST_VIDEO_AD_TOKEN = "LAST_VIDEO_AD_TOKEN"

    /**
     * 视频sdk的版本号
     */
    const val VAST_PLAYER_VERSION = "vast_player_version"

    /**
     * 记录用户是否已经点击过，首页隐私弹窗的同意按钮
     * @since 9.6.70
     */
    const val AGREEMENT_DIALOG_OK_BTN_CLICKED = "privacy_dialog_agree_btn_clicked"
    /**
     * p2p service文件更新的文件路径
     */
    const val KEY_P2P_SERVICE_FILE_PATH = "key_p2p_service_file_path"

    /**
     * passport模块需要的公参psign
     */
    const val SERVER_PASSPORT_PSIGN = "server_passport_psign"

    /**
     * 是否弹出过评分引导弹窗
     */
    const val IS_SHOWED_RATING_DIALOG = "is_showed_rating_dialog"

    /**
     *  启动app的次数
     */
    const val LAUNCH_APP_TIMES = "launch_app_times"

    /**
     * p2p 版本号
     */
    const val P2P_VERSION: String = "p2p_version"

    /**
     * 网络状态
     */
    const val NETWORK_TYPE: String = "network_type"

    /**
     * 谷歌渠道是否强制升级
     */
    const val IS_GOOGLE_CHANNEL_FORCE_UPDATE: String = "is_google_channel_force_update"
    /**
     * 闲时备份时间间隔范围（小时）最小值
     */
    const val WORK_MANAGER_INTERVAL_HOURS_MIN	: String = "work_manager_interval_hours_min"
    /**
     * 闲时备份时间间隔范围（小时）最大值
     */
    const val WORK_MANAGER_INTERVAL_HOURS_MAX	: String = "work_manager_interval_hours_max"
    /**
     * 帐号同步时间间隔范围（分钟）最小值
     */
    const val ACCOUNT_SYNC_INTERVAL_MINUTE_MIN	: String = "account_sync_interval_minute_min"
    /**
     * 帐号同步时间间隔范围（分钟）最大值
     */
    const val ACCOUNT_SYNC_INTERVAL_MINUTE_MAX	: String = "account_sync_interval_minute_max"

    /**
     * 五天内是否出现过(文件tab)
     */
    const val GUIDEPOWERPLAN_APPEAR_5DAY = "guidepowerplan_appear_5day"

    /**
     * 2.1.5
     * 上次上报手机可用内存时间戳
     */
    const val DEVICE_SPACE_UPLOAD_TIME = "device_space_upload_time_new"

    /**
     * 2.1.5
     * 上次上报时间戳
     */
    const val PRELOAD_LAST_UPLOAD_TIME = "preload_last_upload_time_new"
    /**
     * 2.1.5
     * 上次单条上报时间戳
     */
    const val PRELOAD_PER_LAST_UPLOAD_TIME = "preload_per_last_upload_time_new"

    /**
     * 2.1.5
     * 当天上报次数
     */
    const val PRELOAD_UPLOAD_TIMES = "preload_upload_times_new"


    /**
     * 观看视频上次上报时间
     */
    const val LAST_UPLOAD_WATCH_VIDEO_TIME = "last_upload_watch_video_time"

    /**
     * 观看视频累计时长
     */
    const val WATCH_VIDEO_TOTAL_TIME = "watch_video_total_time"

    /**
     * 观看照片上次上报时间
     */
    const val LAST_UPLOAD_VIEW_PHOTO_TIME = "last_upload_view_photo_time"

    /**
     * 观看照片累计数量
     */
    const val VIEW_PHOTO_TOTAL_PICS = "view_photo_total_pics"

    /**
     * 上次使用app上报时间
     */
    const val LAST_UPLOAD_USE_APP_TIME = "last_upload_use_app_time"

    /**
     * 在7x24时间段内app累次使用次数
     */
    const val USE_APP_TIMES = "use_app_times"

    /**
     * TinyConverter下载版本号
     * 10.1.40
     */
    const val KEY_PLUGIN_TINY_CONVERTER_DOWNLOAD_VERSION = "key_plugin_tiny_converter_download_version"

    /**
     * TinyConverter下载zip路径
     * 10.1.40
     */
    const val KEY_PLUGIN_TINY_CONVERTER_DOWNLOAD_PATH = "key_plugin_tiny_converter_download_path"

    /**
     * TinyConverter当前使用的版本号
     * 10.1.40
     */
    const val KEY_PLUGIN_TINY_CONVERTER_INSTALL_VERSION = "key_plugin_tiny_converter_install_version"

    /**
     * 文档阅读器 cache形式目录是否被删除
     * 10.1.60
     */
    const val KEY_DOCUMENT_CACHE_DIR_DELETED = "key_document_cache_dir_deleted"
    /**
     * 是否展示过资源圈 网络不稳定，引导，注意：值是影片的唯一标识，因为需求是让每个影片每天出现一次。
     */
    const val VIDEO_GUIDE_NET_INSTABLE = "video_guide_net_instable"
    /**
     * 当前崩溃次数
     */
    const val CURRENT_CRASH_COUNT = "current_crash_count"
    /**
     * 测试模式
     */
    const val IS_TEST_MODEL = "is_test_model"
    /**
     * 最近一次用户分享的外链链接
     */
    const val LAST_SHARE_LINK = "last_share_link"
    /**
     * 最近一次用户分享的外链链接时间
     */
    const val LAST_SHARE_LINK_TIME = "last_share_link_time"
    /**
     * 剪切板最近一次来自其他用户分享的外链连接
     */
    const val LAST_SHARE_LINK_FROM_OTHER: String = "last_share_link_from_other"
    /**
     * 是否开启夜间模式
     */
    const val IS_OPEN_NIGHT_MODE = "is_open_night_mode"

    /**
     * 是否进入过设置页面
     */
    const val IS_OPEN_SETTING_PAGE = "is_open_setting_page"

    /**
     * 是否展示过全网搜索引导, 2.12.0功能更新后，重新弹出一次
     */
    const val HAS_SHOW_NET_SEARCH_GUIDE_NEW = "has_show_net_search_guide_new"

    /**
     * 语言切换
     */
    const val LANGUAGE_SWITCH = "language_switch"

    /**
     * 俄罗斯宽限期 是否可以使用google pay
     */
    const val RUSSIA_CAN_USE_GOOGLE_PAY: String = "russia_can_use_google_pay"

    /**
     * 俄罗斯宽限期 标题
     */
    const val RUSSIA_GRACE_CARD_TITLE: String = "russia_grace_card_title"

    /**
     * 俄罗斯宽限期 内容
     */
    const val RUSSIA_GRACE_CARD_INFO: String = "russia_grace_card_info"

    /**
     * 俄罗斯宽限期 按钮文案
     */
    const val RUSSIA_GRACE_CONFIRM_TXT: String = "russia_grace_confirm_txt"

    /**
     * 日活统计-冷启次数-设备纬度
     */
    const val KEY_STARTUP_TIMES: String = "key_startup_times"

    /**
     * 谷歌商店商品价格缓存
     */
    const val GOOGLE_PLAY_PRODUCT_PRICE_CACHE: String = "google_play_product_price_cache"

    /**
     * 每天的广告收入合计 - 日期
     */
    const val AD_TOTAL_REVENUE_EVERY_DAY_DATE: String = "ad_total_revenue_every_day_date"

    /**
     * 每天的广告收入合计
     */
    const val AD_TOTAL_REVENUE_EVERY_DAY: String = "ad_total_revenue_every_day"

    /**
     * AppsFlayer的广告素材配置
     */
    const val APPSFLYER_AD_SET: String = "apps_flayer_ad_set"

    /**
     * 设备评分
     */
    const val DEVICE_PERFORMANCE_SCORE: String = "device_performance_score"

    /**
     * domain
     */
    const val DOMAIN_FROM_SERVER: String = "domain_from_server"

    /**
     * 记录appsflyer的引流信息
     * af 对应的 user source
     */
    const val AF_DIVERSION_INFO_KEY = "af_diversion_info"

    const val DEEPLINK_VALUE = "deep_link_value"

    /**
     * AF创建的OneLink配置的深度链接
     */
    const val AF_DEEPLINK_VALUE = "deep_link_value"

    /**
     * Facebook来源的Deep Link跳转路由
     */
    const val META_DEEPLINK_VALUE = "meta_deep_link_value"

    /**
     * 热启广告今日显示的次数
     */
    const val HOT_AD_SHOW_TIMES_TODAY = "hot_ad_show_times_today"


    /**
     * 关闭通知栏时的时间戳
     */
    const val NOTIFICATION_BAR_CLOSE_TIME_KEY = "notification_bar_close_time"

    /**
     * 多进程守护本地开关,该开关的值是从firebase的success回调中设置的
     * 为什么本地需要存一个,直接从firebase取不就行了吗? firebase只在主进程初始化了,其他进程取不到值,故在global中存一份,方便其他进程取用
     */
    const val DUAL_PROCESS_LOCAL_SWITCHER = "dual_process_local_switcher"

    /**
     * 是否已经登录  用Account.isLogin,多进程判断时有问题
     */
    const val IS_LOGIN_FOR_DUAL_PROCESS = "is_login_for_dual_process"

    /**
     * 关闭通知栏持续时间  用DuboxRemoteConfig.INSTANCE.getLong(NOTIFICATION_BAR_SHOW_AGAIN_TIME_KEY);多进程情况下有点问题,
     * 因为firebase仅在主进程初始化了
     */
    const val NOTIFICATION_BAR_SHOW_AGAIN_TIME = "notification_bar_show_again_time"

    /**
     * 记录多进程守护拉起的launch time
     */
    const val DUAL_START_LAUNCH_TIME_KEY = "dual_start_launch_time_key"

    /**
     * 上次多进程拉活打点时间
     */
    const val LAST_DUAL_PULL_STAT_TIME = "last_dual_pull_stat_time"

    /**
     * 是否请求权限标记
     * */
    const val HAS_REQUEST_NOTIFICATION_PERMISSION = "has_request_notification_permission"
}