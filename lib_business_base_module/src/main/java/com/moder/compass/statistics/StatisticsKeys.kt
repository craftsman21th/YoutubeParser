package com.moder.compass.statistics

/**
 * Created by yeliangliang on 2020/9/8
 */

/** 海外版 开启备份按钮点击uv&pv 【1.4.0】
 * from 来源
 * 1.新用户首次进入Dubox引导页面
 * 2.首页卡片引导开启
 * 3.个人设置tab
 * 4.时光轴tab引导开启
 * type 0关闭 1开启
 */
const val SWITCH_AUTO_BACKUP: String = "switch_auto_backup"
const val SWITCH_AUTO_BACKUP_FROM_FIRST_GUIDE: String = "1"
const val SWITCH_AUTO_BACKUP_FROM_HOME_HEADER: String = "2"
const val SWITCH_AUTO_BACKUP_FROM_MINE_TAB: String = "3"
const val SWITCH_AUTO_BACKUP_FROM_TIMELINE: String = "4"

/**
 * 多外链页面展示
 */
//@Stat(sourceId = "31505-002", desc = "多外链页面展示")
const val MULTI_SHARE_LIST_PAGE_SHOW: String = "multi_share_list_page_show"

/**
 * 多外链页面展示时间
 */
//@Stat(sourceId = "31505-003", desc = "多外链页面展示时间")
const val MULTI_SHARE_LIST_PAGE_SHOW_TIME: String = "multi_share_list_page_show_time"

/**
 * 多外链页选择路径点击
 */
//@Stat(sourceId = "31405-005", desc = "多外链页选择路径点击")
const val MULTI_SHARE_LIST_SELECT_PATH_CLICK: String = "multi_share_list_select_path_click"

/**
 * 多外链页直接保存按钮点击
 */
//@Stat(sourceId = "31405-007", desc = "多外链页直接保存按钮点击")
const val MULTI_SHARE_LIST_SAVE_CLICK: String = "multi_share_list_save_click"

// 商店评分引导弹窗展示
const val SHOW_RATING_GUIDE_DIALOG: String = "show_rating_guide_dialog"

// 评分弹窗关闭按钮点击的uv/pv
const val SHOW_RATING_GUIDE_DIALOG_CLOSE_CLICK: String = "show_rating_guide_dialog_close_click"

// 好评的PV UV
const val SHOW_RATING_GUIDE_DIALOG_GOOD: String = "show_rating_guide_dialog_good"

// 评分1-4星时的弹窗展示uv/pv
const val SHOW_RATING_GUIDE_DIALOG_BAD: String = "show_rating_guide_dialog_bad"

// 好评后点击下次
const val CLICK_RATING_GUIDE_DIALOG_GOOD_CANCEL: String = "click_rating_guide_dialog_good_cancel"

// 好评后点击去评价
const val CLICK_RATING_GUIDE_DIALOG_GOOD_TO_PLAY: String = "click_rating_guide_dialog_good_to_play"

// server psign request failed
const val PSIGN_REQUEST_FAILED: String = "psign_request_failed"

// fe 公钥获取失败
const val PUBLIC_KEY_REQUEST_FAILED: String = "public_key_request_failed"

// fe 登录页md5 list获取失败
const val LOGIN_H5_MD5_LIST_REQUEST_FAILED: String = "login_h5_md5_list_request_failed"

// domain下发验签失败
const val DOMAIN_REQUEST_VERIFY_FAILED: String = "domain_request_verify_failed"

// 登录页验签失败
const val LOGIN_H5_URL_MD5_VERIFY_FAILED: String = "login_h5_url_md5_verify_failed"

// 登录页https证书失败
const val LOGIN_HTTPS_VERIFY_FAILED: String = "login_https_verify_failed"

// 登录页https证书失败-详细信息埋点
const val LOGIN_ON_SSL_ERROR_OCCUR: String = "login_on_ssl_error_occur"

// 登录页普通onReceivedError失败-详细信息埋点
const val LOGIN_ON_ORDINARY_ERROR_OCCUR: String = "login_on_ordinary_error_occur"

// 在线url和离线包manifest所有条目都匹配不上
const val LOGIN_ON_MANIFEST_ITEMS_NO_MATCH: String = "login_on_manifest_items_no_match"

// 离线包Manifest解析失败 or 格式错误
const val LOGIN_ON_MANIFEST_PARSE_ERROR: String = "login_on_manifest_parse_error"

// 个人中心-安全中心点击uv&pv
const val SECURITY_PAGE_ITEM_CLICK: String = "security_page_item_click"

// 相册tab智能分类热区点击uv&pv
const val CLICK_TIMELINE_HEADER_TAG_ENTER: String = "click_timeline_header_tag_enter"

// 智能分类二级页面展现uv&pv
const val VIEW_TAG_LIST: String = "view_tag_list"

// 二级页面下对应分类详情页展现uv&pv（三级页面）
const val VIEW_TAG_DETAIL: String = "view_tag_detail"

// 海外版 截屏提示PV/UV
const val SCREEN_SHOT_NOTICE_SHOW: String = "screen_shot_notice_show"

// 进入回收站的次数、用户数
const val RECYCLE_BIN_OPEN = "recycle_bin_open"

// 点击清空的次数、用户数
const val RECYCLE_BIN_CLEAR = "recycle_bin_clear"

// 进入批选点击还原的次数、用户数
const val RECYCLE_BIN_EDIT_RESTORE = "recycle_bin_edit_restore"

// 进入关于
const val ENTER_ABOUT = "enter_about"

// 文件tab指引 PV&UV
const val SHOW_SHARE_GUIDE_FILE_TAB = "show_share_guide_file_tab"

// 点击个人中心「我的分享」楼层PV&UV
const val CLICK_ENTER_SHARE_MANAGE = "click_enter_share_manage"

// 点击文件列表页「管理分享」事件PV&UV
const val CLICK_ENTER_SHARE_MANAGE_FILE_LIST = "click_enter_share_manage_file_list"

// 点击取消链接PV&UV
const val CLICK_CANCEL_SHARELINK = "click_cancel_sharelink"

// 点击复制链接PV&UV
const val CLICK_COPY_SHARELINK = "click_copy_sharelink"

// 详情页点击复制链接PV&UV
const val CLICK_COPY_SHARELINK_IN_DETAIL = "click_copy_sharelink_in_detail"

// 启动时自动备份为开启状态PV&UV
const val LAUNCH_WITH_OPEN_AUTO_BACKUP = "launch_with_open_auto_backup"

// 进入帐号页 包括账号绑定
const val ENTER_ACCOUNT_ACTIVITY = "enter_account_activity"

// 进入帐号页仅登录的PV
const val LOGIN_VIEW_PV = "login_view_pv"

// 帐号页H5加载成功
const val ACCOUNT_ACTIVITY_H5_LOAD_SUCCESS = "account_activity_h5_load_success"

// 登录成功
const val ACCOUNT_ACTIVITY_LOGIN_SUCCESS = "account_activity_login_success"

// 登录签名验证失败的PV
const val ACCOUNT_ACTIVITY_LOGIN_VERIFY_FAILED = "account_activity_login_verify_failed"

// 分享面板-有效期设置PV&UV
const val VIEW_SHARE_PERIOD_CHOICE_LIST = "view_share_period_choice_list"

// 分享面板-有效期选择：永久有效
const val CLICK_SHARE_PERIOD_CHOICE_PERMANENT = "click_share_period_choice_permanent"

// 分享面板-点击电子邮件
const val CLICK_SHARE_EMAIL_ENTRANCE = "click_share_email_entrance"

// 分享面板-电子邮件延展页PV&UV
const val VIEW_SHARE_EMAIL_PAGE = "view_share_email_page"

// 分享面板-在消息中输入字符的PV&UV
const val CLICK_SHARE_EMAIL_INPUT_EXTRA_MSG = "click_share_email_input_extra_msg"

// 分享面板-点击分享按钮
const val CLICK_SHARE_EMAIL_SEND_BUTTON = "click_share_email_send_button"

// 分享面板-点击本地邮箱icon
const val CLICK_SHARE_EMAIL_THIRDPARTY_EMAIL_APP = "click_share_email_thirdparty_email_app"

// 点击视频服务化按名称排序
const val CLICK_VIDEO_SERVICE_SORT_BY_NAME: String = "click_video_service_sort_by_name"

// 点击视频服务化按时间排序
const val CLICK_VIDEO_SERVICE_SORT_BY_TIME: String = "click_video_service_sort_by_time"

// 点击视频服务化按大小排序
const val CLICK_VIDEO_SERVICE_SORT_BY_SIZE: String = "click_video_service_sort_by_size"

// 点击视频服务化列表全选按钮
const val CLICK_VIDEO_SERVICE_SELECT_ALL: String = "click_video_service_select_all"

// 点击切换清晰度 720P
const val CLICK_SWITCH_RESOLUTION_720P: String = "click_switch_resolution_720p"

// 点击切换清晰度 480P
const val CLICK_SWITCH_RESOLUTION_480P: String = "click_switch_resolution_480p"

// 点击切换清晰度 360P
const val CLICK_SWITCH_RESOLUTION_360P: String = "click_switch_resolution_360p"

// 清晰度切换失败toast展现uv/pv
const val SHOW_SWITCH_RESOLUTION_FAILED: String = "show_switch_resolution_failed"

// 最近观看卡片点击uv/pv
const val CLICK_VIDEO_RECENT_HEADER_ITEM: String = "click_video_recent_header_item"

// 最近观看卡片滑动 uv/pv
const val SHOW_VIDEO_RECENT_HEADER_SCROLLING: String = "show_video_recent_header_scrolling"

// 最近观看页面展现uv/pv
const val SHOW_VIDEO_RECENT_LIST_PAGE: String = "show_video_recent_list_page"

// 最近观看清除记录按钮uv/pv
const val CLICK_VIDEO_RECENT_LIST_PAGE_DELETE: String = "click_video_recent_list_page_delete"

// ReportSizeConfigurationsException 触发次数
const val REPORT_SIZE_CONFIGURATIONS_EXCEPTION: String = "report_size_configurations_exception"

// RemoteException 触发次数
const val REPORT_SIZE_REMOTE_EXCEPTION: String = "report_size_remote_exception"

// RemoteException 触发次数
const val REPORT_SIZE_SQL_EXCEPTION: String = "report_size_sql_exception"

// RemoteException 触发次数
const val REPORT_SIZE_SECURITY_EXCEPTION: String = "report_size_security_exception"

// TimeoutException 触发次数
const val TIME_OUT_EXCEPTION: String = "time_out_exception"

// 查看原图
const val PHOTO_PREVIEW_ORIGIN_PICTURE_VIEW_ACTION = "photo_preview_origin_picture_view_action"

// 关闭原图查看按钮
const val PHOTO_PREVIEW_ORIGIN_PICTURE_VIEW_CANCEL_ACTION = "photo_preview_origin_picture_view_cancel_action"

// 个人中心vip卡片展现
const val USER_CENTER_VIP_CARD_VIEW = "user_center_vip_card_view"

// 个人中心vip卡片点击
const val USER_CENTER_VIP_CARD_ACTION = "user_center_vip_card_action"

// 首页文件列表下载大文件速度引导飘条展现
const val NON_VIP_HOME_DOWNLOAD_SPEED_VIEW = "non_vip_home_download_speed_view"

// 首页文件列表下载大文件速度引导飘条点击cancel
const val CLICK_HOME_DOWNLOAD_SPEED_GUIDE_VIEW_CLOSE = "click_home_download_speed_guide_view_close"

// 下载列表下载大文件速度引导飘条点击cancel
const val CLICK_DOWNLOAD_LIST_DOWNLOAD_SPEED_GUIDE_VIEW_CLOSE = "click_download_list_download_speed_guide_view_close"

// 极速下载激励视频弹窗展示
const val SHOW_DOWNLOAD_SPEED_REWARD_DIALOG = "show_download_speed_reward_dialog"

// 激励视频弹窗点击去播放
const val CLICK_REWARD_DIALOG_TO_PLAY = "click_reward_dialog_to_play"

// 首页文件列表下载大文件速度引导飘条点击购买
const val NON_VIP_HOME_DOWNLOAD_SPEED_ACTION = "non_vip_home_download_speed_action"

// 首页未再服务区弹窗展示
const val NON_VIP_NON_SERVICE_AREA_VIEW = "non_vip_non_service_area_view"

// 非vip空间不足弹窗
const val NON_VIP_NO_SPACE_ALERT_VIEW = "non_vip_no_space_alert_view"

// 非vip空间不足弹窗点击进入会员中心
const val NON_VIP_NO_SPACE_ALERT_ACTION = "non_vip_no_space_alert_action"

// 视频自动备份引导弹窗
const val NON_VIP_VIDEO_BACKUP_ALERT_VIEW = "non_vip_video_backup_alert_view"

// 视频自动备份点击进入会员中心
const val NON_VIP_VIDEO_BACKUP_ALERT_ACTION = "non_vip_video_backup_alert_action"

// 大文件上传引导弹窗
const val NON_VIP_LARGE_FILE_UPLOAD_ALERT_VIEW = "non_vip_large_file_upload_alert_view"

// 大文件上传引导弹窗点击进入会员中心
const val NON_VIP_LARGE_FILE_UPLOAD_ALERT_ACTION = "non_vip_large_file_upload_alert_action"

// 大文件上传超过vip上传限制弹窗(20GB)
const val VIP_LARGE_FILE_UPLOAD_OVER_LIMIT_ALERT_VIEW = "vip_large_file_upload_over_limit_alert_view"

// vip大文件上传尊享感Toast
const val VIP_LARGE_FILE_UPLOAD_TOAST_VIEW = "vip_large_file_upload_toast_view"

// 自动备份展示
const val BACKUP_SETTING_VIEW = "backup_setting_view"

// 非会员视频自动备份开
const val NON_VIP_VIDEO_BACKUP_OPEN_ACTION = "non_vip_video_backup_open_action"

// 会员视频自动备份开
const val VIP_VIDEO_BACKUP_OPEN_ACTION = "vip_video_backup_open_action"

// 会员视频自动备份关
const val VIP_VIDEO_BACKUP_CLOSE_ACTION = "vip_video_backup_close_action"

// 非会员首页文件列表底部视频自动备份飘条
const val NON_VIP_HOME_BOTTOM_VIDEO_BACKUP_VIEW = "non_vip_home_bottom_video_backup_view"

// 非会员首页文件列表底部视频自动备份飘条点击
const val NON_VIP_HOME_BOTTOM_VIDEO_BACKUP_ACTION = "non_vip_home_bottom_video_backup_action"

// 首页文件列表自动备份卡片展现
const val HOME_BACKUP_CARD_VIEW = "home_backup_card_view"

// 首页文件列表自动备份卡片点击开
const val HOME_BACKUP_CARD_OPEN_ACTION = "home_backup_card_open_action"

// 视频播放器展示uv/pv
const val VIDEO_PLAYER_PAGE_VIEW: String = "video_player_page_view"

// 视频播放器展示uv/pv(会员)
const val PREMIUM_VIDEO_PLAYER_PAGE_VIEW: String = "premium_video_player_page_view"

// 视频播放器-倍速播放入口点击uv/pv
const val VIDEO_PLAYER_SPEEDUP_BTN_CLICK: String = "click_video_player_speedup_btn"

// 视频播放器-倍速播放入口点击uv/pv(会员)
const val PREMIUM_VIDEO_PLAYER_SPEEDUP_BTN_CLICK: String = "click_video_player_speedup_btn_premium"

// 倍速播放器弹窗展示uv/pv
const val SHOW_VIDEO_PLAYER_SPEEDUP_DIALOG: String = "show_video_player_speedup_dialog"

// 倍速播放器弹窗展示uv/pv(会员)
const val SHOW_VIDEO_PLAYER_SPEEDUP_DIALOG_PREMIUM: String = "show_video_player_speedup_dialog_premium"

// 倍速播放器弹窗-点击开通会员按钮uv/pv（包括所有跳转到会员页面的按钮）
const val CLICK_VIDEO_PLAYER_SPEEDUP_PURCHASE: String = "click_video_player_speedup_purchase"

// 点击倍速：x0.75 uv/pv
const val CLICK_VIDEO_PLAYER_SPEEDUP_075: String = "click_video_player_speedup_075"

// 点击倍速：x1.0 uv/pv
const val CLICK_VIDEO_PLAYER_SPEEDUP_100: String = "click_video_player_speedup_100"

// 点击倍速：x1.25 uv/pv
const val CLICK_VIDEO_PLAYER_SPEEDUP_125: String = "click_video_player_speedup_125"

// 点击倍速：x1.5 uv/pv
const val CLICK_VIDEO_PLAYER_SPEEDUP_150: String = "click_video_player_speedup_150"

// 点击倍速：x2.0 uv/pv
const val CLICK_VIDEO_PLAYER_SPEEDUP_200: String = "click_video_player_speedup_200"

// 视频播放器-清晰度选择入口点击uv/pv
const val CLICK_VIDEO_PLAYER_RESOLUTION_BTN: String = "click_video_player_resolution_btn"

// 视频播放器-清晰度选择入口点击uv/pv(会员)
const val CLICK_VIDEO_PLAYER_RESOLUTION_BTN_PREMIUM: String = "click_video_player_resolution_btn_premium"

// 清晰度弹窗展示uv/pv
const val SHOW_VIDEO_PLAYER_RESOLUTION_DIALOG: String = "show_video_player_resolution_dialog"

// 清晰度弹窗展示uv/pv(会员)
const val SHOW_VIDEO_PLAYER_RESOLUTION_DIALOG_PREMIUM: String = "show_video_player_resolution_dialog_premium"

// 清晰度弹窗-点击开通会员按钮uv/pv（包括所有跳转到会员页面的按钮）
const val CLICK_VIDEO_PLAYER_RESOLUTION_PURCHASE: String = "click_video_player_resolution_purchase"

// 点击清晰度：1080P uv/pv
const val CLICK_VIDEO_PLAYER_RESOLUTION_1080: String = "click_video_player_resolution_1080"

// 从加载URL计算时间，直到WebViewClient的ONPAGEFINISHED回调结束。小于1s
const val LOGIN_LOAD_H5_ONPAGEFINISHED_LESS_THAN_1S = "login_load_h5_onpagefinished_less_than_1s"

// 从加载URL计算时间，直到WebViewClient的ONPAGEFINISHED回调结束。在1s-3s之间
const val LOGIN_LOAD_H5_ONPAGEFINISHED_BETWEEN_1S_AND_3S = "login_load_h5_onpagefinished_between_1s_and_3s"

// 从加载URL计算时间，直到WebViewClient的ONPAGEFINISHED回调结束。大于3s
const val LOGIN_LOAD_H5_ONPAGEFINISHED_MORE_THAN_3S = "login_load_h5_onpagefinished_more_than_3s"

// 从加载URL计算时间，直到前端调用Hybrid协议pageLoadComplete。小于1s
const val LOGIN_LOAD_H5_PAGELOADCOMPLETE_LESS_THAN_1S = "login_on_h5_pageLoadComplete_less_than_1s"

// 从加载URL计算时间，直到前端调用Hybrid协议pageLoadComplete。在1s-3s之间
const val LOGIN_LOAD_H5_PAGELOADCOMPLETE_BETWEEN_1S_AND_3S = "login_on_h5_pageLoadComplete_between_1s_and_3s"

// 从加载URL计算时间，直到前端调用Hybrid协议pageLoadComplete。大于3s
const val LOGIN_LOAD_H5_PAGELOADCOMPLETE_MORE_THAN_3S = "login_on_h5_pageLoadComplete_more_than_3s"

// 新用户外部导流归因
const val FROM_WAP_SHARE_NEW_USER_INSTALL = "from_wap_share_new_user_install"

// app改名一封信动画展现uv&pv
const val SHOW_APP_NAME_CHANGE_DIALOG: String = "show_app_name_change_dialog"

// app改名一封信点击『我知道了』uv&pv
const val CLICK_APP_NAME_CHANGE_DIALOG_CONFIRM: String = "click_app_name_change_dialog_confirm"

// 个人中心运营位展示
const val SHOW_USER_CENTER_ACTIVITY_BANNER: String = "show_user_center_activity_banner"

// 点击个人中心运营位
const val CLICK_USER_CENTER_ACTIVITY_BANNER: String = "click_user_center_activity_banner"

// 个人中心运营位关闭
const val CLOSE_USER_CENTER_ACTIVITY_BANNER: String = "close_user_center_activity_banner"

// 个人头像入口点击行为uv&pv
const val ENTER_USER_CENTER_BY_CLICK_HEAD_IMG: String = "enter_user_center_by_click_head_img"

// 个人中心入口右滑行为uv&pv
const val ENTER_USER_CENTER_BY_SCROLL_HOME: String = "enter_user_center_by_scroll_home"

// 个人中心入口出现 uv&pv，理论上等于上面两个 key 的和。
const val ENTER_USER_CENTER_APPEAR: String = "enter_user_center_appear"

// 首页运营位展示
const val SHOW_HOME_CARD_ACTIVITY_BANNER: String = "show_home_card_activity_banner"

// 点击首页运营位
const val CLICK_HOME_CARD_ACTIVITY_BANNER: String = "click_home_card_activity_banner"

// 首页运营位关闭
const val CLOSE_HOME_CARD_ACTIVITY_BANNER: String = "close_home_card_activity_banner"

// 最近上传卡片点击浏览uv&pv
const val HOME_CARD_RECENT_UPLOAD_CLICK_ITEM: String = "home_card_recent_upload_click_item"

// 最近上传卡片点击查看全部uv&pv
const val HOME_CARD_RECENT_UPLOAD_VIEW_ALL_CLICK: String = "home_card_recent_upload_view_all_click"

// 最近上传卡片点击关闭按钮uv&pv
const val HOME_CARD_RECENT_UPLOAD_CLOSE: String = "home_card_recent_upload_close"

// 首页tab-有卡片展现uv&pv
const val HOME_CARD_CARD_SHOW: String = "home_card_card_show"

// 首页tab-无卡片展现总uv&pv
const val HOME_CARD_NO_CARD_SHOW: String = "home_card_no_card_show"

// 首页下拉刷新次数
const val HOME_CARD_REFRESH_NUM: String = "home_card_refresh_num"

// 图片卡片的展示PV
const val HOME_CARD_IMAGE_SHOW: String = "home_card_image_show"

// 图片点击浏览uv&pv
const val HOME_CARD_IMAGE_CLICK: String = "home_card_image_click"

// 图片点击查看全部uv&pv
const val HOME_CARD_IMAGE_VIEW_ALL_CLICK: String = "home_card_image_view_all_click"

// 图片点击查看更多
const val HOME_CARD_IMAGE_VIEW_MORE_CLICK: String = "home_card_image_view_more_click"

// 图片点击关闭按钮uv&pv
const val HOME_CARD_IMAGE_CLOSE_CLICK = "home_card_image_close_click"

// 视频卡片的展示PV
const val HOME_CARD_VIDEO_SHOW = "home_card_video_show"

// 视频点击浏览uv&pv
const val HOME_CARD_VIDEO_CLICK = "home_card_video_click"

// 视频点击查看全部uv&pv
const val HOME_CARD_VIDEO_VIEW_ALL_CLICK = "home_card_video_view_all_click"

// 视频点击查看更多
const val HOME_CARD_VIDEO_VIEW_MORE_CLICK: String = "home_card_video_view_more_click"

// 视频点击关闭按钮uv&pv
const val HOME_CARD_VIDEO_CLOSE_CLICK = "home_card_video_close_click"

// vip卡片的展示PV
const val HOME_CARD_VIP_SHOW: String = "home_card_vip_show"

// vip免广告卡片的展示PV
const val HOME_CARD_VIP_AD_FREE_SHOW: String = "home_card_vip_ad_free_show"

// vip卡片的点击
const val HOME_CARD_VIP_CLICK: String = "home_card_vip_click"

// vip免广告卡片的点击
const val HOME_CARD_VIP_AD_FREE_CLICK: String = "home_card_vip_ad_free_click"

// vip免广告卡片的点击close
const val HOME_CARD_VIP_AD_FREE_CLICK_CLOSE: String = "home_card_vip_ad_free_click_close"

// 首页tab时长统计
const val MAIN_TAB_SHOW_ON_START = "main_tab_show_on_start"

// 首页tab时长统计
const val MAIN_TAB_SHOW_ON_END = "main_tab_show_on_end"

/**
 *  Android 版本更新
 */

// 版本更新红点展示 uv/pv
const val UPDATE_RED_POINT_SHOW = "update_red_point_show"

// 版本更新入口点击 uv/pv
const val UPDATE_ENTER_CLICK_EVENT = "update_enter_click_event"

// 最新版本 Toast 提示
const val LATEST_VERSION_TOAST_EVENT = "latest_version_toast_event"

// Google 渠道更新人数
const val GOOGLE_UPDATE_SUCCESS_COUNT = "google_update_success_count"

// Google 渠道点击更新人数
const val GOOGLE_UPDATE_ACCEPT_COUNT = "google_update_accept_count"

// 非 Google 渠道更新弹窗展示
const val UPDATE_DIALOG_SHOW = "update_dialog_show"

// 非 Google 渠道点击升级人数
const val UPDATE_DIALOG_CLICK_ACCEPT = "update_dialog_click_accept"

// 非 Google 渠道下载 apk 成功次数
const val UPDATE_DIALOG_DOWNLOAD_SUCCESS = "update_dialog_download_success"

// 非 Google 渠道升级成功人数
const val UPDATE_DIALOG_SUCCESS_COUNT = "update_dialog_success_count"

// 版本详情页展示
const val APP_VERSION_INFO_VIEW_SHOW = "app_version_info_view_show"

// 版本详情页点击立即更新
const val APP_VERSION_INFO_VIEW_UPDATE_BUTTON_CLICK = "app_version_info_view_update_button_click"

// 最近上传二级页面
const val RECENT_UPLOAD_PAGE_SHOW = "recent_upload_page_show"

// 最近上传图片三级页面
const val RECENT_UPLOAD_IMAGE_DETAIL_PAGE_SHOW = "recent_upload_image_detail_page_show"

// 最近上传视频三级页面
const val RECENT_UPLOAD_VIDEO_DETAIL_PAGE_SHOW = "recent_upload_video_detail_page_show"

// 最近上传文件三级页面
const val RECENT_UPLOAD_FILE_DETAIL_PAGE_SHOW = "recent_upload_file_detail_page_show"

// workManager执行时长
const val WORK_MANAGER_BACKUP_RUN_TIME = "work_manager_backup_run_time"

/**
 * 上传弹窗相关
 * */
// 在首页tab点击+号用户uv/pv
const val UPLOAD_DIALOG_FROM_HOME_TAB: String = "upload_dialog_from_home_tab"

// 在文件tab点击+号用户uv/pv
const val UPLOAD_DIALOG_FROM_FILE_TAB: String = "upload_dialog_from_file_tab"

// 上传弹窗展现uv/pv
const val UPLOAD_DIALOG_VIEW: String = "upload_dialog_view"

/**
 * 账号同步相关
 * */
// 成功调起APP并进行账户同步次数
const val APP_WAKE_UP_TIMES_BY_ACCOUNT_ASYNC: String = "app_wake_up_times_by_account_async"

// 当天最近观看视频页面展现uv/pv
const val RECENT_VIDEO_BY_DATE_SHOW = "recent_video_by_date_show"

// 当天最近观看页面展现uv/pv
const val RECENT_IMAGE_BY_DATE_SHOW = "recent_image_by_date_show"

// 全部图片观看三级页面展现uv/pv
const val RECENT_IMAGE_3RD_SHOW = "recent_image_3rd_show"

// 全部图片观看二级页面展现uv/pv
const val RECENT_IMAGE_2ND_SHOW = "recent_image_2nd_show"

// 2.1.5
// sweet调端成功埋点
const val SWEET_SELEIE_SUCCESS = "com.cam001.selfie_success"

/**
 * v2.1.5
 * 省电白名单相关
 * */
// 加入电池白名单用户uv/pv
const val POWER_SAVING_PLAN: String = "power_saving_plan"

// 已加入电池白名单—用户闲时后台备份时长
const val POWER_SAVING_PLAN_JOINED_BACKUP_TIME: String = "power_saving_plan_joined_backup_time"

// 未加入电池白名单—用户闲时后台备份时长
const val POWER_SAVING_PLAN_UNJOIN_BACKUP_TIME: String = "power_saving_plan_unjoin_backup_time"

// 2.1.5视频预加载临时埋点
const val HTTP_CREATE_ERROR = "http_create_error_"
const val HTTP_CONNECT_ERROR = "http_connect_error_"
const val HTTP_PARSE_M3U8_FILE_ERROR = "http_parse_m3u8_file_error_"
const val DEVICE_AVAILABLE_SPACE = "device_available_space_"
const val PRELOAD_SUCCESS = "preload_success"
const val PRELOAD_FAIL = "preload_fail"
const val PRELOAD_DOWN_FILE_ERROR = "preload_down_file_error"
const val PRELOAD_DOWN_FILE_IO_ERROR = "preload_down_file_io_error"
const val PRELOAD_DOWN_FILE_SLICE_ERROR = "preload_down_file_slice_error"

/**
 * v2.2.0 埋点
 */
// 个人中心-点击保险箱
const val ENTER_SAFE_BOX_FROM_ABOUT_ME = "enter_safe_box_from_about_me"

// 文件 Tab 点击保险箱
const val ENTER_SAFE_BOX_FROM_FILE_TAB = "enter_safe_box_from_file_tab"

// 开启保险箱功能-非会员人数
const val NON_VIP_USE_SAFE_BOX = "non_vip_use_safe_box"

// 开启保险箱功能-会员人数
const val VIP_USE_SAFE_BOX = "vip_use_safe_box"

// 销毁重置保险箱人数
const val DESTROY_SAFE_BOX = "destroy_safe_box"

// 开启生物识别用户人数
const val OPEN_FINGERPRINT = "open_fingerprint "

// 开屏广告展示
const val OPEN_APP_AD_SHOW = "open_app_ad_show"

// 首页卡片广告展示
const val HOME_CARD_AD_SHOW = "home_card_ad_show"

// 首页卡片广告展示
const val HOME_CARD_AD_RECENTLY_SHOW: String = "home_card_ad_recently_show"

// 首页卡片广告关闭
const val HOME_CARD_AD_CLICK_CLOSE = "home_card_ad_click_close"

// 开屏广告展示
const val SPLASH_ACTIVITY_SHOW = "splash_activity_show"

// 开屏广告展示
const val SPLASH_ACTIVITY_CLICK = "splash_activity_click"

// 激励视频引导弹窗
const val REWARD_AD_GUIDE_DIALOG_SHOW = "reward_ad_guide_dialog_show"

// 激励视频引导弹窗关闭
const val REWARD_AD_GUIDE_DIALOG_CLICK_CLOSE = "reward_ad_guide_dialog_click_close"

// 激励视频完成弹窗
const val REWARD_AD_SUCCESS_DIALOG_SHOW = "reward_ad_success_dialog_show"

// 激励视频完成弹窗点击使用
const val REWARD_AD_SUCCESS_DIALOG_CLICK_USE = "reward_ad_success_dialog_click_use"

// 个人中心-空间分析器点击次数
const val ABOUT_ME_STORAGE_ANALYZER_CLICK: String = "about_me_storage_analyzer_click"

// 首页卡片-空间分析器展现次数
const val HOME_CARD_STORAGE_ANALYZER_SHOW: String = "home_card_storage_analyzer_show"

// 首页卡片-空间分析器点击次数
const val HOME_CARD_STORAGE_ANALYZER_CLICK: String = "home_card_storage_analyzer_click"

// 空间检测二级页面展示uv/pv
const val STORAGE_ANALYZER_SHOW: String = "storage_analyzer_show"

// 未开启自动备份—照片备份卡片展现uv/pv
const val STORAGE_ANALYZER_NO_BACKUP_PHOTO_SHOW: String = "storage_analyzer_no_backup_photo_show"

// 未开启自动备份—照片备份卡片点击uv/pv
const val STORAGE_ANALYZER_BACKUP_PHOTO_CLICK: String = "storage_analyzer_backup_photo_click"

// 未开启自动备份—视频备份卡片展现uv/pv
const val STORAGE_ANALYZER_NO_BACKUP_VIDEO_SHOW: String = "storage_analyzer_no_backup_video_show"

// 未开启自动备份—视频备份卡片点击uv/pv
const val STORAGE_ANALYZER_BACKUP_VIDEO_CLICK: String = "storage_analyzer_backup_video_click"

// 本地清理三级页面展现uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_SHOW: String = "storage_analyzer_clean_up_show"

// 本地清理三级页面展现uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_NEW_SHOW: String = "storage_analyzer_clean_up_new_show"

// 点击『选择删除文件』按钮uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_DEL_CLICK: String = "storage_analyzer_clean_up_del_click"

// 成功删除弹窗展现uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_DEL_OK_SHOW: String = "storage_analyzer_clean_up_del_ok_show"

// 成功删除弹窗展现uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_DEL_OK_NEW_SHOW: String = "storage_analyzer_clean_up_del_ok_new_show"

// 成功删除弹窗展现uv/pv
const val STORAGE_ANALYZER_CLEAN_UP_EMPTY_SHOW: String = "storage_analyzer_clean_up_empty_show"

/**
 * v 2.3.0 埋点
 */

// --------云解压埋点----------

// 非会员用户，点击压缩包次数
const val NORMAL_USER_CLICK_UNZIP_COUNT: String = "normal_user_click_unzip_count"

// 会员用户，点击压缩包次数
const val VIP_USER_CLICK_UNZIP_COUNT: String = "vip_user_click_unzip_count"

// 云解压非会员引导弹窗展现次数
const val SHOW_PREMIUM_GUIDE_DIALOG_COUNT_FOR_UNZIP: String = "show_premium_guide_dialog_count_for_unzip"

// 非会员用户在云解压引导弹窗中点击开通会员的次数
const val USER_CLICK_OPEN_PREMIUM_FOR_UNZIP: String = "user_click_open_premium_for_unzip"

// 会员用户成功打开压缩文件的次数
const val UNZIP_FILE_SUCCESS: String = "unzip_file_success"

// 用户点击尝试打开压缩文件格式&数量
const val USER_CLICK_UNZIP_FILE_TYPE: String = "user_click_unzip_file_type"

// 用户成功打开压缩文件格式&数量
const val UNZIP_FILE_TYPE_SUCCESS: String = "unzip_file_type_success"

// 用户点击解压到当前文件夹次数
const val USER_CLICK_UNZIP_TO_CURRENT: String = "user_click_unzip_to_current"

// 用户点击解压到指定文件夹次数
const val USER_CLICK_UNZIP_TO_FILE: String = "user_click_unzip_to_file"

// 用户点击解压到文件 list 中并成功
const val UNZIP_TO_FILE_LIST_SUCCESS: String = "unzip_to_file_list_success"

// 点击资源圈版权举报
const val CLICK_SHARE_RESOURCE_REPORT: String = "click_share_resource_report"

// 点击文档预览
const val CLICK_DOC_PREVIEW: String = "click_doc_preview"

// 文档成功预览
const val PREVIEW_DOC_LOAD_SUCCESS: String = "preview_doc_load_success"

// 文档预览页点击返回
const val CLICK_PREVIEW_DOC_BACK: String = "click_preview_doc_back"

/**
 * 非官方渠道包，文档模块资源索引问题
 */
const val DOCUMENT_MODULE_RES_ERROR: String = "document_module_res_not_found_exception"


// 已用容量>0的上报
const val KEY_USED_QUOTA: String = "key_used_quota"

// 远端上传/入口 传输面板点击【远程上传】UV&PV
const val ALERT_WINDOW_CLICK_REMOTE_UPLOAD: String = "alert_window_click_remote_upload"

// 远端上传 选择【新建链接任务】UV&PV
const val REMOTE_UPLOAD_NEW_LINK_TASK: String = "remote_upload_new_link_task"

// 远端上传 选择【新建BT任务】UV&PV
const val REMOTE_UPLOAD_NEW_BT_TASK: String = "remote_upload_new_bt_task"

// 远端上传 点击【教程】UV&PV
const val REMOTE_UPLOAD_CLICK_HELP: String = "remote_upload_click_help"

// 远端上传 点击新建链接任务的【保存】按钮UV&PV
const val REMOTE_UPLOAD_CLICK_NEW_LINK_SAVE: String = "remote_upload_click_new_link_save"

// 远端上传 链接任务成功下载UV&PV
const val REMOTE_UPLOAD_CLICK_LINK_DOWNLOAD_SUCCESS: String = "remote_upload_click_link_download_success"

// 远端上传 点击新建BT任务的【保存】按钮UV&PV
const val REMOTE_UPLOAD_CLICK_BT_SAVE: String = "remote_upload_click_bt_save"

// 远端上传 BT任务成功下载UV&PV
const val REMOTE_UPLOAD_CLICK_BT_DOWNLOAD_SUCCESS: String = "remote_upload_click_bt_download_success"

// 远端上传/传输列表 从链接任务下载成功，点击预览UV&PV
const val REMOTE_UPLOAD_CLICK_LINK_PREVIEW_SUCCESS: String = "remote_upload_click_link_preview_success"

// 远端上传/传输列表 从BT任务下载成功，点击预览UV&PV
const val REMOTE_UPLOAD_CLICK_BT_PREVIEW_SUCCESS: String = "remote_upload_click_bt_preview_success"

// 资源圈点赞
const val RESOURCE_CIRCLE_LIKE_ACTION: String = "resource_circle_like_action"

// 资源圈保存
const val RESOURCE_CIRCLE_DETAIL_SAVE_ACTION: String = "resource_circle_detail_save_action"

// 资源圈分享
const val RESOURCE_CIRCLE_DETAIL_SHARE_ACTION: String = "resource_circle_detail_share_action"

// 资源圈频道内时长
const val SHARE_RESOURCE_SHOW_ON_START: String = "share_resource_duration_start"

// 资源圈频道内时长
const val SHARE_RESOURCE_SHOW_ON_END: String = "share_resource_duration_end"

// 资源圈频道-二级FEED页
const val SHARE_RESOURCE_PAGE_TAG_FEED: String = "share_resource_page_tag_feed"

// 资源圈频道-三级详情页
const val SHARE_RESOURCE_PAGE_TAG_DETAIL: String = "share_resource_page_tag_detail"

// 资源圈7.30补：首页资源圈卡片展现uv/pv
const val SHARE_RESOURCE_HOME_CARD_SHOW: String = "share_resource_home_card_show"

// 资源圈7.30补：首页资源圈卡片点击进入FEED页面uv/pv
const val SHARE_RESOURCE_HOME_CARD_JUMP_FEED: String = "share_resource_home_card_jump_feed"

// 资源圈7.30补：首页资源圈卡片点击进入资源详情页uv/pv
const val SHARE_RESOURCE_HOME_CARD_JUMP_DETAIL: String = "share_resource_home_card_jump_detail"

// 资源圈7.30补：首页资源圈固定入口展现uv/pv
const val SHARE_RESOURCE_HOME_TOP_ICON_SHOW: String = "share_resource_home_top_icon_show"

// 资源圈7.30补：首页资源圈固定入口点击uv/pv
const val SHARE_RESOURCE_HOME_TOP_ICON_CLICK: String = "share_resource_home_top_icon_click"

// 资源圈7.30补：资源圈FEED页面展现uv/pv
const val SHARE_RESOURCE_FEED_PAGE_SHOW: String = "share_resource_feed_page_show"

// 资源圈7.30补：资源圈FEED页面点击资源uv/pv
const val SHARE_RESOURCE_FEED_PAGE_CLICK_ITEM: String = "share_resource_feed_page_click_item"

// 入口进入资源圈频道(首页右上角、列表更多，进入FEED的点击事件)
const val SHARE_RESOURCE_ENTRANCE: String = "share_resource_entrance"

// 点击资源进行浏览数
const val SHARE_RESOURCE_DETAIL_ACTION: String = "share_resource_detail_action"

// 资源圈详情页展示 - 针对全部入口
const val SHARE_RESOURCE_DETAIL_PAGE_SHOW: String = "share_resource_detail_page_show"

// 资源圈-筛选项点击
const val SHARE_RESOURCE_TAG_CLICK: String = "share_resource_tag_click"

// 资源圈-刷新或加载更多
const val SHARE_RESOURCE_FEED_REFRESH: String = "share_resource_feed_refresh"

// 发送用户日活
const val REPORT_USER_ACTIVE: String = "report_user_active"

// 发送用户日活失败
const val REPORT_USER_ACTIVE_FAILED: String = "report_user_active_failed"

// 未登录退出
const val ON_NOT_LOGIN_QUIT: String = "on_not_login_quit"

// kaokao 登录
const val CLICK_KAKAO_LOGIN: String = "click_kakao_login"

// kaokao 登录成功
const val KAOKAO_LOGIN_SUCCESS: String = "kaokao_login_success"

// kaokao 登录失败
const val KAOKAO_LOGIN_FAILED: String = "kaokao_login_failed"

// GOOGLE 登录
const val CLICK_GOOGLE_LOGIN: String = "click_google_login"

// GOOGLE 登录成功
const val GOOGLE_LOGIN_SUCCESS: String = "google_login_success"

// GOOGLE 登录失败
const val GOOGLE_LOGIN_FAILED: String = "google_login_failed"

// FACEBOOK 登录
const val CLICK_FACEBOOK_LOGIN: String = "click_facebook_login"

// FACEBOOK 登录成功
const val FACEBOOK_LOGIN_SUCCESS: String = "facebook_login_success"

// FACEBOOK 登录失败
const val FACEBOOK_LOGIN_FAILED: String = "facebook_login_failed"

// Line登陆
const val CLICK_LINE_LOGIN: String = "click_line_login"

// Line 登录成功
const val LINE_LOGIN_SUCCESS: String = "line_login_success"

// Line 登录失败
const val LINE_LOGIN_FAILED: String = "line_login_failed"

// fe登录成功
const val LOGIN_SUCCESS_FE: String = "login_success_fe"

// fe登录失败
const val LOGIN_FAILED_FE: String = "login_failed_fe"

// 首页tab展现总uv&pv
const val HOME_CARD_TAB_PV: String = "home_card_tab_pv"

// 资源圈tab展现
const val HOME_VIDEO_TAB_PV: String = "home_video_tab_pv"

// 资源小组tab展现
const val HOME_GROUP_TAB_PV: String = "home_group_tab_pv"

// 点击首页卡片tab
const val CLICK_HOME_CARD_TAB: String = "click_home_card_tab"

// 点击文件列表tab
const val CLICK_HOME_FILE_TAB = "click_home_file_tab"

// 点击小组列表tab
const val CLICK_HOME_GROUP_TAB = "click_home_group_tab"

// 首页文件tab展现总uv&pv
const val HOME_FILE_TAB_PV = "home_file_tab_pv"

// 点击站长中心Tab
const val CLICK_HOME_EARN_TAB = "click_earn_tab"

// 首页download tab展现pv
const val HOME_DOWNLOAD_TAB_PV = "home_downloader_tab_pv"

// 首页视频服务化tab点击
const val CLICK_HOME_VIDEO_SERVICE_TAB: String = "click_home_video_service_tab"

// 首页download tab点击
const val CLICK_HOME_DOWNLOAD_TAB: String = "click_home_download_tab"

// 首页视频服务化展示pv
const val HOME_VIDEO_SERVICE_PV: String = "home_video_service_pv"

// 相册tab点击uv&pv
const val CLICK_TIMELINE_TAB: String = "click_timeline_tab"

// 相册tab展示uv&pv
const val HOME_TIME_LINE_PV: String = "home_time_line_pv"

// 首页分享tab点击
const val CLICK_HOME_SHARE_TAB = "click_home_share_tab"

// 首页分享tab展现总uv&pv
const val HOME_SHARE_TAB_PV = "home_share_tab_pv"

// 资源圈频道-搜索页
const val SHARE_RESOURCE_PAGE_TAG_SEARCH = "share_resource_page_tag_search"

// 资源圈频道-反馈页
const val SHARE_RESOURCE_PAGE_TAG_FEEDBACK = "share_resource_page_tag_feedback"

// 资源圈拉新-专区展示
const val SHARE_RESOURCE_FEED_HOT_AREA_SHOW = "share_resource_feed_hot_area_show"

// 资源圈拉新-滑动行为
const val SHARE_RESOURCE_FEED_HOT_AREA_SCROLL = "share_resource_feed_hot_area_scroll"

// 资源圈拉新-专区点击全部按钮
const val SHARE_RESOURCE_FEED_CLICK_VIEW_ALL = "share_resource_feed_click_view_all"

// 资源圈拉新-专区Feed页展示
const val SHARE_RESOURCE_FEED_HOT_AREA_LIST = "share_resource_feed_hot_area_list"

// 资源圈拉新-专区 Feed 点击单个推荐
const val SHARE_RESOURCE_HOT_DETAIL_ACTION = "share_resource_hot_detail_action"

// 资源圈拉新-专区内点击推荐内容
const val SHARE_RESOURCE_HOT_RECOMMEND_DETAIL_ACTION = "share_resource_hot_recommend_detail_action"

// 资源圈拉新-专区详情页展示
const val SHARE_RESOURCE_HOT_PAGE_SHOW = "share_resource_hot_page_show"

// 资源圈拉新-专区内容分享
const val RESOURCE_CIRCLE_HOT_SHARE_ACTION = "resource_circle_hot_share_action"

// 资源圈搜索框页面展现uv/pv
const val SHARE_RESOURCE_SEARCH_PAGE_SHOW = "share_resource_search_page_show"

// 搜索历史记录item点击
const val SEARCH_HISTORY_CLICK = "search_item_click"

// 点击搜索按钮uv/pv
const val SHARE_RESOURCE_SEARCH_BUTTON_CLICK = "share_resource_search_button_click"

// 搜索结果为空页面展现uv/pv
const val SHARE_RESOURCE_SEARCH_RESULT_EMPTY_PAGE_SHOW = "share_resource_search_result_empty_page_show"

// 搜索结果为有结果展现uv/pv
const val SHARE_RESOURCE_SEARCH_RESULT_PAGE_SHOW = "share_resource_search_result_page_show"

// 影片反馈页面展现uv/pv
const val SHARE_RESOURCE_SEARCH_FEEDBACK_PAGE_SHOW = "share_resource_search_feedback_page_show"

// 影片反馈成功提交页面展现uv/pv
const val SHARE_RESOURCE_SEARCH_FEEDBACK_SUCCEED_PAGE_SHOW = "share_resource_search_feedback_succeed_page_show"

// 保险箱开通成功并进入的埋点
const val SAFE_BOX_FILE_LIST_PAGE_SHOW = "safe_box_file_list_page_show"

// 资源圈 引导转存toast展现：观看1分钟后，提示toast展现uv/pv（统计横屏和竖屏一共）
const val SHARE_RESOURCE_SAVE_GUIDE_SHOW: String = "share_resource_save_guide_show"

// 资源圈 引导转存toast展现：网络不稳定，提示toast展现uv/pv（统计横屏和竖屏一共）
const val SHARE_RESOURCE_NET_INSTABLE_GUIDE_SHOW: String = "share_resource_net_instable_guide_show"

// 资源圈 分享气泡样式展现：分享给朋友提示气泡样式展示toast展现uv/pv
const val SHARE_RESOURCE_SHARE_GUIDE_SHOW: String = "share_resource_share_guide_show"

// 资源圈 已保存到用户文件夹中toast展现：已保存到用户文件夹中toast展现uv/pv
const val SHARE_RESOURCE_HAS_SAVED_FLODER: String = "share_resource_has_saved_floder"

/**
 * 补充资源圈专区埋点
 */
// 资源圈 Feed 点击单个专区内容 uv/pv
const val SHARE_RESOURCE_FEED_HOT_ITEM_CLICK = "share_resource_feed_hot_item_click"

// 资源圈 Feed 点击单个专区内容 位置信息 从1开始
const val SHARE_RESOURCE_HOT_CELL_CLICK = "share_resource_hot_cell_click"
const val SHARE_RESOURCE_HOT_CELL_ITEM_INDEX = "item_index"

// 资源圈频道内时长
const val SHARE_RESOURCE_PAGE_HOT_TAG_FEED: String = "share_resource_page_hot_tag_feed"

// 资源圈频道内时长
const val SHARE_RESOURCE_PAGE_HOT_TAG_DETAIL: String = "share_resource_page_hot_tag_detail"

/**
 * 2.4.5埋点
 */

// 新用户首页引导浮层-展现uv/pv
const val HOME_USER_GUIDE_AUTO_BACKUP_SHOW = "home_user_guide_auto_backup_show"

// 新用户首页引导浮层-点击开启自动备份uv/pv
const val HOME_USER_GUIDE_AUTO_BACKUP_CLICK = "home_user_guide_auto_backup_click"

/**扫码登录相关埋点**/
// 点击抽屉扫码icon
const val SCAN_QRCODE_LOGIN_ENTER_CLICK = "scan_qrcode_login_enter_click"

// 扫码页展示
const val SCAN_QRCODE_PAGE_SHOW = "scan_qrcode_page_show"

// 确认页展示
const val SCAN_QRCODE_CONFIRM_PAGE_SHOW = "scan_qrcode_confirm_page_show"

// 点击二维码无法识别的取消
const val SCAN_QRCODE_UNRECOGNIZED_CANCEL_CLICK = "scan_qrcode_unrecognized_cancel_click"

// 确认页失效 case
const val SCAN_QRCODE_CONFIRM_INVALID = "scan_qrcode_confirm_invalid"

// 二维码无法识别的 uv
const val SCAN_QRCODE_UNRECOGNIZED_SHOW = "scan_qrcode_unrecognized_show"

// 二维码无法识别-点击重新登录
const val SCAN_QRCODE_UNRECOGNIZED_RESCAN_CLICK = "scan_qrcode_unrecognized_rescan_click"

// 二维码失效-点击退出
const val SCAN_QRCODE_INVALID_CANCEL_CLICK = "scan_qrcode_invalid_cancel_click"

// 二维码失效-点击重新扫码
const val SCAN_QRCODE_INVALID_RESCAN_CLICK = "scan_qrcode_invalid_rescan_click"

// 登录确认页点击 ok
const val SCAN_QRCODE_CONFIRM_PAGE_OK_CLICK = "scan_qrcode_confirm_page_ok_click"

// 登录确认页点击取消登录
const val SCAN_QRCODE_CONFIRM_PAGE_CANCEL_CLICK = "scan_qrcode_confirm_page_cancel_click"

/** 消息相关 */
// 个人中心-消息通知点击次数 在个人中心点击进入消息通知uv/pv
const val MESSAGE_SETTING_ENTRY_CLICK: String = "message_setting_entry_click"

// 资源圈-消息通知点击次数 在资源圈点击进入消息通知uv/pv
const val MESSAGE_RESOURCE_CIRCLE_ENTRY_CLICK: String = "message_resource_circle_entry_click"

// 内容广场消息通知中点击查找内容 点击查找内容uv/pv
const val RESOURCE_CIRCLE_MESSAGE_FEEDBACK_CLICK: String = "resource_circle_message_feedback_click"

// 全局消息通知中点击查看内容 全局消息通知点击资源详情次数uv/pv（需要上报资源圈id）
const val GLOBAL_MESSAGE_LIST_DETAIL_CLICK: String = "global_message_list_detail_click"

// 内容广场消息通知中点击查看内容 内容广场消息通知点击资源详情次数uv/pv（需要上报资源圈id）
const val RESOURCE_MESSAGE_LIST_DETAIL_CLICK: String = "resource_message_list_detail_click"

//资源圈-视频上架推送展示次数
const val MESSAGE_NEW_MOVIE_ALERT_SHOW = "message_new_movie_alert_show"

//资源圈-视频上架推送点击次数
const val MESSAGE_NEW_MOVIE_ALERT_CLICK = "message_new_movie_alert_click"

// 点击首页工具栏新手引导视频
const val CLICK_HOME_TOOLS_USER_GUIDE_VIDEO = "click_home_tools_user_guide_video"

// 点击首页工具栏空间管理
const val CLICK_HOME_TOOLS_STORAGE_MANAGER = "click_home_tools_storage_manager"

// 点击首页工具栏我的分享
const val CLICK_HOME_TOOLS_MY_SHARELINK = "click_home_tools_my_sharelink"

// 点击首页工具栏保险箱
const val CLICK_HOME_TOOLS_SAFE_BOX = "click_home_tools_safe_box"

// 点击首页工具栏远程上传
const val CLICK_HOME_TOOLS_OFFLINE_UPLOAD = "click_home_tools_offline_upload"

// 点击首页工具栏内容广场
const val CLICK_HOME_TOOLS_SHARE_RESOURCE = "click_home_tools_share_resource"

// 点击首页工具栏回收站
const val CLICK_HOME_TOOLS_RECYCLE = "click_home_tools_recycle"

// 点击首页工具栏福利中心
const val CLICK_HOME_TOOLS_WELFARE_CENTER = "click_home_tools_welfare_center"

// 点击首页工具栏更多功能
const val CLICK_HOME_TOOLS_MORE = "click_home_tools_more"

// 点击首页工具栏全网搜索
const val CLICK_HOME_TOOLS_SEARCH = "click_home_tools_search"

// 首页卡片-空间分析器点击次数
const val HOME_STORAGE_ANALYZER_CARD_CLICK: String = "home_storage_analyzer_card_click"

// 空间检测二级页面展示uv/pv
const val HOME_STORAGE_ANALYZER_CARD_SHOW: String = "home_storage_analyzer_card_show"

// target30 通过mediaStore创建文件uri返回null时埋点
const val MEDIASTORE_CREATE_LOCAI_URL_NULL = "mediastore_create_local_uri_null"

// 授权失败
const val SAF_PERMISSION_FAILED = "saf_permission_failed"

// 开启fd没有权限
const val OPEN_FD_SECURITY_EXCEPTION: String = "open_fd_security_exception_v2"

// target30创建uri错误
const val TARGET30_CREATE_URI_ERROR = "target30_create_uri_error"

// target30重命名文件错误
const val TARGET30_RENAME_URI_ERROR = "target30_rename_uri_error"

/** Push 推送 */
// 消息到达数-端上报
const val PUSH_DID_RECEIVE_NOTIFICATION = "push_did_receive_notification"

// 消息展示数-发送通知
const val PUSH_MESSAGE_SHOW_NOTIFICATION = "push_message_show_notification"

// 消息点击
const val PUSH_DID_CLICK_NOTIFICATION = "push_did_click_notification"

// 点击消息打开落地页
const val PUSH_OPEN_PAGE_SUCCESS = "push_open_page_success"

/** 分享面板 */
// 点击Copy links的uv&pv
const val SNS_SHARE_USE_COPY_LINK_ACTION = "sns_share_use_copy_link_action"

// 点击Email的uv&pv
const val SNS_SHARE_USE_EMAIL_ACTION = "sns_share_use_email_action"

// 点击Facebook一键分享的uv&pv
const val SNS_SHARE_USE_FACEBOOK_ACTION = "sns_share_use_facebook_action"

// 点击Facebook Mesenger一键分享的uv&pv
const val SNS_SHARE_USE_FB_MESSENGER_ACTION = "sns_share_use_fb_messenger_action"

// 点击Whatsapp一键分享的uv&pv
const val SNS_SHARE_USE_WA_ACTION = "sns_share_use_wa_action"

// 点击Telegram一键分享的uv&pv
const val SNS_SHARE_USE_TG_ACTION = "sns_share_use_tg_action"

// 点击设置的uv&pv
const val SNS_SHARE_SETTING_ACTION = "sns_share_setting_action"

/** 端外资源搜索相关 */
// 搜索关键词引导 标签展示uv/pv
const val SHARE_RESOURCE_SEARCH_LABEL_SHOW: String = "share_resource_search_label_show"

// 搜索关键词引导 标签搜索uv/pv
const val SHARE_RESOURCE_SEARCH_LABEL_ACTION: String = "share_resource_search_label_action"

// 跳转 d 站资源的链接 展示uv/pv
const val SHARE_RESOURCE_MORE_D_LINK_SHOW: String = "share_resource_more_d_link_show"

// 跳转 d 站资源的链接 点击uv/pv
const val SHARE_RESOURCE_MORE_D_LINK_CLICK: String = "share_resource_more_d_link_click"

/** 补充保险箱会员引导弹窗埋点 */
// 保险箱触发会员引导弹窗展现
const val SAFE_BOX_PREMIUM_GUIDE_SHOW: String = "safe_box_premium_guide_show"

// 保险箱触发会员引导弹窗并点击
const val SAFE_BOX_PREMIUM_GUIDE_CLICK: String = "safe_box_premium_guide_click"

// 删除文件会员权益引导展现
const val DELETE_RECYCLE_BUY_VIP_GUIDE_SHOW: String = "delete_recycle_buy_vip_guide_show"

// 删除文件会员权益引导点击
const val DELETE_RECYCLE_BUY_VIP_GUIDE_CLICK: String = "delete_recycle_buy_vip_guide_click"

// 登录页的加载时长
const val LOGIN_ACTIVITY_LOADING_DURATION: String = "login_activity_loading_duration"

// 登录页的url加载时长
const val LOGIN_URL_LOADING_DURATION: String = "login_url_loading_duration"

// 设置页点击切换账号
const val CLICK_SETTING_SWITCH_ACCOUNT: String = "click_setting_switch_account"

// 切换帐号页展示
const val LOGIN_HISTORY_VIEW_PV: String = "login_history_view_pv"

// 切换帐号页点击左上角返回按钮
const val CLICK_LOGIN_HISTORY_VIEW_FINISH: String = "click_login_history_view_finish"

// 点击登录记录上的切换按钮
const val CLICK_LOGIN_HISTORY_SWITCH: String = "click_login_history_switch"

// 账号切换成功
const val SWITCH_ACCOUNT_SUCCESS: String = "switch_account_success"

// 资源圈搜索点击搜索结果条目 - 输入搜索
const val SHARE_RESOURCE_INPUT_SEARCH_RESULT_CLICK: String = "share_resource_input_search_result_click"

// 资源圈搜索点击搜索结果条目 - 点击热词搜索
const val SHARE_RESOURCE_HOTWORD_SEARCH_RESULT_CLICK: String = "share_resource_hotword_search_result_click"

// 字幕开关 开
const val SUBTITLE_SWITCH_ON = "subtitle_switch_on"

// 字幕开关 关
const val SUBTITLE_SWITCH_OFF = "subtitle_switch_off"

// 字幕 面板展现
const val SUBTITLE_VIEW_SHOW = "subtitle_view_show"

// 字幕 条目点击
const val SUBTITLE_ITEM_CLICK = "subtitle_item_click"

// 点展 字幕条目展现source
const val SUBTITLE_ITEM_SHOW = "subtitle_item_show"

// 点展 字幕条目点击source
const val SUBTITLE_ITEM_CLICK_NEW = "subtitle_item_click_new"

// 点展 字幕 条目内容展现
const val SUBTITLE_ITEMS_INFO_SHOW_KEY = "subtitle_info"

// 点展 字幕 条目内容展现
const val SUBTITLE_ITEMS_INFO_CLICK_KEY = "subtitle_click"

// 点展 字幕 条目内容md5
const val SUBTITLE_ITEM_MD5_KEY = "md5"

// 资源圈分类页面展示
const val SHARE_RESOURCE_CATEGORY_LIST_SHOW = "share_resource_category_list_show"

// 资源圈分类页面中条目点击
const val SHARE_RESOURCE_CATEGORY_LIST_ITEM_CLICK = "share_resource_category_list_item_click"

/**
 * 账号注销埋点
 */
// 点击设置 List
const val SETTING_ITEM_CLICK = "setting_item_click"

// 账号注销页面展示
const val ACCOUNT_LOGIN_OFF_PAGE_SHOW = "account_login_off_page_show"

// 点击【注销】
const val ACCOUNT_CLICK_LOGIN_OFF = "account_click_login_off"

/**
 * 账号管理优化相关埋点
 */
// 点击【管理】按钮
const val LOGIN_CLICK_MANAGER = "login_click_manager"

// 点击【移除】按钮
const val LOGIN_CLICK_COMPLETE = "login_click_complete"

// 确认移除弹窗-点击【取消】
const val REMOVE_CONFIRM_CANCEL_CLICK = "remove_confirm_cancel_click"

// 确认移除弹窗-点击【移除】
const val REMOVE_CONFIRM_OK_CLICK = "remove_confirm_ok_click"

// 点击【添加账号】uv/pv
const val CLICK_ADD_ACCOUNT = "click_add_account"

/**
 * 全网搜索相关埋点
 */
//用户进入全网搜索频道uv/pv
const val NETWORK_SEARCH_SHOW = "network_search_show"

// 全网搜索人均频道时长
const val NETWORK_SEARCH_SHOW_TIME = "network_search_show_time"

// 用户主动点击新手引导次数
const val NETWORK_SEARCH_GUIDE_CLICK = "network_search_guide_click"

// 未检测到资源状态--悬浮球点击次数
const val NETWORK_SEARCH_NO_RESOURCE_FLOAT_CLICK = "network_search_no_resource_float_click"

// 检测到资源状态--悬浮球点击次数
const val NETWORK_SEARCH_RESOURCE_FLOAT_CLICK = "network_search_resource_float_click"

// 检测到资源状态的次数
const val NETWORK_SEARCH_RESOURCE_DETECTED = "network_search_resource_detected"

// 资源选择页面--点击保存
const val NETWORK_SEARCH_SAVE_CLICK = "network_search_save_click"

// 任务成功进入传输列表的个数
const val NETWORK_SEARCH_SAVE_TASK_NUMBER = "network_search_save_task_number"

// 全网搜索入口资源圈展现
const val SHARE_RESOURCE_NETWORK_SEARCH_SHOW: String = "share_resource_network_search_show"

// 全网搜索入口资源圈点击
const val SHARE_RESOURCE_NETWORK_SEARCH_CLICK: String = "share_resource_network_search_click"

// 首页搜索icon点击
const val SEARCH_ENTRANCE_CLICK_HOME_ICON: String = "search_entrance_click_home_icon"

// 文件tab 假搜索框点击
const val SEARCH_ENTRANCE_CLICK_FILELIST: String = "search_entrance_click_filelist"

// 谷歌全网搜索条点击
const val NETWORK_SEARCH_ENTRANCE_CLICK: String = "network_search_entrance_click"
const val NETWORK_SEARCH_ENTRANCE_TYPE_HOME: String = "1"
const val NETWORK_SEARCH_ENTRANCE_TYPE_FILELIST: String = "2"

// 全网搜索成功率: other0: url.host other0:嗅探到的数量
const val NETWORK_SEARCH_DETECT_SUCCESS_RATE: String = "network_search_detect_success_rate"

// 上报GAID
const val GAID_KEY = "gaid_key"

/**
 * 静默 Push
 */
// 静默 Push 接收 uv/pv
const val RECEIVE_SILENT_PUSH = "receive_silent_push"

// 静默 Push 成功调起自动备份
const val SILENT_PUSH_WEAK_UP_BACKUP = "silent_push_weak_up_backup"

// 上传面板点击视频
const val CLICK_UPLOAD_DIALOG_VIDEO = "click_upload_dialog_video"

// 上传面板点击图片
const val CLICK_UPLOAD_DIALOG_IMAGE = "click_upload_dialog_image"

// 上传面板点击文档
const val CLICK_UPLOAD_DIALOG_DOC = "click_upload_dialog_doc"

// 点击添加上传任务的vp
const val CLICK_ADD_UPLOAD_TASK_PV = "click_add_upload_task_pv"

// 上传页面—点击『全选』按钮行为
const val CLICK_UPLOAD_MEDIA_SELECT_ALL = "click_upload_media_select_all"

// 上传页面—点击『上传』按钮行为
const val CLICK_UPLOAD_MEDIA_SELECT_UPLOAD = "click_upload_media_select_upload"

// 全局消息展现
const val GLOBAL_MSG_LIST_SHOW = "global_msg_list_show"

// 资源圈消息展现 resource_msg_list_show
const val RESOURCE_MSG_LIST_SHOW = "resource_msg_list_show"

// 脱机使用tab每天展现uv&pv
const val OFFLINE_TAB_SHOW = "offline_tab_show"

// 脱机使用tab出现过文件展现uv&pv
const val OFFLINE_TAB_LIST = "offline_tab_list"

// 每天在脱机使用tab中调起视频播放器次数
const val OFFLINE_VIDEO_CLICK = "offline_video_click"

// 每天在脱机使用tab中调起图片预览次数
const val OFFLINE_IMG_CLICK = "offline_img_click"

// 每天在脱机使用tab中调起音频播放器次数
const val OFFLINE_MUSIC_CLICK = "offline_music_click"

// 文件搜索搜索框按钮点击事件uv/pv
const val SEARCH_BUTTON_ACTION_CLICK = "search_button_action_click"

// 文件搜索搜索框按钮点击事件uv/pv
const val SEARCH_BUTTON_ACTION_FILE_CLICK = "search_button_action_file_click"

// 【链接任务】页面展示
const val OFFLINE_UPLOAD_LINK_TAB_PV = "offline_upload_link_tab_pv"

// 【BT任务】页面展示
const val OFFLINE_UPLOAD_BT_TAB_PV = "offline_upload_bt_tab_pv"

// 【链接任务】点击解析并保存
const val CLICK_OFFLINE_UPLOAD_LINK_SAVE = "click_offline_upload_link_save"

// 【新建BT任务】点击上传
const val CLICK_OFFLINE_UPLOAD_BT_UPLOAD = "click_offline_upload_bt_upload"

// 从链接任务下载成功，点击预览UV&PV
const val CLICK_OFFLINE_UPLOAD_TAB_PREVIEW_LINK = "click_offline_upload_tab_preview_link"

// 从BT任务下载成功，点击预览UV&PV
const val CLICK_OFFLINE_UPLOAD_TAB_PREVIEW_BT = "click_offline_upload_tab_preview_bt"

// 选择BT文件后点击【保存】按钮UV&PV
const val CLICK_OFFLINE_UPLOAD_BT_FILE_SAVE = "click_offline_upload_bt_file_save"

// 资源圈资源的展现PV
const val SHARE_RESOURCE_ITEM_PV = "share_resource_item_pv"

// 引导页展现
const val USER_GUIDE_PAGE_SHOW = "user_guide_page_show"

// 引导页 - 按钮点击 - 视频播放
const val USER_GUIDE_PAGE_VIDEO_PLAY = "user_guide_page_video_play"

// 引导页 - 按钮点击 - 照片存储
const val USER_GUIDE_PAGE_PHOTO_CLICK = "user_guide_page_photo_click"

// 引导页 - 按钮点击 - 文件分享
const val USER_GUIDE_PAGE_SHARE_CLICK = "user_guide_page_share_click"

// 引导页 - 按钮点击 - 多终端
const val USER_GUIDE_PAGE_MULTI_TERMINAL_CLICK = "user_guide_page_multi_terminal_click"

// 引导页 - 按钮点击 - 远程上传
const val USER_GUIDE_PAGE_REMOTE_UPLOAD_CLICK = "user_guide_page_remote_upload_click"

// 引导页 - 按钮点击 - 跳过
const val USER_GUIDE_PAGE_SKIP_CLICK = "user_guide_page_skip_click"

// 远程上传按钮展现
const val USER_GUIDE_PAGE_REMOTE_UPLOAD_SHOW = "user_guide_page_remote_upload_show"

// 照片存储页-点击自动备份按钮
const val USER_GUIDE_BACKUP_CLICK = "user_guide_backup_click"

// 照片存储页-点击手动上传按钮点击
const val USER_GUIDE_UPLOAD_CLICK = "user_guide_upload_click"

// 文件分享页·点击上传以分享按钮点击
const val USER_GUIDE_UPLOAD_TO_SHARE_CLICK = "user_guide_upload_to_share_click"

// 多终端查看页·上传按钮点击
const val USER_GUIDE_MULTI_TERMINAL_CLICK = "user_guide_multi_terminal_click"

// 引导蒙层展现
const val NEW_USER_HOME_GUIDE_SHOW = "new_user_home_guide_show"

// 按钮点击 - 开始体验
const val NEW_USER_HOME_GUIDE_EXPERIENCE_CLICK = "new_user_home_guide_experience_click"

// 账号绑定-弹窗展现
const val ACCOUNT_BIND_ALERT_SHOW = "account_bind_alert_show"

// 主页头像点击
const val ENTER_USER_CENTER_BY_AVATOR_ACTION = "enter_user_center_by_avator_action"

// 登录注册错误码检测
const val LOGIN_REGISTER_ERRNO_CHECK = "login_register_errno_check"

// 空文件时文件tab的展现
const val FILE_TAB_EMPTY_GUIDE_SHOW = "file_tab_empty_guide_show"

// 自动备份照片按钮的点击
const val FILE_TAB_EMPTY_GUIDE_BACKUP_ACTION = "file_tab_empty_guide_backup_action"

// 上传照片按钮的点击
const val FILE_TAB_EMPTY_GUIDE_PHOTO_ACTION = "file_tab_empty_guide_photo_action"

// 上传视频按钮的点击
const val FILE_TAB_EMPTY_GUIDE_VIDEO_ACTION = "file_tab_empty_guide_video_action"

// 上传文件按钮的点击
const val FILE_TAB_EMPTY_GUIDE_FILE_ACTION = "file_tab_empty_guide_file_action"

// 新建文件夹按钮的点击
const val FILE_TAB_EMPTY_GUIDE_CREATE_DIR_ACTION = "file_tab_empty_guide_create_dir_action"

// 空文件时相册tab的展现
const val PHOTO_TAB_EMPTY_GUIDE_SHOW = "photo_tab_empty_guide_show"

// 选择图片上传按钮的点击
const val PHOTO_TAB_EMPTY_GUIDE_UPLOAD_ACTION = "photo_tab_empty_guide_upload_action"

// 后台备份完成系统 Push 展现
const val BACKGROUND_BACKUP_FINISH_PUSH_SHOW = "background_backup_finish_push_show"

// 后台备份完成，Push 点击
const val BACKGROUND_BACKUP_FINISH_PUSH_CLICK = "background_backup_finish_push_click"

// 试看 toast 展示 uv/pv
const val TRY_WATCH_GUIDE_TOAST_SHOW = "try_watch_guide_toast_show"

// 试看 toast 点击 uv/pv
const val TRY_WATCH_GUIDE_TOAST_CLICK = "try_watch_guide_toast_click"

// 试看遮罩展示 uv/pv
const val TRY_MASK_VIDEO_SHOW = "try_mask_video_show"

// 试看遮罩点击 uv/pv
const val TRY_MASK_VIDEO_CLICK = "try_mask_video_click"

// 点击重新试看按钮
const val REWATCH_WITH_MASK_CLICK = "rewatch_with_mask_click"

// 转存成功
const val RESOURCE_SAVE_FILE_SUCCEEDED = "resource_save_file_succeeded"

// 后台备份完成次数
const val BACKGROUND_BACKUP_TIMES = "background_backup_times"

// 传输飘条的展现
const val UPLOAD_ADD_TASK_TOAST_PV = "upload_add_task_toast_pv"

// 传输飘条的点击
const val CLICK_UPLOAD_ADD_TASK_TOAS = "click_upload_add_task_toas"

// 上传完成飘条的展现
const val UPLOAD_COMPLETE_TOAST_PV = "upload_complete_toast_pv"

// 资源圈热词换一批按钮点击
const val SHARE_RESOURCE_HOTWORDS_SWAP_CLICK: String = "share_resource_hotwords_swap_click"

// DuboxService onCreate方法执行时间
const val DUBOX_SERVICE_ONCREATE_TIME = "dubox_service_oncreate_time"

// 缩略图加载错误 getResourceUrl is empty
const val THUMB_ERROR_RESOURCE_URL_NULL: String = "thumb_error_resource_url_null"

// 缩略图加载错误 getResourceUrl is empty local 文件
const val THUMB_ERROR_RESOURCE_URL_NULL_LOCAL: String = "thumb_error_resource_url_null_local"

// 缩略图加载错误 glide加载失败
const val THUMB_ERROR_GLIDE_FAILED: String = "thumb_error_glide_failed"

/**
 * 点击福利中心
 */
const val CLICK_WELFARE_CENTER = "click_welfare_center"

/**
 * 点击空间管理
 */
const val CLICK_STORAGE_MANAGER = "click_storage_manager"

/**
 * 进入分享搜索页
 */
const val ENTER_SHARE_SEARCH_PAGE = "enter_share_search_page"

/**
 * 分享tab右上角点击我的分享
 */
const val SHARE_TAB_MY_SHARE_CLICK = "share_tab_my_share_click"

/**
 * 分享选择页面pv-uv
 */
const val SHARE_FILE_VIEW_SHOW = "share_file_view_show"

/**
 * 分享按钮点击
 */
const val SHARE_FILE_SHARE_ACTION = "share_file_Share_action"

/**
 * 分享到facebook
 */
const val SHARE_TAB_SHARE_TO_FACEBOOK = "share_tab_share_to_facebook"

/**
 * 分享到whatsapp
 */
const val SHARE_TAB_SHARE_TO_WHATSAPP = "share_tab_share_to_whatsapp"

/**
 * 分享到messenger
 */
const val SHARE_TAB_SHARE_TO_MESSENGER = "share_tab_share_to_messenger"

/**
 * 分享到telegram
 */
const val SHARE_TAB_SHARE_TO_TELEGRAM = "share_tab_share_to_telegram"

/**
 * 分享到system
 */
const val SHARE_TAB_SHARE_TO_SYSTEM = "share_tab_share_to_system"

/**
 * 分享到email
 */
const val SHARE_TAB_SHARE_TO_EMAIL = "share_tab_share_to_email"

/**
 * 分享到copy_link
 */
const val SHARE_TAB_SHARE_TO_COPY_LINK = "share_tab_share_to_copy_link"

/**
 * 分享tab设置分享
 */
const val SHARE_TAB_SHARE_SETTING = "share_tab_share_setting"

/**
 * 分享结果页展现
 */
const val SHARE_TAB_RESULT_PAGE_SHOW = "share_tab_result_page_show"

/**
 * 文件分享结果页面—点击『回到首页』uv/pv
 */
const val SHARE_TAB_RESULT_GO_HOME_CLICK = "share_tab_result_go_home_click"

/**
 * 文件分享结果页面—点击『继续分享』uv/pv
 */
const val SHARE_TAB_RESULT_GO_ON_CLICK = "share_tab_result_go_on_click"

/**
 * 文件分享结果页面—点击『关闭』uv/pv
 */
const val SHARE_TAB_RESULT_CLOSE_CLICK = "share_tab_result_close_click"

/**
 * 首页奖励可领取飘条的展现
 */
const val HOME_CARD_PROMPT_BAR_SHOW = "home_card_prompt_bar_show"

/**
 * 首页奖励可领取飘条的点击
 */
const val HOME_CARD_PROMPT_BAR_CLICK = "home_card_prompt_bar_click"

/**
 * 首充优惠券飘条展现
 */
const val HOME_COUPON_CARD_FIRST_CHARGE_SHOW: String = "home_coupon_card_first_charge_show"

/**
 * 首充优惠券飘条的点击
 */
const val HOME_COUPON_CARD_FIRST_CHARGE_CLICK: String = "home_coupon_card_first_charge_click"

/**
 * 提示弹窗展现uv&pv
 */
const val DISCOUNT_ALERT_SHOW = "discount_alert_show"

/**
 * 提示弹窗点击uv&pv
 */
const val DISCOUNT_ALERT_BUY_CLICK = "discount_alert_buy_click"

/**
 * 首页—头部常驻会员icon展示
 */
const val HOME_CARD_DISCOUNT_ICON_SHOW = "home_card_discount_icon_show"

/**
 * 首页—头部常驻会员icon点击
 */
const val HOME_CARD_DISCOUNT_ICON_CLICK = "home_card_discount_icon_click"

/**
 * 裂变活动带来新增用户，这个只算登录成功之后
 */
const val FISSION_NEW_USER = "fission_new_user"

/**
 * 夜间模式开启
 */
const val NIGHT_MODE_OPEN = "night_mode_open"

/**
 * 夜间模式关闭
 */
const val NIGHT_MODE_CLOSE = "night_mode_close"

/**
 * 高速下载状态提示
 */
const val SHOW_DOWNLOAD_SPEED_UP_VIEW = "show_download_speed_up_view"

/**
 * 视频倍速会员状态toast提示展现
 */
const val SHOW_VIDEO_SPEED_SWITCH_SUCCESS_TOAST = "show_video_speed_switch_success_toast"

/**
 * 视频清晰度会员状态toast提示展现
 */
const val SHOW_VIDEO_RESOLUTION_SWITCH_SUCCESS_TOAST = "show_video_resolution_switch_success_toast"

/**
 * 用户前台活跃时长
 */
const val APP_ACTION_TIME_FOREGROUND = "app_action_time_foreground"

/**
 * 用户后台存活时长
 */
const val APP_ACTION_TIME_BACKGROUND = "app_action_time_background"

/**
 * 上传面板切换文件夹按钮点击
 */
const val UPLOAD_PAGE_CLICK_CHANGE_FOLD = "upload_page_click_change_fold"

/**
 * 上传面板显示 - 图片
 */
const val SELECT_MEDIA_UPLOAD_IMAGE_SHOW = "select_media_upload_image_show"

/**
 * 上传面板显示 - 视频
 */
const val SELECT_MEDIA_UPLOAD_VIDEO_SHOW = "select_media_upload_video_show"

/**
 * 清理成功页面自动备份展现
 */
const val CLEAN_RESULT_PAGE_VIDEO_BACKUP_SHOW = "clean_result_page_video_backup_show"

/**
 * 清理成功页面自动备份点击
 */
const val CLEAN_RESULT_PAGE_VIDEO_BACKUP_CLICK = "clean_result_page_video_backup_click"

/**
 * 清理成功页面免广告卡片展现
 */
const val CLEAN_RESULT_PAGE_AD_CARD_SHOW = "clean_result_page_ad_card_show"

/**
 * 清理成功页面免广告卡片点击
 */
const val CLEAN_RESULT_PAGE_AD_CARD_CLICK = "clean_result_page_ad_card_click"

/**
 * 打开资源圈首页出现异常
 */
const val OPEN_SHARE_RESOURCES_MAIN_ERROR: String = "open_share_resources_main_error"

/**
 * 打开资源圈Feed出现异常
 */
const val OPEN_SHARE_RESOURCES_FEED_ERROR: String = "open_share_resources_feed_error"

/**
 * 设置contentprovider可用，出现异常
 */
const val ENABLE_SHARE_RESOURCES_PROVIDER_ERROR: String = "ENABLE_SHARE_RESOURCES_PROVIDER_ERROR"

/**
 * 获取资源圈数据异常
 */
const val SHARE_RESOURCES_FETCH_DATA_ERROR: String = "share_resources_fetch_data_error"

/**
 * 创建首页资源圈卡片异常
 */
const val CREATE_SHARE_RESOURCES_HOME_CARD_ERROR: String = "create_share_resources_home_card_error"

/**
 * 打开资源圈反馈页面异常
 */
const val OPEN_SHARE_RESOURCS_FEED_BACK_ERROR: String = "open_share_resourcs_feed_back_error"

/**
 * 打开资源圈详情页面异常
 */
const val OPEN_SHARE_RESOURCES_DETAIL_ERROR: String = "open_share_resources_detail_error"

/**
 * 资源圈数据预加载异常
 */
const val PREFETCH_SHARE_RESOURCES_DATA_ERROR: String = "prefetch_share_resources_data_error"

/**
 * 非官方渠道包，资源圈资源索引问题
 */
const val SHARE_RESOURCE_CATEGORIES_LAYOUT_RES_ERROR: String = "share_resource_categories_layout_res_error"

// 文档模块动态下发触发次数
const val DYNAMIC_FEATURE_MODULE_DOCUMENT_DOWNLOAD = "dynamic_feature_module_document_download"

// 文档模块动态下发成功次数
const val DYNAMIC_FEATURE_MODULE_DOCUMENT_DOWNLOAD_SUCCESS = "dynamic_feature_module_document_download_success"

// 文档模块动态下发失败次数
const val DYNAMIC_FEATURE_MODULE_DOCUMENT_DOWNLOAD_FAILED = "dynamic_feature_module_document_download_failed"

/**
 * 文档模块加载异常
 */
const val DYNAMIC_FEATURE_MODULE_DOCUMENT_RCONTEXT_ERROR: String = "dynamic_feature_module_document_rcontext_error"

// 清理模块动态下发触发次数
const val DYNAMIC_FEATURE_MODULE_CLEANER_DOWNLOAD = "dynamic_feature_module_cleaner_download"

// 清理模块动态下发成功次数
const val DYNAMIC_FEATURE_MODULE_CLEANER_DOWNLOAD_SUCCESS = "dynamic_feature_module_cleaner_download_success"

// 清理模块动态下发失败次数
const val DYNAMIC_FEATURE_MODULE_CLEANER_DOWNLOAD_FAILED = "dynamic_feature_module_cleaner_download_failed"

/**
 * 清理模块加载异常
 */
const val DYNAMIC_FEATURE_MODULE_CLEANER_RCONTEXT_ERROR: String = "dynamic_feature_module_cleaner_rcontext_error"

/**
 * 全网搜索二期-文件搜索页面展现
 */
const val SEARCH_PAGE_WEB_SEARCH_ENTRY_SHOW: String = "search_page_web_search_entry_show"

/**
 * 全网搜索二期- 文件搜索页面点击 tiktok、ins、Facebook、Google
 */
const val SEARCH_PAGE_WEB_SEARCH_ENTRY_CLICK: String = "search_page_web_search_entry_click"

/**
 * 全网搜索二期-资源圈搜索页面展现
 */
const val SHARE_RESOURCE_WEB_SEARCH_ENTRY_SHOW: String = "share_resource_web_search_entry_show"

/**
 * 全网搜索二期- 资源圈搜索页面点击 tiktok、ins、Facebook、Google
 */
const val SHARE_RESOURCE_WEB_SEARCH_ENTRY_CLICK: String = "share_resource_web_search_entry_click"

/**
 * 全网搜索二期-视频下载器页面展现
 */
const val DOWNLOADER_WEB_SEARCH_ENTRY_SHOW: String = "downloader_web_search_entry_show"

/**
 * 全网搜索二期-视频下载器页面点击 tiktok、ins、Facebook、Google
 */
const val DOWNLOADER_WEB_SEARCH_ENTRY_CLICK: String = "downloader_web_search_entry_click"


/**
 * 首页传输列表入口点击
 */
const val HOME_CARD_TRANS_BTN_CLICK: String = "home_card_trans_btn_click"

/**
 * 转存文件权益弹窗展示
 */
const val VIP_PREMIUM_SAVE_FILE_DIALOG_SHOW: String = "vip_premium_save_file_dialog_show"

/**
 * 转存文件权益弹窗点击购买
 */
const val VIP_PREMIUM_SAVE_FILE_PURCHASE: String = "vip_premium_save_file_purchase"

/**
 * 订阅引导页PV
 */
const val VIP_SUB_GUIDE_PV: String = "vip_sub_guide_pv"

/**
 * 展示默认七天免费使用文案
 */
const val VIP_SUB_GUIDE_DEFALUT_TRAIL: String = "vip_sub_guide_defalut_trail"

/**
 * 展示默认成为会员文案
 */
const val VIP_SUB_GUIDE_DEFALUT_UPGRADE_PREMIUM: String = "vip_sub_guide_defalut_upgrade_premium"

/**
 * 展示默认七天免费使用文案
 */
const val VIP_SUB_GUIDE_TRAIL: String = "vip_sub_guide_trail"

/**
 * 展示默认成为会员文案
 */
const val VIP_SUB_GUIDE_UPGRADE_PREMIUM: String = "vip_sub_guide_upgrade_premium"

/**
 * 购买时用的默认商品
 */
const val VIP_SUB_BUY_DUFAULT_PRODUCT: String = "vip_sub_buy_dufault_product"

/**
 * 购买时商品
 */
const val VIP_SUB_BUY_PRODUCT: String = "vip_sub_buy_product"

/**
 * 订阅引导页跳过点击
 */
const val VIP_SUB_GUIDE_SKIP_CLICK: String = "vip_sub_guide_skip_click"

/**
 * 订阅引导页试用购买点击
 */
const val VIP_SUB_GUIDE_BUY_CLICK: String = "vip_sub_guide_buy_click"

/**
 * 订阅引导页用户协议点击
 */
const val VIP_SUB_GUIDE_USER_AGREEMENT_CLICK: String = "vip_sub_guide_user_agreement_click"

/**
 * 订阅引导页自动续费协议点击
 */
const val VIP_SUB_GUIDE_AUTOMATIC_AGREEMENT_CLICK: String = "vip_sub_guide_automatic_agreement_click"

/**
 * 购买引导支付成功
 */
const val VIP_BUY_GUIDE_DIALOG_PAY_SUCCESS: String = "vip_buy_guide_dialog_pay_success"

/**
 * 购买引导支付失败
 */
const val VIP_PREMIUM_PURCHASE_RESULT_SAFE_BOX: String = "vip_premium_purchase_result_safe_box"

const val VIP_BUY_GUIDE_DIALOG_PAY_FAILED: String = "vip_buy_guide_dialog_pay_failed"

/**
 * 视频tab自动备份卡片展现
 */
const val TAB_VIDEO_BACKUP_VIEW_SHOW: String = "tab_video_backup_view_show"

/**
 * 视频tab自动备份卡片点击开启备份
 */
const val TAB_VIDEO_BACKUP_VIEW_CLICK_OPEN: String = "tab_video_backup_view_click_open"

/**
 * 视频tab自动备份卡片点击关闭
 */
const val TAB_VIDEO_BACKUP_VIEW_CLICK_CLOSE: String = "tab_video_backup_view_click_close"

/**
 * 视频自动备份引导弹窗
 */
const val TAB_VIDEO_NON_VIP_VIDEO_BACKUP_ALERT_VIEW: String = "tab_video_non_vip_video_backup_alert_view"

/**
 * 视频自动备份点击进入会员中心
 */
const val TAB_VIDEO_NON_VIP_VIDEO_BACKUP_ALERT_ACTION: String = "tab_video_non_vip_video_backup_alert_action"

/**
 * 订阅引导页用户协议点击
 */
const val VIP_PREMIUM_USER_AGREEMENT_CLICK: String = "vip_premium_user_agreement_click"

/**
 * 订阅引导页自动续费协议点击
 */
const val VIP_PREMIUM_AUTOMATIC_AGREEMENT_CLICK: String = "vip_premium_automatic_agreement_click"

/**
 * 针对 fcm_token 获取失败的处理
 */
const val FIREBASE_REGISTER_FAILED_REASON: String = "firebase_register_failed_reason"

/**
 * 常驻通知栏展现
 */
const val KEEP_ACTIVE_NOTIFICATION_SHOW: String = "keep_active_notification_show"

/**
 * 点击常驻通知栏进入首页
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME: String = "keep_active_notification_launcher_home"

/**
 * 常驻通知栏上传点击
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_UPLOAD: String =
    "keep_active_notification_launcher_home_from_upload"

/**
 * 常驻通知栏下载点击
 */
const val KEEP_ACTIVE_NOTIFICATION_LAUNCHER_HOME_FROM_DOWNLOAD: String =
    "keep_active_notification_launcher_home_from_download"

/**
 * 常驻通知栏资源小组点击
 */
const val KEEP_ACTIVE_NOTIFICATION_LAUNCHER_HOME_FROM_HIVE: String = "keep_active_notification_launcher_home_from_hive"

/**
 * 常驻通知栏分享点击
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_SHARE: String = "keep_active_notification_launcher_home_from_share"

/**
 * 常驻通知栏earn点击
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_EARN: String = "keep_active_notification_launcher_home_from_earn"

/**
 * 常驻通知栏搜索点击
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_SEARCH: String =
    "keep_active_notification_launcher_home_from_search"

/**
 * 常驻通知栏关闭按钮点击
 */
const val KEEP_ACTIVE_NOTICATION_CLOSE: String = "keep_active_notification_close"


/**
 * 全局接口的pvlost监控错误url
 */
const val MONITOR_API_RESPONSE_PV_LOST_ERROR_URL = "monitor_api_response_pv_lost_error_url"

/**
 * 音频播放器的展现
 */
const val AUDIO_PLAYER_SHOW: String = "audio_player_show"

/**
 * 音频页点击分享
 */
const val AUDIO_PAGE_CLICK_SHARE: String = "audio_page_click_share"

/**
 * 音频页点击下载
 */
const val AUDIO_PAGE_CLICK_DOWNLOAD: String = "audio_page_click_download"

/**
 * 音频页点击下载
 */
const val AUDIO_PAGE_CLICK_15_REMOVE: String = "audio_page_click_15_remove"

/**
 * 音频倍速会员引导弹窗展现
 */
const val AUDIO_SPEED_VIP_GUIDE_SHOW: String = "audio_speed_vip_guide_show"

/**
 * 音频倍速会员引导弹窗并点击
 */
const val AUDIO_SPEED_VIP_GUIDE_CLICK: String = "audio_speed_vip_guide_click"

/**
 * 倍速播放按钮点击
 */
const val AUDIO_CHANGE_SPEED_CLICK = "audio_change_speed_click"

/**
 * 个人中心展现
 */
const val USER_CENTER_PAGE_SHOW: String = "user_center_page_show"

/**
 * 用户反馈页面展现
 */
const val FEEDBACK_HELP_CENTER_PAGE_SHOW: String = "feedback_help_center_page_show"

/**
 * 帮助中心点击
 */
const val HELP_AND_FEEDBACK_ENTRANCE_ACTION: String = "help_and_feedback_entrance_action"

/**
 * 登陆页面右上角反馈按钮点击
 */
const val LOGIN_AND_SIGN_PAGE_FEEDBACK_ENTRANCE_ACTION: String = "login_and_sign_page_feedback_entrance_action"

/**
 * 首页宽限期展示
 */
const val HOME_GRACE_CARD_SHOW: String = "home_grace_card_show"

/**
 * 首页宽限期展示
 */
const val HOME_GRACE_CARD_CONFIRM_CLICK: String = "home_grace_card_confirm_click"

/**
 * 首页链接识别飘条 - 飘条展现
 */
const val HOME_INTERCEPT_PASTEBOARD_VIEW_SHOW: String = "home_intercept_pasteboard_view_show"

/**
 * 首页链接识别飘条 - 飘条点击
 */
const val HOME_INTERCEPT_PASTEBOARD_VIEW_CLICK: String = "home_intercept_pasteboard_view_click"

/**
 * 视频链接解析页面-展现
 */
const val SNIFF_VIDEO_SAVE_SETTING_PAGE_SHOW: String = "sniff_video_save_setting_page_show"

/**
 * 视频链接解析页面-点击上传
 */
const val SNIFF_VIDEO_SAVE_SETTING_UPLOAD_CLICK: String = "sniff_video_save_setting_upload_click"

/**
 * 第三方app分享调起端
 */
const val THIRD_APP_SHARE_TO_GLOBAL_SEARCH: String = "third_app_share_to_global_search"

/**
 * 进入全网搜索并主动调起嗅探浮层
 */
const val GOOGLE_SEARCH_SNIFF_RESOURCE_AUTO_SHOW: String = "google_search_sniff_resource_auto_show"

/**
 * 点击音频倍速播放
 */
const val CLICK_AUDIO_SPEED_PV: String = "click_audio_speed_pv"

/**
 * 点击音频倍速播放底部会员权益引导
 */
const val CLICK_AUDIO_SPEED_BOTTOM_VIP_GUIDE: String = "click_audio_speed_bottom_vip_guide"

/**
 * 拍照失败
 */
const val CAMERA_FAILED_CODE_STATIS: String = "camera_failed_code_statis"

// 上传面板点击拍照上传
const val CLICK_UPLOAD_DIALOG_TAKE_PHOTO = "click_upload_dialog_take_photo"

// 拍照预览页PV
const val CAMERA_PREVIEW_ACTIVITY_PV = "camera_preview_activity_pv"

// 拍照上传保存页PV
const val TAKE_PHOTO_UPLOAD_SAVE_ACTIVITY_PV = "take_photo_upload_save_activity_pv"

// 拍照上传成功
const val TAKE_PHOTO_UPLAOD_SUCCESS_PV = "take_photo_uplaod_success_pv"

// 文件夹备份-入口点击
const val BACKUP_FOLDER_ENTRY_CLICK = "backup_folder_entry_click"

// 文件夹备份
const val BACKUP_FOLDER_PAGE_SHOW = "backup_folder_page_show"

// 文件夹备份-vip引导展示
const val BACKUP_FOLDER_VIP_PAGE_SHOW = "backup_folder_vip_page_show"

// 文件夹备份-vip购买点击
const val BACKUP_FOLDER_VIP_BUY_CLICK = "backup_folder_vip_buy_click"

/**
 * 照片编辑-vip引导展示
 */
const val PICTURE_EDIT_VIP_PAGE_SHOW = "picture_edit_vip_page_show"

/**
 * 照片编辑-vip购买点击
 */
const val PICTURE_EDIT_VIP_BUY_CLICK = "picture_edit_vip_buy_click"

/**
 * 照片编辑页 - 保存点击
 */
const val EDIT_PICTURE_SAVE_CLICKED = "edit_picture_save_btn_click"

/**
 * 照片编辑页 - 功能应用点击
 */
const val EDIT_PICTURE_ACCEPT_CLICKED = "edit_picture_accept_%s_btn_click"

/**
 * 播放模式切换
 */
const val AUDIO_CHANGE_PLAY_MODE: String = "audio_change_play_mode"

/**
 * 播放列表点击
 */
const val AUDIO_SOURCE_LIST_CLICK: String = "audio_source_list_click"

/**
 * 播放文件名区域点击
 */
const val AUDIO_CLOUD_FILE_CLICK: String = "audio_cloud_file_click"

/**
 * 播放文件名区域点击
 */
const val AUDIO_NEXT_OR_PRELOAD_CLICK: String = "audio_next_or_preload_click"

/**
 * 悬浮球展示
 */
const val AUDIO_CIRCLE_VIEW_SHOW: String = "audio_circle_view_show"

/**
 * 悬浮球暂停或播放点击
 */
const val AUDIO_CIRCLE_VIEW_PAUSE_OR_START: String = "audio_circle_view_pause_or_start"

/**
 * 悬浮球关闭点击
 */
const val AUDIO_CIRCLE_VIEW_CLOSE_CLICK: String = "audio_circle_view_close_click"

/**
 * 因策略屏蔽的push数量
 */
const val PUSH_NOTIFICATION_DISCARD: String = "push_notification_discard"

/**
 * 点击视频tab的item
 */
const val CLICK_VIDEO_SERVICE_TAB_ITEM: String = "click_video_service_tab_item"

/**
 * 首页顶部会员 icon 广告礼盒展现
 */
const val HOME_VIP_ICON_AD_GIFT_BOX_SHOW: String = "home_vip_icon_ad_gift_box_show"

/**
 * 首页顶部会员 icon 广告礼盒点击
 */
const val HOME_VIP_ICON_AD_GIFT_BOX_CLICK: String = "home_vip_icon_ad_gift_box_click"

/**
 * 首页顶部会员 icon 广告展现
 */
const val HOME_VIP_ICON_AD_SHOW: String = "home_vip_icon_ad_show"

/**
 * 首页会员 icon 广告关闭后的弹窗广告展现
 */
const val HOME_DIALOG_AD_AFTER_GIFT_BOX_SHOW: String = "home_dialog_ad_after_gift_box_show"

/**
 * @see com.dubox.drive.safebox.operate.MoveOperate#moveFromDB
 * 错误统计
 */
const val MOVE_OPERATIONG_ERROR_UNREGISTERRECEIVER: String = "move_operationg_error_unregisterreceiver"

/**
 * 上传Toast广告展现
 */
const val UPLOAD_TOAST_AD_SHOW: String = "upload_toast_ad_show"

/**
 * 自动备份Toast广告展现
 */
const val BACKUP_TOAST_AD_SHOW: String = "backup_toast_ad_show"

/**
 * 退出app广告展现
 */
const val SHOW_AD_APP_EXIT: String = "show_ad_app_exit"

/**
 * 退出视频广告展现
 */
const val SHOW_AD_VIDEO_EXIT: String = "show_ad_video_exit"

/**
 * 退出清理完成页展现
 */
const val SHOW_CLEANUP_RESULT_EXIT: String = "show_cleanup_result_exit"

/**
 * tab切换广告展现
 */
const val SHOW_AD_TAB_SWITCH: String = "show_ad_tab_switch"

/**
 * tab切换广告展现
 */
const val CLICK_EXIT_APP_DIALOG_COPNFIRM: String = "click_exit_app_dialog_copnfirm"

/**
 * 首页点击返回按钮的pv
 */
const val CLICK_MAIN_PAGER_BACK_BUTTON: String = "click_main_pager_back_button"

/**
 * 视频退出广告展现
 */
const val CLICK_EXIT_VIDEO_DIALOG_CONFIRM: String = "click_exit_video_dialog_confirm"

/**
 * 清理完成页退出广告展现
 */
const val CLICK_EXIT_CLEANUP_RESULT_CONFIRM: String = "click_exit_cleanup_result_confirm"

/**
 * 上传列表广告应该展现
 */
const val SHOULD_SHOW_AD_TRANSFER_LIST_UPLOAD: String = "translist_banner_ad_should_show_upload"

/**
 * 下载列表广告应该展现
 */
const val SHOULD_SHOW_AD_TRANSFER_LIST_DOWNLOAD: String = "translist_banner_ad_should_show_download"


/**
 * 添加下载任务 toast 广告
 */
const val SHOW_DOWNLOAD_TOAST_AD: String = "show_download_toast_ad"

/**
 * 点击会员中心购买记录
 */
const val CLICK_VIP_WEB_ACTIVITY_RECORD: String = "click_vip_web_activity_record"

/**
 * 传输列表优化 -- 下载列表 -- 传输失败页面悬浮条展现
 */
const val DOWN_TRANS_FAIL_FLOATING_BAR_SHOW: String = "down_trans_fail_floating_bar_show"

/**
 * 传输列表优化 -- 下载列表 -- 传输失败页面悬浮条点击
 */
const val DOWN_TRANS_FAIL_FLOATING_BAR_CLICK: String = "down_trans_fail_floating_bar_click"

/**
 * 传输列表优化 -- 下载列表 -- 失败二级页点击「删除记录」
 */
const val DOWN_TRANS_FAIL_LIST_DELETE_CLICK: String = "down_trans_fail_list_delete_click"

/**
 * 传输列表优化 -- 下载列表 -- 失败二级页点击「重试」
 */
const val DOWN_TRANS_FAIL_LIST_RETRY_CLICK: String = "down_trans_fail_list_retry_click"

/**
 * 传输列表优化 -- 上传列表 -- 传输失败页面悬浮条展现
 */
const val UPLOAD_TRANS_FAIL_FLOATING_BAR_SHOW: String = "upload_trans_fail_floating_bar_show"

/**
 * 传输列表优化 -- 上传列表 -- 传输失败页面悬浮条点击
 */
const val UPLOAD_TRANS_FAIL_FLOATING_BAR_CLICK: String = "upload_trans_fail_floating_bar_click"

/**
 * 传输列表优化 -- 上传列表 -- 失败二级页点击「删除记录」
 */
const val UPLOAD_TRANS_FAIL_LIST_DELETE_CLICK: String = "upload_trans_fail_list_delete_click"

/**
 * 传输列表优化 -- 上传列表 -- 失败二级页点击「重试」
 */
const val UPLOAD_TRANS_FAIL_LIST_RETRY_CLICK: String = "upload_trans_fail_list_retry_click"

/**
 * 传输列表优化 -- 远程上传列表 -- 传输失败页面悬浮条展现
 */
const val REMOTE_UPLOAD_TRANS_FAIL_FLOATING_BAR_SHOW: String = "remote_upload_trans_fail_floating_bar_show"

/**
 * 传输列表优化 -- 远程上传列表 -- 传输失败页面悬浮条点击
 */
const val REMOTE_UPLOAD_TRANS_FAIL_FLOATING_BAR_CLICK: String = "remote_upload_trans_fail_floating_bar_click"

/**
 * 传输列表优化 -- 远程上传列表 -- 失败二级页点击「删除记录」
 */
const val REMOTE_UPLOAD_TRANS_FAIL_LIST_DELETE_CLICK: String = "remote_upload_trans_fail_list_delete_click"

/**
 * 日活埋点-冷启次数-设备纬度
 */
const val DAY_LIVE_STARTUP_TIMES: String = "day_live_startup_times"

/**
 * widget 刷新
 */
const val RECEIVE_WIDGET_UPDATE: String = "receive_widget_update"

/**
 * 点击 widget 图片更新 notification
 */
const val LAUNCH_FROM_WIDGET: String = "launch_from_widget"

/**
 * 常驻通知栏点击拍照上传
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_CAPTURE_UPLOAD: String =
    "keep_active_notification_launcher_home_from_capture_upload"

/**
 * 常驻通知栏点击福利中心
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_BONUS_CENTER: String =
    "keep_active_notification_launcher_home_from_bonus_center"

/**
 * 常驻通知栏点击清理
 */
const val KEEP_ACTIVE_NOTICATION_LAUNCHER_HOME_FROM_CLEAN: String = "keep_active_notification_launcher_home_from_clean"

/**
 * shortcuts 点击
 */
const val SHORTCUTS_CLICK_EVENT: String = "shortcuts_click_event"

/**
 * 相册内滑动后出现浮层引导的展现
 */
const val WIDGET_GUIDE_DIALOG_SHOW: String = "widget_guide_dialog_show"

/**
 * 相册内滑动后出现浮层引导的点击添加
 */
const val WIDGET_GUIDE_DIALOG_CLICK_ADD: String = "widget_guide_dialog_click_add"

/**
 * widget 展现
 */
const val WIDGET_ENABLE_SHOW: String = "widget_enable_show"

/**
 * widget 移除
 */
const val WIDGET_DISABLE_HIDE: String = "widget_disable_hide"

/**
 * 离线飘条展示
 */
const val OFFLINE_VIDEO_TIP_SHOW: String = "offline_video_tip_show"

/**
 * 离线飘条关闭
 */
const val OFFLINE_VIDEO_TIP_CLOSE: String = "offline_video_tip_close"

/**
 * 离线视频列表展示
 */
const val OFFLINE_VIDEO_LIST_PAGE_SHOW: String = "offline_video_list_page_show"

/**
 * 离线视频列表条目点击
 */
const val OFFLINE_VIDEO_LIST_PAGE_ITEM_CLICK: String = "offline_video_list_page_item_click"

/**
 * 转存(他人共享)飘条展示
 */
const val TRANSFER_VIDEO_TIP_SHOW: String = "transfer_video_tip_show"

/**
 * 转存(他人共享)飘条关闭
 */
const val TRANSFER_VIDEO_TIP_CLOSE: String = "transfer_video_tip_close"

/**
 * 转存(他人共享)视频列表展示
 */
const val TRANSFER_VIDEO_LIST_PAGE_SHOW: String = "transfer_video_list_page_show"

/**
 * 转存(他人共享)视频列表条目点击
 */
const val TRANSFER_VIDEO_LIST_PAGE_ITEM_CLICK: String = "transfer_video_list_page_item_click"

/**
 * 远程上传飘条展示
 */
const val REMOTE_UPLOAD_VIDEO_TIP_SHOW: String = "remote_upload_video_tip_show"

/**
 * 远程上传飘条关闭
 */
const val REMOTE_UPLOAD_VIDEO_TIP_CLOSE: String = "remote_upload_video_tip_close"

/**
 * 远程上传视频列表展示
 */
const val REMOTE_UPLOAD_VIDEO_PAGE_SHOW: String = "remote_upload_video_page_show"

/**
 * 远程上传视频列表条目点击
 */
const val REMOTE_UPLOAD_VIDEO_PAGE_ITEM_CLICK: String = "remote_upload_video_page_item_click"

/**
 * home-裂变card-邀请banner展现
 */
const val FISSION_CARD_INVITE_BANNER_SHOW: String = "fission_card_invite_banner_show"

/**
 * home-裂变card-邀请点击
 */
const val FISSION_CARD_INVITE_BANNER_INVITE_CLICK: String = "fission_card_invite_banner_invite_click"

/**
 * home-裂变card-删除点击
 */
const val FISSION_CARD_INVITE_BANNER_CLOSE_CLICK: String = "fission_card_invite_banner_close_click"

/**
 * 视频 tab 资源圈引导展现
 */
const val VIDEO_TAB_SHARE_RESOURCE_VIEW_SHOW: String = "video_tab_share_resource_view_show"

/**
 * 视频 tab 资源圈第一行点击
 */
const val VIDEO_TAB_SHARE_RESOURCE_VIEW_FIRST_LINE_CLICK: String = "video_tab_share_resource_view_first_line_click"

/**
 * 视频 tab 资源圈第二行及更多点击
 */
const val VIDEO_TAB_SHARE_RESOURCE_VIEW_SECOND_LINE_CLICK: String = "video_tab_share_resource_view_second_line_click"

/**
 * 视频 tab 时长筛选 全部时长 点击
 */
const val VIDEO_TAB_DURATION_FILTER_ALL_CLICK: String = "video_tab_duration_filter_all_click"

/**
 * 视频 tab 时长筛选 5 分钟以上的点击
 */
const val VIDEO_TAB_DURATION_FILTER_5_MINUTES_CLICK: String = "video_tab_duration_filter_5_minutes_click"

/**
 * 视频 tab 上传视频按钮点击
 */
const val VIDEO_TAB_UPLOAD_BUTTON_CLICK: String = "video_tab_upload_button_click"

/**
 * 来自视频 tab 上传视频页面展现
 */
const val VIDEO_TAB_UPLOAD_VIDEO_SHOW: String = "video_tab_upload_video_show"

/**
 * 来自视频 tab 上传视频页面选择文件后上传按钮点击
 */
const val VIDEO_TAB_UPLOAD_VIDEO_SELECT_VIDEO_UPLOAD_CLICK: String = "video_tab_upload_video_select_video_upload_click"

/**
 * 视频 tab 资源圈 Context 未加载好之前调用导致的异常
 */
const val SHARE_RESOURCE_CONTEXT_BEFORE_LOADED_INVOKE_ERROR: String =
    "share_resource_context_before_loaded_invoke_error"

/**
 * 视频 tab 资源圈 Context 2秒后仍未加载好调用导致的异常
 */
const val SHARE_RESOURCE_CONTEXT_BEFORE_LOADED_INVOKE_SECOND_ERROR: String =
    "share_resource_context_before_loaded_invoke_second_error"

/**
 * 广告点击pv
 */
const val AD_MONITOR_CLICK: String = "ad_monitor_click"

/**
 * 广告展现成功
 */
const val AD_MONITOR_DISPLAY_SUCCESS: String = "ad_monitor_display_success"

/**
 * 广告展现失败
 */
const val AD_MONITOR_DISPLAY_FAILED: String = "ad_monitor_display_failed"

/**
 * 广告期望展现
 */
const val AD_MONITOR_EXPECT_SHOW: String = "ad_monitor_expect_show"

/**
 * 广告期望展现失败
 */
const val AD_MONITOR_EXPECT_SHOW_FAILED: String = "ad_monitor_expect_show_failed"

/**
 * 广告调用show
 */
const val AD_MONITOR_INVOKE_SHOW: String = "ad_monitor_invoke_show"

/**
 * 广告开始load
 */
const val AD_MONITOR_LOAD_START: String = "ad_monitor_load_start"

/**
 * 广告load成功
 */
const val AD_MONITOR_LOAD_SUCCESS: String = "ad_monitor_load_success"

/**
 * 冷启广告期望展示
 */
const val COLD_AD_MONITOR_EXCEPT_SHOW: String = "cold_ad_monitor_except_show"

/**
 * 热启广告触发入口
 */
const val HOT_AD_MONITOR_ONRESUME: String = "hot_ad_monitor_onresume"

/**
 * 热启广告期望展示
 */
const val HOT_AD_MONITOR_EXCEPT_SHOW: String = "hot_ad_monitor_except_show"

/**
 * 热启广告没有展示
 */
const val HOT_AD_MONITOR_EXCEPT_NOT_SHOW: String = "hot_ad_monitor_except_not_show"

/**
 * 冷启广告展示成功
 */
const val COLD_AD_MONITOR_SHOW_SUCCESS: String = "cold_ad_monitor_show_success"

/**
 * 冷启广告会员引导展示成功
 */
const val COLD_AD_MONITOR_VIP_GUIDE_SHOW_SUCCESS: String = "cold_ad_monitor_vip_guide_show_success"

/**
 * 冷启广告会员引导点击
 */
const val COLD_AD_MONITOR_VIP_GUIDE_CLICK: String = "cold_ad_monitor_vip_guide_click"

/**
 * 冷启广告不期望展示
 */
const val COLD_AD_MONITOR_NOT_EXCEPT_SHOW: String = "cold_ad_monitor_not_except_show"

/**
 * 导航页的pv、不包括未登录
 */
const val NAVIGATE_VIEW_PV_AFTER_LOGIN_V2: String = "navigate_view_pv_after_login_v2"

/**
 * 导航页未登录的pv
 */
const val NAVIGATE_VIEW_PV_BEFORE_LOGIN: String = "navigate_view_pv_before_login"

/**
 * 冷启广告展示成功后，关闭进入首页
 */
const val COLD_AD_MONITOR_SHOW_HIDDEN_TO_MAIN_VIEW: String = "cold_ad_monitor_show_hidden_to_main_view"

/**
 * 冷启广告展示失败
 */
const val COLD_AD_MONITOR_SHOW_FAILED: String = "cold_ad_monitor_show_failed"

/**
 * 广告load成功-首次
 */
const val AD_MONITOR_LOAD_SUCCESS_FIRST: String = "ad_monitor_load_success_first"

/**
 * 广告load失败
 */
const val AD_MONITOR_LOAD_FAILED: String = "ad_monitor_load_failed"

/**
 * 广告sdk开始初始化
 */
const val AD_MONITOR_SDK_INIT_START: String = "ad_monitor_sdk_init_start"

/**
 * 广告sdk初始化成功
 */
const val AD_MONITOR_SDK_INIT_SUCCESS: String = "ad_monitor_sdk_init_success"

/**
 * 广告sdk初始化失败
 */
const val AD_MONITOR_SDK_INIT_FAILED: String = "ad_monitor_sdk_init_failed"

/**
 * 侧滑菜单开启时长start
 */
const val DURATION_ABOUT_ME_FRAGMENT_START: String = "duration_about_me_fragment_start"

/**
 * 侧滑菜单开启时长end
 */
const val DURATION_ABOUT_ME_FRAGMENT_END: String = "duration_about_me_fragment_end"

/**
 * 免广告展示
 */
const val HOME_AD_BUBBLE_VIEW_SHOW: String = "home_AD_bubble_view_show"

/**
 * 免广告点击
 */
const val HOME_AD_BUBBLE_CLICK_ACTION: String = "home_AD_bubble_click_action"

/**
 * adx空白流量填充
 */
const val MAX_FALLBACK_TO_ADX: String = "max_fallback_to_adx"

/**
 * 视频 tab 进入「搜索视频」的页面展现
 */
const val VIDEO_TAB_SEARCH_VIDEO_VIEW_SHOW: String = "video_tab_search_video_view_show"

/**
 * 视频 tab 进入「搜索视频」后，个人视频搜索结果的点击
 */
const val VIDEO_TAB_SEARCH_VIDEO_VIEW_ITEM_CLICK: String = "video_tab_search_video_view_item_click"

/**
 * 视频 tab 进入「搜索视频」后，内容广场引导的点击
 */
const val VIDEO_TAB_SEARCH_VIDEO_VIEW_CONTENT_SQUARE_CLICK: String = "video_tab_search_video_view_content_square_click"

/**
 * 视频 tab 进入「搜索视频」后，在网络搜索的点击
 */
const val VIDEO_TAB_SEARCH_VIDEO_VIEW_NETWORK_SEARCH_CLICK: String = "video_tab_search_video_view_network_search_click"

/**
 * 资源圈横屏广告展示
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_SHOW: String = "resource_horizontal_video_pause_ad_show"

/**
 * 资源圈横屏广告关闭
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_CLOSE_CLICK: String = "resource_horizontal_video_pause_close_click"

/**
 * 资源圈横屏开会员点击
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_OPEN_VIP_CLICK: String = "resource_horizontal_video_pause_open_vip_click"

/**
 * 资源圈横屏广告数据无效
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_UNAVAILABLE: String = "resource_horizontal_video_pause_ad_unavailable"

/**
 * 横屏暂停广告期望展示
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_EXPECT_SHOW: String = "resource_horizontal_video_pause_ad_expect_show"

/**
 * 横屏暂停广告未展示
 */
const val RESOURCE_HORIZONTAL_VIDEO_PAUSE_AD_NOT_SHOW: String = "resource_horizontal_video_pause_ad_not_show"

/**
 * 贴片广告展示
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_SHOW: String = "video_bonding_manual_native_ad_show"

/**
 * 贴片广告点击
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_CLICK: String = "video_bonding_manual_native_ad_click"

/**
 * 贴片广告开会员点击
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_OPEN_VIP_CLICK: String = "video_bonding_manual_native_open_vip_click"

/**
 * 贴片广告无效
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_UNAVAILABLE: String = "video_bonding_manual_native_ad_unavailable"

/**
 * 贴片广告期望展示
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_EXPECT_SHOW: String = "video_bonding_manual_native_ad_expect_show"

/**
 * 贴片广告未展示
 */
const val VIDEO_BONDING_MANUAL_NATIVE_AD_NOT_SHOW: String = "video_bonding_manual_native_ad_not_show"

/**
 * 日活中添加当前启动来源
 * @since moder 2.18.0
 */
const val KEY_REPORT_USER_START_SOURCE: String = "key_report_user_start_source"

/**
 * 首充优惠券弹窗展现
 */
const val COUPON_TYPE_FIRST_PURCHASE_POPUP_SHOW: String = "coupon_type_first_purchase_popup_show"

/**
 * 首充优惠券弹窗点击x
 */
const val COUPON_TYPE_FIRST_PURCHASE_POPUP_CLOSE: String = "coupon_type_first_purchase_popup_close"

/**
 * 首充优惠券弹窗点击「领取」&其他可以跳去收银台的位置
 */
const val COUPON_TYPE_FIRST_PURCHASE_POPUP_ACTIVE: String =
    "coupon_type_first_purchase_popup_active"

/**
 * 复购优惠券弹窗展现
 */
const val COUPON_TYPE_REPURCHASE_POPUP_SHOW: String = "coupon_type_repurchase_popup_show"

/**
 * 复购优惠券弹窗点击x
 */
const val COUPON_TYPE_REPURCHASE_POPUP_CLOSE: String = "coupon_type_repurchase_popup_close"

/**
 * 复购优惠券弹点击「领取」&其他可以跳去收银台的位置
 */
const val COUPON_TYPE_REPURCHASE_POPUP_ACTIVE: String = "coupon_type_repurchase_popup_active"

/**
 * 优惠券场景引导优惠信息（包括倒计时）展现
 */
const val PREMIUM_GUIDE_COUPON_PURCHASE_SHOW: String = "premium_guide_coupon_purchase_show"

/**
 * 文件列表中offline状态的文件点击
 */
const val OFFLINE_STATUS_FILE_CLICK: String = "offline_status_file_click"

/**
 * 悬浮窗展现
 */
const val FLOAT_ACTIVITY_POPUP_SHOW_NEW: String = "float_activity_popup_show_new"

/**
 * 悬浮窗点击确认
 */
const val FLOAT_ACTIVITY_POPUP_CLICK_NEW: String = "float_activity_popup_click_new"

/**
 * 悬浮窗点击取消
 */
const val FLOAT_ACTIVITY_POPUP_CLICK_CANCLE_NEW: String = "float_activity_popup_click_cancle_new"

/**
 * 首页活动弹窗展现
 */
const val HOME_ACTIVITY_POPUP_SHOW: String = "home_activity_popup_show"

/**
 * 首页活动弹窗点击确认
 */
const val HOME_ACTIVITY_POPUP_CLICK: String = "home_activity_popup_click"

/**
 * 首页活动弹窗点击关闭
 */
const val HOME_ACTIVITY_POPUP_CLICK_CANCEL: String = "home_activity_popup_click_cancel"

/**
 * 本地 push 展现
 */
const val LOCAL_PUSH_SHOW: String = "local_push_show"

/**
 * 本地 push 添加/查看等点击
 */
const val LOCAL_PUSH_CONFIRM_CLICK: String = "local_push_confirm_click"

/**
 * 本地 push 忽略点击
 */
const val LOCAL_PUSH_IGNORE_CLICK: String = "local_push_ignore_click"

/**
 * 周计划促活-toast显示
 */
const val TASK_COMPLETE_TOAST_SHOW: String = "task_complete_toast_show"

/**
 * 周计划促活-toast点击
 */
const val TASK_COMPLETE_TOAST_CLICK: String = "task_complete_toast_click"

/**
 * 极速上传 - 视频自动备份点击购买
 */
const val QUICK_UPLOAD_VIDEO_AUTO_BACK_GUIDE_PAY: String = "quick_upload_video_auto_back_guide_pay"

/**
 * 极速上传 - 文件上传点击购买
 */
const val QUICK_UPLOAD_DOC_UPLOAD_GUIDE_PAY: String = "quick_upload_doc_upload_guide_pay"

/**
 * 极速上传 - 文件上传引导展示
 */
const val QUICK_UPLOAD_DOC_UPLOAD_GUIDE_SHOW: String = "quick_upload_doc_upload_guide_show"

/**
 * 极速上传 - 文件上传引导点击关闭
 */
const val QUICK_UPLOAD_DOC_UPLOAD_GUIDE_CLOSE: String = "quick_upload_doc_upload_guide_close"

/**
 * 极速上传 - 视频自动备份引导展示
 */
const val QUICK_UPLOAD_VIDEO_AUTO_BACK_GUIDE_SHOW: String = "quick_upload_video_auto_back_guide_show"

/**
 * 极速上传 - 视频自动备份引导点击关闭
 */
const val QUICK_UPLOAD_VIDEO_AUTO_BACK_GUIDE_CLOSE: String = "quick_upload_video_auto_back_guide_close"

/**
 * 极速上传 - 图片备份点击购买
 */
const val QUICK_UPLOAD_IMAGE_AUTO_BACK_GUIDE_PAY: String = "quick_upload_image_auto_back_guide_pay"

/**
 * 极速上传 - 图片备份引导展示
 */
const val QUICK_UPLOAD_IMAGE_AUTO_BACK_GUIDE_SHOW: String = "quick_upload_image_auto_back_guide_show"

/**
 * 极速上传 - 图片备份引导点击关闭
 */
const val QUICK_UPLOAD_IMAGE_AUTO_BACK_GUIDE_CLOSE: String = "quick_upload_image_auto_back_guide_close"

/**
 * 极速上传-引导付费飘条-展现-自动备份-照片
 */
const val QUICK_UPLOAD_BACKUP_GUIDE_SHOW: String = "quick_upload_backup_guide_show"

/**
 * 极速上传-引导付费飘条-点击x-自动备份-照片
 */
const val QUICK_UPLOAD_BACKUP_GUIDE_CLOSE: String = "quick_upload_backup_guide_close"

/**
 * 极速上传-引导付费飘条-点击「购买」按钮-自动备份-照片
 */
const val QUICK_UPLOAD_BACKUP_GUIDE_PAY: String = "quick_upload_backup_guide_pay"

/**
 * 尊享飘条展现
 */
const val QUICK_UPLOAD_BACKUP_VIP_TIPS_SHOW: String = "quick_upload_backup_vip_tips_show"

/***
 * 图片上传 - 飘条展示
 */
const val QUICK_UPLOAD_PHOTO_FLOAT_SHOW: String = "quick_upload_photo_float_show"

/**
 * 图片上传 - 飘条点击关闭
 */
const val QUICK_UPLOAD_PHOTO_FLOAT_CLOSE: String = "quick_upload_photo_float_close"

/**
 * 图片上传 - 飘条点击购买
 */
const val QUICK_UPLOAD_PHOTO_FLOAT_BUY: String = "quick_upload_photo_float_buy"

/**
 * 文件上传 - 飘条点击购买
 */
const val QUICK_UPLOAD_DOC_FLOAT_BUY: String = "quick_upload_doc_float_buy"

/***
 * 视频上传 - 飘条展示
 */
const val QUICK_UPLOAD_VIDEO_FLOAT_SHOW: String = "quick_upload_video_float_show"

/***
 * 文件上传 - 飘条展示
 */
const val QUICK_UPLOAD_DOC_FLOAT_SHOW: String = "quick_upload_doc_float_show"

/**
 * 视频上传 - 飘条点击关闭
 */
const val QUICK_UPLOAD_VIDEO_FLOAT_CLOSE: String = "quick_upload_video_float_close"

/**
 * 文件上传 - 飘条点击关闭
 */
const val QUICK_UPLOAD_DOC_FLOAT_CLOSE: String = "quick_upload_doc_float_close"

/**
 * 视频上传 - 飘条点击购买
 */
const val QUICK_UPLOAD_VIDEO_FLOAT_BUY: String = "quick_upload_video_float_buy"

/**
 * AF投放渠道 - 新安装打开调起指定页面
 */
const val AF_NEW_INSTALL_OPEN_PAGE: String = "af_new_install_open_page"

/**
 * 首页活动卡片 - 展现
 */
const val ACTIVITY_CARD_CONFIG_SHOW: String = "activity_card_config_show"

/**
 * 首页活动卡片 - 点击跳转落地页
 */
const val ACTIVITY_CARD_CONFIG_CLICK: String = "activity_card_config_click"

/**
 * 首页活动卡片 - "关闭"的点击
 */
const val ACTIVITY_CARD_CONFIG_CLOSE: String = "activity_card_config_close"

/**
 * 新手任务活动卡片 - 展现
 */
const val NEWBIE_CARD_CONFIG_SHOW: String = "newbie_card_config_show"

/**
 * 新手任务活动卡片 - 点击跳转落地页
 */
const val NEWBIE_CARD_CONFIG_CLICK: String = "newbie_card_config_click"

/**
 * 新手任务活动卡片 - "关闭"的点击
 */
const val NEWBIE_CARD_CONFIG_CLOSE: String = "newbie_card_config_close"

/**
 * 电影Tab展现
 */
const val SEARCH_RECOMMEND_BEFORE_MOVIE_SHOW: String = "search_recommend_before_movie_show"

/**************  常驻通知栏前后台转化  ****************/

/***
 * 常驻通知栏展现，[KEEP_ACTIVE_NOTIFICATION_SHOW] 这个点缺少了常驻通知栏开启后应用一致不被杀，导致进程一直
 * 存在进而缺少了这部分用户的开启情况
 */
const val KEEP_ACTIVE_NOTIFICATION_APPEAR: String = "keep_active_notification_appear"

/**
 * 进程启动后60秒后才进行后台上报，同时常驻通知栏存在的情况下认为是常驻通知栏保活引起的后台上报
 */
const val KEEP_ACTIVE_NOTIFICATION_BACKGROUND_REPORT: String = "keep_active_notification_background_report"

/**
 * 进入新版搜索页
 */
const val ENTER_NEW_SEARCH: String = "enter_new_search"

/**
 * 用户点击搜索
 */
const val USER_DO_SEARCH: String = "user_do_search"

/**
 * 搜索有结果
 */
const val SEARCH_HAS_RESULT: String = "search_has_result"

/**
 * 搜索无结果
 */
const val SEARCH_NO_RESULT: String = "search_no_result"

/**
 * 搜索结果页点击个人文件
 */
const val SEARCH_RESULT_PERSON_FILE_CLICK: String = "search_result_person_file_click"

/**
 * 点击全网资源
 */
const val SEARCH_RESULT_SHARE_RESOURCE_CLICK: String = "search_result_share_resource_click"

/**
 * 点击 BT 文件
 */
const val SEARCH_RESULT_BT_FILE_CLICK: String = "search_result_bt_file_click"

/**
 * BT 文件保存成功
 */
const val SEARCH_RESULT_BT_FILE_SAVE_SUCCESS: String = "search_result_bt_file_save_success"

/**
 * 矩阵APP唤起应用
 */
const val LAUNCH_FROM_MATRIX_APP: String = "launch_from_matrix_app"

/**
 * 剧集推荐Tab展现
 */
const val SEARCH_RECOMMEND_SERIES_TAB_SHOW: String = "search_recommend_series_tab_show"

/**
 * 电影推荐 - 点击
 */
const val SEARCH_RECOMMEND_MOVIE_CLICK: String = "search_recommend_movie_click"

/**
 * 剧集推荐 - 点击
 */
const val SEARCH_RECOMMEND_SERIES_CLICK: String = "search_recommend_series_click"

/**
 * 推荐内容刷新
 */
const val SEARCH_RECOMMEND_REFRESH: String = "search_recommend_refresh"

/**
 * 用户使用 DocumentProvider 查看文件
 */
const val CONTENT_PROVIDER_SHOW: String = "content_provider_show"

/**
 * 用户使用 DocumentProvider 中的文件
 */
const val CONTENT_PROVIDER_CLICK_FILE: String = "content_provider_click_file"

/**
 * 忽略掉的热启广告
 */
const val IGNOR_HOT_OPEN_AD_PV: String = "ignor_hot_open_ad_pv"

/**
 * 剧集保存浮层展现
 */
const val SERIES_SAVE_DIALOG_SHOW: String = "series_save_dialog_show"

/**
 * 剧集保存浮层点击保存按钮
 */
const val SERIES_SAVE_DIALOG_SAVE_CLICK: String = "series_save_dialog_save_click"

/**
 * 资源圈合集页 -- 合集页面的展现
 */
const val SHARE_RESOURCE_SERIES_PAGE_SHOW = "share_resource_series_page_show"

/**
 * 资源圈合集页 -- 合集页面的「保存剧集」按钮点击
 */
const val SHARE_RESOURCE_SERIES_PAGE_SAVE_CLICK = "share_resource_series_page_save_click"

/**
 * 资源圈合集页 -- 合集页面点击下方某一剧集
 */
const val SHARE_RESOURCE_SERIES_PAGE_ITEM_CLICK = "share_resource_series_page_item_click"

/**
 * 点击分享
 */
const val SHARE_RESOURCE_SHARE_CLICK: String = "share_resource_share_click"

/**
 * 分享成功
 */
const val SHARE_RESOURCE_SHARE_SUCCESS: String = "share_resource_share_success"

/**
 * 福袋展示
 */
const val HOME_BONUS_BAG_SHOW: String = "home_bonus_bag_show"

/**
 * 新福袋展示
 */
const val HOME_ENCOURAGE_BAG_SHOW: String = "home_encourage_bag_show"

/**
 * 福袋点击
 */
const val HOME_BONUS_BAG_CLICK: String = "home_bonus_bag_click"

/**
 * 新福袋点击
 */
const val HOME_ENCOURAGE_BAG_CLICK: String = "home_encourage_bag_click"

/**
 * 福袋领取弹窗展示
 */
const val HOME_BONUS_BAG_DIALOG_SHOW: String = "home_bonus_bag_dialog_show"

/**
 * 福袋领取弹窗点击领取
 */
const val HOME_BONUS_BAG_DIALOG_CLICK_OBTAIN: String = "home_bonus_bag_dialog_click_obtain"

/**
 * 福袋成功toast
 */
const val HOME_BONUS_BAG_TOAST_SUCCESS: String = "home_bonus_bag_toast_success"

/**
 * 福袋失败toast
 */
const val HOME_BONUS_BAG_TOAST_FAILURE: String = "home_bonus_bag_toast_failure"

/**
 * debug 主面板展示
 */
const val MY_TEST_MAIN_PANEL_SHOW = "my_test_main_panel_show"

/**
 * debug 多语言开关点击
 */
const val MY_TEST_SWITCH_LANGUAGE_CLICK = "my_test_switch_language_click"

/**
 * debug 多语言切换悬浮球展示
 */
const val MY_TEST_SWITCH_LANGUAGE_FLOAT_SHOW = "my_test_switch_language_float_show"

/**
 * debug 多语言切换悬浮球点击
 */
const val MY_TEST_SWITCH_LANGUAGE_FLOAT_CLICK = "my_test_switch_language_float_click"

/**
 * debug 多语言切换点击
 */
const val MY_TEST_SWITCH_LANGUAGE_DONE = "my_test_switch_language_done"

/**
 * 首页空间卡片展示
 */
const val HOME_SPACE_INSPECTOR_CARD_SHOW = "home_space_inspector_card_show"

/**
 * debug Server环境入口点击
 */
const val MY_TEST_SWITCH_SERVER_HOST_CLICK = "my_test_switch_server_host_click"

/**
 * debug 单个Server环境入口点击
 */
const val MY_TEST_SWITCH_SINGLE_SERVER_HOST_SHOW = "my_test_switch_single_server_host_show"

/**
 * debug 单个Server环境切换
 */
const val MY_TEST_SWITCH_SINGLE_SERVER_HOST_DONE = "my_test_switch_single_server_host_done"

/**
 * debug Server环境重置
 */
const val MYTEST_SWITCH_SINGLE_SERVER_HOST_RESET = "my_test_switch_single_server_host_reset"

/**
 * debug Firebase 配置页展现
 */
const val MY_TEST_SWITCH_FIREBASE_CONFIG_SHOW = "my_test_switch_firebase_config_show"

/**
 * debug Firebase 配置页保存
 */
const val MY_TEST_SWITCH_FIREBASE_CONFIG_DONE = "my_test_switch_firebase_config_done"

/**
 * debug Firebase 配置页重置
 */
const val MY_TEST_SWITCH_FIREBASE_CONFIG_RESET_CLICK = "my_test_switch_firebase_config_reset_click"

/**
 * 冷启动细分阶段耗时统计
 */
const val COLD_STARTUP_TRACE = "cold_startup_trace"

/**
 * 冷启动细分阶段耗时统计
 */
const val COLD_STARTUP_TRACE_1 = "cold_startup_trace_1"

/**
 * 开屏页细分阶段耗时统计
 */
const val NAVIGATE_STARTUP_SUBDIVIDE_2 = "navigate_startup_subdivide_2"

/**
 * 冷启动耗时监控，首Activity加载
 */
const val COLD_STARTUP_TRACE_MONITOR = "cold_startup_trace_monitor"

/**
 * com.dubox.drive.files.ui.cloudfile.presenter.DuboxFilePresenter.addToDownloadTask
 * 集合可能产生空指针异常
 */
const val ADD_TO_DOWNLOAD_TASK_FAILED_NPE = "add_to_download_task_failed_npe"

/**
 * 记录前台归因上报时对应的埋点和间隔时间差
 * @since moder 3.0.0
 */
const val RECORD_FOREGROUND_LAUNCH_SOURCE_AND_TIME: String = "record_foreground_launch_source_and_time"

/**
 * 首页顶部运营位-活动轮播位展示
 */
const val OPERATION_TOP_BANNER_SHOW: String = "operation_top_banner_show"

/**
 * 首页顶部运营位-活动轮播位点击
 */
const val OPERATION_TOP_BANNER_CLICK: String = "operation_top_banner_click"

/**
 * 首页顶部运营位-顶部头图位展示
 */
const val OPERATION_TOP_IMAGE_SHOW: String = "operation_top_image_show"

/**
 * 1+1运营位-左边展示
 */
const val OPERATION_ONE_AND_ONE_LEFT_SHOW: String = "operation_one_and_one_left_show"

/**
 * 1+1运营位-左边点击
 */
const val OPERATION_ONE_AND_ONE_LEFT_CLICK: String = "operation_one_and_one_left_click"

/**
 * 1+1运营位-右边展示
 */
const val OPERATION_ONE_AND_ONE_RIGHT_SHOW: String = "operation_one_and_one_right_show"

/**
 * 1+1运营位-右边点击
 */
const val OPERATION_ONE_AND_ONE_RIGHT_CLICK: String = "operation_one_and_one_right_click"

/**
 * 首页快捷tab-福利tab入口展现
 */
const val HOME_SHORTCUT_BONUS_TAB_SHOW: String = "home_shortcut_bonus_tab_show"

/**
 * 首页快捷tab-最近tab入口展现
 */
const val HOME_SHORTCUT_RECENT_TAB_SHOW: String = "home_shortcut_recent_tab_show"

/**
 * 首页快捷tab-内容广场tab入口展现
 */
const val HOME_SHORTCUT_RESOURCE_TAB_SHOW: String = "home_shortcut_resource_tab_show"

/**
 * 首页快捷tab-离线tab入口展现
 */
const val HOME_SHORTCUT_OFFLINE_TAB_SHOW: String = "home_shortcut_offline_tab_show"

/**
 * 首页快捷tab-点击频道管理
 */
const val HOME_SHORTCUT_TAB_EDIT_CLICK: String = "home_shortcut_tab_edit_click"

/**
 *  * 首页空间卡片点击进入空间管理
 */
const val HOME_SPACE_INSPECTOR_CARD_GO_MANAGE = "home_space_inspector_card_go_manage"

/**
 * 首页空间卡片点击进入文件列表  参数：image/video/document/music
 */
const val HOME_SPACE_INSPECTOR_CARD_GO_CATEGORY = "home_space_inspector_card_go_category"

/**
 * 首页会员卡片点击购买
 */
const val HOME_VIP_CARD_CLICK_PURCHASE = "home_vip_card_click_purchase"

/**
 * 点击排序入口
 */
const val CLICK_SORT_ENTRANCE = "click_sort_entrance"

/**
 * 点击按名称排序
 */
const val CLICK_SORT_BY_NAME = "click_sort_by_name"

/**
 * 点击按时间排序
 */
const val CLICK_SORT_BY_TIME = "click_sort_by_time"

/**
 * 点击按大小排序
 */
const val CLICK_SORT_BY_SIZE = "click_sort_by_size"

/**
 * 点击按类型排序
 */
const val CLICK_SORT_BY_TYPE = "click_sort_by_type"

/**
 * 排序面板展现
 */
const val VIEW_SORT_PANEL = "view_sort_panel"

/**
 * 首页快捷路径-最近tab页面展现
 */
const val HOME_SHORTCUT_RECENT_SHOW: String = "home_shortcut_recent_show"

/**
 * 上传视频 VIP 购买点击
 */
const val UPLOAD_VIDEO_BUY_VIP_CLICK: String = "upload_video_buy_vip_click"

/**
 * 文件回收站延长30天 VIP 购买点击
 */
const val RESTORE_FILE_BUY_VIP_CLICK: String = "restore_file_buy_vip_click"

/**
 * 上传视频 VIP 引导展示
 */
const val UPLOAD_VIDEO_BUY_VIP_GUIDE_SHOW: String = "upload_video_buy_vip_guide_show"

/**
 * 文件回收站延长30天 VIP 引导展示
 */
const val RESTORE_FILE_BUY_VIP_GUIDE_SHOW: String = "restore_file_buy_vip_guide_show"

/**
 * 更换 LOGO 成功
 */
const val CHANGE_LOGO_SUCCESS: String = "change_logo_success"

/**
 * 首页快捷tab-福利tab页面展现
 */
const val HOME_SHORTCUT_BONUS_SHOW: String = "home_shortcut_bonus_show"

/**
 * 首页快捷tab-内容广场tab页面展现
 */
const val HOME_SHORTCUT_RESOURCE_SHOW: String = "home_shortcut_resource_show"

/**
 * 手机上传视频引导飘条展现
 */
const val UPLOAD_VIDEO_VIP_GUIDE_FLOATING_BAR_SHOW: String = "upload_video_vip_guide_floating_bar_show"

/**
 * 手机上传视频引导飘条购买点击
 */
const val UPLOAD_VIDEO_VIP_GUIDE_FLOATING_BAR_BUY_CLICK: String = "upload_video_vip_guide_floating_bar_buy_click"

/**
 * 保险箱空状态上传点击
 */
const val SAFE_BOX_EMPTY_UPLOAD_CLICK: String = "safe_box_empty_upload_click"

/**
 * 新手任务 - 任务完成弹窗展现
 */
const val NEWBIE_TASK_FINISH_DIALOG_SHOW: String = "newbie_task_finish_dialog_show"

/**
 * 新手任务 - 任务完成弹窗去完成
 */
const val NEWBIE_TASK_FINISH_DIALOG_TO_NEXT_CLICK: String = "newbie_task_finish_dialog_to_next_click"

/**
 * 新手任务 - 任务完成弹窗关闭
 */
const val NEWBIE_TASK_FINISH_DIALOG_CLOSE: String = "newbie_task_finish_dialog_close"

/**
 * 新手任务 - 关卡完成弹窗展现
 */
const val NEWBIE_CHECKPOINT_FINISH_DIALOG_SHOW: String = "newbie_checkpoint_finish_dialog_show"

/**
 * 新手任务 - 关卡完成弹窗去完成
 */
const val NEWBIE_CHECKPOINT_FINISH_DIALOG_TO_NEXT_CLICK: String = "newbie_checkpoint_finish_dialog_to_next_click"

/**
 * 新手任务 - 关卡完成弹窗关闭
 */
const val NEWBIE_CHECKPOINT_FINISH_DIALOG_CLOSE: String = "newbie_checkpoint_finish_dialog_close"

/**
 * 资源承接页-右上角原生登录
 */
const val WAP_RESOURCE_PLAZA_PAGE_NATIVE_LOGIN_CLICK: String = "wap_resource_plaza_page_native_login_click"

/**
 * 新手任务 - 蒙层 - SHOW
 * 参数：关卡、taskId、第几个蒙层(1、2、3 ...)、其他自定义参数
 */
const val NEWBIE_TASK_GUIDE_SHOW: String = "newbie_task_guide_show"

/**
 * 新手任务 - 蒙层 - SKIP
 */
const val NEWBIE_TASK_GUIDE_SKIP: String = "newbie_task_guide_skip"

/**
 * 新手任务 - 蒙层 - NEXT
 */
const val NEWBIE_TASK_GUIDE_NEXT: String = "newbie_task_guide_next"

/**
 * job等待时长
 */
const val BASE_JOB_WAIT_DURATION_MONITOR_NO: String = "base_job_wait_duration_monitor_no"

/**
 * job运行时长
 */
const val BASE_JOB_RUN_DURATION_MONITOR_NO: String = "base_job_run_duration_monitor_no"

/**
 * 卡顿率 - 非监控
 */
const val BLOCK_RATE_V1_NOT_MONITOR: String = "block_rate_v1_not_monitor"

/**
 * 新福利弹窗-展示
 */
const val ENCOURAGE_FRAGMENT_SHOW: String = "encourage_fragment_show"

/**
 * 新福利弹窗-点击查看视频
 */
const val ENCOURAGE_FRAGMENT_CLICK_WATCH_AD: String = "encourage_fragment_click_watch_ad"

/**
 * 新福利弹窗-领取成功 toast
 */
const val ENCOURAGE_TOAST_COLLECTED_SUCCESS: String = "encourage_toast_collected_success"

/**
 * 新福利弹窗-领取失败 toast
 */
const val ENCOURAGE_TOAST_COLLECTED_FAILED: String = "encourage_toast_collected_failed"

/**
 * 新福利弹窗 - 1天会员发放成功弹窗展现
 */
const val ENCOURAGE_RECEIVE_VIP_ONE_DAY: String = "encourage_receive_vip_one_day"

/**
 * 新福利弹窗 - 3天会员发放成功弹窗展现
 */
const val ENCOURAGE_RECEIVE_VIP_THREE_DAY: String = "encourage_receive_vip_three_day"

/**
 * 新福利弹窗 - 会员领取达到1000天上限弹窗展现
 */
const val ENCOURAGE_RECEIVE_VIP_OVER_LIMIT: String = "encourage_receive_vip_over_limit"

/**
 * 新福利弹窗 - 领取弹窗页会员按钮点击
 */
const val ENCOURAGE_FRAGMENT_CLICK_VIP_GUIDE: String = "encourage_fragment_click_vip_guide"

/**
 * 新福利弹窗 - 1天会员发放成功弹窗【继续看视频】按钮点击
 */
const val NEW_BLESSING_BAG_PREMIUM1_DAY_ALERT_CONTINUE: String = "new_blessing_bag_premium1_day_alert_continue"

/**
 * 新福利弹窗 - 1天会员发放成功弹窗【继续看视频】按钮点击
 */
const val NEW_BLESSING_BAG_PREMIUM_LIMIT_ALERT_TOMORROW: String = "new_blessing_bag_premium_limit_alert_tomorrow"

/**
 * 新福利弹窗 - 会员领取达到1000天上限弹窗【看视频领空间】点击
 */
const val NEW_BLESSING_BAG_PREMIUM_LIMIT_ALERT_CONTINUE: String = "new_blessing_bag_premium_limit_alert_continue"

/**
 * 新福利弹窗 - 3天会员发放成功弹窗【明天继续领Premium】按钮点击
 */
const val NEW_BLESSING_BAG_PREMIUM3_DAY_ALERT_CONTINUE: String = "new_blessing_bag_premium3_day_alert_continue"

/**
 * 新福利弹窗 - 广告点击
 */
const val ENCOURAGE_NATIVE_AD_CLICK: String = "encourage_native_ad_click"

/**
 * 新福利弹窗 - 广告展现
 */
const val ENCOURAGE_NATIVE_AD_SHOW: String = "encourage_native_ad_show"

/**
 * 付费引导弹窗 【免费体验Premium】按钮展现
 */
const val PREMIUM_FREE_TRY_ENTRY_SHOW: String = "premium_free_try_entry_show"

/**
 * 极速上传， 免费试用按钮展现, 与其他三个埋点连用
 * QUICK_UPLOAD_DOC_UPLOAD_GUIDE_SHOW (quick_upload_doc_upload_guide_show),
 * QUICK_UPLOAD_VIDEO_AUTO_BACK_GUIDE_SHOW (quick_upload_video_auto_back_guide_show),
 * QUICK_UPLOAD_IMAGE_AUTO_BACK_GUIDE_SHOW (quick_upload_image_auto_back_guide_show)
 */
const val FREE_TRY_USE: String = "_free_try_use"

/**
 * 极速上传， 免费试用按钮点击, 与其他三个埋点连用
 */
const val FREE_TRY_USE_CLICK: String = "_free_try_use_click"

/**
 * 外链页转存成功弹窗展示
 */
const val SHARE_LINK_SAVE_SUCCESS_DIALOG_SHOW: String = "share_link_save_success_dialog_show"

/**
 * 外链页转存成功弹窗去查看按钮点击
 */
const val SHARE_LINK_SAVE_SUCCESS_DIALOG_CHECK_FILE_CLICK: String = "share_link_save_success_dialog_check_file_click"

/**
 * 多外链页进入多选模式
 */
const val MULTI_SHARE_LIST_ENTER_MULTI_CHOICE_MODE: String = "multi_share_list_enter_multi_choice_mode"

/**
 * 多外链页退出多选模式
 */
const val MULTI_SHARE_LIST_EXIT_MULTI_CHOICE_MODE: String = "multi_share_list_exit_multi_choice_mode"


/**
 * 多外链页面图片预览
 */
const val MULTI_SHARE_LIST_PREVIEW_IMAGE: String = "multi_share_list_preview_image"

/**
 * 多外链页面视频预览
 */
const val MULTI_SHARE_LIST_PREVIEW_VIDEO: String = "multi_share_list_preview_video"

/**
 * 多外链页面音频预览
 */
const val MULTI_SHARE_LIST_PREVIEW_MUSIC: String = "multi_share_list_preview_music"


/**
 * 外链页转存成功弹窗关闭按钮点击
 */
const val SHARE_LINK_SAVE_SUCCESS_DIALOG_CLOSE_CLICK: String = "share_link_save_success_dialog_close_click"

/**
 * 外链视频播放尝试自动保存成功
 */
const val SHARE_LINK_VIDEO_AUTO_SAVE_SUCCESS: String = "share_link_video_auto_save_success"

/**
 * 多外链页选择路径页保存点击
 */
const val MULTI_SHARE_LIST_SELECT_PATH_SAVE_CLICK: String = "multi_share_list_select_path_save_click"

/**
 * 外链视频播放尝试自动保存失败后降级到 A 方案
 */
const val SHARE_LINK_VIDEO_AUTO_SAVE_DOWN_TO_A: String = "share_link_video_auto_save_down_to_a"

/**
 * 外链视频播放尝试自动保存失败
 */
const val SHARE_LINK_VIDEO_AUTO_SAVE_FAILED: String = "share_link_video_auto_save_failed"

/**
 * 外链页转存成功弱 toast 提示展现
 */
const val SHARE_LINK_SAVE_SUCCESS_TOAST_SHOW: String = "share_link_save_success_toast_show"


/**
 * 付费引导弹窗 【免费体验Premium】按钮点击
 */
const val PREMIUM_FREE_TRY_ENTRY_CLICK: String = "premium_free_try_entry_click"

/**
 * 新手引导任务卡片展现
 */
const val HOME_CARD_NEWBIE_GUIDE_SHOW: String = "home_card_newbie_guide_show"

/**
 * 新手引导任务卡片关闭
 */
const val HOME_CARD_NEWBIE_GUIDE_CLOSE: String = "home_card_newbie_guide_close"

/**
 * 新手引导任务卡片点击
 */
const val HOME_CARD_NEWBIE_GUIDE_CLICK: String = "home_card_newbie_guide_click"

/**
 * 新手引导任务承接页展现
 */
const val NEWBIE_GUIDE_PAGE_SHOW: String = "newbie_guide_page_show"

/**
 * 新手引导任务承接页点击跳过
 */
const val NEWBIE_GUIDE_PAGE_CLICK_SKIP: String = "newbie_guide_page_click_skip"

/**
 * 新手引导任务承接页点击开始任务
 */
const val NEWBIE_GUIDE_PAGE_CLCIK_START: String = "newbie_guide_page_clcik_start"

/**
 * 资源圈举报：resourceId
 */
const val SHARE_RESOURCE_REPORT_ID: String = "share_resource_report_id"

/**
 * 资源承接页--展示
 */
const val RESOURCE_UNDERTAKING_SHOW: String = "resource_undertaking_show"

/**
 * 资源承接页--NA端登录按钮点击
 */
const val RESOURCE_UNDERTAKING_LOGIN_CLICK: String = "resource_undertaking_login_click"

/**
 * 外链承接--底部保存按钮点击
 */
const val CHAIN_UNDERTAKING_SAVE_CLICK: String = "chain_undertaking_save_click"

/**
 * 外链承接--左上角「取消」按钮点击
 */
const val CHAIN_UNDERTAKING_CANCEL_CLICK: String = "chain_undertaking_cancel_click"

/**
 * 外链承接--右上角「全选」or「取消全选」按钮点击
 */
const val CHAIN_UNDERTAKING_SELECT_CLICK: String = "chain_undertaking_select_click"

/**
 * 外链页原生广告展示 参数 1 表示已登录，参数 0 表示未登录
 */
const val SHARE_LINK_PAGE_NATIVE_AD_SHOW: String = "share_link_page_native_ad_show"

/**
 * 资源圈举报功能中举报用户的展现
 */
const val SHARE_RESOURCE_REPORT_USER_SHOW: String = "share_resource_report_user_show"

/**
 * 视频播放前进后退快捷操作 : 视频播放-非沉浸态（单击后触发的操作页面）的展现
 */
const val VIDEO_PLAY_FAST_OPERATION_SHOW: String = "video_play_fast_operation_show"

/**
 * 视频播放前进后退快捷操作 : 视频播放-非沉浸态（单击后触发的操作页面）-前进10S的点击
 */
const val VIDEO_PLAY_FAST_FORWARD_CLICK: String = "video_play_fast_forward_click"

/**
 * 视频播放前进后退快捷操作 : 视频播放-非沉浸态（单击后触发的操作页面）-后退10S的点击
 */
const val VIDEO_PLAY_FAST_BACK_CLICK: String = "video_play_fast_back_click"

/**
 * 视频播放前进后退快捷操作 : 视频播放-非沉浸态（单击后触发的操作页面）-暂停的点击
 */
const val VIDEO_PLAY_FAST_PAUSE_CLICK: String = "video_play_fast_pause_click"

/**
 * 视频播放前进后退快捷操作 : 视频播放-沉浸态-双击中部暂停热区
 */
const val VIDEO_PLAY_FAST_FORWARD_DOUBLE_CLICK: String = "video_play_fast_forward_double_click"

/**
 * 视频播放前进后退快捷操作 : 视频播放-沉浸态-左部后退热区
 */
const val VIDEO_PLAY_FAST_BACK_DOUBLE_CLICK: String = "video_play_fast_back_double_click"

/**
 * 视频播放前进后退快捷操作 : 视频播放-沉浸态-暂停的点击
 */
const val VIDEO_PLAY_FAST_PAUSE_DOUBLE_CLICK: String = "video_play_fast_pause_double_click"

/**
 * 端内视频播放器 : 下载按钮的点击
 */
const val VIDEO_PLAY_DOWNLOAD_CLICK: String = "video_play_download_click"

/**
 * 资源圈优化 - 保存时间的时长
 */
const val RESUORCE_VIDEO_PLAY_SAVE_DURATION: String = "resuorce_video_play_save_duration"

/**
 * 资源圈优化 - 已保存View的展示
 */
const val RESOURCE_VIDEO_PLAY_SAVE_INFO_SHOW: String = "resource_video_play_save_info_show"

/**
 * 资源圈优化 - 已保存View点击查看
 */
const val RESOURCE_VIDEO_PLAY_SAVE_INFO_CLICK: String = "resource_video_play_save_info_click"

/**
 * 资源圈优化 - 自动跳转成功
 */
const val RESOURCE_VIDEO_PLAY_AUTO_OPEN_VIDEO: String = "resource_video_play_auto_open_video"

/**
 * 资源圈优化 - 保存失败的展示
 */
const val RESOURCE_VIDEO_PLAY_SAVE_FAILED_SHOW: String = "resource_video_play_save_failed_show"

/**
 * 设备评分
 */
const val DEVICE_SCORE: String = "device_score"

/**
 * 清理APP直接打开次数
 */
const val FLEXTECHCLEANER_OPEN_DIRECTLY_NUM: String = "flextechcleaner_open_directly_num"

/**
 * 清理APP触发打开次数
 */
const val FLEXTECHCLEANER_OPEN_OPERATE_NUM: String = "flextechcleaner_open_operate_num"

/**
 * 新福袋激励视频-积分入口展现
 */
const val REWARD_COIN_ENCOURAGE_SHOW: String = "reward_coin_encourage_show"

/**
 * 新福袋激励视频-积分入口点击
 */
const val REWARD_COIN_ENCOURAGE_CLICK: String = "reward_coin_encourage_click"

/**
 * 旧福袋激励视频-积分入口展现
 */
const val REWARD_COIN_BONUS_SHOW: String = "reward_coin_bonus_show"

/**
 * 旧福袋激励视频-积分入口点击
 */
const val REWARD_COIN_BONUS_CLICK: String = "reward_coin_bonus_click"

/**
 * 个人中心-积分入口展现
 */
const val REWARD_COIN_USER_CENTER_SHOW: String = "reward_coin_user_center_show"

/**
 * 个人中心-积分入口点击
 */
const val REWARD_COIN_USER_CENTER_CLICK: String = "reward_coin_user_center_click"

/**
 * 试看模式-保存按钮的展示
 */
const val PLAYER_INTERCEPT_MODEL_SAVE_SHOW: String = "player_intercept_model_save_show"

/**
 * 试看模式-重新试看的展示
 */
const val PLAYER_INTERCEPT_MODEL_REWATCH_SHOW: String = "player_intercept_model_rewatch_show"

/**
 * 试看模式-保存按钮的点击
 */
const val PLAYER_INTERCEPT_MODEL_SAVE_CLICK: String = "player_intercept_model_save_click"

/**
 * 画中画点击
 */
const val PIC_IN_PIC_BTN_CLICK: String = "floating_bt_click"

/**
 * 试看模式-重新试看的点击
 */
const val PLAYER_INTERCEPT_MODEL_REWATCH_CLICK: String = "player_intercept_model_rewatch_click"

/**
 * 竖屏时会员引导按钮展现
 */
const val PLAYER_PORTRAIT_1080P_HINT_SHOW = "player_portrait_1080p_hint_show"

/**
 * 横屏时titlebar 1080p按钮展现
 */
const val PLAYER_LANDSCAPE_1080P_HINT_SHOW = "player_landscape_1080p_hint_show"

/**
 * 横屏时1080p提示气泡展示
 */
const val PLAYER_LANDSCAPE_1080P_TIPS_SHOW = "player_landscape_1080p_tips_show"

/**
 * 竖屏时会员引导按钮点击
 */
const val PLAYER_PORTRAIT_1080P_HINT_CLICK = "player_portrait_1080p_hint_click"

/**
 * 横屏时1080p提示气泡点击
 */
const val PLAYER_LANDSCAPE_1080P_TIPS_CLICK = "player_landscape_1080p_tips_click"

/**
 * 横屏时1080p提示气泡关闭
 */
const val PLAYER_LANDSCAPE_1080P_TIPS_CLOSE = "player_landscape_1080p_tips_close"

/**
 * 横屏时1080p提示按钮点击
 */
const val PLAYER_LANDSCAPE_1080P_HINT_CLICK = "player_landscape_1080p_hint_click"

/**
 * 雷达入口点击 : 参数区分:上传面板入口(0)&视频tab中的入口(1)
 */
const val RADAR_ENTRANCE_CLICK = "radar_entrance_click"

/**
 * 雷达首页展现
 */
const val RADAR_HOME_PAGE_SHOW = "radar_home_page_show"

/**
 * 雷达首页点击「开始探索」
 */
const val RADAR_HOME_PAGE_EXPLORE_CLICK = "radar_home_page_explore_click"

/**
 * 雷达首页点击「查看今日结果」
 */
const val RADAR_HOME_PAGE_TODAY_RESULT_CLICK = "radar_home_page_today_result_click"

/**
 * 推荐面板刷新埋点
 */
const val PLAYER_RECOMMAND_LIST_REFRESH_CLICK_PORTRAIT_STYLE: String =
    "player_recommand_list_refresh_click_portrait_style"

/**
 * 推荐视频点击
 */
const val PLAYER_RECOMMAND_RESOURCE_SELECTED_PORTRAIT_STYLE: String =
    "player_recommand_resource_selected_portrait_style"

/**
 * 推荐tab展现
 */
const val PLAYER_RECOMMAND_LIST_SHOW_PORTRAIT_STYLE: String = "player_recommand_list_show_portrait_style"

/**
 * 推荐tab点击
 */
const val PLAYER_RECOMMAND_TAB_CLICK_PORTRAIT_STYLE: String = "player_recommand_tab_click_portrait_style"

/**
 * 播放列表tab点击
 */
const val PLAYER_LIST_TAB_CLICK_PORTRAIT_STYLE: String = "player_list_tab_click_portrait_style"

/**
 * 播放器-会员特权介绍图展现
 */
const val PLAYER_VIP_GUIDE_SHOW: String = "player_vip_guide_show"

/**
 * 播放器-会员特权介绍图点击
 */
const val PLAYER_VIP_GUIDE_CLICK: String = "player_vip_guide_click"

/**
 * 雷达结果页展现
 */
const val RADAR_RESULT_PAGE_SHOW: String = "radar_result_page_show"

/**
 * 雷达结果页卡片背面点击
 */
const val RADAR_RESULT_PAGE_CARD_BACK_CLICK: String = "radar_result_page_card_back_click"

/**
 * 雷达结果页卡片正面点击
 */
const val RADAR_RESULT_PAGE_CARD_FRONT_CLICK: String = "radar_result_page_card_front_click"

/**
 * 雷达卡片详情弹窗展现
 */
const val RADAR_CARD_DETAIL_DIALOG_SHOW: String = "radar_card_detail_dialog_show"

/**
 * 雷达卡片详情弹窗点击保存
 */
const val RADAR_CARD_DETAIL_DIALOG_SAVE_CLICK: String = "radar_card_detail_dialog_save_click"

/**
 * 雷达卡片详情弹窗点击观看视频
 */
const val RADAR_CARD_DETAIL_DIALOG_WATCH_VIDEO_CLICK: String = "radar_card_detail_dialog_watch_video_click"

/**
 * 雷达卡片详情弹窗点击查看文件
 */
const val RADAR_CARD_DETAIL_DIALOG_CHECK_FILE_CLICK: String = "radar_card_detail_dialog_check_file_click"

/**
 * 雷达资源在播放器试看页面保存
 */
const val RADAR_RESOURCE_SAVE_IN_PLAYER_TRY_WATCH: String = "radar_resource_save_in_player_try_watch"

/**
 * 雷达资源在外链页保存
 */
const val RADAR_RESOURCE_SAVE_IN_CHAIN_INFO: String = "radar_resource_save_in_chain_info"

/**
 * 雷达资源在外链页下载
 */
const val RADAR_RESOURCE_DOWNLOAD_IN_CHAIN_INFO: String = "radar_resource_download_in_chain_info"

/**
 * 雷达结果页还有翻卡次数挽留弹窗展现
 */
const val RADAR_RESULT_PAGE_REMAIN_CHANCE_EXIT_CONFIRM_DIALOG_SHOW: String =
    "radar_result_page_remain_chance_exit_confirm_dialog_show"

/**
 * 雷达结果页还有翻卡次数挽留弹窗点击继续翻卡
 */
const val RADAR_RESULT_PAGE_REMAIN_CHANCE_EXIT_CONFIRM_DIALOG_CONTINUE_CLICK: String =
    "radar_result_page_remain_chance_exit_confirm_dialog_continue_click"

/**
 * 雷达结果页还有未保存文件挽留弹窗展现
 */
const val RADAR_RESULT_PAGE_NOT_SAVED_EXIT_CONFIRM_DIALOG_SHOW: String =
    "radar_result_page_not_saved_exit_confirm_dialog_show"

/**
 * 雷达结果页还有未保存文件挽留弹窗点击继续保存
 */
const val RADAR_RESULT_PAGE_NOT_SAVED_EXIT_CONFIRM_DIALOG_CONTINUE_SAVE_CLICK: String =
    "radar_result_page_not_saved_exit_confirm_dialog_continue_save_click"

/**
 * 外链落地页的展现
 */
const val SHARE_LINK_FILE_LIST_SHOW: String = "share_link_file_list_show"

/**
 * 外链落地页的数据load成功
 */
const val SHARE_LINK_FILE_LIST_LOAD_SUCESS: String = "share_link_file_list_load_sucess"

/**
 * 外链落地页的数据load失败
 */
const val SHARE_LINK_FILE_LIST_LOAD_FAILED: String = "share_link_file_list_load_failed"

/**
 * 视频起播
 */
const val VIDEO_DID_PREPARED = "video_did_prepared"

/**
 * 雷达视频试看起播
 */
const val VIDEO_DID_PREPARED_RADAR = "video_did_prepared_radar"

/**
 * 推荐视频试看起播
 */
const val VIDEO_DID_PREPARED_RECOMMAND = "video_did_prepared_recommand"

/**
 * 来自转存文件起播
 */
const val VIDEO_DID_PREPARED_SHARE_SAVED = "video_did_prepared_share_saved"

/**
 * 资源圈文件
 */
const val VIDEO_DID_PREPARED_RESOURCE_CIRCLE = "video_did_prepared_resource_circle"

/**
 * 外链视频起播
 */
const val VIDEO_DID_PREPARED_SHARE_FILE_OPEN = "video_did_prepared_share_file_open"

/**
 * 手动上传视频起播
 */
const val VIDEO_DID_PREPARED_UPLOAD = "video_did_prepared_upload"

/**
 * 自动备份视频起播
 */
const val VIDEO_DID_PREPARED_AUTO_UPLOAD = "video_did_prepared_auto_upload"

/**
 * 远程上传视频起播
 */
const val VIDEO_DID_PREPARED_REMOTE_UPLOAD = "video_did_prepared_remote_upload"

/**
 * 其他文件
 */
const val VIDEO_DID_PREPARED_OTHER = "video_did_prepared_other"

/**
 * 雷达播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_RADAR = "video_player_did_show_radar"

/**
 * 推荐播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_RECOMMAND = "video_player_did_show_recommand"

/**
 * 外链播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_SHARE_FILE_OPEN = "video_player_did_show_share_file_open"

/**
 * 手动上传播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_UPLOAD = "video_player_did_show_upload"

/**
 * 自动上传播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_AUTO_UPLOAD = "video_player_did_show_auto_upload"

/**
 * 转存播放展现
 */
const val VIDEO_PLAYER_DID_SHOW_SHARE_SAVED = "video_player_did_show_share_saved"

/**
 * 远程上传展现
 */
const val VIDEO_PLAYER_DID_SHOW_REMOTE_UPLOAD = "video_player_did_show_remote_upload"

/**
 * 其他展现
 */
const val VIDEO_PLAYER_DID_SHOW_OTHER = "video_player_did_show_other"

/**
 * 影视圈导流卡片点击查看更多
 */
const val SHARE_RESOURCE_HOME_CARD_SEE_ALL_CLICK = "share_resource_home_card_see_all_click"

/**
 * 金刚位展示 - file sharing
 */
const val HOME_DIAMOND_CARD_FILE_SHARING_SHOW = "home_diamond_card_file_sharing_show"

/**
 * 金刚位点击 - file sharing
 */
const val HOME_DIAMOND_CARD_FILE_SHARING_CLICK = "home_diamond_card_file_sharing_click"

/**
 * 金刚位展示 - radar
 */
const val HOME_DIAMOND_CARD_RADAR_SHOW = "home_diamond_card_radar_show"

/**
 * 金刚位点击 - radar
 */
const val HOME_DIAMOND_CARD_RADAR_CLICK = "home_diamond_card_radar_click"

/**
 * 金刚位展示 - 远程上传
 */
const val HOME_DIAMOND_CARD_REMOTE_UPLOAD_SHOW = "home_diamond_card_remote_upload_show"

/**
 * 金刚位点击 - 远程上传
 */
const val HOME_DIAMOND_CARD_REMOTE_UPLOAD_CLICK = "home_diamond_card_remote_upload_click"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_SHOW_350 = "resource_impr"
const val YTB_RESOURCE_SHOW = "ytb_resource_impr"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_FEED_ITEM_CLICK_350 = "resource_click"
const val YTB_RESOURCE_CLICK = "ytb_resource_click"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_ITEM_SHARE_CLICK_350 = "resource_share_click"
const val YTB_RESOURCE_SHARE_CLICK = "ytb_resource_share_click"

/**
 * 分享结果埋点
 */
const val VIDEO_DETAIL_SHARE_RESULRT = "resource_share_result_click"
const val VIDEO_DETAIL_SHARE_RESULRT_PARAM_RESULT = "result"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_ITEM_SAVE_CLICK_350 = "resource_save_click"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_ITEM_SAVE_RESULT_CLICK_350 = "resource_save_result_click"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_LIKE_CLICK_350 = "resource_like_click"
const val YTB_RESOURCE_LIKE_CLICK = "ytb_resource_like_click"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_EXTRA_KEY_URL_350 = "url"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_EXTRA_KEY_TYPE_350 = "type"

/**
 * 3.5.0 埋点方案：successful
 */
const val SHARE_RESOURCE_EXTRA_KEY_TYPE_SUCCESSFUL_350 = "successful"

/**
 * 3.5.0 埋点方案：failed
 */
const val SHARE_RESOURCE_EXTRA_KEY_TYPE_FAILED_350 = "failed"

/**
 * 3.5.0 埋点方案：event name
 */
const val SHARE_RESOURCE_EXTRA_KEY_RESULT_350 = "result"

/**
 * 3.5.0 埋点方案：event name
 * 保存到云端 & 下载到本地
 */
const val SHARE_RESOURCE_EXTRA_VALUE_BOTH_350 = "both"

/**
 * 3.5.0 埋点方案：event name
 * 仅 保存到云端
 */
const val SHARE_RESOURCE_EXTRA_VALUE_SAVE_350 = "cloud"

/**
 * 仅 下载到本地
 */
const val SHARE_RESOURCE_EXTRA_VALUE_DOWNLOAD_350 = "local"

/**
 * 3.5.0 埋点方案：page字段名
 */
const val OTHER0_PAGE_EXPLORE_350 = "explore"

/**
 * 3.5.0 埋点方案：page字段名
 */
const val OTHER0_PAGE_LIST_350 = "listpage"

/**
 * 3.5.0 埋点方案：page字段名
 */
const val OTHER0_PAGE_HOME_350 = "home"

/**
 * 3.5.0 埋点方案：page 和 module 字段名
 */
const val OTHER0_PAGE_DETAIL_350 = "detail"

/**
 * 标签详情页
 */
const val OTHER0_PAGE_TAG_DETAIL = "tag_detail"

/**
 * 3.5.0 埋点方案：module字段名
 */
const val OTHER1_MODULE_SLIDE_350 = "slide"

/**
 * 3.5.0 埋点方案：module字段名
 */
const val OTHER1_MODULE_RELATED_350 = "related"

/**
 * 埋点方案:播放器页
 */
const val OTHER0_PAGE_PLAYER = "player"

/**
 * 3.5.0 埋点方案：event name video downloader卡片展示
 */
const val VIDEO_DOWNLOADER_EVENT_CARD_IMPRESSION = "videodownloader_card_impression"

/**
 * 3.5.0 埋点方案：event name video downloader卡片中内容点击
 */
const val VIDEO_DOWNLOADER_PAGE = "video_download"

/**
 * 3.5.0 埋点方案：event name video downloader 卡片内容点击
 */
const val VIDEO_DOWNLOADER_EVENT_CARD_CLICK = "videodownloader_card_click"

/**
 * 3.5.0 埋点方案：event name video download 页面，解析按钮点击
 */
const val VIDEO_DOWNLOADER_EVENT_SEARCH_BT_CLICK = "videodownload_search_bt_click"

/**
 * 3.5.0 埋点方案：event name 嗅探下载按钮点击
 */
const val VIDEO_DOWNLOADER_EVENT_BT_CLICK = "videodownload_bt_click"

/**
 * 3.5.0 埋点方案：event name 嗅探结果呈现
 */
const val VIDEO_DOWNLOADER_EVENT_RESULT = "videodownload_result"

/**
 * 3.5.0 埋点方案：event name 嗅探结果保存操作
 */
const val VIDEO_DOWNLOADER_EVENT_ACTION = "videodownload_action"

/**
 * 3.5.0 埋点方案：event name 嗅探结果保存点击
 */
const val VIDEO_DOWNLOADER_EVENT_SAVE = "videodownload_save"

/**
 * video downloader 添加按钮成功与否埋点
 */
const val VIDEO_DOWNLOADER_MARK = "video_downloader_mark"

/**
 * video downloader 解析成功与否埋点
 */
const val VIDEO_DOWNLOADER_PARSE = "video_downloader_parse"

/**
 * video downloader 保存成功与否埋点
 */
const val VIDEO_DOWNLOADER_SAVE = "video_downloader_save"

/**
 * video downloader 嗅探时间（用户点击-到结果展示）
 */
const val VIDEO_DOWNLOADER_SNIFF_TIME = "video_downloader_sniff_time"

/**
 * video downloader 埋点：h5按钮点击
 */
const val VIDEO_DOWNLOAGER_LOG_JS_CLICK = "videodownload_log_js_click"

/**
 * video downloader 埋点：js嗅探到结果
 */
const val VIDEO_DOWNLOAGER_LOG_JS_GET_VIDEO = "videodownload_log_js_get_video"

/**
 * video downloader 埋点：js嗅探warn
 */
const val VIDEO_DOWNLOAGER_LOG_JS_WARN = "videodownload_log_js_warn"

/**
 * 视频下载器埋点：js嗅探错误
 */
const val VIDEO_DOWNLOADER_ERROR_LOG = "videodownload_error_log"

/**
 * 视频下载器埋点：js嗅探错误
 */
const val VIDEO_DOWNLOADER_EVENT_BT_CLICK_2 = "videodownload_bt_click_2"

/**
 * 视频下载器埋点：js嗅探错误
 */
const val VIDEO_DOWNLOADER_PARSE_2 = "video_downloader_parse_2"

/**
 * 广告展示埋点
 */
const val AD_EVENT_SHOW = "ads_impr"

/**
 * 广告点击埋点
 */
const val AD_EVENT_CLICK = "ads_click"

const val EVENT_TRACE_COMMON_PARAM_URL = "url"
const val EVENT_TRACE_COMMON_PARAM_TYPE = "type"
const val EVENT_TRACE_COMMON_PARAM_CATEGORY = "category"

/**
 * 视频播放时长
 */
const val VIDEO_PLAY_DURATION = "play_time"
const val VIDEO_PLAY_DURATION_PARAM_DURATION = "duration"


const val VIDEO_YOUTUBE_SHORTS_LOADING_FAILED_TAG = "video_youtube_shorts_loading_failed_tag"

/**
 * 推荐关注列表展示
 */
const val RECOMMEND_FOLLOW_PAGE_SHOW: String = "recommend_follow_page_show"
const val OTHER2_SOURCE_HOME_SLIDE_MORE: String = "home_slide_more"
const val OTHER2_SOURCE_EXPLORE_SLIDE_MORE: String = "explore_slide_more"
const val OTHER2_SOURCE_EXPLORE_ICON: String = "explore_icon"

/**
 * 关注成功
 */
const val FOLLOW_SUCCESS: String = "follow_success"
const val OTHER0_PAGE_RECOMMEND_FOLLOW: String = "recommend_follow"
const val OTHER0_PAGE_RESOURCE_FOLLOW: String = "resource_detail_follow"
const val OTHER3_UID: String = "uid"

/**
 * 首页slide卡片展示成功
 */
const val HOME_SLIDE_SHOW_SUCCESSFUL: String = "home_slide_show_successful"

const val OTHER1_MODULE_FOLLOWING: String = "following"

/**
 * 3.8.0 埋点方案：page字段名 个人主页
 */
const val OTHER0_PAGE_PROFILE_380 = "profile"


/**
 * 3.8.0 埋点方案：event name
 */
const val RECOMMEND_FOLLOW_CARD_SHOW_380 = "recommend_follow_card_impr"

/**
 * 3.8.5 通知栏优化打点
 * */
const val PAGE_SHOW = "page_show"
const val BUTTON_CLICK = "button_click"
const val POPUP_SHOW = "popup_show"
const val POPUP_BUTTON_CLICK = "popup_button_click"

/**
 * WhatsApp item 点击
 */
const val WA_STATUS_CLICK = "wa_status_click"

const val DOWNLOAD_SEARCH = "download_search"

const val DOWNLOAD_ALLNET_SEARCH = "allnet_search"

const val DUAL_PROCESS_PULL_SUCCESS = "dual_process_pull_success"

const val DUAL_PROCESS_CHANGE_END_EQUALS = "dual_process_change_end_equals"
const val DUAL_PROCESS_CHANGE_END_NOT_BACKGROUND = "dual_process_change_end_not_background"
const val DUAL_PROCESS_CHANGE_END_OVER_TIME = "dual_process_change_end_over_time"
const val DUAL_PROCESS_CHANGE_END_FIVE_SECOND_INNER = "dual_process_change_end_five_second_inner"
const val DUAL_PROCESS_FOR_DAU = "dual_process_for_dau"

/**
 * 首页最近小组入口展现
 */
//@Stat(sourceId = "31300-013", desc = "首页最近小组入口展现")
const val HOME_RECENT_RESOURCE_GROUP_CARD_SHOW: String = "home_recent_resource_group_card_show"

/**
 * 首页最近小组入口单个小组点击
 */
//@Stat(sourceId = "31300-014", desc = "首页最近小组入口单个小组点击")
const val HOME_RECENT_RESOURCE_GROUP_CARD_ITEM_CLICK: String = "home_recent_resource_group_card_item_click"

/**
 * 首页最近小组除小组外其他热区点击
 */
//@Stat(sourceId = "31300-015", desc = "首页最近小组除小组外其他热区点击")
const val HOME_RECENT_RESOURCE_GROUP_CARD_MORE_GROUP_CLICK: String = "home_recent_resource_group_card_more_group_click"


const val DYNAMIC_SO_DOWNLOAD_ERROR = "dynamic_so_download_error"
const val DYNAMIC_SO_DOWNLOAD_MD5ERROR = "dynamic_so_download_md5error"
const val DYNAMIC_SO_LOAD_CHECK_ERROR = "dynamic_so_load_check_error"
const val DYNAMIC_SO_LOAD_SLOW_TAG = "dynamic_so_load_slow_tag"
//const val DYNAMIC_SO_LOAD_SLOW_TAG = "DYNAMIC_SO_LOAD_SLOW_TAG"

const val DOWNLOAD_TAB_PAGE = "downloader"

const val FIRST_PLAY_SHORTS_LOADING_TIME = "first_play_shorts_loading_time"
const val VALUE = "value"

// 上报 获取到的 Facebook DeepLink 路由
const val FB_DEFERRED_DPLINK = "fb_deferred_dplink"
const val DEFERRED_DPLINK = "deferred_dplink"

//上报屏蔽成人内容弹窗
const val CONTENT_PRF_POPUP_SHOW = "content_prf_popup_show"
const val CONTENT_PRF_POPUP_CLOSE = "content_prf_popup_close"
const val CONTENT_PRF_STATUS = "content_prf_status"

//搜索结果
const val SEARCH_RESULT_EXPLORE_CLICK = "search_result_explore_click"

const val SEARCH_RESULT_EXPLORE_IMPR = "search_result_explore_impr"

//倍速切换点击
const val SWITCH_SPEED_UP_CLICK = "switch_speed_up_click"

//倍速切换成功
const val SWITCH_SPEED_UP_SUC = "switch_speed_up_suc"

//倍速切换失败
const val SWITCH_SPEED_UP_FAILED = "switch_speed_up_failed"

//分辨率切换点击
const val SWITCH_QUALITY_CLICK = "switch_quality_click"

//分辨率切换成功
const val SWITCH_QUALITY_SUC = "switch_quality_suc"

//分辨率切换失败
const val SWITCH_QUALITY_FAILED = "switch_quality_failed"

const val DETAIL_UNFULL = "detail_unfull"

const val DETAIL_FULL = "detail_full"

const val FILES_UNFULL = "files_unfull"

const val FILES_FULL = "files_full"

const val SPEED = "speed"

const val ISYTB = "resource_is_ytb"

const val QUALITY = "quality"

/**
 * 热搜词点击时上报
 */
const val HOT_SEARCH_ITEM_CLICK = "hot_search_item_click"

const val HOTSEARCH = "hotsearch"

const val QUERY = "query"

const val LINK_YOUTUBE_BT_CLICK: String = "link_youtube_bt_click"
const val UNLINK_YOUTUBE_BT_CLICK: String = "unlink_youtube_bt_click"
const val LINKTO_YOUTUBE_PAGE_SHOW: String = "linkto_youtube_page_show"
const val LINK_BT_CLICK: String = "link_bt_click"
const val LINK_SUCCESSFUL: String = "link_successful"

/**
 * speed dial 各个入口按钮点击
 */
const val SPEED_DIAL_BT_CLICK = "speed_dial_bt_click"

/**
 * 添加书签成功
 */
const val ADD_BOOKMARK_SUCCESS = "add_bookmark_success"

/**
 * 书签
 */
const val BOOKMARK = "bookmark"

/**
 * WebView
 */
const val WEBVIEW = "webview"

/**
 * 书签页展示
 */
const val BOOKMARK_PAGE_SHOW = "bookmark_page_show"

/**
 * search 入口点击
 */
const val SEARCH_BT_CLICK = "search_bt_click"

const val STAT_KEY_ID = "id"

const val MY_FILES = "myfiles"

/** 添加磁力链按钮点击 */
const val KEY_URL_LINKS_BT_CLICK = "url_links_bt_click"
/** 添加bt files按钮点击 */
const val KEY_BT_FILES_BT_CLICK = "bt_files_bt_click"

/**
 * 流畅播埋点
 */
// 流畅播：启动流畅播嗅探的次数
const val KEY_SMOOTH_PLAYBACK_START = "smooth_playback_start"

// 流畅播：请求server 按照url规则匹配接口的次数
const val KEY_SMOOTH_PLAYBACK_SERVER_URL_MATCH = "smooth_playback_server_url_match"

// 流畅播：请求server 按照url规则匹配接口，匹配成功的次数
const val KEY_SMOOTH_PLAYBACK_SERVER_URL_MATCH_SUCCESSFUL =
    "smooth_playback_server_url_match_successful"

// 流畅播：请求server 按照名称+时长+大小规则匹配接口的次数
const val KEY_SMOOTH_PLAYBACK_SERVER_NAME_MATCH = "smooth_playback_server_name_match"

// 流畅播：请求server 按照名称+时长+大小规则匹配接口，匹配成功的次数
const val KEY_SMOOTH_PLAYBACK_SERVER_NAME_MATCH_SUCCESSFUL =
    "smooth_playback_server_name_match_successful"

// 流畅播：嗅探有结果
const val KEY_SMOOTH_PLAYBACK_RESULT = "smooth_playback_result"

// 流畅播：嗅探成功后流畅播按钮点击
const val KEY_SMOOTH_PLAYBACK_BT_CLICK = "smooth_playback_bt_click"

// 流畅播：流畅播弹窗展示
const val KEY_SMOOTH_PLAYBACK_POPUP_SHOW = "smooth_playback_popup_show"

// 流畅播：按钮状态是"流畅播"时，该按钮点击
const val KEY_SMOOTH_PLAYBACK_PLAY_BT_CLICK = "smooth_playback_play_bt_click"

// 流畅播：按钮状态是"查看进度"对应的弹窗展示
const val KEY_SMOOTH_PLAYBACK_PROGRESS_POPUP_SHOW =
    "smooth_playback_progress_popup_show"

// 流畅播：按钮状态是"查看进度"时，该按钮点击
const val KEY_SMOOTH_PLAYBACK_PROGRESS_BT_CLICK = "smooth_playback_progress_bt_click"

// 流畅播：用户在loading 过程中时，关闭了该进程
const val KEY_SMOOTH_PLAYBACK_PROGRESS_CLOSE = "smooth_playback_progress_close"

// 流畅播：按钮状态是"Play Now"对应的弹窗展示
const val KEY_SMOOTH_PLAYBACK_PLAYNOW_POPUP_SHOW =
    "smooth_playback_playnow_popup_show"

// 流畅播：按钮状态是"Play Now"时，该按钮点击
const val KEY_SMOOTH_PLAYBACK_PLAYNOW_BT_CLICK = "smooth_playback_playnow_bt_click"

// 流畅播：点击“流畅播”，到成功出现“Play Now”状态所消耗的时长, 单位ms
const val KEY_SMOOTH_PLAYBACK_TIME_SPEND = "smooth_playback_time_spend"

// 流畅播：按钮状态是"Failed and retry"对应的弹窗展示
const val KEY_SMOOTH_PLAYBACK_RETRY_POPUP_SHOW = "smooth_playback_retry_popup_show"

// 流畅播：按钮状态是"Failed and retry"时，该按钮点击
const val KEY_SMOOTH_PLAYBACK_RETRY_BT_CLICK = "smooth_playback_retry_bt_click"

// 点击“流畅播”，然后一直loading，还没到出现Play it 状态，用户直接关闭了webview，此时所消耗的时间
const val KEY_SMOOTH_PLAYBACK_KILL_TIME_SPEND = "smooth_playback_kill_time_spend"

// 当前访问的站点的url，需要urlEncode
const val PARAM_ORIGIN_URL = "origin_url"

// 嗅探出来的资源对应的url，需要urlEncode
const val PARAM_RESOURCE_URL = "resource_url"

// 消耗的时长，单位ms
const val PARAM_TIME = "time"

/**
 * type：
 * 1：通过url匹配出来的内容
 * 2：通过名称+时长+大小 匹配出来的内容
 * 3：通过通用嗅探匹配出来的内容
 */
const val PARAM_TYPE = "type"
const val SOURCE_SMOOTH_BING = "bing"
const val SOURCE_SMOOTH_NORMAL = "normal"

/** 标签点击 */
const val KEY_TAG_CLICK = "tag_click"

/** 标签展开按钮点击 */
const val KEY_TAG_SHOW_MORE_BT_CLICK = "tag_show_more_bt_click"

/** 所点击tag 的名称 */
const val PARAM_TAG = "tag"

/** 用户点击输入框搜索按钮或点击输入键盘搜索按钮发起搜索 */
const val KEY_AGGREGATED_SEARCH_START = "aggregated_search_start"

/** 全网搜索结果展示 */
const val KEY_AGGREGATED_SEARCH_RESULT = "aggregated_search_result"

/** 全网搜索结果点击 */
const val KEY_AGGREGATED_SEARCH_OPERATION = "aggregated_search_operation"
const val PARAM_DURATION = "duration"
const val PARAM_DURATION1 = "duration1"
const val PARAM_DURATION2 = "duration2"
const val PARAM_OPERATION_TYPE = "operationType"
const val PARAM_TRACE_ID = "traceId"

const val HOME_FEED_TIME_SPEND = "home_feed_time_spend"

/**
 * 搜索内容 埋点
 */
const val QUERY_CONTENT = "query_content"

/**
 * 全网搜展示错误兜底页
 */
const val INTERNET_SEARCH_FAILED_PAGE_SHOW = "internet_search_failed_page_show"

/**
 * 全网搜展示“未找到结果”页面
 */
const val INTERNET_SEARCH_NULL_PAGE_SHOW = "internet_search_null_page_show"

/** startup各启动任务执行情况 */
const val STARTUP_TASK_EXECUTE_STATISTICS = "startup_task_execute_statistics"
/** startup各启动任务执行情况 */
/** startup 主线程的总等待时长、任务执行总耗时情况 */
const val STARTUP_TASK_EXECUTE_TIME_COUNT = "startup_task_execute_time_count"

/** startup 执行出错 */
const val STARTUP_LAUNCH_ERROR = "startup_launch_error"

/** 转存成功后的快捷分享弹窗展示  */
const val SAVE_AFTER_POPUP_SHOW = "save_after_popup_show"
/** 转存成功后的快捷分享弹窗-分享按钮点击  */
const val SAVE_AFTER_POPUP_SHARE_BT_CLICK = "save_after_popup_share_bt_click"
