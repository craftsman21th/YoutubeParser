package com.moder.compass.ui.transfer;

import com.moder.compass.transfer.base.Processor;

/**
 * Created by libin09 on 2015/2/6.
 */
public class OnAddUploadTaskListener implements Processor.OnAddTaskListener {
    private static final String TAG = "OnAddUploadTaskListener";

    @Override
    public boolean onAddTask() {

        return true;
    }
}
