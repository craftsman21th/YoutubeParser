package com.moder.compass.ui.widget;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * 能自旋转的ImageView
 * 
 * @author 孙奇 <br/>
 *         create at 2013-4-23 下午04:16:34
 */
public class RotateImageView extends ImageView {
    private static final String TAG = "RotateImageView";

    Animation animationRotate;
    private boolean mIsRotating = false;

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniRotatetanimation();
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        iniRotatetanimation();
    }

    @SuppressLint("NewApi")
    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        iniRotatetanimation();
    }

    /**
     * 开始旋转
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-4-23 下午04:19:22
     */
    public void startRotate() {
        DuboxLog.d(TAG, "startRotate");
        if (mIsRotating) {
            return;
        }
        startAnimation(animationRotate);
        mIsRotating = true;
    }

    /**
     * 停止旋转
     * 
     * @author 孙奇 V 1.0.0 Create at 2013-4-23 下午04:19:27
     * @see android.view.View#clearAnimation()
     */
    public void stopRotate() {
        DuboxLog.d(TAG, "stopRotate");
        if (!mIsRotating) {
            return;
        }
        clearAnimation();
        mIsRotating = false;
    }

    /**
     * 是否正在旋转
     * 
     * @return
     * @author 孙奇 V 1.0.0 Create at 2013-5-28 下午03:39:16
     */
    public boolean isRotating() {
        return mIsRotating;
    }

    private void iniRotatetanimation() {
        if (animationRotate == null) {
            animationRotate = AnimationUtils.loadAnimation(getContext(), R.anim.clockwise_rotate_animation);
            LinearInterpolator lir = new LinearInterpolator();
            animationRotate.setInterpolator(lir);
        }
    }

    /**
     * 设置每次动画运行时间
     * 
     * @param duration
     */
    public void setDuration(long duration) {
        if (animationRotate == null) {
            return;
        }
        animationRotate.setDuration(duration);
    }

    /**
     * 修复在RecyclerView中刷新状态的动画显示异常的问题。
     * manyongqinag 2017-8-28
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DuboxLog.d(TAG, "onAttachedToWindow");
        if (mIsRotating) {
            startAnimation(animationRotate);
        }
    }
}
