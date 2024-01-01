package com.moder.compass.transfer.task.process.upload;

import static com.moder.compass.base.utils.EventCenterHandlerKt.MESSAGE_UPLOAD_UPDATE;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;

import com.moder.compass.base.utils.EventCenterHandler;
import com.moder.compass.transfer.base.UploadInfo;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.BaseApplication;
import com.moder.compass.transfer.base.Processor;
import com.moder.compass.transfer.storage.UploadTaskProviderHelper;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.UploadTask;

/**
 * 创建上传完成的任务
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-14 下午07:10:26
 */
class NewFinishedUploadTaskProcessor extends Processor {
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final UploadInfo mUploadInfo;

    NewFinishedUploadTaskProcessor(UploadInfo uploadInfo, boolean isNotify, String bduss, String uid) {
        super();
        this.mUploadInfo = uploadInfo;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
    }

    @Override
    public void process() {
        TransferTask task =
                new UploadTask(BaseApplication.getInstance(), mUploadInfo.getLocalFile(), mUploadInfo.getRemotePath(),
                        mBduss, mUid, mUploadInfo.getConflictStrategy(), mUploadInfo.getQuality());

        final UploadTaskProviderHelper helper = new UploadTaskProviderHelper(mBduss);

        final Uri taskUri =
                helper.addUploadFinishTask(BaseApplication.getInstance().getContentResolver(), task, mIsNotify);

        if (taskUri != null) {
            task.mTaskId = (int) ContentUris.parseId(taskUri);
        }


        // 发送广播，通知接收 libin09 2015-4-17
        final Bundle data = new Bundle(2);
        data.putString(TransferContract.Tasks.LOCAL_URL, task.getLocalUrl());
        data.putString(TransferContract.Tasks.REMOTE_URL, task.mRemoteUrl);

        EventCenterHandler.INSTANCE.sendMsg(MESSAGE_UPLOAD_UPDATE, task.mTaskId,
                TransferContract.Tasks.STATE_FINISHED, data);

        // 加入相册备份去重数据
        helper.addAlbum(BaseApplication.getInstance().getContentResolver(), mUploadInfo.getLocalFile(),
            mUploadInfo.getRemotePath());
    }
}
