package com.moder.compass.statistics;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.job.BaseJob;
import com.dubox.drive.common.scheduler.TaskSchedulerImpl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 在线程池中执行任务的Handler
 * 通过send方法发送的
 *
 * @author guanshuaichao
 * @since 2019-10-23
 */
public class HandlerJob {

    private static final String TAG = "HandlerJob";

    /** 主线程Handler */
    private volatile InternalHandler mHandler;

    /** Handler名称，用于上报时的名称前缀 */
    private String mHandlerName;

    /** Handler是否已退出 */
    private volatile AtomicBoolean mIsQuit = new AtomicBoolean(false);

    /** 待放入线程池执行的message */
    private volatile Queue<Message> mMessages = new LinkedList<>();

    /** 是否正在执行任务 */
    private volatile AtomicBoolean mExecuting = new AtomicBoolean(false);

    /** 正在运行的ThreadJob */
    private volatile BaseJob mThreadJob;
    private String mThreadJobTaskId;

    /**
     * 构造基于线程池的Handler
     *
     * @param handlerName handler名称 上报时作为任务名前缀
     */
    public HandlerJob(String handlerName) {
        mHandlerName = handlerName;
        mHandler = new InternalHandler(Looper.getMainLooper());
    }

    /**
     * 发送消息 与{@link Handler}中方法保持一致
     */
    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public final boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        return mHandler.sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean post(Runnable r) {
        return mHandler.post(r);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return mHandler.postAtTime(r, uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return mHandler.postDelayed(r, delayMillis);
    }

    /**
     * 处理消息
     * 子类重写此方法，处理消息
     *
     * @param msg msg
     */
    public void handleMessage(Message msg) {

    }

    public final synchronized boolean hasMessages(int what) {
        if (mHandler.hasMessages(what)) {
            return true;
        }

        for (Message msg : mMessages) {
            if (msg.what == what && msg.getCallback() == null) {
                return true;
            }
        }

        return false;
    }

    public final synchronized void removeCallbacks(Runnable r) {
        if (r == null) {
            return;
        }
        mHandler.removeCallbacks(r);
        for (Message msg : mMessages) {
            if (r == msg.getCallback()) {
                mMessages.remove(msg);
            }
        }
    }

    public final synchronized void removeMessages(int what) {
        mHandler.removeMessages(what);
        for (Message msg : mMessages) {
            if (what == msg.what && msg.getCallback() == null) {
                mMessages.remove(msg);
            }
        }
    }

    public final synchronized void removeCallbacksAndMessages() {
        mHandler.removeCallbacksAndMessages(null);
        mMessages.clear();
    }

    /**
     * 退出handler，并移除队列中的消息
     */
    public synchronized void quit() {
        mIsQuit.set(true);
        mMessages.clear();
        if (mThreadJobTaskId != null) {
            TaskSchedulerImpl.INSTANCE.cancelTask(mThreadJobTaskId);
        }
    }

    /**
     * 把消息压入任务待执行缓存队列
     *
     * @param msg 待执行的消息
     */
    private synchronized void pushMessage(Message msg) {
        if (mMessages.offer(msg)) {
            tryPopMessageToExecute();
        } else {
            // never happened
            DuboxLog.e(TAG, mHandlerName + " queue is full");
        }
    }

    /**
     * 尝试冲队列中取出消息并放入线程池执行
     */
    private synchronized void tryPopMessageToExecute() {
        if (mIsQuit.get() || mExecuting.get()) { // 已退出或者有其它任务在执行
            return;
        }

        Message msg = mMessages.poll();
        if (msg == null) {
            return;
        }

        mThreadJob = new HandleMessageThread(generateJobName(msg), msg);
        mThreadJobTaskId = TaskSchedulerImpl.INSTANCE.addHighTask(mThreadJob);

        mExecuting.set(true);
    }

    /**
     * 生成任务名称，用于上报
     */
    private String generateJobName(Message msg) {
        String endName;
        if (msg != null) {
            endName = msg.getCallback() == null ? String.valueOf(msg.what) : "runnable";
        } else {
            endName = "null";
        }
        return mHandlerName + "_" + endName;
    }

    /**
     * 结束一个消息的执行 尝试执行下个等待中的消息
     */
    private synchronized void finishExecute() {
        mExecuting.set(false);
        tryPopMessageToExecute();
    }

    /**
     * 负责主线程延时分发消息
     */
    private class InternalHandler extends Handler {

        InternalHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                pushMessage(Message.obtain(msg));
            } catch (Exception e) {
                DuboxLog.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 处理消息的子线程
     */
    private class HandleMessageThread extends BaseJob {

        private Message mMsg;

        HandleMessageThread(String name, Message msg) {
            super(name);
            mMsg = msg;
        }

        @Override
        protected void performExecute() {
            try {
                if (mIsQuit.get() || mMsg == null) {
                    return;
                }

                Runnable runnable = mMsg.getCallback();
                if (runnable != null) {
                    runnable.run();
                } else {
                    handleMessage(mMsg);
                }
            } catch (Throwable t) {
                DuboxLog.e(TAG, t.getMessage(), t);
            } finally {
                finishExecute();
            }
        }
    }
}
