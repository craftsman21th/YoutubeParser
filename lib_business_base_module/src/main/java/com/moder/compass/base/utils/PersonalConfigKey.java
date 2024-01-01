/*
 * PersonalConfigKey.java
 * .PersonalConfigKey
 * @author chenyuquan
 * V 1.0.0
 * Create at 2013-3-26 上午11:05:22
 */
package com.moder.compass.base.utils;

/**
 * 个人配置的key
 */
public class PersonalConfigKey {

    /**
     * 外链页视频自动保存的文件夹名字
     */
    public static final String SHARE_LINK_AUTO_SAVE_DIR_NAME = "share_link_auto_save_dir_name";
    /**
     * 快速设置页是否弹出过
     */
    public static final String KEY_IS_QUICK_SETTING_SHOWN = "KEY_IS_QUICK_SETTING_SHOWN";

    /**
     * 开启文件全名配置项
     **/
    public static final String SETTING_FULL_FILENAME = "setting_full_filename";

    /**
     * 检测输入的密码是否是正确
     */
    public static final String IS_INPUT_PWD_CORRECT = "is_inputpassword_correct";

    /**
     * 是否打开字幕开关
     **/
    public static final String SUBTITLE_SWITCH_BUTTON = "subtitle_switch_button";

    /**
     * 存储更新头像缓存的时间戳
     */
    public static final String AVATAR_TIMESTAMP = "avatar_timestamp";

    /**
     * 勾选仅wifi传输时，是否首次弹窗
     **/
    public static final String ALERT_WIFI_ONLY_CHECKED = "alert_wifi_only_checked";

    /**
     * 未勾选仅wifi传输时，是否首次弹窗
     **/
    public static final String ALERT_WIFI_ONLY_UNCHECKED = "alert_wifi_only_unchecked";

    /**
     * 是否显示更多页面分享动态的NEW标记
     **/
    public static final String SHOW_DISCOVERY_SHARED_RESOURCES_NEW_TAG_TIPS = "SHARED_RESOURCES_NEW_TAG";

    /**
     * 是否显示活动中心的NEW标记
     */
    public static final String SHOW_MISSION_CENTER_NEW_TAG_TIPS = "MISSION_CENTER_NEW_TAG";

    /**
     * 上次转存路径
     */
    public static final String SAVE_PATH_TRANSFER = "save_path_transfer";
    /**
     * 上次转存路径的时间
     */
    public static final String SAVE_PATH_TRANSFER_TIME = "save_path_transfer_time";

    /**
     * 是否显示过删除文件时的VIP引导
     */
    public static final String SHOWN_DELETE_FILE_VIP_GUIDE = "shown_delete_file_vip_guide";

    /**
     * 是否显示关于我中免流量特权红点
     **/
    public static final String SHOW_SINGKIL_NEW_TAG_TIPS = "show_singkil_new_tag_tips";

    /**
     * 隐藏空间加锁标志位
     **/
    public static final String SAFE_BOX_LOCK = "safe_box_lock";

    /**
     * 隐藏空间加锁标志位
     **/
    public static final String CARD_PACKAGE_LOCK = "card_package_lock";

    /**
     * 免流时需要重新拉取m3u8文件
     *
     * @since 7.19.0
     */
    public static final String M3U8_RESET = "m3u8_reset_";

    /**
     * 卡包一键导入引导本次出现过
     *
     * @since 8.0
     */
    public static final String CARD_PACKAGE_ONE_BTN_IMPORT_SHOWED = "card_package_one_btn_import_showed";

    /**
     * 新版本引导提示
     *
     * @since 8.0
     */
    public static final String NEW_VERSION_GUIDE = "new_version_guide";

    /**
     * 是否显示过共享文件夹详情引导
     *
     * @since 8.0
     */
    public static final String KEY_SHARE_DIRECTORY_INFO_GUIDE_SHOWN = "key_share_directory_guide_info_shown";

    /**
     * 覆盖安装8.0版本需要清空云图数据
     *
     * @since 8.1
     */
    public static final String NEED_RESET_SHARE_CLOUD_IMAGE = "need_reset_share_cloud_image";

    /**
     * 是否显示我的卡包
     *
     * @since 8.3
     */
    public static final String KEY_FOLDERS_SETTING_SHOW_CARD_PACKAGE = "key_folders_setting_show_card_package";

    /**
     * 云端文件库是否已经升级完成
     *
     * @since 8.5.0
     */
    public static final String KEY_CLOUD_FILE_DATABASE_UPGRADED = "key_cloud_file_database_upgraded";
    /**
     * live photo文件首次下载提示
     *
     * @since 8.6.0
     */
    public static final String KEY_LIVE_PHOTO_DOWNLOAD_FIRST_PROMPT = "key_live_photo_download_first_prompt";

    /**
     * 是否展示试用特权下载的运营活动，有效期只针对本次启动期间，会在应用启动的时候清空此数据
     */
    public static final String SHOW_PROBATIONARY_ACTIVITY = "show_probationary_activity";

    /**
     * 是否老用户
     *
     * @since 9.0
     */
    public static final String KEY_IS_OLD_USER = "key_is_old_user";

    /**
     * 是否非首次进行私密链接分享
     */
    public static final String NOT_FIRST_SHARE_LINK = "not_first_share_link";

    /**
     * 是否展示已购资源入口
     *
     * @since 9.2
     */
    public static final String KEY_FOLDERS_SETTING_SHOW_PURCHASED = "key_folders_setting_show_purchased";

    /**
     * 登录后日活上报携带source链接（用于新用户来源判断）
     *
     * @since 9.4
     */
    public static final String KEY_REPORT_USER_SOURCE_URL = "key_report_user_source_url";

    /**
     * 添加好友功能的引导，是否展示过
     *
     * @since 9.6.10
     */
    public static final String KEY_ADD_FRIEND_GUIDE_SHOWN = "add_friend_guide_shown";

    /**
     * 滑动选择图片引导是否显示过
     *
     * @since 9.6.20
     */
    public static final String KEY_SELECT_PIC_GESTURE_GUIDE_SHOWN = "key_select_pic_gesture_guide_shown";

    /**
     * 微信推荐好友是否有新推荐下发 用于展示小红点
     *
     * @since 9.6.20
     */
    public static final String KEY_WEIXIN_FRIENDS_SHOW_NEW = "key_weixin_friends_show_new";

    /**
     * 备份手动暂停
     */
    public static final String BACKUP_STOP_BY_HAND = "backup_stop_by_hand";

    /**
     * 允许使用手机流量备份图片
     *
     * @since 9.6.40
     */
    public static final String KEY_USE_INTERNET_BACKUP_PHOTO = "key_use_internet_backup_photo";

    /**
     * 允许使用手机流量备份视频
     *
     * @since 9.6.40
     */
    public static final String KEY_USE_INTERNET_BACKUP_VIDEO = "key_use_internet_backup_video";

    /**
     * 用户在备份设置页点击过开启备份（包括图片和视频）
     *
     * @since 10.0.0
     */
    public static final String KEY_USER_CLICK_PHOTO_OR_VIDEO_BACKUP = "key_user_click_photo_or_video_backup";

    /**
     * 快速设置页点击完成时，是否拉取到视频压缩备份的配置
     *
     * @since 10.0.0
     */
    public static final String KEY_IS_QUICK_SETTING_PAGE_FINISH_HAS_VIDEO_COMPRESS_CONFIG
        = "key_is_quick_setting_page_finish_has_video_compress_config";

    /**
     * 大图预览页更多，用户点击更多小红点隐藏的日期
     *
     * @since 10.0.40
     */
    public static final String KEY_IMAGE_MORE_RED_DOT_CLICK
        = "key_image_more_red_dot_click";

    /**
     * 视频播放清晰度new图标是否点击过
     *
     * @since 10.0.80
     */
    public static final String VIEDO_PLAYER_RESOLUTION_NEW_HAS_CLICKED = "video_player_resolution_new_has_clicked";

    /**
     * 卡顿toast上次显示时间（以天为单位）
     *
     * @since 10.0.110
     */
    public static final String CARTON_TIP_LAST_SHOW_TIME = "carton_tip_last_show_time";

    /**
     * 今天显示过卡顿toast的视频集
     *
     * @since 10.0.110
     */
    public static final String VIDEOS_TODAY_HAS_SHOWED_CARTON_TIP = "videos_today_has_showed_carton_tip";

    /**
     * 是否显示过大图预览底部引导
     */
    public static final String KEY_HAS_SHOW_IMAGE_DETAIL_GUIDE = "key_has_show_image_detail_guide";
    /**
     * 时光轴纬度持久化
     */
    public static final String TIME_LINE_VIEW_TYPE = "time_line_view_type";
    /**
     * 时光轴筛选条件
     */
    public static final String TIME_LINE_FILTER = "time_line_filter";

    /**
     * 登录保护海外登录开关（在xml中使用，此处避免key冲突）
     */
    public static final String SECURITY_LOGIN_PROTECT_FORIGEN = "security_login_protect_forigen";
    /**
     * 登录保护每次验证开关 在xml中使用，此处避免key冲突）
     */
    public static final String SECURITY_LOGIN_PROTECT_TIMES = "security_login_protect_times";
    /**
     * 已使用空间的缓存
     */
    public static final String CACHE_USER_QUOTA_USED = "cache_user_quota_used";
    /**
     * 端版本升级导致的云图diff重置标识，使用时需要与server确认接口压力
     */
    public static final String RESET_CLOUD_IMAGE_DIFF_WITH_VERSION = "reset_cloud_image_diff_with_version";
    /**
     * 标识APP启动后已经展示过一次网速提示条
     */
    public static final String KEY_HAS_SHOWN_TRANSFER_TIP_ON_APP_CREATE = "has_shown_transfer_tip_on_app_create";
    /**
     * 标识当天已经展示过一次网速提示条
     */
    public static final String KEY_HAS_SHOWN_TRANSFER_TIP_ON_ONE_DAY = "has_shown_transfer_tip_on_date_";
    /**
     * 分享diff cursor
     */
    public static final String CURSOR_SHARE_LINK_DIFF = "cursor_share_link_diff";
    /**
     * 智能分类配置拉取成功
     */
    public static final String TAG_CONFIG_FETCH_SUCCESS = "tag_config_fetch_success";

    /**
     * 是否开启相册自动备份
     **/
    public static final String PHOTO_AUTO_BACKUP = "photo_auto_backup";
    /**
     * 相册服务list 排序持久化
     **/
    public static final String SORT_VIDEO_SERVICE_LIST = "sort_video_service_list";
    /**
     * 1.8.0版本重新回捞云图数据，修正mtime为空的问题
     **/
    public static final String NEED_MERGE_CLOUD_MEDIA_180 = "need_merge_cloud_media_180";
    /**
     * 订阅凭据上报频控
     */
    public static final String REPORT_GOOGLE_SUB_TOKEN_FREQUENCY = "report_google_sub_token_frequency_";
    /**
     * 订阅凭据上报频控的日期
     */
    public static final String REPORT_GOOGLE_SUB_TOKEN_FREQUENCY_DATE = "report_google_sub_token_frequency_date_";
    /**
     * 个人中心运营位用户手动关闭的日期
     */
    public static final String USER_CENTER_ACTIVITY_BANNER_CLOSE_DATE = "user_center_activity_banner_close_date";
    /**
     * 一次性的展示
     */
    public static final String KEY_ONCE_DRAWER_GUIDE_APPEARED = "key_once_drawer_guide_appeared";
    /**
     * 最近使用记录的diff cursor
     */
    public static final String KEY_RECORD_DIFF_CURSOR = "key_record_diff_cursor";
    /**
     * 最近使用记录的diff是否完成
     */
    public static final String KEY_RECORD_DIFF_IS_COMPLETE = "key_record_diff_is_complete";

    /**
     * 记录用户通过更新弹窗下载了最新版 APK
     */
    public static final String KEY_HAS_DOWNLOAD_NEW_VERSION_APK = "key_has_download_new_version_apk";

    /**
     * 是否设置了保险箱密码
     */
    public static final String KEY_SAFE_BOX_PWD_ALREADY_INIT = "key_safe_box_pwd_already_init";

    /**
     * 是否展示日本电视盒子
     */
    public static final String KEY_SHOW_PIXELA = "key_show_pixela";

    /**
     * 保险箱token
     */
    public static final String KEY_SAFE_BOX_TOKEN = "key_safe_box_token";

    /**
     * 用户离开加密空间的时间
     */
    public static final String KEY_SAFE_BOX_LEAVE = "key_safe_box_leave";

    /**
     * 保险箱密文密码
     */
    public static final String KEY_SAFE_BOX_HASHED_PWD = "key_safe_box_hashed_pwd";

    /**
     * 是否开启指纹解锁
     */
    public static final String KEY_IS_OPEN_FINGER_UNLOCK = "key_is_open_finger_unlock";

    /**
     * 加密空间加密时公钥
     */
    public static final String KEY_SAFE_BOX_PUB_KEY = "key_safe_box_pub_key";

    /**
     * 加密空间加密时加密算法加盐
     */
    public static final String KEY_SAFE_BOX_SALT_KEY = "key_safe_box_salt_key";

    /**
     * 是否上报过空间超10G
     */
    public static final String HAS_UPLOAD_QUOTA_MORE_10G = "has_upload_quota_more_10g";

    /**
     * 保险箱 token 添加时间, 用于判断是否刷新
     */
    public static final String KEY_LAST_SAVE_TOKEN_TIME = "key_last_save_token_time";

    /**
     * fcm token key
     */
    public static final String FCM_TOKEN_KEY = "fcm_token_key";

    /**
     * 是否显示过首页引导新用户开启自动备份的引导
     */
    public static final String KEY_SHOWN_HOME_NEW_USER_AUTO_BACKUP_GUIDE = "key_shown_home_new_user_auto_backup_guide";

    /**
     * 站内信 cursor 下标
     */
    public static final String KEY_STATION_MAIL_CURSOR = "key_station_mail_cursor";

    /**
     * 个人中心消息小红点
     */
    public static final String KEY_SHOW_MINE_CENTER_MESSAGE_RED_DOT = "key_show_mine_center_message_red_dot";
    /**
     * 资源圈消息小红点
     */
    public static final String KEY_SHOW_RESOURCE_MESSAGE_RED_DOT = "key_show_resource_message_red_dot";
    /**
     * 资源圈上新弹窗
     */
    public static final String KEY_SHOW_RESOURCE_MESSAGE_DIALOG = "key_show_resource_message_dialog";
    /**
     * target30升级标志
     */
    public static final String TARGET30_UPDATE_TAG = "target30_update_tag";
    /**
     * 是否点击了新手引导
     */
    public static final String IS_CLICK_USER_GUIDE = "is_click_user_guide";
    /**
     * 大文件上传引导标志
     */
    public static final String IS_SHOW_LARGE_FILE_SIZE = "is_show_large_file_size";
    /**
     * 最近一次显示评分引导的时间
     */
    public static final String LAST_RATING_SHOW_TIME = "last_rating_show_time";

    /**
     * 记录评分引导的【上传、下载、分享、转存】四种操作是否完成的状态
     */
    public static final String RATING_SHOW_OP_CONDITION = "rating_show_condition";

    /**
     * 用户第一次登录的时间
     */
    public static final String USER_FIRST_LAUNCH_APP_TIME = "user_first_launch_app_time";
    public static final String LAST_SHOW_DELETE_VIP_GUIDE_TIME = "last_show_delete_vip_guide_time";
    /**
     * 监控用-是否首次启动
     */
    public static final String IS_FIRST_START_MONITOR = "is_first_start_monitor";
    /**
     * 全网搜索-保存路径
     */
    public static final String KEY_NET_SEARCH_SAVE_PATH = "net_search_save_path";
    /**
     * 流畅播-保存路径
     */
    public static final String KEY_SMOOTH_PLAY_SAVE_PATH = "key_smooth_play_save_path";

    /**
     * 是否开启视频自动备份
     **/
    public static final String VIDEO_AUTO_BACKUP = "video_auto_backup";
    /**
     * 是否显示过首页新用户引导蒙层
     */
    public static final String IS_SHOW_HOME_USER_GUIDE = "is_show_home_user_guide";
    /**
     * 是否完成首次本地diff
     */
    public static final String IS_FIRST_LOCAL_DIFF_COMPLETE = "is_first_local_diff_complete";

    /**
     * 是否显示低分辨率提示
     */
    public static final String IS_SHOW_LOW_RESOLUTION_TOAST = "is_show_low_resolution_toast";
    /**
     * 个人中心广告位用户手动关闭的日期
     */
    public static final String USER_CENTER_AD_BANNER_CLOSE_DATE = "user_center_ad_banner_close_date";

    /**
     * 是否显示定价试验dialog是否显示
     */
    public static final String IS_SHOW_PREMIUM_DISCOUNT_DIALOG = "is_show_premium_discount_dialog";

    /**
     * 视频tab下，视频自动备份上次用户关闭时间
     */
    public  static final String TAB_VIDEO_BACKUP_CLOSE_TIME = "tab_video_backup_close_time";

    /**
     * 允许使用手机流量播放音频
     *
     * @since 10.0.120
     */
    public static final String KEY_USE_INTERNET_AUDIO_PLAY = "key_use_internet_audio_play";

    /**
     * 宽限期展示次数
     */
    public static final String KEY_GOOGLE_PAY_GRACE_TIPS_SHOW_TIMES = "key_google_pay_grace_tips_show_times";

    /**
     * 文件夹自动备份
     */
    public static final String KEY_BACKUP_FOLDER_SWITCH = "key_backup_folder_switch";

    /**
     * 使用移动流量备份文件夹
     */
    public static final String KEY_BACKUP_FOLDER_MOBILE_NETWORK = "key_backup_folder_mobile_network";

    /**
     * 文件自动备份流程完成时间，存储的是long
     */
    public static final String FILE_BACKUP_END_TIME = "file_backup_end_time";

    /**
     * 会员购买实验商品
     */
    public static final String KEY_PRODUCT_LIST_INFO = "key_product_list_info";

    /**
     * 首页tab点击日期
     */
    public static final String KEY_MAIN_TAB_CLICK_SWITCH_DATE = "key_main_tab_click_switch_date";

    /**
     * 首页tab广告展示次数/天
     */
    public static final String KEY_MAIN_TAB_AD_PV_EVERY_DAY = "key_main_tab_ad_pv_every_day";
    /**
     * 首页tab广告展示次数重置次数/天
     */
    public static final String KEY_MAIN_TAB_RESTART_TIMES_EVERY_DAY = "key_main_tab_restart_times_every_day";
    /**
     * 备份广告显示次数
     */
    public static final String KEY_BACKUP_AD_SHOW_COUNT = "key_backup_ad_show_count";
    /**
     * 上次备份广告显示时间
     */
    public static final String KEY_BACKUP_AD_SHOW_TIME = "key_backup_ad_show_time";

    /**
     * 全部工具栏弹窗展示日期
     */
    public static final String KEY_ALL_TOOLS_DIALOG_SHOW_SWITCH_DATE = "key_all_tools_dialog_show_switch_date";

    /**
     * 全部工具栏弹窗展示次数/天
     */
    public static final String KEY_ALL_TOOLS_DIALOG_TIMES_EVERY_DAY = "key_all_tools_dialog_times_every_day";
    /**
     * 广告声音开关
     */
    public static final String KEY_AD_VOICE_SWITCH = "key_ad_voice_switch";

    /**
     * Widget 本地故事索引
     */
    public static final String KEY_STORY_WIDGET_INDEX = "key_story_widget_index";

    /**
     * Widget 本地故事索引更新时间
     */
    public static final String KEY_STORY_WIDGET_UPDATE_TIME = "key_story_widget_update_time";

    /**
     * 最近上传视频总数
     */
    public static final String KEY_REMOTE_UPLOAD_VIDEO_COUNT = "key_remote_upload_video_count";

    /**
     * 最近上传卡片条目关闭时间
     */
    public static final String KEY_REMOTE_UPLOAD_VIDEO_CLOSE_TIME = "key_remote_upload_video_close_time";

    /**
     * 转存视频总数
     */
    public static final String KEY_TRANSFER_VIDEO_COUNT = "key_transfer_video_count";

    /**
     * 转存卡片条目关闭时间
     */
    public static final String KEY_TRANSFER_VIDEO_CLOSE_TIME = "key_transfer_video_close_time";

    /**
     * 离线视频总数
     */
    public static final String KEY_OFFLINE_VIDEO_COUNT = "key_offline_video_count";

    /**
     * 离线卡片条目关闭时间
     */
    public static final String KEY_OFFLINE_VIDEO_CLOSE_TIME = "key_offline_video_close_time";

    /**
     * app指纹
     */
    public static final String NDUT_FMT = "ndut_fmt";

    /**
     * app指纹识别, 记录时间
     */
    public static final String IS_APPID_REPORT = "is_appid_report";

    /**
     * 是否是首次登陆app
     */
    public static final String IS_FIRST_LAUNCH = "is_first_launch";

    /**
     * 上次活动展示时间
     */
    public static final String LAST_ACTIVITY_WINDOW_SHOW_TIME = "last_activity_window_show_time";

    /**
     * 上次首页悬浮窗展示时间
     */
    public static final String LAST_HOME_FLOAT_WINDOW_SHOW_TIME = "last_home_float_window_show_time";

    /**
     * 首次启动时间戳-已登录
     */
    public static final String FIRST_LAUNCHER_TIME_LOGIN = "first_launcher_time_login";

    /**
     * 分享-email和copyLink的展示顺序
     */
    public static final String SHARE_WITH_EMAIL_OR_LINK = "share_with_email_or_link";

    /**
     * 分享-社媒的展示顺序
     */
    public static final String SHARE_CHANNEL_LIST_ORDER = "share_channel_list_order";

    /**
     * 上次弹出提示添加 widget 的通知的时间
     */
    public static final String LAST_ADD_WIDGET_PUSH_TIME = "last_add_widget_push_time";

    /**
     * 免广告气泡-展示次数-设备纬度
     */
    public static final String KEY_NO_AD_TIP_SHOW_COUNT = "key_no_ad_tip_show_count";


    /**
     * 免广告气泡-最后展示时间-设备纬度
     */
    public static final String KEY_NO_AD_TIP_LAST_SHOW_TIME = "key_no_ad_tip_last_show_time";

    /**
     * 上次进入视频 tab 的时间
     */
    public static final String LAST_ENTER_VIDEO_TAB_TIME = "last_enter_video_tab_time";

    /**
     * 上次弹出提示进入视频 tab 的通知的时间
     */
    public static final String LAST_SHOW_ENTER_VIDEO_TAB_PUSH_TIME = "last_show_enter_video_tab_push_time";

    /**
     * 上次进入相册 tab 的时间
     */
    public static final String LAST_ENTER_ALBUM_TAB_TIME = "last_enter_album_tab_time";

    /**
     * 上次弹出提示进入相册 tab 的通知的时间
     */
    public static final String LAST_SHOW_ENTER_ALBUM_TAB_PUSH_TIME = "last_show_enter_album_tab_push_time";

    /**
     * 上次弹出提示清理的通知的时间
     */
    public static final String LAST_SHOW_CLEAN_STORAGE_PUSH_TIME = "last_show_clean_storage_push_time";

    /**
     * 云图数据库同步下载状态
     */
    public static final String KEY_SYNC_CLOUD_MEDIA_OFFLINE_DATA = "key_sync_cloud_media_offline_data";

    /**
     * 视频自动备份弹窗展示次数
     */
    public static final String KEY_QUICK_VIDEO_UPLOAD_COUNT = "key_quick_video_upload_count";

    /**
     * 视频自动备份弹窗展示时间
     */
    public static final String KEY_QUICK_VIDEO_UPLOAD_SHOW_TIME = "key_quick_video_upload_show_time";

    /**
     * 极速上传 - 视频备份引导飘条显示次数
     */
    public static final String KEY_QUICK_VIDEO_FLOAT_COUNT = "key_quick_video_float_count";
    /**
     * 极速上传 - 文件上传引导飘条显示次数
     */
    public static final String KEY_QUICK_DOC_FLOAT_COUNT = "key_quick_doc_float_count";
    /**
     * 极速上传 - 视频备份引导飘条显示时间
     */
    public static final String KEY_QUICK_VIDEO_FLOAT_TIME = "key_quick_video_float_time";
    /**
     * 极速上传 - 文件上传引导飘条显示时间
     */
    public static final String KEY_QUICK_DOC_FLOAT_TIME = "key_quick_doc_float_time";

    /**
     * 极速上传 - 视频飘条显示日期，用于自动增加 count
     */
    public static final String KEY_QUICK_VIDEO_REFRESH_TIME = "key_quick_video_refresh_time";
    /**
     * 极速上传 - 文件飘条显示日期，用于自动增加 count
     */
    public static final String KEY_QUICK_DOC_REFRESH_TIME = "key_quick_doc_refresh_time";
    /**
     * 极速上传 - 图片飘条显示日期，用于自动增加 count
     */
    public static final String KEY_QUICK_IMAGE_REFRESH_TIME = "key_quick_image_refresh_time";

    /**
     * 图片备份弹窗展示次数
     */
    public static final String KEY_QUICK_IMAGE_UPLOAD_COUNT = "key_quick_image_upload_count";

    /**
     * 图片备份弹窗展示时间
     */
    public static final String KEY_QUICK_IMAGE_UPLOAD_SHOW_TIME = "key_quick_image_upload_show_time";
    /**
     * 文件快速上传权益弹窗展示次数
     */
    public static final String KEY_QUICK_DOC_UPLOAD_COUNT = "key_quick_doc_upload_count";

    /**
     * 文件快速上传权益弹窗展示时间
     */
    public static final String KEY_QUICK_DOC_UPLOAD_SHOW_TIME = "key_quick_doc_upload_show_time";
    /**
     * 极速上传 - 图片备份引导飘条显示次数
     */
    public static final String KEY_QUICK_IMAGE_FLOAT_COUNT = "key_quick_image_float_count";
    /**
     * 极速上传 - 图片备份引导飘条显示时间
     */
    public static final String KEY_QUICK_IMAGE_FLOAT_TIME = "key_quick_image_float_time";

    /**
     * 极速上传 - 上传列表 - 极速权益引导关闭时间
     */
    public static final String KYE_UPLOAD_LIST_SPEED_BAR_CLOSE_TIME = "kye_upload_list_speed_bar_close_time";
    /**
     * 极速上传 - 上传列表 - 极速权益引导显示次数: days_lastDayTimes_timeStamp
     */
    public static final String KYE_UPLOAD_LIST_SPEED_BAR_TIMES = "kye_upload_list_speed_bar_times";
    /**
     * 极速上传 - push - 极速权益引导关闭次数
     */
    public static final String KYE_QUICK_UPLOAD_PUSH_CLOSE_TIMES = "kye_quick_upload_push_close_times";

    /**
     * 通知栏存在上报时间，一天只上报一次
     */
    public static final String KEY_KEEP_ACTIVE_NOTIFICATION_REPORT_TIME = "key_keep_active_notification_report_time";
    /**
     * 广告点击的次数
     */
    public static final String KEY_AD_CLICK_TIMES = "key_ad_click_times";

    /**
     * 新手任务中 append 到福利中心的内容
     */
    public static final String KEY_NEW_USER_GUIDE_APPEND = "key_new_user_guide_append";

    /**
     * 新手任务，上传引导是否命中
     */
    public static final String KEY_NEWBIE_UPLOAD_GUIDE_SHOWED = "key_newbie_upload_guide_showed";

    /**
     * 新手任务-邀请新用户任务id
     */
    public static final String KEY_NEWBIE_TASK_INVITE_NEWBIE_ID = "key_newbie_task_invite_newbie_id";

    /**
     * [激励视频兑换短期会员]是否展示免费使用
     */
    public static final String SHOW_FREE_TRY_USE = "show_free_try_use";

    /**
     * 音乐数量
     */
    public static final String FILE_MUSIC_COUNT = "file_music_count";
    /**
     * 视频数量
     */
    public static final String FILE_VIDEO_COUNT = "file_video_count";
    /**
     * 文档数量
     */
    public static final String FILE_DOCUMENT_COUNT = "file_document_count";
    /**
     * 图片数量
     */
    public static final String FILE_IMG_COUNT = "file_img_count";
    /**
     * 首页是否对用户不可见
     */
    public static final String HOME_FRAGMENT_HIDE_TO_USER = "home_fragment_hide_to_user";

    /**
     * 资源落地页FE的操作
     */
    public static final String SHARE_RESOURCE_DETAIL_OPERATE = "share_resource_detail_operate";

    /**
     * 雷达搜索结果列表
     */
    public static final String RADAR_SEARCH_RESULT = "radar_search_result";

    /**
     * 上一次从雷达接口获取结果时间
     */
    public static final String RADAR_SEARCH_SUCCESS_TIME = "radar_search_success_time";

    /**
     * 已使用翻卡次数
     */
    public static final String RADAR_CARD_CLICK_USED_CHANCE = "radar_card_click_used_chance";

    /**
     * 雷达结果页上一次返回时间
     */
    public static final String RADAR_RESULT_PAGE_LAST_BACK_TIME = "radar_result_page_last_back_time";

    /**
     * 记录1080p视频tips最新显示时间戳
     */
    public static final String RESOLUTION_TIPS_SHOW_LAST_TIME = "resolution_tips_show_last_time";

    /**
     * 首页探索tab红点是否显示
     */
    public static final String MAIN_TAB_VIDEO_RED_DOT_IS_SHOW = "main_tab_video_red_dot_is_show";


    /**
     * 记录上一次自动检测更新版本
     */
    public static final String LAST_AUTO_CHECK_NEW_VERSION = "last_auto_check_new_version";

    /**
     * 激励视频记录
     */
    public static final String REWARD_VIDEO_COUNT_RECORD = "reward_video_count_record";

    /**
     * 是否进入过资源小组
     */
    public static final String HAS_ENTER_RESOURCE_GROUP = "has_enter_resource_group";

    /**
     * 资源小组动态页加入挽留弹窗上次展示时间
     */
    public static final String RESOURCE_GROUP_POST_LIST_JOIN_LAST_SHOW_TIME
            = "resource_group_post_list_join_last_show_time";

    /**
     * 资源小组-资源获取气泡-展示次数-设备纬度
     */
    public static final String KEY_GROUP_SEARCH_TIP_SHOW_COUNT = "key_group_search_tip_show_count";


    /**
     * 资源小组-资源获取气泡-最后展示时间-设备纬度
     */
    public static final String KEY_KEY_GROUP_SEARCH_TIP_LAST_SHOW_TIME = "key_key_group_search_tip_last_show_time";

    /**
     * 外链页播放视频降级到 A 组
     */
    public static final String SHARE_LINK_PLAY_VIDEO_DOWNGRADE_TO_A = "share_link_play_video_downgrade_to_a";

    /**
     * 成人屏蔽选项key
     * */
    public static final String KEY_BAN_ADULT_SELECT_INDEX = "KEY_BAN_ADULT_SELECT_INDEX";
    /**
     * 成人屏蔽设置页是否需要展示标记
     * */
    public static final String KEY_SETTING_BAN_ADULT_SHOW_TAG = "KEY_SETTING_BAN_ADULT_SHOW_TAG";
    /**
     * 成人屏蔽是否需要展示标记
     * */
    public static final String KEY_BAN_ADULT_SHOW_TAG = "KEY_BAN_ADULT_SHOW_TAG";

    /**
     * app 退到后台时间
     */
    public static final String KEY_APP_LAST_PAUSE_TIME = "key_app_last_pause_time";

    /**
     * 记录上次请求home slide数据的rankindex
     */
    public static final String HOME_SLIDE_REQUEST_RANK_INDEX = "home_slide_request_rank_index";

    /**
     * 记录上次请求home slide数据的time
     */
    public static final String HOME_SLIDE_LAST_REQUEST_TIME = "home_slide_last_request_time";

    /**
     * 是否已添加默认书签
     */
    public static final String BOOK_MARK_IS_ADD_DEFAULT = "book_mark_is_add_default";
}
