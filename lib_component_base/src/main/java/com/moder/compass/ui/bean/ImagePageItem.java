package com.moder.compass.ui.bean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * 单纯的图片Item页面
 * 
 * @author 孙奇 <br/>
 *         create at 2013-3-8 下午02:25:18
 */
public class ImagePageItem implements ItemView {
    private static final String TAG = "ImagePageItem";

    /**
     * @author 孙奇 V 1.0.0 Create at 2013-3-8 下午02:44:24
     */
    private int mDrawableResId;
    private Context mContext;

    public ImagePageItem(int mDrawableResId, Context mContext) {
        this.mDrawableResId = mDrawableResId;
        this.mContext = mContext;
    }

    @Override
    public View getItemView() {
        ImageView iv = new ImageView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setImageResource(mDrawableResId);
        return iv;
    }

    @Override
    public void clear() {
        // do nothing
    }

}
