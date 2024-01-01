package com.moder.compass.transfer.transmitter.constant;

/**
 * 网络异常码
 *
 * @author sunqi01
 */
public interface NetworkExceptionCode {
    /**
     * 有信号无网
     */
    int NETWORK_NOT_AVAILABLE = 101;
    int NETWORK_NO_CONNECTION = 102;
    int WAITING_FOR_WIFI = 103;
    /**
     * 有信号无网正在检测
     */
    int NETWORK_VERIFY_CHECKING = 104;

    /**
     * 网络安全异常
     */
    int NETWORK_REFUSE = 105;
}
