
package com.moder.compass.util.toast;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by linchangxin on 2014/12/16.
 */
public class ProgressDrawable extends Drawable {
    private static final String TAG = "ProgressDrawable";

    /**
     * Property Max of ProgressDrawable
     */
    public static final String MAX_PROPERTY = "max";

    /**
     * Property Progress of ProgressDrawable
     */
    public static final String PROGRESS_PROPERTY = "progress";

    /**
     * Property BackgroundColor of ProgressDrawable
     */
    public static final String BACKGROUND_COLOR_PROPERTY = "backgroundColor";

    /**
     * Property ProgressColor of ProgressDrawable
     */
    public static final String PROGRESS_COLOR_PROPERTY = "progressColor";

    /**
     * Paint object to draw the ProgressDrawable
     */
    private final Paint paint;

    /**
     * Max value of the ProgressDrawable
     */
    protected int mMax;

    /**
     * Progress value of the ProgressDrawable
     */
    protected int mProgress;

    /**
     * Background color of the ProgressDrawable
     */
    protected int mBackgroundColor;

    /**
     * Progress color of the ProgressDrawable
     */
    protected int mProgressColor;

    /**
     * The x-radius of the oval used to round the corners
     */
    protected int mRadiusX;

    /**
     * The y-radius of the oval used to round the corners
     */
    protected int mRadiusY;

    protected OnProgressChangedListener mOnProgressChangedListener;

    /**
     * Create a new ProgressDrawable
     * 
     * @param max
     * @param backgroundColor
     * @param progressColor
     */
    public ProgressDrawable(int max, int backgroundColor, int progressColor, int radiusX, int radiusY) {
        this.mMax = max;
        this.mProgress = 0;
        this.mBackgroundColor = backgroundColor;
        this.mProgressColor = progressColor;
        this.mRadiusX = radiusX;
        this.mRadiusY = radiusY;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();

        // background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        RectF backRect = new RectF(bounds);
        canvas.drawRoundRect(backRect, mRadiusX, mRadiusY, paint);

        // progress
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mProgressColor);
        int right = bounds.left + bounds.width() * mProgress / mMax;
        if (right > bounds.right) {
            right = bounds.right;
        }
        RectF progressRect = new RectF(bounds.left, bounds.top, right, bounds.bottom);
        canvas.drawRoundRect(progressRect, mRadiusX, mRadiusY, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return 1 - paint.getAlpha();
    }

    /**
     * Returns the max value of the ProgressDrawable
     * 
     * @return
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Sets the max
     * 
     * @param max
     */
    public void setMax(int max) {
        if (this.mMax != max && mOnProgressChangedListener != null) {
            mOnProgressChangedListener.onProgressChanged(mMax, mProgress);
        }

        mMax = max;
        this.invalidateSelf();
    }

    /**
     * Returns the progress value of the ProgerssDrawable
     * 
     * @return
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Sets the progress value of the ProgressDrawable
     * 
     * @param progress
     */
    public void setProgress(int progress) {
        if (this.mProgress != progress && mOnProgressChangedListener != null) {
            if (mMax > 0) {
                mOnProgressChangedListener.onProgressChanged(mMax, mProgress);
            }
        }

        this.mProgress = progress;
        invalidateSelf();
    }

    /**
     * Returns the background color of the ProgressDrawable
     * 
     * @return
     */
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * Sets the background color of the ProgressDrawable
     * 
     * @param backgroundColor
     */
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        invalidateSelf();
    }

    /**
     * Returns the progress color of the ProgressDrawable
     * 
     * @return
     */
    public int getProgressColor() {
        return mProgressColor;
    }

    /**
     * Sets the progress color of the ProgressDrawable
     * 
     * @param progressColor
     */
    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidateSelf();
    }

    /**
     * Sets progress/max value changed listener
     * 
     * @param listener
     */
    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        mOnProgressChangedListener = listener;
    }

    /**
     * Interface used to allow the creator of a ProgressDrawable to run some code when an progress/max value is changed
     */
    public interface OnProgressChangedListener {
        /**
         * This method will be invoked when progress/max changed
         * 
         * @param max
         * @param progress
         */
        void onProgressChanged(int max, int progress);
    }
}
