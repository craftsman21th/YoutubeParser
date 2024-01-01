
package com.moder.compass.database

import android.content.ContentResolver
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.*
import com.dubox.drive.kernel.BaseShellApplication
import com.dubox.drive.kernel.architecture.job.BaseJob
import com.dubox.drive.common.scheduler.TaskSchedulerImpl

import com.mars.kotlin.extension.Tag
import com.mars.united.core.debug.assertWhenLog
import com.mars.united.core.debug.printStackTraceWhenLog
import com.mars.united.core.util.thread.isUIThread

private const val START_TASK = 1
private const val UPDATE_VALUE = 2
private const val GAP_TIME = 500L
private const val GAP_TIME_LOW_DEVICE = 800L
/**
 * 自带Cursor监听的LiveData
 * @param gapTime 查询间隙
 */
@Tag("CursorLiveData")
class CursorLiveData<T>(
    private val parser: (Cursor) -> T,
    private val gapTime: Long = if (BaseShellApplication.getContext().lowDeviceTag) GAP_TIME_LOW_DEVICE else GAP_TIME,
    private val customDebugTag: String = "",
    private val extraNotifyUrisInfo: Pair<ContentResolver, List<Uri>>? = null,
    private val getCursor: () -> Cursor?) : MutableLiveData<T>(), LifecycleEventObserver {

    /**
     * 用户部分场景下忽略数据的变更
     */
    @Volatile
    var ignoreChange: Boolean = false
        set(value) {
            field = value
            if (needUpdate) {
                startLoadTask()
            }
        }

    private val handler = object: Handler(Looper.getMainLooper()) {
        override fun dispatchMessage(msg: Message) {
            if (msg.what == START_TASK) {
                if (ignoreChange || mRunningTaskId != null) {
                    mIsAwaiting = true
                    return
                }
                mIsAwaiting = false
                // 添加异步任务
                mRunningTaskId = TaskSchedulerImpl.addHighTask(CursorTask())

            } else if (msg.what == UPDATE_VALUE) {
                @Suppress("UNCHECKED_CAST")
                val result = msg.obj as? Pair<Cursor?, T?>
                assertWhenLog( {result != null} )
                val cursor = result?.first
                val data = result?.second
                if (cursor != null) {
                    updateResult(cursor, data)
                }
                mRunningTaskId = null
                if (needUpdate) {
                    startLoadTask(gapTime)
                }
            }
        }
    }
    private var mCursor: Cursor? = null
    /**
     * 记录忽略结果过程中忽略结果，需要记录下次恢复是否需要主动查询一次
     */
    private var mIsAwaiting = true
    /**
     * 运行中的任务
     */
    private var mRunningTaskId: String? = null

    /**
     * 是否处于激活状态
     */
    private var isActive: Boolean = false

    /**
     * 是否可以触发startUpdate方法；
     */
    private val needUpdate: Boolean
        get() = mCursor == null || (!ignoreChange && mIsAwaiting)

    // 主线程执行
    private val mObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun deliverSelfNotifications(): Boolean {
            return true
        }

        override fun onChange(selfChange: Boolean) {
            if (!ignoreChange) {
                startLoadTask()
            } else { // 记录有新数据等待查询的状态
                mIsAwaiting = true
            }
        }
    }

    /**
     * 仅仅负责发送加载请求
     */
    private fun startLoadTask(time: Long? = null) {
        if (!hasActiveObservers()) {
            mIsAwaiting = true
            return
        }
        if (handler.hasMessages(START_TASK)) {
            return
        }
        if (time == null) {
            handler.sendEmptyMessage(START_TASK)
        } else {
            handler.sendEmptyMessageDelayed(START_TASK, time)
        }
    }

    /**
     * 初始化监听器；触发第一次查询
     */
    override fun onActive() {
        super.onActive()
        check(isUIThread())
        if (!isActive) {
            isActive = true
            extraNotifyUrisInfo?.second?.forEach {
                extraNotifyUrisInfo.first.registerContentObserver(it, true, mObserver)
            }
        }
        if (needUpdate) {
            startLoadTask()
        }
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        owner.lifecycle.addObserver(this)
    }

    override fun removeObserver(observer: Observer<in T?>) {
        super.removeObserver(observer)
        if (!hasObservers()) {
            destroy()
        }
    }

    /**
     * 回收资源，关闭监听
     */
    private fun destroy() {
        isActive = false
        // clear message
        handler.removeMessages(START_TASK)
        handler.removeMessages(UPDATE_VALUE)
        // 取消异步任务
        mRunningTaskId?.let {
            TaskSchedulerImpl.cancelTask(it)
        }
        mRunningTaskId = null
        // add content observer
        mCursor?.unregisterContentObserverSafe(mObserver)
        extraNotifyUrisInfo?.first?.unregisterContentObserver(mObserver)
        // close cursor
        if (mCursor?.isClosed != true) {
            mCursor?.close()
        }
        mCursor = null
    }

    /**
     * 更新值到observer
     */
    private fun updateResult(cursor: Cursor, result: T?) {
        if (ignoreChange) {
            cursor.close()
            mIsAwaiting = true // 记录下次恢复需要重新查询
        } else {
            // 存储上一个cursor
            mCursor?.unregisterContentObserverSafe(mObserver)
            cursor.registerContentObserver(mObserver)
            value = result
            if (mCursor?.isClosed != true) {
                mCursor?.close()
            }
            mCursor = cursor
        }
    }

    /**
     * 查询cursor和解析cursor
     */
    @Tag("CursorLiveData_CursorTask")
    private inner class CursorTask:
        BaseJob("CursorLiveData_${this@CursorLiveData.hashCode()}") {
        override fun performExecute() {
            try {
                val startTime = System.currentTimeMillis()
                val cursor = getCursor()
                assertWhenLog({ cursor?.isClosed != true }) { "parse should not close cursor"}
                if (isCancelled) {
                    cursor?.close()
                } else {
                    val count = cursor?.count // 请求一次count，在非ui线程提前加载好数据避免UI卡顿
                    val parseResult = cursor?.let(parser)
                    if (parseResult is Cursor) {
                        parseResult.count  // 请求一次count，在非ui线程提前加载好数据避免UI卡顿
                    }
                    handler.sendMessage(handler.obtainMessage(UPDATE_VALUE, cursor to parseResult))
                }
            } catch (e: Throwable) {
                e.printStackTraceWhenLog(customDebugTag)
            }
        }
    }

    /**
     * 首页卡片页面中使用了 CursorLiveData , 存在如下场景：
     * 切换 【图片 Tab】，点击预览图片，此时每预览一张图片，ContentObserver 就会通知发生变化，
     * 因为跳转到图片预览页时，MainActivity 只有 onPause 被调用，所以这个 LiveData 会通知
     * Fragment 中的 observer，主线程会执行很多和 UI 不相关逻辑
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.lifecycle.removeObserver(this)
            return
        }
        val temp = event != Lifecycle.Event.ON_RESUME
        // ignoreChange 赋值时会触发刷新判断不需要重复赋值
        if (temp != ignoreChange) {
            ignoreChange = temp
        }

    }
}

private fun Cursor.unregisterContentObserverSafe(observer: ContentObserver) {
    try { unregisterContentObserver(observer) } catch (ignore: Exception) { }
}
