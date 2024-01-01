package com.moder.compass.transfer.base;

import android.util.Pair;

import java.util.List;

/**
 * Created by liuliangping on 2015/10/22.
 */
public interface IUploadInfoGenerator {
    Pair<List<UploadInfo>, UploadInterceptorInfo> generate();
}
