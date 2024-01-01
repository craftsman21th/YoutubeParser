package com.moder.compass.statistics.activation.io.model;

import com.dubox.drive.network.base.Response;
import com.dubox.drive.kernel.util.NoProguard;
import com.google.gson.annotations.SerializedName;

import android.annotation.SuppressLint;
import android.os.Parcel;

/**
 * Created by libin09 on 2015/7/16.
 */

@SuppressLint("ParcelCreator")
public class ReportUserResponse extends Response implements NoProguard {
    @SerializedName("uinfo")
    public String uinfo;
    @SerializedName("isnew")
    public int isnew = -1;
    @SerializedName("mediainfo")
    public NewUserMediaInfo mediainfo;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public class NewUserMediaInfo implements NoProguard {
        @SerializedName("picurl")
        public String picurl;
        @SerializedName("videourl")
        public String videourl;
        @SerializedName("videoname")
        public String videoname;
    }

}
