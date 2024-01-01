package com.moder.compass.base.imageloader;

import com.dubox.glide.request.target.DrawableImageViewTarget;
import com.dubox.glide.request.transition.Transition;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by panchao02 on 2018/9/3.
 */

public class GlideDrawableImageViewTarget extends DrawableImageViewTarget {

    private static final String TAG = "GlideDrawableImageViewTarget";
    private GlideLoadingListener listener;

    public GlideDrawableImageViewTarget(ImageView view) {
        this(view, null);
    }

    public GlideDrawableImageViewTarget(ImageView view,
                                        GlideLoadingListener loadingListener) {
        super(view);
        this.listener = loadingListener;
    }


    @Override
    public void onLoadStarted(@Nullable Drawable placeholder) {
        super.onLoadStarted(placeholder);
        View v = getView();
        if (v != null && listener != null) {
            listener.onLoadStarted(v, placeholder);
        }
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        super.onLoadCleared(placeholder);
        View v = getView();
        if (v != null && listener != null) {
            listener.onLoadCleared(v, placeholder);
        }
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        super.onLoadFailed(errorDrawable);
        View v = getView();
        if (v != null && listener != null) {
            listener.onLoadFailed(v, errorDrawable);
        }
    }

    @Override
    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        super.onResourceReady(resource, transition);
        View v = getView();
        if (v != null && listener != null) {
            listener.onResourceReady(v, resource);
        }
    }

}
