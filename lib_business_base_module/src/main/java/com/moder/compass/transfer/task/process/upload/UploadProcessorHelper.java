package com.moder.compass.transfer.task.process.upload;

import java.util.ArrayList;

import android.content.ContentUris;
import android.net.Uri;

import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.storage.UploadTaskProviderHelper;
import com.moder.compass.transfer.task.TransferTask;

/**
 * Created by libin09 on 2015/2/6.
 */
class UploadProcessorHelper {
    private static final String TAG = "UploadProcessorHelper";

    private final String mBduss;

    UploadProcessorHelper(String bduss) {
        mBduss = bduss;
    }

    /**
     * 添加一个上传任务到上传列表
     * <p>
     * before calling this method, make sure to create task in DB
     *
     * @param task
     * @param isNotify
     * @author 孙奇 V 1.0.0 Create at 2012-11-14 下午03:58:39
     */
    void addTask(TransferTask task, boolean isNotify, Processor.OnAddTaskListener listener) {
        final Uri taskUri =
                new UploadTaskProviderHelper(mBduss).addUploadingTask(BaseApplication.getInstance()
                        .getContentResolver(), task, isNotify);
        if (taskUri != null) {
            task.mTaskId = (int) ContentUris.parseId(taskUri);
        }

        listener.onAddTask();
    }

    public void cancelTask(int id) {
        // 操作数据库删除此任务
        final ArrayList<Integer> ids = new ArrayList<Integer>(1);
        ids.add(id);
        new UploadTaskProviderHelper(mBduss).deleteTask(BaseApplication.getInstance().getContentResolver(), false, ids);
    }
}
