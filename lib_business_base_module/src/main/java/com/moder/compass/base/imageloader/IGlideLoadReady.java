package com.moder.compass.base.imageloader;

import java.util.ArrayList;

/**
 * Created by panchao02 on 2018/10/14.
 * 一组图片是否加载成功回调
 */

public interface IGlideLoadReady {

    // 首张图：封面图或者第一张题
    void imageFirstBitmap(String firsturl);

    void imageLoadFinish(ArrayList<String> urlPaths);

    void imageLoadFail();
}
