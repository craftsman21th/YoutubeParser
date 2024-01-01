
package com.moder.compass.transfer.base;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.task.TransferTask;

/**
 * Created by liuliangping on 2015/1/30.
 */
public interface ITaskGenerator {
    TransferTask generate(IDownloadable downloadable, String bduss, String uid);
}
