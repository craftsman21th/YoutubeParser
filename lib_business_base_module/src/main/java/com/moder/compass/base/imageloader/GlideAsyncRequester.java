package com.moder.compass.base.imageloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.moder.compass.account.Account;
import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.glide.Glide;
import com.dubox.glide.Priority;
import com.dubox.glide.RequestBuilder;
import com.dubox.glide.load.engine.IPriority;
import com.dubox.glide.load.engine.executor.GlideExecutor;
import com.dubox.glide.request.RequestOptions;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

/**
 * Glide异步请求
 *
 * @author guanshuaichao
 * @since 2019/3/28
 */
class GlideAsyncRequester {

    private static final String TAG = "GlideAsyncRequester";

    public static final boolean ENABLE = true;

    private static final String PREFIX_HTTP = "http";
    private static final String PARAM_PATH = "path";
    private static final String PARAM_MD5 = "md5";
    private static final String PARAM_SIZE = "size";

    private Context mContext;
    private GlideHelper mGlideHelper;
    private ThumbnailHelper mThumbnailHelper;

    private Handler mUiHandler;

    /** Server path 和 url对应缓存表 */
    private LruCache<String, String> mPathUrlCache = new LruCache<>(100);
    private ThreadPoolExecutor mExecutorService;

    GlideAsyncRequester(Context context, GlideHelper glideHelper, ThumbnailHelper thumbnailHelper) {
        mContext = context;
        mGlideHelper = glideHelper;
        mThumbnailHelper = thumbnailHelper;
        mExecutorService = (ThreadPoolExecutor) GlideExecutor.newSourceExecutor().getExecutor();
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 线程池线程工厂，给线程标记一个名字，能够区分出来
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "GlideAsyncRequester #" + mCount.getAndIncrement());
        }
    };

    /**
     * 异步请求Glide
     */
    public void asyncRequest(Fragment fragment, String thumbnail,
                             SimpleFileInfo simpleFileInfo, ThumbnailSizeType thumbnailSizeType,
                             String cacheKey,
                             RequestOptions requestOptions, final ImageView imageView,
                             GlideLoadingListener listener) {
        clear(imageView);

        RequestInfo info = new RequestInfo();
        info.fragmentRef = new WeakReference<>(fragment);
        info.thumbnail = thumbnail;
        info.simpleFileInfo = simpleFileInfo;
        info.thumbnailSizeType = thumbnailSizeType;
        info.cacheKey = cacheKey;
        info.requestOptions = requestOptions;
        info.imageViewRef = new WeakReference<>(imageView);
        info.listener = listener;

        RequestRunnable requestRunnable = new RequestRunnable(info, mGlideHelper, mThumbnailHelper,
                mUiHandler, mContext, mPathUrlCache);

        imageView.setTag(R.id.time_line_image_tag_request, requestRunnable);

        mExecutorService.execute(requestRunnable);
    }

    /**
     * 异步请求Glide
     */
    public void asyncRequest(Fragment fragment, String thumbnail, final String url, String cacheKey,
                             RequestOptions requestOptions, final ImageView imageView,
                             GlideLoadingListener listener) {
        clear(imageView);

        Drawable loadingDrawable = requestOptions.getPlaceholderDrawable();
        if (loadingDrawable != null) {
            imageView.setImageDrawable(loadingDrawable);
        }

        RequestInfo info = new RequestInfo();
        info.fragmentRef = new WeakReference<>(fragment);
        info.thumbnail = thumbnail;
        info.url = url;
        info.cacheKey = cacheKey;
        info.requestOptions = requestOptions;
        info.imageViewRef = new WeakReference<>(imageView);
        info.listener = listener;
        RequestRunnable requestRunnable = new RequestRunnable(info, mGlideHelper, mThumbnailHelper,
                mUiHandler, mContext, mPathUrlCache);

        imageView.setTag(R.id.time_line_image_tag_request, requestRunnable);

        mExecutorService.execute(requestRunnable);
    }

    public static class RequestRunnable implements
            Runnable, Comparable<IPriority>, IPriority {

        public RequestInfo mRequestInfo;
        private GlideHelper mGlideHelper;
        private ThumbnailHelper mThumbnailHelper;

        private Handler mUiHandler;
        private Context mContext;
        private LruCache<String, String> mPathUrlCache;
        private boolean isCanceled = false;
        Priority priority = Priority.HIGH;

        public RequestRunnable(RequestInfo info, GlideHelper glideHelper,
                               ThumbnailHelper thumbnailHelper, Handler uiHandler,
                               Context context, LruCache<String, String> pathUrlCache) {
            mRequestInfo = info;
            mGlideHelper = glideHelper;
            mThumbnailHelper = thumbnailHelper;
            mUiHandler = uiHandler;
            mContext = context;
            mPathUrlCache = pathUrlCache;
        }

        @Override
        public void run() {
            requestInner(mRequestInfo);
        }

        public void cancel() {
            isCanceled = true;
        }

        private boolean isInValid() {
            boolean invaild = isCanceled
                    || mRequestInfo == null
                    || mRequestInfo.imageViewRef == null
                    || mRequestInfo.imageViewRef.get() == null;
            return invaild;
        }

        /**
         * 异步查询是否存在缓存
         */
        private void requestInner(final RequestInfo info) {
            if (isInValid()) {
                return;
            }


            if (info.simpleFileInfo != null
                    && TextUtils.isEmpty(info.url)
                    && !TextUtils.isEmpty(info.simpleFileInfo.mMd5)) {
                StringBuffer sb = new StringBuffer(info.simpleFileInfo.mMd5);
                sb.append(info.thumbnailSizeType)
                        .append(Account.INSTANCE.getUk());
                String key = sb.toString();
                info.url = mPathUrlCache.get(key);

                if (!TextUtils.isEmpty(info.url)) {
                    GlideImageSize glideImageSize = mThumbnailHelper.getImageSizeByType(info.thumbnailSizeType);
                    try {
                        if (glideImageSize != null && mGlideHelper.isInMemoryCache(
                                info.simpleFileInfo.mMd5,
                                glideImageSize.mWidth, glideImageSize.mHeight)) {
                            // 内存中的图片 直接请求glide 不走异步
                            mUiHandler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    if (isInValid()) {
                                        return;
                                    }
                                    directRequest(info, info.imageViewRef.get());
                                }
                            });
                            return;
                        }
                    } catch (Exception e) {
                        DuboxLog.e(TAG, e.getMessage(), e);
                    }
                }
            }

            if (TextUtils.isEmpty(info.url)
                    && info.simpleFileInfo != null
                    && info.thumbnailSizeType != null) {
                info.url = mThumbnailHelper.makeRemoteUrlByPath(info.simpleFileInfo, info.thumbnailSizeType);
                if (!TextUtils.isEmpty(info.simpleFileInfo.mMd5) && !TextUtils.isEmpty(info.url)) {
                    String key = info.simpleFileInfo.mMd5 + info.thumbnailSizeType
                            + Account.INSTANCE.getUk();
                    mPathUrlCache.put(key, info.url);
                }
            }

            if (info.simpleFileInfo != null && info.simpleFileInfo.mMd5 != null) {

                File file = GlideHelper.getInstance().getDiskCacheFileByUrl(info.simpleFileInfo.mMd5 + "6464");
                if (file != null && file.exists()) {
                    Drawable drawable = BitmapDrawable.createFromPath(file.getPath());
                    if (drawable != null) {
                        info.requestOptions.placeholder(drawable);
                        info.requestOptions.thumbnailPlaceholder(true);
                    }
                }
            }

            final String url = info.url;
            String thumbnail = info.thumbnail;
            String cacheKey = info.cacheKey;
            Fragment fragment = info.fragmentRef.get();
            RequestOptions requestOptions = info.requestOptions;
            GlideLoadingListener listener = info.listener;

            if (isInValid()) {
                return;
            }

            final GlideDrawableImageViewTarget glideDrawableImageViewTarget =
                    new GlideDrawableImageViewTarget(mRequestInfo.imageViewRef.get(), listener);
            glideDrawableImageViewTarget.setPositionTagId(R.id.time_line_image_tag_position);
            // 获取缓存文件
            File cacheFile = null;
            if (!TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
                cacheFile = mGlideHelper.getDiskCacheFileByUrl(url);
            }

            // 原始尺寸缓存获取失败
            File glideFile = null;
            if (cacheFile == null && !TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
                String serverPath = Uri.parse(url).getQueryParameter(PARAM_PATH);
                String md5 = Uri.parse(url).getQueryParameter(PARAM_MD5);
                String imageSize = Uri.parse(url).getQueryParameter(PARAM_SIZE);
                String fullScreenSize = mThumbnailHelper.calUrlDimension(ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE);
                String gridScreenSize = mThumbnailHelper.calUrlDimension(ThumbnailSizeType.THUMBNAIL_SIZE_96);

                // 尝试获取无尺寸的图片缓存
                if (!TextUtils.isEmpty(imageSize) && !imageSize.equalsIgnoreCase(fullScreenSize)
                        && !TextUtils.isEmpty(serverPath)) {
                    String cacheUrl = mThumbnailHelper.makeRemoteUrlByPath(serverPath);
                    DuboxLog.i(TAG, "glideFile get cacheUrl = " + cacheUrl);
                    glideFile = mGlideHelper.getDiskCacheFileByUrl(cacheUrl);
                }

                // 当未命中文件，并且请求缩略图为空，缓存找到对应64*64缩略图，则添加到thumbnail中
                if (glideFile == null && !TextUtils.isEmpty(serverPath)
                        && !TextUtils.isEmpty(imageSize)
                        && imageSize.equalsIgnoreCase(gridScreenSize)
                        && TextUtils.isEmpty(thumbnail)) {
                    String thumbnailUrl = mGlideHelper.generateUrlFromPath(
                            new SimpleFileInfo(serverPath, md5), ThumbnailSizeType.THUMBNAIL_FULL_PRELOAD_SIZE_64);
                    File thumbnailFile = mGlideHelper.getDiskCacheFileByUrl(thumbnailUrl);
                    if (thumbnailFile != null && thumbnailFile.exists()) {
                        thumbnail = thumbnailFile.getAbsolutePath();
                        DuboxLog.d(TAG, "add thumbnail = " + thumbnail + " url = " + url);
                    }
                }
            }

            if (isInValid()) {
                return;
            }

            final boolean isFromLocalThumbnail = (glideFile != null);

            OnceReportListener requestListener = new OnceReportListener(mRequestInfo.imageViewRef.get(),
                    url, thumbnail, isFromLocalThumbnail);

            CustomGlideUrl glideUrl = null;
            if (!TextUtils.isEmpty(cacheKey) && cacheKey.startsWith(PREFIX_HTTP)) {
                glideUrl = new CustomGlideUrl(url, cacheKey);
            }

            final RequestBuilder requestBuilder;
            if (fragment == null || fragment.getActivity() == null
                    || fragment.getActivity().isFinishing()) {
                if (cacheFile != null) {
                    requestBuilder = Glide.with(mContext).load(cacheFile);
                    DuboxLog.i(TAG, "cacheFile get url = " + url);
                } else if (glideFile != null) {
                    requestBuilder = Glide.with(mContext).load(glideFile);
                    DuboxLog.i(TAG, "glideFile get url = " + url);
                } else {
                    requestBuilder = Glide.with(mContext).load(glideUrl != null ? glideUrl : url);
                }

                if (!TextUtils.isEmpty(thumbnail)) {
                    RequestOptions thumbRequestOptions = new RequestOptions();
                    thumbRequestOptions.apply(mGlideHelper.setRequestSize(thumbnail, requestOptions));
                    requestBuilder.thumbnail(Glide.with(mContext).load(thumbnail).apply(thumbRequestOptions)
                            .listener(requestListener.mThumbnailRequestListener));
                }
            } else {
                if (cacheFile != null) {
                    requestBuilder = Glide.with(fragment).load(cacheFile);
                } else if (glideFile != null) {
                    requestBuilder = Glide.with(fragment).load(glideFile);
                } else {
                    requestBuilder = Glide.with(fragment).load(glideUrl != null ? glideUrl : url);
                }

                if (!TextUtils.isEmpty(thumbnail)) {
                    RequestOptions thumbRequestOptions = new RequestOptions();
                    thumbRequestOptions.apply(mGlideHelper.setRequestSize(thumbnail, requestOptions));
                    requestBuilder.thumbnail(
                            Glide.with(fragment).load(thumbnail).apply(thumbRequestOptions)
                                    .listener(requestListener.mThumbnailRequestListener));
                }
            }
            requestOptions = mGlideHelper.setRequestSize(url, requestOptions)
                    .priority(Priority.NORMAL);
            try {
                requestBuilder.apply(requestOptions)
                        .listener(requestListener.mRequestListener);
            } catch (Exception e) {
                DuboxLog.e(TAG, e.getMessage());
                return;
            }

            // 已被取消请求
            if (isInValid()) {
                return;
            }

            // Glide 需要在UI线程调用into
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 已被取消请求
                        if (isInValid()) {
                            return;
                        }
                        requestBuilder.into(glideDrawableImageViewTarget);
                    } catch (Exception e) {
                        DuboxLog.e(TAG, e.getMessage());
                    }
                }
            });
        }

        private void directRequest(RequestInfo info, ImageView imageView) {
            String url = info.url;
            String thumbnail = info.thumbnail;
            Fragment fragment = info.fragmentRef.get();
            RequestOptions requestOptions = info.requestOptions;
            GlideLoadingListener listener = info.listener;

            GlideDrawableImageViewTarget glideDrawableImageViewTarget =
                    new GlideDrawableImageViewTarget(imageView, listener);
            glideDrawableImageViewTarget.setPositionTagId(R.id.time_line_image_tag_position);

            OnceReportListener requestListener = new OnceReportListener(imageView, url, thumbnail, true);

            RequestBuilder requestBuilder;

            if (fragment == null || fragment.getActivity() == null
                    || fragment.getActivity().isFinishing()) {
                requestBuilder = Glide.with(mContext).load(url);
            } else {
                requestBuilder = Glide.with(fragment).load(url);
            }

            requestOptions = mGlideHelper.setRequestSize(url, requestOptions);

            try {
                requestBuilder.apply(requestOptions)
                        .listener(requestListener.mRequestListener)
                        .into(glideDrawableImageViewTarget);
            } catch (Exception e) {
                DuboxLog.e(TAG, e.getMessage());
            }

        }

        @Override
        public int compareTo(@NonNull IPriority other) {
            try {
                int result = getPriority() - other.getPriority();
                return result;
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public int getPriority() {
            return priority.ordinal();
        }
    }


    /**
     * 从请求队列移除
     *
     * @param imageView com.dubox.drive.preview.image view
     * @return 移除是否成功
     */
    public boolean clear(ImageView imageView) {
        boolean ret = true;
        if (imageView != null) {
            RequestRunnable requestRunnable = (RequestRunnable) imageView.getTag(R.id.time_line_image_tag_request);
            if (requestRunnable != null) {
                requestRunnable.cancel();
            }
        }
        return ret;
    }

    private class RequestInfo {
        WeakReference<Fragment> fragmentRef;
        String thumbnail;
        SimpleFileInfo simpleFileInfo;
        ThumbnailSizeType thumbnailSizeType;
        String url;
        String cacheKey;
        RequestOptions requestOptions;
        WeakReference<ImageView> imageViewRef;
        GlideLoadingListener listener;
    }

}
