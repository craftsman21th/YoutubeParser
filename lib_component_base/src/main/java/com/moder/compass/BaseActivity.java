package com.moder.compass;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.dubox.drive.a.HC;
import com.moder.compass.base.utils.ActivityUtils;
import com.dubox.drive.common.component.BaseComponentManager;
import com.dubox.drive.common.component.IBaseActivityCallback;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.PhoneUtilKt;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.statistics.StatisticsLog;
import com.moder.compass.ui.view.IView;
import com.moder.compass.ui.widget.titlebar.AbstractTitleBar;
import com.moder.compass.ui.widget.titlebar.BaseTitleBar;
import com.moder.compass.util.DayNightModeKt;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Activity界面的公共类
 * 从BaiduNetDisk下沉到基础组件层
 * @author guoqiqin
 * @since 2019.8.15
 */
public abstract class BaseActivity extends AppCompatActivity implements IView {
    private static final String TAG = "BaseActivity";

    /**
     * 没有布局文件，动态添加View 时使用的ID
     */
    protected static final int NO_LAYOUT = 0;

    protected BaseTitleBar mTitleBar;

    protected BaseTitleBar mCollapsingTitleBar;

    /** 是否欢迎界面,默认不是   */
    private boolean mNavigate = false;

    /** 是否要显示splash,默认不是   */
    private boolean mSplash = false;

    /**
     * 会员服务
     */
    public static final String VIP_SERVICE = "vip";


    /**
     * 文件下载服务
     *
     * @author libin09 2015-7-14
     * @since 7.10
     */
    public static final String DOWNLOAD_SERVICE = "download_service";

    /**
     * 文件上传服务
     *
     * @author libin09 2015-7-14
     * @since 7.10
     */
    public static final String UPLOAD_SERVICE = "upload_service";

    /**
     * 云端文件服务
     *
     * @author libin09 2015-9-9
     * @since 7.11
     */
    public static final String CLOUD_FILE_SERVICE = "cloud_file_service";
    /**
     * wap调起服务
     */
    public static final String WAP_CONTROL_SERVICE = "wap_control_service";
    /**
     * 图片备份服务
     */
    public static final String PHOTO_BACKUP_SERVICE = "photo_backup_service";
    /**
     * 视频备份服务
     */
    public static final String VIDEO_BACKUP_SERVICE = "video_backup_service";

    /**
     * 绑定圣卡Activity退出时返回的CODE
     */
    public static final int REQUEST_CODE_BIND_SINGKIL = 60;

    private static ArrayList<String> videoPlayActivityNames = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DuboxLog.v(this.getClass().getSimpleName(), "onCreate");
        // context = this;
        super.onCreate(savedInstanceState);
        // 设置夜间、日间模式
        setNightOrLightMode(false);
        // 处理8.0的手机透明非全屏的页面锁定方向崩溃问题
        setPortrait();
        int id = getLayoutId();
        if (id != NO_LAYOUT) {
            setContentView(id);
        }
        initParams();
        initView();
        initEvent();
        if (needSetStatusBar()) {
            ActivityUtils.setSystemBarStatus(this, Color.WHITE);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        HC.showD(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                ActivityLifecycleManager.finishAll();
            }
        });
    }

    /**
     * 设置所有播放activity的名称，用于判断跳转activity时是否使用特定跳转动画
     */
    protected void setVideoPlayActivityNames(String... videoPlayActivityNames) {
        BaseActivity.videoPlayActivityNames.clear();
        BaseActivity.videoPlayActivityNames.addAll(Arrays.asList(videoPlayActivityNames));
    }

    protected boolean needSetPortrait() {
        return false;
    }


    public void setNightOrLightMode(boolean needChange) {
        DayNightModeKt.setDayOrNightMode(getWindow().getDecorView(), needChange);
    }


    /**
     * 兼容android 8.0的问题
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void setPortrait() {
        if (needSetPortrait() && Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        String toActivityName = getIntentComponentName(intent);
        if (videoPlayActivityNames.contains(toActivityName)) {
            overridePendingTransition(R.anim.activity_right_enter_anim, R.anim.activity_right_out_anim);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        String toActivityName = getIntentComponentName(intent);
        if (videoPlayActivityNames.contains(toActivityName)) {
            overridePendingTransition(R.anim.activity_right_enter_anim, R.anim.activity_right_out_anim);
        }
    }

    @Override
    public void finish() {
        super.finish();
        String sourceActivityName = getIntentComponentName(getIntent());
        if (videoPlayActivityNames.contains(sourceActivityName)) {
            overridePendingTransition(R.anim.activity_left_enter_anim, R.anim.activity_left_out_anim);
        }
    }

    private String getIntentComponentName(Intent intent) {
        ComponentName component = intent.getComponent();
        if (component == null) {
            return "";
        }
        return component.getClassName();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        if (DuboxLog.isDebug()) {
            DuboxLog.v(TAG, "Activity Name=" + this.getClass().getSimpleName());
        }
        super.onResume();
        // 统计日活，统计应用打开次数
        // 欢迎界面不统计
        if (!mNavigate) {
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.APP_ALL_ACTIVE);
            StatisticsLog.updateCount(StatisticsLog.StatisticsKeys.APP_ACTIVE);
        }

        // activity栈当前activity和栈内上一个activity横竖屏不一致，back时需要更新屏幕宽高
        if (!mNavigate && DeviceDisplayUtils.getScreenWidth() != PhoneUtilKt.getPhoneWidth(this)) {
            DeviceDisplayUtils.initDensity(this);
        }
    }

    @Override
    protected void onPause() {
        if (DuboxLog.isDebug()) {
            DuboxLog.v(this.getClass().getSimpleName(), "onPause");
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        DuboxLog.v(this.getClass().getSimpleName(), "onDestroy");

        if (mTitleBar != null) {
            mTitleBar.destroy();
        }

        super.onDestroy();
    }

    /**
     * 0返回 当前页面布局的 ID 如果使用布局，请使用常量 NO_LAYOUT
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 加载布局
     */
    protected abstract void initView();

    protected void initParams() {

    }

    /**
     * 初始化事件，如注册监听等
     */
    protected void initEvent() {

    }

    @Override
    public Context getContext() {
        return this.getApplicationContext();
    }

    @Override
    public void showError(String errorMessage) {
    }

    @Override
    public void showError(int errorCode) {
    }

    @Override
    public void showError(int errorCode, String errorMessage) {
    }

    @Override
    public void showSuccess(int successCode) {
    }

    @Override
    public void showSuccess(String successMsg) {
    }

    @Override
    public void startProgress(int progressCode) {
    }

    @Override
    public void stopProgress(int progressCode) {
    }

    @Override
    public boolean isDestroying() {
        return super.isFinishing();
    }

    public BaseTitleBar getTitleBar() {
        return mTitleBar;
    }

    public void setTitleBar(BaseTitleBar t) {
        mTitleBar = t;
    }

    public AbstractTitleBar getAbstractTitleBar() {
        if (mTitleBar != null) {
            return mTitleBar;
        }
        return mCollapsingTitleBar;
    }

    /**
     * 获取基础服务
     *
     * @param name 服务名称 {@link #VIP_SERVICE}
     * @return
     */
    @SuppressWarnings("unchecked")
    public @Nullable <T> T getService1(String name) {
        IBaseActivityCallback iBaseActivityCallback = BaseComponentManager.getInstance().getBaseActivityCallback();
        if (iBaseActivityCallback != null) {
            return iBaseActivityCallback.getService(name);
        } else {
            return null;
        }
    }

    /*
     * git (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IBaseActivityCallback iBaseActivityCallback = BaseComponentManager.getInstance().getBaseActivityCallback();
        if (iBaseActivityCallback != null) {
            iBaseActivityCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    /**
     * 是否有二级页面需要退出
     * @return
     */
    public boolean backFragment() {
        return false;
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 是否需要设置沉浸式状态栏
     * @return
     */
    protected boolean needSetStatusBar() {
        return true;
    }

    /** 设置是否欢迎界面   */
    protected void setNavigate(boolean navigate) {
        mNavigate = navigate;
    }

    /** 设置是否要显示splash   */
    protected void setSplash(boolean splash) {
        mSplash = splash;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        try {
            // 特殊机型会出现config为空的问题
            super.onConfigurationChanged(newConfig);
            DeviceDisplayUtils.initDensity(this, newConfig);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 获取具有Lifecycle的对象
     */
    public LifecycleOwner getLifecycleOwner() {
        return this;
    }

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return super.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }

    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter,
                                   @Nullable String broadcastPermission,
                                   @Nullable Handler scheduler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler,
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
        }
    }

}
