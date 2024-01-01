package com.moder.compass.ui.widget.dialog;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

/**
 * 编辑态-更多按钮条目信息
 *
 * @author wangyang34
 * @since 2019年1月10日
 */
public class EditMoreInfo {

    /** 标题 */
    public String mTitle;

    /** 图标 */
    public Drawable mIcon;

    /** 点击 */
    public View.OnClickListener mClickListener;

    /** 布局 */
    public int mGravity;

    /** 是否置灰 */
    public boolean mIsSetGray;

    /** 是否enable */
    public boolean mIsEnable;

    /** 是否选中 */
    public boolean mIsSelected;

    /** 文字样式 */
    public int mTitleTextStyle;

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, View.OnClickListener clickListener) {
        this(title, icon, Gravity.NO_GRAVITY, clickListener, false);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, View.OnClickListener clickListener, boolean isSetGray) {
        this(title, icon, Gravity.NO_GRAVITY, clickListener, isSetGray);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, int textStyle, Drawable icon, View.OnClickListener clickListener,
                        boolean enable, boolean select) {
        this(title, textStyle, icon, Gravity.NO_GRAVITY, clickListener, false, enable, select);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, View.OnClickListener clickListener,
                        boolean isSetGray, boolean enable, boolean select) {
        this(title, icon, Gravity.NO_GRAVITY, clickListener, isSetGray, enable, select);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, int gravity,
                        View.OnClickListener clickListener) {
        this(title, icon, gravity, clickListener, false);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, int gravity,
                        View.OnClickListener clickListener, boolean isSetGray) {
        this(title, icon, gravity, clickListener, isSetGray, true);
    }


    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, int gravity,
                        View.OnClickListener clickListener, boolean isSetGray, boolean enable) {
        this(title, icon, gravity, clickListener, isSetGray, enable, false);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, Drawable icon, int gravity,
                        View.OnClickListener clickListener, boolean isSetGray, boolean enable, boolean select) {
        this(title, -1, icon, gravity, clickListener, isSetGray, enable, false);
    }

    /**
     * 构造
     * @param title 标题
     * @param icon 图标
     * @param clickListener 点击
     * @return builder 构造
     */
    public EditMoreInfo(String title, int textStyle, Drawable icon, int gravity,
                        View.OnClickListener clickListener, boolean isSetGray, boolean enable, boolean select) {
        mTitle = title;
        mTitleTextStyle = textStyle;
        mIcon = icon;
        mClickListener = clickListener;
        mGravity = gravity;
        mIsSetGray = isSetGray;
        mIsEnable = enable;
        mIsSelected = select;
    }
}
