/**
 * 
 */
package com.moder.compass.sns.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 系统方法的包装类，系统的有些方法会有bug或异常，可以在这里包装一下
 * @author zhushiyu01
 * @since 2015-07-28
 * @functionModule CommonLib基础
 */
public final class SysMethodUtils {

    /**
     * constructor
     */
    private SysMethodUtils() {
    }

    /**
     * 获取Active的网络信息
     * @param context Context
     * @return networkInfo
     */
    public static NetworkInfo getActiveNetworkInfoSafely(Context context) {

        NetworkInfo info = null;
        try {
            Context appContext = context.getApplicationContext();
            ConnectivityManager manager = (ConnectivityManager) appContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            info = manager.getActiveNetworkInfo();
        } catch (Exception e) {
            if (DuboxLog.isDebug()) {
                e.printStackTrace();
            }
        }
        return info;
    }
}
