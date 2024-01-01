package com.moder.compass.ui.transfer;

import com.moder.compass.transfer.base.Processor;

/**
 * Created by libin09 on 2015/2/6.
 */
public class OnAddDownloadTaskListener implements Processor.OnAddTaskListener {
    private static final String TAG = "OnAddTaskListener";

    @Override
    public boolean onAddTask() {
        return false;
    }
}
