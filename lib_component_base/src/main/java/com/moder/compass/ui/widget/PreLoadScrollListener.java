package com.moder.compass.ui.widget;

import android.view.View;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by guanshuaichao on 2019/1/15.
 */
@SuppressWarnings("WeakerAccess")
public class PreLoadScrollListener extends ScrollListenerWrapper {

    // 使用位运算缓存图片预加载状态
    private BitSet mLoadedSet = new BitSet();

    // 当前滚动方向是否向下滚动
    private boolean mIsScrollDown = true;

    // 上次顶部可见item位置 判断滚动方向使用
    private int mLastTopIndex;

    // 上次顶部像素 判断滚动方向使用
    private int mLastTopPixel;

    private OnPreLoadCallback mOnPreLoadCallback;

    private boolean mFirstScroll = true;

    private AbsListView mListView;

    public PreLoadScrollListener(AbsListView listView) {
        mListView = listView;
    }

    public void setOnPreLoadCallback(OnPreLoadCallback onPreLoadCallback) {
        mOnPreLoadCallback = onPreLoadCallback;
        mOnPreLoadCallback.onPreLoad(getPreLoadIndexList());
    }

    public void reset() {
        // 清除预加载状态
        mLoadedSet.clear();
        mFirstScroll = true;
    }

    @Override
    void proxyOnScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && mOnPreLoadCallback != null) {
            // 停止滚动时，开始预加载
            mOnPreLoadCallback.onPreLoad(getPreLoadIndexList());
        }
    }

    @Override
    void proxyOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnPreLoadCallback == null) {
            return;
        }

        View topView = view.getChildAt(0);
        int top = topView == null ? 0 : topView.getTop();
        if (firstVisibleItem > mLastTopIndex) {
            mIsScrollDown = false;
        } else if (firstVisibleItem < mLastTopIndex) {
            mIsScrollDown = true;
        } else {
            // item 位置未变化 根据像素判断方向
            mIsScrollDown = top >= mLastTopPixel;
        }

        mLastTopIndex = firstVisibleItem;
        mLastTopPixel = top;

        if (mFirstScroll && visibleItemCount > 0) {
            mFirstScroll = false;
            mOnPreLoadCallback.onPreLoad(getPreLoadIndexList());
        }
    }

    private boolean isScrollDown() {
        return mIsScrollDown;
    }

    private List<Integer> getPreLoadIndexList() {
        OnPreLoadCallback onPreLoadCallback = mOnPreLoadCallback;
        if (onPreLoadCallback == null) {
            return null;
        }

        List<Integer> list = new ArrayList<>();
        if (isScrollDown()) {
            // 向下滚动
            int pos = mListView.getLastVisiblePosition() + 1;
            for (int i = pos; i < pos + onPreLoadCallback.getPreLoadSize() && i < mListView.getCount(); i++) {
                if (!mLoadedSet.get(i)) {
                    list.add(i);
                    mLoadedSet.set(i);
                }
            }
        } else {
            int pos = mListView.getFirstVisiblePosition() - 1;
            for (int i = pos; i > 0 && i > pos - onPreLoadCallback.getPreLoadSize(); i--) {
                if (!mLoadedSet.get(i)) {
                    list.add(i);
                    mLoadedSet.set(i);
                }
            }
        }

        return list;
    }

    public interface OnPreLoadCallback {
        void onPreLoad(List<Integer> indexList);

        int getPreLoadSize();
    }
}
