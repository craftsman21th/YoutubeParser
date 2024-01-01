package com.moder.compass.transfer.transmitter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于管理传输列表待处理的任务数量
 * 使用方法：
 * 1、此 Manager 为单例，内存中仅存在一个实例
 * 2、当上传、下载、备份的任务发生了影响任务数量的操作时，即任务暂停、完成、失败，应设置标记位
 * 3、Manger 中使用定时器，当存在未完成任务时，每2秒查询一次标记位，当满足条件，进行数据库查询
 * 4、查询结果，给予业务层使用
 *
 * @author zhangyuchao
 * @since 9.6.30
 *
 * @revise lihongliang05
 * @since moder v2.2
 * @describe 删除原有 定时器 + 锁 + 广播 的机制。采用 cursor 监听方式实现。
 */
public class TransferNumManager {

    private static final String TAG = "TransferNumManager";

    private static TransferNumManager mInstance;

    public AtomicBoolean mNumChanged = new AtomicBoolean(true);

    /**
     * 获取单例
     *
     * @return instance
     */
    public static TransferNumManager getInstance() {
        if (mInstance == null) {
            mInstance = new TransferNumManager();
        }
        return mInstance;
    }

    /**
     * 设置待备份任务数量发生变化
     */
    public void setTransferNumChanged(boolean isDownloadType) {
        mNumChanged.set(true);
    }

    public synchronized void stopDistribute() {
        mNumChanged.set(false);
    }

}
