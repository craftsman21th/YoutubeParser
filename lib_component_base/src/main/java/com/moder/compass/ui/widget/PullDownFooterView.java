package com.moder.compass.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moder.compass.component.base.R;

public class PullDownFooterView extends RelativeLayout implements OnClickListener {
    private static final String TAG = "PullDownHeaderView";

    private LinearLayout mFooterRefreshAddMoreLayout;

    private LinearLayout mFooterAddFollowLayout;

    private LinearLayout mFooterRefreshingLayout;

    private Button mBtnAddFollow;

    private OnFooterViewClickListener mListener;

    private boolean mIsRefreshing;

    /**
     * 底部正在刷新的菊花
     */
    private RotateImageView mFooterRefreshingRotateView;

    public PullDownFooterView(Context context) {
        super(context);
    }

    public PullDownFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRefresh(boolean isRefreshing) {
        this.mIsRefreshing = isRefreshing;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFooterRefreshingLayout = (LinearLayout) findViewById(R.id.layout_bottom_bar_refreshing);
        mFooterAddFollowLayout = (LinearLayout) findViewById(R.id.layout_add_follow);
        mFooterRefreshAddMoreLayout = (LinearLayout) findViewById(R.id.layout_refresh_add_more);
        mBtnAddFollow = (Button) findViewById(R.id.btn_add_follow);
        mFooterRefreshingRotateView = (RotateImageView) findViewById(R.id.footer_rotate_image_refreshing);
        if (mBtnAddFollow != null) {
            mBtnAddFollow.setOnClickListener(this);
        }
        mFooterRefreshAddMoreLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_follow) {
            mListener.onAddFollowClicked();
        } else if (v.getId() == R.id.layout_refresh_add_more) {
            mListener.onRefreshClicked();
        }
//        switch (v.getId()) {
//            case R.id.btn_add_follow:
//                mListener.onAddFollowClicked();
//                break;
//            case R.id.layout_refresh_add_more:
//                mListener.onRefreshClicked();
//                break;
//
//            default:
//                break;
//        }
    }

    /**
     * 显示底部"更多"layout
     */
    public void showFooterRefreshMore() {
        mIsRefreshing = false;
        mFooterRefreshingLayout.setVisibility(View.GONE);
        mFooterRefreshAddMoreLayout.setVisibility(View.VISIBLE);
        mFooterAddFollowLayout.setVisibility(View.GONE);
    }

    /**
     * 显示底部添加关注的layout
     */
    public void showFooterAddFollow() {
        mFooterRefreshingLayout.setVisibility(View.GONE);
        mFooterRefreshAddMoreLayout.setVisibility(View.GONE);
        mFooterAddFollowLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 显示底部正在刷新的layout
     */
    public void showFooterRefreshing() {
        setRefresh(true);
        mFooterRefreshingLayout.setVisibility(View.VISIBLE);
        mFooterRefreshingRotateView.startRotate();
        mFooterRefreshAddMoreLayout.setVisibility(View.GONE);
        mFooterAddFollowLayout.setVisibility(View.GONE);
    }

    public void setOnFooterViewClickListener(OnFooterViewClickListener listenter) {
        this.mListener = listenter;
    }

    public interface OnFooterViewClickListener {
        void onAddFollowClicked();

        void onRefreshClicked();
    }

}
