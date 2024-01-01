package com.moder.compass.log.transfer;

import android.text.TextUtils;
import android.util.Pair;

import com.moder.compass.BaseApplication;
import com.dubox.drive.base.network.NetworkUtil;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;

/**
 * Created by liuliangping on 2016/4/13.
 */
public class UploadTransferLogGenerator extends TransferLogGenerator<UploadLog> {
    private static final String TAG = "UploadTransferLogGenerator";

    @Override
    protected String getFileListField(UploadLog field) {
        DataConnector dataConnector = new DataConnector();
        dataConnector.append(TransferFieldKey.CLIENT_TYPE, RequestCommonParams.getClientType(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OP, field.getOpValue(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.TYPE, field.getLogUploadType(), field.getFieldSeparator());

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
            dataConnector.append(TransferFieldKey.FILE_SIZE_TYPE, String.valueOf(field.getFileSizeType()),
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
            }
            dataConnector.append(TransferFieldKey.FileTypeKey.OTHER_CODE, String.valueOf(field.getOtherErrorCode()),
                    field.getFieldSeparator());
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

            if (field.getTransferSize() > 0L && field.getRapidType() == 0) {
                dataConnector.append(TransferFieldKey.FileTypeKey.AVERAGE_SPEED,
                        String.valueOf(
                                field.getTransferSize() / getIntervalTime(field.getEndTime(), field.getStartTime())),
                        field.getFieldSeparator());
            }
        }

        if (!TextUtils.isEmpty(field.getClientIp())) {
            dataConnector.append(TransferFieldKey.CLIENT_IP, field.getClientIp(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getServerIp())) {
            dataConnector.append(TransferFieldKey.SERVER_IP, field.getServerIp(), field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.MODEL, String.valueOf(field.getUploadType()),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.NET_TYPE, field.getNetWorkType(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.USER_AGENT, "android", field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.CLIENT_VERSION, AppCommon.VERSION_DEFINED, field.getFieldSeparator());
        if (field.getSpeedLimit() > 0L) {
            dataConnector.append(TransferFieldKey.SPEED_LIMIT, String.valueOf(field.getSpeedLimit()),
                    field.getFieldSeparator());
        }

        if (field.getBlockSum() > 0) {
            dataConnector.append(TransferFieldKey.FileTypeKey.BLOCK_NUM_ALL, String.valueOf(field.getBlockSum()),
                    field.getFieldSeparator());
        }

        if (field.getNeedBlockSum() > 0) {
            dataConnector.append(TransferFieldKey.FileTypeKey.BLOCK_NUM_THIS_TIME, String.valueOf(field.getBlockSum()),
                    field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.REQUEST_URL, field.getRequestUrl(), field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.UPLOAD_CONCURRENT_STATE,
                String.valueOf(field.getConcurrentState()), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.UPLOAD_CONCURRENT_COUNT,
                String.valueOf(field.getConcurrentCount()), field.getFieldSeparator());

        return dataConnector.generatorResult();
    }

    @Override
    protected String getBlockListField(UploadLog field) {
        DataConnector dataConnector = new DataConnector();

        dataConnector.append(TransferFieldKey.CLIENT_TYPE, RequestCommonParams.getClientType(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OP, field.getOpValue(), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.TYPE, field.getLogUploadType(), field.getFieldSeparator());

        if (!TextUtils.isEmpty(field.getUid())) {
            dataConnector.append(TransferFieldKey.UID, field.getUid(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getFileFid())) {
            dataConnector.append(TransferFieldKey.LOG_FILE_FID, field.getFileFid(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getFileName())) {
            dataConnector.append(TransferFieldKey.FILE_NAME, field.getFileName(), field.getFieldSeparator());
        }

        if (field.getBlockSize() > 0L) {
            dataConnector.append(TransferFieldKey.BlockTypeKey.BLOCK_SIZE, String.valueOf(field.getBlockSize()),
                    field.getFieldSeparator());
        }

        // 约定 uid + 手机当前毫秒值
        dataConnector.append(TransferFieldKey.LOG_TASK_ID, field.getLogTaskId(), field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.BlockTypeKey.TRANSFER_STATES, String.valueOf(field.getFinishStates()),
                field.getFieldSeparator());
        if (field.getFinishStates() == TransferFieldKey.TRANSFER_FAIL) {
            if (field.getHttpErrorCode() > 0) {
                dataConnector.append(TransferFieldKey.BlockTypeKey.HTTP_CODE, String.valueOf(field.getHttpErrorCode()),
                        field.getFieldSeparator());
                dataConnector.append(TransferFieldKey.BlockTypeKey.PCS_CODE, String.valueOf(field.getPcsErrorCode()),
                        field.getFieldSeparator());

                if (!TextUtils.isEmpty(field.getXPcsRequestId())) {
                    dataConnector.append(TransferFieldKey.BlockTypeKey.X_PCS_REQUEST_ID, field.getXPcsRequestId(),
                            field.getFieldSeparator());
                }

                if (!TextUtils.isEmpty(field.getXbsRequestId())) {
                    dataConnector.append(TransferFieldKey.BlockTypeKey.X_BS_REQUEST_ID, field.getXbsRequestId(),
                            field.getFieldSeparator());
                }
            }
            dataConnector.append(TransferFieldKey.OTHER_CODE, String.valueOf(field.getOtherErrorCode()),
                    field.getFieldSeparator());
            dataConnector.append(TransferFieldKey.OTHER_ERROR_MESSAGE,
                    String.valueOf(field.getOtherErrorMessage()), field.getFieldSeparator());
        }

        if (field.getStartTime() > 0L) {
            dataConnector.append(TransferFieldKey.START_TIME, getTimeString(field.getStartTime()),
                    field.getFieldSeparator());
        }

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
                dataConnector.append(TransferFieldKey.AVERAGE_SPEED,
                        String.valueOf(
                                field.getTransferSize() / getIntervalTime(field.getEndTime(), field.getStartTime())),
                        field.getFieldSeparator());
            }
        }

        if (!TextUtils.isEmpty(field.getClientIp())) {
            dataConnector.append(TransferFieldKey.CLIENT_IP, field.getClientIp(), field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getServerIp())) {
            dataConnector.append(TransferFieldKey.SERVER_IP, field.getServerIp(), field.getFieldSeparator());
        }

        dataConnector.append(TransferFieldKey.MODEL, String.valueOf(field.getUploadType()),
                field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.NET_TYPE, NetworkUtil.getNetworkInfo(BaseApplication.getInstance()),
                field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.USER_AGENT, "android", field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.CLIENT_VERSION, AppCommon.VERSION_DEFINED, field.getFieldSeparator());
        if (field.getSpeedLimit() > 0L) {
            dataConnector.append(TransferFieldKey.SPEED_LIMIT, String.valueOf(field.getSpeedLimit()),
                    field.getFieldSeparator());
        }

        if (!TextUtils.isEmpty(field.getServerHost())) {
            dataConnector.append(TransferFieldKey.BlockTypeKey.SERVER_HOST, field.getServerHost(),
                    field.getFieldSeparator());
        }

        if (field.getFileRate() > 0L) {
            dataConnector.append(TransferFieldKey.FileTypeKey.INSTANT_SPEED_FILE, String.valueOf(field.getFileRate()),
                    field.getFieldSeparator());
        }

        Pair<Integer, Long> info = field.getTransferSchedulerInfo();
        if (info != null) {
            if (info.first > 0) {
                dataConnector.append(TransferFieldKey.FileTypeKey.FILE_NUM, String.valueOf(info.first),
                        field.getFieldSeparator());
            }

            if (info.second > 0L) {
                dataConnector.append(TransferFieldKey.FileTypeKey.INSTANT_SPEED_ALL, String.valueOf(info.second),
                        field.getFieldSeparator());
            }
        }

        dataConnector.append(TransferFieldKey.BlockTypeKey.BLOCK_INDEX, String.valueOf(field.getBlockIndex()),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.REQUEST_URL, field.getRequestUrl(), field.getFieldSeparator());

        dataConnector.append(TransferFieldKey.UPLOAD_CONCURRENT_STATE,
                String.valueOf(field.getConcurrentState()), field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.UPLOAD_CONCURRENT_COUNT,
                String.valueOf(field.getConcurrentCount()), field.getFieldSeparator());

        // 分片日志增加server IP上报 8.6.0
        if (!TextUtils.isEmpty(field.getServerIp())) {
            dataConnector.append(TransferFieldKey.BlockTypeKey.SERVER_IP, field.getServerIp(),
                    field.getFieldSeparator());
        }

        return dataConnector.generatorResult();
    }

    @Override
    protected String getFileListHeader(UploadLog field) {
        // 所有日志的拼接顺序不允许变更
        DataConnector dataConnector = new DataConnector();
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.USER_IP, field.getClientIp(),
                field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.SUBSYS,
                TransferFieldKey.OpMoniterHeaderKey.SUBSYS_UPLOAD, field.getFieldSeparator());
        dataConnector.append(TransferFieldKey.OpMoniterHeaderKey.TYPE, "",
                field.getFieldSeparator());
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
