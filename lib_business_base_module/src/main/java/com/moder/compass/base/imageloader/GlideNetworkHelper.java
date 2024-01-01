package com.moder.compass.base.imageloader;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.android.util.network.NetWorkMonitor;

/**
 * Glide缩略图加载耗时使用的 快速 获取网络类型工具类
 * 网络类型做缓存 收到网络变化广播时重置缓存
 *
 * @author guanshuaichao
 * @since 2019/3/14
 */
public class GlideNetworkHelper implements NetWorkMonitor.NetWorkChangeListener {

    private static volatile GlideNetworkHelper sInstance;

    /** 网络变化监听 */
    private NetWorkMonitor mNetWorkMonitor;

    /** 缓存的网络类型是否有效 */
    private volatile boolean mIsValid;

    /** 缓存的网络类型 */
    private String mNetworkClass;

    public static GlideNetworkHelper getInstance() {
        if (sInstance == null) {
            synchronized (GlideNetworkHelper.class) {
                if (sInstance == null) {
                    sInstance = new GlideNetworkHelper();
                }
            }
        }

        return sInstance;
    }

    private GlideNetworkHelper() {
        registerNetChange();
    }

    /**
     * 注册网络状态变化广播 不进行解注册
     */
    private void registerNetChange() {
        mNetWorkMonitor = new NetWorkMonitor(this, 0L,
                BaseApplication.getInstance().getApplicationContext());
    }

    @Override
    public void networkStateChanged(boolean isNetWorkConnected, boolean isWifiConnected) {
        // 重置缓存
        mIsValid = false;
        mNetworkClass = null;
    }

    @Override
    public void noNetToMobileNet() {

    }

    public String getNetworkClass() {
        if (!mIsValid) {
            mNetworkClass = ConnectivityState.getNetworkClass(
                    BaseApplication.getInstance().getApplicationContext());
            mIsValid = true;
        }

        return mNetworkClass;
    }
}
