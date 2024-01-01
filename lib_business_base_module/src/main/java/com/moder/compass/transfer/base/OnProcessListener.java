package com.moder.compass.transfer.base;

/**
 * 批量传输监听接口
 * 
 * @author 孙奇 <br/>
 *         create at 2013-3-20 下午03:45:24
 */
public interface OnProcessListener {
    /**
     * 发送去重后的taskId
     */
    void onItemTaskLoadProcess(int taskId);

    /**
     * 去重后有新的任务添加到下载
     */
    void onNewFileInfoLoadProcess(int taskId, FileInfo fileInfo);
}
