package com.moder.compass.base.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.dubox.glide.load.Options;
import com.dubox.glide.load.data.DataFetcher;
import com.dubox.glide.load.model.ModelLoader;
import com.dubox.glide.load.model.ModelLoaderFactory;
import com.dubox.glide.load.model.MultiModelLoaderFactory;
import com.dubox.glide.signature.ObjectKey;

import java.io.InputStream;

/**
 * 本地图片加载Loader，支持自定义缓存Key
 *
 * Created by lijunnian on 2018/9/14.
 */

public class DuboxCustomLocalUriLoader<Data> implements ModelLoader<CustomLocalUri, Data> {

    private final LocalUriFetcherFactory<Data> mFactory;

    @SuppressWarnings("WeakerAccess")
    public DuboxCustomLocalUriLoader(LocalUriFetcherFactory<Data> factory) {
        this.mFactory = factory;
    }

    @Override
    public LoadData<Data> buildLoadData(@NonNull CustomLocalUri customLocalUri, int width, int height,
                                        @NonNull Options options) {
        return new LoadData<>(new ObjectKey(customLocalUri.getCacheKey()), mFactory.build(customLocalUri.getUri()));
    }

    @Override
    public boolean handles(@NonNull CustomLocalUri customLocalUri) {
        return true;
    }

    /**
     * Factory for obtaining a {@link DataFetcher} for a data type for a particular {@link Uri}.
     *
     * @param <Data> The type of data the returned {@link DataFetcher} will obtain.
     */
    public interface LocalUriFetcherFactory<Data> {
        DataFetcher<Data> build(Uri uri);
    }

    /**
     * Loads {@link InputStream}s from {@link Uri}s.
     */
    public static class StreamFactory implements ModelLoaderFactory<CustomLocalUri, InputStream>,
            DuboxCustomLocalUriLoader.LocalUriFetcherFactory<InputStream> {

        private final ContentResolver contentResolver;
        private final Context context;

        public StreamFactory(Context context, ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
            this.context = context;
        }

        @Override
        public DataFetcher<InputStream> build(Uri uri) {
            return new DuboxLocalUriFetcher(context, contentResolver, uri);
        }

        @NonNull
        @Override
        public ModelLoader<CustomLocalUri, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new DuboxCustomLocalUriLoader<>(this);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
