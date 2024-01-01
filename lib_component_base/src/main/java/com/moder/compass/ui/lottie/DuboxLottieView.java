package com.moder.compass.ui.lottie;

import android.animation.ValueAnimator;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 在View不可见、锁屏情况下，pause了Lottie动画播放，以节省电量和CPU占用
 * <p>
 * 注意：
 * 1. 此控件会对 {@link #isAnimating()} 和
 * {@link #addAnimatorUpdateListener(ValueAnimator.AnimatorUpdateListener)} 等函数造成影响
 * <p>
 * 2. 建议仅针对 {@link #loop(boolean)} 或者 app:lottie_loop 设置了true的动画，使用此控件
 *
 * @author guanshuaichao
 * @since 2019/3/19
 */
public class DuboxLottieView extends LottieAnimationView {

    private static final String TAG = "DuboxLottieView";

    /** View不可见时是否正在播放动画 */
    private boolean mWasAnimatingWhenVisibilityChanged = false;

    /** 锁屏时是否正在播放动画 */
    private boolean mWasAnimatingWhenScreenStateChanged = false;

    /** 构造函数是否初始化完成 */
    private boolean mInit;

    /** 可见状态 */
    private int mVisibility;

    /** 屏幕亮屏状态 */
    private int mScreenState = View.SCREEN_STATE_ON;

    public DuboxLottieView(Context context) {
        this(context, null);
    }

    public DuboxLottieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DuboxLottieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        mVisibility = getVisibility();

        mInit = true;
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        DuboxLog.d(TAG, "onVisibilityChanged " + visibility + " " + this);

        // 避免系统View构造函数调用 onVisibilityChanged 导致空指针
        if (!mInit) {
            return;
        }

        mVisibility = visibility;
        if (mVisibility != VISIBLE) {
            mWasAnimatingWhenVisibilityChanged = isAnimating();
        }

        updatePlayState();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);

        DuboxLog.d(TAG, "onScreenStateChanged " + screenState + " " + this);

        // 避免系统View构造函数调用 onScreenStateChanged 导致空指针
        if (!mInit) {
            return;
        }

        mScreenState = screenState;
        if (mScreenState == View.SCREEN_STATE_OFF) {
            mWasAnimatingWhenScreenStateChanged = isAnimating();
        }

        updatePlayState();
    }

    private void updatePlayState() {
        // 增加try catch修复pause、resume view内部可能空指针崩溃问题
        try {
            if (mVisibility == VISIBLE && mScreenState == View.SCREEN_STATE_ON) {
                // View可见 并且是亮屏状态
                if ((mWasAnimatingWhenVisibilityChanged
                        || mWasAnimatingWhenScreenStateChanged)
                        && !isAnimating()) {
                    // 如果之前有记录的播放态，则恢复动画播放
                    resumeAnimation();

                    // 重置记录的播放态
                    mWasAnimatingWhenVisibilityChanged = false;
                    mWasAnimatingWhenScreenStateChanged = false;

                    DuboxLog.d(TAG, "updatePlayState resumeAnimation " + this);
                }
            } else {
                if (isAnimating()) {
                    pauseAnimation();
                    DuboxLog.d(TAG, "updatePlayState pauseAnimation " + this);
                }
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage(), e);
        }
    }
}
