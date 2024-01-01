package com.moder.compass.transfer.transmitter.p2p;

import com.cocobox.library.ErrorCode;
import com.moder.compass.transfer.transmitter.constant.OtherErrorCode;
import com.moder.compass.transfer.transmitter.constant.TransmitterConstant;

public class UniversalDownloadUtils {

    public static int convertToClientErrCode(ErrorCode errorCode) {
        final int errno;
        if (errorCode == ErrorCode.TASK_ERR_TIMEOUT) {
            errno = TransmitterConstant.NETWORK_NO_CONNECTION;
        } else if (errorCode == ErrorCode.TASK_ERR_PCS_FILE_NOT_EXIST) {
            errno = TransmitterConstant.FILE_DOES_NOT_EXISTS;
        } else if (errorCode == ErrorCode.TASK_ERR_PCS_FILE_ILLEGAL) {
            errno = TransmitterConstant.FILE_IS_ILLEGAL;
        } else if (errorCode == ErrorCode.TASK_ERR_PCS_FILE_INCOMPLETE) {
            errno = TransmitterConstant.REMOTE_PCS_FILE_IS_IMPERFECT;
        } else if (errorCode == ErrorCode.TASK_ERR_DISK_SPACE) {
            errno = TransmitterConstant.SDCARD_NO_SPACE_ERROR;
        } else if (errorCode == ErrorCode.TASK_ERR_PCS_HOTLINKING_FORBIDDEN) {
            errno = TransmitterConstant.SERVER_FORBIDDEN_USER;
        } else if (errorCode == ErrorCode.TASK_ERR_STREAMING_NOT_INTEGRITY) {
            errno = TransmitterConstant.SDK_M3U8_TRANSFER_OVERTIME;
        } else if (errorCode == ErrorCode.TASK_ERR_DLINK_REFRESH_ERROR) {
            errno = TransmitterConstant.SDK_DLINK_REFRESH_EXCEPTION;
        } else if (errorCode == ErrorCode.TASK_ERR_FILE_METAS_CHANGED) {
            errno = TransmitterConstant.SERVER_FILE_IS_CHANGE;
        } else if (errorCode == ErrorCode.TASK_NOT_EXIST) {
            errno = TransmitterConstant.SDK_TASK_NOT_EXIST;
        } else if (errorCode == ErrorCode.TASK_ERR_FS_ADD_FILE_FAILED) {
            // 错误码41
            errno = TransmitterConstant.SDK_LOCAL_FILE_SYSTEM_ERROR;
        } else if (errorCode == ErrorCode.TASK_ERR_ADD_CHECKSUM_FAILED) {
            // 错误码 45
            errno = TransmitterConstant.SDK_P2P_CHECKSUM_ERROR;
        } else {
            errno = OtherErrorCode.ERROR_OTHER_UNKNOWN; // 默认的未知错误，保证p2p sdk的errcode和网盘本身的错误码不能一致
        }
        return errno;
    }
}
