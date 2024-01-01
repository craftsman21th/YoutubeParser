package com.moder.compass.ui.home.swiperefresh;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.moder.compass.base.utils.SizeUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.sns.ui.magicindicator.buildins.UIUtil;
import com.moder.compass.statistics.StatisticsLogForMutilFields;

/**
 * Created by lbf on 2016/7/24.
 */
public class CustomSwipeRefreshLayout extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {
    public static final String TAG = "CustomSwipeRefreshLayout";

    private static final String LOG_TAG = SwipeRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;

    private static final int CIRCLE_DIAMETER = 23;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = 0.5f;

    private static final int SCALE_DOWN_DURATION = 200;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    public static final int DEFAULT_CIRCLE_TARGET = 46;

    /**
     * 动画时长
     */
    public static final int OFFSET_TIME_BY_BRANCH = 300;

    /**
     * 下拉刷新有广告牌后偏移量
     */
    private int mBrandHegigt = 0;

    /**
     * 下拉刷新有广告牌后动画时长的增量
     */
    private int mBrandTime = 0;

    /**
     * 广告牌高度
     */
    public static final int BRAND_IMAGE_HEIGTH = 142;

    private View mTarget; // the target of the gesture
    private OnRefreshListener mListener;
    private boolean mRefreshing = false;
    protected int mTouchSlop;
    private float mTotalDragDistance = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    private int mMediumAnimationDuration;
    private int mCurrentTargetOffsetTop;
    // Whether or not the starting offset has been determined.
    private boolean mOriginalOffsetCalculated = false;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = INVALID_POINTER;
    // Whether this item is scaled up rather than clipped
    private boolean mScale = true;

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private boolean mReturningToStart;
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };

    private CircleImageWithTextView mCircleView;
    private int mCircleViewIndex = -1;

    protected int mFrom;

    private float mStartingScale;

    protected int mOriginalOffsetTop;

    private Animation mScaleAnimation;

    private Animation mScaleDownAnimation;

    private Animation mScaleDownToStartAnimation;

    private float mSpinnerFinalOffset;

    private boolean mNotify;

    // Whether the client has set a custom starting position;
    private boolean mUsingCustomStart = false;

    private int margintop = 10;

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mCircleView.start();
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
                mCurrentTargetOffsetTop = mCircleView.getTop();
            } else {
                reset();
            }
        }
    };

    private void reset() {
        mCircleView.clearAnimation();
        mCircleView.stop();
        mCircleView.setVisibility(View.GONE);

        setColorViewAlpha(MAX_ALPHA);
        // Return the circle to its start position
//        if (mScale) {
//            setAnimationProgress(0 /* animation complete and view is hidden */);
//        } else {
//            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop,
//                    true /* requires update */);
//        }
        mCurrentTargetOffsetTop = mCircleView.getTop();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    private void setColorViewAlpha(int targetAlpha) {
        // mCircleView.getBackground().setAlpha(targetAlpha);
        mCircleView.setAlpha(targetAlpha);
    }

    /**
     * Simple constructor to use when creating a SwipeRefreshLayout from code.
     *
     * @param context
     */
    public CustomSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor that is called when inflating SwipeRefreshLayout from XML.
     *
     * @param context
     * @param attrs
     */
    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        mMediumAnimationDuration = 300;
        // 重写ondraw
        setWillNotDraw(false);
        // 在动画开始的地方快然后慢
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerFinalOffset = UIUtil.dip2px(getContext(), DEFAULT_CIRCLE_TARGET);
        mTotalDragDistance = mSpinnerFinalOffset;
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    protected int getChildDrawingOrder(int childCount, int i) {
        if (mCircleViewIndex < 0) {
            return i;
        } else if (i == childCount - 1) {
            // Draw the selected child last
            return mCircleViewIndex;
        } else if (i >= mCircleViewIndex) {
            // Move the children after the selected child earlier one
            return i + 1;
        } else {
            // Keep the children before the selected child the same
            return i;
        }
    }


    private void createProgressView() {
        mCircleView = new CircleImageWithTextView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER / 2);
        mCircleView.setVisibility(View.GONE);
        addView(mCircleView);
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the swipe
     * gesture.
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * Pre API 11, alpha is used to make the progress circle appear instead of scale.
     */
    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = (int) (mSpinnerFinalOffset +
                        UIUtil.dip2px(getContext(), mBrandHegigt) + mOriginalOffsetTop);
            } else {
                endTarget = (int) mSpinnerFinalOffset +
                        UIUtil.dip2px(getContext(), mBrandHegigt);
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop,
                    true /* requires update */);
            mNotify = false;
            startScaleUpAnimation(mRefreshListener);

            mTarget.animate().translationY(getCircleViewHeight())
                    .setDuration(200 + mBrandTime).start();
        } else {
            setRefreshing(refreshing, false /* notify */);
//            // 首页上滑时间
//            mTarget.animate().translationY(0).setDuration(200 + mBrandTime).start();
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition(margintop, mRefreshListener);
            } else {
                startScaleDownAnimation(mRefreshListener);
            }
        }
    }

    private int getCircleViewHeight() {
        int height = SizeUtils.measureView(mCircleView)[1];
        if (height <= 0) {
            height = DEFAULT_CIRCLE_TARGET + 18;
        }
        return height;
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        mCircleView.setVisibility(View.VISIBLE);
        mScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        mScaleAnimation.setDuration(mMediumAnimationDuration + mBrandTime);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleAnimation);
    }

    /**
     * Pre API 11, this does an alpha animation.
     *
     * @param progress
     */
    private void setAnimationProgress(float progress) {
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(mCircleView, progress);
            ViewCompat.setScaleY(mCircleView, progress);
        }
    }



    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        //            mScaleDownAnimation = new Animation() {
        //                @Override
        //                public void applyTransformation(float interpolatedTime, Transformation t) {
        //                    setAnimationProgress(1 - interpolatedTime);
        //                }
        //            };

        //            最终的偏移量就是mCircleView距离顶部的高度
//        final int deltaY = -mCircleView.getBottom();
//        mScaleDownAnimation = new TranslateAnimation(0, 0, 0, deltaY);
        //            mScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        mScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownAnimation.setDuration(200 + mBrandTime);
        mCircleView.setAnimationListener(listener);
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownAnimation);
        mTarget.animate().translationY(0).setDuration(200 + mBrandTime).start();
    }

    /**
     * @deprecated Use {@link #setProgressBackgroundColorSchemeResource(int)}
     */
    @Deprecated
    public void setProgressBackgroundColor(int colorRes) {
        setProgressBackgroundColorSchemeResource(colorRes);
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param colorRes Resource id of the color.
     */
    public void setProgressBackgroundColorSchemeResource(@ColorRes int colorRes) {
        setProgressBackgroundColorSchemeColor(getResources().getColor(colorRes));
    }

    /**
     * Set the background color of the progress spinner disc.
     *
     * @param color
     */
    public void setProgressBackgroundColorSchemeColor(@ColorInt int color) {
        mCircleView.setBackgroundColor(color);
    }

    /**
     * @deprecated Use {@link #setColorSchemeResources(int...)}
     */
    @Deprecated
    public void setColorScheme(@ColorInt int... colors) {
        setColorSchemeResources(colors);
    }

    /**
     * Set the color resources used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colorResIds
     */
    public void setColorSchemeResources(@ColorRes int... colorResIds) {
        final Resources res = getResources();
        int[] colorRes = new int[colorResIds.length];
        for (int i = 0; i < colorResIds.length; i++) {
            colorRes[i] = res.getColor(colorResIds[i]);
        }
        setColorSchemeColors(colorRes);
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors
     */
    @ColorInt
    public void setColorSchemeColors(int... colors) {
        ensureTarget();
    }

    /**
     * @return Whether the SwipeRefreshWidget is actively showing refresh
     * progress.
     */
    public boolean isRefreshing() {
        return mRefreshing;
    }

    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mCircleView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        //            final int childTop = (int) (getPaddingTop()+child.getTranslationY());
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        int circleWidth = mCircleView.getMeasuredWidth();
        int circleHeight = mCircleView.getMeasuredHeight();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        mCircleView.layout((width / 2 - circleWidth / 2), mCurrentTargetOffsetTop,
                (width / 2 + circleWidth / 2), mCurrentTargetOffsetTop + circleHeight);
        //            修改进度圈的X坐标使之位于左边
        //            mCircleView.layout(childLeft, mCurrentTargetOffsetTop,
        //                    childLeft+circleWidth, mCurrentTargetOffsetTop + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
                        MeasureSpec.EXACTLY));
        mCircleView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        if (!mUsingCustomStart && !mOriginalOffsetCalculated) {
            mOriginalOffsetCalculated = true;
            mCurrentTargetOffsetTop = mOriginalOffsetTop = -mCircleView.getMeasuredHeight();
        }
        mCircleViewIndex = -1;
        // Get the index of the circleview.
        for (int index = 0; index < getChildCount(); index++) {
            if (getChildAt(index) == mCircleView) {
                mCircleViewIndex = index;
                break;
            }
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
                        || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mTarget, -1) || mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        final int action = MotionEventCompat.getActionMasked(ev);

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }
                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mCircleView.start();
                }
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            default:
                break;
        }

        return mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
            DuboxLog.d(TAG, "requestDisallowInterceptTouchEvent()");
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    // NestedScrollingParent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && canChildScrollUp() && !mReturningToStart && !mRefreshing
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
        mNestedScrollInProgress = true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;

            }
            moveSpinner(mTotalUnconsumed);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            mCircleView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScrollInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0) {
            mTotalUnconsumed += Math.abs(dy);
            moveSpinner(mTotalUnconsumed);
        }
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    private void moveSpinner(float overscrollTop) {
        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.HOMEPAGE_YIKE_BRANCH_SHOW);
        }
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }

//        if (mScale) {
//            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
//        }
//        if (overscrollTop < mTotalDragDistance) {
//            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
//                    && !isAnimationRunning(mAlphaStartAnimation)) {
//                // Animate the alpha
//                startProgressAlphaStartAnimation();
//            }
//        } else {
//            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
//                // Animate the alpha
//                startProgressAlphaMaxAnimation();
//            }
//        }
        //            setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update */);
        //            最终刷新的位置
        setTargetOffsetTopAndBottom((int) (overscrollTop - mSpinnerFinalOffset
                        - UIUtil.dip2px(getContext(), mBrandHegigt) + margintop),
                true);

    }

    private void finishSpinner(float overscrollTop) {
        if (overscrollTop > (mTotalDragDistance + UIUtil.dip2px(getContext(), mBrandHegigt))) {
            setRefreshing(true, true /* notify */);
        } else {
            // cancel refresh
            mRefreshing = false;
            Animation.AnimationListener listener = null;
            if (!mScale) {
                listener = new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                };
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp() || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;

                break;

            case MotionEvent.ACTION_MOVE: {

                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);

                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop;
                    mIsBeingDragged = true;
                    mCircleView.start();
                }
                //                    记录手指移动的距离,mInitialMotionY是初始的位置，DRAG_RATE是拖拽因子。
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                //                    赋值给mTarget的top使之产生拖动效果

                mTarget.setTranslationY(overscrollTop);

                if (mIsBeingDragged) {
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                pointerIndex = MotionEventCompat.getActionIndex(ev);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                //                    手指松开时启动动画回到头部
                // mTarget.animate().translationY(0).setDuration(200).start();

                // mTarget.animate().translationY(200).setDuration(200).start();

                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;

                mIsBeingDragged = false;
                finishSpinner(overscrollTop);
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
            default:
                return true;
        }

        return true;
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        mFrom = from;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION + mBrandTime);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            mAnimateToStartPosition.reset();
            mAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
            if (listener != null) {
                mCircleView.setAnimationListener(listener);
            }
            mCircleView.clearAnimation();
            mCircleView.startAnimation(mAnimateToStartPosition);
        }
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = 0;
            if (!mUsingCustomStart) {
                endTarget = margintop;
            } else {
                endTarget = (int) mSpinnerFinalOffset + UIUtil.dip2px(getContext(), mBrandHegigt);
            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));

            mTarget.setTranslationY(getCircleViewHeight());
            setTargetOffsetTopAndBottom(targetTop, false /* requires update */);
        }
    };

    /**
     * 没有拉到底
     */
    private void moveToStart(float interpolatedTime) {
        int targetTop = 0;
        targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
        int offset = targetTop - mCircleView.getTop();
        DuboxLog.i("custom", "moveToStart -- offset = " + offset
                + ",  mOriginalOffsetTop = " + mOriginalOffsetTop + ", mFrom = " + mFrom);
        setTargetOffsetTopAndBottom(targetTop, false /* requires update */);
//        mTarget.animate().translationY(0).setDuration(200).start();
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void startScaleDownReturnToStartAnimation(int from,
                                                      Animation.AnimationListener listener) {
        mFrom = from;
        mScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
//                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        mScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION + mBrandTime);
        if (listener != null) {
            mCircleView.setAnimationListener(listener);
        }
        mCircleView.clearAnimation();
        mCircleView.startAnimation(mScaleDownToStartAnimation);
        mTarget.animate().translationY(0).setDuration(200 + mBrandTime).start();
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        DuboxLog.i("custom", "mcircleview  offset = " + offset);
        mCircleView.bringToFront();
        mCircleView.setTop(offset);
        mCurrentTargetOffsetTop = mCircleView.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public void setText(String text) {
        mCircleView.setText(text);
    }

    /**
     * 设置广告牌图片
     */
    public void setBrandImageBackground(int imageBackground) {
        mCircleView.setBrandImageBackground(imageBackground);
    }

    /**
     * 设置广告牌的高度
     */
    public void setViewHight(int height) {
        mCircleView.setViewHeigth(height);
    }

    /**
     * 设置下拉偏移量
     */
    public void setOffsetHeight(int height) {
        mBrandHegigt = height;
    }

    /**
     * 设置动画时长增量
     */
    public void setBrandTime(int brandTime) {
        mBrandTime = brandTime;
    }

    /**
     * 设置广告牌可点
     */
    public void setBrandViewClickbale() {
        mCircleView.setBrandViewClickbale();
    }

    /**
     * Classes that wish to be notified when the swipe gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnRefreshListener {
        public void onRefresh();
    }

}
