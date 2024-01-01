package com.moder.compass.transfer.transmitter.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 定时器类
 * 
 * @author 孙奇 <br/>
 *         create at 2012-8-28 上午10:25:14
 */
public class TimerHelper {

    /**
     * 触发延时
     */
    private long mDelay;

    /**
     * 是否需要循环执行
     */
    private final boolean mIsRepeat;

    private TimerProcesser mProcessor;

    private Handler mHandler;

    private HandlerThread mHandlerThread;

    private Runnable mRunnable;

    /**
     * 构造延时器
     * 
     * @param delay
     * @param process
     */
    public TimerHelper(long delay, TimerProcesser process) {
        this.mProcessor = process;
        this.mDelay = delay;
        this.mIsRepeat = false;
    }

    /**
     * 构造延时器，支持指定是否需要循环执行
     *
     * @param delay 延迟时间
     * @param isRepeat 是否需要循环执行
     * @param process 处理类
     */
    public TimerHelper(long delay, boolean isRepeat, TimerProcesser process) {
        this.mDelay = delay;
        this.mIsRepeat = isRepeat;
        this.mProcessor = process;
    }

    /**
     * 开始计时
     */
    public void startTimer() {
        mHandlerThread =
                new HandlerThread(mProcessor.getClass().getSimpleName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
        if (mHandlerThread == null) {
            return;
        }
        mHandlerThread.start();
        mHandler = new TimerHandler<TimerHelper>(TimerHelper.this, mHandlerThread.getLooper());

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mProcessor != null) {
                    mProcessor.doProcess();
                }
                if (mIsRepeat && mHandler != null) {
                    mHandler.postDelayed(this, mDelay);
                }
            }
        };
        mHandler.postDelayed(mRunnable, mDelay);
    }

    /**
     * 取消本次计时
     */
    public void stopTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }

        mHandler = null;
        mHandlerThread = null;
    }

    private static class TimerHandler<T> extends Handler {
        private WeakReference<T> mReference;

        TimerHandler(T reference, Looper looper) {
            super(looper);
            mReference = new WeakReference<T>(reference);
        }

        public TimerHandler(T reference) {
            super();
            mReference = new WeakReference<T>(reference);
        }

        /**
         * Handle system messages here.
         *
         * @param msg
         */
        @Override
        public void dispatchMessage(Message msg) {
            T t = mReference.get();
            if (t == null) {
                return;
            }
            super.dispatchMessage(msg);
        }
    }
}
