package com.moder.compass.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moder.compass.component.base.R;
import com.moder.compass.ui.lottie.DynamicHostLottieView;
import com.moder.compass.util.StateListDrawableHelper;

/**
 * 标题栏带红点按钮
 * Created by wenjun on 2018.12.27
 */
public class RedRemindButton extends FrameLayout {

    public static final String TAG = "RedRemindButton";
    private final Context mContext;
    public DynamicHostLottieView mLottieAnimationView;
    public ImageView mImageView;
    private final TextView imageIndicator;

    public void setImageResource(int resId) {
        Drawable icon = StateListDrawableHelper.createBgDrawableWithAlphaMode(mContext, resId, 0.4f);
        mImageView.setImageDrawable(icon);
    }

    public RedRemindButton(Context context) {
        this(context, null);
    }

    public RedRemindButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.remindButton);
        int imageResource = array.getResourceId(R.styleable.remindButton_image_icon, 0);
        boolean darkModel = array.getBoolean(R.styleable.remindButton_dark_model, false);
        array.recycle();
        View inflater =
                ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.item_common_redhot_btn, this);

        mLottieAnimationView = inflater.findViewById(R.id.lottie_image_btn);
        mImageView = (ImageView) inflater.findViewById(R.id.image_btn);
        imageIndicator = (TextView) inflater.findViewById(R.id.tab_indicator);
        if (darkModel) {
            mLottieAnimationView.setAnimationFromUrl(context.getString(R.string.lottie_transmission_dark));
        } else {
            mLottieAnimationView.setAnimationFromUrl(context.getString(R.string.lottie_transmission));
        }
        if (imageResource > 0) {
            Drawable icon = StateListDrawableHelper.createBgDrawableWithAlphaMode(mContext, imageResource, 0.4f);
            mImageView.setImageDrawable(icon);
        }

    }

    /**
     * 显示任务状态
     */
    public void showIndicator(long backupNum, long downloadOrUploadNum) {
        // 有下载、上传任务、备份任务 时
        // 业务逻辑变更，update by lihongliang05 at v2.1
        long allNum = backupNum + downloadOrUploadNum;
        if (allNum > 0) {
            imageIndicator.setVisibility(View.VISIBLE);
            imageIndicator.setText(String.valueOf(allNum > 99 ? "99+" : allNum));
            startLottieAnimation();
        } else {
            imageIndicator.setVisibility(View.GONE);
            stopLottieAnimation();
        }
    }

    /**
     * 停止lottie动画
     */
    private void startLottieAnimation() {
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(GONE);
        if (!mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setSafeMode(true);
            mLottieAnimationView.playAnimation();
        }
    }

    /**
     * 停止lottie动画
     */
    private void stopLottieAnimation() {
        mLottieAnimationView.setVisibility(View.GONE);
        mImageView.setVisibility(VISIBLE);
        if (mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.cancelAnimation();
        }
    }
}
