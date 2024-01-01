package com.moder.compass.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

public class PopupWindow24Wrapper extends PopupWindow {
    public PopupWindow24Wrapper(Context context) {
        super(context);
    }

    public PopupWindow24Wrapper(View contentView, int width, int height) {
        super(contentView, width, height, false);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            Resources resources = anchor.getResources();
            int heightPixels = resources.getDisplayMetrics().heightPixels;
            int height = heightPixels - visibleFrame.bottom;
            int availableHeight = getMaxAvailableHeight(anchor, yoff);
            setHeight(Math.max(height, availableHeight));
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }
}
