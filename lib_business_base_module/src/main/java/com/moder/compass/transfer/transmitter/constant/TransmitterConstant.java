package com.moder.compass.transfer.transmitter.constant;

import com.moder.compass.transfer.transmitter.PCSTransmitErrorCode;

/**
 * 传输器异常码
 *
 * @author sunqi01
 */
public final class TransmitterConstant implements NetworkExceptionCode, PCSTransmitErrorCode, DownloadExceptionCode,
        UploadExceptionCode, TransmitterState, ShareLinkErrorCode {
    private static final String TAG = "TransmitterExceptionCode";

    public static String getExceptionMsg(int exceptionCode) {
        switch (exceptionCode) {
            case SDCARD_NO_SPACE_ERROR:
                return "sdcard is full";
            case NETWORK_NOT_AVAILABLE:
                return "network connect but not available";
            case WAITING_FOR_WIFI:
                return "waiting for wifi";
            case NETWORK_NO_CONNECTION:
                return "network not connected";
            case DESTINATION_FILE_ERROR:
                return "destination file error";
            case PCS_LINK_EXPIRE_TIME:
                return "dlink expire time";
            case NETWORK_VERIFY_CHECKING:
                return "network verify checking";
            default:
                return "other unknown error";
        }
    }
}
