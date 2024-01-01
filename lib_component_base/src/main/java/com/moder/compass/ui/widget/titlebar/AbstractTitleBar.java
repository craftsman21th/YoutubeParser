/*
 * AbstractTitleBar.java
 * classes : com.dubox.drive.ui.widget.titlebar.AbstractTitleBar
 * @author 文超
 * V 1.0.0
 * Create at 2014年5月26日 下午3:31:42
 */
package com.moder.compass.ui.widget.titlebar;

import java.lang.ref.WeakReference;

import com.moder.compass.component.base.R;
import com.moder.compass.ui.widget.titlebar.EditModeLayout.EditModeLayoutVisibleListener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 尽量不要再用继承、copy sub布局的方式新增其他的标题栏，尽量复用BaseTitleBar
 */
public abstract class AbstractTitleBar {
    // 传入的activity
    protected WeakReference<Activity> mActivity;

    protected EditModeLayout mEditModeLayout;

    protected boolean mIsSelectedMode;
    protected View mParentView;

    public AbstractTitleBar(Activity activity) {
        this(activity, null);
    }

    public AbstractTitleBar(Activity activity, View view) {
        mActivity = new WeakReference<Activity>(activity);
        mParentView = view;
        initDefaultView();
        initEditMode(activity);
    }

    /**
     * 初始化编辑态的title layout
     *
     * @param activity activity
     */
    protected void initEditMode(Activity activity) {
        mEditModeLayout = new EditModeLayout(activity, getRootView());
    }

    protected View findViewById(int id) {
        if (mParentView != null) {
            return mParentView.findViewById(id);
        }
        Activity activity = mActivity.get();
        if (activity != null) {
            return activity.findViewById(id);
        } else {
            return null;
        }
    }

    protected abstract void initDefaultView();

    protected abstract ViewGroup getRootView();

    public void destroy() {
        mActivity.clear();
        mEditModeLayout = null;
        destroyDefaultView();
    }

    protected abstract void destroyDefaultView();

    public synchronized void switchToEditMode() {
        if (!mIsSelectedMode) {
            mIsSelectedMode = true;
            mEditModeLayout.switchToEditMode(new EditModeLayout.EditModeLayoutVisibleListener() {

                @Override
                public void onChange(boolean isVisible) {
                    if (isVisible) {
                        //                        getRootView().setVisibility(View.GONE);
                    }
                }
            });
            if (editModeListener != null) {
                editModeListener.switchToEditMode();
            }
        }
    }

    public synchronized void switchToNormalMode() {
        if (mIsSelectedMode) {
            mIsSelectedMode = false;
            if (getRootView() != null) {
                getRootView().setVisibility(View.VISIBLE);
            }
            if (mEditModeLayout != null) {
                mEditModeLayout.switchToNormalMode();
            }
            if (editModeListener != null) {
                editModeListener.switchToNormalMode();
            }
        }
    }

    public boolean isSelectedMode() {
        return mIsSelectedMode;
    }

    /**
     * 设置选中的数量，title会显示相应的文案，并对选择按钮的文案进行修改。
     * <p/>
     * 当选择数量等于全部数量时，那么选择按钮显示为“全不选”，否则显示“全选”
     *
     * @param selectedNum 选中的数量
     * @param allNum      全部数量
     */
    public void setSelectedNum(int selectedNum, int allNum) {
        final Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }

        String text = activity.getResources().getString(R.string.selected_file_to_edit, selectedNum);
        mEditModeLayout.setTitle(text);

        mEditModeLayout.setSelectedAllButton(selectedNum == allNum ? R.string.deselect_all : R.string.select_all);
    }

    /**
     * 设置选中的数量，title指定的文案，并对选择按钮的文案进行修改。
     * <p/>
     * 当选择数量等于全部数量时，那么选择按钮显示为“全不选”，否则显示“全选”
     *
     * @param titleText   title文案
     * @param selectedNum 选中的数量
     * @param allNum      全部数量
     */
    public void setSelectedNum(String titleText, int selectedNum, int allNum) {
        final Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        mEditModeLayout.setTitle(titleText);
        if (selectedNum == allNum && allNum > 0) {
            mEditModeLayout.setSelectedAllButton(R.string.deselect_all);
        } else {
            mEditModeLayout.setSelectedAllButton(R.string.select_all);
        }
    }

    /**
     * 隐藏或显示全选按钮
     *
     * @param isVisible
     *
     * @author libin09 2014-7-10
     */
    public void setSelectedAllButtonVisible(boolean isVisible) {
        mEditModeLayout.setSelectedAllButtonVisible(isVisible);
    }

    public void setSelectedModeListener(ITitleBarSelectedModeListener listener) {
        if (mEditModeLayout != null) {
            mEditModeLayout.setSelectedModeListener(listener);
        }
    }

    public void setBackgroundResource(int resid) {
    }

    public void setSelectModeBackgroundResource(int resid) {
        mEditModeLayout.setBackgroundResource(resid);
    }

    public void setSelectModeTextResource(int resid) {
        mEditModeLayout.setTextResource(resid);
    }

    /**
     * 设置编辑态中间文本的颜色
     * @param resid
     */
    public void setSelectModeEditTextColorResource(int resid) {
        mEditModeLayout.setEditTextColorResource(resid);
    }

    /**
     * 设置编辑态title bar 高度
     *
     * @param height 高度 单位像素
     */
    void setEditModeLayoutHeight(int height) {
        mEditModeLayout.setHeight(height);
    }

    private EditModeListener editModeListener;

    public void setEditModeListener(EditModeListener editModeListener) {
        this.editModeListener = editModeListener;
    }

    public interface EditModeListener {
        public void switchToEditMode();
        public void switchToNormalMode();
    }
}
