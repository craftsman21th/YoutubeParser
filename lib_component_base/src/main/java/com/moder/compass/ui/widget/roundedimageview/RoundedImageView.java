/*
 * Copyright (C) 2019 Baidu, Inc. All Rights Reserved.
 */
package com.moder.compass.ui.widget.roundedimageview;

import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    // Constants for tile mode attributes
    private static final int TILE_MODE_UNDEFINED = -2;
    private static final int TILE_MODE_CLAMP = 0;
    private static final int TILE_MODE_REPEAT = 1;
    private static final int TILE_MODE_MIRROR = 2;

    public static final String TAG = "RoundedImageView";
    public static final float DEFAULT_RADIUS = 0f;
    public static final float DEFAULT_BORDER_WIDTH = 0f;
    public static final Shader.TileMode DEFAULT_TILE_MODE = Shader.TileMode.CLAMP;
    private static final ScaleType[] SCALE_TYPES = { ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START,
            ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE };

    private final float[] mCornerRadii = new float[] { DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS, DEFAULT_RADIUS };

    private Drawable mBackgroundDrawable;
    private ColorStateList mBorderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    private float mBorderWidth = DEFAULT_BORDER_WIDTH;
    private ColorFilter mColorFilter = null;
    private boolean mColorMod = false;
    private Drawable mDrawable;
    private boolean mHasColorFilter = false;
    private boolean mIsOval = false;
    private boolean mMutateBackground = false;
    private int mResource;
    private int mBackgroundResource;
    private ScaleType mScaleType;
    private Shader.TileMode mTileModeX = DEFAULT_TILE_MODE;
    private Shader.TileMode mTileModeY = DEFAULT_TILE_MODE;

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MRoundedImageView, defStyle, 0);

        int index = a.getInt(R.styleable.MRoundedImageView_android_scaleType, -1);
        if (index >= 0) {
            setScaleType(SCALE_TYPES[index]);
        } else {
            // default scaletype to FIT_CENTER
            setScaleType(ScaleType.FIT_CENTER);
        }

        float cornerRadiusOverride = a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_corner_radius, -1);

        mCornerRadii[Corner.TOP_LEFT] =
                a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_corner_radius_top_left, -1);
        mCornerRadii[Corner.TOP_RIGHT] =
                a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_corner_radius_top_right, -1);
        mCornerRadii[Corner.BOTTOM_RIGHT] =
                a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_corner_radius_bottom_right, -1);
        mCornerRadii[Corner.BOTTOM_LEFT] =
                a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_corner_radius_bottom_left, -1);

        boolean any = false;
        for (int i = 0, len = mCornerRadii.length; i < len; i++) {
            if (mCornerRadii[i] < 0) {
                mCornerRadii[i] = 0f;
            } else {
                any = true;
            }
        }

        if (!any) {
            if (cornerRadiusOverride < 0) {
                cornerRadiusOverride = DEFAULT_RADIUS;
            }
            for (int i = 0, len = mCornerRadii.length; i < len; i++) {
                mCornerRadii[i] = cornerRadiusOverride;
            }
        }

        mBorderWidth = a.getDimensionPixelSize(R.styleable.MRoundedImageView_riv_border_width, -1);
        if (mBorderWidth < 0) {
            mBorderWidth = DEFAULT_BORDER_WIDTH;
        }

        mBorderColor = a.getColorStateList(R.styleable.MRoundedImageView_riv_border_color);
        if (mBorderColor == null) {
            mBorderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
        }

        mMutateBackground = a.getBoolean(R.styleable.MRoundedImageView_riv_mutate_background, false);
        mIsOval = a.getBoolean(R.styleable.MRoundedImageView_riv_oval, false);

        final int tileMode = a.getInt(R.styleable.MRoundedImageView_mriv_tile_mode, TILE_MODE_UNDEFINED);
        if (tileMode != TILE_MODE_UNDEFINED) {
            setTileModeX(parseTileMode(tileMode));
            setTileModeY(parseTileMode(tileMode));
        }

        final int tileModeX = a.getInt(R.styleable.MRoundedImageView_mriv_tile_mode_x, TILE_MODE_UNDEFINED);
        if (tileModeX != TILE_MODE_UNDEFINED) {
            setTileModeX(parseTileMode(tileModeX));
        }

        final int tileModeY = a.getInt(R.styleable.MRoundedImageView_mriv_tile_mode_y, TILE_MODE_UNDEFINED);
        if (tileModeY != TILE_MODE_UNDEFINED) {
            setTileModeY(parseTileMode(tileModeY));
        }

        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(true);

        if (mMutateBackground) {
            // noinspection deprecation
            super.setBackgroundDrawable(mBackgroundDrawable);
        }

        a.recycle();
    }

    private static Shader.TileMode parseTileMode(int tileMode) {
        switch (tileMode) {
            case TILE_MODE_CLAMP:
                return Shader.TileMode.CLAMP;
            case TILE_MODE_REPEAT:
                return Shader.TileMode.REPEAT;
            case TILE_MODE_MIRROR:
                return Shader.TileMode.MIRROR;
            default:
                return null;
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        assert scaleType != null;

        if (mScaleType != scaleType) {
            mScaleType = scaleType;

            switch (scaleType) {
                case CENTER:
                case CENTER_CROP:
                case CENTER_INSIDE:
                case FIT_CENTER:
                case FIT_START:
                case FIT_END:
                case FIT_XY:
                    super.setScaleType(ScaleType.FIT_XY);
                    break;
                default:
                    super.setScaleType(scaleType);
                    break;
            }

            updateDrawableAttrs();
            updateBackgroundDrawableAttrs(false);
            invalidate();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mResource = 0;
        mDrawable = RoundedDrawable.fromDrawable(drawable);
        updateDrawableAttrs();
        super.setImageDrawable(mDrawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mResource = 0;
        mDrawable = RoundedDrawable.fromBitmap(bm);
        updateDrawableAttrs();
        super.setImageDrawable(mDrawable);
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (mResource != resId) {
            mResource = resId;
            mDrawable = resolveResource();
            updateDrawableAttrs();
            super.setImageDrawable(mDrawable);
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setImageDrawable(getDrawable());
    }

    private Drawable resolveResource() {
        Resources rsrc = getResources();
        if (rsrc == null) {
            return null;
        }

        Drawable d = null;

        if (mResource != 0) {
            try {
                d = rsrc.getDrawable(mResource);
            } catch (Exception e) {
                DuboxLog.w(TAG, "Unable to find resource: " + mResource, e);
                // Don't try again.
                mResource = 0;
            }
        }
        return RoundedDrawable.fromDrawable(d);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    public void setBackgroundResource(@DrawableRes int resId) {
        if (mBackgroundResource != resId) {
            mBackgroundResource = resId;
            mBackgroundDrawable = resolveBackgroundResource();
            setBackgroundDrawable(mBackgroundDrawable);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundDrawable = new ColorDrawable(color);
        setBackgroundDrawable(mBackgroundDrawable);
    }

    private Drawable resolveBackgroundResource() {
        Resources rsrc = getResources();
        if (rsrc == null) {
            return null;
        }

        Drawable d = null;

        if (mBackgroundResource != 0) {
            try {
                d = rsrc.getDrawable(mBackgroundResource);
            } catch (Exception e) {
                DuboxLog.w(TAG, "Unable to find resource: " + mBackgroundResource, e);
                // Don't try again.
                mBackgroundResource = 0;
            }
        }
        return RoundedDrawable.fromDrawable(d);
    }

    private void updateDrawableAttrs() {
        updateAttrs(mDrawable, mScaleType);
    }

    private void updateBackgroundDrawableAttrs(boolean convert) {
        if (mMutateBackground) {
            if (convert) {
                mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable);
            }
            updateAttrs(mBackgroundDrawable, ScaleType.FIT_XY);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mColorFilter != cf) {
            mColorFilter = cf;
            mHasColorFilter = true;
            mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    private void applyColorMod() {
        // Only mutate and apply when modifications have occurred. This should
        // not reset the mColorMod flag, since these filters need to be
        // re-applied if the Drawable is changed.
        if (mDrawable != null && mColorMod) {
            mDrawable = mDrawable.mutate();
            if (mHasColorFilter) {
                mDrawable.setColorFilter(mColorFilter);
            }
            // mDrawable.setXfermode(mXfermode);
            // mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
        }
    }

    private void updateAttrs(Drawable drawable, ScaleType scaleType) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof RoundedDrawable) {
            ((RoundedDrawable) drawable).setScaleType(scaleType).setBorderWidth(mBorderWidth)
                    .setBorderColor(mBorderColor).setOval(mIsOval).setTileModeX(mTileModeX).setTileModeY(mTileModeY);

            if (mCornerRadii != null) {
                ((RoundedDrawable) drawable).setCornerRadius(mCornerRadii[Corner.TOP_LEFT],
                        mCornerRadii[Corner.TOP_RIGHT], mCornerRadii[Corner.BOTTOM_RIGHT],
                        mCornerRadii[Corner.BOTTOM_LEFT]);
            }

            applyColorMod();
        } else if (drawable instanceof LayerDrawable) {
            // loop through layers to and set drawable attrs
            LayerDrawable ld = ((LayerDrawable) drawable);
            for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
                updateAttrs(ld.getDrawable(i), scaleType);
            }
        }
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
        updateBackgroundDrawableAttrs(true);
        // noinspection deprecation
        super.setBackgroundDrawable(mBackgroundDrawable);
    }

    /**
     * Set the corner radii of all corners in px.
     *
     * @param radius the radius to set.
     */
    public void setCornerRadius(float radius) {
        setCornerRadius(radius, radius, radius, radius);
    }

    /**
     * Set the corner radii of each corner individually. Currently only one unique nonzero value is supported.
     *
     * @param topLeft radius of the top left corner in px.
     * @param topRight radius of the top right corner in px.
     * @param bottomRight radius of the bottom right corner in px.
     * @param bottomLeft radius of the bottom left corner in px.
     */
    public void setCornerRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        if (mCornerRadii[Corner.TOP_LEFT] == topLeft && mCornerRadii[Corner.TOP_RIGHT] == topRight
                && mCornerRadii[Corner.BOTTOM_RIGHT] == bottomRight && mCornerRadii[Corner.BOTTOM_LEFT] == bottomLeft) {
            return;
        }

        mCornerRadii[Corner.TOP_LEFT] = topLeft;
        mCornerRadii[Corner.TOP_RIGHT] = topRight;
        mCornerRadii[Corner.BOTTOM_LEFT] = bottomLeft;
        mCornerRadii[Corner.BOTTOM_RIGHT] = bottomRight;

        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        invalidate();
    }

    public void setTileModeX(Shader.TileMode tileModeX) {
        if (this.mTileModeX == tileModeX) {
            return;
        }

        this.mTileModeX = tileModeX;
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        invalidate();
    }

    public void setTileModeY(Shader.TileMode tileModeY) {
        if (this.mTileModeY == tileModeY) {
            return;
        }

        this.mTileModeY = tileModeY;
        updateDrawableAttrs();
        updateBackgroundDrawableAttrs(false);
        invalidate();
    }
}
