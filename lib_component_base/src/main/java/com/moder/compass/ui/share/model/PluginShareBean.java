package com.moder.compass.ui.share.model;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangjie06 on 17/9/28.
 */

public class PluginShareBean implements Parcelable {
    private static final String TAG = "PluginShareBean";
    @SerializedName("sharemethod")
    public String[] mShareMethod;
    @SerializedName("period")
    public boolean mIsNeedPeriod;
    @SerializedName("title")
    public String mTitle;
    @SerializedName("desc")
    public String mDescription;
    @SerializedName("url")
    public String mUrl;
    @SerializedName("thumbpath")
    public String mThumbBitmap;
    @SerializedName("thumburl")
    public String mThumbUrl;
    @SerializedName("dialogtitle")
    public String mDialogTitle;
    @SerializedName("singleimage")
    public boolean mIsSingleImage;
    @SerializedName("linktype")
    public String mLinkType;
    @SerializedName("supportminiprogram")
    public boolean mSupportMiniProgram;
    @SerializedName("pcode")
    public String mPcode;

    public PluginShareBean(String[] shareMethed, boolean isPeriod, String title, String desc, String url,
                           String imgPath, String imgUrl, String linkType, String pcode) {
        mShareMethod = shareMethed;
        mIsNeedPeriod = isPeriod;
        mTitle = title;
        mDescription = desc;
        mUrl = url;
        mThumbBitmap = imgPath;
        mThumbUrl = imgUrl;
        mLinkType = linkType;
        mPcode = pcode;
    }

    public PluginShareBean(String[] mShareMethod, boolean mIsNeedPeriod, String mTitle, String mDescription,
                           String mUrl, String mThumbBitmap, String mThumbUrl, String mDialogTitle,
                           boolean mIsSingleImage,
                           String mLinkType, boolean mSupportMiniProgram, String pcode) {
        this.mShareMethod = mShareMethod;
        this.mIsNeedPeriod = mIsNeedPeriod;
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mUrl = mUrl;
        this.mThumbBitmap = mThumbBitmap;
        this.mThumbUrl = mThumbUrl;
        this.mDialogTitle = mDialogTitle;
        this.mIsSingleImage = mIsSingleImage;
        this.mLinkType = mLinkType;
        this.mSupportMiniProgram = mSupportMiniProgram;
        this.mPcode = pcode;
    }

    protected PluginShareBean(Parcel in) {
        mShareMethod = in.createStringArray();
        mIsNeedPeriod = in.readByte() != 0;
        mTitle = in.readString();
        mDescription = in.readString();
        mUrl = in.readString();
        mThumbBitmap = in.readString();
        mThumbUrl = in.readString();
        mDialogTitle = in.readString();
        mIsSingleImage = in.readByte() != 0;
        mLinkType = in.readString();
        mPcode = in.readString();
    }

    public static final Creator<PluginShareBean> CREATOR = new Creator<PluginShareBean>() {
        @Override
        public PluginShareBean createFromParcel(Parcel in) {
            return new PluginShareBean(in);
        }

        @Override
        public PluginShareBean[] newArray(int size) {
            return new PluginShareBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(mShareMethod);
        dest.writeByte((byte) (mIsNeedPeriod ? 1 : 0));
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mUrl);
        dest.writeString(mThumbBitmap);
        dest.writeString(mThumbUrl);
        dest.writeString(mDialogTitle);
        dest.writeByte((byte) (mIsSingleImage ? 1 : 0));
        dest.writeString(mLinkType);
        dest.writeString(mPcode);
    }
}
