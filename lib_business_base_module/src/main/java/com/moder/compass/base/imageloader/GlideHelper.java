package com.moder.compass.base.imageloader;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.moder.compass.BaseApplication;
import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.dubox.glide.Glide;
import com.dubox.glide.Priority;
import com.dubox.glide.RequestBuilder;
import com.dubox.glide.load.DataSource;
import com.dubox.glide.load.DecodeFormat;
import com.dubox.glide.load.engine.DataCacheKey;
import com.dubox.glide.load.engine.DiskCacheStrategy;
import com.dubox.glide.load.engine.GlideException;
import com.dubox.glide.load.engine.ResourceCacheKey;
import com.dubox.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.dubox.glide.load.engine.cache.MemorySizeCalculator;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.load.resource.gif.GifOptions;
import com.dubox.glide.request.Request;
import com.dubox.glide.request.RequestListener;
import com.dubox.glide.request.RequestOptions;
import com.dubox.glide.request.SingleRequest;
import com.dubox.glide.request.target.SimpleTarget;
import com.dubox.glide.request.target.Target;
import com.dubox.glide.request.transition.Transition;
import com.dubox.glide.signature.EmptySignature;

import java.io.File;
import java.util.List;
/**
 * Created by panchao02 on 2018/9/2.
 */

public class GlideHelper implements IGlidePreLoadListener {

    private static final String TAG = "GlideHelper";
    /**
     * gif 图的标记
     */
    public static final String GIF_TAG = "gif";

    /**
     * 缩略图帮助类
     *
     * @author 孙奇 V 1.0.0 Create at 2012-12-4 上午12:13:09
     */
    private ThumbnailHelper thumbnailHelper;

    /**
     * 图片缓存任务管理manager
     */
    private GlidePreLoadManager mPreLoadManager;

    private static final String IMAGE_CATEGORY_SMALL = "small";
    private static final String IMAGE_CATEGORY_BIG = "big";
    private static final String IMAGE_CATEGORY_SIZE800 = "size800";
    private static final String PREFIX_HTTP = "http";
    private static final String PARAM_PATH = "path";
    private static final String PARAM_MD5 = "md5";
    private static final String PARAM_SIZE = "size";
    private static int mLoadCount = 0;
    private static String mImageCategory = IMAGE_CATEGORY_SMALL;
    private static Boolean mIsCached = false;
    private static long mLoadTime = 0L;
    private static long mDispaterTime = 0L;
    private static String mNetworkType = ConnectivityState.NETWORK_CLASS_WIFI;
    private static boolean mFromPreLoad;
    private static boolean mFromLocalThumbnail;
    private volatile static GlideHelper instance;
    private Context mContext;

    private static final int LOW_MEMORY_BYTE_ARRAY_POOL_DIVISOR = 2;
    private static final int ARRAY_POOL_SIZE_BYTES = 4 * 1024 * 1024;

    private GlideAsyncRequester mAsyncRequester;

    public static GlideHelper getInstance() {
        if (instance == null) {
            synchronized (GlideHelper.class) {
                if (instance == null) {
                    instance = new GlideHelper();
                }
            }
        }
        return instance;
    }

    private GlideHelper() {
        mContext = BaseApplication.getContext();
        if (thumbnailHelper == null) {
            thumbnailHelper = new ThumbnailHelper(mContext);
        }
        mPreLoadManager = new GlidePreLoadManager(mContext, thumbnailHelper);
        mAsyncRequester = new GlideAsyncRequester(mContext, this, thumbnailHelper);
    }

    /**
     * Clear memory cache.<br />
     * Do nothing if {@link # init(long, Context)} method wasn't called before.
     */
    public void clearMemoryCache() {
        try {
            Glide.get(mContext).clearMemory();
        } catch (Exception e) {
            DuboxLog.e(TAG, "clearMemoryCache e = " + e.toString());
        }
    }

    public ThumbnailHelper getThumbnailHelper() {
        return thumbnailHelper;
    }

    public void displayImage(String path, int loadingImageRes, int emptyImageRes, int errorImageRes, boolean cacheable,
                             ThumbnailSizeType thumbnailSizeType, ImageView imageView, GlideLoadingListener listener) {
        displayImage(new SimpleFileInfo(path, ""), loadingImageRes,
                emptyImageRes, errorImageRes, cacheable, thumbnailSizeType, imageView, listener);
    }

    public void displayImage(SimpleFileInfo simpleImageFile, int loadingImageRes,
                             int emptyImageRes, int errorImageRes, boolean cacheable,
                             ThumbnailSizeType thumbnailSizeType, ImageView imageView, GlideLoadingListener listener) {
        String url = thumbnailHelper.makeRemoteUrlByPath(simpleImageFile, thumbnailSizeType);
        displayImageFromNetwork(url, loadingImageRes, emptyImageRes, errorImageRes, cacheable, imageView, listener);
    }

    public void displayImage(SimpleFileInfo file, Drawable drawableLoading,
                             Drawable drawableEmpty, Drawable drawableError,
                             boolean cacheable, ThumbnailSizeType thumbnailSizeType, ImageView imageView,
                             GlideLoadingListener listener) {

        final String url = thumbnailHelper.makeRemoteUrlByPath(file, thumbnailSizeType);
        displayImageFromNetwork(url, drawableLoading, drawableEmpty, drawableError, cacheable, imageView, listener);
    }
    /**
     * @param drawableLoading
     * @param drawableEmpty
     * @param drawableError
     * @param cacheable
     * @param thumbnailSizeType
     * @param imageView
     * @param listener
     * @since 8.3 自定义BitmapDisplayer 将LoadedFrom信息设置为ImageView的Tag
     * 用于8.3统计上报云图首页加载时间（区分是本地加载还是网络加载）
     */
    public void displayImageWithLoadFromInfo(Fragment fragment, SimpleFileInfo file, Drawable drawableLoading,
                                             boolean isThumbnailDrawable,
                                             Drawable drawableEmpty,
                                             Drawable drawableError, boolean cacheable,
                                             ThumbnailSizeType thumbnailSizeType, final ImageView imageView,
                                             GlideLoadingListener listener) {
        String url = null;
        String thumbUrl = null;

        if (!GlideAsyncRequester.ENABLE) {
            url = thumbnailHelper.makeRemoteUrlByPath(file, thumbnailSizeType);
        }

        RequestOptions requestOptions = new RequestOptions();
        if (drawableLoading != null) {
            requestOptions.placeholder(drawableLoading);
        }
        if (drawableEmpty != null) {
            requestOptions.fallback(drawableEmpty);
        }
        if (drawableError != null) {
            requestOptions.error(drawableError);
        }
        requestOptions.thumbnailPlaceholder(isThumbnailDrawable);
        requestOptions
                .skipMemoryCache(!cacheable)
                .diskCacheStrategy(cacheable ? DiskCacheStrategy.DATA : DiskCacheStrategy.NONE);

        if (GlideAsyncRequester.ENABLE && TextUtils.isEmpty(url)) {
            mAsyncRequester.asyncRequest(fragment, thumbUrl, file, thumbnailSizeType,
                    null, requestOptions, imageView, listener);
        } else {
            beginGlideLoaderRequestOptions(fragment, thumbUrl, url,
                    null, requestOptions, imageView, listener);
        }
    }

    /**
     * 异步显示网络图片，默认缓存到内存中
     *
     * @param url
     * @param stubImageRes
     * @param cacheable
     * @param imageView
     * @param listener
     */
    public void displayImage(String url, int stubImageRes, boolean cacheable, final ImageView imageView,
                             GlideLoadingListener listener) {
        beginGlideLoader(url, null, stubImageRes, -1, -1, cacheable, imageView, listener);
    }

    public void displayImage(String uri, final ImageView imageView,
                             GlideLoadingListener listener) {
        RequestOptions newRequestOptions = new RequestOptions();
        beginGlideLoaderRequestOptions(null, uri, newRequestOptions, imageView, listener);
    }

    public void displayImage(String uri, final ImageView imageView, RequestOptions options,
                             GlideLoadingListener listener) {
        RequestOptions newRequestOptions = new RequestOptions();
        if (options != null) {
            newRequestOptions = options;
        }

        beginGlideLoaderRequestOptions(null, uri, newRequestOptions, imageView, listener);
    }

    public void displayImage(String uri, String thumbnail, final ImageView imageView, RequestOptions options,
                             GlideLoadingListener listener) {
        RequestOptions newRequestOptions = new RequestOptions();
        if (options != null) {
            newRequestOptions = options;
        }

        beginGlideLoaderRequestOptions(null, thumbnail, uri, null, newRequestOptions, imageView, listener);
    }


    public void displayImage(String uri, RequestOptions options,
                             int width, int height) {
        RequestOptions newRequestOptions = new RequestOptions();
        if (options != null) {
            newRequestOptions = options;
        }
        newRequestOptions.priority(Priority.LOW);
        RequestBuilder requestBuilder = Glide.with(mContext).load(uri);
        final String finalUri = uri;
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                loadImageLoadFail();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (target != null) {
                    reportImageLoad(finalUri, target.getRequest(), dataSource, true, false);
                }
                return false;
            }
        };
        requestBuilder.apply(newRequestOptions).listener(requestListener).preload(width, height);
    }

    public void displayImage(String uri, String thumbnail, String cacheKey, final ImageView imageView,
                             RequestOptions options, GlideLoadingListener listener) {
        RequestOptions newRequestOptions = new RequestOptions();
        if (options != null) {
            newRequestOptions = options;
        }

        beginGlideLoaderRequestOptions(null, thumbnail, uri, cacheKey, newRequestOptions, imageView, listener);
    }

    /**
     * 异步加载图片，with discCache
     *
     * @param url
     * @param stubImageRes
     * @param emptyImageRes
     * @param errorImageRes
     * @param cacheable
     * @param imageView
     * @param listener
     */
    public void displayImageFromNetwork(String url, int stubImageRes, int emptyImageRes, int errorImageRes,
                                        boolean cacheable, ImageView imageView, GlideLoadingListener listener) {
        beginGlideLoader(url, null, stubImageRes, emptyImageRes, errorImageRes, cacheable, imageView, listener);
    }

    /**
     * 异步加载图片，with discCache
     *
     * @param url
     * @param drawableLoading
     * @param drawableEmpty
     * @param drawableError
     * @param cacheable
     * @param imageView
     * @param listener
     */
    public void displayImageFromNetwork(String url, Drawable drawableLoading, Drawable drawableEmpty,
                                        Drawable drawableError, boolean cacheable, ImageView imageView, GlideLoadingListener listener) {

        beginGlideLoader(null, url, drawableLoading, drawableEmpty, drawableError, cacheable, imageView, listener);
    }

    public void displayImageFromNetwork(String url, String cacheKey, int stubImageRes, int emptyImageRes,
                                        int errorImageRes, boolean cacheable, ImageView imageView,
                                        GlideLoadingListener listener) {
        beginGlideLoader(url, cacheKey, stubImageRes, emptyImageRes, errorImageRes, cacheable, imageView, listener);
    }

    /**
     * 异步加载图片，without discCache
     *
     * @param path
     * @param imageView
     * @param emptyImageRes
     * @param listener
     * @author 孙奇 V 1.0.0 Create at 2012-12-4 下午09:24:43
     */
    public void displayImageFromFile(String path, ImageView imageView, int emptyImageRes,
                                     GlideLoadingListener listener) {

        final String url = ThumbnailHelper.makeLocalUrlByPath(path);

        beginGlideLoader(url, null, -1, emptyImageRes, -1,
                true, false, imageView, listener);
    }

    public void displayImageFromFile(String path, ImageView imageView, int loadingImageRes,
                                     int emptyImageRes, int errorImageRes,
                                     GlideLoadingListener listener) {

        final String url = ThumbnailHelper.makeLocalUrlByPath(path);

        beginGlideLoader(url, null, loadingImageRes, emptyImageRes, errorImageRes,
                true, false, imageView, listener);
    }

    public void displayImageFromDrawable(int imageRes, final ImageView imageView) {
        if (imageRes > 0) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.icon_list_folder_n);
            Glide.with(mContext).load(imageRes).apply(requestOptions).into(imageView);
        }
    }

    public void displayImageFromLocalFile(String url, RequestOptions requestOptions,
                                          final ImageView imageView, GlideLoadingListener listener) {

        beginGlideLoaderRequestOptions(null, null, url, null,
                requestOptions, imageView, listener);
    }

    private void beginGlideLoader(Fragment fragment, String url, Drawable drawableLoading, Drawable drawableEmpty,
                                  Drawable drawableError, boolean cacheable, final ImageView imageView,
                                  GlideLoadingListener listener) {
        beginGlideLoader(fragment, url, drawableLoading, drawableEmpty, drawableError, cacheable, cacheable, imageView, listener);
    }

    private void beginGlideLoader(Fragment fragment, String url, Drawable drawableLoading, Drawable drawableEmpty,
                                  Drawable drawableError, boolean memCacheable, boolean diskCacheable, final ImageView imageView,
                                  GlideLoadingListener listener) {
        RequestOptions requestOptions = new RequestOptions();
        if (drawableLoading != null) {
            requestOptions.placeholder(drawableLoading);
        }
        if (drawableEmpty != null) {
            requestOptions.fallback(drawableEmpty);
        }
        if (drawableError != null) {
            requestOptions.error(drawableError);
        }
        requestOptions
                .skipMemoryCache(!memCacheable)
                .diskCacheStrategy(diskCacheable ? DiskCacheStrategy.DATA : DiskCacheStrategy.NONE);
        beginGlideLoaderRequestOptions(fragment, url, requestOptions, imageView, listener);
    }

    private void beginGlideLoader(String url, String cacheKey, int drawableLoading, int drawableEmpty,
                                  int drawableError, boolean cacheable, final ImageView imageView,
                                  GlideLoadingListener listener) {
        beginGlideLoader(url, cacheKey, drawableLoading, drawableEmpty, drawableError, cacheable,
                cacheable, imageView, listener);
    }

    public void beginGlideLoader(String url, String cacheKey, int drawableLoading, int drawableEmpty,
                                 int drawableError, boolean memCacheable, boolean diskCacheable, final ImageView imageView,
                                 GlideLoadingListener listener) {
        RequestOptions requestOptions = new RequestOptions();
        if (drawableLoading > 0) {
            requestOptions.placeholder(drawableLoading);
        }
        if (drawableEmpty > 0) {
            requestOptions.fallback(drawableEmpty);
        }
        if (drawableError > 0) {
            requestOptions.error(drawableError);
        }
        requestOptions
                .skipMemoryCache(!memCacheable)
                .diskCacheStrategy(diskCacheable ? DiskCacheStrategy.DATA : DiskCacheStrategy.NONE);
        beginGlideLoaderRequestOptions(null, null, url, cacheKey,
                requestOptions, imageView, listener);
    }


    public void beginGlideLoaderRequestOptions(Fragment fragment, String url, RequestOptions requestOptions,
                                               final ImageView imageView, GlideLoadingListener listener) {
        beginGlideLoaderRequestOptions(fragment, null, url, null, requestOptions, imageView, listener);
    }

    public void beginGlideLoaderRequestOptions(Fragment fragment, String thumbnail, final String url, String cacheKey,
                                               RequestOptions requestOptions, final ImageView imageView,
                                               GlideLoadingListener listener) {

        if (imageView == null || mContext == null) {
            return;
        }
        final GlideDrawableImageViewTarget glideDrawableImageViewTarget =
                new GlideDrawableImageViewTarget(imageView, listener);

        File cacheFile = null;
        if (!TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
            cacheFile = getDiskCacheFileByUrl(url);
        }

        File glideFile = null;
        if (cacheFile == null && !TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
            String serverPath = Uri.parse(url).getQueryParameter(PARAM_PATH);
            String md5 = Uri.parse(url).getQueryParameter(PARAM_MD5);
            String imageSize = Uri.parse(url).getQueryParameter(PARAM_SIZE);
            String fullScreenSize = thumbnailHelper.calUrlDimension(ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE);
            String gridScreenSize = thumbnailHelper.calUrlDimension(ThumbnailSizeType.THUMBNAIL_SIZE_96);

            if (!TextUtils.isEmpty(imageSize) && !imageSize.equalsIgnoreCase(fullScreenSize)
                    && !TextUtils.isEmpty(serverPath)) {
                String cacheUrl = thumbnailHelper.makeRemoteUrlByPath(serverPath);
//                DuboxLog.i(TAG, "glideFile get cacheUrl = " + cacheUrl);
                glideFile = getDiskCacheFileByUrl(cacheUrl);
            }

            // 当未命中文件，并且请求缩略图为空，缓存找到对应21*21缩略图，则添加到thumbnail中
            if (glideFile == null && !TextUtils.isEmpty(serverPath)
                    && imageSize.equalsIgnoreCase(gridScreenSize)
                    && TextUtils.isEmpty(thumbnail)) {
                String thumbnailUrl = generateUrlFromPath(
                        new SimpleFileInfo(serverPath, md5), ThumbnailSizeType.THUMBNAIL_FULL_PRELOAD_SIZE_64);
                File thumbnailFile = getDiskCacheFileByUrl(thumbnailUrl);
                if (thumbnailFile != null && thumbnailFile.exists()) {
                    thumbnail = thumbnailFile.getAbsolutePath();
                    //DuboxLog.d(TAG, "add thumbnail = " + thumbnail + " url = " + url);
                }
            }
        }

        final boolean isFromLocalThumbnail = (glideFile != null);

        OnceReportListener requestListener = new OnceReportListener(imageView, url, thumbnail, isFromLocalThumbnail);

        CustomGlideUrl glideUrl = null;
        if (!TextUtils.isEmpty(cacheKey) && cacheKey.startsWith(PREFIX_HTTP)) {
            glideUrl = new CustomGlideUrl(url, cacheKey);
        }

        RequestBuilder requestBuilder;
        if (fragment == null || fragment.getActivity() == null
                || fragment.getActivity().isFinishing()) {
            if (cacheFile != null) {
                requestBuilder = Glide.with(mContext).load(cacheFile);
                //DuboxLog.i(TAG, "cacheFile get url = " + url);
            } else if (glideFile != null) {
                requestBuilder = Glide.with(mContext).load(glideFile);
                //DuboxLog.i(TAG, "glideFile get url = " + url);
            } else {
                if (!TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
                    requestBuilder = Glide.with(mContext).load(glideUrl != null ? glideUrl : new GlideUrl(url));
                } else {
                    requestBuilder = Glide.with(mContext).load(glideUrl != null ? glideUrl : url);
                }
            }

            if (!TextUtils.isEmpty(thumbnail)) {
                RequestOptions thumbRequestOptions = new RequestOptions();
                thumbRequestOptions.apply(setRequestSize(thumbnail, requestOptions));
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
                thumbRequestOptions.apply(setRequestSize(thumbnail, requestOptions));
                requestBuilder.thumbnail(
                        Glide.with(fragment).load(thumbnail).apply(thumbRequestOptions)
                                .listener(requestListener.mThumbnailRequestListener));
            }
        }
        requestOptions = setRequestSize(url, requestOptions);
        try {
            requestBuilder.apply(requestOptions)
                    .listener(requestListener.mRequestListener)
                    .into(glideDrawableImageViewTarget);
        } catch (Exception e) {
            DuboxLog.e(TAG, e.getMessage());
        }
    }

    RequestOptions setRequestSize(final String url, RequestOptions requestOptions) {
        // 如果传入options参数，则采用传入的options
        if (requestOptions.getOverrideWidth() > -1) {
            return requestOptions;
        }
        GlideImageSize glideImageSize = new GlideImageSize(300, 300);
        String md5 = null;
        if (!TextUtils.isEmpty(url) && url.startsWith(PREFIX_HTTP)) {
            String imageSize = Uri.parse(url).getQueryParameter(PARAM_SIZE);
            md5 = Uri.parse(url).getQueryParameter(PARAM_MD5);
            if (!TextUtils.isEmpty(imageSize)) {
                glideImageSize = thumbnailHelper.getGlideImageSize(imageSize);
            }
        }
        if (glideImageSize != null) {
            requestOptions.override(glideImageSize.mWidth, glideImageSize.mHeight);
//            DuboxLog.i(TAG, "glideImageSize.mWidth = " + glideImageSize.mWidth
//                    + " glideImageSize.mHeight = " + glideImageSize.mHeight);
        }
        requestOptions.encodeQuality(50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestOptions.encodeFormat(Bitmap.CompressFormat.WEBP_LOSSY);
        }
        if (!TextUtils.isEmpty(md5) && !md5.equals("-1")) {
            requestOptions.md5(md5);
        }
        return requestOptions;
    }

    public void reportImageLoad() {
        if (mLoadCount > 0) {
            // 先上报
            StatisticsLogForMutilFields.getInstance().updateCount(StatisticsLogForMutilFields
                            .StatisticsKeys.LOAD_IMAGE, true, mImageCategory, String.valueOf(mIsCached),
                    String.valueOf(mLoadCount), String.valueOf(mLoadTime), mNetworkType.toUpperCase(),
                    String.valueOf(mFromPreLoad), String.valueOf(mFromLocalThumbnail)
                            + SPLITER + String.valueOf(mDispaterTime));
        }
        mLoadCount = 0;
        mLoadTime = 0;
        mDispaterTime = 0;
    }

    private static final String SPLITER = "&";

    /**
     * 上报图片加载时间
     *
     * @param width
     * @param height
     * @param isCached
     * @param loadTime
     * @param fromPreLoad
     * @param fromLocalThumbnail
     * @since 8.5.0
     */
    public void reportImageLoad(int width, int height, boolean isCached, long loadTime,
                                boolean fromPreLoad, boolean fromLocalThumbnail, long dispaterTime) {
        String imageCategory;
        if (width >= DeviceDisplayUtils.getScreenWidth() && height >= DeviceDisplayUtils.getScreenHeight()) {
            imageCategory = IMAGE_CATEGORY_BIG;
        } else if (width == 800 && height == 800) {
            imageCategory = IMAGE_CATEGORY_SIZE800;
        } else {
            imageCategory = IMAGE_CATEGORY_SMALL;
        }

        // 网络状态从缓存中获取
        // String networkType = ConnectivityState.getNetworkClass(BaseApplication.getInstance());
        String networkType = GlideNetworkHelper.getInstance().getNetworkClass();
        if (TextUtils.isEmpty(networkType)) {
            networkType = ConnectivityState.NETWORK_CLASS_WIFI;
        }

//        DuboxLog.i(TAG, "fromPreLoad = " + fromPreLoad + " isCached = "
//                + isCached + " imageCategory = " + imageCategory);
        // 图片类型，是否命中缓存，网络类型和加载类型（正常加载、预加载、本地缩略图加载）任何一种条件变了则直接上报统计，否则累加count和time
        if (imageCategory.equals(mImageCategory) && (isCached && mIsCached || (!isCached && !mIsCached))
                && networkType.equals(mNetworkType) && (fromPreLoad && mFromPreLoad
                || (!fromPreLoad && !mFromPreLoad)) && (fromLocalThumbnail && mFromLocalThumbnail
                || (!fromLocalThumbnail && !mFromLocalThumbnail))) {
            mLoadTime += loadTime;
            mDispaterTime += dispaterTime;
            mLoadCount++;
        } else {
            if (mLoadCount > 0) {
                // 先上报
                StatisticsLogForMutilFields.getInstance().updateCount(StatisticsLogForMutilFields
                                .StatisticsKeys.LOAD_IMAGE, true, mImageCategory, String.valueOf(mIsCached),
                        String.valueOf(mLoadCount), String.valueOf(mLoadTime), mNetworkType.toUpperCase(),
                        String.valueOf(mFromPreLoad), String.valueOf(mFromLocalThumbnail)
                                + SPLITER + String.valueOf(mDispaterTime));
            }

            // 从新开始记录
            mImageCategory = imageCategory;
            mIsCached = isCached;
            mLoadCount = 1;
            mLoadTime = loadTime;
            mDispaterTime = dispaterTime;
            mNetworkType = networkType;
            mFromPreLoad = fromPreLoad;
            mFromLocalThumbnail = fromLocalThumbnail;
        }
    }

    public void deleteAllCacheByPath(String path) {
        deleteCacheByPath(path, ThumbnailSizeType.THUMBNAIL_SIZE_48);
        deleteCacheByPath(path, ThumbnailSizeType.THUMBNAIL_SIZE_96);
        deleteCacheByPath(path, ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE);
    }

    // 没有必要删除缓存
    public void deleteCacheByPath(String path, ThumbnailSizeType thumbnailSizeType) {
        final String url = thumbnailHelper.makeRemoteUrlByPath(new SimpleFileInfo(path, ""), thumbnailSizeType);
//        DiscCacheUtil.removeFromCache(url, ImageLoader.getInstance().getDiscCache());
//        MemoryCacheUtil.removeFromCache(url, ImageLoader.getInstance().getMemoryCache());
    }

    @Override
    public void startPreLoad(boolean isWifiConnected) {
        if (mPreLoadManager != null) {
            mPreLoadManager.startPreLoad(isWifiConnected);
        }
    }

    @Override
    public void pausePreLoad() {
        if (mPreLoadManager != null) {
            mPreLoadManager.pausePreLoad();
        }
    }

    @Override
    public void resumePreLoad(boolean isWifiConnected) {
        if (mPreLoadManager != null) {
            mPreLoadManager.resumePreLoad(isWifiConnected);
        }
    }

    @Override
    public void stopPreLoad() {
        if (mPreLoadManager != null) {
            mPreLoadManager.stopPreLoad();
        }
    }

    public void handleNetWorkChange(boolean isNetworkConnected, boolean isWifiConnected) {
        if (isNetworkConnected && isWifiConnected) {
            resumePreLoad(isWifiConnected);
        } else {
            pausePreLoad();
        }
    }

    @Override
    public String generateUrlFromPath(SimpleFileInfo file, ThumbnailSizeType type) {
        if (mPreLoadManager != null) {
            return mPreLoadManager.generateUrlFromPath(file, type);
        }
        return null;
    }

    @Override
    public void addPreLoadTask(Fragment fragment, SimpleFileInfo file, ThumbnailSizeType type) {
        final String url = thumbnailHelper.makeRemoteUrlByPath(file, type);

        addPreLoadTaskByUrl(fragment, url, type);
    }

    @Override
    public void addPreLoadTasks(Fragment fragment, List<SimpleFileInfo> files, ThumbnailSizeType type) {
        if (mPreLoadManager != null) {
            mPreLoadManager.addPreLoadTasks(fragment, files, type);
        }
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type) {
        if (mPreLoadManager != null) {
            mPreLoadManager.addPreLoadTaskByUrl(fragment, url, type);
        }
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey,
                                    RequestOptions options, GlideImageSize glideImageSize,
                                    IImagePreLoadTask.PreLoadResultListener stateListener) {
        if (mPreLoadManager != null) {
            mPreLoadManager.addPreLoadTaskByUrl(fragment, url, cacheKey, options, glideImageSize, stateListener);
        }
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, String cacheKey) {
        if (mPreLoadManager != null) {
            mPreLoadManager.addPreLoadTaskByUrl(fragment, url, cacheKey);
        }
    }

    @Override
    public void addPreLoadTaskByUrl(Fragment fragment, String url, ThumbnailSizeType type,
                                    IImagePreLoadTask.PreLoadResultListener listener) {
        if (mPreLoadManager != null) {
            mPreLoadManager.addPreLoadTaskByUrl(fragment, url, type, listener);
        }
    }

    @Override
    public void addPreLoadTaskByUrls(Fragment fragment, List<String> urls, ThumbnailSizeType type) {
        mPreLoadManager.addPreLoadTaskByUrls(fragment, urls, type);
    }

    @Override
    public void addPreLoadTaskByParent(Fragment fragment, ThumbnailSizeType type, PreLoadExtraParams params) {
        mPreLoadManager.addPreLoadTaskByParent(fragment, type, params);
    }

    @Override
    public void addPreLoadTaskByUrl(byte[] bytes, ThumbnailSizeType type, String md5) {
        mPreLoadManager.addPreLoadTaskByUrl(bytes, type, md5);
    }

    public void displayImage(final String url, ImageView imageView) {
        displayImage(url, imageView, false);
    }

    public void displayImage(final String url, ImageView imageView, boolean centerCrop) {
        displayImage(url, 0, imageView, centerCrop);
    }

    public void displayImage(final String url, int defaultResId, ImageView imageView, boolean centerCrop) {
        if (imageView == null) {
            return;
        }
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                loadImageLoadFail();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                reportImageLoad(url, target.getRequest(), dataSource, false, false);
                return false;
            }
        };
        RequestOptions requestOptions = new RequestOptions();
        if (centerCrop) {
            requestOptions = requestOptions.centerCrop();
        }
        if (defaultResId > 0) {
            requestOptions.placeholder(defaultResId)
                    .error(defaultResId)
                    .fallback(defaultResId);
        }
        Glide.with(mContext).load(url).apply(requestOptions)
                .listener(requestListener).into(new GlideDrawableImageViewTarget(imageView));
    }

    public void displayImage(final String url, String thumbnail,
                             ImageView imageView) {
        if (imageView == null) {
            return;
        }
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                loadImageLoadFail();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                reportImageLoad(url, target.getRequest(), dataSource, false, false);
                return false;
            }
        };
        Glide.with(mContext).load(url)
                .thumbnail(Glide.with(mContext).load(thumbnail)).listener(requestListener)
                .into(new GlideDrawableImageViewTarget(imageView));
    }

    public void displayImage(File file, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        Glide.with(mContext).load(file).into(new GlideDrawableImageViewTarget(imageView));
    }

    public void removeFromCache(final String url) {
        File file = getDiskCacheFileByUrl(url);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    public void cancelDisplayTask(ImageView imageAware) {
        if (imageAware == null) {
            return;
        }
        Glide.with(mContext).clear(new GlideDrawableImageViewTarget(imageAware));
    }

    public void loadPreViewImage(final String url, RequestOptions requestOptions) {
        requestOptions.priority(Priority.LOW);
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                loadImageLoadFail();
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                reportImageLoad(url, target.getRequest(), dataSource, true, false);
                return false;
            }
        };
        Glide.with(mContext).asBitmap().load(url).apply(requestOptions).listener(requestListener).preload();
    }

    public File getDiskCacheFileByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(url), EmptySignature.obtain());
        File imageFile = Glide.get(mContext).getDiskCache().get(dataCacheKey);
        String imageSize = Uri.parse(url).getQueryParameter(PARAM_SIZE);
        String fullScreenSize = thumbnailHelper.calUrlDimension(ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE);

        // 大图预览如果磁盘没有缓存，则跳过取300缩略图的逻辑
        if (imageFile == null && !TextUtils.isEmpty(imageSize) && !imageSize.equalsIgnoreCase(fullScreenSize)) {
            ThumbnailHelper thumbnailHelper = new ThumbnailHelper(BaseApplication.getInstance());
            GlideImageSize gridSize = thumbnailHelper.getImageSizeByType(
                    ThumbnailSizeType.THUMBNAIL_SIZE_96);
            ResourceCacheKey resourceCacheKey = new ResourceCacheKey(
                    new LruArrayPool(getArrayPoopSize()),
                    new GlideUrl(url),
                    EmptySignature.obtain(),
                    gridSize.mWidth,
                    gridSize.mHeight,
                    null,
                    null,
                    null);
            imageFile = Glide.get(mContext).getDiskCache().get(resourceCacheKey);
        }

        return imageFile;
    }

    private int getArrayPoopSize() {
        ActivityManager activityManager = (ActivityManager) BaseApplication.getInstance()
                .getSystemService(Context.ACTIVITY_SERVICE);
        int arrayPoolSize =
                MemorySizeCalculator.isLowMemoryDevice(activityManager)
                        ? (ARRAY_POOL_SIZE_BYTES / LOW_MEMORY_BYTE_ARRAY_POOL_DIVISOR) : ARRAY_POOL_SIZE_BYTES;
        return arrayPoolSize;
    }

    public void loadImageLoadFail() {
        if (mPreLoadManager != null) {
            mPreLoadManager.imageLoadFinished();
        }
    }

    public void reportImageLoad(String s, Request request, DataSource dataSource,
                                boolean isFromPreload, boolean isFromLocalThumbnail) {
        if (mPreLoadManager != null) {
            mPreLoadManager.imageLoadFinished();
        }
        if (TextUtils.isEmpty(s)
                || (!s.startsWith(HostURLManagerKt.PRO_STR_HTTPS)
                            && !s.startsWith(HostURLManagerKt.PRO_STR_HTTP))) {
            // load uri为空或不是网络地址则不统计
            return;
        }
        Uri uri = Uri.parse(s);
        String size = uri.getQueryParameter("size");
        if (TextUtils.isEmpty(size)) {
            return;
        }
        int index = size.indexOf("_");
        if (index <= 1 || (index + 2) >= size.length()) {
            return;
        }
        String width = size.substring(1, index);
        String height = size.substring(index + 2, size.length());
        int trueWidth = 0;
        int trueHeight = 0;
        try {
            trueWidth = Integer.valueOf(width);
            trueHeight = Integer.valueOf(height);
        } catch (NumberFormatException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            return;
        }

        boolean cached = false;
        if (dataSource != null) {
            DataSource loadedFrom = dataSource;
            switch (loadedFrom) {
                case REMOTE:
                    cached = false;
                    break;
                case LOCAL:
                case RESOURCE_DISK_CACHE:
                case DATA_DISK_CACHE:
                case MEMORY_CACHE:
                    cached = true;
                    break;
                default:
                    cached = false;
                    break;
            }
        }
        if (request != null && request instanceof SingleRequest) {
            long loadTime = ((SingleRequest) request).getLoadCostTime();
            long dispaterTime = ((SingleRequest) request).getDispaterCostTime();
            reportImageLoad(trueWidth, trueHeight, cached, loadTime, isFromPreload, isFromLocalThumbnail, dispaterTime);
        }
    }

    public String getImageType(String url) {
        String imageCategory = IMAGE_CATEGORY_SMALL;
        if (TextUtils.isEmpty(url)
                || (!url.startsWith(HostURLManagerKt.PRO_STR_HTTPS)
                            && !url.startsWith(HostURLManagerKt.PRO_STR_HTTP))) {
            // load uri为空或不是网络地址则不统计
            return imageCategory;
        }
        Uri uri = Uri.parse(url);
        String size = uri.getQueryParameter("size");
        if (TextUtils.isEmpty(size)) {
            return imageCategory;
        }
        int index = size.indexOf("_");
        if (index <= 1 || (index + 2) >= size.length()) {
            return imageCategory;
        }
        String width = size.substring(1, index);
        String height = size.substring(index + 2);
        int trueWidth = 0;
        int trueHeight = 0;
        try {
            trueWidth = Integer.valueOf(width);
            trueHeight = Integer.valueOf(height);
        } catch (NumberFormatException e) {
            DuboxLog.e(TAG, e.getMessage(), e);
            return imageCategory;
        }
        if (trueWidth >= DeviceDisplayUtils.getScreenWidth() && trueHeight >= DeviceDisplayUtils.getScreenHeight()) {
            imageCategory = IMAGE_CATEGORY_BIG;
        } else if (trueWidth == 800 && trueHeight == 800) {
            imageCategory = IMAGE_CATEGORY_SIZE800;
        } else {
            imageCategory = IMAGE_CATEGORY_SMALL;
        }
        return imageCategory;
    }

    /**
     * 判断是否已经缓存到磁盘缓存
     *
     * @param url 请求图片地址
     * @return true:存在 false:不存在
     */
    public boolean isInDiskCache(String url) {
        File image = getDiskCacheFileByUrl(url);
        return image != null && image.exists();
    }

    public boolean isInMemoryCache(String md5, int width, int height) {
        try {
            return Glide.get(mContext).isInMemoryCache(md5, width, height);
        } catch (Exception e) {
            return false;
        }
    }

    public void clear(ImageView view) {
        if (view != null) {
            if (GlideAsyncRequester.ENABLE) {
                mAsyncRequester.clear(view);
            }
        }
    }

    /**
     * 使用SimpleTarget读取
     * @param context
     * @param url
     * @param imageView
     * @param placeholder
     */
    public void loadImageWithSimpleTarget(Context context, String url, ImageView imageView, int placeholder) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().placeholder(placeholder).dontAnimate()
                        .skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap,
                                                @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    public void registerPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        if (mPreLoadManager != null) {
            mPreLoadManager.registerPreLoadStateListener(stateListener);
        }
    }

    @Override
    public void unregisterPreLoadStateListener(IGlidePreLoadIdleListener stateListener) {
        if (mPreLoadManager != null) {
            mPreLoadManager.unregisterPreLoadStateListener(stateListener);
        }
    }

    @Override
    public int getPreLoadTaskSize() {
        if (mPreLoadManager != null) {
            mPreLoadManager.getPreLoadTaskSize();
        }
        return 0;
    }

    /**
     * 获取全屏幕尺寸的缩略图
     *
     * @param url
     */
    public void loadImage(String url, RequestListener requestListener) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .priority(Priority.LOW);
        RequestBuilder requestBuilder = Glide.with(mContext).load(url);
        if (requestListener != null) {
            requestBuilder.listener(requestListener);
        }
        requestBuilder.apply(requestOptions).preload();
    }

    /**
     * 加载 Gif 图
     * @param url gif 图片地址
     * @param imageView 目标 view
     */
    public void loadGif(String url, ImageView imageView, Context context, int placeHolder) {
        RequestOptions options = new RequestOptions()
                .set(GifOptions.DECODE_FORMAT, DecodeFormat.PREFER_RGB_565);
        if (placeHolder != 0) {
            options = options.placeholder(placeHolder);
        }
        Glide.with(context)
                .setDefaultRequestOptions(options)
                .asGif()
                .load(url)
                .into(imageView);
    }

}
