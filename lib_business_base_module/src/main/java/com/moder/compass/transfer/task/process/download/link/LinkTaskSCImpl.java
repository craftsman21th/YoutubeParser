package com.moder.compass.transfer.task.process.download.link;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;

import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.BaseApplication;
import com.moder.compass.base.utils.ActivityUtils;
import com.dubox.drive.cloudfile.utils.FileType;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceAppUtils;
import com.moder.compass.transfer.transmitter.statuscallback.impl.DownloadTaskSCImpl;

/**
 * Created by liuliangping on 2015/11/3.
 */
class LinkTaskSCImpl extends DownloadTaskSCImpl {
    LinkTaskSCImpl(ContentResolver resolver, String bduss, int taskId) {
        super(resolver, bduss, taskId);
    }

    @Override
    public void onFailed(int reason, String extraInfo) {
        super.onFailed(reason, extraInfo);
    }

    @Override
    public void onSuccess(String content) {
        super.onSuccess(content);
        final Uri taskUri = ContentUris.withAppendedId(mFinishUri, mTaskId);

        final Cursor cursor =
                mResolver.query(taskUri, new String[]{TransferContract.Tasks.LOCAL_URL,
                        TransferContract.Tasks.REMOTE_URL, TransferContract.Tasks.TRANSMITTER_TYPE,
                        TransferContract.Tasks.TYPE}, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            if (!cursor.moveToFirst()) {
                return;
            }
            final String localUrl = cursor.getString(0);
            final String transmitterType = cursor.getString(2);
            // 如果不是广告任务，或者不是安装文件，则退出
            if (!TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK.equals(transmitterType)
                    || !FileType.isApk(localUrl)) {
                return;
            }

            // 如果百度云在前台，调起安装下载完成的apk
            if (ActivityUtils.isDuboxForeground(BaseApplication.getInstance())) {
                DeviceAppUtils.installApk(BaseApplication.getInstance(), localUrl);
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    public int onUpdate(long size, long rate) {
        return super.onUpdate(size, rate);
    }
}
