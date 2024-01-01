package com.moder.compass.util

import android.annotation.SuppressLint
import android.os.Build
import com.moder.compass.statistics.REPORT_SIZE_CONFIGURATIONS_EXCEPTION
import com.moder.compass.statistics.statisticViewEvent
import com.mars.kotlin.extension.e
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy


/**
 * android 10、11 有大量reportSizeConfigurations 报 ActivityRecord not found的case
 * 其本质是UI线程有阻塞性任务导致的activity无法创建，根据调研结果高度怀疑service，也不排除其他阻塞
 * 临时修复方案hook
 * 最终解决方案合并到卡顿优化中一起解
 */
private const val ANDROID_R = 30

@SuppressLint("PrivateApi")
        /**
         * 修复ReportSizeConfigurations异常
         */
fun fixReportSizeConfigurationsException() {
    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q && Build.VERSION.SDK_INT != ANDROID_R) {
        return
    }
    try {
        val activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager")
        val activityManager: Field = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton")
        activityManager.isAccessible = true
        val iActivityManagerSingleton: Any = activityManager.get(null) ?: return
        val singletonCls = iActivityManagerSingleton.javaClass.superclass ?: return
        val instance: Field = singletonCls.getDeclaredField("mInstance")
        instance.isAccessible = true
        val getMethod: Method = singletonCls.getMethod("get")
        getMethod.isAccessible = true
        val iActivityManager: Any = getMethod.invoke(iActivityManagerSingleton) ?: return
        val iActivityManagerCls = Class.forName("android.app.IActivityTaskManager")
        val classes = arrayOf(iActivityManagerCls)
        val iActivityManageProxy: Any = Proxy.newProxyInstance(
            iActivityManagerCls.classLoader,
            classes,
            IActivityManagerProxy(iActivityManager)
        )
        instance.set(iActivityManagerSingleton, iActivityManageProxy)
    } catch (e: Exception) {
        e.e()
    }
}


/**
 * 动态代理处理 try catch  ATMS
 * ActivityTaskManager#reportSizeConfigurations()方法 10.0
 */
class IActivityManagerProxy(private val mIActivityManager: Any) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        return if ("reportSizeConfigurations" == method?.name) {
            try {
                if (args == null) {
                    method.invoke(mIActivityManager)
                } else {
                    method.invoke(mIActivityManager, *args)
                }
            } catch (e: Exception) {
                e.e()
                statisticViewEvent(REPORT_SIZE_CONFIGURATIONS_EXCEPTION)
            }
        } else {
            try {
                if (args == null) {
                    method?.invoke(mIActivityManager)
                } else {
                    method?.invoke(mIActivityManager, *args)
                }
            } catch (e: Exception) {
                e.e()
            }
        }
    }
}
