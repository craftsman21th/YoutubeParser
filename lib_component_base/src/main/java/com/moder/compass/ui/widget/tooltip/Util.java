package com.moder.compass.ui.widget.tooltip;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.ColorRes;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by 魏铮铮 on 16/10/12.
 */
public class Util {
    private static final String TAG = "Util";

    public static RectF calculateRectOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(),
                location[1] + view.getMeasuredHeight());
    }

    public static RectF calculateRectInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new RectF(location[0], location[1], location[0] + view.getMeasuredWidth(),
                location[1] + view.getMeasuredHeight());
    }

    public static float dpFromPx(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static float pxFromDp(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static void setWidth(View view, float width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams((int) width, view.getHeight());
        } else {
            params.width = (int) width;
        }
        view.setLayoutParams(params);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setX(View view, int x) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setX(x);
        } else {
            ViewGroup.MarginLayoutParams marginParams = getOrCreateMarginLayoutParams(view);
            marginParams.leftMargin = x - view.getLeft();
            view.setLayoutParams(marginParams);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setY(View view, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setY(y);
        } else {
            ViewGroup.MarginLayoutParams marginParams = getOrCreateMarginLayoutParams(view);
            marginParams.topMargin = y - view.getTop();
            view.setLayoutParams(marginParams);
        }
    }

    public static void setMargins(View view, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams lpk = Util.getOrCreateMarginLayoutParams(view);
        lpk.setMargins(left, top, right, bottom);
        view.setLayoutParams(lpk);
    }

    public static ViewGroup.MarginLayoutParams getOrCreateMarginLayoutParams(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                return (ViewGroup.MarginLayoutParams) lp;
            } else {
                return new ViewGroup.MarginLayoutParams(lp);
            }
        } else {
            return new ViewGroup.MarginLayoutParams(view.getWidth(), view.getHeight());
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } else {
            // noinspection deprecation
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void setTextAppearance(TextView tv, @StyleRes int textAppearanceRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAppearance(textAppearanceRes);
        } else {
            // noinspection deprecation
            tv.setTextAppearance(tv.getContext(), textAppearanceRes);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getColor(Context context, @ColorRes int colorRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(colorRes);
        } else {
            // noinspection deprecation
            return context.getResources().getColor(colorRes);
        }
    }

    public static int[] getWiewWidthAndHeight(View view) {
        int[] result = new int[2];
        result[0] = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        result[1] = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(result[0], result[1]);
        final ViewGroup.MarginLayoutParams lp = getOrCreateMarginLayoutParams(view);
        result[0] = view.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        result[1] = view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        return result;
    }
}