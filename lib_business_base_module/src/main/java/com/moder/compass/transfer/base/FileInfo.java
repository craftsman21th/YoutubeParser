package com.moder.compass.transfer.base;

import com.dubox.drive.kernel.util.RFile;

/**
 * 文件信息
 * 
 * @author sunqi01
 *
 * @update: 添加filename字段，因为外链下载，
 * RFile中存入的文件名可能因为系统存在相同名字文件而在后缀添加(1)
 * 
 */
public abstract class FileInfo {
    public String serverPath;
    public RFile localFile;
    public long size;
    public String mFid;
    public String fileName;

    /**
     * 云端文件md5
     * 
     * @since 7.9 2015-6-3
     * @author libin09
     */
    public final String serverMD5;

    public abstract int getType();

    /**
     * @param serverPath
     * @param size
     * @param serverMD5
     */
    public FileInfo(String serverPath, RFile localFile, long size, String serverMD5, String fid,
                    String fileName) {
        super();
        this.serverPath = serverPath;
        this.localFile = localFile;
        this.size = size;
        this.serverMD5 = serverMD5;
        this.mFid = fid;
        this.fileName = fileName;
    }

}
