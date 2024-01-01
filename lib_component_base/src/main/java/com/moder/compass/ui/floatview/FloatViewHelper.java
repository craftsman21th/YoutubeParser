package com.moder.compass.ui.floatview;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Application;
import android.app.LocalActivityManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 在Activity中添加悬浮窗口，切换Activity可自动重新添加浮窗(copy自百度网盘)
 *
 * @author guanshuaichao
 * @since 2019-07-09
 */
public class FloatViewHelper {

    private static final String TAG = "FloatViewHelper";

    private static volatile FloatViewHelper sInstance;

    private WeakReference<FrameLayout> mContainer;
    private List<FloatView> mViews;
    private ArrayMap<FloatView, IAttachFilter> filterArrayMap = new ArrayMap<>();
    private WeakReference<LocalActivityManager> mLocalActivityManager;

    public static FloatViewHelper getInstance() {
        if (sInstance == null) {
            synchronized (FloatViewHelper.class) {
                if (sInstance == null) {
                    sInstance = new FloatViewHelper();
                }
            }
        }
        return sInstance;
    }

    private FloatViewHelper() {
        mViews = new LinkedList<>();
        BaseApplication.getInstance().registerActivityLifecycleCallbacks(new FloatViewActivityLifecycle());
    }

    /**
     * 添加悬浮 View
     * @param v 悬浮 View 实现类
     * @param layoutParams View 对应的设置
     * @param filter 显示过滤器
     */
    public void add(FloatView v, FrameLayout.LayoutParams layoutParams, IAttachFilter filter) {
        if (v == null) {
            return;
        }

        if (mViews.contains(v)) {
            DuboxLog.d(TAG, "Already added this view " + v);
            return;
        }

        if (ViewCompat.isAttachedToWindow(v)) {
            DuboxLog.e(TAG, "Can not add a attached view " + v);
            return;
        }
        if (filter != null) {
            filterArrayMap.put(v, filter);
        }
        mViews.add(v);
        v.setLayoutParams(layoutParams);

        addView(v, null);
    }

    public void remove(FloatView v) {
        if (v == null) {
            return;
        }

        if (!mViews.contains(v)) {
            DuboxLog.d(TAG, "Already removed this view " + v);
            return;
        }

        removeView(v);
        mViews.remove(v);
    }

    private void attach(Activity activity) {
        // 判断是否是LocalActivity LocalActivity不需要处理浮窗 父Activity已处理过
        updateLocalActivityManager(activity);
        if (isLocalActivity(activity)) {
            return;
        }

        // 从旧Activity释放浮标View
        removeAllViews();

        // 赋值新Container
        setContainer(getActivityRoot(activity));

        // 添加到新Activity
        addAllViews(activity);
    }

    private void detach(Activity activity) {
        // 判断是否是LocalActivity LocalActivity不需要处理浮窗 父Activity已处理过
        updateLocalActivityManager(activity);
        if (isLocalActivity(activity)) {
            return;
        }

        // 非当前container所在activity 无需处理
        FrameLayout container = getContainer();
        if (container != getActivityRoot(activity)) {
            return;
        }

        // 从集合中清除auto clear的view
        removeAutoClearView();
    }

    private void updateLocalActivityManager(Activity activity) {
        if (activity instanceof ActivityGroup) {
            ActivityGroup activityGroup = (ActivityGroup) activity;
            mLocalActivityManager = new WeakReference<>(activityGroup.getLocalActivityManager());
        }
    }

    private boolean isLocalActivity(Activity activity) {
        if (activity == null || mLocalActivityManager == null) {
            return false;
        }
        // 网盘首页tab使用class名称作为Local Activity 的 id
        String id = activity.getClass().getSimpleName();
        LocalActivityManager localActivityManager = mLocalActivityManager.get();
        return localActivityManager != null && localActivityManager.getActivity(id) != null;
    }

    private FrameLayout getContainer() {
        return mContainer == null ? null : mContainer.get();
    }

    private void setContainer(FrameLayout frameLayout) {
        mContainer = null;
        mContainer = new WeakReference<>(frameLayout);
    }

    private void removeAllViews() {
        for (FloatView v : mViews) {
            removeView(v);
        }
    }

    private void removeView(View v) {
        FrameLayout container = getContainer();
        if (container == null || v == null) {
            return;
        }

        if (v.getParent() != null) {
            try {
                container.removeView(v);
            } catch (Exception e) {
                DuboxLog.d(TAG, e.getMessage(), e);
            }
        }
    }

    private void removeAutoClearView() {
        Iterator<FloatView> it = mViews.iterator();
        while (it.hasNext()) {
            FloatView v = it.next();
            if (v.isAutoClear()) {
                it.remove();
                removeView(v);
            }
        }
    }

    private void addAllViews(Activity activity) {
        for (FloatView v : mViews) {
            addView(v, activity);
        }
    }

    private void addView(FloatView v, Activity activity) {
        FrameLayout container = getContainer();
        if (container == null || v == null) {
            return;
        }

        // activity 过滤
        IAttachFilter iAttachFilter = filterArrayMap.get(v);
        if (activity == null && container.getContext() instanceof Activity) {
            activity = (Activity) container.getContext();
        }
        // 过滤筛选是否在对应页面显示
        if (iAttachFilter != null && activity != null && !iAttachFilter.isShowInActivity(activity)) {
            return;
        }
        try {
            container.addView(v);
        } catch (Exception e) {
            DuboxLog.d(TAG, e.getMessage(), e);
        }
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }

        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            DuboxLog.d(TAG, e.getMessage(), e);
        }

        return null;
    }

    private class FloatViewActivityLifecycle implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityResumed(Activity activity) {
            attach(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            detach(activity);
        }

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}
    }
}
