package com.moder.compass.transfer.task;

/**
 * 任务状态的回调
 *
 * @author wangyang34
 * @since 2019年10月31日
 */
public interface ITaskStateCallback {

    /** 开始 */
    void onStart();

    /** 取消 */
    void onCancel();
}
