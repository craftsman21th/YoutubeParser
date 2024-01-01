package com.moder.compass.backup;

import android.net.Uri;

import androidx.annotation.NonNull;


import com.moder.compass.albumbackup.AlbumBackupPathHelper;
import com.moder.compass.backup.IMediaPathHelper;

import java.util.ArrayList;

/**
 * 多媒体文件备份路径相关信息的处理接口
 *
 * Created by huantong on 2018/5/22.
 */
public interface IMediaBackupPathProcessor {
    /**
     * @return 备份映射路径存储的uri
     */
    Uri getBackupPathUri();

    /**
     * @return 存储本地待备份路径的key
     */
    String getBackupDirsKey();

    /**
     * 将备份路径保存至数据库
     *
     * @param bduss
     * @param dirPath 本地备份路径
     * @param remotePath 云端映射路径
     * @return
     */
    boolean saveToDatabase(String bduss, String dirPath, String remotePath);

    /**
     * 获取默认备份路径
     * 
     * @return
     */
    ArrayList<String> getDefaultBackupPath(@NonNull AlbumBackupPathHelper helper);

    String getRemoteDirPath(@NonNull String localDirPath, @NonNull IMediaPathHelper helper);

}
