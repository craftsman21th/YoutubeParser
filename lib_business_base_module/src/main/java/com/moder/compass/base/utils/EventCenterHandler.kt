package com.moder.compass.base.utils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import java.lang.ref.WeakReference

/**
 * @author sunmeng
 * create at 2022-01-12
 * Email: sunmeng12@moder.com
 * 替换 MessageUtil
 */
object EventCenterHandler : Handler.Callback {

    private val handler = Handler(Looper.getMainLooper(), this)
    private val eventHandlers = arrayListOf<IEventHandler>()

    // 遍历中移除会导致崩溃，所以在遍历中时不移除
    private var waitRemoveHandler: IEventHandler? = null
    private var inIterator: Boolean = false

    /**
     * 发送消息, 参数对应 Message 中的各个参数
     */
    fun sendMsg(
        what: Int, arg1: Int = 0,
        arg2: Int = 0, data: Bundle? = null,
        delayMillis: Long = 0L, obj: Any? = null
    ) {
        val msg = handler.obtainMessage()
        msg.what = what
        msg.arg1 = arg1
        msg.arg2 = arg2
        if (data != null) {
            msg.data = data
        }
        msg.obj = obj
        handler.sendMessageDelayed(msg, delayMillis)
    }

    /**
     * 针对 Java 的特俗处理
     */
    fun sendMsg(what: Int, arg1: Int, arg2: Int = 0, data: Bundle? = null) {
        sendMsg(what, arg1, arg2, data, 0L, null)
    }

    /**
     * 真的 Java 的特殊处理
     */
    fun sendMsg(what: Int) {
        sendMsg(what, 0, 0, null)
    }

    /**
     * remove message
     */
    fun removeMsg(what: Int) {
        handler.removeMessages(what)
    }

    /**
     * has message
     */
    fun hasMsg(what: Int) = handler.hasMessages(what)

    /**
     * 添加到列表中
     */
    fun registerHandler(handler: IEventHandler) {
        if (eventHandlers.contains(handler)) {
            return
        }
        eventHandlers.add(handler)
    }

    /**
     * 从列表中移除
     */
    fun removeHandler(handler: IEventHandler) {
        if (inIterator) {
            DuboxLog.d("EventCenterHandler", "remove handle while Iterator")
            waitRemoveHandler = handler
        } else {
            eventHandlers.remove(handler)
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        inIterator = true
        val size = eventHandlers.size
        // Kotlin For 循环出现 ConcurrentModificationException
        var h: IEventHandler
        for (i in size - 1 downTo 0) {
            h = eventHandlers.getOrNull(i)?:continue
            if (h != waitRemoveHandler && h.messageFilter(msg.what)) {
                h.handleMessage(msg)
            }
        }
        inIterator = false
        if (waitRemoveHandler != null) {
            eventHandlers.remove(waitRemoveHandler)
        }
        return true
    }
}

/**
 * 具体事件的处理类
 */
interface IEventHandler {

    /**
     * 处理 Message
     */
    fun handleMessage(message: Message)

    /**
     * 过滤事件
     */
    fun messageFilter(what: Int): Boolean

}

/**
 * 实现软引用的 IEventHandler
 */
abstract class WeakReferenceEventHandler<T>(t: T) : IEventHandler {
    private val reference = WeakReference(t)

    override fun handleMessage(message: Message) {
        val ref: T = reference.get() ?: return
        handleMessage(ref, message)
    }

    /**
     * 消息处理
     */
    abstract fun handleMessage(reference: T, message: Message)
}
/**
 * 外链页自动保存视频后播放，进入视频播放页弹出 toast
 */
const val MESSAGE_SHARE_LINK_AUTO_SAVE_PLAY_VIDEO_SHOW_TOAST: Int = 501
/**
 * 进度相关消息
 */
const val MESSAGE_UPLOAD_PROGRESS = 100
const val MESSAGE_DOWNLOAD_PROGRESS = 101
const val MESSAGE_UPLOAD_UPDATE = 102
const val MESSAGE_DOWNLOAD_UPDATE = 103
const val MESSAGE_BACKUP_UPDATE = 104
const val MESSAGE_BACKUP_PROGRESS = 105

/**
 * 预览
 */
const val MESSAGE_PREVIEW_UPDATE = 106

/**
 * 通知压缩图片
 */
const val MESSAGE_COMPRESS_IMAGE = 107

const val MESSAGE_UPLOAD_SUCCESS = 108
const val MESSAGE_BACKUP_SUCCESS = 109

/**
 * 通知小说上传完成
 */
const val MESSAGE_UPLOAD_NOVEL_SUCCESS = 110

/**
 * 完成分享的消息通知
 * 分享邮件：点击发送按钮
 * 其他分享：生成外链
 */
const val MESSAGE_SHARE_SUCCESS = 111

const val MESSAGE_FINISH = 200
const val MESSAGE_LOGIN_EXPIRE = 201
const val MESSAGE_ACCOUNT_ERROR = 403

/** 刷新设置界面程序锁提示item 请保留5000-6000 YQH 20130220  */
const val MESSAGE_SEETTING_FRESH_CODEDLOCK = 5001

/** 主页根据排列方式刷新列表YQH 20130528  */
const val MESSAGE_MAIN_REFRESH_SORT = 5008

/** 列表全文件名刷新 YQH 20131121  */
const val MESSAGE_LIST_FULLANME_REFRESH = 5011

/** 传输列表重新加载刷新 YQH 20131121  */
const val MESSAGE_TRANSFER_LIST_RELOAD = 5012

/** 通知显示下载会员引导  */
const val MESSAGE_DOWNLOAD_SHOW_GUIDE = 5050
const val MESSAGE_UPLOAD_LARGET_SHOW_GUIDE = 5051
const val MESSAGE_SHARE_RESOURCE_SEARCH_UPDATE = 5052

const val MESSAGE_IMAGE_LIST_SCROLL_POSITION = 5013
const val MESSAGE_TIMELINE_IMAGE_PREVIEW_RECT = 5014

const val MESSAGE_NO_NETWORK = 5015

/**
 * 设置夜间模式变化通知
 */
const val MESSAGE_SETTING_NIGHT_MODE = 5016

/**
 * 文件列表进入编辑态事件
 */
const val MESSAGE_EDIT_MODEL: Int = 5017

/**
 * 检查本地 push 是否满足条件
 */
const val CHECK_ADD_WIDGET_PUSH: Int = 301
const val CHECK_ENTER_VIDEO_TAB_PUSH: Int = 302
const val CHECK_ENTER_ALBUM_TAB_PUSH: Int = 303
const val CHECK_CLEAN_STORAGE_PUSH: Int = 304

/**
 * message bundle 参数
 */
const val MESSAGE_EXTRA_DATA: String = "message_extra_data"

/**
 * 打开相册自动备份
 */
const val MESSAGE_OPEN_ALBUM_AUTO_BACKUP: Int = 300

/**
 * 开启保险箱功能
 */
const val MESSAGE_OPEN_SAFE_BOX: Int = 5018

/**
 * 查看一张照片
 */
const val MESSAGE_OPEN_IMAGE_PREVIEW: Int = 5019

/**
 * 播放一个视频
 */
const val MESSAGE_OPEN_VIDEO_PREVIEW: Int = 5020

/**
 * 转存文件成功的消息
 */
const val MESSAGE_OPEN_REDEPOSIT_FILE: Int = 5021

/**
 * 播放资源圈视频
 */
const val MESSAGE_PLAY_RESOURCE_VIDEO: Int = 5025

/**
 * 转存资源圈视频
 */
const val MESSAGE_RESOURCE_VIDEO_SAVE: Int = 5026

/**
 * 分享资源圈视频
 */
const val MESSAGE_RESOURCE_VIDEO_SHARE: Int = 5027

/**
 * 点赞资源圈视频
 */
const val MESSAGE_RESOURCE_VIDEO_LIKE: Int = 5028

/**
 * 触发视频下载器搜索
 */
const val MESSAGE_OPEN_VIDEO_SEARCH: Int = 5029

/**
 * 在线播放音乐
 */
const val MESSAGE_PLAY_AUDIO: Int = 5030

/**
 * 多选状态文件操作完成后发送消息
 */
const val MESSAGE_DELETE_FILES: Int = 1234
const val MESSAGE_RENAME_FILE: Int = 1235
const val MESSAGE_MOVE_FILES: Int = 1236
const val MESSAGE_COPY_FILES: Int = 1237
// 点击上传图片
const val MESSAGE_CLICK_UPLOAD_PHOTO: Int = 2001
// 点击分享
const val MESSAGE_CLICK_SHARE: Int = 2002
// 新手引导重新加载
const val MESSAGE_RELOAD_URL: Int = 2003
// 点击资源圈tab
const val MESSAGE_CLICK_RESOURCE_SQUARE: Int = 2005
// 开始新手任务引导
const val MESSAGE_START_NEWBIE_TASK: Int = 2006
// 中断新手任务引导，通知此事件是为了移除首页的引导弹窗以展示其他的orderDialog
const val MESSAGE_STOP_NEWBIE_TASK: Int = 2007

/**
 * 外链页转存单视频完成后打开视频，返回时跳转视频所在文件夹
 */
const val MESSAGE_TARGET_FILE_WHEN_VIDEO_PLAYER_FINISH: Int = 500

/**
 * 首页-tab快速吸顶
 */
const val MESSAGE_HOME_CARD_SCROLL_TO_TOP = 3000

/**
 * 首页-floatingActionButton上移
 */
const val MESSAGE_HOME_FLOATING_BUTTON_UP = 3001

/**
 * 首页-floatingActionButton下移
 */
const val MESSAGE_HOME_FLOATING_BUTTON_DOWN = 3002


val MAIN_ACTIVITY_MESSAGE_LIST = arrayOf(
        MESSAGE_UPLOAD_SUCCESS, MESSAGE_BACKUP_SUCCESS,
        MESSAGE_UPLOAD_UPDATE, MESSAGE_DOWNLOAD_SHOW_GUIDE,
        MESSAGE_SHARE_SUCCESS, MESSAGE_SETTING_NIGHT_MODE,
        MESSAGE_BACKUP_UPDATE, MESSAGE_OPEN_ALBUM_AUTO_BACKUP,
        MESSAGE_OPEN_SAFE_BOX, MESSAGE_DOWNLOAD_UPDATE,
        MESSAGE_OPEN_IMAGE_PREVIEW, MESSAGE_OPEN_VIDEO_PREVIEW,
        MESSAGE_OPEN_REDEPOSIT_FILE, MESSAGE_OPEN_VIDEO_SEARCH,
        MESSAGE_PLAY_RESOURCE_VIDEO, MESSAGE_RESOURCE_VIDEO_SAVE,
        MESSAGE_RESOURCE_VIDEO_SHARE, MESSAGE_RESOURCE_VIDEO_LIKE,
        MESSAGE_PLAY_AUDIO, MESSAGE_CLICK_UPLOAD_PHOTO,
        MESSAGE_CLICK_RESOURCE_SQUARE, MESSAGE_START_NEWBIE_TASK, MESSAGE_STOP_NEWBIE_TASK
)

