package com.moder.compass.base.imageloader;

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * Created by panchao02 on 2018/9/3.
 */

public interface GlideLoadingListener<R> {

    void onLoadStarted(@NonNull View imageView, @Nullable Drawable placeholder);

    void onLoadFailed(@NonNull View imageView, @Nullable Drawable errorDrawable);

    void onResourceReady(@NonNull View imageView, @NonNull R resource);

    void onLoadCleared(@NonNull View imageView, @Nullable Drawable placeholder);
}
