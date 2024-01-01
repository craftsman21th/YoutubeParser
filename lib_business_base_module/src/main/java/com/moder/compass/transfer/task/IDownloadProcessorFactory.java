
package com.moder.compass.transfer.task;

import com.dubox.drive.cloudfile.base.IDownloadable;
import com.moder.compass.transfer.base.Processor;

/**
 * Created by liuliangping on 2015/2/3.
 */
public interface IDownloadProcessorFactory {
    int TYPE_NEW_TASK = 100;
    int TYPE_NEW_TASK_AND_REMOVE_LAST_TASK = 101;
    int TYPE_NEW_FINISHED_TASK = 102;
    int TYPE_NEW_FINISHED_TASK_AND_COPY_TO_NEW_PATH = 103;
    int TYPE_SET_TASK_STATE_TO_PENDING = 104;
    int TYPE_START_SCHEDULER = 105;
    int TYPE_NEW_TASK_RENAME_BACKUP = 107;
    int TYPE_NEW_TASK_AND_REMOVE_LAST_TASK_AND_RENAME_BACKUP = 108;

    /**
     * 工厂方法
     * @param downloadable
     * @param isNotify
     * @param bduss
     * @param uid
     * @param downloadPriority
     * @return
     */
    Processor createProcessor(IDownloadable downloadable, boolean isNotify, String bduss, String uid,
                              int downloadPriority);
}
