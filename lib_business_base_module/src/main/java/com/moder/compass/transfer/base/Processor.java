package com.moder.compass.transfer.base;

/**
 * loadFileProcesser基类，用于处理上传下载任务的加载 com.dubox.drive.task.process.loadProcesser
 *
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午01:45:17
 */
public abstract class Processor {
    private static final String TAG = "LoadProcessor";

    public Processor() {
    }

    /**
     * 上传下载文件处理监听器
     *
     * @author 孙奇 V 1.0.0 Create at 2012-11-12 上午01:46:12
     */
    protected OnProcessListener mOnProcessListener;

    protected OnAddTaskListener mOnAddTaskListener;

    /**
     * 设置上传下载文件处理监听器
     *
     * @param listener
     * @author 孙奇 V 1.0.0 Create at 2012-11-12 上午02:21:18
     */
    public void setOnProcessListener(OnProcessListener listener) {
        mOnProcessListener = listener;
    }

    public void setOnAddTaskListener(OnAddTaskListener onAddTaskListener) {
        mOnAddTaskListener = onAddTaskListener;
    }

    public abstract void process();

    public interface OnAddTaskListener {
        boolean onAddTask();
    }
}
