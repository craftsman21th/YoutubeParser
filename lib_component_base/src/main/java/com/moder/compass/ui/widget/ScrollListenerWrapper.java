package com.moder.compass.ui.widget;

import android.widget.AbsListView;

/**
 * Created by guanshuaichao on 2019/1/15.
 */
public abstract class ScrollListenerWrapper implements AbsListView.OnScrollListener {

    private AbsListView.OnScrollListener mOnScrollListener;

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    abstract void proxyOnScrollStateChanged(AbsListView view, int scrollState);

    abstract void proxyOnScroll(AbsListView view, int firstVisibleItem,
                                int visibleItemCount, int totalItemCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        proxyOnScrollStateChanged(view, scrollState);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        proxyOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
