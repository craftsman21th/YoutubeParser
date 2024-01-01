package com.moder.compass.sns.util;

import static android.content.Context.NOTIFICATION_SERVICE;

import java.lang.reflect.Method;
import java.util.Locale;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * 提供一些常用工具方法.
 *
 * @functionModule CommonLib基础
 */
public final class Utility {
    /**
     * Log TAG.
     */
    private static final String TAG = "Utility";

    /**
     * 域名前缀
     */
    public static final String HTTP_SCHEME = "http://";
    /**
     * 域名前缀
     */
    public static final String HTTPS_SCHEME = "https://";

    /**
     * 私有构造函数.
     */
    private Utility() {

    }

    /**
     * 获取通知栏管理类
     */
    public static NotificationManager safetyGetNotificationManager() {
        Context context = BaseApplication.getInstance();
        try {
            Object manager = context.getSystemService(NOTIFICATION_SERVICE);
            return (NotificationManager) manager;
        } catch (Exception e) {
            return null;
        } catch (NoSuchMethodError error) {
            return null;
        }
    }

    /**
     * 跟UI相关的工具类
     */
    public static final class UIUtility {
        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         *
         * @param context context
         * @param dpValue dpValue
         * @return px
         */
        public static int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f); // SUPPRESS CHECKSTYLE
        }
    }

    /**
     * 网络相关的工具类
     */
    public static final class NetUtility {

        /**
         * 检查当前是否有可用网络
         *
         * @param context Context
         * @return true 表示有可用网络，false 表示无可用网络
         */
        public static boolean isNetWorkEnabled(Context context) {
            if (context == null || context.getApplicationContext() == null){
                return false;
            }
            try {
                ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivity != null) {
                    NetworkInfo info = connectivity.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        // 当前网络是连接的
                        if (info.getState() == NetworkInfo.State.CONNECTED) { // 当前所连接的网络可用
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                if (DuboxLog.isDebug()) {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }

        /**
         * 判断当前网络类型是否是wifi
         *
         * @param context Context
         * @return true 是wifi网络，false 非wifi网络
         */
        public static boolean isWifiNetWork(Context context) {
            String networktype = "NotAvaliable";
            NetworkInfo networkinfo = SysMethodUtils.getActiveNetworkInfoSafely(context);
            if (networkinfo != null && networkinfo.isAvailable()) {
                if (DuboxLog.isDebug()) {
                    DuboxLog.d(TAG, "netWorkInfo: " + networkinfo);
                }
                networktype = networkinfo.getTypeName().toLowerCase();
                if (networktype.equalsIgnoreCase("wifi")) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 检查当前是否有可用网络
         *
         * @param context Context
         * @return true 表示有可用网络，false 表示无可用网络
         */
        public static boolean isNetworkConnected(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) { // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED) { // 当前所连接的网络可用
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 文件相关的工具类
     */
    public static final class FileUtility {
        /**
         * 根据大小值返回转义过的文件大小和单位
         *
         * @param number  long型数据
         * @param shorter true:
         * @return 含有数字和单位的长度为2的数组
         */
        public static String[] toFileSize(long number, boolean shorter) {
            float result = number;
            String suffix = "B";
            if (result > 900) { //  SUPPRESS CHECKSTYLE
                suffix = "KB";
                result = result / 1024; //  SUPPRESS CHECKSTYLE
            }
            if (result > 900) { //  SUPPRESS CHECKSTYLE
                suffix = "MB";
                result = result / 1024; //  SUPPRESS CHECKSTYLE
            }
            if (result > 900) { //  SUPPRESS CHECKSTYLE
                suffix = "GB";
                result = result / 1024; //  SUPPRESS CHECKSTYLE
            }
            if (result > 900) { //  SUPPRESS CHECKSTYLE
                suffix = "TB";
                result = result / 1024; //  SUPPRESS CHECKSTYLE
            }
            if (result > 900) { //  SUPPRESS CHECKSTYLE
                suffix = "PB";
                result = result / 1024; //  SUPPRESS CHECKSTYLE
            }
            String value;
            if (result < 1) {
                value = String.format(Locale.getDefault(), "%.2f", result);
            } else if (result < 10) { //  SUPPRESS CHECKSTYLE
                if (shorter) {
                    value = String.format(Locale.getDefault(), "%.1f", result);
                } else {
                    value = String.format(Locale.getDefault(), "%.2f", result);
                }
            } else if (result < 100) { //  SUPPRESS CHECKSTYLE
                if (shorter) {
                    value = String.format(Locale.getDefault(), "%.1f", result);
                } else {
                    value = String.format(Locale.getDefault(), "%.2f", result);
                }
            } else {
                value = String.format(Locale.getDefault(), "%.0f", result);
            }
            return new String[]{value, suffix};
        }
    }

    /**
     * 跟通知栏相关的工具类
     */
    public static final class NotificationUtility {
        /**
         * 收起通知栏
         *
         * @param ctx context
         */
        @SuppressWarnings("WrongConstant")
        public static void collapseStatusBar(Context ctx) {
            Object sbservice = ctx.getSystemService("statusbar");
            try {
                Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
                Method collapse;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    collapse = statusBarManager.getMethod("collapsePanels");
                } else {
                    collapse = statusBarManager.getMethod("collapse");
                }
                collapse.invoke(sbservice);
            } catch (Exception e) {
                if (DuboxLog.isDebug()) {
                    e.printStackTrace();
                }
            }
        }
    }
}
