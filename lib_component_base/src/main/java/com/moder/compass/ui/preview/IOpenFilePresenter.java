package com.moder.compass.ui.preview;

import android.content.Context;

import com.moder.compass.transfer.task.DownloadTask;

import java.util.ArrayList;

/**
 * OpenFilePresenter对外部组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/10/08
 */
public interface IOpenFilePresenter {
    void registerCancelPreviewFinishBroadcast(Context context);
    void handleDownload(boolean hasOtherPreviewDialog, String remotePath, String serverMD5,
                        String transmitterType);
    void unRegisterCancelPreviewFinishBroadcast(Context context);
    void resumeRunningTasks(final ArrayList<Integer> runningTaskIds);
    void updatePreviewTask(final DownloadTask task);
    void fetchThenStopRunningTasks(final ArrayList<Integer> runningTaskIds);
    void addLogTasks(String remoteUrl, String localUrl, String fid);
    boolean isStorageEnough(long minByte, String path);
}
