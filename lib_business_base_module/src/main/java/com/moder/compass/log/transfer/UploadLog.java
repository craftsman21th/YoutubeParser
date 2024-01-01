package com.moder.compass.log.transfer;

/**
 * Created by liuliangping on 2016/3/23.
 */
public class UploadLog extends TransferLog {
    protected int mBlockIndex;

    private int concurrentCount;

    private boolean isConcurrentEnable;

    public UploadLog(String uid) {
        super(uid);
    }

    public String getLogUploadType() {
        String type;
        if (mCurrentUploadType == LogUploadType.FILE) {
            type = TransferFieldKey.FileTypeKey.TYPE;
        } else if (mCurrentUploadType == LogUploadType.BLOCK_SUCCESS) {
            type = TransferFieldKey.BlockTypeKey.TYPE_BLOCK_SPEED;
        } else {
            type = TransferFieldKey.BlockTypeKey.TYPE_BLOCK_FAIL;
        }
        return type;
    }

    @Override
    public int getTransferType() {
        return 0;
    }

    @Override
    public String getOpValue() {
        return TransferFieldKey.UPLOAD_OP_VALUE;
    }

    @Override
    public String getFileFid() {
        return "";
    }

    @Override
    public String getTransferByteKey() {
        return TransferFieldKey.UPLOAD_SEND_BYTES;
    }

    @Override
    public String getTransferTimeKey() {
        return TransferFieldKey.UPLOAD_SEND_TIMES;
    }

    @Override
    public void clear() {

    }

    public void setBlockIndex(int blockIndex) {
        this.mBlockIndex = blockIndex;
    }

    public int getBlockIndex() {
        return mBlockIndex;
    }

    /**
     * 当前并发上传正在运行的数量
     * @return
     */
    public int getConcurrentCount() {
        return concurrentCount;
    }

    /**
     * 设置并发上传正在运行的数量
     * @return
     */
    public void setConcurrentCount(int runningBlockCount) {
        this.concurrentCount = runningBlockCount;
    }

    /**
     * 并发是否开启
     * @return
     */
    public int getConcurrentState() {
        return isConcurrentEnable ? 1 : 0;
    }

    /**
     * 记录并发是否开启
     * @param concurrentEnable
     */
    public void setConcurrentEnable(boolean concurrentEnable) {
        isConcurrentEnable = concurrentEnable;
    }
}
