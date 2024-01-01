/*
 * Version.java
 * classes : Version
 * @author 
 * V 1.0.0
 * Create at 2012-10-22 下午1:37:26
 */
package com.moder.compass.versionupdate.io.model;

import android.os.Parcelable
import com.dubox.drive.network.base.Response
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * 检验是否升级的实体
 * 数据成员名称使用_等是为了对应到response里的字段，完成自动解析，请别重命名 create at 2012-10-22 下午1:37:26
 */
@Parcelize
class Version(
              @JvmField
              val version: String? = null,
              @JvmField
              val title: String? = null,
              @JvmField
              val detail: String? = null,
              @JvmField
              val url: String? = null,
              /** 序列化之后的名称，为了对应server的json格式和程序的变量字段对应上 author:cyq  */
              @SerializedName("force_update")
              @JvmField
              val forceUpdate: Int = 0,
              @SerializedName("version_code")
              @JvmField
              val versionCode: Int = 0,
              /** 升级的apk文件的正确md5  */
              @SerializedName("md5")
              @JvmField
              val md5: String? = null) : Response(), Parcelable