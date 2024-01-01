/*
 * AlbumBackupOption.java
 * classes : AlbumBackupOption
 * @author caowenbin
 * V 1.0.0
 * Create at 2014-5-26 下午9:51:40
 */
package com.moder.compass.backup.albumbackup;

import static com.moder.compass.base.utils.PersonalConfigKey.PHOTO_AUTO_BACKUP;
import static com.moder.compass.statistics.UserFeatureKeysKt.KEY_USER_FEATURE_PHOTO_BACKUP_OPEN;
import static com.moder.compass.statistics.UserFeatureKeysKt.KEY_USER_FEATURE_VIDEO_BACKUP;

import com.moder.compass.base.utils.EventCenterHandler;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.moder.compass.base.utils.EventCenterHandlerKt;
import com.moder.compass.statistics.UserFeatureReporter;

/**
 * AlbumBackupOption
 *
 * @author caowenbin <br/>
 * create at 2014-5-26 下午9:51:40
 */
public class AlbumBackupOption {
    @SuppressWarnings("unused")
    private static final String TAG = "AlbumBackupOption";

    public boolean isPhotoEnable() {
        return PersonalConfig.getInstance().getBoolean(PHOTO_AUTO_BACKUP, false);
    }

    public void setPhotoEnable(boolean enable) {
        PersonalConfig.getInstance().putBoolean(PHOTO_AUTO_BACKUP, enable);
        if (enable) {
            EventCenterHandler.INSTANCE.sendMsg(EventCenterHandlerKt.MESSAGE_OPEN_ALBUM_AUTO_BACKUP);
            new UserFeatureReporter(KEY_USER_FEATURE_PHOTO_BACKUP_OPEN).reportAFAndFirebase();
        }
    }

    public void setVideoEnable(boolean enable) {
        PersonalConfig.getInstance().putBoolean(AlbumBackupConfigKey.VIDEO_AUTO_BACKUP, enable);
        if (!enable) {
            PersonalConfig.getInstance().remove(AlbumBackupConfigKey.VIDEO_AUTO_BACKUP_ORIGINAL);
        } else {
            new UserFeatureReporter(KEY_USER_FEATURE_VIDEO_BACKUP).reportAFAndFirebase();
        }
        PersonalConfig.getInstance().commit();
    }

    public boolean isVideoEnable() {
        return PersonalConfig.getInstance().getBoolean(AlbumBackupConfigKey.VIDEO_AUTO_BACKUP, false);
    }

    /**
     * 相册是否被全部选中
     */
    public boolean isSelectAll() {
        return PersonalConfig.getInstance().getBoolean(AlbumBackupConfigKey.BACKUP_SELECT_ALL, false);
    }

    /**
     * 设置相册是否被全部选中
     */
    public void setSelectAll(boolean enable) {
        PersonalConfig.getInstance().putBoolean(AlbumBackupConfigKey.BACKUP_SELECT_ALL, enable);
        PersonalConfig.getInstance().commit();
    }

    public boolean isPhotoOrVideoEnable() {
        return isPhotoEnable() || isVideoEnable();
    }
    public boolean isPhotoAndVideoEnable() {
        return isPhotoEnable() && isVideoEnable();
    }

    /**
     * 当前备份照片的品质是否是原图
     *
     * @return
     */
    public boolean isOriginPhoto() {
        return true; // CompressConstant.COMPRESS_IMAGE_LEVEL_0 == getPhotoCompressLevel();
    }

}
