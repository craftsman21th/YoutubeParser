
package com.moder.compass.transfer.task.process.download.link;

import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.TransferTask;
import com.moder.compass.transfer.task.process.download.DownloadProcessorHelper;
import com.moder.compass.transfer.base.FileInfo;
import com.moder.compass.transfer.base.Processor;

class NewLinkDownloadTaskAndRemoveLastTaskProcessor extends Processor {

    private FileInfo mFileInfo;
    private TransferTask task;
    private final boolean mIsNotify;
    private final String mBduss;
    private final String mUid;
    private final DownloadProcessorHelper mProcesserHelper;
    private final int mFrom;

    NewLinkDownloadTaskAndRemoveLastTaskProcessor(FileInfo mFileInfo, TransferTask task, boolean isNotify, String bduss,
                                                  String uid, int from) {
        super();
        this.mFileInfo = mFileInfo;
        this.task = task;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mProcesserHelper = new DownloadProcessorHelper(mBduss, uid);
        mFrom = from;
    }

    @Override
    public void process() {
        // sunqi 重新下载并且删除原来的文件
        mProcesserHelper.cancelTask(task.mTaskId);// 删除原来的任务
        LinkDownloadTask newTask =
                new LinkDownloadTask(mFileInfo.localFile, mFileInfo.serverPath, mFileInfo.size, null, mBduss, mUid);
        newTask.setTransmitterType(TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK);
        mProcesserHelper.addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);
    }

}
