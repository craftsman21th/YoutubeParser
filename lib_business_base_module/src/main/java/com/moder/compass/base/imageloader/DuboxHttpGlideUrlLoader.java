package com.moder.compass.base.imageloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dubox.glide.load.Option;
import com.dubox.glide.load.Options;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.load.model.ModelCache;
import com.dubox.glide.load.model.ModelLoader;
import com.dubox.glide.load.model.ModelLoaderFactory;
import com.dubox.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * An {@link ModelLoader} for translating {@link
 * GlideUrl} (http/https URLS) into {@link InputStream} data.
 */
// Public API.
@SuppressWarnings("WeakerAccess")
public class DuboxHttpGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {
  /**
   * An integer option that is used to determine the maximum connect and read timeout durations (in
   * milliseconds) for network connections.
   *
   * <p>Defaults to 2500ms.
   */
  public static final Option<Integer> TIMEOUT = Option.memory(
      "HttpGlideUrlLoader.Timeout", 2500);

  @Nullable private final ModelCache<GlideUrl, GlideUrl> modelCache;

  public DuboxHttpGlideUrlLoader() {
    this(null);
  }

  public DuboxHttpGlideUrlLoader(@Nullable ModelCache<GlideUrl, GlideUrl> modelCache) {
    this.modelCache = modelCache;
  }

  @Override
  public LoadData<InputStream> buildLoadData(@NonNull GlideUrl model, int width, int height,
      @NonNull Options options) {
    // GlideUrls memoize parsed URLs so caching them saves a few object instantiations and time
    // spent parsing urls.
    GlideUrl url = model;
    if (modelCache != null) {
      url = modelCache.get(model, 0, 0);
      if (url == null) {
        modelCache.put(model, 0, 0, model);
        url = model;
      }
    }
    int timeout = options.get(TIMEOUT);
    return new LoadData<>(url, new DuboxHttpGlideFetcher(url, timeout));
  }

  @Override
  public boolean handles(@NonNull GlideUrl model) {
    return model.toStringUrl().startsWith("http");
  }

  /**
   * The default factory for {@link DuboxHttpGlideUrlLoader}s.
   */
  public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
    private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache<>(500);

    @NonNull
    @Override
    public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new DuboxHttpGlideUrlLoader(modelCache);
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}
