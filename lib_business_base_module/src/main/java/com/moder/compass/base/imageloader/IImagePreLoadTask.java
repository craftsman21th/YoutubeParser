package com.moder.compass.base.imageloader;

/**
 * Created by liaozhengshuang on 17/11/21.
 */

public interface IImagePreLoadTask {
    void execute();
    /**
     * 是否是直接可执行的单一图片预缓存任务
     * @return
     */
    boolean isExecutableTask();

    String getLoadUrl();

    /**
     * 通知已经加载完毕，比如可以在任务执行前检查此资源之前是否已经加载过，如果已经加载就通过此方法告知已经加载完毕
     */
    void notifyLoaded();

    interface PreLoadResultListener {
        void onLoadFailed(String url);

        void onResourceReady(String url);
    }
}
