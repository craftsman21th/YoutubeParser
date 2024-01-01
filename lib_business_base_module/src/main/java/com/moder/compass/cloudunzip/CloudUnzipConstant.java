package com.moder.compass.cloudunzip;

/**
 * Created by liji01 on 15-6-24.
 * 2021.3月  云解压组件化
 * 将外部组件使用到的通用参数整合到这个类中
 */
public interface CloudUnzipConstant {

    // 后台任务job的类型
    int TYPE_COPY_TASK_JOB = 1;
    int TYPE_DIFF_QUERY_TASK_JOB = TYPE_COPY_TASK_JOB + 1;

    /**
     * 压缩文件在pcs的路径
     */
    String EXTRA_KEY_PATH = "extra_key_path";
    /**
     * 压缩文件中的子目录路径
     */
    String EXTRA_KEY_SUBPATH = "extra_key_subpath";

    /**
     * 压缩文件中的多文件子目录路径
     */
    String EXTRA_KEY_SUBPATHS = "extra_key_subpaths";
    /**
     * 解压文件的指定目录
     */
    String EXTRA_KEY_TOPATH = "extra_key_topath";
    /**
     * 压缩文件大小
     */
    String EXTRA_KEY_SIZE = "extra_key_size";
    /**
     * 分享类产品类型，默认不传为私有文件
     */
    String EXTRA_KEY_PRODUCT = "extra_key_product";
    /**
     * 分享类产品的压缩文件的fsid
     */
    String EXTRA_KEY_FSID = "extra_key_fsid";
    /**
     * shareid/albumid
     */
    String EXTRA_KEY_PRIMARYID = "extra_key_primaryid";
    /**
     * uk
     */
    String EXTRA_KEY_UK = "extra_key_uk";
    /**
     * extra
     */
    String EXTRA_KEY_EXTRA = "extra_key_extra";

    /**
     * 压缩文件的密码
     */
    String EXTRA_KEY_PASSWORD = "extra_key_password";

    /**
     * 压缩文件的md5
     */
    String EXTRA_KEY_FILE_MD5 = "extra_key_file_md5";

    /**
     * 当前压缩包文件所在目录
     */
    String EXTRA_KEY_CURRENT_DIR = "extra_key_current_dir";

    /**
     * Bundle Key
     */
    String EXTRA_KEY_BUNDLE = "extra_key_bundle";

    String TASK_STATUS_SUCCESS = "success";
    String TASK_STATUS_FAILED = "failed";
    String TASK_STATUS_PENDING = "pending";
    String TASK_STATUS_RUNNING = "running";

    /**
     * 原始压缩包文件
     *
     * @since 8.0
     */
    String EXTRA_KEY_CLOUD_FILE = "extra_key_cloud_file";

    // 发给通知栏的广播
    String ACTION_UNZIP_NOTIFICATION_PROGRESS = "com.haha.netdisk.ACTION_UNZIP_NOTIFICATION_PROGRESS";

    // 解压进度
    String EXTRA_UNZIP_TASK_PROGRESS = "extra_unzip_task_progress";


    // 解压、下载任务完成状态
    String EXTRA_UNZIP_TASK_STATUS = "extra_unzip_task_status";

    // 解压、下载任务文件名
    String EXTRA_UNZIP_TASK_FILENAME = "extra_unzip_task_filename";

    // 错误状态
    String EXTRA_UNZIP_TASK_ERRNO = "extra_unzip_task_errno";

    // 目的地址
    String EXTRA_UNZIP_TASK_TOPATH = "extra_unzip_task_topath";

    // 解压任务类型
    String EXTRA_UNZIP_TASK_TYPE = "extra_unzip_task_type";

    // job类型
    String EXTRA_UNZIP_JOB_TYPE = "extra_unzip_job_type";

    // 文件大小
    String EXTRA_UNZIP_COPY_SUCCESS_FILE_SIZE = "extra_unzip_copy_success_file_size";

    // 发送给云解压内部的广播
    int SEND_DIALOG_BROADCAST = 1;

    // 发送给系统通知栏的广播
    int SEND_NOTIFICATION_BROADCAST = 2;
}
