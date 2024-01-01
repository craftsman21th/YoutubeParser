package com.moder.compass;

import static com.moder.compass.base.BackgroundWeakHelperKt.FOREGROUND_START_SOURCE_FRONTDESK;
import static com.moder.compass.statistics.UserFeatureKeysKt.KEY_USER_FEATURE_APP_HOT_OPEN;

import java.util.concurrent.CopyOnWriteArrayList;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.base.BackgroundWeakHelperKt;
import com.moder.compass.statistics.UserFeatureReporter;
import com.moder.compass.statistics.activation.ActivationManager;
import com.moder.compass.stats.DuboxStatsEngine;

import android.app.Activity;
/**
 * 应用前后台管理类, 注意： 多进程时问题
 *
 * @author tianzengming<br               />
 * Created by tianzengming on 2015/10/26.
 */
public final class AppStatusManager {

    private static final String TAG = "AppStatusManager";

    private static AppStatusManager appStatusManager = new AppStatusManager();

    public static AppStatusManager getInstance() {
        return appStatusManager;
    }
    // 热启动标记
    private boolean hotStartTag = false;
    private int mActivitiesCount = 0;
    private long mForegroundStartTime = 0L;
    private CopyOnWriteArrayList<AppStatusListener> listeners = new CopyOnWriteArrayList<>();

    private AppStatusManager() {

    }

    /**
     * 在Activity的onStart中调用
     */
    public void onStart(Activity activity) {
        if (mActivitiesCount == 0) {
            mForegroundStartTime = System.currentTimeMillis();
            onBroughtForeground(activity);
        }
        mActivitiesCount++;
    }

    /**
     * 在Activity的onStop中调用
     */
    public void onStop(Activity activity) {
        if (mActivitiesCount == 1) {
            long currentTime = System.currentTimeMillis();
            if (mForegroundStartTime > 0L) {
                int onLineTime = (int) (currentTime - mForegroundStartTime) / 1000;

                if (onLineTime > 0) {
                    StatisticsLogForMutilFields.getInstance().updateCount(
                            StatisticsLogForMutilFields.StatisticsKeys.MY_FOREGROUND_ONLINE_TIME_CLUSTER,
                            onLineTime);
                }
                mForegroundStartTime = 0L;
            }
            onGoBackground(activity);
        }
        mActivitiesCount--;
    }

    /**
     * 应用切换到后台
     */
    private void onGoBackground(Activity activity) {
        DuboxLog.d(TAG, "onGoBackground");
        DuboxStatsEngine.getInstance().uploadAll();
        for(AppStatusListener listener : listeners) {
            listener.goBackground(activity);
        }
        hotStartTag = true;
    }


    /**
     * 应用切换到前台
     */
    private void onBroughtForeground(Activity activity) {
        DuboxLog.d(TAG, "onBroughtForeground");
        for(AppStatusListener listener : listeners) {
            listener.broughtForeground(activity);
        }
        BackgroundWeakHelperKt.changeStartSource(FOREGROUND_START_SOURCE_FRONTDESK);
        ActivationManager.sendActiveUser(null);
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.MY_FOREGROUND_ACTIVE);
        if (hotStartTag) {
            new UserFeatureReporter(KEY_USER_FEATURE_APP_HOT_OPEN).reportAFAndFirebase();
        }
    }


    public void registerListener(AppStatusListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AppStatusListener listener) {
        listeners.remove(listener);
    }

    public interface AppStatusListener {

        /**
         * 前台切换到后台
         * @param activity
         */
        void goBackground(Activity activity);

        /**
         * 后台切回到前台
         */
        void broughtForeground(Activity activity);

    }

}
