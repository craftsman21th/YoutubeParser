package com.moder.compass.ui.widget.titlebar;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moder.compass.component.base.R;
import com.moder.compass.ui.widget.SearchBox;
/**
 * 尽量不要再用继承、copy sub布局的方式新增其他的标题栏，尽量复用BaseTitleBar
 */
public class SearchTitleBar extends BaseTitleBar {

    private static final String TAG = "SearchTitleBar";
    private SearchBox mSearchBox;
    private View tvRightSearch;

    public SearchTitleBar(Activity activity) {
        super(activity);
    }

    @Override
    public void showAvatar(boolean showAvatar) {

    }

    @Override
    protected void initDefaultView() {
        ViewStub stub = (ViewStub) findViewById(R.id.viewstub_search_title);
        stub.inflate();
        mRootViewCommon = (ViewGroup) findViewById(R.id.search_title_bar);
        mSearchBox = (SearchBox) findViewById(/* yi. */R.id.search_text);
        mBackButton = (ImageView) findViewById(R.id.left_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onBackButtonClicked();
                }
            }
        });

        tvRightSearch = findViewById(R.id.tv_right_search);
        tvRightSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onRightButtonClicked(v);
                }
            }
        });

//         mSearchBox.setBackgroundResource(R.drawable.title_bar_background_netdisk);
    }

    @Override
    public ViewGroup getRootView() {
        return mRootViewCommon;
    }


    public SearchBox getSearchBox() {
        return mSearchBox;
    }

    public void setDarkMode() {
        setBackgroundColor(R.color.black);
        View divider = findViewById(R.id.divider);
        divider.setBackgroundColor(mActivity.get().getResources().getColor(R.color.color_0fffffff));
        LinearLayout llSearchContainer = (LinearLayout) findViewById(R.id.ll_search_container);
        llSearchContainer.setBackgroundResource(R.drawable.bg_dn_search_background_round_rect_dark);
        mBackButton.setColorFilter(Color.WHITE);
        mSearchBox.setTextColor(Color.WHITE);
        mEditModeLayout.setEditTextColorResource(R.color.color_FFFFFF);
        mEditModeLayout.setBackgroundResource(R.color.color_09162e);
    }

    /**
     * 设置搜索按钮是否可以点击
     * @param clickable
     */
    public void setTvRightSearchClickable(Boolean clickable) {
        tvRightSearch.setClickable(clickable);
    }

}
