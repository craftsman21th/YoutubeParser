package com.mars.united.international.webplayer.common.repo

import com.moder.compass.network.request.StorageValue
import com.mars.united.international.webplayer.parser.utils.JsonCaller
import com.mars.united.international.webplayer.parser.utils.asString
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import com.mars.united.international.webplayer.parser.utils.get
import java.lang.ref.WeakReference

private const val TAG: String = "CommonRepo"

/**
 * @Author 陈剑锋
 * @Date 2023/8/24-16:38
 * @Desc 公共仓库
 */
class CommonRepo {

    companion object {

        private var instanceRef: WeakReference<CommonRepo> = WeakReference(null)

        /**
         * 获取实例
         * @return CommonRepo
         */
        fun get(): CommonRepo {
            return instanceRef.get() ?: CommonRepo().also {
                instanceRef = WeakReference(it)
            }
        }

    }

    private var _ytConfig: String by StorageValue(
        key = "${TAG}_ytConfig",
        defaultValue = "",
        logSetValueTime = false,
        isSameDayValue = true
    )

    val ytConfig: JsonCaller?
        get() {
            return JsonCaller.create(_ytConfig)
        }

    val apiKey get() = ytConfig["INNERTUBE_API_KEY"].asString

    var subscribeKey: String by StorageValue(
        key = "${TAG}_subscribeKey",
        defaultValue = "",
        logSetValueTime = true,
        isSameDayValue = false
    )

    var unsubscribeKey: String by StorageValue(
        key = "${TAG}_unsubscribeKey",
        defaultValue = "",
        logSetValueTime = true,
        isSameDayValue = false
    )

    /**
     * 更新 ytConfig
     * @param newYtConfig String
     */
    fun updateYtConfig(newYtConfig: String) {
        _ytConfig = newYtConfig
    }

    /**
     * 检查 ytConfig 是否有效
     * @return Boolean
     */
    fun checkYtConfigValid(): Boolean {
        return _ytConfig.checkKeyParamsValid()
    }

}