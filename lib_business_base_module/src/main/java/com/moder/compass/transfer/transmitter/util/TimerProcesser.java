package com.moder.compass.transfer.transmitter.util;

/**
 * 触发处理类,用于作为构造TimerHelper的参数
 * 
 * @author 孙奇 <br/>
 *         create at 2012-9-7 下午12:02:20
 */
public abstract class TimerProcesser {
    /**
     * 触发处理方法
     */
    public abstract void doProcess();
}