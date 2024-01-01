package com.moder.compass.transfer.util;

import android.net.Uri;
import android.text.TextUtils;

import com.baidu.android.common.util.CommonParam;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.base.network.FallbackManager;
import com.dubox.drive.base.network.NetworkUtil;
import com.moder.compass.base.utils.PersonalConfigKey;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.kernel.util.encode.SHA1Util;


/**
 * com.dubox.drive.preview.video.VideoURL
 *
 * @panwei <br/>
 * create at 2012-12-18 下午2:43:16
 */
public class VideoURLUtil {
    private static final String TAG = "VideoURL";

    static final String DLINK_CTRL_PARAMETER = "origin=player";

    /**
     * 直链客户端类型
     */
    public static final String DIRECT_LINK_CLIENT_TYPE = "5";

    /**
     * M3U8_AUTO_240、M3U8_AUTO_360、M3U8_AUTO_480、M3U8_AUTO_720对应于开发平台的高固定、宽自适应类型
     */
    static final String M3U8_AUTO_240 = "M3U8_AUTO_240";
    public static final String M3U8_AUTO_480 = "M3U8_AUTO_480";
    // 新增转码类型
    static final String M3U8_FLV_264_480 = "M3U8_FLV_264_480";
    static final String M3U8_BRG_MP4_265_480 = "M3U8_BRG_MP4_265_480";
    static final String M3U8_BRG_FLV_264_480 = "M3U8_BRG_FLV_264_480";
    // 音频码率
    static final String M3U8_MP3_128 = "M3U8_MP3_128";

    /**
     * 视频目前支持分辨率四种：
     * 360
     */
    public static final String M3U8_AUTO_360 = "M3U8_AUTO_360";
    /**
     * 480
     */
    public static final String M3U8_MP4_265_480 = "M3U8_MP4_265_480";
    /**
     * 720
     */
    public static final String M3U8_AUTO_720 = "M3U8_AUTO_720";
    /**
     * 1080
     */
    public static final String M3U8_AUTO_1080 = "M3U8_AUTO_1080";

    /**
     * 2k分辨率
     */
    public static final String RESOLUTION_2K = "RESOLUTION_2K";
    /**
     * 4k分辨率
     */
    public static final String RESOLUTION_4K = "RESOLUTION_4K";

    /**
     * 获取直链用到的type
     */
    public static final String M3U8_MP4_264_480_PRVW = "M3U8_MP4_264_480_PRVW";

    /**
     * 根据屏幕尺寸、byterange开关、H265开关获取视频地址format
     *
     * @param isCheckScreenWidth
     * @return
     */
    public static String getVideoFormat(boolean isCheckScreenWidth) {
        final String format;
        if (isCheckScreenWidth) {
            int screenWidth = DeviceDisplayUtils.getScreenWidth();
            int screenHeight = DeviceDisplayUtils.getScreenHeight();
            int width = screenWidth > screenHeight ? screenHeight : screenWidth;
            // 默认转码格式改为hls+flv
            if (width < 480) {
                format = M3U8_AUTO_240;
            } else if (width < 720) {
                format = M3U8_AUTO_360;
            } else {
                format = getVideoFormat();
            }
        } else {
            format = getVideoFormat();
        }
        return format;
    }

    /**
     * 解析视频分辨率
     *
     * @param resolution resolution = "width:848,height:460";
     * @return
     */
    public static String getOnlineVideoResolution(String resolution) {
        if (TextUtils.isEmpty(resolution)) {
            return getVideoFormat();
        }
        int width = 0;
        int height = 0;
        String[] a = resolution.split(",");
        if (!TextUtils.isEmpty(a[0])
                && !TextUtils.isEmpty(a[0].split(":")[1])
                && TextUtils.equals("width", a[0].split(":")[0])) {
            width = Integer.valueOf(a[0].split(":")[1]);
            DuboxLog.d(TAG, "width = " + width);
        }
        if (!TextUtils.isEmpty(a[1])
                && !TextUtils.isEmpty(a[1].split(":")[1])
                && TextUtils.equals("height", a[1].split(":")[0])) {
            height = Integer.valueOf(a[1].split(":")[1]);
            DuboxLog.d(TAG, "height = " + height);
        }
        if (width * height > 1280 * 720) {
            return M3U8_AUTO_1080;
        } else if (width * height > 854 * 480) {
            return M3U8_AUTO_720;
        }
        return getVideoFormat();
    }

    /**
     * 解析视频分辨率
     *
     * @param resolution resolution = "width:848,height:460";
     * @return
     */
    public static String getVideoResolution(String resolution) {
        if (TextUtils.isEmpty(resolution)) {
            return getVideoFormat();
        }
        int width = 0;
        int height = 0;
        String[] a = resolution.split(",");
        if (!TextUtils.isEmpty(a[0])
                && !TextUtils.isEmpty(a[0].split(":")[1])
                && TextUtils.equals("width", a[0].split(":")[0])) {
            width = Integer.valueOf(a[0].split(":")[1]);
            DuboxLog.d(TAG, "width = " + width);
        }
        if (!TextUtils.isEmpty(a[1])
                && !TextUtils.isEmpty(a[1].split(":")[1])
                && TextUtils.equals("height", a[1].split(":")[0])) {
            height = Integer.valueOf(a[1].split(":")[1]);
            DuboxLog.d(TAG, "height = " + height);
        }

        if (width * height > 2560 * 1440) {
            return RESOLUTION_4K;
        } else if (width * height > 1920 * 1080) {
            return RESOLUTION_2K;
        } else if (width * height > 1280 * 720) {
            return M3U8_AUTO_1080;
        } else if (width * height > 854 * 480) {
            return M3U8_AUTO_720;
        }
        return getVideoFormat();
    }

    /**
     * 音频码率
     *
     * @return
     */
    public static String getAudioFormat() {
        return M3U8_MP3_128;
    }

    /**
     * 根据byterange开关和H265开关获取视频地址format
     *
     * @return
     */
    private static String getVideoFormat() {
        if (isEnableByterange()) {
            if (isEnableH265()) {
                return M3U8_BRG_MP4_265_480;
            }
            return M3U8_BRG_FLV_264_480;
        }
        if (isEnableH265()) {
            return M3U8_MP4_265_480;
        }
        return M3U8_FLV_264_480;
    }

    /**
     * 是否开着byterange下载和播放
     *
     * @return
     */
    private static boolean isEnableByterange() {
        return false;
    }

    /**
     * 手机是否支持H265
     *
     * @return
     */
    private static boolean isEnableH265() {
        return false;
    }

    /**
     * 添加原画的dlink防盗链控制
     */
    public static String addDlinkCtrlParameter(String dlink) {
        if (!TextUtils.isEmpty(dlink) && !dlink.contains(DLINK_CTRL_PARAMETER)) {
            dlink += dlink.contains("?") ? "&" : "?";
            dlink += DLINK_CTRL_PARAMETER;
        }
        DuboxLog.d(TAG, "DLINK-CTRL player：" + dlink);
        return dlink;
    }

    /**
     * 根据serverpath获取流畅播放视频的path
     */
    public static String getSmoothPlayPath(String bduss, String path, String format) {
        String smoothPath;

        final FallbackManager fallbackManager = new FallbackManager();
        String scheme = fallbackManager.getStreamingScheme();
        smoothPath = String.format(HostURLManager.videoPlayUrlNoBduss(), scheme, format,
                Uri.encode(path), HostURLManager.getEhps(),
                PersonalConfig.getInstance().getString(PersonalConfigKey.KEY_SAFE_BOX_TOKEN));

        smoothPath = NetworkUtil.addCommonParams(BaseApplication.getInstance(), smoothPath);
        String uid = Account.INSTANCE.getUid();
        if (!TextUtils.isEmpty(uid)) {
            smoothPath = NetworkUtil.addRand(smoothPath, bduss, uid);
        }

        return smoothPath;
    }

    /**
     * 获取视频播放直链
     *
     * @return
     */
    public static String getVideoPlayDirectLink(
            final String path, final String uk, final String shareId,
            final String secretKey, final String albumId, final String fsid
    ) {
        return getVideoPlayPath(
                path, uk, shareId, secretKey, albumId, fsid, M3U8_MP4_264_480_PRVW, DIRECT_LINK_CLIENT_TYPE,
                HostURLManager.defaultShareVideoName()
        );
    }

    /**
     * 网页用的视频播放链接
     *
     * @param path
     * @param uk
     * @param shareId
     * @param fsId
     * @return
     */
    public static String getWebVideoPlayPath(
            final String path, final String uk, final String shareId, final String fsId
    ) {
        return getVideoPlayPath(
                path, uk, shareId, "", "", fsId, M3U8_AUTO_720, RequestCommonParams.getClientType(),
                HostURLManager.defaultWebVideoName()
        ).replace("/share/streaming", "/share/streaming.m3u8");
    }

    /**
     * feed页播放视频时获取流畅播放地址
     *
     * @param path
     * @param uk
     * @return
     */
    public static String getFeedVideoPlayPath(
            final String path, final String uk, final String shareId,
            final String secretKey, final String albumId, final String fsid, String format
    ) {
        return getVideoPlayPath(
                path, uk, shareId, secretKey, albumId, fsid, format, RequestCommonParams.getClientType(),
                HostURLManager.defaultShareVideoName()
        );
    }

    private static String getVideoPlayPath(
            final String path, final String uk, final String shareId,
            final String serectKey, final String albumId, final String fsid, String format,
            final String clientType, final String baseUrl
    ) {
        DuboxLog.d(TAG, "getFeedVideoPlayPath:::" + ":path:" + path + ":uk:" + uk + ":shareId:" + shareId
                + ":serectKey:" + serectKey + ":albumId:" + albumId + ":fsid:" + fsid);

        String sid = "0";
        String aid = "0";
        String fid = "0";
        String sekey = "";
        String temPath = "";
        if (!TextUtils.isEmpty(path)) {
            if (!path.equals(Uri.decode(path))) {
                temPath = Uri.decode(path);
            } else {
                temPath = path;
            }
        }
        if (!TextUtils.isEmpty(shareId)) {
            sid = shareId;
        }
        if (!TextUtils.isEmpty(serectKey)) {
            sekey = serectKey;
        }
        if (!TextUtils.isEmpty(albumId)) {
            aid = albumId;
        }
        if (!TextUtils.isEmpty(fsid)) { // 如果fid 不为空 要把path置为空 为了让server依赖fid去播放
            fid = fsid;
            temPath = "";
        }
        long currTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append(clientType);
        sb.append(RequestCommonParams.getChannel());
        sb.append(AppCommon.DEVUID);
        sb.append(currTime);
        String sign = sb.toString();
        sign = SHA1Util.hmacSha1(sign);
        sign = Uri.encode(sign);
        temPath = Uri.encode(temPath);

        final FallbackManager fallbackManager = new FallbackManager();
        final String scheme = fallbackManager.getStreamingScheme();

        String videoPath = String.format(baseUrl,
                scheme, uk, format, temPath, sid, aid,
                currTime, sign, fid, clientType,
                Uri.encode(RequestCommonParams.getChannel()),
                Uri.encode(AppCommon.DEVUID),
                Uri.encode(AppCommon.VERSION_DEFINED), sekey, HostURLManager.getEhps());

        videoPath += "&cuid=" + Uri.encode(CommonParam.getCUID(BaseApplication.getInstance()));
        if (AppCommon.FIRST_LAUNCH_TIME > 0) {
            videoPath += "&firstlaunchtime=" + AppCommon.FIRST_LAUNCH_TIME;
        }

        DuboxLog.d(TAG, "videoPath pre = " + videoPath);
        // 非私密分享(sekey为空),去除sekey
        if (TextUtils.isEmpty(sekey)) {
            if (videoPath.contains("sekey=")) {
                videoPath = videoPath.replace("sekey=", "");
            }
        }
        DuboxLog.d(TAG, "videoPath = " + videoPath);
        return videoPath;
    }
}
