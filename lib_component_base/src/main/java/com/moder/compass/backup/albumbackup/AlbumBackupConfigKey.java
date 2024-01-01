/*
 * AlbumBackupConfigKey.java
 * classes : AlbumBackupConfigKey
 * @author caowenbin
 * V 1.0.0
 * Create at 2014-6-4 下午7:01:42
 */
package com.moder.compass.backup.albumbackup;

/**
 * 相册备份和恢复存配置文件中的key com.dubox.drive.backup.albumbackup.AlbumBackupConfig
 *
 * @author chenyuquan <br/>
 * create at 2013-1-30 下午4:49:34
 */
public interface AlbumBackupConfigKey {
    /**
     * 是否开启视频自动备份
     **/
    String VIDEO_AUTO_BACKUP = "video_auto_backup";
    /**
     * 备份是否全选
     **/
    String BACKUP_SELECT_ALL = "backup_select_all";
    /**
     * 是否是开启的视频原画无损备份
     */
    String VIDEO_AUTO_BACKUP_ORIGINAL = "video_auto_backup_original";
    /**
     * 是否成功的获取用户图片备份记录文件
     */
    String GET_BACKUP_PHOTO_SUCCESS = "get_backup_photo_success";

    /**
     * 是否成功的获取用户视频备份记录文件
     */
    String GET_BACKUP_VIDEO_SUCCESS = "get_backup_video_success";

    /**
     * 照片自动备份流程完成时间，存储的是long
     */
    String PHOTO_PROCESS_END_TIME = "photo_process_end_time";

    /**
     * 视频自动备份流程完成时间，存储的是long
     */
    String VIDEO_PROCESS_END_TIME = "video_process_end_time";

    String VIDEO_MAX_ID = "video_max_id";

    String IMAGE_MAX_ID = "image_max_id";

    /**
     * 清理已经不存在的备份记录的时间
     */
    String LAST_CLEAR_BACKUP_FILES_TIME = "last_clear_backup_files_time";
    /**
     * 是否需要保存图片备份记录
     */
    String NEED_SYNC_PHOTOS = "need_sync_photos";
    /**
     * 是否需要保存视频备份记录
     */
    String NEED_SYNC_VIDEOS = "need_sync_videos";

    /**
     * 上次图片全量对比的时间
     */
    String LAST_PHOTO_FULL_BACKUP_TIME = "last_photo_full_backup_time";

    /**
     * 上次视频全量对比的时间
     */
    String LAST_VIDEO_FULL_BACKUP_TIME = "last_video_full_backup_time";

    /**
     * 存储备份路径的key，将路径通过@#的方式分割，然后存储下来
     */
    String BACKUP_DIRS_KEY = "backup_dirs_key";
    /**
     * 存储图片备份路径的key，将路径通过@#的方式分割，然后存储下来
     */
    String PHOTO_BACKUP_DIRS_KEY = "photo_backup_dirs_key";
    /**
     * 存储视频备份路径的key，将路径通过@#的方式分割，然后存储下来
     */
    String VIDEO_BACKUP_DIRS_KEY = "video_backup_dirs_key";
    /**
     * 开启相册备份通知提示时间（年月）
     */
    String KEY_NOTIFICATION_ALBUM_BACKUP_TIME = "KEY_NOTIFICATION_ALBUM_BACKUP_TIME";
    /**
     * 最近一次开启相册备份 时间 毫秒时间
     */
    String KEY_LAST_TIME_ALBUM_BACKUP_OPEN_TIME = "KEY_LAST_TIME_ALBUM_BACKUP_OPEN_TIME";
    /**
     * 7.12 最近一次开启视频备份 时间 毫秒
     */
    String KEY_LAST_TIME_VIDEO_BACKUP_OPEN_TIME = "KEY_LAST_TIME_VIDEO_BACKUP_OPEN_TIME";

    String KEY_LAST_TIME_AUDIO_BACKUP_OPEN_TIME = "KEY_LAST_TIME_AUDIO_BACKUP_OPEN_TIME";
    /**
     * 未登录状态下，开启相册备份key
     */
    String KEY_ANONYMOUS_ALBUM_BACKUP_OPEN = "KEY_ANONYMOUS_ALBUM_BACKUP_OPEN";
    /**
     * /** 不再提示开启相册备份通知key
     */
    String KEY_NOTIFICATION_ALBUM_BACKUP_NO_LONGER_APPEAR =
        "KEY_NOTIFICATION_ALBUM_BACKUP_NO_LONGER_APPEAR";
    /**
     * 最后一次出现图片备份通知的时间 毫秒时间
     */
    String KEY_PHOTO_NOTIFICATION_LAST_TIME = "key_photo_notification_last_time";

    /**
     * 统计图片备份完成的数量
     */
    String PHOTO_BACKUP_SUCCESS_COUNT = "photo_backup_success_count";
    /**
     * 统计图片备份完成的数量记录时间
     */
    String PHOTO_BACKUP_RECORD_TIME = "photo_backup_record_time";

    /**
     * 每日自动备份通知栏弹出的次数
     */
    String DAY_PHOTO_BACKUP_NOTIFICATION_NUM = "day_photo_backup_notification_num";

}