/*
 * CircleImageViw.java
 * classes : com.dubox.drive.ui.widget.CircleImageView
 * 
 * @author not me
 * @link https://github.com/hdodenhof/CircleImageView
 * 
 * 特别注意：The ScaleType is always CENTER_CROP and you'll get an exception if you try to change it
 * 不要设置scaletype为center_crop之外的方式
 * 
 * V 1.0.0
 * Create at 2014年3月7日 下午3:50:51
 */
package com.moder.compass.ui.widget;

import com.moder.compass.BaseApplication;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * com.dubox.drive.ui.widget.CircleImageViw
 * 
 * @author chenyuquan <br/>
 *         create at 2014年3月7日 下午3:50:51
 */
public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "CircleImageView";

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config DEFAULT_BITMAP_CONFIG = Bitmap.Config.RGB_565;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    /**
     * 默认边框的颜色
     */
    private static final int DEFAULT_BORDER_COLOR = BaseApplication.getInstance().getResources()
            .getColor(R.color.transparent);

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;
    private Bitmap.Config bitmapConfig = DEFAULT_BITMAP_CONFIG;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NetdiskImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.NetdiskImageView_view_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.NetdiskImageView_view_border_color, DEFAULT_BORDER_COLOR);
        a.recycle();

        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }

        canvas.drawCircle(getWidth() / (float) 2, getHeight() / (float) 2, mDrawableRadius, mBitmapPaint);
        canvas.drawCircle(getWidth() / (float) 2, getHeight() / (float) 2, mBorderRadius, mBorderPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    /**
     * 设置解析图片时使用的颜色规格。
     */
    public void setBitmapConfig(Bitmap.Config bitmapConfig) {
        if (this.bitmapConfig == bitmapConfig) {
            return;
        }
        this.bitmapConfig = bitmapConfig;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (bitmapConfig == null) {
                bitmapConfig = DEFAULT_BITMAP_CONFIG;
            }
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, bitmapConfig);
            } else {
                bitmap =
                        Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), bitmapConfig);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            DuboxLog.e(TAG, "OutOfMemoryError", e);
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setShader(mBitmapShader);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height()
                - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (this.mOnVisibilityChangedListener != null) {
            this.mOnVisibilityChangedListener.onVisibilityChanged(changedView, visibility);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    public OnVisibilityChangedListener mOnVisibilityChangedListener;

    public void setOnVisibilityChangedListener(OnVisibilityChangedListener listener) {
        this.mOnVisibilityChangedListener = listener;
    }

    public interface OnVisibilityChangedListener {
        void onVisibilityChanged(@NonNull View changedView, int visibility);
    }
}
