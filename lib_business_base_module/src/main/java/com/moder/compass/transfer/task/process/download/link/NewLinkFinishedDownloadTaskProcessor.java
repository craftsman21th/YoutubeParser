
package com.moder.compass.transfer.task.process.download.link;

import com.moder.compass.transfer.base.FileInfo;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.process.download.DownloadProcessorHelper;

class NewLinkFinishedDownloadTaskProcessor extends Processor {
    private FileInfo mFileInfo;
    private final boolean mIsNotify;
    private String mBduss;
    private final String mUid;
    private int mFrom;

    NewLinkFinishedDownloadTaskProcessor(FileInfo fileInfo, boolean isNotify, String bduss, String uid, int from) {
        super();
        this.mFileInfo = fileInfo;
        mIsNotify = isNotify;
        mBduss = bduss;
        mUid = uid;
        mFrom = from;
    }

    @Override
    public void process() {
        LinkDownloadTask newTask =
            new LinkDownloadTask(mFileInfo.localFile, mFileInfo.serverPath, mFileInfo.size, null, mBduss, mUid);
        newTask.setTransmitterType(TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK);
        new DownloadProcessorHelper(mBduss, mUid).addDownloadFile(newTask, mIsNotify, mOnAddTaskListener);

    }
}
