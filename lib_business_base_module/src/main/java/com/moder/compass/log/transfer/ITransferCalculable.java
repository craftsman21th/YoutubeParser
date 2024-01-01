package com.moder.compass.log.transfer;

import android.util.Pair;

/**
 * Created by liuliangping on 2016/3/28.
 */
public interface ITransferCalculable {
    /**
     * 计算传输任务数和速度
     * 
     * @return
     */
    Pair<Integer, Long> calculateTransferTask();

    enum TransferLogType {
        DOWNLOAD, UPLOAD
    }
}
