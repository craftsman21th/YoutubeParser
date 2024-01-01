/*
 * ErrorCode.java
 * classes : com.dubox.drive.io.ErrorCode
 * @author 魏铮铮
 * V 1.0.0
 * Create at 2012-8-21 下午1:23:43
 */
package com.moder.compass.io;


import com.moder.compass.component.base.R;

/**
 * com.dubox.drive.io.ErrorCode
 * 
 * @author 陈昱全<br/>
 *         create at 2012-8-21 下午1:23:43
 */
public class ErrorCode {

    /**
     * 文件或目录不存在
     */
    public static final int ERROR_CODE_FILE_NOT_EXIST = -9;

    /**
     * 文件父目录或目标目录不存在
     */
    public static final int ERROR_CODE_PARENT_FILE_NOT_EXIST = -11;

    /**
     * -30 转存失败，文件已存在
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-4-24 下午03:53:36
     */
    public static final int ERROR_TRANSFER_FILE_AREADY_EXIST = -30;
    /**
     * -8 转存失败，分享连接已过期
     * 
     * @author 魏铮铮 V 1.0.0 Create at 2013-7-9
     */
    public static final int ERROR_TRANSFER_FILE_EXPIRED = -8;
    /**
     * -7 转存失败，不存在的shareid
     * 
     * @author 魏铮铮 V 1.0.0 Create at 2013-7-9
     */
    public static final int ERROR_TRANSFER_SHARE_ID_ERROR = -7;

    /**
     * -31 转存失败，数据库操作失败
     *
     * @author xujing V 9.6.0
     */
    public static final int ERROR_TRANSFER_DB_OPERATION_FAILED = -31;

    /**
     * -9 分享链接失效或分享文件被删除或移除
     *
     * @author xujing V 9.6.0
     */
    public static final int ERROR_TRANSFER_LINK_OR_FILE_INVALID = -9;

    /**
     * 4 转存接口查询失败
     *
     * @author xujing V 9.6.0
     */
    public static final int ERROR_TRANSFER_INTERFACE_ERROR = 4;

    /**
     * 114 转存PCS异步任务ID无效
     *
     * @author xujing V 9.6.0
     */
    public static final int ERROR_TRANSFER_PCS_TASK_ID_INVALID_ERROR = 114;

    /**
     * 111 转存已有任务进行中，需稍后重试
     *
     * @author xujing V 9.6.0
     */
    public static final int ERROR_TRANSFER_ALREADY_DOING_TASK_ERROR = 111;

    /**
     * -32 转存失败，用户剩余空间不足
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-4-24 下午03:53:52
     */
    public static final int ERROR_TRANSFER_NO_MORE_STORAGE = -32;

    /**
     * 转存数量超过5w
     */
    public static final int ERROR_TRANSFER_FILE_NUM_OVER_LIMIT = -33;

    /**
     * 转存失败，一次转存文件数目超过1000，小于3000
     *
     * @author 孙奇 V 1.0.0 Create at 2013-4-24 下午03:53:59
     */
    public static final int ERROR_TRANSFER_SAVE_OVER_NORMAL_LIMIT = 120;

    /**
     * 转存失败，转存自己的专辑失败
     */
    public static final int ERROR_TRANSFER_SELF_ALBUM = 144;

    /**
     * 转存失败，一次转存文件数目超过3000，小于50000
     *
     * @author 孙奇 V 1.0.0 Create at 2013-4-24 下午03:53:59
     */
    public static final int ERROR_TRANSFER_SAVE_OVER_VIP_LIMIT = 130;

    /**
     * 转存失败，一次转存文件数目超过50000
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-4-24 下午03:53:59
     */
    public static final int ERROR_TRANSFER_SAVE_OVER_SVIP_LIMIT = -33;

    /**
     * 云解压时，文件超过500M（PCS错误码）（PCS错误码）
     */
    public static final int ERROR_CLOUD_UNZIP_TO_BIG = 31183;

    /**
     * 36000 内部服务器错误
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 上午11:38:05
     */
    public static final int ERROR_INTERNAL_SERVER_ERROR = 36000;

    /**
     * 参数错误
     */
    public static final int ERROR_PARAM_INVALID = 36001;

    /**
     * 需要登陆
     */
    public static final int ERROR_NEED_LOGIN = 36003;

    /**
     * 需要登陆
     */
    public static final int ERROR_NEED_LOGIN2 = 36004;

    /**
     * 需要登陆
     */
    public static final int ERROR_NEED_LOGIN3 = 36005;

    /**
     * 需要激活
     */
    public static final int ERROR_NEED_ACTIVATE = 36006;

    /**
     * 用户不存在
     */
    public static final int ERROR_USER_NOT_FOUND = 36008;

    /**
     * 36009 网盘空间不足
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:02:12
     */
    public static final int ERROR_STORAGE_EXCEED_LIMIT = 36009;

    /**
     * 36010 对象不存在
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:03:02
     */
    public static final int ERROR_OBJECT_NOT_EXIST = 36010;

    /**
     * 超时，重试
     */
    public static final int ERROR_TIMEOUT = 36012;

    /**
     * 36013 太多的任务
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:09:24
     */
    public static final int ERROR_TOO_MANY_TASK = 36013;

    /**
     * 36014 存储路径已使用
     *
     * @author libin09 V 1.0.0 Create at 2013-10-24 下午12:09:24
     */
    public static final int ERROR_TARGET_PATH_IN_USE = 36014;

    /**
     * 36016 任务不存在
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:12:09
     */
    public static final int ERROR_TASK_NOT_FOUND = 36016;

    /**
     * 36017 取消失败，任务已经完成
     *
     * @author libin09 V 1.0.0 Create at 2013-10-24 下午12:12:09
     */
    public static final int ERROR_TASK_CANCEL_FAILED = 36017;

    /**
     * 36018 无效的资源名称
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:12:25
     */
    public static final int ERROR_INVALID_TORRENT = 36018;

    /**
     * 36019 任务正在处理中
     *
     * @author libin09 V 1.0.0 Create at 2013-10-24 下午12:12:09
     */
    public static final int ERROR_TASK_IN_PROCESS = 36019;

    /**
     * 无效的源URL
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:34:20
     */
    public static final int ERROR_INVALID_URL = 36020;

    /**
     * 普通用户离线下载数量超过限制
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:34:15
     */
    public static final int ERROR_USER_NOT_VIP_AND_EXCEED_CONCURRENCY = 36021;
    /**
     * VIP用户离线下载数量超过限制
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:36:31
     */
    public static final int ERROR_USER_IS_VIP_AND_EXCEED_CONCURRENCY = 36022;
    /**
     * 普通用户超出配额
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:36:47
     */
    public static final int ERROR_USER_NOT_VIP_AND_EXCEED_QUOTA = 36023;
    /**
     * VIP用户超出配额
     *
     * @author 孙奇 V 1.0.0 Create at 2013-5-19 下午12:37:05
     */
    public static final int ERROR_USER_IS_VIP_AND_EXCEED_QUOTA = 36024;

    /**
     * 分享链接已失效
     */
    public static final int ERROR_SHARELINK_INVALID = 36025;

    /**
     * 链接失效
     */
    public static final int ERROR_INVALID_LINK = 36026;

    /**
     * 离线文件含有违规内容无法下载
     */
    public static final int ERROR_FILE_CONTENT_ILLEGAL = 36032;

    /**
     * 应版权方通知无法下载
     */
    public static final int ERROR_COPYRIGHT = 36038;

    /**
     * 网络连接失败
     */
    public static final int ERROR_NETWORK_INVALID = 99999;

    /**
     * -10 转存失败，用户剩余空间不足
     * 
     * @author libin V 1.0.0 Create at 2013-4-24 下午03:53:52
     */
    public static final int ERROR_PAN_TRANSFER_NO_MORE_STORAGE = -10;

    /**
     * 外链转存文件数量超限(区别于原来的12)
     * @since 1.3.0
     */
    public static final int ERROR_SHARELINK_TRANS_OVERLIMIT = 17;
    /**
     * 创建分享时，文件名过词表失败(包含敏感字符)
     * */
    public static final int ERROR_INBOX_FILENAME_PASSDICTIONARY_FAIL = 4;

    /**
     * 创建分享时，用户列表中包含非网盘用户
     * */
    public static final int ERROR_INBOX_INCLUDE_UNDUBOXUSER = -62;

    /**
     * 创建分享时，写数据库失败
     * */
    public static final int ERROR_INBOX_WRITETODB_FAIL = -63;

    /**
     * 创建分享完成后，写redis添加最近好友失败
     * */
    public static final int ERROR_INBOX_WRITETOREDIS_FAIL = -64;

    /**
     * 创建分享时，fs_id不存在
     * */
    public static final int ERROR_INBOX_FSID_NOEXIST = -72;

    /**
     * 验证码校验失败
     * */
    public static final int ERROR_INBOX_VERTIFYCODE_FAIL = -74;

    /**
     * 收件箱转存错误码
     * */
    public static final int ERROR_INBOX_ALL_SUCCESS = 2;

    public static final int ERROR_INBOX_RECEIVING = 3;

    public static final int ERROR_INBOX_CHECK_ALL_FAIL = 4;

    public static final int ERROR_INBOX_CHECK_FAIL_TRANS_SUCCESS = 5;

    public static final int ERROR_INBOX_CHECK_FAIL_TRANS_FAIL = 6;

    public static final int ERROR_INBOX_CHECK_FAIL_TRANS_ALL_FAIL = 7;

    public static final int ERROR_INBOX_CHECK_SUCCESS_TRANS_FAIL = 8;

    public static final int ERROR_INBOX_CHECK_SUCCESS_TRANS_ALL_FAIL = 9;

    public static final int ERROR_INBOX_QUOTA_EXCEED = 10;

    public static final int ERROR_INBOX_TOO_MUCH_FILES = 11;

    public static final int ERROR_INBOX_FILE_ALL_EXISTS = 12;

    public static final int ERROR_INBOX_RECEIVING_MID_STATE = 20;

    /**
     * 获取离线下载错误文案资源
     *
     * @param errorCode 服务器返回的错误码
     * @return
     */
    public static int getOfflineDownloadErrorResId(int errorCode) {
        switch (errorCode) {
            case ERROR_INTERNAL_SERVER_ERROR:
                return R.string.internal_server_error;
//            case ERROR_PARAM_INVALID:
//                return R.string.offline_failed;
            case ERROR_NEED_LOGIN:
                return R.string.not_login_or_account_not_exist;
            case ERROR_NEED_LOGIN2:
                return R.string.not_login_or_account_not_exist;
            case ERROR_NEED_LOGIN3:
                return R.string.login_and_try_again;
            case ERROR_NEED_ACTIVATE:
                return R.string.need_activate;
            case ERROR_USER_NOT_FOUND:
                return R.string.user_not_found;
            case ERROR_STORAGE_EXCEED_LIMIT:
                return R.string.storage_exceed_limit;
            case ERROR_OBJECT_NOT_EXIST:
                return R.string.file_not_exists;
            case ERROR_TIMEOUT:
                return R.string.timeout;
            case ERROR_TOO_MANY_TASK:
                return R.string.too_many_tasks;
            case ERROR_TARGET_PATH_IN_USE:
                return R.string.bt_save_failed_target_path_in_use;
            case ERROR_TASK_NOT_FOUND:
                return R.string.task_was_not_found;
            case ERROR_TASK_CANCEL_FAILED:
                return R.string.bt_save_failed_cancel_failed;
            case ERROR_INVALID_TORRENT:
                return R.string.invalid_torrent;
            case ERROR_TASK_IN_PROCESS:
                return R.string.bt_save_failed_in_process;
//            case ERROR_INVALID_LINK:
//            case ERROR_INVALID_URL:
//                return R.string.invalid_source_url;
            case ERROR_USER_NOT_VIP_AND_EXCEED_CONCURRENCY:
                return R.string.too_many_tasks;
            case ERROR_USER_IS_VIP_AND_EXCEED_CONCURRENCY:
                return R.string.too_many_tasks;
            case ERROR_USER_NOT_VIP_AND_EXCEED_QUOTA:
                return R.string.too_many_tasks;
            case ERROR_TRANSFER_NO_MORE_STORAGE:
                return R.string.transfer_error_no_storage;
            case ERROR_TRANSFER_FILE_AREADY_EXIST:
                return R.string.transfer_error_file_already_exist;
            case ERROR_NETWORK_INVALID:
                return R.string.network_exception;
            case ERROR_COPYRIGHT:
                return R.string.download_is_unavailable;
            default:
                return R.string.transfer_error;
        }
    }
}
