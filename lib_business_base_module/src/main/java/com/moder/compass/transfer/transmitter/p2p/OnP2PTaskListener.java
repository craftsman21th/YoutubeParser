package com.moder.compass.transfer.transmitter.p2p;

/**
 * Created by libin on 15/12/5.
 */
public interface OnP2PTaskListener {
    void onP2PCreateFailed(long taskId);

    /**
     * 停止p2p任务
     * @param createID
     */
    void onP2PStop(String createID);

    /**
     * 设置任务来源于整合下载SDK
     * @param taskId 要被设置的任务ID
     * @param isSDKTask 是否来源于整合下载SDK
     */
    void onSDKTaskTypeSet(long taskId,boolean isSDKTask);

    /**
     * 设置任务是否属于P2P任务
     * @param taskId 要被设置的任务ID
     * @param isP2PTask 是否为P2P任务
     */
    void onP2PTaskTypeSet(long taskId,boolean isP2PTask);
}
