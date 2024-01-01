package com.moder.compass.stats.upload;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2016/9/12.
 */
public class StringUpload extends StatsUpload {
    private static final String TAG = "StringUpload";

    public StringUpload(UploadData data) {
        super(data);
    }

    @Override
    public String generator() {
        if (!TextUtils.isEmpty(mUploadData.getOp())) {
            mStringBuilder.append(mUploadData.getOp());
            mStringBuilder.append(Separator.ITEM_SPLIT);
        }
        mStringBuilder.append(mUploadData.getOther0());
        return super.generator();
    }
}
