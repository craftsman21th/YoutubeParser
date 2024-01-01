//package com.dubox.drive.base.imageloader;
//
// import java.util.List;
//
// import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
//
///**
// * Created by liaozhengshuang on 17/11/21.
// */
//
//public interface IImagePreLoadListener {
//    /**
//     * 启动缩略图预加载
//     * @param isWifiConnected
//     */
//    void startPreLoad(boolean isWifiConnected);
//
//    /**
//     * 暂停缩略图预加载
//     */
//    void pausePreLoad();
//
//    /**
//     * 重新开始缩略图预加载
//     * @param isWifiConnected
//     */
//    void resumePreLoad(boolean isWifiConnected);
//
//    /**
//     * 停止缩略图预加载
//     */
//    void stopPreLoad();
//    void addPreLoadTask(String path, ThumbnailSizeType type, ImageLoadingListener listener);
//    void addPreLoadTasks(List<String> paths, ThumbnailSizeType type);
//    void addPreLoadTaskByUrl(String url, ThumbnailSizeType type, ImageLoadingListener listener);
//    void addPreLoadTaskByUrls(List<String> urls, ThumbnailSizeType type);
//    void addPreLoadTaskByParent(ThumbnailSizeType type, PreLoadExtraParams params);
//}
