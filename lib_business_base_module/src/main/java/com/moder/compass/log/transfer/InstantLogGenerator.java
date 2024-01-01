package com.moder.compass.log.transfer;

import android.text.TextUtils;
import android.util.Pair;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;

/**
 * Created by liuliangping on 2016/7/27.
 */
public class InstantLogGenerator extends TransferLogGenerator<InstantDownloadLog> {
    private static final String TAG = "InstantLogGenerator";

    @Override
    protected String getFileListField(InstantDownloadLog field) {
        DataConnector dataConnector = new DataConnector();
        dataConnector.append(TransferFieldKey.CLIENT_TYPE, RequestCommonParams.getClientType(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OP, field.getOpValue(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.TYPE, field.getLogUploadType(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.FileTypeKey.DOWNLOAD_TYPE, String.valueOf(field.getTransferType()),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.FileTypeKey.IS_SDK_DOWNLOAD, String.valueOf(field.getIsSDKTransfer()),
                field.getFieldSeparator());

        if (!TextUtils.isEmpty(field.getUid())) {
            dataConnector.append(TransferFieldKey.UID, field.getUid(), field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.LOG_TASK_ID, field.getLogTaskId(), field.getFieldSeparator());

        // 日志增加serverIP clientIp上报 8.9.0
        if (!TextUtils.isEmpty(field.getClientIp())) {
            dataConnector.append(TransferFieldKey.CLIENT_IP, field.getClientIp(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getServerIp())) {
            dataConnector.append(TransferFieldKey.BlockTypeKey.SERVER_IP, field.getServerIp(),
                    field.getFieldSeparator());
        }

        /**
         * 当满足瞬时速度的上报条件后，需要一定时间更新进度，然后getTransferSchedulerInfo才能拿到最新的数据，
         * 日志线程单独的线程调度，不影响其他模块
         */
        try {
            Thread.sleep(1500L);
        } catch (Exception e) {
            DuboxLog.d(TAG, "", e);
        }
        Pair<Integer, Long> info = field.getTransferSchedulerInfo();
        if (info != null) {
            if (info.first > 0) {
                dataConnector.append(TransferFieldKey.FileTypeKey.FILE_NUM, String.valueOf(info.first),
                        field.getFieldSeparator());
            }

            if (info.second > 0L) {
                dataConnector.append(TransferFieldKey.FileTypeKey.INSTANT_SPEED_ALL, String.valueOf(info.second));
            }
        }
        return dataConnector.generatorResult();
    }

    @Override
    protected String getBlockListField(InstantDownloadLog field) {
        return null;
    }
}
