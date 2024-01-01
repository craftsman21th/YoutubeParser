package com.moder.compass.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;

public class CustomLinearLayout extends LinearLayout {
    private static final String TAG = "CustomLinearLayout";

    private Fling2RightListenner fling2RightListenner;

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFling2RightListenner(Fling2RightListenner listener) {
        fling2RightListenner = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (fling2RightListenner != null && w > DeviceDisplayUtils.dip2px(getContext(), 320)) {
            fling2RightListenner.onFling2Right(w);
        }
        DuboxLog.d(TAG, "CustomLinearLayout " + w + " " + h + " " + oldw + " " + oldh);
    }

    public interface Fling2RightListenner {
        void onFling2Right(int width);
    }

}
