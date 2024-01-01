/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.moder.compass.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

public final class SearchBox extends AutoCompleteTextWithDeleteButton {

    /**
     * @param context
     */
    public SearchBox(Context context) {
        // super(context);
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public SearchBox(Context context, AttributeSet attrs) {
        // super(context, attrs);
        this(context, attrs, /* com.yi.internal. */R.attr.searchEditTextStyle);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        boolean showClearIcon = true;
        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBox);
            showClearIcon = typedArray.getBoolean(R.styleable.SearchBox_show_clear_icon, true);
        } catch (Throwable e) {
            DuboxLog.e(getClass().getName(), e.getMessage());
        }
        if (showClearIcon) {
            setDeleteIcon(R.drawable.search_box_delete);
        }
    }

    /*
     * protected void setIcon(Drawable icon) { super.setIcon(icon);
     *
     * if (icon == null) { Drawable searchIcon = this.getResources().getDrawable(android
     * .R.drawable.yi_ic_element_text_area_magnifier); this.setCompoundDrawablesWithIntrinsicBounds(null, null,
     * searchIcon, null); setGravity(Gravity.CENTER_VERTICAL); requestLayout(); } }
     */

    /**
     * @param level
     */
    public void setLeftIcon(int level) {
        Drawable[] drawables;
        drawables = this.getCompoundDrawables();

        if (drawables[0] != null) {
            drawables[0].setLevel(level);
        }
    }

    /**
     * 设置删除按钮样式
     *
     * @param resId
     */
    private void setDeleteIcon(int resId) {
        mDelete = this.getResources().getDrawable(resId);
        this.setIcon(mDelete);
        this.updateDeleteIcon();
    }

    protected boolean isOnDelete(int x, int y) {
        // 暂时解决三星手机上可能会出现的崩溃问题
        // 尝试修复线上三星手机bug：SearchBox->java.io.EOFException
        try {
            this.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 为解决点击区域过小，增加偏移量，增大点击区域
        int offset = mIconWidth / 2;
        // 真实的icon左pos
        int realLeft = this.getWidth() - mIconWidth - this.getPaddingRight();
        // 真实的icon右pos
        int realRight = realLeft + mIconWidth;
        // 偏移后icon左pos
        int offsetLeft = realLeft - offset;
        // 偏移后icon右pos
        int offsetRight = realRight + offset;
        // 搜索框上沿为top
        int top = 0;
        // 搜索框下沿为top
        int bottom = this.getHeight();
        Rect rect = new Rect(offsetLeft, top, offsetRight, bottom);

        if (rect.contains(x, y)) {
            return true;
        }
        return false;
    }

    private OnClearInputListener onClearInputListener = null;

    @Override
    public void doDelete() {
        super.doDelete();
        if (onClearInputListener != null) {
            try {
                // 尝试修复线上bug：SearchBox->java.io.EOFException
                onClearInputListener.onClearInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置删除文本回调
     *
     * @param onClearInputListener
     */
    public void setOnClearInputListener(OnClearInputListener onClearInputListener) {
        this.onClearInputListener = onClearInputListener;
    }

    /**
     * 删除回调
     */
    public interface OnClearInputListener {
        /**
         * 删除回调
         */
        public void onClearInput();
    }
}
