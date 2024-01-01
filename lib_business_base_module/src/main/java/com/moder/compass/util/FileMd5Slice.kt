/*
 * Copyright (C) 2021 moder, Inc. All Rights Reserved.
 */
package com.moder.compass.util

import android.os.SystemClock
import com.dubox.drive.kernel.BaseShellApplication
import com.dubox.drive.kernel.util.RFile
import com.dubox.drive.kernel.util.encode.HexUtil
import com.mars.kotlin.extension.Tag
import com.mars.kotlin.extension.e
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest
import java.util.zip.CRC32
import kotlin.math.ceil

/**
 * 分片计算MD5时的buffer大小，经测试 16K大小性能表现比较好，再加大buffer大小性能提升不明显
 *
 * @author 孙奇 V 1.0.0 Create at 2013-2-20 下午11:57:10
 */
private const val CALL_MD5_SIZE: Int = 1024 * 16

// 大于等于256K的文件前面256K算SliceMd5
private const val SLICE_SIZE: Long = 256 * 1024.toLong()

/**
 * 重试读取文件时间间隔
 */
private const val READ_RETRY_DELAY: Int = 500

/**
 * 文件 md5 切片
 */
@Tag("FileMd5Slice")
class FileMd5Slice {

    /**
     * 重试读取文件的次数
     */
    private var reReadNum = 0

    /**
     * 获取分片的文件的md5，按4M分片和服务器保持一致
     *
     * @param fileSize
     * @param localFile
     * @return
     */
    fun create(fileSize: Long, localFile: RFile): List<String> {
        val allMd5List = arrayListOf<String>()
        val dynamicUploadBlockSize = getSize(fileSize)
        val block = ceil(fileSize.toDouble() / dynamicUploadBlockSize).toInt()
        "fileSize.toDouble(): ${fileSize.toDouble()} , " + "dynamicUploadBlockSize:$dynamicUploadBlockSize ,ceil:${ceil(fileSize.toDouble() / dynamicUploadBlockSize)}, result: $block".e()
        val buffer = ByteArray(CALL_MD5_SIZE)

        // 为了每4M算一次MD5,4M除以的到的值
        val blockDivCount: Long = dynamicUploadBlockSize / CALL_MD5_SIZE
        // 大于等于256K的文件前面256K算SliceMd5
        val sliceDivCount = SLICE_SIZE / CALL_MD5_SIZE
        var inputStream: BufferedInputStream? = null
        var digester: MessageDigest? // 分片MD5计算器
        var sliceDigester: MessageDigest? // 前256K MD5计算器
        var contentDigester: MessageDigest? // 全文MD5计算器
        var contentCrc32: CRC32? // crc计算器
        try {
            digester = MessageDigest.getInstance("MD5")
            sliceDigester = MessageDigest.getInstance("MD5")
            contentDigester = MessageDigest.getInstance("MD5")
            contentCrc32 = CRC32()
            inputStream = BufferedInputStream(localFile.inputStream(BaseShellApplication.getContext()), CALL_MD5_SIZE)
            for (i in 0 until block) {
                // 4M算一次MD5
                for (j in 0 until blockDivCount) {
                    val blockLen = (if ((blockDivCount * i + (j + 1)) * CALL_MD5_SIZE > fileSize) (fileSize - (blockDivCount * i + j) * CALL_MD5_SIZE).toInt() else CALL_MD5_SIZE)
                    inputStream.read(buffer, 0, blockLen)
                    digester.update(buffer, 0, blockLen)
                    contentDigester.update(buffer, 0, blockLen)
                    if (i == 0 && j < sliceDivCount) sliceDigester.update(buffer, 0, blockLen)
                    contentCrc32.update(buffer, 0, blockLen)
                    if ((blockDivCount * i + (j + 1)) * CALL_MD5_SIZE > fileSize) break
                }
                allMd5List.add(HexUtil.toHexLowerCaseString(digester.digest()))
            }
        } catch (e: FileNotFoundException) {
            e.message.e()
        } catch (e: IOException) {
            "IOException : allMd5List clear".e()
            allMd5List.clear()
            // 发生异常，重试两次，间隔一秒
            reReadNum++
            while (reReadNum <= 2) {
                SystemClock.sleep(READ_RETRY_DELAY.toLong())
                create(fileSize, localFile)
            }
            e.message.e()
        } catch (e: Exception) {
            e.message.e()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.localizedMessage.e()
            }
        }
        return allMd5List
    }
}