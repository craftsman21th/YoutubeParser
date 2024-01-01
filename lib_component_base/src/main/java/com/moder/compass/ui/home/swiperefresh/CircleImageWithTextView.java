package com.moder.compass.ui.home.swiperefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moder.compass.base.utils.SizeUtils;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.moder.compass.statistics.StatisticsLogForMutilFields;

/**
 * @author guanshuaichao
 * @since 2019-06-21
 */
public class CircleImageWithTextView extends LinearLayout {

    public static final int CIRCLE_DIAMETER = 23;

    /**
     * 有广告牌的view
     */
    private RelativeLayout mBrandImageView;

    private CircleImageView mCircleImageView;
    private TextView mTextView;

    public CircleImageWithTextView(Context context, int color, final float radius) {
        super(context);

        mBrandImageView = new RelativeLayout(context);
        mBrandImageView.setGravity(Gravity.CENTER_HORIZONTAL);
        mBrandImageView.setBackground(null);
        LinearLayout.LayoutParams imageParams = new LayoutParams(
                DeviceDisplayUtils.getScreenWidth(),
                SizeUtils.dp2px(CIRCLE_DIAMETER * 2));
        addView(mBrandImageView, imageParams);
        mBrandImageView.setClickable(false);
        mBrandImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StatisticsLogForMutilFields.getInstance().updateCount(
                        StatisticsLogForMutilFields.StatisticsKeys.HOMEPAGE_YIKE_BRANCH_CLICK);
            }
        });

        // laoding样式
        mCircleImageView = new CircleImageView(context, color, radius);
        mCircleImageView.setImageDrawable(getResources().getDrawable(R.drawable.loading_red));
        int size = SizeUtils.dp2px(CIRCLE_DIAMETER);
        RelativeLayout.LayoutParams circleParams = new RelativeLayout.LayoutParams(size, size);
        circleParams.bottomMargin = SizeUtils.dp2px(25);
        circleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        circleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mBrandImageView.addView(mCircleImageView, circleParams);

        // 文案
        mTextView = new TextView(context);
        mTextView.setTextColor(getResources().getColor(R.color.color_999999));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        mTextView.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textParams.topMargin = SizeUtils.dp2px(14);
        textParams.bottomMargin = SizeUtils.dp2px(5);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mBrandImageView.addView(mTextView, textParams);
    }

    public void start() {
        mCircleImageView.start();
    }

    public void stop() {
        mCircleImageView.stop();
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mCircleImageView.setAnimationListener(listener);
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        mCircleImageView.onAnimationStart();
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        mCircleImageView.onAnimationEnd();
    }

    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(getContext().getResources().getColor(colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if ((getBackground() != null) && getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
            mTextView.setVisibility(GONE);
        } else {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(text);
        }
    }

    /**
     * 设置广告牌图片
     */
    @SuppressLint("RestrictedApi")
    public void setBrandImageBackground(int imageDrawable) {
        if (mBrandImageView == null) {
            return;
        }
        if (getContext() != null) {
            mBrandImageView
                    .setBackgroundResource(imageDrawable);
            mBrandImageView.setVisibility(VISIBLE);
        } else {
            mBrandImageView.setVisibility(GONE);
        }
    }

    /**
     * 设置view的高度
     * 有广告牌：142dp
     * 没有广告牌：46dp
     */
    public void setViewHeigth(int height) {
        if (mBrandImageView == null
                || mBrandImageView.getLayoutParams() == null) {
            return;
        }
        mBrandImageView.getLayoutParams().height = SizeUtils.dp2px(height);
    }

    /**
     * 设置广告牌可点
     */
    public void setBrandViewClickbale() {
        mBrandImageView.setClickable(true);
    }
}
