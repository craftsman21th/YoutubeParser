package com.moder.compass.ui.widget.titlebar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.component.base.R;
import com.moder.compass.ui.RedRemindButton;
import com.moder.compass.ui.widget.CircleImageView;
import com.moder.compass.util.StateListDrawableHelper;

/**
 * 尽量不要再用继承、copy sub布局的方式新增其他的标题栏，尽量复用BaseTitleBar
 */
public class FileTitleBar extends BaseTitleBar {

    private static final String TAG = "FileTitleBar";

    private int mTitleMode;
    /** 文件列表首页模式   */
    public static final int ROOT_FILELIST_MODE = 0;
    /** 文件列表二级页模式   */
    public static final int SUB_FILELIST_MODE = 1;
    /** 分类列表模式   */
    public static final int CATEGORY_MODE = 2;

    /**
     * 返回
     */
    private ImageView mFileListTitleBack;
    /**
     * 文件名称
     */
    private TextView mFileListTitleName;
    /**
     * 右边第四个按钮
     */
    private ImageView mFileListTitleRightFour;
    /**
     * 右边第三个按钮
     */
    private ImageView mFileListTitleRightThree;

    /**
     * 右边第二个按钮
     */
    private RedRemindButton mFileListTitleRightTwo;

    /**
     * 右边第一个按钮
     */
    private ImageView mFileListTitleRightOne;

    private VipAvatarIconView mAvatarImage;

    private ImageView ivPhotoDecorate;

    private TextView mCenterTitleBar;

    private ViewGroup searchContainer;

    public FileTitleBar(Activity activity) {
        super(activity);
    }

    public FileTitleBar(Activity activity, View view) {
        super(activity, view);
    }

    @Override
    protected void initDefaultView() {
        ViewStub stub = (ViewStub) findViewById(R.id.viewstub_file_list_title);
        stub.inflate();
        mRootViewCommon = (ViewGroup) findViewById(R.id.file_list_title_root);

        mFileListTitleBack = (ImageView) findViewById(R.id.file_list_title_back);
        ivPhotoDecorate = (ImageView) findViewById(R.id.iv_photo_decorate);
        mFileListTitleName = (TextView) findViewById(R.id.file_list_title_name);
        mAvatarImage = (VipAvatarIconView) findViewById(R.id.civ_photo);

        mFileListTitleRightFour = (ImageView) findViewById(R.id.file_list_title_right_four);
        mFileListTitleRightThree = (ImageView) findViewById(R.id.file_list_title_right_three);
        mFileListTitleRightTwo = (RedRemindButton) findViewById(R.id.file_list_title_right_two);
        mFileListTitleRightOne = (ImageView) findViewById(R.id.file_list_title_right_one);

        mCenterTitleBar = (TextView) findViewById(R.id.file_list_title_name_center);
        searchContainer = (FrameLayout) findViewById(R.id.search_container);

        initDefaultImageView();
    }

    /**
     * 设置默认按钮
     */
    public void initDefaultImageView() {
        DuboxLog.d(TAG, "initDefaultImageView");
        switchAvatarAndBack(false);
        mFileListTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onBackButtonClicked();
                }
            }
        });
    }
    /**
     * 设置返回键按键处理
     * @param listener
     */
    public void setTitleBackOnClickListener(View.OnClickListener listener) {
        DuboxLog.d(TAG, "setTitleBackOnClickListener");
        switchAvatarAndBack(false);
        mFileListTitleBack.setOnClickListener(listener);
    }

    public void setTitleBackEnable(boolean enable) {
        mFileListTitleBack.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        mFileListTitleBack.setEnabled(enable);
    }

    /**
     * 设置右边第一个按钮
     *
     * @param resId
     * @param listener
     */
    public void setRightOneOnClickListener(int resId, View.OnClickListener listener) {
        mFileListTitleRightOne.setVisibility(View.VISIBLE);

        Drawable icon = StateListDrawableHelper
                .createBgDrawableWithAlphaMode(mFileListTitleRightOne.getContext(), resId, 0.4f);
        mFileListTitleRightOne.setClickable(true);
        mFileListTitleRightOne.setBackgroundDrawable(icon);
        mFileListTitleRightOne.setOnClickListener(listener);
    }

    public void setRightOneEnable(boolean enable) {
        mFileListTitleRightOne.setVisibility(enable ? View.VISIBLE : View.GONE);
    }
    public void setRightTwoEnable(boolean enable) {
        mFileListTitleRightTwo.setVisibility(enable ? View.VISIBLE : View.GONE);
    }
    public void setRightThreeEnable(boolean enable) {
        mFileListTitleRightThree.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    public void setRightOneRes(int resId) {
        Drawable icon = StateListDrawableHelper
                .createBgDrawableWithAlphaMode(mFileListTitleRightOne.getContext(), resId, 0.4f);
        mFileListTitleRightOne.setClickable(true);
        mFileListTitleRightOne.setBackground(icon);
    }

    /**
     * 设置右边第二个按钮
     *
     * @param resId
     * @param listener
     */
    public void setRightTwoOnClickListener(int resId, View.OnClickListener listener) {
        mFileListTitleRightTwo.setVisibility(View.VISIBLE);
        mFileListTitleRightTwo.setImageResource(resId);
        mFileListTitleRightTwo.setOnClickListener(listener);
    }

    /**
     * 设置右边第三个按钮
     *
     * @param resId
     * @param listener
     */
    public void setRightThreeOnClickListener(int resId, View.OnClickListener listener) {
        mFileListTitleRightThree.setVisibility(View.VISIBLE);
        Drawable icon = StateListDrawableHelper
                .createBgDrawableWithAlphaMode(mFileListTitleRightThree.getContext(), resId, 0.4f);
        mFileListTitleRightThree.setClickable(true);
        mFileListTitleRightThree.setBackgroundDrawable(icon);
        mFileListTitleRightThree.setOnClickListener(listener);
    }

    @Override
    public void setLeftLabel(String spanBuilder) {
        DuboxLog.d(TAG, "setLeftLabel");
        if (mFileListTitleName != null) {
            mFileListTitleName.setText(spanBuilder);
        }
    }
    @Override
    public void setLeftLabel(SpannableStringBuilder spanBuilder) {
        DuboxLog.d(TAG, "setLeftLabel");
        if (mFileListTitleName != null) {
            mFileListTitleName.setText(spanBuilder);
        }
    }

    @Override
    public void setMiddleTitle(int id) {
        if (id > 0 && mCenterTitleBar != null) {
            mCenterTitleBar.setVisibility(View.VISIBLE);
            mFileListTitleName.setVisibility(View.GONE);
            mCenterTitleBar.setText(id);
        }
    }

    @Override
    public void setMiddleTitle(String text) {
        if (text != null && mCenterTitleBar != null) {
            mCenterTitleBar.setVisibility(View.VISIBLE);
            mFileListTitleName.setVisibility(View.GONE);
            mCenterTitleBar.setText(text);
        }
    }

    @Override
    public void setMiddleTitle(SpannableStringBuilder spanBuilder) {
        if (spanBuilder != null && mCenterTitleBar != null) {
            mCenterTitleBar.setVisibility(View.VISIBLE);
            mFileListTitleName.setVisibility(View.GONE);
            mCenterTitleBar.setText(spanBuilder);
        }
    }

    public void setLeftLabel(int label) {
        if (mFileListTitleName != null) {
            mFileListTitleName.setText(label);
        }
    }
    // 文件首页模式
    public void setRootFilelistTitleBarView() {
        switchAvatarAndBack(true);
    }

    // 文件二级页模式
    public void setSubFilelistTitleBarView() {
        switchAvatarAndBack(false);
    }

    public void setTitleMode(int titleMode) {
        this.mTitleMode = titleMode;
    }

    /**
     * 重置title bar
     */
    public void resetTitleBar() {
        DuboxLog.d(TAG, "resetTitleBar");
        mFileListTitleRightOne.setVisibility(View.GONE);
        mFileListTitleRightTwo.setVisibility(View.GONE);
        mFileListTitleRightThree.setVisibility(View.GONE);
        mFileListTitleRightFour.setVisibility(View.GONE);
        mFileListTitleName.setText("");
        initDefaultImageView();

    }

    public RedRemindButton getFileListTitleRightTwo() {
        return mFileListTitleRightTwo;
    }

    public CircleImageView getAvatarImage() {
        return mAvatarImage.getAvatarView();
    }

    public void setAvatarClickListener(View.OnClickListener onClickListener) {
        if (mAvatarImage != null) {
            mAvatarImage.setOnClickListener(onClickListener);
        }
    }

    public VipAvatarIconView getVipAvatarIconView() {
        return mAvatarImage;
    }

    public ImageView getAvatarDecorateView() {
        return ivPhotoDecorate;
    }

    public ViewGroup getSearchContainer() {
        return searchContainer;
    }

    private void switchAvatarAndBack(boolean isShowAvatar) {
        if (isShowAvatar) {
            mAvatarImage.setVisibility(View.VISIBLE);
            searchContainer.setVisibility(View.VISIBLE);
            ivPhotoDecorate.setVisibility(View.VISIBLE);
            mFileListTitleBack.setVisibility(View.GONE);
            mFileListTitleName.setVisibility(View.GONE);
        } else {
            mAvatarImage.setVisibility(View.GONE);
            searchContainer.setVisibility(View.GONE);
            ivPhotoDecorate.setVisibility(View.GONE);
            mFileListTitleBack.setVisibility(View.VISIBLE);
            mFileListTitleName.setVisibility(View.VISIBLE);
        }
    }
}
