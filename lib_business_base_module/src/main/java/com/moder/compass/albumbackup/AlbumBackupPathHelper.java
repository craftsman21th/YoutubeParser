package com.moder.compass.albumbackup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.moder.compass.BaseApplication;
import com.moder.compass.backup.IMediaPathHelper;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceInfo;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageManager;
import com.dubox.drive.kernel.android.util.storage.DeviceStorageUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.os.Build;
import android.text.TextUtils;

/**
 * Created by liji01 on 15-9-2.
 */
public class AlbumBackupPathHelper implements IMediaPathHelper {
    private static final String TAG = "AlbumBackupPathHelper";

    // 相册备份的文件夹
    public static final String ALBUM_BACKUP_DIR = "/From：" + Build.MODEL;

    /**
     * 生成云端的路径
     *
     * @param dirPath 文件夹的路径
     */
    @Override
    public String getRemoteDirPath(String dirPath) {
        String remotePath = null;
        if (isFirstStorageBackupDir(dirPath) || isSecondStorageBackupDir(dirPath)) {
            File file = new File(dirPath);
            String mid = "";
            if (dirPath.equals(getFirstStorageBackupDir())) { // 如果是默认SDCARD的DCIM目录
                remotePath = ALBUM_BACKUP_DIR + File.separator + DeviceInfo.getDCIMpath();
            } else if (dirPath.equals(getSecondStorageBackupDir())) { // 如果是第二张卡的DCIM目录
                remotePath = ALBUM_BACKUP_DIR + File.separator + DeviceInfo.getDCIMpath() + "(1)";
            } else {
                for (File f = file.getParentFile(); f != null; f = f.getParentFile()) {
                    String parentPath = f.getAbsolutePath();
                    DuboxLog.d(TAG, "parentpath=" + parentPath + " defaultbackupdir=" + getFirstStorageBackupDir());
                    if (parentPath.equals(getFirstStorageBackupDir())) {
                        // 如果递归到了父路径等于默认的相册备份的文件夹路径
                        // 如果是DCIM的子目录要保证结果是DCIM打头
                        remotePath = ALBUM_BACKUP_DIR + File.separator + DeviceInfo.getDCIMpath() + File.separator + mid
                            + file.getName();
                        break;
                    } else if (parentPath.equals(getSecondStorageBackupDir())) {
                        // 如果是DCIM的子目录要保证结果是DCIM打头
                        remotePath = ALBUM_BACKUP_DIR + File.separator + DeviceInfo.getDCIMpath() + "(1)"
                            + File.separator + mid + file.getName();
                        break;
                    }
                    mid = f.getName() + File.separator + mid;
                }
            }
        } else {
            remotePath = ALBUM_BACKUP_DIR + File.separator + new File(dirPath).getName();
        }
        return remotePath;
    }

    @Override
    public ArrayList<String> getBackupPaths() {
        return null;
    }

    @Override
    public boolean setBackupPaths(List<String> backupDirs) {
        return false;
    }

    @Override
    public String getRemotePath(String path) {
        return "";
    }

    /**
     * 是否是默认目录下的子目录
     *
     * @param path
     *
     * @return
     */
    private boolean isFirstStorageBackupDir(final String path) {
        final String firstPath = getFirstStorageBackupDir();
        return (!TextUtils.isEmpty(firstPath) && path.startsWith(firstPath));
    }

    /**
     * 是否是第二张sdcard要备份的目录
     *
     * @param path
     *
     * @return
     */
    private boolean isSecondStorageBackupDir(final String path) {
        final String secondPath = getSecondStorageBackupDir();
        return (!TextUtils.isEmpty(secondPath) && path.startsWith(secondPath));
    }

    /**
     * 获取默认的备份路径，针对于单卡手机
     *
     * @return
     */
    public String getFirstStorageBackupDir() {
        if (!DeviceStorageUtils.isSDCardExists()) {
            return "";
        }
        DuboxLog.d(TAG, "default sdcard path=" + DeviceStorageUtils.getSDPath());
        return DeviceStorageUtils.getSDPath() + FileUtils.PATH_CONNECTOR + DeviceInfo.getDCIMpath();
    }

    /**
     * 添加默认路径
     */
    public String getSecondStorageBackupDir() {

        String secondaryStoragePath = DeviceStorageManager.createDevicesStorageManager(BaseApplication.getInstance())
            .getSecondaryStoragePath();
        if (!TextUtils.isEmpty(secondaryStoragePath)) {
            if (secondaryStoragePath.endsWith(File.separator)) {
                return (secondaryStoragePath + DeviceInfo.getDCIMpath());
            } else {
                return (secondaryStoragePath + File.separator + DeviceInfo.getDCIMpath());
            }
        }
        return null;
    }

}
