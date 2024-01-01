package com.moder.compass.ui.share.utils

import android.app.Activity
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.moder.compass.ui.share.ShareOption
import java.io.BufferedOutputStream

/**
 * 获取Parcelable类型数据
 */
inline fun <reified T : Parcelable> Activity.getParcelableOrNull(key: String, default: T?): T? {
    var data: T? = null
    kotlin.runCatching {
        val fis = openFileInput(key)
        val bytes = ByteArray(fis.available())
        fis.read(bytes)
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0)
        data = parcel.readParcelable(classLoader)
        fis.close()
    }
    return data ?: default
}

/**
 * 存储Parcelable类型数据
 */
fun Activity.putParcelable(key: String, data: Parcelable) {
    kotlin.runCatching {
        val fos = openFileOutput(key, Context.MODE_PRIVATE)
        val bos = BufferedOutputStream(fos)
        val parcel = Parcel.obtain()
        parcel.writeParcelable(data, 0)
        bos.write(parcel.marshall())
        runCatching {
            bos.flush()
        }
        runCatching {
            bos.close()
        }
        runCatching {
            fos.flush()
        }
        runCatching {
            fos.close()
        }
    }
}