/*
 * LocateUploadHelper.java
 * classes : com.dubox.drive.task.transmit.LocateUploadHelper
 * @author 文超
 * V 1.0.0
 * Create at 2013-6-9 下午1:11:50
 */
package com.moder.compass.transfer.transmitter.locate;

import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.moder.compass.base.utils.GlobalConfigKey;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.config.GlobalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.transfer.io.TransferApi;
import com.moder.compass.transfer.io.model.LocateUploadResponse;
import com.moder.compass.transfer.io.model.UploadUrlInfo;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取上传接口的服务器列表的帮助类 com.dubox.drive.task.transmit.LocateUpload
 *
 * @author 文超 <br/>
 *         create at 2013-6-9 下午1:11:50
 */
public class LocateUpload {
    private static final String TAG = "LocateUpload";

    // 每个服务器地址被访问的最大次数
    private static final int MAX_SERVER_CONNECT_TIME = 2;

    /** 服务器列表的有效期 */
    private static long sExpireTime;

    /** 服务器列表 */
    private static List<UploadUrlInfo> sUploadUrlInfoList;

    /**
     * 只作为锁mServerList专用
     */
    private static final Object LOCK_SERVER_LIST_OBJECT = new Object();

    /**
     * 文件路径和serverList index的映射，value的Pair表示serverList index和对其访问的次数
     */
    private static final Map<String, Pair<Integer, Integer>> sPathIndexMap = Collections
            .synchronizedMap(new HashMap<String, Pair<Integer, Integer>>());

    /**
     * 云端配置项
     */
    private final ConfigSystemLimit mConfigSystemLimit;

    public LocateUpload() {
        mConfigSystemLimit = ConfigSystemLimit.getInstance();
        initServerList();
    }

    private void initServerList() {
        synchronized (LOCK_SERVER_LIST_OBJECT) {
            if (CollectionUtils.isEmpty(sUploadUrlInfoList)) {
                sUploadUrlInfoList = new ArrayList<UploadUrlInfo>();
                sUploadUrlInfoList.add(initLocalUrl());
            }
        }
    }

    /**
     * 获取上传接口的服务器列表
     * @param bduss
     * @param uid
     * @param sign 别人共享的目录带sign
     */
    private void getLocateUpload(String bduss, String uid, @Nullable String sign) {
        // 检查当前服务器列表的有效期
        if (checkExpire()) {
            return;
        }

        try {
            LocateUploadResponse response = new TransferApi(bduss, uid).getLocateUpload(sign);
            // 更新服务器列表
            if (response == null) {
                DuboxLog.d(TAG, "response == NULL");
                return;
            }
            if (response.servers == null) {
                DuboxLog.d(TAG, "response.server  == NULL");
                return;
            }
            synchronized (LOCK_SERVER_LIST_OBJECT) {
                sUploadUrlInfoList = response.servers;
                sUploadUrlInfoList.add(initLocalUrl());
            }

            saveClientIp(response.clientIP);
            // 对于新的服务器列表，需要将请求的映射清空
            sPathIndexMap.clear();
            // 记录有效期
            recordExpireTime(response.expire);
        }  catch (JSONException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        } catch (RemoteException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 初始化本地默认的请求地址
     *
     * @return
     */
    private UploadUrlInfo initLocalUrl() {
        final String scheme = mConfigSystemLimit.httpsUploadEnable ?
                HostURLManagerKt.PRO_STR_HTTPS : HostURLManagerKt.PRO_STR_HTTP;
        return new UploadUrlInfo(scheme + HostURLManager.getUploadPCSDomain());
    }

    /**
     * 检查时效性。过期返回false，否则返回true
     */
    private synchronized boolean checkExpire() {
        return sExpireTime >= System.currentTimeMillis();
    }

    /**
     * 根据LocateUpload返回的时效性值，计算过期时间，并存储起来
     *
     * @param second 有效期
     */
    private synchronized void recordExpireTime(int second) {
        sExpireTime = System.currentTimeMillis() + second * 1000;
    }

    /**
     * 获取上传服务器域名。
     *
     * @param path 文件的上传路径
     * @param isRetry 获取服务器路径的请求是否是由重试而来，这是因为，如果有多个分片，那么前一个分片成功后，后一个分片再次获取Server时， 是不能对访问次数++的
     * @param bduss
     * @param uid
     * @param sign 别人共享给我的目录带
     * @return
     */
    public UploadUrlInfo getServer(String path, boolean isRetry, String bduss, String uid, @Nullable String sign) {
        getLocateUpload(bduss, uid, sign);
//
        initServerList();

        // 如果在pathIndexMap找不到，那么表明这个path是对serverList的第一次访问
        if (!sPathIndexMap.containsKey(path)) {
            // 为path初始化value
            sPathIndexMap.put(path, new Pair<Integer, Integer>(0, 1));
        } else { // 否则，将下标的访问次数++
            if (isRetry) {
                Pair<Integer, Integer> pair = sPathIndexMap.get(path);
                // 计算下标，如果下标的访问次数超过阀值，那么下标++，并将访问次数清零
                if (pair.second >= MAX_SERVER_CONNECT_TIME) {
                    sPathIndexMap.put(path, new Pair<Integer, Integer>(pair.first + 1, 1));
                } else {
                    // 将path和serverList的下标以及该下标被访问的次数记录到pathIndexMap
                    sPathIndexMap.put(path, new Pair<Integer, Integer>(pair.first, pair.second + 1));
                }
            }
        }

        Pair<Integer, Integer> pair = sPathIndexMap.get(path);

        // libin09 2014.8.4
        if (pair == null) {
            DuboxLog.w(TAG, "pair is null");
            return null;
        }

        int index = pair.first;
        int size = 0;
        synchronized (LOCK_SERVER_LIST_OBJECT) {
            size = sUploadUrlInfoList.size();
        }
        // 通过下标从serverList获取server
        if (index >= size) {
            sPathIndexMap.remove(path);
            return null;
        }

        synchronized (LOCK_SERVER_LIST_OBJECT) {
            return sUploadUrlInfoList.get(index);
        }
    }

    private void saveClientIp(String clientIp) {
        DuboxLog.d(TAG, "clientIp:" + clientIp);

        if (!TextUtils.isEmpty(clientIp)
                && !TextUtils.equals(GlobalConfig.getInstance().getString(GlobalConfigKey.CLIENT_IP), clientIp)) {
            GlobalConfig.getInstance().putString(GlobalConfigKey.CLIENT_IP, clientIp);
            GlobalConfig.getInstance().commit();
        }
    }
}
