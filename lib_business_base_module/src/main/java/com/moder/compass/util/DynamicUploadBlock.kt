/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.util

/**
 * 文件大小，1G
 */
private const val FILE_SIZE_1G: Long = 1024 * 1024 * 1024 * 1.toLong()

/**
 * 文件大小，4G
 */
private const val FILE_SIZE_4G: Long = 1024 * 1024 * 1024 * 4.toLong()

/**
 * 文件大小，8G
 */
private const val FILE_SIZE_8G: Long = 1024 * 1024 * 1024 * 8.toLong()

/**
 * 文件大小，16G
 */
private const val FILE_SIZE_16G: Long = 1024 * 1024 * 1024 * 16.toLong()

/**
 * 文件大小，32G
 */
private const val FILE_SIZE_32G: Long = 1024 * 1024 * 1024 * 32.toLong()

/**
 * 文件上传分片大小(4G)
 */
const val UPLOAD_BLOCK_SIZE: Long = 1024 * 1024 * 4.toLong()

/**
 * 文件上传分片大小(8G以下)
 */
private const val UPLOAD_BLOCK_SIZE_8G: Long = 1024 * 1024 * 8.toLong()

/**
 * 文件上传分片大小(16G以下)
 */
private const val UPLOAD_BLOCK_SIZE_16G: Long = 1024 * 1024 * 16.toLong()

/**
 * 文件上传分片大小(32G以下)
 */
private const val UPLOAD_BLOCK_SIZE_32G: Long = 1024 * 1024 * 32.toLong()

/**
 * 动态上传文件块
 */
fun getSize(fileSize:Long):Long {
    return when (fileSize) {
        in 1 until FILE_SIZE_4G -> {
            UPLOAD_BLOCK_SIZE
        }
        in FILE_SIZE_4G until FILE_SIZE_8G -> {
            UPLOAD_BLOCK_SIZE_8G
        }
        in FILE_SIZE_8G until FILE_SIZE_16G -> {
            UPLOAD_BLOCK_SIZE_16G
        }
        in FILE_SIZE_16G until FILE_SIZE_32G -> {
            UPLOAD_BLOCK_SIZE_32G
        }
        else -> UPLOAD_BLOCK_SIZE
    }
}