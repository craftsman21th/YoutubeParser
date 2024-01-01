/* Copyright (C) 2014 Baidu, Inc. All Rights Reserved. */

package com.moder.compass.ui.share;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 新版的email分享参数
 * 标题：{user name}分享了「{资源名称}」给你
 * {username}：当前用户昵称
 * {资源名称}：当前资源的名称
 * 内容（客户端已有文案）：
 * Hi, I am using {appName} to share "{资源名称}" with you. Come and take a look! （要注意换行）
 * {外链}
 * Extract code：{提取码}（私密文件才会有，不是私密文件的不显示）
 */
public class ShareNewEmailInfo implements Parcelable {

    /**
     * 分享的标题
     */
    private String title = "";
    /**
     * 分享的内容
     */
    private String content = "";
    /**
     * 提取码(私有内容可能会用到)
     */
    private String extractCode = "";

    /**
     * 获取分享的标题
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * 获取分享内容
     *
     * @return
     */
    public String getContent() {
        StringBuffer sb = new StringBuffer();
        sb.append(content);
        if (!TextUtils.isEmpty(extractCode)) {
            sb.append(extractCode);
        }
        return sb.toString();
    }

    public ShareNewEmailInfo(String title, String content, String extractCode) {
        this.title = title;
        this.content = content;
        this.extractCode = extractCode;
    }

    protected ShareNewEmailInfo(Parcel in) {
        title = in.readString();
        content = in.readString();
        extractCode = in.readString();
    }

    public static final Creator<ShareNewEmailInfo> CREATOR = new Creator<ShareNewEmailInfo>() {
        @Override
        public ShareNewEmailInfo createFromParcel(Parcel in) {
            return new ShareNewEmailInfo(in);
        }

        @Override
        public ShareNewEmailInfo[] newArray(int size) {
            return new ShareNewEmailInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(extractCode);
    }
}
