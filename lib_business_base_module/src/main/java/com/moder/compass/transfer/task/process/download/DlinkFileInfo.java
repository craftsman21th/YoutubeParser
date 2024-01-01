package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.base.FileInfo;

/**
 * 用DLINK下载的文件Info
 * 
 * @author sunqi01
 * 
 */
public class DlinkFileInfo extends FileInfo {
    private static final String TAG = "DlinkFileInfo";
    public static final int TYPE = 100;

    /**
     * 资源文件的serverPath
     */
    public final String dlinkServerPath;
    public final String uk;
    /**
     * 可以是shareId也可以是albumId
     */
    public final String resourceId;
    public final String fid;
    /**
     * 可以是DLINK 也可以是ALBUM
     */
    public final int resourceType;

    @Override
    public int getType() {
        return TYPE;
    }

    /**
     * @param serverPath
     * @param size
     * @param dlinkServerPath
     * @param uk
     * @param resourceId
     * @param fid
     * @param resourceType
     */
    public DlinkFileInfo(String serverPath, RFile localFile, long size, String dlinkServerPath, String uk,
                         String resourceId, String fid, int resourceType, String fileName) {
        super(serverPath, localFile, size, null, fid, fileName);
        this.dlinkServerPath = dlinkServerPath;
        this.uk = uk;
        this.resourceId = resourceId;
        this.fid = fid;
        this.resourceType = resourceType;
    }
}
