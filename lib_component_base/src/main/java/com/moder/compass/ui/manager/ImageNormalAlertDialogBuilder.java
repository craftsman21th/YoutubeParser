package com.moder.compass.ui.manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.moder.compass.component.base.R;


/**
 * Created by liaozhengshuang on 18/10/16.
 * 图片类型的弹窗，竖排布局，内容如下：
 * 1. com.dubox.drive.preview.image 支持设置图片（加载本地图片或者网络图片）
 * 2. subContent
 * 3. content
 * 4. confirmButton 支持设置背景
 * 5. secondConfirmButton 文字链形式，支持设置文字颜色
 * 6. cancelButton 支持设置背景，布局在图片的右上角
 */

public class ImageNormalAlertDialogBuilder {

    /**
     * 不显示弹窗的按钮
     */
    public static final int DISABLE_DIALOG_BUTTON = -1;

    private Activity mActivity;

    /**
     * 图片资源
     */
    @DrawableRes
    private int mImageRes = DISABLE_DIALOG_BUTTON;
    /**
     * 描述副内容
     */
    @StringRes
    private int mSubContentRes = DISABLE_DIALOG_BUTTON;
    /**
     * 描述内容
     */
    @StringRes
    private int mContentRes = DISABLE_DIALOG_BUTTON;

    /**
     * 描述内容
     */
    private String mContentResStr;
    /**
     * 确定按钮文案
     */
    @StringRes
    private int mConfirmButtonTextRes = DISABLE_DIALOG_BUTTON;
    /**
     * 确定按钮背景
     */
    @DrawableRes
    private int mConfirmButtonBgRes = DISABLE_DIALOG_BUTTON;
    /**
     * 确定按钮文案颜色资源
     */
    private int mConfirmButtonTextColorRes = DISABLE_DIALOG_BUTTON;
    /**
     * 文字链文案资源
     */
    @StringRes
    private int mSecondConfirmTextRes = DISABLE_DIALOG_BUTTON;
    /**
     * 文字链颜色
     */
    private int mSecondConfirmTextColorRes = DISABLE_DIALOG_BUTTON;
    /**
     * 取消按钮背景
     */
    @DrawableRes
    private int mCancelBgRes = DISABLE_DIALOG_BUTTON;

    /**
     * 副内容颜色
     */
    private int mSubContentResColor = DISABLE_DIALOG_BUTTON;
    /**
     * 副内容是否加粗
     */
    private boolean mSubContentResBold = false;

    private String mSubContentText;
    private int mSubContentTextSize = 0;
    @DrawableRes
    private int mContentTextColor = DISABLE_DIALOG_BUTTON;
    // 单位DIP
    private int mContentTextSize = 0;
    private String mContentText;
    private String mConfirmButtonText;
    private String mSecondConfirmText;

    private String mImagePath;

    private NewExpandDialogCtrListener mDialogCtrListener;

    private boolean mDismissDialogWhenClick = true;

    private boolean mCancelable = true;

    public ImageNormalAlertDialogBuilder() {
    }

    public ImageNormalAlertDialogBuilder setImageRes(int mImageRes) {
        this.mImageRes = mImageRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSubContentRes(int mSubContentRes) {
        this.mSubContentRes = mSubContentRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setContentRes(int mContentRes) {
        this.mContentRes = mContentRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setContent(String content) {
        this.mContentResStr = content;
        return this;
    }
    public ImageNormalAlertDialogBuilder setConfirmButtonTextRes(int mConfirmButtonTextRes) {
        this.mConfirmButtonTextRes = mConfirmButtonTextRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setConfirmButtonBgRes(int mConfirmButtonBgRes) {
        this.mConfirmButtonBgRes = mConfirmButtonBgRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setConfirmButtonTextColorRes(int mConfirmButtonTextColorRes) {
        this.mConfirmButtonTextColorRes = mConfirmButtonTextColorRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSecondConfirmTextRes(int mSecondConfirmTextRes) {
        this.mSecondConfirmTextRes = mSecondConfirmTextRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSecondConfirmTextColorRes(int mSecondConfirmTextColorRes) {
        this.mSecondConfirmTextColorRes = mSecondConfirmTextColorRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setCancelBgRes(int mCancelBgRes) {
        this.mCancelBgRes = mCancelBgRes;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSubContentText(String mSubContentText) {
        this.mSubContentText = mSubContentText;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSubContentTextColor(int mSubContentTextColor) {
        this.mSubContentResColor = mSubContentTextColor;
        return this;
    }

    /**
     * 设置字体加粗
     */
    public ImageNormalAlertDialogBuilder setSubContentTextBold(boolean mSubContentTextBold) {
        this.mSubContentResBold = mSubContentTextBold;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSubContentTextSize(int mSubContentTextSize) {
        this.mSubContentTextSize = mSubContentTextSize;
        return this;
    }

    public ImageNormalAlertDialogBuilder setContentTextSize(int mContentTextSize) {
        this.mContentTextSize = mContentTextSize;
        return this;
    }

    public ImageNormalAlertDialogBuilder setContentTextColor(int mContentText) {
        this.mContentTextColor = mContentText;
        return this;
    }

    public ImageNormalAlertDialogBuilder setContentText(String mContentText) {
        this.mContentText = mContentText;
        return this;
    }

    public ImageNormalAlertDialogBuilder setConfirmButtonText(String mConfirmButtonText) {
        this.mConfirmButtonText = mConfirmButtonText;
        return this;
    }

    public ImageNormalAlertDialogBuilder setSecondConfirmText(String mSecondConfirmText) {
        this.mSecondConfirmText = mSecondConfirmText;
        return this;
    }

    public ImageNormalAlertDialogBuilder setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
        return this;
    }

    public ImageNormalAlertDialogBuilder setDialogCtrListener(
            NewExpandDialogCtrListener mDialogCtrListener) {
        this.mDialogCtrListener = mDialogCtrListener;
        return this;
    }

    public ImageNormalAlertDialogBuilder setDismissDialogWhenClick(boolean mDismissDialogWhenClick) {
        this.mDismissDialogWhenClick = mDismissDialogWhenClick;
        return this;
    }

    public ImageNormalAlertDialogBuilder setCancelable(boolean flag) {
        mCancelable = flag;
        return this;
    }

    public Dialog build(Activity activity) {
        if (null == activity || activity.isFinishing()) {
            return null;
        }
        final Dialog dialog = new Dialog(activity, R.style.ModerDialogTheme);
        View view =
                ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                        R.layout.dialog_normal_image_alert_layout, null);
        final ImageView content = (ImageView) view.findViewById(R.id.dialog_content_image);
        content.setImageResource(mImageRes);
        showLocalImage(activity, content);
        final TextView subContent = view.findViewById(R.id.sub_content);
        final TextView contentInfo = view.findViewById(R.id.content_info);
        final Button buttonConfirm = view.findViewById(R.id.button_confirm);
        final TextView textChain = view.findViewById(R.id.text_chain);
        final ImageView cancelImage = view.findViewById(R.id.dialog_cancel);

        if (mContentRes != DISABLE_DIALOG_BUTTON || !TextUtils.isEmpty(mContentResStr)) {
            contentInfo.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(mContentResStr)) {
                contentInfo.setText(mContentRes);
            } else {
                contentInfo.setText(mContentResStr);
            }
        } else {
            if (TextUtils.isEmpty(mContentText)) {
                contentInfo.setVisibility(View.GONE);
            } else {
                contentInfo.setVisibility(View.VISIBLE);
                contentInfo.setText(mContentText);
                if (mContentTextColor != DISABLE_DIALOG_BUTTON) {
                    contentInfo.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                            mContentTextColor));
                }
                if (mContentTextSize != 0) {
                    contentInfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mContentTextSize);
                }
            }
        }

        if (mSubContentRes != DISABLE_DIALOG_BUTTON) {
            subContent.setVisibility(View.VISIBLE);
            subContent.setText(mSubContentRes);
            subContent.setTextColor(mSubContentResColor);
            TextPaint textPaint = subContent.getPaint();
            textPaint.setFakeBoldText(mSubContentResBold);
        } else {
            if (TextUtils.isEmpty(mSubContentText)) {
                subContent.setVisibility(View.GONE);
            } else {
                subContent.setVisibility(View.VISIBLE);
                subContent.setText(mSubContentText);
                if (mSubContentResColor != DISABLE_DIALOG_BUTTON) {
                    subContent.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                            mSubContentResColor));
                }
                TextPaint textPaint = subContent.getPaint();
                textPaint.setFakeBoldText(mSubContentResBold);
                if (mSubContentTextSize != 0) {
                    subContent.setTextSize(mSubContentTextSize);
                }
            }
        }

        if (mConfirmButtonTextRes != DISABLE_DIALOG_BUTTON) {
            buttonConfirm.setText(mConfirmButtonTextRes);
            showConfirmButton(activity, buttonConfirm);
        } else {
            if (TextUtils.isEmpty(mConfirmButtonText)) {
                buttonConfirm.setVisibility(View.GONE);
            } else {
                buttonConfirm.setText(mConfirmButtonText);
                showConfirmButton(activity, buttonConfirm);
            }
        }
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogCtrListener != null) {
                    mDialogCtrListener.onOkBtnClick();
                }
                if (mDismissDialogWhenClick) {
                    dialog.dismiss();
                }
            }
        });

        if (mSecondConfirmTextRes != DISABLE_DIALOG_BUTTON) {
            textChain.setVisibility(View.VISIBLE);
            textChain.setText(mSecondConfirmTextRes);
            if (mSecondConfirmTextColorRes != DISABLE_DIALOG_BUTTON) {
                textChain.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                        mSecondConfirmTextColorRes));
            }
        } else {
            if (TextUtils.isEmpty(mSecondConfirmText)) {
                textChain.setVisibility(View.GONE);
            } else {
                textChain.setVisibility(View.VISIBLE);
                textChain.setText(mSecondConfirmText);
                if (mSecondConfirmTextColorRes != DISABLE_DIALOG_BUTTON) {
                    textChain.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                            mSecondConfirmTextColorRes));
                }
            }
        }
        textChain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogCtrListener != null) {
                    mDialogCtrListener.onSecondConfirmClick();
                }
                if (mDismissDialogWhenClick) {
                    dialog.dismiss();
                }
            }
        });

        if (mCancelBgRes != DISABLE_DIALOG_BUTTON) {
            cancelImage.setVisibility(View.VISIBLE);
            cancelImage.setImageResource(mCancelBgRes);
        } else {
            cancelImage.setVisibility(View.GONE);
        }
        cancelImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogCtrListener != null) {
                    mDialogCtrListener.onCancelBtnClick();
                }
                dialog.dismiss();
            }
        });
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(view, layoutParams);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(mCancelable);
        return dialog;
    }

    /**
     * 显示确认按钮
     * @param button
     */
    private void showConfirmButton(Activity activity, Button button) {
        button.setVisibility(View.VISIBLE);
        if (mConfirmButtonBgRes != DISABLE_DIALOG_BUTTON) {
            button.setBackgroundResource(mConfirmButtonBgRes);
        }
        if (mConfirmButtonTextColorRes != DISABLE_DIALOG_BUTTON) {
            button.setTextColor(ContextCompat.getColor(activity.getApplicationContext(),
                    mConfirmButtonTextColorRes));
        }
    }

    private void showLocalImage(Activity activity, ImageView imageView) {
        if (TextUtils.isEmpty(mImagePath)) {
            return;
        }
        final Bitmap bitmapOrigin = BitmapFactory.decodeFile(mImagePath);
        final Bitmap bitmap;
        if (bitmapOrigin != null) {
            float dialogWith = activity.getResources().getDimension(R.dimen.dialog_vip_guide_with);
            bitmap = resizeBitmapByDialog(activity.getApplicationContext(), bitmapOrigin, dialogWith);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            bitmap = null;
        }

        if (bitmapOrigin == null || bitmap == null) {
            imageView.setImageResource(mImageRes);
        }
    }

    private Bitmap resizeBitmapByDialog(@NonNull Context context, @NonNull Bitmap bitmap, float destWith) {
        final float imageWidth = bitmap.getWidth();
        final float imageHeight = bitmap.getHeight();
        final Matrix matrix = new Matrix();
        final float scale = destWith / imageWidth;
        matrix.postScale(scale, scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) imageWidth, (int) imageHeight, matrix, true);
        resizedBitmap.setDensity(context.getResources().getDisplayMetrics().densityDpi);
        return resizedBitmap;
    }
}
