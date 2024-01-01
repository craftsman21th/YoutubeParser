package com.moder.compass.transfer.transmitter.throwable;

/**
 * 重试
 *
 * @author sunqi01
 */
public class Retry extends Throwable {
    private static final String TAG = "Retry";
    public int mFinalStatus;

    private Retry() {
        super();
    }

    public Retry(int finalStatus, String message) {
        super(message);
        mFinalStatus = finalStatus;
    }

    public Retry(int finalStatus, String message, Throwable throwable) {
        super(message, throwable);
        mFinalStatus = finalStatus;
    }
}
