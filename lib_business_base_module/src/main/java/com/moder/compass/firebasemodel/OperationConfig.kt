package com.moder.compass.firebasemodel

import com.google.gson.annotations.SerializedName

data class OperationConfig(
    @SerializedName("country")
    val country: String = "",
    @SerializedName("url")
    val url: String = "",
    @SerializedName("icon_url")
    val iconUrl: String = "",
)
