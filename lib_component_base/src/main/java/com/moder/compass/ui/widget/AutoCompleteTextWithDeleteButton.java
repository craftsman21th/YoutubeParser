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
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.TextTools;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

public class AutoCompleteTextWithDeleteButton extends AutoCompleteTextView {

    private int mDeleteIcon = R.drawable.edit_text_cancel;
    protected Drawable mDelete;

    protected int mIconWidth;
    protected int mIconHeight;

    private boolean mClean = false;

    private static final int VERY_WIDE = 16384;

    /**
     * 可输入的字节长度
     */
    private int mMaxByteLength = Integer.MAX_VALUE;

    public static final String ENCODEING = "UTF-8";

    /**
     * 输入文字的变化监听
     */
    private EditTextWatcher mEditTextWatcher;

    public void setEditTextWatcher(EditTextWatcher editTextWatcher) {
        this.mEditTextWatcher = editTextWatcher;
    }

    /**
     * @param context
     */
    public AutoCompleteTextWithDeleteButton(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public AutoCompleteTextWithDeleteButton(Context context, AttributeSet attrs) {
        this(context, attrs, /* com.yi.internal. */R.attr.editTextStyle);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoCompleteTextWithDeleteButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setSingleLine(true);
        // this.setCompoundDrawablePadding(0);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter((int) Math.floor(VERY_WIDE
                / this.getTextSize())), inputFilter});

        this.addTextChangedListener(mTextWatcher);
        if (mDelete == null) {
            mDelete = this.getResources().getDrawable(mDeleteIcon);
        }
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        boolean showClearIcon = true;
        try {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SearchBox);
            showClearIcon = typedArray.getBoolean(R.styleable.SearchBox_show_clear_icon, true);
        } catch (Throwable e) {
            DuboxLog.e(getClass().getName(), e.getMessage());
        }
        if (showClearIcon) {
            this.setIcon(mDelete);
            this.updateDeleteIcon();
        }
    }

    /*
     * private void doDelete() { Editable editable = this.getEditableText(); int st = this.getSelectionStart(); int en =
     * this.getSelectionEnd();
     *
     * int start; int end;
     *
     * if (st == en) { start = 0; end = st; } else if (st > en) { start = en; end = st; } else { start = st; end = en; }
     * editable.delete(start, end); this.setSelection(start); }
     */

    public void doDelete() {
        Editable editable = this.getEditableText();
        if (editable != null) {
            editable.clear();
        }
    }

    protected boolean isOnDelete(int x, int y) {
        this.requestFocus();
        int left = this.getWidth() - mIconWidth - this.getPaddingRight();
        int right = left + mIconWidth;
        int top = (this.getHeight() - mIconHeight) / 2;
        int bottom = top + mIconHeight;
        Rect rect = new Rect(left, top, right, bottom);

        if (rect.contains(x, y)) {
            return true;
        }
        return false;
    }

    protected void setIcon(Drawable icon) {
        // Drawable[] drawables = getCompoundDrawables();
        if (icon != null) {
            mIconWidth = icon.getIntrinsicWidth();
            mIconHeight = icon.getIntrinsicHeight();
            icon.setBounds(0, 0, mIconWidth, mIconHeight);
            setCompoundDrawables(null, null, icon, null); // 去掉搜索框中的搜索icon
            setGravity(Gravity.CENTER_VERTICAL);
            requestLayout();
        } else {
            mIconWidth = -1;
            mIconHeight = -1;
            setCompoundDrawables(null, null, null, null); // 去掉搜索框中的搜索icon
        }
    }

    private boolean isEmpty() {
        return TextUtils.isEmpty(getText().toString());
    }

    protected void updateDeleteIcon() {
        if (this.isEmpty()) {
            this.setIconLevel(3);
            return;
        }

        this.setIconLevel(0);
    }

    private boolean setIconLevel(int level) {
        if (this.mDelete == null) {
            return false;
        }

        if (this.mDelete.getLevel() == level) {
            return false;
        }

        this.mDelete.setLevel(level);
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.isEmpty()) {
            this.setIconLevel(3);
        } else {
            this.setIconLevel(enabled ? 0 : 2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled() == false) {
            return super.onTouchEvent(event);
        }

        final int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (!this.isOnDelete(x, y)) {
            mClean = false;
            if (this.mDelete.getLevel() == 1) {
                setIconLevel(0);
            }
            return super.onTouchEvent(event);
        }

        if (this.isEmpty()) {
            mClean = false;
            setIconLevel(3);
            return super.onTouchEvent(event);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                mClean = true;
                setIconLevel(1);
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mClean) {
                    mClean = false;
                    setIconLevel(0);
                    doDelete();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                mClean = false;
                setIconLevel(0);
                break;
            }
        }
        return true;
    }

    /**
     * Callback to watch the EditText field for empty/non-empty
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int before, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int after) {
            updateDeleteIcon();
        }

        public void afterTextChanged(Editable s) {
        }
    };

    public interface EditTextWatcher {
        void afterTextChanged(int length);

        // 目前用于粘贴的时候文字过长的回调
        void onTextOverLength();
    }

    public int getMaxByteLength() {
        return mMaxByteLength;
    }

    /**
     * 设置可输入最大字节长度
     */
    public void setMaxByteLength(int maxByteLength) {
        this.mMaxByteLength = maxByteLength;
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        @SuppressWarnings("deprecation")
        ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        switch (id) {
            case android.R.id.paste:
                // 如果是粘贴的话，判断文字长度
                long length = 0L;
                CharSequence text = clip.getText();
                if (text != null) {
                    length = TextTools.fetchCharNumber(text.toString());
                }
                if (length > mMaxByteLength) {
                    if (mEditTextWatcher != null) {
                        mEditTextWatcher.onTextOverLength();
                        return false;
                    }
                }
                break;

            default:
                break;
        }
        return super.onTextContextMenuItem(id);
    }

    /**
     * input输入过滤
     */
    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int len = 0;
            boolean more = false;
            boolean hasNotifiedMore = false;
            do {
                SpannableStringBuilder builder =
                        new SpannableStringBuilder(dest).replace(dstart, dend, source.subSequence(start, end));
                len = TextTools.fetchCharNumber(builder.toString());
                more = len > mMaxByteLength;
                if (more) {
                    if (!hasNotifiedMore) {
                        notifyTextChanged(len);
                        hasNotifiedMore = true;
                    }
                    end--;
                    source = source.subSequence(start, end);
                } else {
                    notifyTextChanged(len);
                }
            } while (more);
            return source;
        }
    };

    private void notifyTextChanged(int len) {
        if (mEditTextWatcher != null) {
            mEditTextWatcher.afterTextChanged(len);
        }
    }
}
