package com.moder.compass;

import static com.moder.compass.statistics.StatisticsKeysKt.APP_ACTION_TIME_FOREGROUND;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.moder.compass.account.Account;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.EventStatisticsKt;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 网盘所有Activity生命周期管理类
 * <p>
 * Created by huantong on 2018/1/23.
 * 该类方法的回调托管给类 UniteActivityLifecycleCallbacks 所以有改动时需要在 UniteActivityLifecycleCallbacks
 * 中同步改动
 */

public class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivityLifecycleManager";

    private static int sFrontActivityCount;
    private static long mStartForegroundTime = 0;

    private static final List<Activity> ACTIVITIES = new CopyOnWriteArrayList<>();

    public static List<Activity> getAllActivities() {
        return ACTIVITIES;
    }

    private static List<AppVisibilityListener> appVisibilityListenerList = new CopyOnWriteArrayList<>();
    private static List<TopActivityChangeListener> activityChangeListeners = new CopyOnWriteArrayList<>();

    /**
     * 退出应用
     */
    public static void finishAll() {
        for (Activity ac : ACTIVITIES) {
            ac.finish();
        }
        DuboxLog.i(TAG, "--------finish all");
        ACTIVITIES.clear();
    }

    /**
     * 网盘是否在前台
     *
     * @return
     */
    public static boolean isDuboxForeground() {
        return sFrontActivityCount > 0;
    }

    /**
     * 获取顶部的activity
     */
    public static Activity getTopActivity() {
        if (!ACTIVITIES.isEmpty()) {
            return ACTIVITIES.get(ACTIVITIES.size() - 1);
        }
        return null;
    }

    /**
     * 获取栈顶未关闭的界面Activity,可能为null
     *
     * @author libin09 2014.12.15
     * @since 7.5
     */
    public static Activity getTopAvailableActivity() {
        if (ACTIVITIES.isEmpty()) {
            return null;
        }
        Activity tempActivity = null;
        for (int i = ACTIVITIES.size() - 1; i >= 0; i--) {
            tempActivity = ACTIVITIES.get(i);
            if (!tempActivity.isFinishing()) {
                break;
            }
        }
        return tempActivity;
    }

    /**
     * 检查是否有正在播放的画中画activity并关闭
     *
     * @param playActionCallbackClazz 画中画实现回调类
     * @param <T>                     画中画实现回调类
     */
    public static <T> void checkClosePlayPipActivity(Class<T> playActionCallbackClazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ACTIVITIES.isEmpty()) {
                return;
            }
            for (Activity activity : ACTIVITIES) {
                if (activity != null && !activity.isFinishing()
                        && playActionCallbackClazz.isAssignableFrom(activity.getClass())
                        && activity.isInPictureInPictureMode()) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 关闭指定页面
     */
    @SafeVarargs
    public static void closePages(Class<? extends Activity>... needCloseActivities) {
        if (ACTIVITIES.isEmpty() || needCloseActivities == null || needCloseActivities.length <= 0) {
            return;
        }
        for (Activity activity : ACTIVITIES) {
            for (Class clazz : needCloseActivities) {
                if (activity != null && !activity.isFinishing()
                        && clazz.isInstance(activity)) {
                    activity.finish();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (!ACTIVITIES.contains(activity)) {
            ACTIVITIES.add(activity);
        }
        if (DuboxLog.isDebug()) {
            DuboxLog.d(TAG, "onActivityCreated sFrontActivityCount = " + sFrontActivityCount);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        sFrontActivityCount++;
        // 应用进入到前台
        if (sFrontActivityCount == 1) {
            for (AppVisibilityListener listener : appVisibilityListenerList) {
                listener.onVisibilityChange(true);
            }
            mStartForegroundTime = System.currentTimeMillis();
        }
        if (DuboxLog.isDebug()) {
            DuboxLog.d(TAG, "onActivityStarted sFrontActivityCount = " + sFrontActivityCount);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        sFrontActivityCount--;
        // 应用进入到后台
        if (sFrontActivityCount == 0) {
            for (AppVisibilityListener listener : appVisibilityListenerList) {
                listener.onVisibilityChange(false);
            }
            reportActiveTime();
        }
        if (DuboxLog.isDebug()) {
            DuboxLog.d(TAG, "onActivityStopped sFrontActivityCount = " + sFrontActivityCount);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ACTIVITIES.remove(activity);
        for (TopActivityChangeListener listener : activityChangeListeners) {
            listener.change(ACTIVITIES.isEmpty() ? null : ACTIVITIES.get(ACTIVITIES.size() - 1));
        }
        if (DuboxLog.isDebug()) {
            DuboxLog.d(TAG, "onActivityDestroyed sFrontActivityCount = " + sFrontActivityCount);
        }
    }

    /**
     * 上报用户前台活跃时长，触发时机如下：
     * 1. 应用从前台退出后台的时候会记一次
     * 2. 应用点击切换账号按钮的时候会记一次
     * 3. 应用退出登录的时候会记一次
     * <p>
     * 未登录的情况下不统计
     */
    public static void reportActiveTime() {
        if (Account.INSTANCE.isLogin()) {
            long activeTime = (System.currentTimeMillis() - mStartForegroundTime) / 1000;
            EventStatisticsKt.statisticActionEvent(APP_ACTION_TIME_FOREGROUND, activeTime + "");
        }
    }

    /**
     * 账号切换或退出登录后不会触发 sFrontActivityCount == 1 的条件，所以需要手动刷新
     */
    public static void resetForegroundTime() {
        mStartForegroundTime = System.currentTimeMillis();
    }

    public static void addOnAppVisibilityListener(AppVisibilityListener listener) {
        appVisibilityListenerList.add(listener);
    }

    public static void removeOnAppVisibilityListener(AppVisibilityListener listener) {
        appVisibilityListenerList.remove(listener);
    }

    public static void addTopActivityChangeListener(TopActivityChangeListener listener) {
        if (!activityChangeListeners.contains(listener)) {
            activityChangeListeners.add(listener);
        }
    }

    public static void removeTopActivityChangeListener(TopActivityChangeListener listener) {
        if (activityChangeListeners.contains(listener)) {
            activityChangeListeners.remove(listener);
        }
    }


    public interface TopActivityChangeListener {
        void change(Activity curTopActivity);
    }
}
