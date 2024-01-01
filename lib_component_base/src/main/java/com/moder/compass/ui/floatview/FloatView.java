package com.moder.compass.ui.floatview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ConStantKt;

/**
 * 悬浮view(copy自百度网盘)
 * 
 * @author guanshuaichao
 * @since 2019-07-09
 */
public abstract class FloatView extends FrameLayout {

    private static final String TAG = "FloatView";

    /** 浮标内容 */
    private View mContent;

    /** 是否可拖动 */
    private boolean mCanDrag = true;
    /** Activity切换时是否自动清除 */
    private boolean mAutoClear = false;
    /** 按下坐标X */
    private float mStartX;
    /** 按下坐标Y */
    private float mStartY;
    /** 上次拖动事件坐标X */
    private float mLastX;
    /** 上次拖动事件坐标Y */
    private float mLastY;

    /**
     * 是否打开自动侧边吸附
     */
    private boolean mOpenStickSide = false;
    /**
     * 最后通过动画将X轴坐标移动到finalMoveX
     */
    private int mFinalMoveX;

    /**
     * 最后通过动画将Y轴坐标移动到finalMoveY
     */
    private int mFinalMovelY;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    /**
     * 屏幕高度
     */
    private int mScreenHeight = ConStantKt.CONSTANT_0;
    /**
     * 悬浮球下边界限制
     */
    private float mBottomLimit;

    /**
     * 悬浮球上边界限制
     */
    private float mTopLimit;

    /**
     * 悬浮球左、右界限制
     */
    private float mMarginLeftRight;

    /**
     * 悬浮球宽度
     */
    private float mViewWidth ;

    /**
     * 悬浮球高度
     */
    private float mViewHeight;

    public FloatView(@NonNull Context context) {
        super(context);
        try {
            mContent = initView(context);
            addView(mContent);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }

    /**
     * 是否开启自动从中间位置跳到侧边
     * @param open true 开启， false 不开启
     */
    public void setOpenStickSide(boolean open) {
        this.mOpenStickSide = open;
        if (mScreenHeight == ConStantKt.CONSTANT_0) {
            initScreenData();
        }
    }

    private void initScreenData() {
        mScreenWidth = DeviceDisplayUtils.getScreenWidth();
        mScreenHeight = DeviceDisplayUtils.getScreenHeight();
        mBottomLimit = DeviceDisplayUtils.dip2px(getContext(), ConStantKt.INT_100);
        mTopLimit = DeviceDisplayUtils.dip2px(getContext(), ConStantKt.INT_60);
        mMarginLeftRight = DeviceDisplayUtils.dip2px(getContext(), ConStantKt.SPACE_14);
    }

    /**
     * 创建浮标内容View
     */
    protected abstract View initView(Context context);

    public void setDrag(boolean canDrag) {
        mCanDrag = canDrag;
    }

    public boolean canDrag() {
        return mCanDrag;
    }

    public boolean isAutoClear() {
        return mAutoClear;
    }

    public void setAutoClear(boolean autoClear) {
        mAutoClear = autoClear;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mCanDrag) {
            return super.onTouchEvent(event);
        }

        if (mOpenStickSide && mViewWidth == ConStantKt.SPACE_0) {
            mViewWidth = mContent.getWidth();
            mViewHeight = mContent.getHeight();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastX = mStartX = event.getRawX();
                mLastY = mStartY = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                updatePosition(event);

                mLastX = event.getRawX();
                mLastY = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!isDragEvent(event) && hasOnClickListeners()) {
                    performClick();
                    break;
                }
                if (mOpenStickSide) {
                    // 判断当前View是在x轴中的位置，以中间为界
                    if (mLastX >= mScreenWidth / 2) {
                        mFinalMoveX = (int) (mScreenWidth - mViewWidth - mMarginLeftRight);
                    } else {
                        mFinalMoveX = (int) mMarginLeftRight;
                    }

                    // 判断当前View是在y轴中的位置，以顶部title bar(54px)、底部90px作为上下边界
                    float y = getY() + (event.getRawY() - mLastY);
                    if (y < 0) {
                        y = 0;
                    } else if (y > ((View) getParent()).getHeight() - mViewHeight) {
                        y = ((View) getParent()).getHeight() - mViewHeight;
                    }

                    if (mLastY >= mScreenHeight - mBottomLimit) {
                        mFinalMovelY = (int) (mScreenHeight - mBottomLimit);
                    } else if (mLastY <= mTopLimit) {
                        mFinalMovelY = (int) mTopLimit;
                    } else {
                        mFinalMovelY = (int) y;
                    }

                    // 执行悬浮球侧面吸附
                    stickToSide();
                }
            }
        }

        return true;
    }

    /**
     * 悬浮球贴到屏幕左右两侧
     */
    private void stickToSide() {
        ValueAnimator animator =
                ValueAnimator.ofInt((int) mLastX, mFinalMoveX).setDuration(100);
        animator.setInterpolator(new BounceInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setX(mFinalMoveX);
                setY(mFinalMovelY);
            }
        });
        animator.start();
    }

    private void updatePosition(MotionEvent ev) {
        View parentView = (View) getParent();
        float x = getX() + (ev.getRawX() - mLastX);
        if (x < 0) {
            x = 0;
        } else if (x > parentView.getRight() - getWidth()) {
            x = parentView.getRight() - getWidth();
        }
        setX(x);
        float y = getY() + (ev.getRawY() - mLastY);
        if (y < 0) {
            y = 0;
        } else if (y > parentView.getHeight() - getHeight()) {
            y = parentView.getHeight() - getHeight();
        }
        setY(y);
    }

    private boolean isDragEvent(MotionEvent ev) {
        float absX = Math.abs(ev.getRawX() - mStartX);
        float absY = Math.abs(ev.getRawY() - mStartY);
        double distance = Math.sqrt(Math.pow(absX, 2) + Math.pow(absY, 2));
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        return distance > touchSlop;
    }
}
