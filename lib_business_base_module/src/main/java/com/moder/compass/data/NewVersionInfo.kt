package com.moder.compass.data

import com.google.gson.annotations.SerializedName

/**
 *
 * @author huping05
 * @since moder 2022/11/25
 */
data class NewVersionInfo(
    @SerializedName("version")
    val version: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("update_content")
    val updateContent: List<String>?,
    @SerializedName("update_content_en")
    val updateContentEN: List<String>?,
    @SerializedName("update_content_hi")
    val updateContentHI: List<String>?,
    @SerializedName("update_content_in")
    val updateContentIN: List<String>?,
    @SerializedName("update_content_ja")
    val updateContentJA: List<String>?,
    @SerializedName("update_content_ko")
    val updateContentKO: List<String>?,
    @SerializedName("update_content_ru")
    val updateContentRU: List<String>?,
    @SerializedName("update_content_th")
    val updateContentTH: List<String>?,
    @SerializedName("update_content_es")
    val updateContentES: List<String>?,
    @SerializedName("update_content_ar")
    val updateContentAR: List<String>?,
    @SerializedName("update_content_pt")
    val updateContentPT: List<String>?,
    @SerializedName("update_content_vi")
    val updateContentVI: List<String>?,

    @SerializedName("image_urls")
    val imageUrls: List<String>?
)