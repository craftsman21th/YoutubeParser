package com.moder.compass.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;

/**
 * 能够判断是否滑动超过offset距离的HorizontalScrollView
 * 
 * @author sunqi01
 * 
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {
    private static final String TAG = "FolderPathLayout";
    private boolean isLeftEdgeShow = false;
    private final int offsetValue = DeviceDisplayUtils.dip2px(getContext(), 100);

    private OverScrolledListenner mOverScrolledListenner;

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnOverScrolledListenner(OverScrolledListenner mOverScrolledListenner) {
        this.mOverScrolledListenner = mOverScrolledListenner;
    }

    public int getOffsetValue() {
        return offsetValue;
    }

    // public void setOffsetValue(int offsetValue) {
    // this.offsetValue = offsetValue;
    // }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldX, int oldY) {
        super.onScrollChanged(scrollX, scrollY, oldX, oldY);

        if (offsetValue == 0) {
            return;
        }

        if (isLeftEdgeShow && (scrollX <= offsetValue)) {
            isLeftEdgeShow = false;
            mOverScrolledListenner.onLeftEdgeHide(isLeftEdgeShow);
        } else if (!isLeftEdgeShow && (scrollX > offsetValue)) {
            isLeftEdgeShow = true;
            mOverScrolledListenner.onLeftEdgeHide(isLeftEdgeShow);
        }
    }

    public interface OverScrolledListenner {
        void onLeftEdgeHide(boolean isLeftEdgeShow);
    }

}
