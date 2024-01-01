package com.moder.compass.base.imageloader;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.moder.compass.BaseApplication;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

/**
 * 系统多媒体库管理工具类
 *
 * Created by lijunnian on 2018/9/14.
 */

public class MediaStoreHelper {

    private static final String TAG = "MediaStoreHelper";

    private static MediaStoreHelper sInstance;

    public static MediaStoreHelper getInstance() {
        if (sInstance == null) {
            synchronized (MediaStoreHelper.class) {
                if (sInstance == null) {
                    sInstance = new MediaStoreHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 根据本地视频路径获取视频ID，即MediaStore.Video.Media._ID
     * @param localPath 本地视频路径
     * @return 本地视频对应的ID值
     */
    public String getOriginVideoIdByPath(String localPath) {
        String originVideoId = "";

        if (TextUtils.isEmpty(localPath)) {
            return originVideoId;
        }

        Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] mediaProjection = new String[] { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA };
        Cursor mediaCursor = BaseApplication.getInstance().getContentResolver().query(mediaUri, mediaProjection,
                MediaStore.Video.VideoColumns.DATA + " = ?",
                new String[] { localPath }, null );

        if (mediaCursor == null) {
            return originVideoId;
        }

        if (mediaCursor.moveToFirst()) {
            originVideoId = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Video.Media._ID));
            DuboxLog.d(TAG, "media result " + " localPath : " + localPath + " originVideoId : " + originVideoId);
        }

        mediaCursor.close();
        return originVideoId;
    }

    /**
     * 根据本地视频路径获取视频ID，即MediaStore.Images.Media._ID
     * @param localPath 本地原图路径
     * @return 本地原图对应的ID值
     */
    public synchronized String getOriginImageIdByPath(String localPath) {
        String originImageId = "";

        if (TextUtils.isEmpty(localPath)) {
            return originImageId;
        }
        Cursor mediaCursor = null;

        try {
            Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] mediaProjection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
            mediaCursor = BaseApplication.getInstance().getContentResolver().query(mediaUri, mediaProjection,
                    MediaStore.Images.ImageColumns.DATA + " = ?",
                    new String[] { localPath }, null );

            if (mediaCursor == null) {
                return originImageId;
            }

            if (mediaCursor.moveToFirst()) {
                originImageId = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Images.Media._ID));
                DuboxLog.d(TAG, "media result " + " localPath : " + localPath + " originImageId : " + originImageId);
            }
        } catch (Exception e) {
            DuboxLog.d(TAG, "getOriginImageIdByPath exception: " + e.getMessage());
        } finally {
            if (mediaCursor != null) {
                mediaCursor.close();
            }
        }
        return originImageId;
    }

    /**
     * 根据本地缩略图ID获得本地缩略图Path
     * @param imageId 本地缩略图ID
     * @return 本地缩略图Path
     */
    public String getThumbnailPathById(String imageId) {
        String thumbnailPath = "";

        if (TextUtils.isEmpty(imageId)) {
            return thumbnailPath;
        }

        Uri thumbnailUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] thumbnailProjection = new String[] { MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA };

        Cursor thumbnailCursor = null;
        try {
            thumbnailCursor = BaseApplication.getInstance().getContentResolver()
                    .query(thumbnailUri, thumbnailProjection,
                    MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                    new String[]{ imageId }, null );

            if (thumbnailCursor == null) {
                return thumbnailPath;
            }

            if (thumbnailCursor.moveToFirst()) {
                thumbnailPath = thumbnailCursor.getString(
                        thumbnailCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                DuboxLog.d(TAG, "thumbnail result " + " imageId : " + imageId + " thumbnailPath : " + thumbnailPath);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage());
        } finally {
            if (thumbnailCursor != null) {
                thumbnailCursor.close();
            }
        }

        return thumbnailPath;
    }

    /**
     * 根据本地视频ID获得本地缩略图Path
     * @param videoId 本地视频ID
     * @return 本地缩略图Path
     */
    public String getThumbnailPathByVideoId(String videoId) {
        String thumbnailPath = "";

        if (TextUtils.isEmpty(videoId)) {
            return thumbnailPath;
        }

        Uri thumbnailUri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] thumbnailProjection = new String[] { MediaStore.Video.Thumbnails.VIDEO_ID,
                MediaStore.Video.Thumbnails.DATA };

        Cursor thumbnailCursor = null;
        try {
            thumbnailCursor = BaseApplication.getInstance().getContentResolver()
                    .query(thumbnailUri, thumbnailProjection, MediaStore.Video.Thumbnails.VIDEO_ID + " = ?",
                    new String[]{ videoId }, null );

            if (thumbnailCursor == null) {
                return thumbnailPath;
            }

            if (thumbnailCursor.moveToFirst()) {
                thumbnailPath = thumbnailCursor.getString(
                        thumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                DuboxLog.d(TAG, "thumbnail result " + " videoId : " + videoId + " thumbnailPath : " + thumbnailPath);
            }
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage());
        } finally {
            if (thumbnailCursor != null) {
                thumbnailCursor.close();
            }
        }

        return thumbnailPath;
    }
}
