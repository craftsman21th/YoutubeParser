package com.moder.compass.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.moder.compass.component.base.R;

public class CheckableItemLayout extends CheckableLayout {

    public CheckableItemLayout(Context context) {
        this(context, null);
    }

    public CheckableItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckableItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setChoiceMode(int choiceMode) {
        // 解决 Item 复用导致的背景显示问题
        if (choiceMode == ListView.CHOICE_MODE_NONE) {
            setChecked(false);
        }
        View view = findViewById(android.R.id.button1);
        View view2 = findViewById(android.R.id.button2);
        setChoiceMode(choiceMode, view, view2);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = this.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            if (child.getId() == R.id.filesize && mCheckMarkDrawable != null && mCheckMarkDrawable.isVisible()) {
                child.setPadding(0, 0, HAS_CHECK_RIGHT, 0);
            } else if (child.getId() == R.id.filesize
                    && ((mCheckMarkDrawable != null && !mCheckMarkDrawable.isVisible())
                    || mCheckMarkDrawable == null)) {
                child.setPadding(0, 0, NO_CHECK_RIGHT, 0);
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        View view = findViewById(android.R.id.button1);
        View view2 = findViewById(android.R.id.button2);
        onDrawCanvas(canvas, view, view2);
    }

}
