package com.moder.compass.log.transfer;

import com.moder.compass.log.ILogFieldKey;

/**
 * Created by liuliangping on 2016/3/20.
 */
public interface TransferFieldKey extends ILogFieldKey {
    String FIELD_SEPARATOR = "@#";

    /**
     * 下载op值
     */
    String DOWNLOAD_OP_VALUE = "DownloadFiles";

    /**
     * 上传op值
     */
    String UPLOAD_OP_VALUE = "UploadFiles";

    // 文件的在pcs里面的唯一标识
    String LOG_FILE_FID = "fid";

    String FILE_NAME = "file_name";

    String FILE_SIZE = "file_size";
    // 0=(0-10M) 1=(10-100M）2=(100-500M）3=(500-2G）4=2G以上
    String FILE_SIZE_TYPE = "file_size_type";

    // 任务编号也会打印在此文件分片级别的日志中。Uid+taskid可以唯一定位一次用户的下载，android使用：uid+当前手机毫秒值
    String LOG_TASK_ID = "taskid";

    String START_TIME = "start_time";

    /**
     * 传输的起始位置
     */
    String START_POSITION = "start_position";

    String END_TIME = "end_time";

    // 用户本地时间，local_time应该和end_time相同，但是希望同时打印
    String LOCAL_TIME = "local_time";

    String DOWNLOAD_RECV_BYTES = "recv_bytes";

    String UPLOAD_SEND_BYTES = "send_bytes";

    String DOWNLOAD_RECV_TIMES = "recv_times";

    String UPLOAD_SEND_TIMES = "send_times";

    /**并发上传：0：关闭，1：打开*/
    String UPLOAD_CONCURRENT_STATE = "concurrent_state";

    /**并发上传的并发数*/
    String UPLOAD_CONCURRENT_COUNT = "concurrent_count";

    // 某一分片的url
    String REQUEST_URL = "request_url";

    /**
     * 限速值
     */
    String SPEED_LIMIT = "speed_limit";

    /**
     * 限速字段 截取slt字段，file，locatedownload 4.0 接口
     */
    String SPEED_LIMIT_TYPE = "speed_limit_type";

    // 如果有多层错误码，可以分字段展示（http_code、pcs_code、lib_code等）
    String PCS_CODE = "pcs_code";

    String HTTP_CODE = "http_code";

    /**
     * 成功是1，失败：2 暂停：3 删除: 4
     */
    String TRANSFER_STATES = "is_success";

    // 分片类型下，此时该文件速度
    String INSTANT_SPEED_FILE = "instant_speed_file";

    // 分片类型下，此时调度模块的总速度
    String INSTANT_SPEED_ALL = "instant_speed_all";

    // 分片类型下，正在同时传输文件个数
    String FILE_NUM = "file_num";

    /**
     * 下载成功完成
     */
    int TRANSFER_FINISH = 1;

    /**
     * 下载失败
     */
    int TRANSFER_FAIL = 2;

    /**
     * 下载暂停
     */
    int TRANSFER_PAUSE = 3;

    /**
     * 下载删除
     */
    int TRANSFER_REMOVE = 4;

    /**
     * 是否为视频
     */
    String IS_VIDEO = "is_video";

    /**
     * 其他本地错误，如sdcard空间不足或者拒绝使用网络等
     */
    String OTHER_CODE = "other_code";

    /**
     * 错误的信息
     */
    String OTHER_ERROR_MESSAGE = "other_error_message";

    String AVERAGE_SPEED = "average_speed";

    // download，upload接口里面header字段值
    String X_BS_REQUEST_ID = "x_bs_request_id";

    // download，upload接口里面header字段值
    String X_PCS_REQUEST_ID = "x_pcs_request_id";

    // 下载SDK版本号
    String P2P_VERSIOM = "p2p_version";

    interface FileTypeKey extends TransferFieldKey {
        /**
         * file日志上报
         */
        String TYPE = "file";

        String HTTP_RANGE = "http_range";

        String DOWNLOAD_TYPE = "download_type";

        /**
         * 块总数
         */
        String BLOCK_NUM_ALL = "block_num_all";

        /**
         * 本次需要传输的块总数
         */
        String BLOCK_NUM_THIS_TIME = "block_num_this_time";
        
        enum DownloadType {
            Normal(1), SmoothDownload(2), P2PDownload(3);

            private int mValue;

            DownloadType(int value) {
                mValue = value;
            }

            public int getValue() {
                return mValue;
            }
        }

        String IS_SDK_DOWNLOAD = "is_sdk_download";
    }

    interface BlockTypeKey extends TransferFieldKey {
        String BLOCK_SIZE = "block_size";

        /**
         * 分块成功日志上报
         */
        String TYPE_BLOCK_SPEED = "block_speed";

        /**
         * 分块失败日志上报
         */
        String TYPE_BLOCK_FAIL = "block_fail";

        // 访问的host:上传/下载对应域名
        String SERVER_HOST = "server_host";

        // 请求的服务端host：上传/下载对应解析到的服务端ip
        // String SERVER_IP = "server_ip";
        // 限速类型:截取slt字段，file，download接口,locatedownload4.0的url里面才有
        // String SPEED_LIMIT_TYPE = "speed_limit_type";

        /**
         * 块的索引
         */
        String BLOCK_INDEX = "block_index";
    }
    interface SmoothFileTypeKey extends FileTypeKey {
        /**
         * m3u8任意一个ts的url
         */
        String M3U8_TS_URL = "m3u8_ts_url";

        /**
         * 请求host，scheme
         */
        String M3U8_URL_HOST = "m3u8_url_host";

        /**
         * 下载m3u8配置文件耗时
         */
        String M3U8_CONFIG_SPEND_TIME = "m3u8_config_spend_time";

        /**
         * 是否走了byteRange
         */
        String IS_BYTE_RANGE = "is_byte_range";
    }

    interface OpMoniterHeaderKey {
        String UA_TYPE_ANDROID = "android";
        String ERR_YES = "0";
        String ERR_NO = "1";

        String TYPE_NORMAL = "1";
        String TYPE_M3U8 = "2";
        String TYPE_P2P = "3";

        String SUBSYS_DOWNLOAD = "download";
        String SUBSYS_UPLOAD = "upload";
        String SUBSYS_THUMBNAIL = "thumbnail";
        String SUBSYS_VIDEO = "video";

        /** 顺序有特俗要求，不能更改 **/
        // 用户IP
        String USER_IP = "international_userip";
        // 功能名，表示是四大功能的哪个：download、upload、thumbnail、video
        String SUBSYS = "international_subsys";
        // p2p/cdn/idc
        String TYPE = "international_type";
        /**
         * 标记该任务是否成功完成，用于可用性计算
         *
         *     0：无需计损
         *
         *     1：需要计损
         */
        String ERR = "international_err";
        // 自定义的错误码
        String ERRNO = "international_errno";
        // 自定义的错误信息
        String ERRMSG = "international_errmsg";
        // 用户系统类型：iphone、android、web
        String UA_TYPE = "international_uatype";
        // 用户客户端版本
        String UA_VERSION = "international_uaversion";
        String SUCC = "international_succ";
        String ALL = "international_all";
    }
}
