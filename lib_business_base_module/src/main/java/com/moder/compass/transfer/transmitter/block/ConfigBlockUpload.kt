package com.moder.compass.transfer.transmitter.block

import com.moder.compass.base.utils.KEY_BACKUP_CONCURRENCE_LIMIT
import com.moder.compass.firebase.DuboxRemoteConfig
import com.dubox.drive.kernel.architecture.config.ServerConfig
import com.moder.compass.transfer.task.MultiTaskScheduler
import com.moder.compass.util.CONCURRENT_UPLOAD_MAX_POOL_SIZE
import com.moder.compass.util.CONCURRENT_UPLOAD_SWITCH
import rubik.generate.context.dubox_com_dubox_drive_vip.VipContext

/**
 * 分片并发上传的配置信息
 */
object ConfigBlockUpload {

    /**
     * 分片并发开关是否打开
     */
    val switch: Boolean by lazy {
        DuboxRemoteConfig.getLong(CONCURRENT_UPLOAD_SWITCH) == 1L
    }

    /**
     * 是否开启分片并发上传
     */
    fun enable() = switch && VipContext.isVip() ?: false

    /**
     * 上传task并发数
     */
    private val taskLimitCount: Int by lazy {
        val taskLimitCount = ServerConfig.getInt(KEY_BACKUP_CONCURRENCE_LIMIT)
        taskLimitCount.coerceAtLeast(MultiTaskScheduler.DEFAULT_MULTI_TASK_COUNT)
    }

    /**
     * 最大（默认）并发数量
     */
    val maxPoolSize: Int  by lazy {
       DuboxRemoteConfig
            .getLong(CONCURRENT_UPLOAD_MAX_POOL_SIZE).toInt().coerceAtLeast(defaultPoolSize)
    }

    /**
     * 默认并发数量
     */
    val defaultPoolSize: Int by lazy {
        taskLimitCount * 2
    }

    /**
     * 最小并发数
     */
    val minPoolSize: Int by lazy {
        taskLimitCount
    }
}