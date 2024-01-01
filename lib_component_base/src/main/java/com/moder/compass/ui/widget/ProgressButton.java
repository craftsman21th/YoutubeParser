/*
 * ProgressButton.java
 * classes : com.dubox.drive.ui.widget.ProgressButton
 * @author 文超
 * V 1.0.0
 * Create at 2013-7-30 下午4:37:54
 */
package com.moder.compass.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moder.compass.component.base.R;

/**
 * 提供了带有展示Smooth Progress动画的Button。
 * <p/>
 * 动画与Button的文字只会展示一个，也就是说，当展示Smooth Progress时，文字是隐藏的；展示文字的时候，Smooth Progress是隐藏的。
 * com.dubox.drive.ui.widget.ProgressButton
 * 
 * @author 文超 <br/>
 *         create at 2013-7-30 下午4:37:54
 */
public class ProgressButton extends LinearLayout {
    private static final String TAG = "ProgressButton";

    private RotateImageView mProgressView;
    private TextView mTextViewButtonText;

    private LayoutInflater mInflater;

    /**
     * @param context
     */
    public ProgressButton(Context context) {
        super(context);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.button_with_progress, this);
        mProgressView = (RotateImageView) findViewById(R.id.progress_view);
        mTextViewButtonText = (TextView) findViewById(R.id.textview_button_text);

        // 获取文本样式，并为mTextViewButtonText设置
        int[] attrsArray = new int[] { android.R.attr.textStyle, android.R.attr.paddingLeft, android.R.attr.paddingTop,
                android.R.attr.paddingRight, android.R.attr.paddingBottom, android.R.attr.text };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, attrsArray);
        int index = 0;
        int textStyleID = typedArray.getResourceId(index++, -1);
        if (textStyleID > 0) {
            mTextViewButtonText.setTextAppearance(context, textStyleID);
        }
        int paddingLeft = typedArray.getDimensionPixelOffset(index++, 0);
        int paddingTop = typedArray.getDimensionPixelOffset(index++, 0);
        int paddingRight = typedArray.getDimensionPixelOffset(index++, 0);
        int paddingBottom = typedArray.getDimensionPixelOffset(index++, 0);
        String text = typedArray.getString(index++);
        if (!TextUtils.isEmpty(text)) {
            mTextViewButtonText.setText(text);
        }
        mTextViewButtonText.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        typedArray.recycle();
    }

    /**
     * 开始加载动作，隐藏文字，展现smooth progress
     */
    public void startLoad() {
        setClickable(false);
        mProgressView.setVisibility(VISIBLE);
        mTextViewButtonText.setVisibility(INVISIBLE);
        mProgressView.startRotate();
    }

    /**
     * 停止加载动作，展现文字，隐藏smooth progress
     */
    public void stopLoad() {
        setClickable(true);
        mProgressView.setVisibility(GONE);
        mTextViewButtonText.setVisibility(VISIBLE);
        mProgressView.stopRotate();
    }

    /**
     * 为Button设置文字内容
     * 
     * @param text
     */
    public void setText(CharSequence text) {
        mTextViewButtonText.setText(text);
    }

    /**
     * 为Button设置文字内容
     * 
     * @param text
     */
    public void setText(int textId) {
        mTextViewButtonText.setText(textId);
    }

    /**
     * 设置新的样式，只支持背景图和文本样式
     * 
     * @param resId 新样式资源id
     */

    @SuppressLint("ResourceType")
    public void setStyle(int resId) {
        int[] attrsArray = new int[] { android.R.attr.textAppearance, android.R.attr.background };
        TypedArray typedArray = getContext().obtainStyledAttributes(resId, attrsArray);
        // 获取文本样式，并为mTextViewButtonText设置
        int textStyleID = typedArray.getResourceId(0, 0);
        mTextViewButtonText.setTextAppearance(getContext(), textStyleID);
        // 设置背景图片
        Drawable backgroundDrawable = typedArray.getDrawable(1);
        this.setBackgroundDrawable(backgroundDrawable);

        typedArray.recycle();
    }

    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setCompoundDrawables(left, top, right, bottom);
    }

    public void setSingleLine(boolean singleLine) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setSingleLine(singleLine);
    }

    public void setEllipsize(TextUtils.TruncateAt where) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setEllipsize(where);
    }

    public void setGravity(int gravity) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setGravity(gravity);
    }

    public void setTextAppearance(Context context, int resid) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setTextAppearance(context, resid);
    }

    public void setCompoundDrawablePadding(int pad) {
        if (mTextViewButtonText == null) {
            return;
        }
        mTextViewButtonText.setCompoundDrawablePadding(pad);
    }
}
