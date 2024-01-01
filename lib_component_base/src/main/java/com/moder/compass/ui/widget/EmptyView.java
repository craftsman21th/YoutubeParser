/*
 * EmptyView.java
 * classes : com.dubox.drive.ui.widget.EmptyView
 * @author tianzengming
 * V 1.0.0
 * Create at 2014-1-2 下午5:40:10
 */
package com.moder.compass.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.ui.lottie.DynamicHostLottieView;
import com.moder.compass.ui.widget.tooltip.LottieUtil;
import com.moder.compass.ui.widget.tooltip.LottieUtil;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 所有空页面共用View com.dubox.drive.ui.widget.EmptyView
 *
 * @author tianzengming <br/>
 *         create at 2014-1-8 下午1:19:50
 */
public class EmptyView extends LinearLayout {
    private static final String TAG = "EmptyView";
    private TextView mTextView;
    private TextView tvDesc;
    private TextView mUploadButton;
    private TextView mRefreshButton;
    private ViewGroup mEmptyLayout;
    private TextView mForwardButton;
    private TextView mVipRecycleEmptyDesc;
    private int mEmptyResId;
    private DynamicHostLottieView mLottieAnimationView;
    private boolean canPlayAnimation = true;
    private LottieDrawable mLottieDrawable;
    private FrameLayout mFlUpload;
    private TextView mTvPremium;

    /**
     * @param context
     */
    public EmptyView(Context context) {
        super(context);
        init(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.empty_layout, this);
        mTextView = (TextView) findViewById(R.id.empty_text);
        tvDesc = (TextView) findViewById(R.id.empty_desc);
        //        mImageView = (RotateImageView) findViewById(R.id.empty_image);
        mUploadButton = (TextView) findViewById(R.id.btn_upload_file);
        mRefreshButton = (TextView) findViewById(R.id.btn_refresh);
        mForwardButton = (TextView) findViewById(R.id.btn_forward);
        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        mLottieAnimationView = findViewById(R.id.empty_loading_lottie);
        mVipRecycleEmptyDesc = findViewById(R.id.vip_empty_desc);
        mLottieAnimationView.setSafeMode(true);
        mFlUpload = findViewById(R.id.fl_upload_file);
        mTvPremium = findViewById(R.id.tv_premium);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs) {
        init(context);
        TypedArray attrList = context.obtainStyledAttributes(attrs, R.styleable.EmptyView);
        Drawable drawable = attrList.getDrawable(R.styleable.EmptyView_empty_image);
        float width = attrList.getDimension(R.styleable.EmptyView_empty_image_width, -1f);
        mEmptyResId = attrList.getResourceId(R.styleable.EmptyView_empty_image, -1);
        boolean showDefaultDrawable = attrList.getBoolean(R.styleable.EmptyView_show_default_empty_image, false);
        if (drawable != null) {
            if (width > 0) {
                float scale = width / drawable.getIntrinsicWidth();
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                drawable = new BitmapDrawable(getResources(), scaleBitmap);
                // 固定Icon大小(新增修改原因:宽高并未限制到)
                try {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLottieAnimationView.getLayoutParams();
                    lp.width = (int) width;
                    lp.height = (int) width;
                    mLottieAnimationView.setLayoutParams(lp);
                }catch (Exception e){
                    if(DuboxLog.isDebug()){
                        throw e;
                    }
                }
            }
            mLottieAnimationView.setImageDrawable(drawable);
        } else if (showDefaultDrawable) {
            mLottieAnimationView.setImageDrawable(getResources().getDrawable(R.drawable.loading_red));
        }
        String string = attrList.getString(R.styleable.EmptyView_empty_text);
        if (string != null && mTextView != null) {
            mTextView.setText(string);
        }

        // 刷新按钮样式
        Drawable buttonBg = attrList.getDrawable(R.styleable.EmptyView_button_background);
        if (buttonBg != null) {
            int padding = mRefreshButton.getPaddingLeft();
            mRefreshButton.setBackgroundDrawable(buttonBg);
            mRefreshButton.setPadding(padding, 0, padding, 0);
        }
        ColorStateList colors = attrList.getColorStateList(R.styleable.EmptyView_button_text_color);
        if (colors != null) {
            mRefreshButton.setTextColor(colors);
        }
        String buttonText = attrList.getString(R.styleable.EmptyView_button_text);
        if (buttonText != null) {
            mRefreshButton.setText(buttonText);
        }
        attrList.recycle();
    }

    public void setEmptyLayout(int resId) {
        mEmptyLayout.setBackgroundResource(resId);
    }

    public ViewGroup getEmptyLayout() {
        return mEmptyLayout;
    }


    public void setEmptyImageMarginTop(int marginTop) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLottieAnimationView.getLayoutParams();
        lp.topMargin = marginTop;
        mLottieAnimationView.setLayoutParams(lp);

    }
    public void setEmptyTextMarginTop(int marginTop) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTextView.getLayoutParams();
        lp.topMargin = marginTop;
        mTextView.setLayoutParams(lp);
    }

    public TextView getUploadButton() {
        return mUploadButton;
    }

    public void setEmptyText(int resId) {
        mTextView.setText(resId);
    }

    public void setRefreshText(int resId) {
        mRefreshButton.setText(resId);
    }

    public void setUploadText(int resId) {
        mUploadButton.setText(resId);
    }

    public void setEmptyTextColor(@ColorInt int colorId) {
        mTextView.setTextColor(colorId);
    }


    public void setEmptyTextSize(float size) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size);
    }

    public void setEmptyText(String text) {
        mTextView.setText(text);
    }

    public void setEmptyTextVisibility(int visibility) {
        mTextView.setVisibility(visibility);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setEmptyImage(int resId) {
        try {
            cacheLottieDrawable();
            mLottieAnimationView.setScaleX(1f);
            mLottieAnimationView.setScaleY(1f);
            mLottieAnimationView.setImageResource(0);
            mLottieAnimationView.setBackgroundDrawable(null);
            mLottieAnimationView.setProgress(0);
            mLottieAnimationView.cancelAnimation();
            mLottieAnimationView.setImageResource(resId);
            canPlayAnimation = false;
        } catch (Exception e) {
            DuboxLog.d(TAG, "setEmptyImage error()");
        }
    }

    private void cacheLottieDrawable() {
        Drawable drawable = mLottieAnimationView.getDrawable();
        if (drawable instanceof LottieDrawable) {
            mLottieDrawable = (LottieDrawable) drawable;
        }
    }

    /**
     * EmptyView 中给 LottieAnimationView 设置了 imageResource 后，导致其内部逻辑发生变化
     * 需要缓存 LottieDrawable 来重新展示动画
     */
    public void resetLottieDrawable() {
        if (mLottieDrawable != null) {
            mLottieAnimationView.setImageDrawable(mLottieDrawable);
        }
    }

    public LottieAnimationView getmLottieAnimationView() {
        return mLottieAnimationView;
    }

    public void setEmptyImageVisibility(int visibility) {
        mLottieAnimationView.setVisibility(visibility);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setLoadingLottieAnimation() {

        // mRotateDrawable = new RotateDrawable(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size));
        // mImageView.setImageDrawable(mRotateDrawable);
        //
        // final AnimatorSet progressAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), resId);
        // progressAnimation.setTarget(mRotateDrawable);
        //
        // final ObjectAnimator colorAnimator = (ObjectAnimator) progressAnimation.getChildAnimations().get(1);
        // colorAnimator.setEvaluator(new ArgbEvaluator());
        // progressAnimation.start();
        // mImageView.setDuration(500L);
        // mImageView.startRotate();

//        mLottieAnimationView.setBackgroundResource(R.drawable.loading_blue);
        setEmptyTextColor(getResources().getColor(R.color.common_color_666));
//        mLottieAnimationView.setImageResource(resId);
//        Drawable drawable = mLottieAnimationView.getDrawable();
//        if (drawable != null) {
//            if (drawable instanceof AnimationDrawable) {
//                ((AnimationDrawable) drawable).start();
//            } else {
//                mLottieAnimationView.startAnimation(AnimationUtils.loadAnimation(mLottieAnimationView.getContext(),
//                        R.anim.clockwise_rotate_animation));
//            }
//        }
        // lottie加载json需放初始化中，不然会出现loading延迟
//        mLottieAnimationView.setImageDrawable(getResources().getDrawable(R.drawable.loading_red));
        mLottieAnimationView.setImageAssetsFolder("images");
        LottieUtil.INSTANCE.fetchRemote(
                getContext().getString(R.string.lottie_loading_bg),
                new Function1<LottieComposition, Unit>() {
                    @Override
                    public Unit invoke(LottieComposition composition) {
                        if (canPlayAnimation) {
                            mLottieAnimationView.setComposition(composition);
                            mLottieAnimationView.setScaleX(0.6f);
                            mLottieAnimationView.setScaleY(0.6f);
                            mLottieAnimationView.setSafeMode(true);
                            mLottieAnimationView.playAnimation();
                        }
                        canPlayAnimation = true;
                        return null;
                    }
                }
        );
    }

    /**
     * 加载loading
     * @param resId
     */
    public void setLoading(int resId) {
        canPlayAnimation = true;
        setRefreshVisibility(View.GONE);
        setUploadVisibility(View.GONE);
        setDescVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setLoadingLottieAnimation();
        setEmptyText(resId);
    }

    /**
     * api 11以下版本时，仍然播放老动画
     *
     * @param resId
     */
    private void setLoadingImageOld(int resId) {
        mLottieAnimationView.setImageResource(resId);
        AnimationDrawable drawable = (AnimationDrawable) mLottieAnimationView.getDrawable();
        drawable.start();
    }

    public void setRefreshVisibility(int visibility) {
        mRefreshButton.setVisibility(visibility);
    }

    public void setDescVisibility(int visibility){
        tvDesc.setVisibility(visibility);
    }
    public void setUploadVisibility(int visibility) {
        mFlUpload.setVisibility(visibility);
        mUploadButton.setVisibility(visibility);
    }

    public void setRefreshListener(OnClickListener listener) {
        mRefreshButton.setOnClickListener(listener);
    }

    public void setUploadListener(OnClickListener listener) {
        mUploadButton.setOnClickListener(listener);
    }

    public void setLoading(int textId, @ColorInt int colorId, int animId) {
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setLoadingLottieAnimation();
        setEmptyText(textId);
        setEmptyTextColor(colorId);
    }

    public void setUploadButtonText(int resId) {
        mUploadButton.setText(resId);
    }


    public void setDescText(int resId){
        tvDesc.setVisibility(View.VISIBLE);
        tvDesc.setText(resId);
    }

    public void setRefreshButtonText(int resId) {
        mRefreshButton.setText(resId);
    }

    public void setRefreshButtonTextColor(int color) {
        mRefreshButton.setTextColor(color);
    }
    public void setRefreshButtonBg(int resId) {
        mRefreshButton.setBackgroundResource(resId);
    }
    public void setRefreshButtonBg(Drawable drawable) {
        mRefreshButton.setBackground(drawable);
    }
    public void setRefreshButtonPadding(int left, int top, int right, int bottom) {
        mRefreshButton.setPadding(left, top, right, bottom);
    }

    public void setRefreshButtonSize(int width) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRefreshButton.getLayoutParams();
        lp.width = width;
        mRefreshButton.setLayoutParams(lp);
    }

    public void setUploadButtonSize(int width) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mUploadButton.getLayoutParams();
        lp.width = width;
        mUploadButton.setLayoutParams(lp);
    }

    public TextView getRefreshButton() {
        return mRefreshButton;
    }

    public void setLoadNoData(int resId) {
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        if (mEmptyResId != -1) {
            setEmptyImage(mEmptyResId);
        } else {
            setEmptyImage(R.drawable.null_common);
        }

        setEmptyText(resId);
    }

    public void setLoadNoData(String text, @DrawableRes int imageResId) {
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setEmptyImage(imageResId);
        setEmptyText(text);
    }

    public void setLoadNoData(@StringRes int emptyTextId, @DrawableRes int imageResId) {
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setEmptyImage(imageResId);
        setEmptyText(emptyTextId);
    }

    public void setLoadError(int resId) {
        setVisibility(View.VISIBLE);
        setEmptyImage(R.drawable.empty_error);
        setEmptyText(resId);
    }

    public void setLoadError(int resId,@DrawableRes int imageResId) {
        setVisibility(View.VISIBLE);
        setEmptyImage(imageResId);
        setEmptyText(resId);
    }

    public void setLoadNoDataWithForwardButton(@StringRes int testResId, @StringRes int forwardButtonTextResId) {
        mForwardButton.setVisibility(View.VISIBLE);
        mForwardButton.setText(forwardButtonTextResId);
        setLoadNoData(testResId);
    }

    public void setLoadNoDataWithUploadButton(@StringRes int testResId, @StringRes int forwardButtonTextResId) {
        mFlUpload.setVisibility(View.VISIBLE);
        mUploadButton.setVisibility(View.VISIBLE);
        mUploadButton.setText(forwardButtonTextResId);
        setLoadNoData(testResId);
    }

    /**
     * 设置空白页面按钮背景和字体颜色
     * @param testColorResId
     * @param testBgColorDrawable
     */
    public void setLoadNoDataWithUploadButtonBg( int testColorResId, @DrawableRes int testBgColorDrawable) {
        mUploadButton.setTextColor(ContextCompat.getColorStateList(getContext(), testColorResId));
        mUploadButton.setBackgroundResource(testBgColorDrawable);
    }

    /**
     * 设置 forwardBtn 文案
     * @param text
     */
    public void setForwardBtnText(CharSequence text) {
        mForwardButton.setText(text);
    }


    public void setLoadNoDataWithForwardButton(String text, @DrawableRes int imageResId,
            @StringRes int forwardButtonTextResId) {
        mForwardButton.setVisibility(View.VISIBLE);
        mForwardButton.setText(forwardButtonTextResId);
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setEmptyImage(imageResId);
        setEmptyText(text);
    }

    public void setLoadNoDataWithUploadButton(String text, @DrawableRes int imageResId,
                                               @StringRes int forwardButtonTextResId) {
        mFlUpload.setVisibility(View.VISIBLE);
        mUploadButton.setVisibility(View.VISIBLE);
        mUploadButton.setText(forwardButtonTextResId);
        setRefreshVisibility(View.GONE);
        setVisibility(View.VISIBLE);
        setEmptyImage(imageResId);
        setEmptyText(text);
    }

    public void setForwardListener(OnClickListener listener) {
        mForwardButton.setOnClickListener(listener);
    }

    public void setForwardVisibility(int visibility) {
        mForwardButton.setVisibility(visibility);
    }

    public void showVipRecycleEmptyDesc(CharSequence text) {
        mVipRecycleEmptyDesc.setText(text);
        mVipRecycleEmptyDesc.setVisibility(View.VISIBLE);
    }

    public void hideVipRecycleEmptyDesc() {
        mVipRecycleEmptyDesc.setVisibility(View.GONE);
    }

    public void setTvPermiumText(int resId) {
        mTvPremium.setText(resId);
        mTvPremium.setVisibility(View.VISIBLE);
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {
        if (mLottieAnimationView != null && mLottieAnimationView.isAnimating()) {
            mLottieAnimationView.setProgress(0);
            mLottieAnimationView.cancelAnimation();
        }
    }

}
