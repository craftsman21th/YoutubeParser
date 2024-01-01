package com.moder.compass.transfer.transmitter.statuscallback;

/**
 *
 * 此类是基于原 StatusCallback 的扩展，增加了 onStart、onPause 方法的回调
 *
 * 作用，上层业务需要 onSuccess、onFailed、onPause 三个时机，因为此三个时机对 Task 任务的数量有影响
 *
 * @author zhangyuchao
 * @since 9.6.30
 */
public interface ITransferStatusCallback extends IStatusCallback {

    /**
     * 一个 Task 开始时被调用
     */
    void onStart();

    /**
     * 一个 Task 暂停时被调用
     */
    void onPause();
}
