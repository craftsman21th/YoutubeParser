
package com.moder.compass.transfer.task.process.download;

import com.dubox.drive.kernel.util.RFile;
import com.moder.compass.transfer.base.FileInfo;

/**
 * Created by liuliangping on 2015/7/6.
 */
public class LinkFileInfo extends FileInfo {
    private static final String TAG = "LinkFileInfo";
    public static final int TYPE = 104;

    @Override
    public int getType() {
        return TYPE;
    }

    /**
     * @param serverPath
     * @param localFile
     * @param size
     */
    public LinkFileInfo(String serverPath, RFile localFile, long size, String fileName) {
        super(serverPath, localFile, size, null, null, fileName);
    }
}
