
package com.moder.compass.transfer.task.process.download.link;

import com.moder.compass.transfer.base.FileInfo;
import com.moder.compass.transfer.base.Processor;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.task.process.download.DownloadProcessorHelper;

/**
 * 新建其他文件的下载任务
 * 
 * @author sunqi01
 * 
 */
class NewLinkDownloadFileProcessor extends Processor {
    private FileInfo mFileInfo;
    private final boolean mIsNotify;
    private final DownloadProcessorHelper mProcesserHelper;
    private final String mBduss;
    private final String mUid;
    private final int mFrom;

    NewLinkDownloadFileProcessor(FileInfo fileInfo, boolean isNotify, String bduss, String uid, int from) {
        super();
        this.mFileInfo = fileInfo;
        mIsNotify = isNotify;
        mProcesserHelper = new DownloadProcessorHelper(bduss, uid);
        mBduss = bduss;
        mUid = uid;
        mFrom = from;
    }

    @Override
    public void process() {
        LinkDownloadTask task =
                new LinkDownloadTask(mFileInfo.localFile, mFileInfo.serverPath, mFileInfo.size, null, mBduss, mUid);
        task.setTransmitterType(TransferContract.DownloadTasks.TRANSMITTER_TYPE_LINK);
        mProcesserHelper.addDownloadFile(task, mIsNotify, mOnAddTaskListener);
    }
}
