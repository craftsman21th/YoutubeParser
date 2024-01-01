package com.moder.compass.plugins;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by libin on 2017/10/31.
 */

public class PluginCloudFile implements Parcelable {
    public final String path;
    public final String md5;
    public final long fid;
    public final long size;

    public PluginCloudFile(String path, String md5, long fid, long size) {
        this.path = path;
        this.md5 = md5;
        this.fid = fid;
        this.size = size;
    }

    private PluginCloudFile(Parcel in) {
        path = in.readString();
        md5 = in.readString();
        fid = in.readLong();
        size = in.readLong();
    }

    public static final Creator<PluginCloudFile> CREATOR = new Creator<PluginCloudFile>() {
        @Override
        public PluginCloudFile createFromParcel(Parcel in) {
            return new PluginCloudFile(in);
        }

        @Override
        public PluginCloudFile[] newArray(int size) {
            return new PluginCloudFile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(md5);
        dest.writeLong(fid);
        dest.writeLong(size);
    }
}
