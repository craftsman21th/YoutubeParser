package com.moder.compass.base.imageloader;

import androidx.annotation.Nullable;
import android.widget.ImageView;

import com.dubox.drive.basemodule.R;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.glide.load.DataSource;
import com.dubox.glide.load.engine.GlideException;
import com.dubox.glide.request.Request;
import com.dubox.glide.request.RequestListener;
import com.dubox.glide.request.ThumbnailRequestCoordinator;
import com.dubox.glide.request.target.Target;

import java.lang.ref.WeakReference;

/**
 * Created by guanshuaichao on 2018/11/30.
 */
class OnceReportListener {

    private static final String TAG = "OnceReportListener";

    private WeakReference<ImageView> mRef;
    private String mUrl;
    private String mThumbnailUrl;
    private boolean mIsFromLocalThumbnail;
    private boolean mReported;

    RequestListener mRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target target, boolean b) {
            return dealOnLoadFailed();
        }

        @Override
        public boolean onResourceReady(Object o, Object o2, Target target, DataSource dataSource, boolean b) {
            return dealOnResourceReady(mUrl, target.getRequest(), dataSource);
        }
    };

    RequestListener mThumbnailRequestListener = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target target, boolean b) {
            return false;
        }

        @Override
        public boolean onResourceReady(Object o, Object o2, Target target, DataSource dataSource, boolean b) {
            report(mThumbnailUrl, target.getRequest(), dataSource, true);
            return false;
        }
    };

    OnceReportListener(ImageView imageView, String url, String thumbnailUrl, boolean isFromLocalThumbnail) {
        mRef = new WeakReference<>(imageView);
        mUrl = url;
        mThumbnailUrl = thumbnailUrl;
        mIsFromLocalThumbnail = isFromLocalThumbnail;
    }

    private boolean dealOnLoadFailed() {
        DuboxLog.d(TAG, "onResourceReady onLoadFailed");
        GlideHelper.getInstance().loadImageLoadFail();
        return false;
    }

    private boolean dealOnResourceReady(String url, Request request, DataSource dataSource) {
        DuboxLog.d(TAG, "dataSource = " + dataSource + " url = " + url);
        ImageView view = mRef.get();
        if (view != null) {
            view.setTag(R.id.image_load_from_info, dataSource);
        }
        report(url, request, dataSource, false);
        return false;
    }

    private synchronized void report(String url, Request request, DataSource dataSource,
                                     boolean isThumbnail) {
        if (!mReported) {
            mReported = true;
            if (request instanceof ThumbnailRequestCoordinator) {
                if (isThumbnail) {
                    request = ((ThumbnailRequestCoordinator) request).thumb;
                } else {
                    request = ((ThumbnailRequestCoordinator) request).full;
                }
            }
            GlideHelper.getInstance().reportImageLoad(url, request, dataSource, false, mIsFromLocalThumbnail);
        } else {
            DuboxLog.d(TAG, "reportImageLoad already reported for " + url);
        }
    }
}
