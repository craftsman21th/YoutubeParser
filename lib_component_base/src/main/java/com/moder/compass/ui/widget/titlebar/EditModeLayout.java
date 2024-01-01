/*
 * EditModeLayout.java
 * classes : com.dubox.drive.ui.widget.titlebar.EditModeLayout
 * @author 文超
 * V 1.0.0
 * Create at 2014年5月22日 下午4:39:02
 */
package com.moder.compass.ui.widget.titlebar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.moder.compass.BaseApplication;
import com.moder.compass.component.base.R;


/**
 * com.dubox.drive.ui.widget.titlebar.EditModeLayout
 * 
 * @author 文超 <br/>
 *         create at 2014年5月22日 下午4:39:02
 */
public class EditModeLayout {

    private static final String TAG = "EditModeLayout";

    // 编辑模式titlebar
    private View mEditTitleLayout;
    // 编辑模式左侧按钮
    private Button mEditLeftButton;
    // 编辑模式右侧按钮
    private Button mEditRightButton;
    // 编辑模式title
    private TextView mEditTitleText;

    private ITitleBarSelectedModeListener mEditClickListener;

    private final Activity mActivity;

    /**
     * @param viewGroup
     * @param activity
     */
    public EditModeLayout(Activity activity, ViewGroup viewGroup) {
        mActivity = activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_bar_edit_mode_layout, viewGroup);
        initEditTitleView(view);
    }

    private void initEditTitleView(View view) {
        mEditTitleLayout = view.findViewById(R.id.edit_mode_layout);
        // 默认是不显示多选模式
        mEditTitleLayout.setVisibility(View.GONE);
        mEditLeftButton = (Button) view.findViewById(R.id.edit_left_button);
        mEditRightButton = (Button) view.findViewById(R.id.edit_right_button);
        mEditRightButton.setText(R.string.select_all);
        mEditTitleText = (TextView) view.findViewById(R.id.edit_title);
        if (mEditLeftButton != null) {
            mEditLeftButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mEditClickListener != null) {
                        mEditClickListener.onCancelClick();
                    }
                }
            });
        }
        if (mEditRightButton != null) {
            mEditRightButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (mEditClickListener != null) {
                        mEditClickListener.onSelectAllClick();
                    }
                }
            });
        }
        // 进入编辑态时，EditModelTitle 只是覆盖在原先 View 的上层，导致点击事件可以下传，所以做一个拦截
        if (mEditTitleText != null) {
            mEditTitleText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    public void setHeight(int height) {
        ViewGroup.LayoutParams params = mEditTitleLayout.getLayoutParams();
        params.height = height;
        mEditTitleLayout.setLayoutParams(params);
    }

    void switchToEditMode() {
        switchToEditMode(null);
    }

    /**
     * 设置编辑态右侧显示的文本
     * @param text
     */
    public void setEditRightButtonText(String text) {
        mEditRightButton.setText(text);
    }

    public void switchToEditMode(final EditModeLayoutVisibleListener listener) {
        mEditTitleLayout.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.titlebar_push_top_in);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (listener != null) {
                    listener.onChange(true);
                }
            }
        });
        mEditTitleLayout.startAnimation(anim);
    }

    public void switchToNormalMode() {
        mEditTitleLayout.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(BaseApplication.getInstance(), R.anim.titlebar_push_top_out);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mEditTitleLayout.setVisibility(View.GONE);
            }
        });
        mEditTitleLayout.startAnimation(anim);
    }

    public void setSelectedModeListener(ITitleBarSelectedModeListener listener) {
        mEditClickListener = listener;
    }

    public void setTitle(CharSequence text) {
        mEditTitleText.setText(text);
    }

    public void setTitle(int resid) {
        mEditTitleText.setText(resid);
    }

    public void setSelectedAllButton(int resid) {
        mEditRightButton.setText(resid);
    }

    /**
     * 隐藏或显示全选按钮
     * 
     * @author libin09 2014-7-10
     * @param isVisible
     */
    public void setSelectedAllButtonVisible(boolean isVisible) {
        mEditRightButton.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * @param resid
     */
    public void setBackgroundResource(int resid) {
        try {
            mEditTitleLayout.setBackgroundColor(mActivity.getResources().getColor(resid));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 修改编辑模式文本颜色
     *
     * @param resid
     */
    public void setTextResource(int resid) {
        mEditLeftButton.setTextColor(mActivity.getResources().getColor(resid));
        mEditRightButton.setTextColor(mActivity.getResources().getColor(resid));
        mEditTitleText.setTextColor(mActivity.getResources().getColor(resid));
    }

    /**
     * 修改编辑模式中间文本的颜色
     * @param resid
     */
    public void setEditTextColorResource(int resid) {
        mEditTitleText.setTextColor(mActivity.getResources().getColor(resid));
    }

    public View getRootView() {
        return mEditTitleLayout;
    }

    public interface EditModeLayoutVisibleListener {
        void onChange(boolean isVisible);
    }

}
