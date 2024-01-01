/* Copyright (C) 2014 Baidu, Inc. All Rights Reserved. */

package com.moder.compass.ui.share;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.dubox.drive.cloudfile.io.model.CloudFile;
import java.util.ArrayList;

public class ShareOption implements Parcelable {
    /** 分享loading **/
    public static final int SHARE_LOADING = -1;
    /** 复制外链 **/
    public static final int SHARE_COPY_LINK = 2;
    /** 分享到其他 **/
    public static final int SHARE_TO_OTHERS = 3;
    /** 分享到朋友 **/
    public static final int SHARE_TO_FRIENDS = 6;
    /** 分享到email**/
    public static final int SHARE_TO_EMAIL = 12;
    /**
     * share to facebook
     */
    public static final int SHARE_TO_FACEBOOK = 13;
    /**
     * share to messenger
     */
    public static final int SHARE_TO_MESSENGER = 14;
    /**
     * share to whatsapp
     */
    public static final int SHARE_TO_WHATSAPP = 15;
    /**
     * share to telegram
     */
    public static final int SHARE_TO_TELEGRAM = 16;

    public ArrayList<CloudFile> mSelectList;
    public boolean[] mIsDirectory;
    public boolean mNeedPeriod;
    /**
     * 调用端直接传入分享链接，无需调用server去生成链接
     */
    public String shareLink;

    /**
     * 调用端直接传入shareId, 邮件分享时用到
     */
    public long shareId;

    /**
     * 注：6.2.0新增
     * 用于记录新版邮箱分享的相关数据
     */
    public ShareNewEmailInfo shareNewEmailInfo = null;

    /**
     * 是否可设置
     */
    public boolean canBeSet = true;

    /**
     * 是否展示 email 选项
     */
    public boolean isShowEmail = true;

    /**
     * 是否展示站长赚钱计划
     */
    public boolean showEarnPlan = false;

    public ShareOption(Builder builder) {
        mSelectList = builder.mSelectList;
        mIsDirectory = builder.mIsDirectory;
        mNeedPeriod = builder.mNeedPeriod;
        shareLink = builder.shareLink;
        shareNewEmailInfo = builder.shareNewEmailInfo;
        shareId = builder.shareId;
        canBeSet = builder.canBeSet;
        isShowEmail = builder.isShowEmail;
        showEarnPlan = builder.showEarnPlan;
    }

    protected ShareOption(Parcel in) {
        mSelectList = in.createTypedArrayList(CloudFile.CREATOR);
        mIsDirectory = in.createBooleanArray();
        mNeedPeriod = in.readByte() != 0;
        shareLink = in.readString();
        shareNewEmailInfo = in.readParcelable(ShareNewEmailInfo.class.getClassLoader());
        shareId = in.readLong();
        canBeSet = in.readByte() != 0;
        isShowEmail = in.readByte() != 0;
        showEarnPlan = in.readByte() != 0;
    }

    public static final Creator<ShareOption> CREATOR = new Creator<ShareOption>() {
        @Override
        public ShareOption createFromParcel(Parcel in) {
            return new ShareOption(in);
        }

        @Override
        public ShareOption[] newArray(int size) {
            return new ShareOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mSelectList);
        dest.writeBooleanArray(mIsDirectory);
        dest.writeByte((byte) (mNeedPeriod ? 1 : 0));
        dest.writeString(shareLink);
        dest.writeParcelable(shareNewEmailInfo, flags);
        dest.writeLong(shareId);
        dest.writeByte((byte) (canBeSet ? 1 : 0));
        dest.writeByte((byte) (isShowEmail ? 1 : 0));
        dest.writeByte((byte) (showEarnPlan ? 1 : 0));
    }

    /**
     * 从实体构建一个Builder用于编辑。
     */
    public Builder toBuilder() {
        Builder builder = new Builder()
                .setSelectList(mSelectList)
                .setIsDirectory(mIsDirectory)
                .setNeedPeriod(mNeedPeriod)
                .setShowEarnPlan(showEarnPlan);
        builder.setShareLink(shareLink);
        builder.setShareId(shareId);
        builder.setCanBeSet(canBeSet);
        builder.setShowEmail(isShowEmail);
        return builder;
    }

    /**
     * 分享选项的构造器
     */
    public static final class Builder {
        private ArrayList<CloudFile> mSelectList;
        private boolean[] mIsDirectory;
        private boolean mNeedPeriod;
        /**
         * 调用端直接传入分享链接，无需在调用server去生成链接
         */
        private String shareLink = null;

        /**
         * 注：6.2.0更新
         * 主要用于记录新版邮箱分享时候的相关数据
         */
        private ShareNewEmailInfo shareNewEmailInfo = null;

        /**
         *
         */
        private long shareId;

        /**
         * 是否可设置,
         */
        private boolean canBeSet = true;
        /**
         * 是否展示 email 选项
         */
        private boolean isShowEmail = true;
        private boolean showEarnPlan = false;

        public Builder() {
        }

        public Builder(Context context) {
        }

        public Builder setSelectList(ArrayList<CloudFile> selectList) {
            mSelectList = selectList;
            return this;
        }

        public Builder setIsDirectory(boolean[] isDirectory) {
            mIsDirectory = isDirectory;
            return this;
        }
        public Builder setNeedPeriod(boolean needPeriod) {
            mNeedPeriod = needPeriod;
            return this;
        }

        public void setShareLink(String shareLink) {
            this.shareLink = shareLink;
        }

        public void setShareNewEmailInfo(ShareNewEmailInfo shareEmailTitle) {
            this.shareNewEmailInfo = shareEmailTitle;
        }

        public void setShareId(long shareId) {
            this.shareId = shareId;
        }

        public void setCanBeSet(boolean canBeSet) {
            this.canBeSet = canBeSet;
        }

        /**
         * 设置是否显示邮箱
         */
        public void setShowEmail(boolean showEmail) {
            this.isShowEmail = showEmail;
        }

        /**
         * 是否展示站长赚钱计划
         */
        public Builder setShowEarnPlan(boolean showEarnPlan) {
            this.showEarnPlan = showEarnPlan;
            return this;
        }

        public ShareOption build() {
            return new ShareOption(this);
        }
    }
}
