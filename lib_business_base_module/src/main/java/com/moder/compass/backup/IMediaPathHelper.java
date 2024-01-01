package com.moder.compass.backup;

import java.util.ArrayList;
import java.util.List;

/**
 * MediaPathHelper对外组件接口
 *
 * @Author: guoqiqin
 * @CreateDate: 2019/10/15
 */
public interface IMediaPathHelper {
    ArrayList<String> getBackupPaths();

    boolean setBackupPaths(List<String> backupDirs);

    String getRemotePath(String path);

    String getRemoteDirPath(String dirPath);
}
