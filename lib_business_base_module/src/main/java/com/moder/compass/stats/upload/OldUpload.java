package com.moder.compass.stats.upload;

/**
 * Created by liuliangping on 2016/9/12.
 */
public class OldUpload extends StatsUpload {
    private static final String TAG = "OldUpload";

    public OldUpload(UploadData data) {
        super(data);
    }

    @Override
    public String generator() {
        if (mUploadData.getCount() > 0) {
            mStringBuilder.append(mUploadData.getOp());
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getCount());
        } else {
            mStringBuilder.append(mUploadData.getOp());
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther0());
        }

        return super.generator();
    }
}
