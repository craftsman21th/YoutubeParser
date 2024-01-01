package com.moder.compass.model

import android.webkit.URLUtil
import com.moder.compass.account.Account
import com.google.gson.annotations.SerializedName

/**
 * @Author 陈剑锋
 * @Date 2023/10/20-10:53
 * @Desc 站长中心tab配置
 */
data class EarnTabConfig(
    // 根据需求，本地默认关闭状态
    @SerializedName("enable")
    val remoteEnable: Boolean = false,
    @SerializedName("icon")
    val icon: String = "",
    @SerializedName("page_url")
    val pageUrl: String = "",
    @SerializedName("country")
    val supportedCountry: List<String> = listOf()
) {

    val enable: Boolean
        get() {
            return remoteEnable
                    && URLUtil.isValidUrl(pageUrl)
                    && supportedCountry.map { it.lowercase().trim() }.contains(Account.regCountry.lowercase().trim())
        }

}