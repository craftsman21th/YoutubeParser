package com.moder.compass.transfer.task;

import com.moder.compass.transfer.base.UploadInfo;
import com.moder.compass.transfer.base.Processor;

/**
 * 用于创建LoadProcesser的工厂基类
 * 
 * @author 孙奇 <br/>
 *         create at 2012-11-12 上午01:57:18
 */
public interface IUploadProcessorFactory {
    int TYPE_NEW_TASK = 100;
    int TYPE_NEW_TASK_AND_REMOVE_LAST_TASK = 101;
    int TYPE_NEW_FINISHED_TASK = 102;
    int TYPE_SET_TASK_STATE_TO_PENDING = 104;
    int TYPE_START_SCHEDULER = 105;

    /**
     * 工厂方法
     * 
     * @param info
     * @param bean
     * @param isNotify
     * @return
     */
    Processor createProcessor(UploadInfo info, DBBean bean, boolean isNotify);
}
