package com.moder.compass.base.imageloader;

import android.text.TextUtils;

import com.dubox.glide.load.model.GlideUrl;

/**
 * 自定义Glide URL
 *
 * Created by lijunnian on 2018/9/13.
 */

public class CustomGlideUrl extends GlideUrl {
    private String cacheUrl;

    CustomGlideUrl(String requestUrl, String cacheUrl) {
        super(requestUrl);
        this.cacheUrl = cacheUrl;
    }

    @Override
    public String getCacheKey() {
        String md5Key = handleUrl(cacheUrl);
        if (!TextUtils.isEmpty(md5Key)) {
            return md5Key;
        } else {
            return cacheUrl;
        }
    }


}
