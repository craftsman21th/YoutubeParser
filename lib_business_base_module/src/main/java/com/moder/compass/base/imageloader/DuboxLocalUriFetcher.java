package com.moder.compass.base.imageloader;

import android.content.ContentResolver;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.ParallelAsyncTask;
import com.dubox.glide.load.data.LocalUriFetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * A DataFetcher that uses an {@link ContentResolver} to load data from a {@link
 * Uri} pointing to a local resource.
 */
public class DuboxLocalUriFetcher extends LocalUriFetcher<InputStream> {

  private static final String TAG = "ImageLocalUriFetcher";
  /**
   * A lookup uri (e.g. content://com.android.contacts/contacts/lookup/3570i61d948d30808e537)
   */
  private static final int ID_CONTACTS_LOOKUP = 1;
  /**
   * A contact thumbnail uri (e.g. content://com.android.contacts/contacts/38/photo)
   */
  private static final int ID_CONTACTS_THUMBNAIL = 2;
  /**
   * A contact uri (e.g. content://com.android.contacts/contacts/38)
   */
  private static final int ID_CONTACTS_CONTACT = 3;
  /**
   * A contact display photo (high resolution) uri
   * (e.g. content://com.android.contacts/5/display_photo)
   */
  private static final int ID_CONTACTS_PHOTO = 4;
  /**
   * Uri for optimized search of phones by number
   * (e.g. content://com.android.contacts/phone_lookup/232323232
   */
  private static final int ID_LOOKUP_BY_PHONE = 5;
  /**
   * Match the incoming Uri for special cases which we can handle nicely.
   */
  private static final UriMatcher URI_MATCHER;

  private Context mContext;

  static {
    URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*/#", ID_CONTACTS_LOOKUP);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/lookup/*", ID_CONTACTS_LOOKUP);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/#/photo", ID_CONTACTS_THUMBNAIL);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/#", ID_CONTACTS_CONTACT);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "contacts/#/display_photo", ID_CONTACTS_PHOTO);
    URI_MATCHER.addURI(ContactsContract.AUTHORITY, "phone_lookup/*", ID_LOOKUP_BY_PHONE);
  }

  public DuboxLocalUriFetcher(Context context, ContentResolver resolver, Uri uri) {
    super(resolver, uri);
    mContext = context;
  }

  @Override
  protected InputStream loadResource(Uri uri, ContentResolver contentResolver)
          throws FileNotFoundException {
    InputStream inputStream = null;
    try {
      inputStream = loadResourceFromUri(uri, contentResolver);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (inputStream == null) {
      throw new FileNotFoundException("InputStream is null for " + uri);
    }
    return inputStream;
  }


  private InputStream loadResourceFromUri(Uri uri, ContentResolver contentResolver)
          throws FileNotFoundException {
    switch (URI_MATCHER.match(uri)) {
      case ID_CONTACTS_THUMBNAIL:
      case ID_CONTACTS_PHOTO:
      case UriMatcher.NO_MATCH:
      default:
        try {
          return getStreamFromOtherSource(uri, contentResolver);
        } catch (IOException e) {
          e.printStackTrace();
          return contentResolver.openInputStream(uri);
        }
    }
  }

  @Override
  protected void close(InputStream data) throws IOException {
    data.close();
  }

  @NonNull
  @Override
  public Class<InputStream> getDataClass() {
    return InputStream.class;
  }

  private InputStream getStreamFromOtherSource(Uri uri, ContentResolver contentResolver) throws IOException {
    if (uri.getScheme().equals(ThumbnailHelper.LocalThumbnail.SCHEME_THUMBNAIL)) {
      int category =
              Integer.valueOf(uri.getQueryParameter(ThumbnailHelper.LocalThumbnail.QUERY_PARAMETER_CATEGORY));
      if (category == ThumbnailHelper.LocalThumbnail.CATEGORY_IMAGE) {
        return getStreamFromLocalThunmbnail(uri, contentResolver);
      } else if (category == ThumbnailHelper.LocalThumbnail.CATEGORY_VIDEO) {
        return getStreamFromLocalVideoThunmbnail(uri, contentResolver);
      } else {
        return contentResolver.openInputStream(uri);
      }
    } else {
      return contentResolver.openInputStream(uri);
    }
  }

  /**
   * @param uri
   * @return
   * @throws IOException
   */
  private InputStream getStreamFromLocalVideoThunmbnail(Uri uri, ContentResolver contentResolver) throws IOException {
    long videoId = Long.parseLong(uri.getQueryParameter(ThumbnailHelper.LocalThumbnail.QUERY_PARAMETER_ID));

    // 查询缩略图文件路径
    Cursor cursor = null;
    String thumbnailPath = null;
    try {
      cursor = BaseApplication.getInstance().getContentResolver().query(
              MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Video.Thumbnails.DATA },
              MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);
      if (cursor != null && cursor.moveToFirst()) {
        thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

    // 判断缩略图是否存在
    boolean thumbnailExist = !TextUtils.isEmpty(thumbnailPath) && new File(thumbnailPath).exists();

    if (thumbnailExist) {
      // 根据文件路径获取图片数据流
      Uri thumbnailPathUri = Uri.parse(ThumbnailHelper.makeLocalUrlByPath(thumbnailPath));
      return contentResolver.openInputStream(thumbnailPathUri);
    } else {
      // 如果缩略图不存在，那么使用原图文件路径，并且调用系统api生成缩略图
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inDither = false;
      options.inPreferredConfig = Bitmap.Config.RGB_565;

      Bitmap bitmap = android.provider.MediaStore.Video.Thumbnails.getThumbnail(
              BaseApplication.getInstance().getContentResolver(), videoId, MediaStore.Images.Thumbnails.MINI_KIND, options);

      // 将缩略图转为数据流
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
      try {
        return new ByteArrayInputStream(outputStream.toByteArray());
      } finally {
        try {
          outputStream.close();
        } catch (IOException e) {
          DuboxLog.e(TAG, e.getMessage(), e);
        }
      }
    }
  }

  private InputStream getStreamFromLocalThunmbnail(Uri uri, ContentResolver contentResolver) throws IOException {
    long imageId = Long.parseLong(uri.getQueryParameter(ThumbnailHelper.LocalThumbnail.QUERY_PARAMETER_ID));
    String imagePath = uri.getQueryParameter(ThumbnailHelper.LocalThumbnail.QUERY_PARAMETER_IMAGE_PATH);

    // 查询缩略图文件路径
    Cursor cursor = null;
    String thumbnailPath = null;
    try {
      cursor = BaseApplication.getInstance().getContentResolver().query(
              MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
              new String[] { MediaStore.Images.Thumbnails.DATA },
              MediaStore.Images.Thumbnails.IMAGE_ID + "=" + imageId, null, null);
      if (cursor != null && cursor.moveToFirst()) {
        thumbnailPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }

    // 判断缩略图是否存在
    boolean thumbnailExist = !TextUtils.isEmpty(thumbnailPath) && new File(thumbnailPath).exists();

    // 如果缩略图不存在，那么使用原图文件路径，并且调用系统api生成缩略图
    if (!thumbnailExist) {
      thumbnailPath = imagePath;

      new ParallelAsyncTask<Long, Void, Void>() {

        @Override
        protected Void doInBackground(Long...params) {
          long imageId = params[0];
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inDither = false;
          options.inPreferredConfig = Bitmap.Config.RGB_565;

          MediaStore.Images.Thumbnails.getThumbnail(BaseApplication.getInstance().getContentResolver(), imageId,
                  MediaStore.Images.Thumbnails.MINI_KIND, options);
          return null;
        }

      }.execute(imageId);
    }

    Uri thumbnailPathUri = Uri.parse(ThumbnailHelper.makeLocalUrlByPath(thumbnailPath));
    // 根据文件路径获取图片数据流
    return contentResolver.openInputStream(thumbnailPathUri);
  }
}

