package com.moder.compass.transfer.transmitter.constant;

/**
 * 下载异常码
 *
 * @author sunqi01
 */
public interface DownloadExceptionCode {
    int SDCARD_NO_SPACE_ERROR = 1000;
    int DESTINATION_FILE_ERROR = 1001;
    int PCS_LINK_EXPIRE_TIME = 1002;
    int UPDATE_BY_HTTP = 1003;
    int SERVER_FILE_IS_CHANGE = 1004;
    int DOWNLOAD_URL_IS_EMPTY = 1005;
    int DOWNLOAD_URL_CHANGE = 1006;
    int SDK_M3U8_TRANSFER_OVERTIME = 1007;
    int SDK_DLINK_REFRESH_EXCEPTION = 1008;
    int SDK_TASK_NOT_EXIST = 1009;
    int SDK_LOCAL_FILE_SYSTEM_ERROR = 1010;
    int SDK_P2P_CHECKSUM_ERROR = 1011;
}

