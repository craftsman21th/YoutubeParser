
package com.moder.compass.transfer.task.process.download;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_DOWNLOAD_UPDATE;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.storage.DownloadTaskProviderHelper;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.DownloadTask;
import com.moder.compass.transfer.task.TransferTask;

import java.util.ArrayList;

/**
 * 专门处理在loadProcessor过程对task的相关操作
 *
 * Created by liuliangping on 2015/2/3.
 */
public class DownloadProcessorHelper {
    private static final String TAG = "DownloadProcessorHelper";
    private final DownloadTaskProviderHelper mProviderHelper;
    private final String mBduss;
    private final String mUid;

    public DownloadProcessorHelper(String bduss, String uid) {
        mBduss = bduss;
        mUid = uid;
        mProviderHelper = new DownloadTaskProviderHelper(mBduss);
    }

    /**
     * 添加单个完成状态的任务到下载列表
     */
    public void addFinishedDownloadFile(DownloadTask dl, boolean isNotify) {
        if (dl.mTaskId > 0) {
            mProviderHelper.updateDownloadFinishTask(BaseApplication.getInstance().getContentResolver(), dl);
        } else {
            mProviderHelper.updateDownloadFinishTask(BaseApplication.getInstance().getContentResolver(), dl, isNotify);
        }

        // 发送广播，通知接收 libin09 2015-4-17
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.REMOTE_URL, dl.mRemoteUrl);
        data.putString(TransferContract.Tasks.LOCAL_URL, dl.getLocalUrl());
        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_DOWNLOAD_UPDATE, dl.mTaskId, TransferContract.Tasks.STATE_FINISHED,
                data);
    }

    /**
     * 根据FileWrapper创建下载任务
     * @param context
     * @param item
     * @return
     */
    DownloadTask createDownloadTaskWithFileWrapper(Context context, IDownloadable item) {
        if (item == null) {
            return null;
        }
        String savePath = Target30StorageKt.getDownloadPath(context, item, mBduss);
        return new DownloadTask(PathKt.rFile(savePath), item.getFilePath(),
                item.getSize(), item.getServerMD5(), mBduss, mUid);
    }

    public void cancelTask(int id) { // 操作数据库删除此任务
        final ArrayList<Integer> ids = new ArrayList<Integer>(1);
        ids.add(id);
        mProviderHelper.deleteTask(BaseApplication.getInstance().getContentResolver(), false, ids);
    }

    /**
     * 添加单个文件到下载列表
     *
     * @param dl
     * @param isNotify
     * @author 孙奇 V 1.0.0 Create at 2012-10-29 下午07:13:27
     *
     */
    public void addDownloadFile(DownloadTask dl, boolean isNotify, Processor.OnAddTaskListener listener) {
        if (listener.onAddTask()) {
            isNotify = false;
        }

        final Uri taskUri =
                mProviderHelper.addDownloadingTask(BaseApplication.getInstance().getContentResolver(), dl, isNotify,
                        false);
        DuboxLog.d(TAG, "addDownloadFile " + taskUri);
        // 通知传输列表更新，但是不通知调度器
        if (!isNotify) {
            BaseApplication.getInstance().getContentResolver()
                    .notifyChange(TransferContract.DownloadTasks.PROCESSING_CONTENT_URI, null, false);
        }

        if (taskUri != null) {
            dl.mTaskId = (int) ContentUris.parseId(taskUri);
        }
    }

    /**
     * 在恢复为pending状态时候，如果是第三方下载，需要更新字段
     * 
     * @param task
     * @param priority
     */
    void resumeToPendingUpdateStates(TransferTask task, int priority) {
        final ContentValues values = new ContentValues();
        values.put(TransferContract.Tasks.STATE, TransferContract.Tasks.STATE_PENDING);
        values.put(TransferContract.Tasks.RATE, 0);
        values.put(TransferContract.Tasks.DATE, System.currentTimeMillis());
        if (priority > 0) {
            values.put(TransferContract.Tasks.PRIORITY, priority);
        }
        mProviderHelper.updateTask(BaseApplication.getInstance().getContentResolver(), null, task.mTaskId,
                values);
    }
}
