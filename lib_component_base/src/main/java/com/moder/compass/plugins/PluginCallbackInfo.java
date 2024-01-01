package com.moder.compass.plugins;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by libin on 2017/9/27.插件回调信息
 */

public final class PluginCallbackInfo implements Parcelable {
    /**
     * 模块管理器
     */
    private final int mManager;

    /**
     * 方法
     */
    private final int mMethod;

    /**
     * 回调
     */
    private final IPluginPlatformCallback mCallback;

    /**
     * @param manager
     * @param method
     * @param callback
     */
    PluginCallbackInfo(int manager, int method, @Nullable IPluginPlatformCallback callback) {
        mManager = manager;
        mMethod = method;
        mCallback = callback;
    }

    protected PluginCallbackInfo(Parcel in) {
        mManager = in.readInt();
        mMethod = in.readInt();
        mCallback = null;
    }

    /**
     * @param pluginId
     * @param params
     * @throws RemoteException
     */
    public final void onResult(@NonNull String pluginId, @Nullable String params) throws RemoteException {
        if (mCallback == null) {
            return;
        }

        mCallback.onResult(pluginId, mManager, mMethod, params);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mManager);
        dest.writeInt(mMethod);
    }

    public static final Creator<PluginCallbackInfo> CREATOR = new Creator<PluginCallbackInfo>() {
        @Override
        public PluginCallbackInfo createFromParcel(Parcel in) {
            return new PluginCallbackInfo(in);
        }

        @Override
        public PluginCallbackInfo[] newArray(int size) {
            return new PluginCallbackInfo[size];
        }
    };

    @Override
    public String toString() {
        return mManager + "_" + mMethod;
    }
}
