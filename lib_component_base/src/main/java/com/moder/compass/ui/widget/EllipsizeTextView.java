package com.moder.compass.ui.widget;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.TextTools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.core.content.ContextCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 解决TextView超过两行无法使用省率号的问题 com.dubox.drive.ui.widget.EllipsizeTextView
 * 
 * @author tianzengming <br/>
 *         create at 2014-7-9 上午10:42:12
 */
public class EllipsizeTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = "EllipsizeTextView";
    private static final String WORD = " ";
    private boolean mInvalid;
    private boolean mInternalChange;
    private CharSequence mOriginalText;
    // api level低于16不提供getMaxLines函数，自定义变量进行存储
    private int mMaxLines;
    private float mSpacingMult = 1.0f;
    private float mSpacingAdd = 0.0f;
    private static final int[] LAYOUT_ATTRS = new int[] { android.R.attr.maxLines };
    private String hightlightText = null;
    private boolean mHasDrawable = false;

    public EllipsizeTextView(Context context) {
        super(context);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        mMaxLines = typeArray.getInt(0, -1);
        typeArray.recycle();
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        mMaxLines = maxLines;
        mInvalid = true;
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        mSpacingAdd = add;
        mSpacingMult = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!mInternalChange) {
            mOriginalText = text;
            mInvalid = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mInvalid) {
            ellipsizeText();
        }
        super.onDraw(canvas);
    }

    /**
     * @param text
     */
    public void setAndEllipsizeText(String text) {
        if (mOriginalText != null && mOriginalText.equals(text)) {
            ellipsizeText();
        } else {
            setText(text);
        }
    }

    /**
     * 根据当前设置的最大行数，计算省略的字串
     */
    public void ellipsizeText() {
        if (mMaxLines < 0) {
            mInvalid = false;
            return;
        }
        CharSequence workingText = mOriginalText;
        // 使用0.9是因为在使用时，一些 TextView 会出现只显示一半的情况，同时能减少循环
        // 已经测试项目中其他使用这个空间的地方，没有影响显示
        int width = (int) (getWidth() * mMaxLines * 0.9);
        if (getRealLineCount(workingText) > mMaxLines) {
            workingText = TextUtils.ellipsize(mOriginalText, getPaint(), width, getEllipsize());
        }
        if (!workingText.equals(getText())) {
            mInternalChange = true;
            try {
                if (hightlightText != null) {
                    if (mHasDrawable) {
                        setText(TextTools.highlightText(workingText,
                                ContextCompat.getColor(getContext(), R.color.red), true, false, hightlightText));
                    } else {
                        setText(TextTools.highlightText(workingText.toString(),
                                ContextCompat.getColor(getContext(), R.color.red), true, hightlightText));
                    }
                } else {
                    setText(workingText);
                }
            } finally {
                mInternalChange = false;
            }
        }
        mInvalid = false;
    }

    private int getRealLineCount(CharSequence workingText) {
        int tempWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (tempWidth <= 0) {
            return 0;
        }
        return new StaticLayout(workingText, getPaint(), tempWidth, Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd,
                false).getLineCount();
    }

    // 设置需要高亮显示的内容，该方法需在setText之前调用
    public void setHighlightText(String highlightText) {
        this.hightlightText = highlightText;
    }

    public void setDrawable(boolean hasDrawable) {
        this.mHasDrawable = hasDrawable;
    }
}
