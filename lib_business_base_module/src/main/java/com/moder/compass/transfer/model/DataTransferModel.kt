/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.transfer.model

/**
 * @author: 曾勇
 * date: 2021-08-13 15:13
 * e-mail: zengyong01@moder.com
 * desc: 下载表数据迁移model
 */
data class DataTransferModel(
        val taskId: Int,
        val localUrl: String,
        val remoteUrl: String,
        val fileSize: Long,
        val fileMd5: String,
        val transmitterType: String,
        val priority: Int,
        val fileName: String,
        val p2pGid: String? = null
)

/**
 * 上传表数据迁移model
 */
data class UploadTaskTransferModel(val taskId: Int,
                                   val localUrl: String)