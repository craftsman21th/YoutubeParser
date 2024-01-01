package com.moder.compass.log.transfer;

/**
 * Created by liuliangping on 2016/3/28.
 */
public interface ITransferStatisticsAble {
    /**
     * 统计调度器的运行任务个数
     * 
     * @return
     */
    int getStatisticsTaskCount();

    /**
     * 统计调度器的运行任务的总速度
     * 
     * @return
     */
    long getStatisticsSumRate(ITransferCalculable.TransferLogType type);
}
