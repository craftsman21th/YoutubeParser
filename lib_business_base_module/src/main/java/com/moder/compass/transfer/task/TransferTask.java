package com.moder.compass.transfer.task;

import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.android.util.file.FileUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.RFile;
import com.dubox.drive.db.transfer.contract.TransferContract;
import com.moder.compass.transfer.transmitter.Transmitter;
import com.moder.compass.transfer.transmitter.p2p.OnP2PTaskListener;
import com.moder.compass.transfer.transmitter.p2p.P2PSDKCallbackProxy;
import com.moder.compass.transfer.util.TransferUtil;

import android.content.ContentResolver;
import android.database.Cursor;

public abstract class TransferTask {
    private final static String TAG = "TransferTask";
    // task type
    public static final int TYPE_TASK_UPLOAD = 0;
    public static final int TYPE_TASK_DOWNLOAD = 1;
    public static final int TYPE_TASK_PHOTO = 2;
    public static final int TYPE_TASK_VIDEO = 3;
    public Transmitter transmitter;

    /**
     * 插入数据库后赋值的Task的唯一标识
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-9 上午11:19:23
     */
    public int mTaskId = -1;

    /**
     * 本地文件
     */
    public RFile mLocalFileMeta;
    public String mRemoteUrl = "";
    /**
     * TASK对应的文件的名字
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-13 下午08:34:34
     */
    public String mFileName = "";

    public int mType = TYPE_TASK_UPLOAD;

    public long mSize = 0;

    public long mOffset;

    /**
     * 队列失败信息编号 类型
     **/
    public int extraInfoNum = TransferContract.Tasks.EXTRA_INFO_NUM_DEFAULT;

    /**
     * 区分传输器类型
     *
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:12:33
     */
    public String mTransmitterType;

    public int mState;

    // task 优先级
    public int mPriority;

    public boolean mIsP2PFailed;

    /**
     * p2p 标识p2p任务的fgid
     */
    public String mP2PFgid;

    public boolean mIsP2PTask;

    public boolean mIsDownloadSDKTask;

    public TransferTask(RFile localFile, String remotePath) {
        mLocalFileMeta = localFile;
        mRemoteUrl = remotePath;
        if (mRemoteUrl == null) {
            mRemoteUrl = "";
        }
        if (!HostURLManager.isNetURL(mRemoteUrl)) {
            if (mRemoteUrl.startsWith("//")) {
                mRemoteUrl = mRemoteUrl.replace("//", FileUtils.PATH_CONNECTOR);
            } else {
                mRemoteUrl = mRemoteUrl.replaceAll("//", FileUtils.PATH_CONNECTOR);
            }
        }
        if (mLocalFileMeta != null) {
            setFileName(mLocalFileMeta.name());
        }
//        setFileName(TransferUtil.getFileNameDisplay(mRemoteUrl));

    }

    public TransferTask(Cursor cursor) {
        mTaskId = cursor.getInt(cursor.getColumnIndex(TransferContract.Tasks._ID));

        final int typeColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.TYPE);
        if (typeColumnIndex >= 0) {
            mType = cursor.getInt(typeColumnIndex);
        }

        final int remoteColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.REMOTE_URL);
        if (remoteColumnIndex >= 0) {
            mRemoteUrl = cursor.getString(remoteColumnIndex);
        }

        // 适配Target30以后，该值可能是一个uri.toString()的字符串
        String mLocalUrl = cursor.getString(cursor.getColumnIndex(TransferContract.Tasks.LOCAL_URL));
        mLocalFileMeta = PathKt.rFile(mLocalUrl);
        final int sizeColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.SIZE);
        if (sizeColumnIndex >= 0) {
            mSize = cursor.getLong(sizeColumnIndex);
        }

        final int offsetSizeColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.OFFSET_SIZE);
        if (offsetSizeColumnIndex >= 0) {
            mOffset = cursor.getLong(offsetSizeColumnIndex);
        }

        final int stateColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.STATE);
        if (stateColumnIndex >= 0) {
            mState = cursor.getInt(stateColumnIndex);
        }

        final int transmitterTypeColumnIndex = cursor.getColumnIndex(TransferContract.Tasks.TRANSMITTER_TYPE);
        mTransmitterType = transmitterTypeColumnIndex >= 0 ? cursor.getString(transmitterTypeColumnIndex) : null;

        if (mLocalFileMeta != null) {
            setFileName(mLocalFileMeta.name());
        }
//        setFileName(TransferUtil.getFileNameDisplay(mRemoteUrl));

        final int priorityIndex = cursor.getColumnIndex(TransferContract.Tasks.PRIORITY);
        if (priorityIndex >= 0) {
            mPriority = cursor.getInt(priorityIndex);
        }

        final int p2pStateIndex = cursor.getColumnIndex(TransferContract.Tasks.IS_P2P_FAILED);
        if (p2pStateIndex >= 0) {
            mIsP2PFailed = TransferContract.Tasks.YES == cursor.getInt(p2pStateIndex);
            DuboxLog.d(TAG, "mIsP2PFailed:" + mIsP2PFailed);
        }

        final int p2pFgidIndex = cursor.getColumnIndex(TransferContract.Tasks.P2P_FGID);
        if (p2pFgidIndex >= 0) {
            mP2PFgid = cursor.getString(p2pFgidIndex);
            DuboxLog.d(TAG, "p2p fgid:" + mP2PFgid);
        }

        final int p2pTaskIndex = cursor.getColumnIndex(TransferContract.Tasks.IS_P2P_TASK);
        if (p2pTaskIndex >= 0) {
            mIsP2PTask = TransferContract.Tasks.YES == cursor.getInt(p2pTaskIndex);
            DuboxLog.d(TAG, "p2p mIsP2PTask:" + mIsP2PTask);
        }

        final int sdkTaskIndex = cursor.getColumnIndex(TransferContract.Tasks.IS_DOWNLOAD_SDK_TASK);
        if (sdkTaskIndex >= 0) {
            mIsDownloadSDKTask = TransferContract.Tasks.YES == cursor.getInt(sdkTaskIndex);
            DuboxLog.d(TAG, "sdk sdkTaskIndex:" + mIsDownloadSDKTask);
        }
    }

    /**
     * 获取TransferTask的本地路径
     *
     * @return
     */
    public String getLocalUrl() {
        return TransferUtil.removeBN(mLocalFileMeta.localUrl());
    }

    /**
     * 获取路径对应的FILE
     *
     * @return
     */
    public RFile getFile() {
        return mLocalFileMeta;

    }

    public int getExtraInfoNum() {
        return extraInfoNum;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof TransferTask)) {
            return false;
        } else if (((TransferTask) o).mTaskId == this.mTaskId) {
            return true;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return mTaskId;
    }

    /**
     * 设置传输器TYPE
     *
     * @param transmitterType
     *
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:23:33
     */
    public void setTransmitterType(String transmitterType) {
        this.mTransmitterType = transmitterType;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    /**
     * 绑定传输器
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-10 下午04:53:23
     */
    protected abstract Transmitter getTransmitter(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                                  OnP2PTaskListener onP2PTaskListener);

    protected abstract void performStart(ContentResolver resolver, P2PSDKCallbackProxy aP2PSDKCallbackProxy,
                                         OnP2PTaskListener onP2PTaskListener);

    protected abstract void performPause();

    protected abstract void performRemove(boolean isDeleteFile);

    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public String getFileName() {
        return mFileName;
    }

    @Override
    public String toString() {
        return "TransferTask [mTaskId=" + mTaskId + ", mLocalUrl=" + mLocalFileMeta.localUrl() + ", mRemoteUrl="
            + mRemoteUrl
            + ", mFileName=" + mFileName + ", mType=" + mType + ", mSize=" + mSize + ", mOffset=" + mOffset
            + ", state=" + mState + ",transmitter=" + transmitter
            + "]";
    }
}
