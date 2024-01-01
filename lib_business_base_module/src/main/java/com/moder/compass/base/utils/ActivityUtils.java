package com.moder.compass.base.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
/**
 * Created by huantong on 15/11/10.
 */
public class ActivityUtils {
    private static final String TAG = "ActivityUtils";

    /**
     * 查询网盘当前是否在前台
     * 
     * @param context
     * @return
     */
    public static boolean isDuboxForeground(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 获取正在运行的任务
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            return (topActivity != null && TextUtils.equals(topActivity.getPackageName(), context.getPackageName()));
        }
        return false;
    }

    /**
     * 设置状态栏主题
     *
     * @param activity activity
     */
    public static void setSystemBarStatus(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Window window = activity.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            //设置状态栏文字颜色及图标为深色
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            Window window = activity.getWindow();
            // 取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 设置状态栏颜色
            window.setStatusBarColor(statusBarColor);
            // 设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            // 让view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
            // android6.0以后可以对状态栏文字颜色和图标进行修改
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                // windowManager: Failed to deliver inset state change w=
                mChildView.setFitsSystemWindows(false);
//                ViewCompat.setFitsSystemWindows(mChildView, false);
//                ViewCompat.requestApplyInsets(mChildView);
            }
        }
    }


    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    public static void setTransparent(Activity activity) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 适配底部虚拟导航栏
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        View viewContent = activity.findViewById(android.R.id.content);
        if (viewContent == null || !(viewContent instanceof ViewGroup)) {
            return;
        }
        ViewGroup parent = (ViewGroup) viewContent;
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setLightMode(Activity activity) {
        if (activity == null){
            return;
        }
        setMIUIStatusBarDarkIcon(activity, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            activity.getWindow().setNavigationBarColor(Color.WHITE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setNavigationBarColor(Color.LTGRAY);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setDarkMode(Activity activity) {
        if (activity == null){
            return;
        }
        setMIUIStatusBarDarkIcon(activity, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        activity.getWindow().setNavigationBarColor(Color.BLACK);
    }

    /**
     * 修改 MIUI V6  以上状态栏颜色
     */
    private static void setMIUIStatusBarDarkIcon(@NonNull Activity activity, boolean darkIcon) {
        try {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkIcon ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage());
        }
    }


    /**
     * 是不是在栈顶
     *
     * @return 布尔
     */
    public static boolean isActivityOnTop() {
        ActivityManager activityManager =
                (ActivityManager) BaseApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo != null && tasksInfo.size() > 0) {
            ComponentName activity = tasksInfo.get(0).topActivity;
            if (activity == null) {
                return false;
            }
            DuboxLog.d(TAG, " AIU DBG isActivityOnTop activity:" + activity.getClassName() + " "
                    + "getPackageName:" + activity.getPackageName());
            if (AppCommon.PACKAGE_NAME.equals(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
