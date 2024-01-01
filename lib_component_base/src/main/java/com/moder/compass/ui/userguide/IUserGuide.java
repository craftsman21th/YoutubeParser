package com.moder.compass.ui.userguide;

import android.content.Intent;

/**
 * Created by liji01 on 17-8-4.
 */

public interface IUserGuide {
    void showGuide();

    int getPriority();

    IUserGuide setPriority(int priority);

    IUserGuide setResultListener(IGuideResultListener listener);

    void handleActivityResult(int requestCode, int resultCode, Intent data);

    void handleReleaseGuide();
}
