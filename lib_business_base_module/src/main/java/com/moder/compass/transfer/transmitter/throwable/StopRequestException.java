package com.moder.compass.transfer.transmitter.throwable;

/**
 * 传输停止异常
 *
 * @author sunqi01
 */
public class StopRequestException extends Exception {
    private static final long serialVersionUID = 3600587640602555997L;
    public int mFinalStatus;

    private StopRequestException() {
        super();
    }

    public StopRequestException(int finalStatus, String message) {
        super(message);
        mFinalStatus = finalStatus;
    }

    public StopRequestException(int finalStatus, String message, Throwable throwable) {
        super(message, throwable);
        mFinalStatus = finalStatus;
    }

}
