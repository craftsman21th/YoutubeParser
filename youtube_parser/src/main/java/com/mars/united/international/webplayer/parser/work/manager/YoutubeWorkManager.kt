package com.mars.united.international.webplayer.parser.work.manager

import android.os.Handler
import android.os.Looper
import com.mars.united.international.webplayer.parser.work.base.IYoutubeWork
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @Author 陈剑锋
 * @Date 2023/8/1-10:45
 * @Desc
 */
object YoutubeWorkManager {

    val mainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val waitingForEnvWorkList: CopyOnWriteArrayList<IYoutubeWork> = CopyOnWriteArrayList()

    const val DEFAULT_PAGE_COUNT: Int = -1

    fun onEnvInitSuccess() {
        waitingForEnvWorkList.forEach { work ->
            work.onEnvReady()
        }
        waitingForEnvWorkList.clear()
    }

    fun onEnvInitFailed() {
        waitingForEnvWorkList.forEach { work ->
            work.onEnvInitFailed()
        }
        waitingForEnvWorkList.clear()
    }

    /**
     * 添加等待环境初始化完成的任务
     * @param work IYoutubeWork
     */
    fun addWaitingWork(work: IYoutubeWork): Boolean {
        return waitingForEnvWorkList.add(work)
    }

    /**
     * 添加等待环境初始化完成的任务
     * @param work IYoutubeWork
     */
    fun removeWaitingWork(work: IYoutubeWork): Boolean {
        return waitingForEnvWorkList.remove(work)
    }

}