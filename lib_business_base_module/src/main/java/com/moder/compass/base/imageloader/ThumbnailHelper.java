package com.moder.compass.base.imageloader;

import static com.moder.compass.business.kernel.HostURLManager.getDataDomain;

import java.io.File;

import com.moder.compass.account.Account;
import com.dubox.drive.base.imageloader.SimpleFileInfo;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.dubox.drive.kernel.BaseShellApplication;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.util.PathKt;
import com.dubox.drive.kernel.util.encode.MD5Util;
import com.moder.compass.business.kernel.HostURLManagerKt;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;

/**
 * 缩略图帮助类，提供生成缩略图url，获取缩略图统一大小的能力
 * 
 * @author 孙奇 <br/>
 *         create at 2012-12-4 上午12:07:41
 */
public class ThumbnailHelper {
    /**
     * 48x48
     */
    private static final int THUMBNAIL_SIZE_48 = 48;
    /**
     * 64x64
     */
    public static final int THUMBNAIL_SIZE_64 = 64;
    /**
     * 96x96
     */
    private static final int THUMBNAIL_SIZE_96 = 96;
    /**
     * 144x144
     */
    private static final int THUMBNAIL_SIZE_144 = 144;
    /**
     * 200x200
     */
    private static final int THUMBNAIL_SIZE_200 = 200;
    /**
     * 300x300
     */
    private static final int THUMBNAIL_SIZE_300 = 300;
    /**
     * 大图预览最高分辨率
     */
    public static final int MAX_THUMBNAIL_SIZE_1600 = 1600;

    private static final String THUMBNAIL_TOKEN_KEY = "30e4f791d0769bcfa1246a453726a5c9";

    private final String size48Str;
    private final String size64Str;
    private final String size96Str;
    private final String size144Str;
    private final String size200Str;
    private final String size300Str;
    private final String fullPreLoadSize64Str;
    private final String fullScreenSizeStr;
    private final String maxSize1600Str;
    private final GlideImageSize size48;
    private final GlideImageSize size64;
    private final GlideImageSize size96;
    private final GlideImageSize size144;
    private final GlideImageSize size200;
    private final GlideImageSize size300;
    private final GlideImageSize fullPreLoadSize64;
    private final GlideImageSize fullScreenSize;
    private final GlideImageSize maxSize1600;

    private final String mDevUid;

    public ThumbnailHelper(Context context) {
        mDevUid = "&devuid=" + Uri.encode(AppCommon.DEVUID);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int realSize48 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_48));
        int realSize64 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_64));
        int realSize96 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_96));
        int realSize144 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_144));
        int realSize200 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_200));
        int realSize300 = standardizationThumbnailSize((int) (displayMetrics.density * THUMBNAIL_SIZE_300));
        size48 = new GlideImageSize(realSize48, realSize48);
        size48Str = calUrlDimension(realSize48, realSize48);
        size64 = new GlideImageSize(realSize64, realSize64);
        size64Str = calUrlDimension(realSize64, realSize64);
        size96 = new GlideImageSize(realSize96, realSize96);
        size96Str = calUrlDimension(realSize96, realSize96);
        size144 = new GlideImageSize(realSize144, realSize144);
        size144Str = calUrlDimension(realSize144, realSize144);
        size200 = new GlideImageSize(realSize200, realSize200);
        size200Str = calUrlDimension(realSize200, realSize200);
        size300 = new GlideImageSize(realSize300, realSize300);
        size300Str = calUrlDimension(realSize300, realSize300);
        fullPreLoadSize64 = new GlideImageSize(THUMBNAIL_SIZE_64,
                THUMBNAIL_SIZE_64);
        fullPreLoadSize64Str = calUrlDimension(THUMBNAIL_SIZE_64,
                THUMBNAIL_SIZE_64);
        fullScreenSize = new GlideImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
        fullScreenSizeStr = calUrlDimension(displayMetrics.widthPixels, displayMetrics.heightPixels);
        maxSize1600 = new GlideImageSize(MAX_THUMBNAIL_SIZE_1600, MAX_THUMBNAIL_SIZE_1600);
        maxSize1600Str = calUrlDimension(MAX_THUMBNAIL_SIZE_1600, MAX_THUMBNAIL_SIZE_1600);
    }

    /**
     * @param type
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-4 上午12:03:49
     */
    public GlideImageSize getImageSizeByType(ThumbnailSizeType type) {
        if (type == null) {
            return null;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_48) {
            return size48;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_64) {
            return size64;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_96) {
            return size96;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_144) {
            return size144;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_200) {
            return size200;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_300) {
            return size300;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_FULL_PRELOAD_SIZE_64) {
            return fullPreLoadSize64;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE) {
            return fullScreenSize;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_MAX_SIZE_1600) {
            return maxSize1600;
        }
        return null;

    }

    /**
     * 生成网址格式的缩略图路径
     * <p>
     * http://
     *
     * @param file
     * @param type
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-29 下午02:25:23
     */
    public String makeRemoteUrlByPath(SimpleFileInfo file, ThumbnailSizeType type) {
        if (file.mPath == null) {
            return null;
        }
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        String scheme = configSystemLimit.pcsdataEnableHttps
                ? HostURLManagerKt.PRO_STR_HTTPS : HostURLManagerKt.PRO_STR_HTTP;
        String domain = getDataDomain();
        String url = HostURLManager
                .getThumbnailUrl(scheme, domain, Uri.encode(file.mPath), calUrlDimension(type))
                + mDevUid;
        return getThumbnailTokenUrl(url, file.mMd5);
    }

    /**
     * 添加uk、md5等信息，服务端解析使用
     * uk=${uk}&md5=${md5}&thumbnail_token=MD5(${uk}${md5}${dev_id}${app_id}${key})
     * @param url
     * @return
     */
    public String getThumbnailTokenUrl(String url, String md5) {
        StringBuffer result = new StringBuffer();
        result.append(url);
        if (!TextUtils.isEmpty(md5) && !md5.equals("-1")) {
            StringBuffer thumbnailSb = new StringBuffer();
            thumbnailSb.append(Account.INSTANCE.getUk())
                    .append(md5).append(AppCommon.DEVUID).append(AppCommon.getSecondBoxPcsAppId())
                    .append(THUMBNAIL_TOKEN_KEY);
            String thumbnailToken = MD5Util.createMD5WithHex(thumbnailSb.toString(), false);
            result.append("&uk=").append(Account.INSTANCE.getUk())
                    .append("&md5=").append(md5).append("&thumbnail_token=").append(thumbnailToken);
        }
        return result.toString();
    }

    /**
     * 生成网址格式的缩略图路径，没有图片size参数
     *
     * @param serverPath 云端路径
     * @return
     */
    public String makeRemoteUrlByPath(String serverPath) {
        if (serverPath == null) {
            return null;
        }
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        String scheme = configSystemLimit.pcsdataEnableHttps ?
                HostURLManagerKt.PRO_STR_HTTPS : HostURLManagerKt.PRO_STR_HTTP;
        String domain = getDataDomain();
        return HostURLManager.getThumbnailUrlWithoutSize(scheme, domain, Uri.encode(serverPath))
                + mDevUid;
    }

    /**
     * 生成本地文件格式的缩略图路径
     * <p>
     * file://
     * 
     * @param path
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-29 下午02:25:42
     */
    public static String makeLocalUrlByPath(String path) {
        if (path == null) {
            return null;
        }
        if (PathKt.isUri(path)) {
            return path;
        }
        File file = new File(path);
        return Uri.decode(Uri.fromFile(file).toString());
    }

    /**
     * 根据{@link ThumbnailSizeType}计算缩略图尺寸
     * <p>
     * 格式如"c480_u800"
     * 
     * @param type
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-11-29 下午02:14:34
     */
    public String calUrlDimension(ThumbnailSizeType type) {
        if (type == null) {
            return null;
        }

        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_48) {
            return size48Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_64) {
            return size64Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_96) {
            return size96Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_144) {
            return size144Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_200) {
            return size200Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_SIZE_300) {
            return size300Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_FULL_PRELOAD_SIZE_64) {
            return fullPreLoadSize64Str;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE) {
            return fullScreenSizeStr;
        }
        if (type == ThumbnailSizeType.THUMBNAIL_MAX_SIZE_1600) {
            return maxSize1600Str;
        }
        return null;

    }


    public GlideImageSize getGlideImageSize(String imageSize) {
        GlideImageSize glideImageSize = new GlideImageSize(300, 300);
        if (!TextUtils.isEmpty(imageSize)) {
            if (imageSize.equalsIgnoreCase(fullScreenSizeStr)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_FULL_SCREEN_SIZE);
            } else if (imageSize.equalsIgnoreCase(size96Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_96);
            } else if (imageSize.equalsIgnoreCase(size48Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_48);
            } else if (imageSize.equalsIgnoreCase(size64Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_64);
            } else if (imageSize.equalsIgnoreCase(size144Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_144);
            } else if (imageSize.equalsIgnoreCase(size200Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_200);
            }  else if (imageSize.equalsIgnoreCase(size300Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_SIZE_300);
            }  else if (imageSize.equalsIgnoreCase(maxSize1600Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_MAX_SIZE_1600);
            } else if (imageSize.equalsIgnoreCase(fullPreLoadSize64Str)) {
                glideImageSize = getImageSizeByType(ThumbnailSizeType.THUMBNAIL_FULL_PRELOAD_SIZE_64);
            }
        }
        return glideImageSize;
    }

    /**
     * 拼接Dimension
     * 
     * @param width
     * @param height
     * @return
     * @author 孙奇 V 1.0.0 Create at 2012-12-3 下午10:10:33
     */
    private String calUrlDimension(int width, int height) {
        width = getDimension(width);
        height = getDimension(height);
        StringBuilder sb = new StringBuilder();
        sb.append("c");
        sb.append(width);
        sb.append("_u");
        sb.append(height);
        return sb.toString();
    }

    /**
     * 当尺寸大于1600时改成1600
     *
     * @param dimension
     * @return
     */
    private int getDimension(int dimension) {
        if (dimension > 1600) {
            return 1600;
        }
        return dimension;
    }

    public static class LocalThumbnail {
        public static final String SCHEME_THUMBNAIL = "thumbnail";
        public static final String QUERY_PARAMETER_ID = "id";
        public static final String QUERY_PARAMETER_IMAGE_PATH = "image_path";
        public static final String QUERY_PARAMETER_CATEGORY = "category";

        public static final int CATEGORY_IMAGE = 0;
        public static final int CATEGORY_VIDEO = 1;

    }

    /**
     * 对请求服务图片大小做调整
     * @since 8.6 缩略图标准化：小图缩略图统一使用144*144和300*300两种尺寸
     */
    private int standardizationThumbnailSize(int thumbnailSize) {
        int minSize = 144;
        int maxSize = 300;
        if (BaseShellApplication.getContext().lowDeviceTag) {
            minSize = 48;
            maxSize = 300;
        }
        if (thumbnailSize <= minSize) {
            thumbnailSize = minSize;
        } else {
            thumbnailSize = maxSize;
        }
        return thumbnailSize;
    }
}