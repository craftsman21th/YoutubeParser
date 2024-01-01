package com.moder.compass.transfer.transmitter.p2p;

import android.text.TextUtils;

import com.moder.compass.base.utils.GlobalConfigKey;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.cocobox.library.CallbackInterface;
import com.cocobox.library.ErrorCode;
import com.cocobox.library.Key;
import com.cocobox.library.P2P;
import com.cocobox.library.P2PTaskRunningInfo;
import com.cocobox.library.TaskRunningInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by libin on 15/12/3.p2p sdk回调
 */
public class P2PSDKCallbackProxy implements CallbackInterface {
    private static final String TAG = "P2PSDKCallbackProxy";

    /**
     * 创建下载器时候，taskID和transmitter的对应关系
     */
    private final Map<String, CallbackInterface> mTaskTransmitterCache;

    /**
     * 使用下载sdk后，端上taskID和下载的taskHandle的对应关系
     */
    private final Map<Long, String> mTaskHandleCache;

    public P2PSDKCallbackProxy() {
        mTaskTransmitterCache =
                Collections.synchronizedMap(new HashMap<String, CallbackInterface>());
        mTaskHandleCache =
                Collections.synchronizedMap(new HashMap<Long, String>());
    }

    @Override
    public void onP2PCreate(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onCreate callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PCreate(fgid, errorCode);
    }

    @Override
    public void onP2PStart(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onStart callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PStart(fgid, errorCode);
    }

    @Override
    public void onP2PStop(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onStop callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PStop(fgid, errorCode);
    }

    @Override
    public void onP2PPause(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onPause callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PPause(fgid, errorCode);
    }

    @Override
    public void onP2PDeleteTaskWithoutFiles(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG,
                "onDeleteTaskWithoutFiles callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PDeleteTaskWithoutFiles(fgid, errorCode);
    }

    @Override
    public void onP2PDeleteTaskAndFiles(String s, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onDeleteTaskAndFiles callback:" + callback + " ,fgid:" + fgid + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PDeleteTaskAndFiles(fgid, errorCode);
    }

    @Override
    public void onP2PGetTaskInfo(String s, P2PTaskRunningInfo p2PTaskRunningInfo, ErrorCode errorCode) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onGetTaskInfo callback:" + callback + " ,fgid:" + fgid + " ,p2PTaskRunningInfo:"
                + p2PTaskRunningInfo + " ,errorCode:" + errorCode);
        if (callback == null) {
            return;
        }

        callback.onP2PGetTaskInfo(fgid, p2PTaskRunningInfo, errorCode);
    }

    @Override
    public void onError(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        String fgid = s.toLowerCase();
        final CallbackInterface callback = mTaskTransmitterCache.get(fgid);
        DuboxLog.d(TAG, "onError callback:" + callback + " ,fgid:" + fgid);
        if (callback == null) {
            return;
        }

        callback.onError(fgid);
    }

    @Override
    public void onTaskCreate(String createID, long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskCreate createID:" + createID + " ,taskHandle:" + taskHandle
                + " ,errorCode:" + errorCode);
        if (TextUtils.isEmpty(createID)) {
            return;
        }

        final CallbackInterface callback = mTaskTransmitterCache.get(createID);
        DuboxLog.d(TAG, "onTaskCreate callback:" + callback);
        if (callback == null) {
            return;
        }

        synchronized (mTaskHandleCache) {
            mTaskHandleCache.put(taskHandle, createID);
        }
        callback.onTaskCreate(createID, taskHandle, errorCode);
    }

    @Override
    public void onTaskStart(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskStart taskHandle:" + taskHandle + " ,errorCode:" + errorCode);
        final CallbackInterface callback = getCallBack(taskHandle);
        DuboxLog.d(TAG, "onTaskStart callback:" + callback);
        if (callback == null) {
            return;
        }

        callback.onTaskStart(taskHandle, errorCode);
    }

    @Override
    public void onTaskStop(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskStop taskHandle:" + taskHandle + " ,errorCode:" + errorCode);
        final CallbackInterface callback = getCallBack(taskHandle);
        DuboxLog.d(TAG, "onTaskStart callback:" + callback);
        if (callback == null) {
            return;
        }

        callback.onTaskStop(taskHandle, errorCode);
    }

    @Override
    public void onTaskPause(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskPause taskHandle:" + taskHandle + " ,errorCode:" + errorCode);
        final CallbackInterface callback = getCallBack(taskHandle);
        DuboxLog.d(TAG, "onTaskPause callback:" + callback);
        if (callback == null) {
            return;
        }

        callback.onTaskPause(taskHandle, errorCode);
    }

    @Override
    public void onTaskDeleteWithoutFile(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskDeleteWithoutFile taskHandle:" + taskHandle + " ,errorCode:" + errorCode);
        final CallbackInterface callback = getCallBack(taskHandle);
        DuboxLog.d(TAG, "onTaskPause callback:" + callback);
        if (callback == null) {
            return;
        }

        callback.onTaskDeleteWithoutFile(taskHandle, errorCode);
    }

    @Override
    public void onTaskDeleteAndFile(long taskHandle, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskDeleteAndFile taskHandle:" + taskHandle + " ,errorCode:" + errorCode);
        final CallbackInterface callback = getCallBack(taskHandle);
        DuboxLog.d(TAG, "onTaskDeleteAndFile callback:" + callback);
        if (callback == null) {
            return;
        }

        callback.onTaskDeleteAndFile(taskHandle, errorCode);
    }

    @Override
    public void onTaskGetTaskInfo(long taskHandle, TaskRunningInfo info, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onTaskGetTaskInfo taskHandle:" + taskHandle + " ,info:"
                + info + " ,errorCode:" + errorCode);

        final CallbackInterface callback = getCallBack(taskHandle);
        if (callback == null) {
            return;
        }

        callback.onTaskGetTaskInfo(taskHandle, info, errorCode);
    }

    @Override
    public void onGetPlayM3u8Path(String createID, String path, ErrorCode errorCode) {
        DuboxLog.d(TAG, "onGetPlayM3u8Path createID:" + createID + " ,path:"
                + path + " ,errorCode:" + errorCode);

        if (TextUtils.isEmpty(createID)) {
            return;
        }
        final CallbackInterface callback = mTaskTransmitterCache.get(createID);
        DuboxLog.d(TAG, "onGetPlayM3u8Path callback:" + callback);
        if (callback == null) {
            return;
        }
        callback.onGetPlayM3u8Path(createID, path, errorCode);
    }

    @Override
    public void onGetParameter(ErrorCode errorCode, Key key, String value) {
        DuboxLog.d("DownloadService", " errorCode : " + errorCode + " key : " + key + " value : " + value);
        if (errorCode != ErrorCode.SUCCESS) {
            return;
        }
        if (key.equals(Key.SDK_VERSION)) {
            GlobalConfig.getInstance().putString(GlobalConfigKey.P2P_VERSION, value);
        }
        P2P.getInstance().setParameter(key, value);
    }

    /**
     * 通过taskHandle获取callback
     *
     * @param taskHandle
     * @return
     */
    private CallbackInterface getCallBack(long taskHandle) {
        String createID;
        synchronized (mTaskHandleCache) {
            createID = mTaskHandleCache.get(taskHandle);
        }
        if (TextUtils.isEmpty(createID)) {
            return null;
        }

        return getCallBack(createID);
    }

    /**
     * 通过createID获取callback
     *
     * @param createID
     * @return
     */
    private CallbackInterface getCallBack(String createID) {
        if (TextUtils.isEmpty(createID)) {
            return null;
        }

        return mTaskTransmitterCache.get(createID);
    }

    /**
     * 添加p2p任务回调接口
     *
     * @param s p2p任务标识
     * @param callback
     */
    public void add(String s, CallbackInterface callback) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        mTaskTransmitterCache.put(s.toLowerCase(), callback);
    }

    /**
     * 删除p2p任务回调接口
     *
     * @param s p2p任务标识
     */
    public void remove(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        if (!mTaskTransmitterCache.isEmpty()) {
            mTaskTransmitterCache.remove(s.toLowerCase());
        }

        synchronized (mTaskHandleCache) {
            if (!mTaskHandleCache.isEmpty()) {
                Set<Map.Entry<Long, String>> set = mTaskHandleCache.entrySet();
                Iterator<Map.Entry<Long, String>> iterator = set.iterator();

                while (iterator.hasNext()) {
                    Map.Entry<Long, String> item = iterator.next();
                    if (TextUtils.equals(s.toLowerCase(), item.getValue())) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }
}
