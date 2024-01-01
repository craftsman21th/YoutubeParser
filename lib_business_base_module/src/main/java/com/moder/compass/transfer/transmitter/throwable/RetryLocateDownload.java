package com.moder.compass.transfer.transmitter.throwable;

import com.moder.compass.transfer.transmitter.ErrorMessageHelper;

public class RetryLocateDownload extends Throwable {
    private static final String TAG = "RetryLocateDownload";
    public int mFinalStatus;

    // 重试次数
    public int mCount = ErrorMessageHelper.NO_REDO_DEFAULT;

    private RetryLocateDownload() {
        super();
    }

    public RetryLocateDownload(int finalStatus, String message) {
        super(message);
        mFinalStatus = finalStatus;
    }

    public RetryLocateDownload(int finalStatus, String message, Throwable throwable) {
        super(message, throwable);
        mFinalStatus = finalStatus;
    }

    public RetryLocateDownload(int finalStatus, String message, int retryCount) {
        super(message);
        mFinalStatus = finalStatus;
        mCount = retryCount;
    }
}
