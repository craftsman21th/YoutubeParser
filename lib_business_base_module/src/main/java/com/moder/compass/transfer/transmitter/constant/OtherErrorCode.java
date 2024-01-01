package com.moder.compass.transfer.transmitter.constant;

/**
 * 错误
 * Created by liuliangping on 2016/7/18.
 */
public interface OtherErrorCode {
    /**
     * MalformedURL
     */
    int URL_MALFORMED = -10010;

    /**
     * http返回非200和非206
     */
    int HTTP_NO_200_AND_206 = -10011;

    /**
     * 文件名为空
     */
    int FILE_NAME_EMPTY = -10012;

    /**
     * md5集合是空
     */
    int MD5_LIST_EMPTY = -10013;

    /**
     * 没有存储空间
     */
    int LOCAL_NOT_ENOUGH_SPACE = -10014;

    /**
     * 是不retry的错误
     */
    int IS_NO_RETRY = -10015;

    /**
     * 过期查询cursor是空
     */
    int EXPIRE_PROCESSOR_CURSOR_EMPTY = -10016;

    /**
     * 过期查询失败
     */
    int EXPIRE_PROCESSOR_GET_RESULT_EMPTY = -10017;

    /**
     * 流异常
     */
    int STREAM_EXCEPTION = -10018;

    /**
     * 过期dlink为空
     */
    int EXPIRE_PROCESSOR_DLINK_EMPTY = -10019;

    /**
     * 网络返回pscid 或者 bcsid 为空
     */
    int PCSID_OR_BCSID_IS_EMPTY = -10020;

    /**
     * 重试次数超限
     */
    int RETRY_OVER_TIME = -10021;

    /**
     * 重试检查网络次数超限
     */
    int CHECK_NETWORK_RETRY_OVER_TIME = -10022;

    /**
     * 有信号无网重试检查次数超限
     */
    int CHECK_SIGNAL_NETWORK_RETRY_OVER_TIME = -10023;

    /**
     * 线程被打段异常的错误
     */
    int THREAD_INTERRUPTED = -10024;

    /**
     * 任务正常暂停
     */
    int TASK_PAUSE = -10025;

    /**
     * 重命名失败
     */
    int LOCAL_RENAME_FAIL = -10026;

    /**
     * 返回的block集合为空
     */
    int BLOCK_LIST_EMPTY = -10027;

    /**
     * 未知错误
     */
    int ERROR_OTHER_UNKNOWN = -10028;

    /**
     * 下载文件长度错误
     */
    int ERROR_FILE_LENGTH = -10029;

    // 以下是retry

    /**
     * 流畅的大小为o
     */
    int RETRY_SMOOTH_SIZE_ZERO = -20010;

    /**
     * 空指针异常重试
     */
    int RETRY_NULL_EXCEPTION = -20011;

    /**
     * 协议异常重试
     */
    int RETRY_PROTOCOL_EXCEPTION = -20012;

    /**
     * 流异常重试
     */
    int RETRY_STREAM_EXCEPTION = -20013;

    /**
     * 删除已经下载的重试
     */
    int RETRY_HAS_DOWNLOAD_ERROR = -20014;

    /**
     * URLException的重试
     */
    int RETRY_URL_EXCEPTION = -20015;

    /**
     * IndexOutOfBoundsException的重试
     */
    int RETRY_OUT_OF_BOUNDS_EXCEPTION = -20016;

    /**
     * m3u8 file parse err retry
     */
    int RETRY_PARSE_M3U8_FILE_ERROR = -20017;

    /**
     * NumberFormatException重试
     */
    int RETRY_NUMBER_FORMAT_EXCEPTION = -20018;

    /**
     * 完成的completeSize > fileSize重试
     */
    int RETRY_COMPLETE_SIZE_OVER_FILE_SIZE = -20019;

    /**
     * 完成的completeSize < fileSize重试
     */
    int RETRY_COMPLETE_SIZE_LESS_FILE_SIZE = -20020;

    /**
     * HttpURLConnection为null重试
     */
    int RETRY_HTTP_URL_CONNECTION = -20021;

    /**
     * IllegalArgumentException重试
     */
    int RETRY_ILLEGAL_ARGUMENT_EXCEPTION = -20022;

    /**
     * urlInfo null or urlInfo server null重试
     */
    int RETRY_UPLOAD_URL_NULL = -20023;

    /**
     * upload create file result null重试
     */
    int RETRY_UPLOAD_CREATE_FILE = -20024;

    /**
     * upload create file result null重试
     */
    int RETRY_UPLOAD_COMPRESS_LOCAL_ERROR = -20025;

    /**
     * upload pre create重试
     */
    int RETRY_UPLOAD_PRE_CREATE_FILE = -20026;

    /**
     * json解析错误
     */
    int ERROR_JSON_ANALYSIS = -20027;
    /**
     * android11 本地常见uri时返回null错误
     */
    int ERROR_TARGET30_URI_NULL = -20028;

    /**
     * 非vip用户视频大小超限制
     */
    int ERROR_VIDEO_OVERSIZE = -20029;
}
