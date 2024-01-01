/*
 * BaseTitleBar.java
 * classes : com.dubox.drive.ui.widget.titlebar.BaseTitleBar
 * @panwei
 * V 1.0.0
 * Create at 2013-5-21 下午2:44:51
 */
package com.moder.compass.ui.widget.titlebar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.moder.compass.component.base.R;

/**
 * 尽量不要再用继承、copy sub布局的方式新增其他的标题栏，尽量复用BaseTitleBar
 */
public class BaseTitleBar extends AbstractTitleBar {
    // common title-------------
    protected ViewGroup mRootViewCommon;
    protected TextView mTitleText;
    protected Button mRightButton;
    protected Button mLeftPlaceHolder;// 和右侧按钮一模一样，放在这里用于占位，使按钮长度自适应且title文字保证在中间不会和按钮重叠
    protected ImageView mBackButton;
    protected ImageView mRightBtnTag;
    protected ImageButton mRightMenuButton;
    protected TextView mMiddleTitleText;
    protected TextView mLeftTitleText;
    // common title-------------

    // title with avatar-------------

    // titlebar上按钮的回调
    protected ICommonTitleBarClickListener mClickListener;
    // 右边第一个按钮, 其他布局为空
    @Nullable
    protected ImageButton mSecondButton;

    public BaseTitleBar(Activity activity) {
        this(activity, null);
    }

    public BaseTitleBar(Activity activity, View view) {
        super(activity, view);
    }

    @Override
    protected void destroyDefaultView() {
        mActivity.clear();
        mTitleText = null;
        mLeftPlaceHolder = null;
        mRightButton = null;
        mBackButton = null;
        mRootViewCommon = null;
        mClickListener = null;
        mLeftTitleText = null;
    }

    @Override
    protected void initDefaultView() {
        ViewStub stub = (ViewStub) findViewById(R.id.viewstub_general_title);
        if (stub != null) {
            stub.inflate();
            initRootViewCommon();
        }
    }

    protected void initRootViewCommon() {
        mRootViewCommon = (ViewGroup) findViewById(R.id.title_bar_root_view);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mLeftTitleText = (TextView) findViewById(R.id.left_title_text);
        mMiddleTitleText = (TextView) findViewById(R.id.middle_title_text);

        mLeftPlaceHolder = (Button) findViewById(R.id.left_place_holder);
        mRightBtnTag = (ImageView) findViewById(R.id.right_button_tag);
        // 小红点在大多数的情况都不需要，所以默认隐藏
        mRightBtnTag.setVisibility(View.GONE);

        mRightButton = (Button) findViewById(R.id.right_button);
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRightBtnTag.setVisibility(View.GONE);
                if (mClickListener != null) {
                    mClickListener.onRightButtonClicked(v);
                }
            }
        });
        // 默认不显示右侧按钮区域
        setRightLayoutVisible(false);

        mBackButton = (ImageView) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onBackButtonClicked();
                }
            }
        });

        mRightMenuButton = (ImageButton) findViewById(R.id.right_menu_button);
        mRightMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onRightButtonClicked(v);
                }
            }
        });

        mSecondButton = (ImageButton) findViewById(R.id.right_second_button);
    }

    /**
     * 设置右侧功能按钮用于提示用户的小红点的显示状态
     *
     * @param visible
     */
    public void setRightButtonTagVisible(boolean visible) {
        if (mRightBtnTag != null) {
            mRightBtnTag.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public ImageView getBackButton() {
        return mBackButton;
    }

    public void setBackLayoutVisible(boolean visible) {
        if (mBackButton != null) {
            mBackButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setRightLayoutVisible(boolean visible) {
        if (mRightButton != null) {
            mRightButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        }
        if (mRightBtnTag != null) {
            mRightBtnTag.setVisibility(mRightBtnTag.getVisibility());
        }
    }

    @Override
    public ViewGroup getRootView() {
        return mRootViewCommon;
    }

    public void setTopTitleBarClickListener(ICommonTitleBarClickListener titleBarClickListener) {
        mClickListener = titleBarClickListener;
    }

    /**
     * 获取居左标题,可能为null
     *
     * @return
     */
    public TextView getLeftTextView() {
        return mTitleText;
    }

    public void setLeftLabel(String label) {
        if (mTitleText != null) {
            mTitleText.setText(label);
        }
    }

    public void setLeftLabel(int label) {
        if (mTitleText != null) {
            mTitleText.setText(label);
        }
    }

    public void setBackLabel(int label) {
        if (mLeftTitleText != null) {
            mLeftTitleText.setVisibility(View.VISIBLE);
            mLeftTitleText.setText(label);
        }
    }

    public void setBackLabelVisible(boolean isVisible) {
        if (mLeftTitleText != null) {
            mLeftTitleText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }
    }

    public void setLeftLabel(SpannableStringBuilder spanBuilder) {
        if (mTitleText != null) {
            mTitleText.setText(spanBuilder);
        }
    }

    public void setRightLabel(String label) {
        setRightLayoutVisible(true);
        if (mRightButton != null) {
            mRightButton.setText(label);
        }
        if (mLeftPlaceHolder != null) {
            mLeftPlaceHolder.setText(label);
        }
    }

    public void setRightLabel(int label) {
        setRightLayoutVisible(true);
        if (mRightButton != null) {
            mRightButton.setText(label);
        }
        if (mLeftPlaceHolder != null) {
            mLeftPlaceHolder.setText(label);
        }
    }

    public void setRightButtonImage(@DrawableRes int res) {
        if (mRightMenuButton != null) {
            mRightMenuButton.setVisibility(View.VISIBLE);
            mRightMenuButton.setBackgroundResource(res);
        }
    }

    public void setRightSecondImage(@DrawableRes int res) {
        if (mSecondButton != null) {
            mSecondButton.setImageResource(res);
        }
    }

    public void setRightSecondClick(View.OnClickListener listener) {
        if (mSecondButton != null) {
            mSecondButton.setOnClickListener(listener);
        }
    }

    @Nullable
    public View getRightSecondView() {
        return mSecondButton;
    }

    public void setRightButtonDrawable(Drawable drawable) {
        if (mRightMenuButton != null) {
            mRightMenuButton.setVisibility(View.VISIBLE);

            mRightMenuButton.setBackgroundDrawable(drawable);
        }
    }

    public void setRightMenuButtonVisible(boolean visible) {
        if (mRightMenuButton == null) {
            return;
        }
        if (visible) {
            mRightMenuButton.setVisibility(View.VISIBLE);
        } else {
            mRightMenuButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightEnable(boolean isEnabled) {
        if (mRightButton != null) {
            mRightButton.setEnabled(isEnabled);
        }
    }

    public View getRightButtonView() {
        return mRightButton;
    }

    public int getHeight() {
        return mRootViewCommon.getHeight();
    }

    /**
     * 切换title的两种样式，common / with avatar
     *
     * @param showAvatar 是否为with avatar样式
     */
    public void showAvatar(boolean showAvatar) {

    }

    /**
     * @param resid
     *
     * @see AbstractTitleBar#setBackgroundResource(int)
     */
    @Override
    public void setBackgroundResource(int resid) {
        mRootViewCommon.setBackgroundResource(resid);
        super.setBackgroundResource(resid);
    }

    /**
     * 设置title颜色，直接传id，默认支持暗黑
     *
     * @param resid
     */
    public void setBackgroundColor(int resid) {
        Activity activity = mActivity.get();
        if (activity != null) {
            mRootViewCommon.setBackgroundColor(activity.getResources().getColor(resid));
        }
    }

    /**
     * 设置title view 是否可见
     *
     * @param visible
     */
    public void setRootViewVisible(boolean visible) {
        if (mRootViewCommon == null) {
            return;
        }
        if (visible) {
            mRootViewCommon.setVisibility(View.VISIBLE);
        } else {
            mRootViewCommon.setVisibility(View.GONE);
        }
    }

    public void setMiddleTitle(int id) {
        if (mMiddleTitleText != null) {
            mMiddleTitleText.setText(id);
        }
    }

    public void setMiddleTitle(String text) {
        if (mMiddleTitleText != null) {
            mMiddleTitleText.setText(text);
        }
    }

    public void setMiddleTitle(SpannableStringBuilder spanBuilder) {
        if (mMiddleTitleText != null) {
            mMiddleTitleText.setText(spanBuilder);
        }
    }

    /**
     * 设置选中的数量，title会显示相应的文案，并对选择按钮的文案进行修改。
     * 当选择数量等于全部数量时，那么选择按钮显示为“全不选”，否则显示“全选”
     *
     * @param selectedNum 选中的数量
     * @param allNum      全部数量
     * @param textRes     文案资源
     */
    public void setSelectedNum(int selectedNum, int allNum, int textRes) {
        final Activity activity = mActivity.get();
        if (activity == null) {
            return;
        }
        String text = activity.getResources().getString(textRes, selectedNum);
        mEditModeLayout.setTitle(text);
        mEditModeLayout.setSelectedAllButton(selectedNum == allNum ? R.string.deselect_all : R.string.select_all);
    }

    /**
     * 获取中间标题,可能为null
     *
     * @return
     */
    public TextView getMiddleTextView() {
        return mMiddleTitleText;
    }

    /**
     * 确保alpha在合适的范围内
     *
     * @param alpha
     *
     * @return
     */
    public float ensureAlpha(float alpha) {
        if (alpha > 1) {
            return 1;
        } else if (alpha < 0) {
            return 0;
        } else {
            return alpha;
        }
    }
}
