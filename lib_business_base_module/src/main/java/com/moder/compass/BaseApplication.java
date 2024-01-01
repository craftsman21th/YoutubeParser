
package com.moder.compass;

import static com.moder.compass.util.BugHookKt.fixReportSizeConfigurationsException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.dubox.drive.basemodule.BuildConfig;
import com.dubox.drive.kernel.BaseShellApplication;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.business.kernel.DuboxDebugKt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by tianzengming on 2015/1/14.
 */
public abstract class BaseApplication extends BaseShellApplication {
    private static final String TAG = "BaseApplication";
    @SuppressLint("StaticFieldLeak") // Application context没问题忽略
    private static BaseApplication instance = null;
    private static Class<? extends Service> sSchedulerService;
    // 适配targetSdk26 引入的JobScheduler处理处于后台时的任务处理
    private static Class<? extends Service> sAppBackgroundSchedulerService;

    /* 0-首次启动， 1-覆盖安装， 2-冷启动*/
    public static int installType = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
//        initStrictMode();
        AppCommon.setSecondBoxPcsAppId(BuildConfig.APP_COMMON_PCS_APP_ID);
        AppCommon.PACKAGE_NAME = mContext.getPackageName();
        fixTimeout();
        fixReportSizeConfigurationsException();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public BaseApplication() {
        super();
        instance = this;
        DuboxDebugKt.setIsDebug();
    }

    /**
     * 修复timeout崩溃
     */
    private void fixTimeout() {
        try {
            Class<?> c = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = c.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);
            Field field = c.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            method.invoke(field.get(null));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public static void setSchedulerService(Class<? extends Service> cls) {
        sSchedulerService = cls;
    }

    public static void setAppBackgroundSchedulerService(Class<? extends Service> cls) {
        sAppBackgroundSchedulerService = cls;
    }

    public static Class<? extends Service> getSchedulerService() {
        return sSchedulerService;
    }

    public static Class<? extends Service> getAppBackgroundSchedulerService() {
        return sAppBackgroundSchedulerService;
    }

    protected abstract boolean isMainProcess();

    // 组件化完成后，不应存这种接口下沉
    public abstract Busable getBusable();

    private void initStrictMode() {
        if (!DuboxLog.isDebug()) {
            return;
        }
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll() // 检测所有潜在违例
//                .penaltyDialog() // 弹出违规提示对话框
                .penaltyLog() // 在Logcat 中打印违规异常信息
                .build());

        StrictMode.VmPolicy.Builder vmBuilder = new StrictMode.VmPolicy.Builder();
        vmBuilder.detectLeakedSqlLiteObjects(); // 泄露的Sqlite对象
        vmBuilder.detectActivityLeaks(); // Activity泄露
        vmBuilder.detectLeakedClosableObjects(); // 未关闭的Closable对象泄露
        //  用来检查 BroadcastReceiver 或者ServiceConnection 注册类对象是否被正确释放
        vmBuilder.detectLeakedRegistrationObjects();
        vmBuilder.detectFileUriExposure();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vmBuilder.detectContentUriWithoutPermission();
//            vmBuilder.detectUntaggedSockets();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vmBuilder.detectCredentialProtectedWhileLocked();
        }
//        vmBuilder.detectIncorrectContextUse();
//        vmBuilder.detectAll()
        vmBuilder.penaltyLog();
        StrictMode.setVmPolicy(vmBuilder.build());

    }


}
