
package com.moder.compass.transfer.base.link;

import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.moder.compass.Target30StorageKt;
import com.moder.compass.transfer.base.FileInfo;
import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.IFileInfoGenerator;
import com.moder.compass.transfer.task.process.download.LinkFileInfo;

/**
 * Created by liuliangping on 2015/7/6.
 */
public class LinkFileInfoGenerator implements IFileInfoGenerator {
    private static final String TAG = "LinkFileInfoGenerator";
    private String parentPath = "/";

    public LinkFileInfoGenerator() {
    }

    public LinkFileInfoGenerator(String parentPath) {
        this.parentPath = parentPath;
    }

    @Override
    public FileInfo generate(IDownloadable file) {
        if (file == null) {
            DuboxLog.d(TAG, "FileInfo generate file = null");
            return null;
        }
        String localPath = Target30StorageKt.getLinkDownloadPath(
                BaseApplication.getInstance(),
                file,
                Account.INSTANCE.getNduss(),
                parentPath
        );
        return new LinkFileInfo(file.getFileDlink(), PathKt.rFile(localPath), file.getSize(),
                file.getFileName());
    }
}
