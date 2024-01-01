package com.moder.compass.util

import android.os.Environment
import com.moder.compass.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


/**
 * @Author 陈剑锋
 * @Date 2023/9/1-17:38
 * @Desc
 */
class ExternalStorageUtil {

    private val context by lazy {
        BaseApplication.getContext()
    }

    /**
     * 获取外部文件路径（同步）
     * @param subFolderName String
     * @return File?
     */
    fun getExternalFilesDir(subFolderName: String?): File? {
        return with(
            File(
                Environment.getExternalStorageDirectory().path +// /storage/emulated/0
                        "/Android/data" +
                        "/" + context.packageName +
                        if (subFolderName.isNullOrBlank()) {
                            ""
                        } else {
                            "/$subFolderName"
                        }
            )
        ) {
            if (exists()) {
                this
            } else if (kotlin.runCatching { mkdirs() }.getOrNull() == true) {
                this
            } else {
                BaseApplication.getContext().getExternalFilesDir(subFolderName)
            }
        }
    }

    /**
     * 获取外部文件路径（异步）
     * @param subFolderName String
     * @return File?
     */
    suspend fun getExternalFilesDirAsync(subFolderName: String): File? = withContext(Dispatchers.IO) {
        getExternalFilesDir(subFolderName)
    }

    /**
     * 获取外部缓存路径（同步）
     * @return File?
     */
    fun getExternalCacheDir(): File? {
        return with(
            File(
                Environment.getExternalStorageDirectory().path +// /storage/emulated/0
                        "/Android/data" +
                        "/" + context.packageName +
                        "/cache"
            )
        ) {
            if (exists()) {
                this
            } else if (kotlin.runCatching { mkdirs() }.getOrNull() == true) {
                this
            } else {
                BaseApplication.getContext().externalCacheDir
            }
        }
    }

    /**
     * 获取外部缓存路径（异步）
     * @return File?
     */
    suspend fun getExternalCacheDirAsync(): File? = withContext(Dispatchers.IO) {
        getExternalCacheDir()
    }

    /**
     * 获取缓存路径（同步）
     * @return File?
     */
    fun getCacheDir(): File {
        return with(
            File(
                "/data/user/0" +
                        "/" + context.packageName +
                        "/cache"
            )
        ) {
            if (exists()) {
                this
            } else if (kotlin.runCatching { mkdirs() }.getOrNull() == true) {
                this
            } else {
                BaseApplication.getContext().cacheDir
            }
        }
    }

    /**
     * 获取缓存路径（异步）
     * @return File?
     */
    suspend fun getCacheDirAsync(): File = withContext(Dispatchers.IO) {
        getCacheDir()
    }

}