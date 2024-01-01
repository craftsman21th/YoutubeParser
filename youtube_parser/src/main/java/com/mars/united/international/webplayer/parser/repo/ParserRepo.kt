package com.mars.united.international.webplayer.parser.repo

import com.dubox.drive.kernel.util.INT_0
import com.moder.compass.network.request.StorageValue
import com.mars.united.international.webplayer.common.repo.CommonRepo
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asInt
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import com.mars.united.international.webplayer.parser.utils.get
import java.lang.ref.WeakReference

/**
 * @Author 陈剑锋
 * @Date 2023/7/28-14:49
 * @Desc 关键参数仓库
 */
class ParserRepo {

    companion object {

        private const val TAG: String = "YoutubeKeyInfo"

        private var instanceRef: WeakReference<ParserRepo> = WeakReference(null)

        /**
         * 获取实例
         * @return ParserRepo
         */
        fun get(): ParserRepo {
            return instanceRef.get() ?: ParserRepo().also {
                instanceRef = WeakReference(it)
            }
        }

    }

    private val commonRepo by lazy {
        CommonRepo.get()
    }

    var decryptMethod: String by StorageValue(
        key = "${TAG}_decryptMethod",
        defaultValue = "",
        logSetValueTime = false,
        // 该值仅当日有效
        isSameDayValue = true
    )
        private set

    var signatureTimestamp: Int by StorageValue(
        key = "${TAG}_signatureTimestamp",
        defaultValue = INT_0,
        logSetValueTime = false,
        // 该值仅当日有效
        isSameDayValue = true
    )
        private set

    /**
     * 更新 解密函数
     * @param newDecryptMethod String
     */
    fun updateDecryptMethod(newDecryptMethod: String) {
        decryptMethod = newDecryptMethod
    }

    /**
     * 更新解析签名时间戳
     * @param signatureTimestamp Int
     */
    fun updateSignatureTimestamp(newSignatureTimestamp: Int) {
        signatureTimestamp = newSignatureTimestamp
    }

    /**
     * 检查 是否有缓存
     * @return Boolean
     */
    fun check(): Boolean {
        return decryptMethod.checkKeyParamsValid() && commonRepo.checkYtConfigValid()
    }

    /**
     * 更新 关键信息
     * @param keyInfoRaw String
     */
    fun update(keyInfoRaw: String) {
        kotlin.runCatching {
            val json = JsonCaller.create(keyInfoRaw)
            updateDecryptMethod(json["decrypt_method"].asString ?: "")
            updateSignatureTimestamp(json["signature_timestamp"].asInt ?: 0)
            commonRepo.updateYtConfig(json["yt_config"].asString ?: "")
        }
    }

    /**
     * 清除 已存储的信息
     */
    fun clearDecryptMethod() {
        kotlin.runCatching {
            updateDecryptMethod("")
            commonRepo.updateYtConfig("")
        }
    }

}