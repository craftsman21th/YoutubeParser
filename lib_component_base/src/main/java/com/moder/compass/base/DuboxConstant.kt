package com.moder.compass.base

import com.dubox.drive.kernel.architecture.config.PersonalConfig
import com.mars.united.core.util.putSafe
import org.json.JSONObject

/**
 * @author sunmeng
 * create at 2021-11-12
 * Email: sunmeng12@baidu.com
 */

/**
 * token 常量
 */
const val EXTRA_TOKEN = "token"


/**
 * 文件常量
 */
const val EXTRA_CLOUD_FILE = "cloud_file"

/**
 * 索引常量
 */
const val EXTRA_INDEX: String = "index"

/***
 * 离线下载相关错误码
 */

// 任务名称无效，请修改后再试
const val UPLOAD_ERROR_TASK_INVALID: Int = 36001
// 没有访问权限，任务添加失败
const val UPLOAD_ERROR_WITHOUT_PERMISSION: Int = 36004
// 存储空间已满，任务添加失败
const val UPLOAD_ERROR_WITHOUT_SPACE: Int = 36009
// 请求超时，请稍后再试
const val UPLOAD_ERROR_TIMEOUT: Int = 36012
// 进行中任务较多，请稍后再试
const val UPLOAD_ERROR_HAVE_TOO_MANY_TASK: Int = 36013
// 添加任务频繁过快，请稍后再试
const val UPLOAD_ERROR_ADD_TOO_MUCH_TASK: Int = 36031
// 应用权方通知无法下载
const val UPLOAD_ERROR_DOWNLOAD_NO_PERMISSION: Int = 36038
// 其他
const val UPLOAD_ERROR_OTHER: Int = -2

/**
 * widget类型
 */
const val WIDGET_TYPE: String = "widget_type"

/**
 * 更新 Widget 的广播，用于在 AppInit 中绑定 WidgetService
 */
const val ACTION_WIDGET_UPDATE_BROAD_CAST: String = "action_widget_update_broad_cast"

/**
 * Widget 更新完的广播，用于在 AppInit 中解绑
 */
const val ACTION_WIDGET_UPDATE_FINISH: String = "action_widget_update_finish"

/**
 * 上报通知栏存在的本地广播
 */
const val ACTION_KEEP_ACTIVE_NOTIFICATION: String = "action_keep_active_notification"

/**
 * 后台保活时触发常驻通知栏判断
 */
const val ACTION_BACKGROUND_TODAY_REPORT: String = "action_background_today_report"

/****************资源圈来源***************/

/**
 * 资源圈详情页来源 - 首页卡片
 */
const val SHARE_FROM_HOME_CARD: Int = 0

/**
 * 资源圈详情页来源 - 视频Tab
 */
const val SHARE_FROM_VIDEO_TAB: Int = 1

/**
 * 资源圈详情页来源 - 资源圈内点击
 */
const val SHARE_FROM_INNER: Int = 2

/**
 * 资源圈详情页来源 - 搜索
 */
const val SHARE_FROM_SEARCH: Int = 3

/**
 * 资源圈详情页来源 - 消息中心
 */
const val SHARE_FROM_MESSAGE: Int = 4

/**
 * 资源圈详情页来源 - 资源承接页
 */
const val SHARE_FROM_RESOURCE: Int = 5

/**
 * 资源圈详情页来源 - 资源承接页
 */
const val SHARE_FROM_PUSH: Int = 6

/**
 * 福利中心Url 对应的 KEY
 */
private const val WELFARE_CENTER_LINK_URL = "welfare_center_linkUrl"

/**
 * @since TeraBox 3.3 福利中心Url改为 server 下发，所以需要移除原先 firebase 下发的逻辑
 */
fun getWelfareCenterUrl(): String {
    return PersonalConfig.getInstance().getString(
        WELFARE_CENTER_LINK_URL,
        "https://www.terabox.com/wap/commercial/taskcenter")
}

/**
 * 更新福利中心 Url 地址
 */
fun updateWelfareCenterUrl(url: String) {
    PersonalConfig.getInstance().putString(WELFARE_CENTER_LINK_URL, url)
}

/**
 * 外链页 setResult 时转存成功文件的 key
 */
const val EXTRA_CHAIN_INFO_SAVED_FILES: String = "extra_chain_info_saved_files"

/****************** 外链链路额外参数 json 格式 key 值 *******************************/
const val SHARE_LINK_EXTRA_PARAM_KEY_URL: String = "url"
const val SHARE_LINK_EXTRA_PARAM_FROM_KEY: String = "share_link_extra_param_from_key"
const val SHARE_LINK_EXTRA_PARAM_KEY_SHARE_ID: String = "shareId"
const val SHARE_LINK_EXTRA_PARAM_KAY_FROM_PATH: String = "pageFrom" // 标识页面来源，链路层级由','分割
const val SHARE_LINK_EXTRA_PARAM_KEY_TOPIC_ID: String = "topicId" // 标识专题来源

/**
 * 来源常量
 */
const val FROM_HIVE: String = "from_hive"
/**
 * 统一获取外链链路额外参数 json 字符串
 */
fun getExtraParams(url: String? = "", from: String? = "", map: Map<String, String>? = null): String {
    val extraParams = JSONObject()
    extraParams.putSafe(SHARE_LINK_EXTRA_PARAM_KEY_URL, url)
    extraParams.putSafe(SHARE_LINK_EXTRA_PARAM_FROM_KEY, from)
    map?.entries?.forEach {
        extraParams.putSafe(it.key, it.value)
    }
    return extraParams.toString()
}
