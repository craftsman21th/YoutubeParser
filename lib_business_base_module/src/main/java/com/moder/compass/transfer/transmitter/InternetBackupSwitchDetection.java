package com.moder.compass.transfer.transmitter;

/**
 * Created by xujing31 on 2019/4/3.
 */

public class InternetBackupSwitchDetection {
    public static final int NO_CHECK = 0;

    public static final int PHOTO_CHECK = 1;

    public static final int VIDEO_CHECK = 2;

    /**
     * 文件夹备份检查
     */
    public static final int DIRECTORY_CHECK = 3;

    private boolean checkPhoto;
    private boolean checkVideo;
    private boolean checkDirectory;

    public InternetBackupSwitchDetection(int type) {
        if (type == PHOTO_CHECK) {
            checkPhoto = true;
            checkVideo = false;
            checkDirectory = false;
        } else if (type == VIDEO_CHECK) {
            checkPhoto = false;
            checkVideo = true;
            checkDirectory = false;
        } else if (type == DIRECTORY_CHECK) {
            checkPhoto = false;
            checkVideo = false;
            checkDirectory = true;
        } else {
            checkPhoto = false;
            checkVideo = false;
            checkDirectory = false;
        }
    }


    public boolean needCheckPhotoType() {
        return checkPhoto;
    }


    public boolean needCheckVideoType() {
        return checkVideo;
    }

    /**
     * 是否需要检查文件夹自动备份
     * @return
     */
    public boolean needCheckDirectoryType() {
        return checkDirectory;
    }


}
