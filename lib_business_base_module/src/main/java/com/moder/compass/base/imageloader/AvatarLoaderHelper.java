/*
 * AvatarLoader.java
 * classes : com.dubox.drive.util.imageloader.AvatarLoader
 * @author tianzengming
 * V 1.0.0
 * Create at 2014-7-2 上午10:10:44
 */
package com.moder.compass.base.imageloader;

import java.io.File;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.dubox.drive.kernel.android.util.image.ImageUtil;
import com.dubox.drive.kernel.android.util.network.ConnectivityState;
import com.dubox.drive.kernel.android.util.network.NetWorkVerifier;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.glide.Glide;
import com.dubox.glide.load.DecodeFormat;
import com.dubox.glide.load.engine.DiskCacheStrategy;
import com.dubox.glide.request.RequestOptions;
import com.dubox.glide.signature.ObjectKey;
import com.mars.united.core.os.ResourceKt;
import android.content.Context;
import android.widget.ImageView;
/**
 * com.dubox.drive.util.imageloader.AvatarLoader
 *
 * @author tianzengming <br/>
 * create at 2014-7-2 上午10:10:44
 */
public class AvatarLoaderHelper {
    private static final String TAG = "AvatarLoader";
    private static final long CLEAR_AVATAR_INTERVAL = 1000 * 60 * 60 * 24 * 15; // 15days

    private static AvatarLoaderHelper sAvatarLoader;
    private long mAvatarTimestamp;

    private AvatarLoaderHelper() {
        mAvatarTimestamp = PersonalConfig.getInstance().getLong(PersonalConfigKey.AVATAR_TIMESTAMP);
        if (mAvatarTimestamp == 0) {
            mAvatarTimestamp = System.currentTimeMillis();
            PersonalConfig.getInstance().putLong(PersonalConfigKey.AVATAR_TIMESTAMP, mAvatarTimestamp);
            PersonalConfig.getInstance().commit();
        }

    }

    public static AvatarLoaderHelper getInstance() {
        if (sAvatarLoader == null) {
            sAvatarLoader = new AvatarLoaderHelper();
        }
        return sAvatarLoader;
    }

    /**
     * 获取cache的Key，在原来url上加入时间戳，可以通过改变时间戳让所有的头像缓存失效
     *
     * @param uri
     *
     * @return
     */
    private String genCacheKey(String uri) {
        return uri + "?" + mAvatarTimestamp;
    }


    public void displayAvatar(String url, int stubImageRes, ImageView imageView) {
        String cacheKey = genCacheKey(url);
        displayImage(url, cacheKey, stubImageRes, true, imageView, null);
    }


    public void checkAvatarCacheValid() {
        if (!ConnectivityState.isConnected(BaseApplication.getInstance()) || (NetWorkVerifier.isNoNetwork())) {
            return;
        }
        if (System.currentTimeMillis() - mAvatarTimestamp >= CLEAR_AVATAR_INTERVAL) {
            mAvatarTimestamp = System.currentTimeMillis();
            PersonalConfig.getInstance().putLong(PersonalConfigKey.AVATAR_TIMESTAMP, mAvatarTimestamp);
            PersonalConfig.getInstance().commit();
        }
    }

    private void displayImage(String url, String cacheKey, int stubImageRes, boolean cacheable, ImageView imageView,
                              GlideLoadingListener listener) {
        DuboxLog.d(TAG, "displayImage cacheKey = " + cacheKey);
        RequestOptions requestOptions = new RequestOptions();
        if (stubImageRes > 0) {
            requestOptions.placeholder(stubImageRes);
        }
        requestOptions = requestOptions
            .skipMemoryCache(false)
            .diskCacheStrategy(cacheable ? DiskCacheStrategy.DATA : DiskCacheStrategy.NONE)
            .dontAnimate();
        DuboxLog.d(TAG, "url = " + url);
        try {
            Glide.with(BaseApplication.getInstance()).load(url).apply(requestOptions).into(imageView);
        } catch (IllegalStateException e) {
            DuboxLog.d(TAG, e.getMessage());
        }
    }


    public void deleteAvatarCache(String uri) {
        deleteAvatarCache(uri, true);
    }

    private void deleteAvatarCache(String uri, boolean needDelete) {
        if (!ConnectivityState.isConnected(BaseApplication.getInstance()) || (NetWorkVerifier.isNoNetwork())) {
            return;
        }
        String cacheKey = genCacheKey(uri);
        if (needDelete) {
            GlideHelper.getInstance().removeFromCache(cacheKey);
        }
    }

    private void deleteSelfAvatarCache() {
        if (!ConnectivityState.isConnected(BaseApplication.getInstance()) || (NetWorkVerifier.isNoNetwork())) {
            return;
        }
        String url = Account.INSTANCE.getHeaderUrl();
        if (url == null) {
            // 修复 https://console.firebase.google.com/u/0/project/dubox-aba90/crashlytics/app/android:com.dubox.drive/issues/c3ee819fe182d31dbdf7e8c70168bb71?time=last-seven-days&versions=2.12.4%20(134)&types=crash&sessionEventKey=622E72EE02D5000130BC5B303654FB80_1653164045553280659
            DuboxLog.d(TAG, "deleteSelfAvatarCache: Head url is null!!!");
            return;
        }
        deleteAvatarCache(url, true);
    }

    /**
     * 刷新头像
     * @param context
     */
    public void refreshSelfAvatar(Context context) {
        DuboxLog.d(TAG, "refreshSelfAvatar ");
        try {
            // try catch 用来解决firebase莫名崩溃：https://console.firebase.google.com/u/0/project/dubox-aba90/crashlytics/app/android:com.dubox.drive/issues/6980699a5f0afdf632f99fa8c1de26fd?time=last-seven-days&versions=2.12.3%20(133)&sessionEventKey=622B2A1300600001426FADDD09433E1B_1652239525372713905
            deleteSelfAvatarCache();
            loadAvatar(context, Account.INSTANCE.getHeaderUrl());
        } catch (IllegalStateException e) {
            DuboxLog.d(TAG, e.getMessage());
        }
    }

    private void loadAvatar(Context context, String uri) {
        DuboxLog.d(TAG, "loadAvatar uri = " + uri);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .format(DecodeFormat.PREFER_RGB_565)
            .centerCrop();
        requestOptions.signature(new ObjectKey(mAvatarTimestamp));
        String cacheKey = genCacheKey(uri);
        int size = ResourceKt.dip2px(context, 47f);
        requestOptions = requestOptions.dontAnimate();
        try {
            GlideHelper.getInstance().displayImage(cacheKey, requestOptions, size, size);
        } catch (IllegalStateException e) {
            DuboxLog.d(TAG, e.getMessage());
        }
    }

    public void saveSelfAvatarCache(byte[] data) {
        DuboxLog.d(TAG, "saveSelfAvatarCache ");
        String cacheKey = genCacheKey(Account.INSTANCE.getHeaderUrl());
        File file = GlideHelper.getInstance().getDiskCacheFileByUrl(cacheKey);
        if (file == null) {
            return;
        }
        ImageUtil.writeImageFile(file, data);
    }
}
