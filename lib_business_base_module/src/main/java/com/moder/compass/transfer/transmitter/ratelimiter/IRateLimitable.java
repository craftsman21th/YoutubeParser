
package com.moder.compass.transfer.transmitter.ratelimiter;

import android.util.Pair;

/**
 * Created by liuliangping on 2015/6/17.
 */
public interface IRateLimitable {
    /**
     * 限速状态
     *
     * @author libin09
     */
    enum State {
        /**
         * 未限速状态
         */
        UNLIMITED,
        /**
         * 限速中状态，不读取流
         */
        LIMITED,
        /**
         * 限速中，并且需要读取流状态
         */
        LIMITED_READ
    }

    /**
     * 限速
     *
     * @param speed 当前传输器速度
     * @param isP2P 是否为P2P任务
     * @return <状态，限速值></状态，限速值>
     */
    Pair<State, Long> limit(long speed, boolean isP2P);

    void updateThreshold(long speed);

    boolean isRunningProbationary();

    String getSpeedToken();

    String getSpeedTimeStamp();
}
