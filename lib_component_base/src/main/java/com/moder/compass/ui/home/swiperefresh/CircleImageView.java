package com.moder.compass.ui.home.swiperefresh;

import com.airbnb.lottie.LottieComposition;
import com.moder.compass.component.base.R;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.view.animation.Animation;

import com.moder.compass.ui.lottie.DynamicHostLottieView;
import com.moder.compass.ui.widget.tooltip.LottieUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by lbf on 2016/7/24.
 */
public class CircleImageView extends DynamicHostLottieView {

    private Animation.AnimationListener mListener;
    private int mShadowRadius = 0;

    private boolean canPlayAnimation = true;

    public CircleImageView(Context context, int color, final float radius) {
        super(context);
        setImageDrawable(getResources().getDrawable(R.drawable.loading_red));
    }

    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                    + mShadowRadius * 2);
        }
    }

    public void start() {
        LottieUtil.INSTANCE.fetchRemote(
                getContext().getString(R.string.lottie_loading_bg),
                new Function1<LottieComposition, Unit>() {
                    @Override
                    public Unit invoke(LottieComposition composition) {
                        setImageAssetsFolder("images");
                        if (canPlayAnimation) {
                            setComposition(composition);
                            setScaleX(0.6f);
                            setScaleY(0.6f);
                            if (!isAnimating()) {
                                playAnimation();
                            }
                        }
                        canPlayAnimation = false;
                        return null;
                    }
                }
        );
    }

    public void stop() {
        setProgress(0);
        cancelAnimation();
        canPlayAnimation = true;
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    /**
     * Update the background color of the circle com.dubox.drive.preview.image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(getContext().getResources().getColor(colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if ((getBackground() != null) && getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }
}
