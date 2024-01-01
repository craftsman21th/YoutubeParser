package com.moder.compass.statistics

import android.content.Intent
import android.text.TextUtils
import com.moder.compass.base.recordForegroundLaunch

/**
 * @author sunmeng
 * create at 2021-09-10
 * Email: sunmeng12@moder.com
 */

/**
 * 点击 Push 的埋点
 */
fun logPushClick(intent: Intent) {
    // 添加 Push 判断逻辑
    val from = intent.getStringExtra(EXTRA_FROM) ?: ""
    if (TextUtils.equals(from, FROM_SERVER)) {
        val time = intent.getLongExtra(EXTRA_TIME, 0L)
        if (time == 0L) {
            return
        }
        intent.putExtra(EXTRA_TIME, 0L)
    }
    var msgId = intent.getStringExtra(EXTRA_MSG_ID) ?: MSG_ID_DEFAULT
    if (TextUtils.isEmpty(msgId)) {
        msgId = MSG_ID_DEFAULT
    }
    statisticActionEvent(PUSH_DID_CLICK_NOTIFICATION, msgId)
    recordForegroundLaunch(PUSH_DID_CLICK_NOTIFICATION)
}

/**
 * 展示 Push 通知的埋点
 */
fun logPushShow(data: Map<String, String>) {
    var msgId = data[EXTRA_MSG_ID] ?: MSG_ID_DEFAULT
    if (TextUtils.isEmpty(msgId)) {
        msgId = MSG_ID_DEFAULT
    }
    statisticActionEventNow(PUSH_MESSAGE_SHOW_NOTIFICATION, msgId)
}

/**
 * 收到 Firebase Push 的埋点
 */
fun logReceivePush(intent: Intent) {
    var msgId = intent.getStringExtra(EXTRA_MSG_ID) ?: MSG_ID_DEFAULT
    if (TextUtils.isEmpty(msgId)) {
        msgId = MSG_ID_DEFAULT
    }
    statisticActionEventNow(PUSH_DID_RECEIVE_NOTIFICATION, msgId)
}

/**
 * 接收到静默 Push
 */
fun logReceiveSilentPush(data: Map<String, String>) {
    var msgId = data[EXTRA_MSG_ID] ?: MSG_ID_DEFAULT
    if (TextUtils.isEmpty(msgId)) {
        msgId = MSG_ID_DEFAULT
    }
    statisticActionEventNow(RECEIVE_SILENT_PUSH, msgId)
}

/**
 * 静默 Push 唤起备份
 */
fun logSilentPushWeakUpBackup(data: Map<String, String>) {
    var msgId = data[EXTRA_MSG_ID] ?: MSG_ID_DEFAULT
    if (TextUtils.isEmpty(msgId)) {
        msgId = MSG_ID_DEFAULT
    }
    statisticActionEventNow(SILENT_PUSH_WEAK_UP_BACKUP, msgId)
}

/**
 * Push 推送定义消息ID 的 key
 */
const val EXTRA_MSG_ID = "msg_id"

/**
 * 如果通知不包含 msg_id 则默认为老版 firebase 推送
 */
private const val MSG_ID_DEFAULT = "firebase_push"

/**
 * 视频播放
 */
const val MSG_ID_VIDEO_PLAY = "video_play"

/**
 * 自动备份文件数量变更
 */
const val MSG_ID_BACKUP_UPDATE = "backup_update"

/**
 * 网络状态变化的通知
 */
const val MSG_ID_NETWORK_CHANGE = "network_change"

/**
 * 正在上传
 */
const val MSG_ID_UPLOADING = "uploading"

/**
 * 上传完成
 */
const val MSG_ID_UPLOAD_COMPLETE = "upload_complete"

/**
 * 正在下载
 */
const val MSG_ID_DOWNLOADING = "downloading"

/**
 * 下载完成
 */
const val MSG_ID_DOWNLOAD_COMPLETE = "download_complete"

/**
 * 提示开启相册备份
 */
const val MSG_ID_PHOTO_BACKUP = "photo_backup"

/**
 * 离线任务完成通知
 */
const val MSG_ID_OFFLINE_COMPLETE = "offline_complete"

/**
 * Push 推送定义来源的 KEY
 */
const val EXTRA_FROM = "push_from"

/**
 * Push 添加时间戳，防止出现重复上报点击的情况
 */
const val EXTRA_TIME = "extra_time"

/**
 * 根据 from == FROM_SERVER 区分是老版推送还是新版推送
 */
const val FROM_SERVER = "moderServerPush"

/**
 * 来自本地 push
 */
const val FROM_LOCAL_PUSH: String = "from_local_push"

/**
 * 提示添加 widget
 */
const val MSG_ID_ADD_WIDGET = "add_widget"

/**
 * 提示进入视频 tab
 */
const val MSG_ID_ENTER_VIDEO_TAB = "enter_video_tab"

/**
 * 提示进入相册 tab
 */
const val MSG_ID_ENTER_ALBUM_TAB = "enter_album_tab"

/**
 * 提示清理空间
 */
const val MSG_ID_CLEAN_STORAGE = "clean_storage"


