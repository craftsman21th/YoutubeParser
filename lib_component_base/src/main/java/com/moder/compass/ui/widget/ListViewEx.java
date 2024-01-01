package com.moder.compass.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.Checkable;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 主页列表ListView
 * 
 * @author yangqinghai
 * 
 */
public class ListViewEx extends ListView {
    private final String TAG = "ListViewEx";

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewEx(Context context) {
        super(context);
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChoiceMode(CHOICE_MODE_NONE);
    }

    public void setChoiceMode(int choiceMode) {
        super.setChoiceMode(choiceMode);
        clearChoices();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof CheckableLayout) {
                CheckableLayout checkableLayout = (CheckableLayout) child;
                checkableLayout.setChoiceMode(choiceMode);
            }
            if (child instanceof Checkable) {
                ((Checkable) child).setChecked(false);
            }
        }
    }

    @Override
    public void clearChoices() {
        super.clearChoices();
    }

    public void beginRefresh() {

    }

    public void onRefreshComplete() {

    }

    public void onRefreshComplete(CharSequence lastUpdated) {

    }

    public void setAllItemChecked(boolean checked) {
        if (getChoiceMode() == CHOICE_MODE_MULTIPLE) {
            ListAdapter adapter = getAdapter();
            if (adapter != null) {
                SparseBooleanArray mCheckStates = getCheckedItemPositions();
                for (int position = 0, count = adapter.getCount(); position < count; position++) {
                    if (adapter.isEnabled(position)) {
                        mCheckStates.put(position, checked);
                    }
                }
            }
        }
        requestLayout();
    }

    public void setCurrentItemChecked(int position) {
        if (getChoiceMode() == CHOICE_MODE_MULTIPLE) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                SparseBooleanArray mCheckStates = getCheckedItemPositions();
                mCheckStates.put(position, true);
            }
        }
        requestLayout();
    }

    public void clearCurrentItemChecked(int position) {
        if (getChoiceMode() == CHOICE_MODE_MULTIPLE) {
            Adapter adapter = getAdapter();
            if (adapter != null) {
                SparseBooleanArray mCheckStates = getCheckedItemPositions();
                mCheckStates.put(position, false);
            }
        }
        requestLayout();
    }

    @Override
    public long[] getCheckedItemIds() {
        return super.getCheckedItemIds();
    }

    @Override
    public void setAdapter(ListAdapter adpter) {
        super.setAdapter(adpter);
    }

    @Override
    public int getChoiceMode() {
        return super.getChoiceMode();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            DuboxLog.e(TAG, e.toString(), e);
            // 网上介绍三星手机上频繁切换有时会发生改exception
        }
    }

}
