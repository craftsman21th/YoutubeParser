package com.moder.compass.log.transfer;

import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.cocobox.library.P2P;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2016/4/13.
 */
public class DownloadTransferLogGenerator extends TransferLogGenerator<DownloadLog> {
    private static final String TAG = "DownloadTransferLogGenerator";

    @Override
    protected String getFileListField(DownloadLog field) {
        DataConnector dataConnector = new DataConnector();
        dataConnector.append(TransferFieldKey.CLIENT_TYPE, RequestCommonParams.getClientType(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OP, field.getOpValue(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.TYPE, field.getLogUploadType(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.FileTypeKey.IS_SDK_DOWNLOAD, String.valueOf(field.getIsSDKTransfer()),
                field.getFieldSeparator());
        if (field.getIsSDKTransfer() == 1) {
            dataConnector.append(TransferFieldKey.P2P_VERSIOM, P2P.getInstance().getVersion(),
                    field.getFieldSeparator());
        }
        dataConnector.append(TransferFieldKey.FileTypeKey.DOWNLOAD_TYPE, String.valueOf(field.getTransferType()),
                field.getFieldSeparator());

        if (!TextUtils.isEmpty(field.getUid())) {
            dataConnector.append(TransferFieldKey.UID, field.getUid(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getFileFid())) {
            dataConnector.append(TransferFieldKey.LOG_FILE_FID, field.getFileFid(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getFileName())) {
            dataConnector.append(TransferFieldKey.FILE_NAME, field.getFileName(), field.getFieldSeparator());
        }

        if (field.getFileSize() > 0L) {
            dataConnector.append(TransferFieldKey.FILE_SIZE, String.valueOf(field.getFileSize()),
                    field.getFieldSeparator());
        }

        // 约定 uid + 手机当前毫秒值
        dataConnector.append(TransferFieldKey.LOG_TASK_ID, field.getLogTaskId(), field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.FileTypeKey.TRANSFER_STATES, String.valueOf(field.getFinishStates()),
                field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.FileTypeKey.IS_VIDEO, field.isVideo(), field.getFieldSeparator());
        if (field.getFinishStates() == TransferFieldKey.TRANSFER_FAIL) {
            if (field.getHttpErrorCode() > 0) {
                dataConnector.append(TransferFieldKey.FileTypeKey.HTTP_CODE, String.valueOf(field.getHttpErrorCode()),
                        field.getFieldSeparator());
                dataConnector.append(TransferFieldKey.FileTypeKey.PCS_CODE, String.valueOf(field.getPcsErrorCode()),
                        field.getFieldSeparator());

                if (!TextUtils.isEmpty(field.getXPcsRequestId())) {
                    dataConnector.append(TransferFieldKey.FileTypeKey.X_PCS_REQUEST_ID, field.getXPcsRequestId(),
                            field.getFieldSeparator());
                }

                if (!TextUtils.isEmpty(field.getXbsRequestId())) {
                    dataConnector.append(TransferFieldKey.FileTypeKey.X_BS_REQUEST_ID, field.getXbsRequestId(),
                            field.getFieldSeparator());
                }
            }

            dataConnector.append(TransferFieldKey.FileTypeKey.OTHER_CODE,
                    String.valueOf(field.getOtherErrorCode()), field.getFieldSeparator());
            dataConnector.append(TransferFieldKey.FileTypeKey.OTHER_ERROR_MESSAGE,
                    String.valueOf(field.getOtherErrorMessage()), field.getFieldSeparator());
        }

        if (field.getStartTime() > 0L) {
            dataConnector.append(TransferFieldKey.START_TIME, getTimeString(field.getStartTime()),
                    field.getFieldSeparator());
        }
        dataConnector.append(TransferFieldKey.START_POSITION, String.valueOf(field.getStartPosition()),
                field.getFieldSeparator());

        if (field.getEndTime() > 0L) {
            dataConnector.append(TransferFieldKey.END_TIME, getTimeString(field.getEndTime()),
                    field.getFieldSeparator());
            dataConnector.append(TransferFieldKey.LOCAL_TIME, getTimeString(field.getEndTime()),
                    field.getFieldSeparator());
        }

        if (field.getTransferSize() > 0L) {
            dataConnector.append(field.getTransferByteKey(), String.valueOf(field.getTransferSize()),
                    field.getFieldSeparator());
        }

        if (field.getStartTime() > 0L && field.getEndTime() > field.getStartTime()) {
            dataConnector.append(field.getTransferTimeKey(),
                    String.valueOf(getIntervalTime(field.getEndTime(), field.getStartTime())),
                    field.getFieldSeparator());

            if (field.getTransferSize() > 0L) {
                dataConnector.append(TransferFieldKey.FileTypeKey.AVERAGE_SPEED,
                        String.valueOf(
                                field.getTransferSize() / getIntervalTime(field.getEndTime(), field.getStartTime())),
                        field.getFieldSeparator());
            }
        }

        if (!TextUtils.isEmpty(field.getClientIp())) {
            dataConnector.append(TransferFieldKey.CLIENT_IP, field.getClientIp(), field.getFieldSeparator());
        }

        // 日志增加server IP上报 8.6.0
        if (!TextUtils.isEmpty(field.getServerIp())) {
            dataConnector.append(TransferFieldKey.BlockTypeKey.SERVER_IP, field.getServerIp(),
                    field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.NET_TYPE, field.getNetWorkType(), field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.USER_AGENT, "android", field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.CLIENT_VERSION, AppCommon.VERSION_DEFINED, field.getFieldSeparator());
        if (field.getSpeedLimit() > 0L) {
            dataConnector.append(TransferFieldKey.SPEED_LIMIT, String.valueOf(field.getSpeedLimit()),
                    field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.REQUEST_URL, field.getRequestUrl(), field.getFieldSeparator());

        return dataConnector.generatorResult();
    }

    @Override
    protected String getBlockListField(DownloadLog field) {
        return null;
    }

    @Override
    protected String getFileListHeader(DownloadLog field) {
        // 所有日志的拼接顺序不允许变更
        DataConnector dataConnector = new DataConnector();
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.USER_IP, field.getClientIp(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.SUBSYS,
                TransferFieldKey.OpMoniterHeaderKey.SUBSYS_DOWNLOAD, field.getFieldSeparator());
        if (field.getIsSDKTransfer() == 1) {
            dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.TYPE, TransferFieldKey.OpMoniterHeaderKey.TYPE_P2P,
                    field.getFieldSeparator());
        } else {
            dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.TYPE, String.valueOf(field.getTransferType()),
                    field.getFieldSeparator());
        }
        if (field.getFinishStates() == TransferFieldKey.TRANSFER_FAIL) {
            dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.ERR, TransferFieldKey.OpMoniterHeaderKey.ERR_NO,
                    field.getFieldSeparator());
        } else {
            dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.ERR, TransferFieldKey.OpMoniterHeaderKey.ERR_YES,
                    field.getFieldSeparator());
        }
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.ERRNO, String.valueOf(field.getOtherErrorCode()),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.ERRMSG, field.getOtherErrorMessage(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.UA_TYPE,
                TransferFieldKey.OpMoniterHeaderKey.UA_TYPE_ANDROID, field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.UA_VERSION, AppCommon.VERSION_DEFINED,
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.SUCC, "", field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.ALL, "");
        return dataConnector.generatorResult(false);
    }
}
