package com.dubox.drive.base.imageloader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by panchao02 on 2019/2/20.
 */

public class SimpleFileInfo implements Parcelable {

    public String mPath;

    public String mMd5;

    public String mUrl;

    public SimpleFileInfo(String path, String md5) {
        mPath = path;
        mMd5 = md5;
    }

    public SimpleFileInfo(String url) {
        mUrl = url;
    }

    protected SimpleFileInfo(Parcel in) {
        mPath = in.readString();
        mMd5 = in.readString();
        mUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeString(mMd5);
        dest.writeString(mUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SimpleFileInfo> CREATOR = new Creator<SimpleFileInfo>() {
        @Override
        public SimpleFileInfo createFromParcel(Parcel in) {
            return new SimpleFileInfo(in);
        }

        @Override
        public SimpleFileInfo[] newArray(int size) {
            return new SimpleFileInfo[size];
        }
    };
}
