package com.moder.compass.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;

import com.airbnb.lottie.LottieComposition;
import com.moder.compass.BaseApplication;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.util.ToastHelper;
import com.moder.compass.ui.lottie.DynamicHostLottieView;
import com.moder.compass.ui.widget.tooltip.LottieUtil;
import com.moder.compass.ui.widget.tooltip.LottieUtil;
import com.moder.compass.util.IRefreshable;

import java.math.BigDecimal;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * 带可下拉刷新功能的ListView
 *
 * @author yangqinghai Create at 2013-5-22 下午2:06:47
 */
public class PullWidgetListView extends ListViewEx implements IRefreshable, NestedScrollingChild {

    private NestedScrollingChildHelper mScrollingChildHelper;

    private static final String TAG = "PullWidgetListView";
    /** 向下拉动，已经全展示全展示 **/
    private final static int RELEASE_To_REFRESH = 0;
    /** 向下拉动，0-全展示过程 **/
    private final static int PULL_To_REFRESH = 1;
    /** 正在刷新 **/
    private final static int REFRESHING = 2;
    /** 下拉刷新完成 **/
    private final static int DONE = 3;
    /** 实际的padding的距离与界面上偏移距离的比例 **/
    private final static int RATIO = 2;
    /**
     * FastScroll 自动隐藏的时间
     */
    private static final Long FADE_TIMEOUT = 900L;

    /** 用于保证startY的值在一个完整的touch事件中只被记录一次 **/
    private boolean mIsRecored;
    /** 列表头高度 **/
    private int mHeadContentHeight;
    /** 下拉刷新点击屏幕首位置 **/
    private int mStartY;
    /** 当前下拉刷新状态 **/
    private int mState;
    /** 到达释放刷新又回到下拉刷新标示 **/
    private boolean mIsBack;
    /** 刷新、排序和搜索监听器 **/
    private IOnPullDownListener mDownListener;

    /** 是否可下拉 **/
    private boolean mPullDownEnable;

    /**
     * 上拉刷新触发
     */
    private boolean mIsPullUpReady;

    /**
     * 是否可以上拉
     */
    private boolean mIsPullUpEnable;

    /**
     * 拉动的监听，分成上拉下拉
     */
    private IPullListener pullListener;

    /**
     * 是否到达列表底部
     */
    private boolean mIsReachBottom;

    private int mScrollState;

    private PullDownFooterView mFooterView;

    private boolean mFooterAdded = false;

    /**
     * 9.0版本引入lottie加载刷新动画
     */
    private DynamicHostLottieView mLottieAnimationView;

    protected View mHeadView;
    private TextView mHeaderTextView;

    private LinearLayout mLayoutlottie;

    private ValueAnimator mScrollAnimator;

    // ListView 设置了 FastScroller 点击屏幕右侧时会被拦截点击事件，设置这个根据滑动状态自动判断是否关闭 FastScroller
    private boolean automaticCloseFastScroll = false;

    private Function1<Boolean,Unit> mPullDownCallback;

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int paddingTop = (Integer) animation.getAnimatedValue();
            switch (mState) {
                case REFRESHING:
                case DONE: {
                    mHeadView.setPadding(0, paddingTop, 0, 0);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    };

    private WrapperListener mWrapperListener;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mWrapperListener.reset();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mWrapperListener.reset();
        }
    };

    public PullWidgetListView(Context context) {
        super(context);
        init(context);
    }

    public PullWidgetListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public boolean canRefresh() {
        return mPullDownEnable && isEnabled();
    }

    @Override
    public boolean isRefreshing() {
        if (mState == REFRESHING) {
            return true;
        } else {
            return false;
        }
    }

    public void registerPullDownCallBack(Function1<Boolean,Unit> pullDownCallback) {
        mPullDownCallback = pullDownCallback;
    }

    @Override
    public boolean triggerRefresh() {
        if (!isRefreshing()) {
            if (getCount() > 0) {
                setSelection(0);
            }
            showHeaderRefreshing();
            onRefresh();
        }
        return true;
    }

    /**
     * 初始化下拉刷新控件
     *
     * @param context ：上下文
     * @author yangqinghai Create at 2013-5-23 下午5:01:26
     */
    protected void init(Context context) {
        mScrollingChildHelper = new NestedScrollingChildHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setNestedScrollingEnabled(true);
        }

        LayoutInflater mInflater = LayoutInflater.from(context);

        mHeadView = mInflater.inflate(R.layout.loading_lottie, null);
        mHeaderTextView = mHeadView.findViewById(R.id.refresh_tip);
        mLayoutlottie = (LinearLayout) mHeadView.findViewById(R.id.layout_lottie);
        mLottieAnimationView = mHeadView.findViewById(R.id.loading_lottie);
        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
        mHeadView.invalidate();
        initHeaderView();
        addHeaderView(mHeadView, null, false);
        mWrapperListener = new WrapperListener(this);
        super.setOnScrollListener(mWrapperListener);
        mState = DONE;
        mPullDownEnable = false;
    }

    /**
     * 重写该方法，自定义headerview
     */
    protected void initHeaderView() {

    }
    /**
     * 解决快速滑动过程中切换Activity导致ScrollState有问题
     */
    public void resetScrollState() {
        if (mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            onWindowFocusChanged(false);
        }
    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     * @author yangqinghai Create at 2013-5-23 下午5:03:00
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPullDownEnable || mIsPullUpEnable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // DuboxLog.v(TAG, "在down时候记录当前位置‘");
                    mStartY = (int) event.getY();
                    startYRecored(event);
                    break;

                case MotionEvent.ACTION_UP:
                    handleActionUp();
                    if (mPullDownCallback != null){
                        mPullDownCallback.invoke(true);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    float height = mLayoutlottie.getY();
                    float a = (height + mHeadContentHeight) / (mHeadContentHeight * 2);
                    BigDecimal b = new BigDecimal(String.valueOf(a));
                    DuboxLog.v(TAG, "在move时候记录下位置" + b.floatValue());
                    startYRecored(event);

                    handleActionMove(tempY);

                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    public void showHeaderRefreshing() {
        if (mState == REFRESHING) {
            return;
        }
        mState = REFRESHING;
        changeHeaderViewByState();
    }

    /**
     * 设置listview的下来等事件的回调
     *
     * @param listener
     */
    public void setOnPullListener(IPullListener listener) {
        this.pullListener = listener;
        mPullDownEnable = true;
        mIsPullUpEnable = true;
        changeLottieView();
    }

    public void setHeaderText(String tip){
        mHeaderTextView.setText(tip);
        mHeaderTextView.setVisibility(View.VISIBLE);
        mHeadView.setPadding(0, 0, 0, 0);
        measureView(mHeadView);
        mHeadContentHeight = mHeadView.getMeasuredHeight();
        mHeadView.setPadding(0, -1 * mHeadContentHeight, 0, 0);
    }

    private void handleActionMove(int tempY) {
        int paddingTop = (tempY - mStartY) / RATIO;
        if (paddingTop >= 10){
            if (mPullDownCallback != null) {
                mPullDownCallback.invoke(false);
            }
        }
        if (mState != REFRESHING && mIsRecored && mPullDownEnable) {

            // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
            // 可以松手去刷新了
            if (mState == RELEASE_To_REFRESH) {
                setSelection(0);
                // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                if (((tempY - mStartY) / RATIO < mHeadContentHeight) && (tempY - mStartY) > 0) {
//                    setCircleProgress(paddingTop * 100 / mHeadContentHeight);
                    mState = PULL_To_REFRESH;
                    changeHeaderViewByState();
                    // DuboxLog.v(TAG, "由松开刷新状态转变到下拉刷新状态");
                }
                // 一下子推到顶了
                else if (tempY - mStartY <= 0) {
                    mState = DONE;
                    changeHeaderViewByState();
                    // DuboxLog.v(TAG, "由松开刷新状态转变到done状态");
                }
            }
            // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
            if (mState == PULL_To_REFRESH) {
                setSelection(0);
                // 下拉到可以进入RELEASE_TO_REFRESH的状态
//                setCircleProgress(paddingTop * 100 / mHeadContentHeight);
                if ((tempY - mStartY) / RATIO >= mHeadContentHeight) {
                    mState = RELEASE_To_REFRESH;
                    mIsBack = true;
                    changeHeaderViewByState();
                    // DuboxLog.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
                }
                // 上推到顶了
                else if (tempY - mStartY <= 0) {
                    mState = DONE;
                    changeHeaderViewByState();

                    // DuboxLog.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
                }
            }

            // done状态下
            if (mState == DONE) {
                if (tempY - mStartY > 0) {
                    // 下拉时去掉触发刷新的前一段无效拉动
                    // if (tempY - mStartY > mHeadContentHeight) {
                    mState = PULL_To_REFRESH;
                    // }
                    changeHeaderViewByState();
                }
            }
            // 更新headView的size
            if (mState == PULL_To_REFRESH) {
                mHeadView.setPadding(0, -1 * mHeadContentHeight + (tempY - mStartY) / RATIO, 0, 0);
            }

            // 更新headView的paddingTop
            if (mState == RELEASE_To_REFRESH) {
                mHeadView.setPadding(0, (tempY - mStartY) / RATIO - mHeadContentHeight, 0, 0);
            }
        }
        if (paddingTop < 0 && mIsReachBottom) {
            mIsPullUpReady = true;
        } else {
            mIsPullUpReady = false;
        }
    }

    /**
     * 处理触摸抬起事件
     *
     * @author yangqinghai Create at 2013-5-28 上午10:54:03
     */
    private void handleActionUp() {
        if (mState != REFRESHING && mPullDownEnable) {
            if (mState == DONE) {
                // 什么都不做
            }
            if (mState == PULL_To_REFRESH) {
                mState = DONE;
                changeHeaderViewByState();

                // DuboxLog.v(TAG, "由下拉刷新状态，到done状态");
            }
            if (mState == RELEASE_To_REFRESH) {
                mState = REFRESHING;
                changeHeaderViewByState();
                onRefresh();
                // DuboxLog.v(TAG, "由松开刷新状态，到done状态");
            }
        }
        // 只有listview满足条件并且用户设置需要上拉操作时才可以上拉回调
        if (mIsPullUpReady && mIsPullUpEnable) {
            onFooterRefreshMoreClicked();
        }

        mIsRecored = false;
        mIsBack = false;
    }

    /**
     * 点击更多进行刷新
     */
    private void onFooterRefreshMoreClicked() {
        // 没有网络连接时给出toast提示
        if (!ConnectivityState.isConnected(BaseApplication.getInstance())) {
            ToastHelper.showToast(R.string.network_exception_message);
            return;
        }
        if (pullListener != null) {
            pullListener.onPullUp();
        }
    }

    /**
     * 记录Y方向触摸位置
     *
     * @param event
     * @author yangqinghai Create at 2013-5-23 下午5:03:36
     */
    private void startYRecored(MotionEvent event) {
        if (!mPullDownEnable) {
            return;
        }
        if (getFirstVisiblePosition() == 0 && !mIsRecored) {
            mIsRecored = true;
            mStartY = (int) event.getY();
        }
    }

    /**
     * 当状态改变时候，调用该方法，以更新界面
     *
     * @author yangqinghai Create at 2013-5-23 下午5:04:30
     */
    private void changeHeaderViewByState() {
        switch (mState) {
            case RELEASE_To_REFRESH:
                break;
            case PULL_To_REFRESH:
                if (mIsBack) {
                    mIsBack = false;
                }
                break;

            case REFRESHING:
                animateToRefreshing();
                // DuboxLog.v(TAG, "当前状态,正在刷新...");
                break;
            case DONE:
                animateToDone();
//                // DuboxLog.v(TAG, "当前状态，done");
                break;
        }
    }

    private void animateToRefreshing() {
        startScrollAnimation(400, new AccelerateInterpolator(),
                mHeadView.getPaddingTop(), 0);
    }

    private void animateToDone() {
        startScrollAnimation(300, new DecelerateInterpolator(),
                mHeadView.getPaddingTop(), -1 * mHeadContentHeight);
    }

    private void startScrollAnimation(final int time, final Interpolator interpolator, int value, int toValue) {
        if (mScrollAnimator == null) {
            mScrollAnimator = new ValueAnimator();
        }
        // cancel
        mScrollAnimator.removeAllUpdateListeners();
        mScrollAnimator.removeAllListeners();
        mScrollAnimator.cancel();

        // reset new value
        mScrollAnimator.setIntValues(value, toValue);
        mScrollAnimator.setDuration(time);
        mScrollAnimator.setInterpolator(interpolator);
        mScrollAnimator.addUpdateListener(mAnimatorUpdateListener);
        mScrollAnimator.start();
    }

    /**
     * 设置监听
     *
     * @param refreshListener
     * @author yangqinghai Create at 2013-5-23 下午5:04:45
     */
    public void setOnRefreshListener(IOnPullDownListener refreshListener) {
        this.mDownListener = refreshListener;
        mPullDownEnable = true;
        changeLottieView();
    }

    /**
     * 下拉刷新、排序和搜索接口
     *
     * com.dubox.drive.ui.widget.OnRefreshListener
     *
     * @author yangqinghai <br/>
     *         create at 2013-5-23 下午5:05:14
     */
    public interface IOnPullDownListener {
        public void onRefresh();
    }

    /***
     * 刷新完成处理
     *
     * @author yangqinghai Create at 2013-5-23 下午5:05:37
     */
    public void onRefreshComplete(boolean saveTime) {
        // 防止调用的时候非下拉刷新进行处理 YQH 20130606
        if (mState != REFRESHING) {
            DuboxLog.i(TAG, "onRefreshComplete::" + "mState != REFRESHING");
            return;
        }
        mState = DONE;
        changeHeaderViewByState();
    }

    /**
     * 刷新函数
     *
     * @author yangqinghai Create at 2013-5-23 下午5:05:52
     */
    private void onRefresh() {
        if (pullListener != null) {
            pullListener.onPullDown();
        }
        if (mDownListener != null) {
            mDownListener.onRefresh();
        }
        mLottieAnimationView.setImageAssetsFolder("images");
        LottieUtil.INSTANCE.fetchRemote(
                getContext().getString(R.string.lottie_loading_bg),
                new Function1<LottieComposition, Unit>() {
                    @Override
                    public Unit invoke(LottieComposition composition) {
                        mLottieAnimationView.setComposition(composition);
                        mLottieAnimationView.setSafeMode(true);
                        mLottieAnimationView.playAnimation();
                        return null;
                    }
                }
        );
    }

    /**
     * view宽高设置
     *
     * @param child
     * @author yangqinghai Create at 2013-5-23 下午5:06:55
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
        ListAdapter oldAdapter = getAdapter();
        registerDataChanged(oldAdapter, adapter);
        super.setAdapter(adapter);
    }

    private void registerDataChanged(ListAdapter oldAdapter, ListAdapter adapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        adapter.registerDataSetObserver(mDataSetObserver);
    }

    /**
     * 设置下拉刷新是否可用
     *
     * @param mIsRefreshable
     */
    public void setIsRefreshable(boolean mIsRefreshable) {
        this.mPullDownEnable = mIsRefreshable;
        changeLottieView();
    }

    /**
     * 拉动的listener com.dubox.drive.ui.widget.IPullListener
     *
     * @author chenyuquan <br/>
     *         create at 2013年7月31日 下午6:53:07
     */
    public interface IPullListener {
        /**
         * 下拉事件的回调
         */
        public void onPullDown();

        /**
         * 上拉事件回调
         */
        public void onPullUp();
    }

    @Override
    public void setItemChecked(int position, boolean value) {
        super.setItemChecked(position, value);
    }

    @Override
    public void setSelectionFromTop(int position, int y) {
        super.setSelectionFromTop(position, y);
    }

    /**
     * 是否显示加载更多的FooterView
     */
    public void showLoadMoreFooter() {
        mFooterView = (PullDownFooterView) LayoutInflater.from(getContext()).inflate(R.layout.feedlist_footer, null);
        mFooterView.setOnFooterViewClickListener(new PullDownFooterView.OnFooterViewClickListener() {

            @Override
            public void onRefreshClicked() {
                if (pullListener != null) {
                    pullListener.onPullUp();
                }
            }

            @Override
            public void onAddFollowClicked() {
            }
        });
    }

    /**
     * 显示正在加载更多
     */
    public void setLoadingMore() {
        if (!mFooterAdded) {
            mFooterAdded = true;
            addFooterView(mFooterView);
        }
        mFooterView.showFooterRefreshing();
    }

    /**
     * 显示加载更多完成
     *
     * @param hasMore
     */
    public void setLoadMoreFinished(boolean hasMore) {
        if (!mFooterAdded) {
            mFooterAdded = true;
            addFooterView(mFooterView);
        }
        if (hasMore) {
            mFooterView.showFooterRefreshMore();
        } else {
            mFooterAdded = false;
            removeFooterView(mFooterView);
        }
    }

    /**
     * 是否可以加载更多
     *
     * @return
     */
    public boolean canLoadMore() {
        return mFooterAdded && !mFooterView.isRefreshing();
    }

    public void setOnPreLoadCallback(PreLoadScrollListener.OnPreLoadCallback onPreLoadCallback) {
        mWrapperListener.setOnPreLoadCallback(onPreLoadCallback);
    }

    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mWrapperListener.setOnScrollListener(l);
        super.setOnScrollListener(mWrapperListener);
    }

    private Runnable closeFastScroll = new Runnable() {
        @Override
        public void run() {
            setFastScrollEnabled(false);
        }
    };

    private void operateCloseFastScroll(boolean isOpen) {
        if (isOpen) {
            removeCallbacks(closeFastScroll);
            setFastScrollEnabled(true);
        } else {
            postDelayed(closeFastScroll, FADE_TIMEOUT);
        }
    }

    /**
     * 当数据失效时，需要清除缓存状态
     * ListView内监听了Adapter数据变化，此方法一般可不设置
     */
    @SuppressWarnings("unused")
    public void resetPreLoadState() {
        mWrapperListener.reset();
    }

    private class WrapperListener extends PreLoadScrollListener {

        public WrapperListener(AbsListView listView) {
            super(listView);
        }

        @Override
        void proxyOnScrollStateChanged(AbsListView view, int scrollState) {
            super.proxyOnScrollStateChanged(view, scrollState);
            mScrollState = scrollState;
            if (automaticCloseFastScroll) {
                boolean needOpen = scrollState != SCROLL_STATE_IDLE;
                operateCloseFastScroll(needOpen);
            }
        }

        @Override
        void proxyOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            super.proxyOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

            // 列表滚动对mFirstItemIndex赋值
            if (visibleItemCount + firstVisibleItem == totalItemCount) {
                mIsReachBottom = true;
                // onFooterRefreshMoreClicked();
            } else if (visibleItemCount + firstVisibleItem < totalItemCount) {
                mIsReachBottom = false;
            }
        }
    }
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    public void removeRefreshView() {
        if (mHeadView != null) {
            removeHeaderView(mHeadView);
        }
    }

    public void changeLottieView() {
        if (!mPullDownEnable) {
            mLottieAnimationView.setVisibility(GONE);
        } else {
            mLottieAnimationView.setVisibility(VISIBLE);
        }
    }

    /**
     * 设置是否开启自动关闭 FastScroll
     * @param automaticClose true 关闭，false 不处理
     */
    public void setAutomaticCloseFastScroll(boolean automaticClose) {
        this.automaticCloseFastScroll = automaticClose;
    }
}
