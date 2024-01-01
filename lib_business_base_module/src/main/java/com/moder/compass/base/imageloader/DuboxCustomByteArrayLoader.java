package com.moder.compass.base.imageloader;

import androidx.annotation.NonNull;

import com.dubox.glide.Priority;
import com.dubox.glide.load.DataSource;
import com.dubox.glide.load.Options;
import com.dubox.glide.load.data.DataFetcher;
import com.dubox.glide.load.model.ModelLoader;
import com.dubox.glide.load.model.ModelLoaderFactory;
import com.dubox.glide.load.model.MultiModelLoaderFactory;
import com.dubox.glide.signature.ObjectKey;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 本地图片加载byte资源Loader，支持自定义缓存Key
 *
 * Created by panchao02 on 2019/03/28.
 */
public class DuboxCustomByteArrayLoader<Data> implements ModelLoader<CustomLocalBytes, Data> {
  private final DuboxCustomByteArrayLoader.Converter<Data> converter;

  @SuppressWarnings("WeakerAccess") // Public API
  public DuboxCustomByteArrayLoader(DuboxCustomByteArrayLoader.Converter<Data> converter) {
    this.converter = converter;
  }

  @Override
  public LoadData<Data> buildLoadData(
          @NonNull CustomLocalBytes model, int width, int height, @NonNull Options options) {
    return new LoadData<>(new ObjectKey(model.getCacheKey()),
            new DuboxCustomByteArrayLoader.Fetcher<>(model.getBytes(), converter));
  }

  @Override
  public boolean handles(@NonNull CustomLocalBytes customLocalBytes) {
    return true;
  }

  /**
   * Converts between a byte array a desired model class.
   *
   * @param <Data> The type of data to convert to.
   */
  public interface Converter<Data> {
    Data convert(byte[] model);

    Class<Data> getDataClass();
  }

  private static class Fetcher<Data> implements DataFetcher<Data> {
    private final byte[] model;
    private final DuboxCustomByteArrayLoader.Converter<Data> converter;

    /**
     * @param model We really ought to copy the model, but doing so can be hugely expensive and/or
     *              lead to OOMs. In practice it's unlikely that users would pass an array into
     *              Glide and then mutate it.
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    Fetcher(byte[] model, DuboxCustomByteArrayLoader.Converter<Data> converter) {
      this.model = model;
      this.converter = converter;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> callback) {
      Data result = converter.convert(model);
      callback.onDataReady(result);
    }

    @Override
    public void cleanup() {
      // Do nothing.
    }

    @Override
    public void cancel() {
      // Do nothing.
    }

    @NonNull
    @Override
    public Class<Data> getDataClass() {
      return converter.getDataClass();
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
      return DataSource.LOCAL;
    }
  }

  /**
   * Factory for {@link DuboxCustomByteArrayLoader} and
   * {@link java.nio.ByteBuffer}.
   */
  public static class ByteBufferFactory implements ModelLoaderFactory<CustomLocalBytes, ByteBuffer> {

    @NonNull
    @Override
    public ModelLoader<CustomLocalBytes, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
      return new DuboxCustomByteArrayLoader<>(new DuboxCustomByteArrayLoader.Converter<ByteBuffer>() {
        @Override
        public ByteBuffer convert(byte[] model) {
          return ByteBuffer.wrap(model);
        }

        @Override
        public Class<ByteBuffer> getDataClass() {
          return ByteBuffer.class;
        }
      });
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }

  /**
   * Factory for {@link DuboxCustomByteArrayLoader} and {@link java.io.InputStream}.
   */
  public static class StreamFactory implements ModelLoaderFactory<CustomLocalBytes, InputStream> {

    @NonNull
    @Override
    public ModelLoader<CustomLocalBytes, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
      return new DuboxCustomByteArrayLoader<>(new DuboxCustomByteArrayLoader.Converter<InputStream>() {
        @Override
        public InputStream convert(byte[] model) {
          return new ByteArrayInputStream(model);
        }

        @Override
        public Class<InputStream> getDataClass() {
          return InputStream.class;
        }
      });
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }

}
