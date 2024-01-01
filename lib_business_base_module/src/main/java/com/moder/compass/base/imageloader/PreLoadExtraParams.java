package com.moder.compass.base.imageloader;

import android.net.Uri;

/**
 * Created by liaozhengshuang on 17/11/17.
 * 根据目录加载目录下图片缩略图的参数类
 */

public class PreLoadExtraParams {
    public Uri uri;
    public String sort;
    /**
     * 第一列必须是下载缩略图的url或者path
     */
    public String[] projection;
    public String selection;
    public String[] selectionArgs;
    /**
     * 记录load回来的是图片url还是path，url则直接拉取，path需要转换成url
     */
    public boolean isUrl;

    public PreLoadExtraParams(Uri uri, String sort, String[] projection, String selection, String[] selectionArgs,
                              boolean isUrl) {
        this.uri = uri;
        this.sort = sort;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.isUrl = isUrl;
    }
}
