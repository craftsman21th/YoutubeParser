package com.moder.compass.base.imageloader;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.glide.Glide;
import com.dubox.glide.GlideBuilder;
import com.dubox.glide.Registry;
import com.dubox.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.module.GlideModule;

import java.io.InputStream;
import java.nio.ByteBuffer;


/**
 * TimeOutGlideModule
 *
 * @author panchao02
 * @since 2019/3/28.
 */

public class DuboxGlideModule implements GlideModule {
    private static final String TAG = "DuboxGlideModule";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        DuboxLog.i(TAG, "applyOptions");
        int diskCacheSizeBytes = 1024 * 1024 * 500; // 磁盘缓存设置到500M
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes));
    }

    @Override
    public void registerComponents(@NonNull Context context,
                                   @NonNull Glide glide, @NonNull Registry registry) {
        DuboxLog.i(TAG, "applyOptions");
        registry.replace(GlideUrl.class, InputStream.class, new DuboxHttpGlideUrlLoader.Factory());
        registry.prepend(Uri.class, InputStream.class,
                new DuboxLocalUriLoader.StreamFactory(context, context.getContentResolver()));
        registry.prepend(CustomLocalUri.class, InputStream.class,
                new DuboxCustomLocalUriLoader.StreamFactory(context, context.getContentResolver()));
        registry.prepend(CustomLocalBytes.class, InputStream.class,
                new DuboxCustomByteArrayLoader.StreamFactory());
        registry.prepend(CustomLocalBytes.class, ByteBuffer.class,
                new DuboxCustomByteArrayLoader.ByteBufferFactory());
    }
}
