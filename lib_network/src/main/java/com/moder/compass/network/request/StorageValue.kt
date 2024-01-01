package com.moder.compass.network.request

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.moder.compass.BaseApplication
import com.dubox.drive.kernel.architecture.config.GlobalConfig
import com.dubox.drive.kernel.util.TimeUtil
import java.io.BufferedOutputStream
import kotlin.reflect.KProperty

/**
 * @Author 陈剑锋
 * @Time 2023/02/23 11:37
 * @Desc 持久化数据 代理封装
 *
 * @property key String                         存储键
 * @property defaultValue T                     默认值
 * @property logSetValueTime Boolean            是否记录上一次更新值的时间（默认 false）
 * @property isSameDayValue Boolean             是否仅当日有效的值（即次日失效。默认：false，此值为true时，logSetValueTime设置无效，强制为true）
 */
@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
class StorageValue<T>(
    private val key: String,
    private val defaultValue: T,
    private var logSetValueTime: Boolean = false,
    private val isSameDayValue: Boolean = false
) : Delegate<T> {

    init {
        if (isSameDayValue && !logSetValueTime) logSetValueTime = true
    }

    private val globalConfig: GlobalConfig by lazy { GlobalConfig.getInstance() }

    private val KEY_LAST_UPDATE_TIME = "${key}_last_set_value_time"
    var lastSetValueTime: Long
        get() {
            return globalConfig.getLong(KEY_LAST_UPDATE_TIME)
        }
        private set(value) {
            globalConfig.putLong(KEY_LAST_UPDATE_TIME, value)
        }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (isSameDayValue && !TimeUtil.isToday(lastSetValueTime)) {
            return defaultValue
        }
        return when (defaultValue) {
            is Boolean -> {
                globalConfig.getBoolean(key, defaultValue as Boolean)
            }
            is Long -> {
                globalConfig.getLong(key, defaultValue as Long)
            }
            is Int -> {
                globalConfig.getInt(key, defaultValue as Int)
            }
            is String -> {
                globalConfig.getString(key, defaultValue as String)
            }
            is Float -> {
                globalConfig.getFloat(key, defaultValue as Float)
            }
            is Parcelable -> {
                BaseApplication.getContext().getParcelable(key, defaultValue as Parcelable)
            }
            else -> {
                throw IllegalArgumentException("StorageValue: only support type Boolean, Long, Int, String, Float")
            }
        } as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (logSetValueTime) {
            lastSetValueTime = System.currentTimeMillis()
        }
        kotlin.runCatching {
            when (value) {
                is Boolean -> {
                    globalConfig.putBoolean(key, value)
                }
                is Long -> {
                    globalConfig.putLong(key, value)
                }
                is Int -> {
                    globalConfig.putInt(key, value)
                }
                is String -> {
                    globalConfig.putString(key, value)
                }
                is Float -> {
                    globalConfig.putFloat(key, value)
                }
                is Parcelable -> {
                    BaseApplication.getContext().putParcelable(key, value)
                }
                else -> {
                    throw IllegalArgumentException("StorageValue: only support type Boolean, Long, Int, String, Float")
                }
            }
        }
    }
}

/**
 * 获取Parcelable类型数据
 */
inline fun <reified T : Parcelable> Context.getParcelable(key: String, default: T): T {
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
fun Context.putParcelable(key: String, data: Parcelable) {
    kotlin.runCatching {
        val fos = openFileOutput(key, Context.MODE_PRIVATE)
        val bos = BufferedOutputStream(fos)
        val parcel = Parcel.obtain()
        parcel.writeParcelable(data, 0)
        bos.write(parcel.marshall())
        bos.flush()
        bos.close()
        fos.flush()
        fos.close()
    }
}

/**
 * 非空代理
 * @param T
 */
interface Delegate<T> {
    /**
     * 取值
     * @param thisRef Any?
     * @param property KProperty<*>
     * @return T
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T

    /**
     * 赋值
     * @param thisRef Any?
     * @param property KProperty<*>
     * @param value T
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

/**
 * 可空代理
 * @param T
 */
interface DelegateNullable<T> {
    /**
     * 取值
     * @param thisRef Any?
     * @param property KProperty<*>
     * @return T?
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T?

    /**
     * 赋值
     * @param thisRef Any?
     * @param property KProperty<*>
     * @param value T?
     */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?)
}