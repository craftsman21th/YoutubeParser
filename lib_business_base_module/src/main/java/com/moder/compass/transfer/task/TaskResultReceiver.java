package com.moder.compass.transfer.task;

import com.dubox.drive.util.WeakRefResultReceiver;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Task 相关的resultReceiver 抽象类
 * 
 * @author 孙奇 <br/>
 *         create at 2013-6-27 下午02:53:37
 */

public abstract class TaskResultReceiver<T> extends WeakRefResultReceiver<T> {
    private static final String TAG = "TaskResultReceiver";

    /**
     * resultCode 成功
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午02:56:49
     */
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;

    public TaskResultReceiver(T reference, Handler handler) {
        super(reference, handler);
    }

    @Override
    protected boolean onInterceptResult(@NonNull T reference, int resultCode, @Nullable Bundle resultData) {
        switch (resultCode) {
            case STATUS_SUCCESS:
                handleSuccess(reference);
                break;
            case STATUS_FAILED:
                handleFailed(reference);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 成功的回调
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:03:31
     */
    public abstract void handleSuccess(@NonNull T reference);

    /**
     * 失败的回调
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:03:38
     */
    public abstract void handleFailed(@NonNull T reference);

    /**
     * 发送成功
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:06:42
     */
    public void sendSuccess() {
        send(STATUS_SUCCESS, null);
    }

    /**
     * 发送失败
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-6-27 下午03:06:50
     */
    public void sendFailed() {
        send(STATUS_FAILED, null);
    }

}
