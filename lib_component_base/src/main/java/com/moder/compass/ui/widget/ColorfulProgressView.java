package com.moder.compass.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.util.FormatUtils;

import java.util.ArrayList;

/**
 * 设置页存储容量彩条view
 * 
 * @author yangqinghai @created 2012-10-25 下午02:14:08
 */
public class ColorfulProgressView extends View {
    private final String TAG = "ColorfulProgressView";
    private final Paint mPaint = new Paint();

    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Rect mTextRect = new Rect();
    private Rect mRect1 = new Rect();
    private ArrayList<Float> mPercentage;
    private int width = 0;
    private static final int PROGRESS_HEIGHT_DEFAULT = 20;

    /** Progress在这个进度是开始变成警告色 **/
    private static final int WARNING_PROGRESS = 60;
    /** Progress在这个进度是开始颜色加重 **/
    private static final int SERIOUS_PROGRESS = 90;

    /**
     * 转存时显示的进度条的高度
     */
    public static final float PROGRESS_VIEW_HEIGHT_TRANSFER = 20.0f;

    private int height = PROGRESS_HEIGHT_DEFAULT;
    // progress width

    /**
     * 用户配额到期提醒
     */
    private boolean isExpired = false;

    private long mSpaceNum;

    /**
     * 背景绘制的颜色
     */
    private int mBgColor;

    /**
     * 容量过期的颜色
     */
    private int mStorageExpireColor;

    /**
     * 是否正在获取空间
     */
    private boolean mIsGettingSpace;

    /**
     * 获取空间失败
     */
    private boolean mIsGetSpaceFailed;

    /**
     * 是否要显示文字
     */
    private boolean mIsDrawText;

    private static final int PADDNG_RIGHT = 20;

    private static final double STARTY = 0.75;

    private static final float TEXT_SIZE = 12;

    public ColorfulProgressView(Context context, AttributeSet attr) {
        this(context, attr, 0);
        initParam();
    }

    public ColorfulProgressView(Context context, AttributeSet attr, int defstyle) {
        super(context, attr, defstyle);
        initParam();
    }

    private void initParam() {
        int[] colors = getResources().getIntArray(R.array.settings_colors);
        mBgColor = colors[6];
        mStorageExpireColor = colors[1];
        mTextPaint.setColor(getContext().getResources().getColor(R.color.white));
        mTextPaint.setTextSize(DeviceDisplayUtils.dip2px(getContext(), TEXT_SIZE));
    }

    /**
     * 设置是否处于正在获取空间的状态
     */
    public void setIsStatusGettingSapce(boolean isGettingSpace) {
        this.mIsGettingSpace = isGettingSpace;
//        postInvalidate();
    }

    /**
     * 获取空间失败
     */
    public void setIsStatusGetSpaceFailed(boolean isGetSpaceFailed) {
        this.mIsGetSpaceFailed = isGetSpaceFailed;
//        postInvalidate();
    }

    /**
     * 更新progress的颜色
     * 
     * @param percentage
     */
    public void updateView(ArrayList<Float> percentage, boolean isExpired) {
        int size = percentage.size();
        if (size > 0) {
            mPercentage = percentage;
        }
        this.isExpired = isExpired;
        postInvalidate();
    }

    /**
     * 更新进度，是否要显示文字
     * 
     * @param percentage 进度
     * @param isExpired 空间过期
     * @param spaceNum 空间剩余大小
     * @param isShowText 是否要显示文字
     */
    public void updateView(ArrayList<Float> percentage, boolean isExpired, long spaceNum, boolean isShowText) {
        int size = percentage.size();
        if (size > 0) {
            mPercentage = percentage;
        }
        this.isExpired = isExpired;
        this.mIsDrawText = isShowText;
        this.mSpaceNum = spaceNum;
        postInvalidate();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    public void setStorageExpireColor(int color) {
        mStorageExpireColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL);

        width = getWidth();
        if (!isExpired) { // 容量未到期
            if (mPercentage != null && mPercentage.size() > 0) { // 已经使用的容量
                canvas.save();
                mRect1.set(0, 0, width, height);
                mPaint.setColor(mBgColor);
                canvas.drawRect(mRect1, mPaint);
                canvas.restore();

                int startOffset = 0;
                int endOffset = 0;
                for (int i = 0; i < mPercentage.size(); i++) {
                    int temp = endOffset;
                    startOffset = endOffset;
                    int delta = 0;
                    if (mPercentage.get(i) > 0) {
                        delta = (int) (mPercentage.get(i) * width) > 0 ? (int) (mPercentage.get(i) * width) : 1;
                    }
                    endOffset = temp + delta;
                    canvas.save();
                    mRect1.set(startOffset, 0, endOffset, height);
                    int progress = (int) (mPercentage.get(i) * 100);
                    if (progress > SERIOUS_PROGRESS) {
                        mPaint.setColor(getResources().getColor(R.color.progress_serious));
                    } else if (progress > WARNING_PROGRESS) {
                        mPaint.setColor(getResources().getColor(R.color.progress_warning));
                    } else {
                        mPaint.setColor(getResources().getColor(R.color.progress_pic_blue));
                    }
                    canvas.drawRect(mRect1, mPaint);
                    if (mIsDrawText) {
                        String spaceNum = "";
                        if (mSpaceNum >= 0) {
                            spaceNum =
                                    String.format(getContext().getString(R.string.share_user_qouta),
                                            FormatUtils.formatFileSize(mSpaceNum));
                        } else {
                            spaceNum = getContext().getString(R.string.server_storage_not_enough);
                        }
                        drawText(canvas, spaceNum);
                    }
                    canvas.restore();
                }
            } else {
                canvas.save();
                mRect1.set(0, 0, width, height);
                mPaint.setColor(mBgColor);
                canvas.drawRect(mRect1, mPaint);
                if (mIsGetSpaceFailed) {
                    drawText(canvas, getContext().getString(R.string.get_storage_failed));
                } else if (mIsGettingSpace) {
                    drawText(canvas, getContext().getString(R.string.getting_storage));
                }
                canvas.restore();
            }

        } else { // 容量到期
            drawWarningRect(canvas);
        }
    }

    /**
     * @param canvas
     */
    private void drawWarningRect(Canvas canvas) {
        canvas.save();
        mRect1.set(0, 0, width, height);
        mPaint.setColor(mStorageExpireColor);
        canvas.drawRect(mRect1, mPaint);
        if (mIsDrawText) {
            final String spaceNum = getContext().getString(R.string.server_storage_not_enough);
            drawText(canvas, spaceNum);
            canvas.restore();
        }
    }

    /**
     * 绘制要显示的文字
     * 
     * @param canvas
     */
    private void drawText(Canvas canvas, final String text) {

        // 返回包围整个字符串的最小的一个Rect区域
        mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
        final float textWidth = mTextPaint.measureText(text);/*
                                                              * mTextRect. width ()
                                                              */
        DuboxLog.d(TAG, "mesure width=" + mTextPaint.measureText(text) + " textwidth=" + textWidth);

        // final int textHeight = rect.height();
        canvas.drawText(text, (width - textWidth) / 2, (int) (height * STARTY), mTextPaint);
    }

}