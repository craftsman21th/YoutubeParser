/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.p2p

import com.cocobox.library.FileCallback
import com.moder.compass.BaseApplication
import com.moder.compass.getAvailableSizeByPath
import com.moder.compass.isPartitionStorage
import com.dubox.drive.kernel.util.rFile
import com.moder.compass.transfer.TransferFileNameConstant
/**
 * @author: 曾勇
 * date: 2021-08-09 11:16
 * e-mail: zengyong01@moder.com
 * desc:
 */
private const val DELETE_SUCCESS = 0
private const val DELETE_FAIL = -1
private const val MEMORY_BASE = 1024

internal class P2PTarget30FileProcess : FileCallback {


    override fun onFileExist(uri: String?): Boolean {
        // p2p下载不下载预览文件，所以android11该处返回的是文件的uri；
        // 低于android11的返回的是文件path， 为了解决2.5.0覆盖安装，暂停的任务重启下载会从0开始，
        // 所以传入文件不带后缀的路径，而老版本是通过p2p sdk内部自己创建的. 临时文件：原路径+ .dubox.p.downloading
        // 所以低版本判断文件的时候需要在p2p回调的uri后面自己拼个后缀
        val rFile = if (isPartitionStorage()) {
            uri.rFile()
        } else {
            val tempPath = uri + TransferFileNameConstant.P2P_DOWNLOAD_SUFFIX
            tempPath.rFile()
        }
        return rFile.exists()
    }

    override fun onOpenFile(uri: String, mode: String): Int {
        val rFile = if (isPartitionStorage()) {
            uri.rFile()
        } else {
            val tempPath = uri + TransferFileNameConstant.P2P_DOWNLOAD_SUFFIX
            tempPath.rFile()
        }
        return rFile.fd(BaseApplication.getContext(), mode)?.detachFd() ?: -1
    }

    override fun onRemoveFile(uri: String?): Int {
        val rFile = if (isPartitionStorage()) {
            uri.rFile()
        } else {
            val tempPath = uri + TransferFileNameConstant.P2P_DOWNLOAD_SUFFIX
            tempPath.rFile()
        }
        return if (rFile.delete(BaseApplication.getContext())) {
            DELETE_SUCCESS
        } else {
            DELETE_FAIL
        }
    }

    override fun onDiskSpareSpace(uri: String?): Long {
        return getAvailableSizeByPath(uri) / MEMORY_BASE
    }

}
