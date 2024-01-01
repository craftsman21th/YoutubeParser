package com.moder.compass.base.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.dubox.glide.load.Options;
import com.dubox.glide.load.data.DataFetcher;
import com.dubox.glide.load.model.ModelLoader;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.load.model.ModelLoaderFactory;
import com.dubox.glide.load.model.MultiModelLoaderFactory;
import com.dubox.glide.signature.ObjectKey;

import java.io.InputStream;

/**
 * A ModelLoader for {@link Uri}s that handles local {@link Uri}s
 * directly and routes remote {@link Uri}s to a wrapped
 * {@link ModelLoader} that handles
 * {@link GlideUrl}s.
 *
 * @param <Data> The type of data that will be retrieved for {@link Uri}s.
 */
public class DuboxLocalUriLoader<Data> implements ModelLoader<Uri, Data> {

  private final LocalUriFetcherFactory<Data> factory;

  // Public API.
  @SuppressWarnings("WeakerAccess")
  public DuboxLocalUriLoader(LocalUriFetcherFactory<Data> factory) {
    this.factory = factory;
  }

  @Override
  public LoadData<Data> buildLoadData(@NonNull Uri model, int width, int height,
      @NonNull Options options) {
    return new LoadData<>(new ObjectKey(model), factory.build(model));
  }

  @Override
  public boolean handles(@NonNull Uri model) {
    return !model.toString().startsWith("http");
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
  public static class StreamFactory implements ModelLoaderFactory<Uri, InputStream>,
      LocalUriFetcherFactory<InputStream> {

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
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new DuboxLocalUriLoader<>(this);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
