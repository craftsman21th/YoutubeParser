package com.moder.compass.sharelink;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by cuizhe01 on 18/5/3.
 */
public class ShareLinkUtils {

    private static final String TAG = "ShareLinkUtils";
    private static final String PATTERN_SHARE_SHORT_URL =
            "^[\\s\\S]*(" + HostURLManager.getAllDomainPatternString() + "/)s/1+[\\s\\S]*";

    private static final String PATTERN_SHARE_LONG_URL =
            "^[\\s\\S]*(" + HostURLManager.getAllDomainPatternString() + "/)share/link+[\\s\\S]*";

    private static final String PATTERN_SHARE_SPECIAL_URL =
            "^[\\s\\S]*(" + HostURLManager.getAllDomainPatternString() + "/)(share/init|wap/init|wap/link)+[\\s\\S]*";

    private static final Pattern SHARE_SHORT_URL_PATTERN = Pattern.compile(PATTERN_SHARE_SHORT_URL);

    private static final Pattern SHARE_LONG_URL_PATTERN = Pattern.compile(PATTERN_SHARE_LONG_URL);

    private static final Pattern SHARE_SPECIAL_URL_PATTERN = Pattern.compile(PATTERN_SHARE_SPECIAL_URL);

    public static final String SHARE_FILE_SHORT_LINK = "1";
    public static final String SHARE_FILE_LONG_LINK = "/share/link";
    public static final String SHARE_FILE_SPECIAL_LINK_ONE = "/share/init";
    public static final String SHARE_FILE_SPECIAL_LINK_TWO = "/wap/init";
    public static final String SHARE_FILE_SPECIAL_LINK_THREE = "/wap/link";

    private static final String QUERY_KEY_SHARE_ID = "shareid";
    private static final String QUERY_KEY_UK = "uk";
    private static final String QUERY_KEY_SURL = "surl";

    public static boolean isDuboxShortShareLink(String url) {
        return !TextUtils.isEmpty(url) && SHARE_SHORT_URL_PATTERN.matcher(url).matches();
    }

    public static boolean isDuboxLongShareLink(String url) {
        return !TextUtils.isEmpty(url) && SHARE_LONG_URL_PATTERN.matcher(url).matches();
    }

    public static boolean isDuboxSpecialShareLink(String url) {
        return !TextUtils.isEmpty(url) && SHARE_SPECIAL_URL_PATTERN.matcher(url).matches();
    }

    /**
     * 解析短链
     *
     * @param shortLink
     * @return ［0］:linkType ［1］:link
     */
    @Nullable
    public static String[] parseShortLink(@NonNull String shortLink) {
        int linkTypeIndex = shortLink.lastIndexOf("/") + 1;
        String linkType;
        if (linkTypeIndex >= 0 && (linkTypeIndex + 1) < shortLink.length()) {
            linkType = shortLink.substring(linkTypeIndex, linkTypeIndex + 1);
            if (SHARE_FILE_SHORT_LINK.equals(linkType)) {
                String link = shortLink.substring(linkTypeIndex + 1);
                return new String[]{linkType, link};
            }
        }
        return null;
    }

    /**
     * 解析url是短链或长链或特殊链接，获取对应参数
     */
    public static String[] parseUrl(String url) {
        if (ShareLinkUtils.isDuboxShortShareLink(url)) {
            return ShareLinkUtils.parseShortLink(url);
        } else if (ShareLinkUtils.isDuboxSpecialShareLink(url)) {
            return ShareLinkUtils.parseSpecialLink(url);
        } else if (ShareLinkUtils.isDuboxLongShareLink(url)) {
            return ShareLinkUtils.parseLongLink(url);
        }
        return null;
    }

    /**
     * 解析长链
     *
     * @param longtLink
     * @return ［0］:linkType ［1］:shareid ［2］:uk
     */
    @Nullable
    public static String[] parseLongLink(@NonNull String longtLink) {
        try {
            URL url = new URL(longtLink);
            Map<String, String> map = splitQuery(url);
            String linkType = url.getPath();
            if (TextUtils.isEmpty(linkType) || map.size() <= 0) {
                return null;
            }
            switch (linkType) {
                case SHARE_FILE_LONG_LINK:
                    if (map.size() == 1) {
                        String temp = map.get(QUERY_KEY_SURL);
                        if (temp != null && temp.startsWith("1")) {
                            temp = temp.substring(1);
                        }
                        return new String[]{linkType, temp};
                    } else {
                        return new String[]{linkType, map.get(QUERY_KEY_SHARE_ID), map.get(QUERY_KEY_UK)};
                    }
                default:
                    break;
            }
        } catch (Exception e) {
            DuboxLog.d(TAG, "url is error");
        }
        return null;
    }

    /**
     * 解析特殊的外链
     *
     * @param specialLink
     * @return ［0］:linkType ［1］:surl
     */
    @Nullable
    public static String[] parseSpecialLink(@NonNull String specialLink) {
        try {
            URL url = new URL(specialLink);
            Map<String, String> map = splitQuery(url);
            String linkType = url.getPath();
            if (TextUtils.isEmpty(linkType) || map.size() == 0) {
                return null;
            }
            switch (linkType) {
                case SHARE_FILE_SPECIAL_LINK_ONE:
                case SHARE_FILE_SPECIAL_LINK_TWO:
                    return new String[]{linkType, map.get(QUERY_KEY_SURL)};
                case SHARE_FILE_SPECIAL_LINK_THREE:
                    String temp = map.get(QUERY_KEY_SURL);
                    if (temp != null && temp.startsWith("1")) {
                        temp = temp.substring(1);
                    }
                    return new String[]{linkType, temp};
                default:
                    break;
            }
        } catch (Exception e) {
            DuboxLog.d(TAG, "url is error");
        }
        return null;
    }

    private static Map<String, String> splitQuery(URL url) {
        Map<String, String> queryMap = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        // 修复xray检测到的崩溃，此处query可能为null
        if (TextUtils.isEmpty(query)) {
            return queryMap;
        }
        if (!query.contains("&")) {
            try {
                int index = query.indexOf("=");
                if (index >= 0 && (index + 1) < query.length()) {
                    queryMap.put(
                            URLDecoder.decode(query.substring(0, index), "UTF-8"),
                            URLDecoder.decode(query.substring(index + 1), "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            String[] pairs = query.split("&");
            try {
                for (String pair : pairs) {
                    int index = pair.indexOf("=");
                    if (index >= 0 && (index + 1) < pair.length()) {
                        queryMap.put(
                                URLDecoder.decode(pair.substring(0, index), "UTF-8"),
                                URLDecoder.decode(pair.substring(index + 1), "UTF-8"));
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return queryMap;
    }
}
