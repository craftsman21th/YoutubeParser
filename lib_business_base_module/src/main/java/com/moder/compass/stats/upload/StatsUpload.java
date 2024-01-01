package com.moder.compass.stats.upload;

import com.moder.compass.account.Account;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2016/9/12.
 */
public class StatsUpload implements IStatsGenerator {
    private static final String TAG = "StatsUploadFactory";
    protected UploadData mUploadData;
    protected StringBuilder mStringBuilder;

    private static final String USERNAME = "username";
    private static final String BDUSS = "ndus";
    // -1:百度账号登录 1 人人网 ;2 新浪微博;4 腾讯微博;15 QQ登录;42 微信登录
    private static final String OSTYPE = "ostype";

    // 代表通过百度账号登录客户端，仅在此处作为统计时有意义
    private static final String OS_TYPE_NA = "-1";

    /**
     * 当前默认输入法
     */
    private static final String DEFAULT_INPUT_METHOD = "inputmethod";

    private String defaultInputMethod;

    private StatsUpload() {

    }

    public StatsUpload(UploadData data) {
        mStringBuilder = new StringBuilder();
        mUploadData = data;
    }

    public void setDefaultInputMethod(String defaultInputMethod) {
        this.defaultInputMethod = defaultInputMethod;
    }

    @Override
    public String generator() {
        mStringBuilder.append(Separator.ITEM_SPLIT);

        mStringBuilder.append(USERNAME);
        mStringBuilder.append(Separator.ITEM_EQUALS);
        mStringBuilder.append(Account.INSTANCE.getUid());
        mStringBuilder.append(Separator.ITEM_SPLIT);

        mStringBuilder.append(BDUSS);
        mStringBuilder.append(Separator.ITEM_EQUALS);
        mStringBuilder.append(Account.INSTANCE.getNduss());
        mStringBuilder.append(Separator.ITEM_SPLIT);

        if (!TextUtils.isEmpty(defaultInputMethod)) {
            mStringBuilder.append(DEFAULT_INPUT_METHOD);
            mStringBuilder.append(Separator.ITEM_EQUALS);
            mStringBuilder.append(defaultInputMethod);
            mStringBuilder.append(Separator.ITEM_SPLIT);
        }

        mStringBuilder.append(OSTYPE);
        mStringBuilder.append(Separator.ITEM_EQUALS);
        String osType = Account.INSTANCE.getOsType();
        mStringBuilder.append(TextUtils.isEmpty(osType) ? OS_TYPE_NA : osType);
        mStringBuilder.append(Separator.LINE_SPLIT);

        return mStringBuilder.toString();
    }
}
