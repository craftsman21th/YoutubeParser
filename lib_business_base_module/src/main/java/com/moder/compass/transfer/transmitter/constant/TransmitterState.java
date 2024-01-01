package com.moder.compass.transfer.transmitter.constant;

/**
 * 传输器状态
 *
 * @author sunqi01
 */
interface TransmitterState {
    /**
     * 初始化状态
     */
    public static final int TRANSMITTER_STATE_INIT = -1; // 初始化状态
    /**
     * 错误
     */
    public static final int TRANSMITTER_STATE_ERROR = -2; // 错误
    /**
     * 等待
     */
    public static final int TRANSMITTER_STATE_WAITTING = 21; // 等待列表中
    /**
     * 正在传输
     */
    public static final int TRANSMITTER_STATE_TRANSMITTING = 22; // 正在传输
    /**
     * 暂停
     */
    public static final int TRANSMITTER_STATE_PAUSE = 23; // 用户手动暂停
    /**
     * 特殊原因中断
     */
    public static final int TRANSMITTER_STATE_INTERRUPT = 24; // 特殊原因中断（网络原因）
    /**
     * 停止
     */
    public static final int TRANSMITTER_STATE_STOP = 25; // 停止
    /**
     * 完成
     */
    public static final int TRANSMITTER_STATE_DONE = 26; // 完成
}
