/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.moder.compass.base.storage.config.Setting
import com.dubox.drive.cloudfile.base.IDownloadable
import com.dubox.drive.kernel.BaseShellApplication
import com.dubox.drive.kernel.android.util.storage.DeviceStorageManager
import com.dubox.drive.kernel.android.util.storage.DeviceStorageUtils
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.util.RFile
import com.moder.compass.statistics.TARGET30_CREATE_URI_ERROR
import com.moder.compass.statistics.TARGET30_RENAME_URI_ERROR
import com.moder.compass.statistics.statisticActionEvent
import com.moder.compass.transfer.TransferFileNameConstant
import com.moder.compass.transfer.util.TransferUtil
import com.mars.kotlin.extension.e
import com.mars.united.core.os.database.getStringOrNull
import java.io.File
import java.io.IOException
import java.net.URLConnection

/**
 * @author: 曾勇
 * date: 2021-07-30 15:32
 * e-mail: zengyong01@moder.com
 * desc: Target30分区存储相关
 */
const val EMPTY = ""
const val ROOT_DIR = "/"
const val DOWNLOAD_DIR: String = "Download" + Setting.DOWNLOAD_RELATIVE_DIR
const val DCIM_DIR: String = "DCIM/Box"


/**
 * 获取link文件下载路径
 * dlink下载时，download表remote_url添加的是下载路径,
 */
fun getLinkDownloadPath(context: Context, item: IDownloadable, bduss: String): String {
    return getLinkDownloadPath(context, item, bduss, "/")
}

fun getLinkDownloadPath(context: Context, item: IDownloadable, bduss: String, parentPath: String): String {
    if (isPartitionStorage()) {
        val existUris = queryDownloadUriStr(item.fileName, parentPath)
        val existUri = TransferUtil.getExistLocalUri(existUris, bduss)
        if (existUris.isNotEmpty() && existUri.isNullOrEmpty()) {
            // 说明系统Download存在该文件，但是表中没有，需要重新下载，
            // 新建uri前，需先重名系统Download下查询到文件名称
            existUris.forEach {
                renameToBackupFile(it, item.fileName)
            }
        } else if (!existUri.isNullOrEmpty()) {
            return existUri
        }
        return createDownloadUriStr(item.fileName, parentPath)
    }
    val defaultDir = Setting.getDefaultSaveDir(context)
    val filePath = when {
        item.fileName == null -> EMPTY
        item.fileName.startsWith(ROOT_DIR) -> item.fileName
        else -> "$ROOT_DIR${item.fileName}"
    }

    return "$defaultDir$parentPath$filePath"
}

/**
 * 重命名文件，在文件名称前添加back_up
 */
private fun renameToBackupFile(uriStr: String, fileName: String) {
    val values = ContentValues()
    val newName = TransferFileNameConstant.BACKUP_OLD_FILE_NAME + fileName
    values.put(MediaStore.Downloads.DISPLAY_NAME, newName)
    val uri = Uri.parse(uriStr)
    val result = runCatching {
        BaseShellApplication.getContext()
            .contentResolver.update(uri, values, null, null)
    }
    if (result.isFailure) {
        statisticActionEvent(TARGET30_RENAME_URI_ERROR, result.exceptionOrNull()?.message ?: "")
    }
}

/**
 * 获取普通文件下载路径
 */
fun getDownloadPath(
    context: Context, item: IDownloadable,
    bduss: String
): String {
    if (isPartitionStorage()) { // 大于等于Android29使用分区存储，返回uri对应路径
        TransferUtil.getLocalUriByRemoteUrl(item.filePath, bduss)?.let {
            return it
        }
        // 说明下载表中没有该文件，在创建新文件时，先根据文件名是否可以查询到该uri，如果能够查询到，先改名
        val existUris = queryDownloadUriStr(item.fileName, item.parent?.filePath)
        existUris.forEach {
            renameToBackupFile(it, item.fileName)
        }
        return createDownloadUriStr(item.fileName, item.parent?.filePath)
    }
    val path = Setting.getDefaultSaveDir(context)
    val filePath = when {
        item.filePath.startsWith(ROOT_DIR) -> item.filePath
        else -> "$ROOT_DIR${item.fileName}"
    }
    return "$path$filePath"
}

/**
 * 针对Target30，给要写入的文件创建uri
 * 如果下载表中已存在，则直接返回下载表中uri
 * 如果下载表中不存在，则重新创建
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun createDownloadUriStr(fileName: String, relativePath: String?): String {
    val values = ContentValues()
    val mimeType = URLConnection.guessContentTypeFromName(fileName)
    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
    values.put(MediaStore.Downloads.MIME_TYPE, mimeType)
    values.put(MediaStore.Downloads.RELATIVE_PATH, getParentPath(relativePath))
    val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    val result = runCatching {
        BaseApplication.getInstance().contentResolver.insert(external, values)?.toString() ?: ""
    }
    if (result.isFailure) {
        statisticActionEvent(TARGET30_CREATE_URI_ERROR, result.exceptionOrNull()?.message ?: "")
    }
    val inertUri = result.getOrDefault("")
    DuboxLog.d("createDownloadUriStr", "$values, uri: $inertUri")
    return inertUri
}

/**
 * 创建媒体文件
 */
fun createMediaUri(fileName: String, relativePath: String?): Uri? {
    val values = ContentValues()
    val mimeType = URLConnection.guessContentTypeFromName(fileName)
    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.RELATIVE_PATH, getMediaParentPath(relativePath))
    }
    val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    val inertUri = try {
        BaseApplication.getInstance().contentResolver.insert(external, values)
    } catch (e: Exception) {
        null
    }
    DuboxLog.d("createMediaUri", "$values, uri: $inertUri")
    return inertUri
}

/**
 * 根据名称到系统download表中查询是否存在该文件
 */
@RequiresApi(Build.VERSION_CODES.Q)
fun queryDownloadUriStr(fileName: String, parentPath: String?): List<String> {
    val uris = mutableListOf<String>()
    val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    val projection = arrayOf(MediaStore.Downloads._ID, MediaStore.Downloads.DATA)
    val selection = "${MediaStore.Downloads.DISPLAY_NAME}=?"
    val args = arrayOf(fileName)

    /**
     * 针对 Firebase
     * https://console.firebase.google.com/project/dubox-aba90/crashlytics/app/android:com.dubox.drive/issues
     * /426e90505869580f8b28d3c51c9d773a?time=last-seven-days&versions=2.13.2%20(140)&types=crash&
     * sessionEventKey=623580C701630001617A5608D86AE786_1655149670271330990
     *
     * 捕获所有异常，调用处会做 empty 判断
     */
    var cursor: Cursor? = null
    try {
        cursor = BaseApplication.getInstance().contentResolver.query(
            external, projection,
            selection, args, null, null
        )
    } catch (e: Exception) {
        e.e()
    }
    val parentPath = getParentPath(parentPath)
    while (cursor != null && cursor.moveToNext()) {
        val path = cursor.getStringOrNull(MediaStore.Downloads.DATA) ?: ""
        if (path.contains(parentPath)) {
            val id = cursor.getLong(0)
            val uri = ContentUris.withAppendedId(external, id).toString()
            uris.add(uri)
        }
    }
    cursor?.close()
    return uris
}

private fun getParentPath(relativePath: String?): String {
    val parentPath = when {
        relativePath == null -> EMPTY
        TextUtils.equals(relativePath, ROOT_DIR) -> EMPTY
        !relativePath.startsWith(ROOT_DIR) -> "$ROOT_DIR$relativePath"
        else -> relativePath
    }
    return "$DOWNLOAD_DIR$parentPath"
}

/**
 * 获取媒体目录
 */
private fun getMediaParentPath(relativePath: String?): String {
    val parentPath = when {
        relativePath == null -> EMPTY
        TextUtils.equals(relativePath, ROOT_DIR) -> EMPTY
        !relativePath.startsWith(ROOT_DIR) -> "$ROOT_DIR$relativePath"
        else -> relativePath
    }
    return "$DCIM_DIR$parentPath"
}

/**
 * 获取当前下载文件下载的大小
 * @localPathUri：Q及以上传入的是uri， 以下是文件保存的绝对路径
 * @tempDestinationPath: 下载中文件路径
 */
fun getCurrentDownloadSize(tempDestinationPath: RFile?, destinationPath: RFile, isPrivateDir: Boolean): Long {
    // 大于等于Android29使用分区存储，获取到uri对应的localPath,然后获取具体文件大小
    if (isPartitionStorage() && !isPrivateDir) {
        return destinationPath.length()
    }
    return tempDestinationPath?.length() ?: 0
}


/**
 * Target30不需要手动创建文件夹，如果路径，系统自动会创建文件路径
 * target30以下，需要创建临时文件
 * @param isPreview 是否是预览文件，预览文件存在app私有目录
 */
@Throws(IOException::class)
fun createTempFile(tempDestinationPath: RFile?, isPrivateDir: Boolean) {
    if (isPartitionStorage() && !isPrivateDir) return
    val path = tempDestinationPath?.localUrl() ?: ""
    val tempFile = File(path)
    tempFile.parentFile?.let {
        if (it.exists()) return@let
        it.mkdirs()
    }
    if (!tempFile.exists()) {
        tempFile.createNewFile()
    }
}

/**
 * 删除临时文件
 * @param tempDestinationPath 老文件系统，代表绝对路径的临时文件
 * @param destinationPath 保存的路径，target30以下表示绝对路径，
 * target30以上代表uri（下载成功前表示临时文件，下载成功后表示保存的的文件）
 * @param isPrivateDir 是否保持在私有目录
 */
fun deleteTempFile(tempDestinationPath: RFile?, destinationPath: RFile, isPrivateDir: Boolean) {
    if (isPartitionStorage() && !isPrivateDir) {
        destinationPath.delete(BaseApplication.getInstance())
    } else {
        tempDestinationPath?.delete(BaseApplication.getInstance())
    }
}


/**
 * 判断下载空间是否充足
 * @param needSpace
 * @param destinationPath
 * @param isPrivateDir  是否是私有目录
 */
fun isDownloadSpaceEnough(
    needSpace: Long, destinationPath: String?,
    isPrivateDir: Boolean
): Boolean {
    if (isPartitionStorage() && !isPrivateDir) { // target30判断下载目录下可用空间大小
        val storePath = Environment.getExternalStoragePublicDirectory(
            Environment
                .DIRECTORY_DOWNLOADS
        ).absolutePath
        return DeviceStorageUtils.isStorageEnough(needSpace, storePath)
    }
    val deviceStorageManager = DeviceStorageManager.createDevicesStorageManager(BaseApplication.getInstance())
    return if (deviceStorageManager.isInDefaultStorage(destinationPath)) {
        DeviceStorageUtils.isStorageEnough(needSpace, deviceStorageManager.defaultStoragePath)
    } else {
        DeviceStorageUtils.isStorageEnough(needSpace, deviceStorageManager.secondaryStoragePath)
    }
}

/**
 * 获取手机可用空间
 */
fun getAvailableSizeByPath(path: String?): Long {
    if (isPartitionStorage()) { // target30判断下载目录下可用空间大小
        val storePath = Environment.getExternalStoragePublicDirectory(
            Environment
                .DIRECTORY_DOWNLOADS
        ).absolutePath
        return DeviceStorageUtils.getAvailableSizeByPath(storePath)
    }
    val deviceStorageManager = DeviceStorageManager.createDevicesStorageManager(BaseApplication.getInstance())
    return if (deviceStorageManager.isInDefaultStorage(path)) {
        DeviceStorageUtils.getAvailableSizeByPath(deviceStorageManager.defaultStoragePath)
    } else {
        DeviceStorageUtils.getAvailableSizeByPath(deviceStorageManager.secondaryStoragePath)
    }
}

/**
 * 是否开启使用分区存储
 * 注意android10覆盖安装时，将会运行兼容模式
 */
fun isPartitionStorage(): Boolean {
//    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
//            && !Environment.isExternalStorageLegacy()
    return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q
}

/**
 * 适配target30及一下，文件重命名
 * target30以下使用tempDestinationPath ， destinationPath
 * target30以上使用uri , newName
 */
fun rename(tempDestinationPath: RFile?, destinationPath: RFile, isPrivateDir: Boolean): Boolean {
    val isUseOldStorageMode = !isPartitionStorage() || isPrivateDir
    if (isUseOldStorageMode && tempDestinationPath != null && destinationPath != null) {
        return tempDestinationPath.rename(destinationPath.name()) != null
    } else if (isPartitionStorage() && destinationPath != null) {
        return destinationPath.rename(destinationPath.name()) != null
    }
    return false
}