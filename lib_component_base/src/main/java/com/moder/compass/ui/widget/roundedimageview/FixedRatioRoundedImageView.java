/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.ui.widget.roundedimageview;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by liaozhengshuang on 18/9/13.
 */

public class FixedRatioRoundedImageView extends RoundedImageView {
    /**
     * 默认宽高比
     */
    public static final float DEFAULT_ASPECT_RATIO = 1.0f;
    private float mRatio = DEFAULT_ASPECT_RATIO;

    public FixedRatioRoundedImageView(Context context) {
        super(context);
    }

    public FixedRatioRoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedRatioRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioRoundedImageView, defStyle, 0);
        mRatio = a.getFloat(R.styleable.FixedRatioRoundedImageView_fix_aspect_ratio, -1);
        DuboxLog.d(TAG, "mRatio:" + mRatio);
        if (mRatio < 0) {
            mRatio = DEFAULT_ASPECT_RATIO;
        }
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = (int) (width / mRatio + 0.5f);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }
}
