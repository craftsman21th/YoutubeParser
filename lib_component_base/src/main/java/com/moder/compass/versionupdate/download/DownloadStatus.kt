package com.moder.compass.versionupdate.download

import java.io.File

/**
 * 文件下载进度处理
 */
sealed class DownloadStatus {
    /**
     * 下载进度
     */
    data class Progress(val value: Int) : DownloadStatus()

    /**
     * 下载错误 & 失败
     */
    data class Failed(val throwable: Throwable?) : DownloadStatus()

    /**
     * 下载成功
     */
    data class Success(val file: File?) : DownloadStatus()
}