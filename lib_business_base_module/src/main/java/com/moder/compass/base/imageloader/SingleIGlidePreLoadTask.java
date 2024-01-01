package com.moder.compass.base.imageloader;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;

import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.glide.Glide;
import com.dubox.glide.RequestBuilder;
import com.dubox.glide.load.DataSource;
import com.dubox.glide.load.engine.GlideException;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.request.Request;
import com.dubox.glide.request.RequestListener;
import com.dubox.glide.request.RequestOptions;
import com.dubox.glide.request.target.Target;

/**
 * Created by liaozhengshuang on 17/11/21.
 * 单个图片缓存任务
 */

public class SingleIGlidePreLoadTask implements IImagePreLoadTask {
    private static final String TAG = "SingleIGlidePreLoadTask";
    private String mUrl;
    private String mMd5;
    private byte[] mImageBytes;
    private String mCacheKey;
    private RequestOptions mOptions;
    private GlideImageSize mGlideImageSize;
    private Context mContext;
    private Fragment mFragment;
    private PreLoadResultListener mPreLoadResultListener;

    public SingleIGlidePreLoadTask(Context context, Fragment fragment, String url, RequestOptions mOptions,
                                   GlideImageSize glideImageSize) {
        this.mUrl = url;
        this.mOptions = mOptions;
        this.mGlideImageSize = glideImageSize;
        this.mContext = context;
        this.mFragment = fragment;
    }

    public SingleIGlidePreLoadTask(Context context, Fragment fragment, byte[] bytes, RequestOptions mOptions,
                                   GlideImageSize glideImageSize, String md5) {
        this.mImageBytes = bytes;
        this.mOptions = mOptions;
        this.mGlideImageSize = glideImageSize;
        this.mContext = context;
        this.mFragment = fragment;
        this.mMd5 = md5;
    }

    public SingleIGlidePreLoadTask(Context context, Fragment fragment, String mUrl, String cacheKey,
                                   RequestOptions mOptions) {
        this.mUrl = mUrl;
        this.mCacheKey = cacheKey;
        this.mOptions = mOptions;
        this.mContext = context;
        this.mFragment = fragment;
    }

    public SingleIGlidePreLoadTask(Context context, Fragment fragment,
                                   String url, String cacheKey,
                                   RequestOptions options,
                                   GlideImageSize glideImageSize,
                                   PreLoadResultListener preLoadResultListener) {
        mUrl = url;
        mCacheKey = cacheKey;
        mOptions = options;
        mGlideImageSize = glideImageSize;
        mContext = context;
        mFragment = fragment;
        mPreLoadResultListener = preLoadResultListener;
    }

    @Override
    public void execute() {
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target target, boolean isFirstResource) {
                GlideHelper.getInstance().loadImageLoadFail();
                if (mPreLoadResultListener != null) {
                    mPreLoadResultListener.onLoadFailed(mUrl);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target,
                                           DataSource dataSource, boolean isFirstResource) {
                Request request = target.getRequest();
                GlideHelper.getInstance().reportImageLoad(mUrl, request, dataSource, true, false);
                if (mPreLoadResultListener != null) {
                    mPreLoadResultListener.onResourceReady(mUrl);
                }
                return false;
            }
        };
        RequestBuilder requestBuilder;
        if (mImageBytes != null && mImageBytes.length > 0 && !TextUtils.isEmpty(mMd5)) {
            mOptions.md5(mMd5);
            String cacheKey = mMd5 + mGlideImageSize.mWidth + mGlideImageSize.mHeight;
            CustomLocalBytes customLocalBytes = new CustomLocalBytes(mImageBytes, cacheKey);
            DuboxLog.i(TAG, "preload compress cacheKey:" + cacheKey + " mImageBytes.length:" + mImageBytes.length);
            if (mFragment != null && mFragment.getActivity() != null && !mFragment.getActivity().isFinishing()) {
                requestBuilder = Glide.with(mFragment).load(customLocalBytes);
            } else {
                requestBuilder = Glide.with(mContext).load(customLocalBytes);
            }
            DuboxLog.i(TAG, "compress >> task >> mImageBytes >> load");
            requestBuilder.apply(mOptions).listener(requestListener)
                    .preload(mGlideImageSize.mWidth, mGlideImageSize.mHeight);
        } else if (TextUtils.isEmpty(mCacheKey)) {
            if (mFragment != null && mFragment.getActivity() != null && !mFragment.getActivity().isFinishing()) {
                requestBuilder = Glide.with(mFragment).load(new GlideUrl(mUrl));
            } else {
                requestBuilder = Glide.with(mContext).load(new GlideUrl(mUrl));
            }

            requestBuilder.apply(mOptions).listener(requestListener)
                    .preload(mGlideImageSize.mWidth, mGlideImageSize.mHeight);
        } else {
            CustomLocalUri customLocalUri = new CustomLocalUri(Uri.parse(mUrl), mCacheKey);
            DuboxLog.d(TAG, "preloadLocal cacheKey:" + mCacheKey + " thumbnailPath:" + Uri.parse(mUrl));

            if (mFragment != null && mFragment.getActivity() != null && !mFragment.getActivity().isFinishing()) {
                requestBuilder = Glide.with(mFragment).load(customLocalUri);
            } else {
                requestBuilder = Glide.with(mContext).load(customLocalUri);
            }

            if (mGlideImageSize != null) {
                requestBuilder.apply(mOptions).listener(requestListener)
                        .preload(mGlideImageSize.mWidth, mGlideImageSize.mHeight);
            } else {
                requestBuilder.apply(mOptions).listener(requestListener).preload();
            }
        }
    }

    @Override
    public boolean isExecutableTask() {
        return true;
    }

    @Override
    public String getLoadUrl() {
        return mUrl;
    }

    @Override
    public void notifyLoaded() {
        if (mPreLoadResultListener != null && mUrl != null) {
            mPreLoadResultListener.onResourceReady(mUrl);
        }
    }
}
