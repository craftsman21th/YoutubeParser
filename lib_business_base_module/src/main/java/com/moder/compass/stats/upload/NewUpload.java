package com.moder.compass.stats.upload;

import android.text.TextUtils;


/**
 * Created by liuliangping on 2016/9/12.
 */
public class NewUpload extends StatsUpload {
    private static final String TAG = "NewUpload";

    /**
     * 统计项分类键值
     */
    private static final String STATISTICS_KEY_OP = "op";

    /**
     * 统计值
     **/
    private static final String STATISTICS_KEY_COUNT = "count";

    /**
     * 统计项子分类键值前缀，如other0
     **/
    private static final String STATISTICS_KEY_OTHER = "other";

    /**
     * 行为时间，json数组
     */
    private static final String STATISTICS_KEY_OP_TIME = "op_time";

    public NewUpload(UploadData data) {
        super(data);
    }

    @Override
    public String generator() {
        if (!TextUtils.isEmpty(mUploadData.getOp())) {
            mStringBuilder.append(STATISTICS_KEY_OP);
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOp());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther0())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("0");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther0());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther1())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("1");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther1());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther2())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("2");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther2());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther3())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("3");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther3());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther4())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("4");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther4());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther5())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("5");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther5());
        }

        if (!TextUtils.isEmpty(mUploadData.getOther6())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OTHER);
            mStringBuilder.append("6");
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOther6());
        }

        if (mUploadData.getCount() > 0) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_COUNT);
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getCount());
        }

        if (!TextUtils.isEmpty(mUploadData.getOpTime())) {
            mStringBuilder.append(Separator.ITEM_SPLIT);
            mStringBuilder.append(STATISTICS_KEY_OP_TIME);
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(mUploadData.getOpTime());
        }

        String opParam = mUploadData.getOpParam();
        String[] params = null;
        if (!TextUtils.isEmpty(opParam)) {
            params = opParam.split(Separator.PARAM_SPLIT);
        }
        if (params != null && params.length > 0) {
            for (String param : params) {
                if (TextUtils.isEmpty(param)
                        || !param.contains(Separator.PARAM_EQUALS)) {
                    continue;
                }
                String[] keyValue = param.split(Separator.PARAM_EQUALS);
                if (keyValue.length < 2) {
                    continue;
                }
                mStringBuilder.append(Separator.ITEM_SPLIT);
                mStringBuilder.append(keyValue[0]);
                mStringBuilder.append(Separator.ITEM_EQUALS);
                mStringBuilder.append(keyValue[1]);
            }
        }

        return super.generator();
    }
}
