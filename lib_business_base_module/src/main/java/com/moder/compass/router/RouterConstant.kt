package com.moder.compass.router

/**
 * @author sunmeng
 * create at 2021-08-27
 * Email: sunmeng12@moder.com
 *
 * 常量放到 BaseModule 其他 Module 可以直接调用，
 * 防止路由路径变更时，其他地方未变更导致的问题
 */

/**
 * 路由-文件列表
 */
const val ROUTER_FILE_LIST: String = "tab/filelist"

/**
 * 路由-时光轴
 */
const val ROUTER_TIMELINE: String = "tab/album"

/**
 * 路由-视频 tab
 */
const val ROUTER_VIDEO: String = "tab/video"

/**
 * 路由首页 tab
 */
const val ROUTER_HOME: String = "tab/home"

/**
 * 路由-分享 Tab
 */
const val ROUTER_SHARE: String = "tab/share"

/**
 * 路由-资源小组 Tab
 */
const val ROUTER_RESOURCE_ROUTER: String = "tab/resourcegroup"
/**
 * 路由-站长中心 Tab
 */
const val ROUTER_TAB_EARN: String = "tab/earn"

/**
 * 路由-资源小组动态页
 */
const val ROUTER_RESOURCE_GROUP_POST: String = "resourcesGroup/post"

/**
 * 路由-网页活动
 */
const val ROUTER_WEBVIEW: String = "h5"

/**
 * 路由-系统浏览器
 */
const val ROUTER_BROWSER: String = "browser"

/**
 * 路由-资源圈
 */
const val ROUTER_SHARE_RESOURCE: String = "resources"

/**
 * 路由-资源小组问答 feed 首页
 */
const val ROUTER_RESOURCE_GROUP_FEED_HOME: String = "resource/answerFeed"

/**
 * 路由-空间分析
 */
const val ROUTER_STORAGE: String = "storage"

/**
 * 上传文件弹窗
 */
const val ROUTER_ALERT_UPLOAD: String = "alert/uploadfiles"

/**
 * 自动备份系统弹窗
 */
const val ROUTER_ALERT_BACKUP: String = "alert/autobackup"

/**
 * 分享文件，进入文件 Tab 且为编辑态
 */
const val ROUTER_SHARE_EDIT: String = "shareedit"

/**
 * 分享文件，进入文件 Tab 且为音频类型
 */
const val ROUTER_FILE_TO_AUDIO_PAGE: String = "filelist/audio"

/**
 * 路由保险箱
 */
const val ROUTER_SAFEBOX: String = "safebox"

/**
 * 回收站
 */
const val ROUTER_SETTING_RECYCLER: String = "setting/recycler"


/**
 * 设置页
 */
const val ROUTER_SETTING_SETTING: String = "setting/setting"

/**
 * 我的分享
 */
const val ROUTER_SETTING_SHARE: String = "setting/share"

/**
 * 用户反馈
 */
const val ROUTER_SETTING_FEEDBACK: String = "setting/feedback"

/**
 * 自动备份设置
 */
const val ROUTER_SETTING_BACKUP: String = "setting/autobackup"

/**
 * 传输列表的路由协议变动，所以页面的逻辑在 @see{TransRouter}
 */
const val ROUTER_TRANS_DOWNLOAD: String = "trans"

/**
 * 路由-上传图片、视频或文件
 * upload?type=photo 上传图片
 * upload?type=video 上传视频
 * upload?type=file 上传文件
 * upload?type=offline 远程上传
 * upload?type=take_photo 拍照上传
 */
const val ROUTER_UPLOAD: String = "upload"

///**
// * 传输列表-上传
// */
//internal const val ROUTER_TRANS_UPLOAD = "trans?type=upload"
//
///**
// * 传输列表-远程上传
// */
//internal const val ROUTER_TRANS_REMOTE = "trans?type=remote"


/**
 * 路由-离线列表
 */
const val ROUTER_OFFLINE_LIST: String = "filelist/offline"

/**
 * 路由-搜索
 */
const val ROUTER_SEARCH: String = "filelist/search"

/**
 * 路由-智能分类
 */

const val ROUTER_CATEGORIZE: String = "album/categorize"

/**
 * 路由-最近播放
 */
const val ROUTER_RECENT_PLAY: String = "video/recentplay"

/**
 * 路由-帐号管理
 */
const val ROUTER_ACCOUNT_LIST: String = "setting/accountlist"

/**
 * 路由-视频下载器
 */
const val ROUTER_VIDEO_DOWNLOADER: String = "video/downloader"
const val ROUTER_VIDEO_DOWNLOADER_TAB: String = "tab/downloader"
/**
 * 路由-视频下载器->直接调整到bt files tab
 */
const val ROUTER_VIDEO_DOWNLOADER_BT_FILES: String = "video/downloader/btfiles"

/**
 * 路由 - 大图预览
 */
const val ROUTER_IMAGE_VIEW: String = "image/view"
/**
 * 用户教程页
 */
const val ROUTER_USER_TUTORIAL: String = "user/tutorial"
/**
 * 收银台
 */
const val ROUTER_PREMIUM: String = "premium"
/**
 * 资源圈搜索
 */
const val ROUTER_RESOURCES_SEARCH: String = "resources/search"
/**
 * 资源圈详情页，如果是电影直接进播放页，如果是剧集则进剧集列表
 */
const val ROUTER_RESOURCE_DETAIL: String = "resource/detail"
/**
 * 资源圈feed list 页面
 * */
const val ROUTER_RESOURCE_FEED: String = "resource/feed"
/**
 * 首页快捷tab-福利中心
 */
const val ROUTER_HOME_WELFARE: String = "home/welfare"
/**
 * 首页快捷tab-最近
 */
const val ROUTER_HOME_RECENT: String = "home/recent"
/**
 * 首页快捷tab-资源圈
 */
const val ROUTER_HOME_RESOURCE: String = "home/resource"
/**
 * 首页快捷tab-离线列表
 */
const val ROUTER_HOME_OFFLINE: String = "home/offline"
/**
 * 外链承接页
 */
const val ROUTER_SHARE_RECEIVE_LINK: String = "share/receivelink"

/**
 * whatsApp页面
 */
const val ROUTER_WHATS_APP_SCHEME = "video/whatsapp"

/**
 * 雷达二级页
 */
const val ROUTER_RADAR_SCHEME = "home/radar"

/**
 * web搜索页面(带前进后退那个)
 */
const val ROUTER_NET_SEARCH = "net/search"

/*---------------- Action ----------------*/

/**
 * 行为 - 显示上传文件弹窗
 */
const val ACTION_UPLOAD: String = "action/upload"

/**
 * 行为 - 显示自动备份弹窗
 */
const val ACTION_AUTO_BACKUP: String = "action/autobackup"

/**
 * 分享文件，进入文件 Tab 且为编辑态
 */
const val ACTION_SHARE_EDIT: String = "action/shareedit"

/**
 * 进入文件tab Tab 且为音乐分类
 */
const val ACTION_OPEN_AUDIO: String = "action/openAudio"

/**
 * 打开离线 tab
 */
const val ACTION_OFFLINE: String = "action/offline"

/**
 * 打开设置页面
 */
const val ACTION_SETTING: String = "action/setting"

/**
 * 打开大图预览的行为
 */
const val ACTION_VIEW_IMAGE: String = "view/image"

/**
 * 打开拍照上传
 */
const val ACTION_CAPTURE_UPLOAD:String = "capture/upload"

/**
 * 福利中心
 */
const val ACTION_BONUS_CENTER:String = "bonus/center"

/**
 * 清理
 */
const val ACTION_CLEAN: String = "clean"

/**
 * 打开个人中心
 */
const val ACTION_USER_CENTER: String = "user/center"

/**
 * 打开资源圈详情列表
 */
const val ACTION_RESOURCE: String = "action/resource"

/**
 * 跳转到首页底部快捷tab---资源圈
 */
const val ACTION_SHORTCUT_RESOURCE: String = "action/shortcut/resource"

/**
 * 打开外链转存页
 */
const val ACTION_CHAIN_INFO: String = "action/chain/info"

/**
 * 打开资源圈tab后，默认操作（列如打开某个二级页面）
 */
const val ACTION_PARAMS: String = "action_params"

/**
 * Shorts 播放进度
 */
const val PROGRESS: String = "shorts_progress"

/**
 * 小组页跳转到二级页 tab
 */
const val ACTION_GROUP_SWITCH_TO_EXPAND: String = "action/group/switch/to/expand"

/**
 * 小组页跳转到发现 tab
 */
const val ACTION_GROUP_SWITCH_TO_DISCOVER: String = "action/group/switch/to/discover"
/*--------------------------------------路由参数相关-------------------------------------*/
/**
 * 埋点对应的 KEY
 */
const val LOG_FROM: String = "log_from"

/**
 * 埋点对应的参数
 */
const val LOG_OTHER0: String = "other0"

/**
 * cloudFile 对应的 fsid 参数
 */
const val EXTRA_PARAMS_FSID: String = "params_fsid"

/**
 * 页面跳转之后的行为
 */
const val EXTRA_PARAMS_ACTION: String = "action"

/**
 * 矩阵APP跳转协议：
 * moder://tab/home?app=tereclean
 * 解析app参数，不为空则埋点
 */
const val EXTRA_PARAMS_APP: String = "app"

/**
 * 跳转搜索页时传递的提示词参数
 */
const val EXTRA_PARAMS_SEARCH_HINT: String = "extra_params_search_hint"

/**
 * 资源圈详情跳转参数
 */
const val EXTRA_PARAMS_SHARE_ID: String = "shareId"

/**
 * 资源圈Feed页面跳转参数
 */
const val EXTRA_PARAMS_SHARE_EXT_ID: String = "extId"
const val EXTRA_PARAMS_SHARE_RES_TYPE: String = "resType"
const val EXTRA_PARAMS_SHARE_HOT_ORDER_TYPE: String = "hotOrderType"
const val EXTRA_PARAMS_SHARE_TITLE: String = "title"

/**
 * 路由携带的url
 */
const val ROUTER_PARAMS_KEY_URL = "url"

/**
 * 打开承接页需要的分享链接
 */
const val ROUTER_PARAMS_KEY_LINK = "link"

/**
 * 打开书签页
 */
const val ROUTER_BOOKMARK_KEY = "home/bookmark"