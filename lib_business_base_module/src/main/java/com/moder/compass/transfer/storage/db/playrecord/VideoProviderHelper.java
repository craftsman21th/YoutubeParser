package com.moder.compass.transfer.storage.db.playrecord;

import com.dubox.drive.cloudfile.io.model.CloudFile;
import com.dubox.drive.cloudfile.storage.db.CloudFileProviderHelper;
import com.dubox.drive.db.playrecord.contract.VideoContract;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by libin09 on 2015/3/27.
 */
public class VideoProviderHelper {
    private static final String TAG = "VideoProviderHelper";

    /**
     * 用于区别账号，每个操作只影响自己所在账号
     */
    private final String mBduss;

    public VideoProviderHelper(String bduss) {
        mBduss = bduss;
    }

    /**
     * 修改一个视频记录（上次播放时间）
     *
     * @param context
     * @param name
     * @param position
     * @return
     */
    public boolean updateVideoRecorder(final Context context, final String name, final long position) {
        ContentValues values = new ContentValues();
        values.put(VideoContract.VideoRecordInfo.VIDEO_POSITION, position);
        int result =
                context.getContentResolver().update(VideoContract.VideoRecordInfo.buildVideoRecorderUri(mBduss, name),
                        values, null, null);
        return result > 0;
    }


    public void updateVideoRecorderByFsId(final Context context, final long position,
                                          final String fsId) {
        if (TextUtils.isEmpty(fsId)) {
            return;
        }
        CloudFile cloudFile = new CloudFileProviderHelper(mBduss).getFileByFsid(context, fsId);
        if (cloudFile != null && !TextUtils.isEmpty(cloudFile.getFileName())) {
            updateVideoRecorder(context, cloudFile.getFileName(), position);
        }
    }
}
