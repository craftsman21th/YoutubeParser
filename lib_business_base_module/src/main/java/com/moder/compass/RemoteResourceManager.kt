package com.moder.compass

import android.content.Context
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.moder.compass.util.ExternalStorageUtil
import com.mars.united.international.dynamicso.download.DownloadAction
import com.mars.united.international.dynamicso.download.DownloadProgressListener
import com.mars.united.international.dynamicso.download.Downloader
import java.io.File

/**
 * 用作远程资源下载的管理类
 * 存储在cacheDir下，可以避免重复下载
 * @param context
 * @param url
 * */
class RemoteResourceManager public constructor(
    private val context: Context,
    private val url: String,
) {
    private var loading: Boolean = false
    private var listener: ResourceLoadListener? = null
    private val downloader: DownloadAction by lazy {
        Downloader(
            url,
            rootFilePath,
            tempFileKey,
            downloadListener
        )
    }
    private val rootFilePath by lazy {
        ExternalStorageUtil().getCacheDir().absolutePath
    }

    private val fileKey by lazy {
        com.mars.united.international.dynamicso.download.calculateMD5(url)
    }
    private val tempFileKey by lazy {
        "$fileKey.temp"
    }

    private val downloadListener: DownloadProgressListener by lazy {
        object : DownloadProgressListener {
            override fun onError(error: Throwable) {
                loading = false
                DuboxLog.d(TAG, "onError: ${error.message}")

                listener?.onLoadError(error)
            }

            override fun onPause() {
                DuboxLog.d(TAG, "onPause: ")
            }

            override fun onProgress(progress: Int) {
            }

            override fun onResume() {
                DuboxLog.d(TAG, "onResume: ")
            }

            override fun onSuccess(file: File, fileMd5: String) {
                DuboxLog.d(TAG, "onSuccess: ${file.absolutePath} and fileMd5:$fileMd5")
                loading = false
                listener?.onLoadSuccess(renameFile(file, fileKey))
            }
        }
    }

    fun renameFile(downloadTempFile: File, md5: String): File {
        val destFile = File(downloadTempFile.parentFile, md5)
        return if (downloadTempFile.renameTo(destFile)) {
            destFile
        } else {
            downloadTempFile
        }
    }


    fun tryLoad(l: ResourceLoadListener? = null) {
        this.listener = l
        if (!loading) {
            val tagFile = File(rootFilePath, fileKey)
            if (tagFile.exists()) {
                DuboxLog.d(TAG, "tryLoad: checkRemoteFile pass success and init complete")
                listener?.onLoadSuccess(tagFile)
            } else {
                deleteTempFile()
                val rootFile = File(rootFilePath)
                if (!rootFile.exists()){
                    rootFile.mkdirs()
                }
                DuboxLog.d(TAG, "tryLoad: checkRemoteFile pass fail and start download")
                loading = true
                downloader.download()
            }
        }
    }

    private fun deleteTempFile(){
        val tempFile = File(rootFilePath, tempFileKey)
        if (tempFile.exists()) {
            tempFile.deleteRecursively()
        }
    }


    fun destroy() {
        loading = false
        deleteTempFile()
    }

    companion object {
        private const val TAG = "RemoteResourceManager"

    }


}