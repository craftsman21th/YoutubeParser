package com.moder.compass.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.moder.compass.component.base.R;


/**
 * WebView的进度条，包含一个最小进度值，保证在加载进度为0时，也会显示进度条。进度为100时不显示。
 *
 * Created by huantong on 16/9/30.
 */

public class WebProgressBar extends ProgressBar {
    private static final int MIN_PERCENT = 10;
    private int percent = MIN_PERCENT;
    private Paint paint;

    public WebProgressBar(Context context) {
        this(context, null);
    }

    public WebProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        int tintColor = getResources().getColor(R.color.color_06a6e5);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(tintColor);
    }

    public WebProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
    }

    public void setTintColor(int color) {
        paint.setColor(color);
    }

    @Override
    public synchronized void setProgress(int progress) {
        this.percent = progress;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // percent >= 100, 不显示进度信息，相当于隐藏了
        if (this.percent < 100) {
            int w = getMeasuredWidth();
            int h = getMeasuredHeight();
            int percent = Math.max(this.percent, MIN_PERCENT);
            canvas.drawRect(0.0f, 0.0f, w * (float) percent / 100.0f, h, paint);
        }
    }
}
