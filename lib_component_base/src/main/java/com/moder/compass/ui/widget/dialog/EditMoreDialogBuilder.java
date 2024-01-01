package com.moder.compass.ui.widget.dialog;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 编辑态-更多按钮的dialog构建类
 *
 * @author wangyang34
 * @since 2019年1月10日
 */
public class EditMoreDialogBuilder {

    /** Activity */
    private Activity mActivity;

    /** 条目 */
    private List<EditMoreInfo> mEditItems = new ArrayList<>();

    /** 标题*/
    private String mTittle;

    /** 取消按钮的监听器 */
    private View.OnClickListener mCancelListener;

    /**
     * 对话框是否可以返回键取消
     */
    private boolean mCancelable = true;

    /** 无效的资源ID*/
    private static final int INVALID_RES_ID = -1;

    /**
     * 对话框是否菜单项之间显示分割线，默认item之前不要分割线
     */
    private boolean mShowDividerBetweenItems = false;

    /**
     * 对话框是否在取消按钮上方显示分割线，默认取消上方要分割线
     */
    private boolean mShowDividerAboveCancel = true;

    /**
     * 构造
     *
     * @param activity activity
     */
    public EditMoreDialogBuilder(Activity activity) {
        mActivity = activity;
    }

    public EditMoreDialogBuilder setTittle(int resDialogTittle) {
        if (mActivity != null) {
            mTittle = mActivity.getString(resDialogTittle);
        }
        return this;
    }

    public String getTittle() {
        return mTittle;
    }

    /**
     * 设置item之间是否显示分割线
     * @param isShow 是否显示分割线
     */
    public EditMoreDialogBuilder setIsShowDivider(boolean isShow) {
        mShowDividerBetweenItems = isShow;
        return this;
    }

    public boolean getIsShowDivider() {
        return mShowDividerBetweenItems;
    }


    /**
     * 设置取消按钮上方是否显示分割线
     * @param isShow 是否显示分割线
     */
    public EditMoreDialogBuilder setIsShowDividerAboveCancel(boolean isShow) {
        mShowDividerAboveCancel = isShow;
        return this;
    }

    public boolean getIsShowDividerAboveCancel() {
        return mShowDividerAboveCancel;
    }

    /**
     * 添加item,item内容居中对齐
     * @param resTitle 标题
     * @param listener 点击
     * @return builder 构造
     */
    public EditMoreDialogBuilder addItemCenterLayout(int resTitle, View.OnClickListener listener) {
        String title = "";
        if (mActivity != null) {
            title = mActivity.getString(resTitle);
        }
        return addItemCenterLayout(title, INVALID_RES_ID, listener);
    }

    public EditMoreDialogBuilder addItemCenterLayout(String tittle, int resIcon,
                                                     View.OnClickListener listener) {
        Drawable icon = null;
        if (mActivity != null && resIcon != INVALID_RES_ID) {
            icon = ContextCompat.getDrawable(mActivity, resIcon);
        }
        mEditItems.add(new EditMoreInfo(tittle, icon, Gravity.CENTER, listener));
        return this;
    }

        /**
         * 添加item
         * @param resTitle 标题
         * @param resIcon 图标
         * @param listener 点击
         * @return builder 构造
         */
    public EditMoreDialogBuilder addItem(int resTitle, int resIcon, View.OnClickListener listener) {
        String title = "";
        if (mActivity != null) {
            title = mActivity.getString(resTitle);
        }
        return addItem(title, resIcon, listener);
    }

    /**
     * 添加item
     * @param title 标题
     * @param resIcon 图标
     * @param listener 点击
     * @return builder 构造
     */
    public EditMoreDialogBuilder addItem(String title, int resIcon, View.OnClickListener listener) {
        Drawable icon = null;
        if (mActivity != null && resIcon != INVALID_RES_ID) {
            icon = ContextCompat.getDrawable(mActivity, resIcon);
        }
        mEditItems.add(new EditMoreInfo(title, icon, listener));
        return this;
    }

    /**
     * 添加item
     * @param resTitle 标题
     * @param resIcon 图标
     * @param listener 点击
     * @param isEnable 是否可点击
     * @return builder 构造
     */
    public EditMoreDialogBuilder addItem(int resTitle, int resIcon,
                                         View.OnClickListener listener, boolean isEnable) {
        Drawable icon = null;
        if (mActivity != null && resIcon != INVALID_RES_ID) {
            icon = ContextCompat.getDrawable(mActivity, resIcon);
        }
        String title = "";
        if (mActivity != null) {
            title = mActivity.getString(resTitle);
        }
        mEditItems.add(new EditMoreInfo(title, icon, Gravity.NO_GRAVITY, listener, false, isEnable));
        return this;
    }

    /**
     * 添加item
     * @param title 标题
     * @param icon 图标
     * @param listener 点击
     * @return builder 构造
     */
    public EditMoreDialogBuilder addItem(String title, Drawable icon,
                                         View.OnClickListener listener, boolean isSetGray) {
        mEditItems.add(new EditMoreInfo(title, icon, listener, isSetGray));
        return this;
    }

    /**
     * 添加item
     * @param title 标题
     * @param icon 图标
     * @param listener 点击
     * @return builder 构造
     */
    public EditMoreDialogBuilder addItem(String title, int textStyle, Drawable icon, View.OnClickListener listener,
                                         boolean enable, boolean select) {
        mEditItems.add(new EditMoreInfo(title, textStyle, icon, listener, enable, select));
        return this;
    }

    /**
     * 生成dialog
     *
     * @return dialog
     */
    public EditMoreDialog build() {
        EditMoreDialog dialog = new EditMoreDialog(mActivity, this);
        dialog.setCancelable(mCancelable);
        return dialog;
    }

    /**
     * 获取item
     *
     * @return dialog
     */
    public List<EditMoreInfo> getItems() {
        return mEditItems;
    }

    public void setCancelListener(View.OnClickListener listener) {
        mCancelListener = listener;
    }

    public View.OnClickListener getCancelListener() {
        return mCancelListener;
    }

    public void setCancelable(boolean isCancelable) {
        mCancelable = isCancelable;
    }

}
