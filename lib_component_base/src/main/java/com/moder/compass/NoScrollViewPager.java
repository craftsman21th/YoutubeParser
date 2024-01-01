package com.moder.compass;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * Created by xujing31 on 2019/3/6.
 */

public class NoScrollViewPager extends ViewPager {

    private static final String TAG = "NoScrollViewPager";
    private boolean canScroll = true;
    private boolean isUp = true;

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (canScroll) {
                return super.onTouchEvent(event);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "onTouchEvent " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (canScroll) {
                return super.onInterceptTouchEvent(event);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, "onInterceptTouchEvent " + e.getMessage());
        }
        return false;
    }


    @Override
    public void setCurrentItem(int item) {
        // 控制可否切换fragment
        if (canScroll) {
            super.setCurrentItem(item, true);
        }
    }

}
