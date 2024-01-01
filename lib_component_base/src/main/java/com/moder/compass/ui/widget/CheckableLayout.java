package com.moder.compass.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;

public abstract class CheckableLayout extends RelativeLayout implements Checkable {

    protected static final String TAG = "CheckableItemLayout";

    protected boolean mChecked;
    protected Drawable mCheckMarkDrawable = null;
    protected boolean mShowCheckMarkDrawable = true;
    protected int mBasePaddingRight;

    protected static final int BASE_PADDING = (int) (40 * DeviceDisplayUtils.getDensity());

    protected int mChoiceMode = CHOICE_MODE_NONE;

    public static final int CHOICE_MODE_NONE = 0;

    public static final int CHOICE_MODE_SINGLE = 1;

    public static final int CHOICE_MODE_MULTIPLE = 2;

    // 当有checkbox的时候文件大小离右边的距离
    public static final int HAS_CHECK_RIGHT = 50;
    public static final int NO_CHECK_RIGHT = 8;


    // 首页界面的filesize
    public TextView fileSize = null;

    protected static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableLayout(Context context) {
        this(context, null);
    }

    public CheckableLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);
    }

    public int getChoiceMode() {
        return mChoiceMode;
    }

    public void setChoiceMode(int choiceMode) {
        setChoiceMode(choiceMode,  null, null);
    }

    public void setChoiceMode(int choiceMode, View view, View view2) {

        if (mChoiceMode == choiceMode) {
            return;
        }
        mChoiceMode = choiceMode;
        Drawable d = null;
        if (mChoiceMode == CHOICE_MODE_SINGLE) {
            d = getResources().getDrawable(R.drawable.rice_btn_radio);
        } else if (mChoiceMode == CHOICE_MODE_MULTIPLE) {
            d = getResources().getDrawable(R.drawable.rice_btn_check);
        } else if (mChoiceMode == CHOICE_MODE_NONE) {
            d = null;
        }

        setCheckMarkDrawable(d, view, view2);

        if (view2 == null && view != null) {
            if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置编辑态按钮是否显示
     *
     * @since 10.0.20
     * @param showCheckMarkDrawable
     */
    public void showCheckMarkDrawable(boolean showCheckMarkDrawable) {
        this.mShowCheckMarkDrawable = showCheckMarkDrawable;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    private void setCheckMarkDrawable(Drawable d, View view, View view2) {

        if (mCheckMarkDrawable != null) {
            mCheckMarkDrawable.setCallback(null);
            unscheduleDrawable(mCheckMarkDrawable);
        }
        if (d != null) {
            d.setCallback(this);
            d.setVisible(getVisibility() == VISIBLE, false);
            d.setState(CHECKED_STATE_SET);
            if (view2 == null && view != null) {
                // 右侧padding
                super.setPadding(this.getPaddingLeft(), this.getPaddingTop(),
                        BASE_PADDING, this.getPaddingBottom());
            }
            // 控件高度那么多的距离，留给checkbox
            d.setState(getDrawableState());
        } else {
            if (view2 == null && view != null) {
                super.setPadding(this.getPaddingLeft(), this.getPaddingTop(),
                        mBasePaddingRight, this.getPaddingBottom());
            }
        }
        mCheckMarkDrawable = d;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (mChoiceMode == CHOICE_MODE_NONE) {
            mBasePaddingRight = this.getPaddingRight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawCanvas(canvas, null, null);
    }


    public void onDrawCanvas(Canvas canvas, View view, View view2) {
        if (mCheckMarkDrawable != null) {
            if (view2 != null && !mShowCheckMarkDrawable) {
                super.setPadding(this.getPaddingLeft(), this.getPaddingTop(),
                        mBasePaddingRight, this.getPaddingBottom());
            } else {
                // 右侧padding, 控件高度那么多的距离，留给checkbox
                super.setPadding(this.getPaddingLeft(), this.getPaddingTop(), BASE_PADDING, this.getPaddingBottom());
            }
        } else {
            super.setPadding(this.getPaddingLeft(), this.getPaddingTop(), mBasePaddingRight, this.getPaddingBottom());
        }

        if (view != null && view2 != null) {
            if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                view.setVisibility(View.GONE);
                if (mShowCheckMarkDrawable) {
                    view2.setVisibility(View.GONE);
                } else {
                    view2.setVisibility(View.VISIBLE);
                }
            } else {
                view.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
            }
        }

        final Drawable checkMarkDrawable = mCheckMarkDrawable;
        if (checkMarkDrawable != null && mShowCheckMarkDrawable) {
            final int height = checkMarkDrawable.getIntrinsicHeight();
            final int width = checkMarkDrawable.getIntrinsicWidth();

            int top = (getHeight() - height) / 2;
            int boxWidth = BASE_PADDING;
            int left = getWidth() - boxWidth + (boxWidth - width) / 2;
            checkMarkDrawable.setBounds(left, top, left + width, top + height);
            checkMarkDrawable.draw(canvas);
        }
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mCheckMarkDrawable != null) {
            int[] myDrawableState = getDrawableState();

            mCheckMarkDrawable.setState(myDrawableState);

            invalidate();
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean populated = super.dispatchPopulateAccessibilityEvent(event);
        if (!populated) {
            event.setChecked(mChecked);
        }
        return populated;
    }

}
