package com.mars.united.international.webplayer.parser.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.dubox.drive.kernel.util.INT_0
import com.google.gson.annotations.SerializedName
import com.mars.united.international.webplayer.parser.utils.checkKeyParamsValid
import kotlinx.android.parcel.Parcelize

/**
 * @Author 陈剑锋
 * @Date 2023/7/28-10:32
 * @Desc 关键信息
 */
@Keep
@Parcelize
data class KeyInfo(
    @SerializedName("decrypt_method")
    val decryptMethod: String = "",
    @SerializedName("yt_config")
    val ytConfig: String = "",
    @SerializedName("signature_timestamp")
    val signatureTimestamp: Int = INT_0,
) : Parcelable {

    fun isValid(): Boolean {
        return (decryptMethod.checkKeyParamsValid()
                && ytConfig.checkKeyParamsValid()
                && signatureTimestamp != INT_0)
    }

}