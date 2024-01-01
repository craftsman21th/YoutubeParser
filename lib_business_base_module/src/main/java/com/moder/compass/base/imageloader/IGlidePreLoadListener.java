package com.moder.compass.base.imageloader;

import java.util.List;

import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.dubox.glide.request.RequestOptions;

import androidx.fragment.app.Fragment;

/**
 * Created by liaozhengshuang on 17/11/21.
 */

public interface IGlidePreLoadListener {
    /**
     * 启动缩略图预加载
     * @param isWifiConnected
     */
    void startPreLoad(boolean isWifiConnected);

    /**
     * 暂停缩略图预加载
     */
    void pausePreLoad();

    /**
     * 重新开始缩略图预加载
     * @param isWifiConnected
     */
    void resumePreLoad(boolean isWifiConnected);

    /**
     * 停止缩略图预加载
     */
    void stopPreLoad();

    /**
     * 注册预加载运行状态监听
     *
     * @param stateListener 状态监听器
     */
    void registerPreLoadStateListener(IGlidePreLoadIdleListener stateListener);

    /**
     * 移除预加载运行状态监听
     *
     * @param stateListener 状态监听器
     */
    void unregisterPreLoadStateListener(IGlidePreLoadIdleListener stateListener);

    /**
     * 生成网址格式的缩略图路径
     * @param simpleImageFile 云图Server_path、md5
     * @param type 缩略图类型
     * @return 缩略图网址
     */
    String generateUrlFromPath(SimpleFileInfo simpleImageFile, ThumbnailSizeType type);

    /**
     * 获取预加载队列任务数量
     *
     * @return 任务数量
     */
    int getPreLoadTaskSize();

    void addPreLoadTask(Fragment fragment, SimpleFileInfo simpleImageFile, ThumbnailSizeType type);
    void addPreLoadTasks(Fragment fragment, List<SimpleFileInfo> simpleImageFiles, ThumbnailSizeType type);
    void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type);
    void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type,
                             IImagePreLoadTask.PreLoadResultListener listener);
    void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey);
    void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey,
                             RequestOptions options, GlideImageSize glideImageSize,
                             IImagePreLoadTask.PreLoadResultListener stateListener);
    void addPreLoadTaskByUrls(Fragment fragment, List<String> urls, ThumbnailSizeType type);
    void addPreLoadTaskByUrl(byte[] bytes, ThumbnailSizeType type, String md5);
    void addPreLoadTaskByParent(Fragment fragment, ThumbnailSizeType type, PreLoadExtraParams params);
}
