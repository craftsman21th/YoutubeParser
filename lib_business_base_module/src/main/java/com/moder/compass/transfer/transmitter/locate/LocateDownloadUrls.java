/*
 * LocateDownloadUrls.java
 * @author weizhengzheng
 * V 1.0.0
 * Create at 2013年11月28日 下午1:49:23
 */
package com.moder.compass.transfer.transmitter.locate;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.moder.compass.business.kernel.HostURLManagerKt;

/**
 * LocateDownloadUrls
 * 
 * @author weizhengzheng <br/>
 *         create at 2013年11月28日 下午1:49:23
 */
public class LocateDownloadUrls implements Parcelable {
    private static final String TAG = "LocateDownloadUrls";

    public String url;
    public String host;
    public String rank;

    public LocateDownloadUrls() {

    }

    /**
     * @param url
     */
    public LocateDownloadUrls(String url, boolean isPreview) {
        super();
        this.url = url;

        if (isPreview && !TextUtils.isEmpty(url)) {// 预览时添加参数，以便server跟踪在线消费
            if (url.contains("&")) {
                this.url += "&" + HostURLManagerKt.PREVIEW_PARAM
                        + "&" + HostURLManagerKt.FILE_PREVIEW_PARAM;
            } else {
                this.url += "?" + HostURLManagerKt.PREVIEW_PARAM
                        + "&" + HostURLManagerKt.FILE_PREVIEW_PARAM;
            }
        }
    }

    public LocateDownloadUrls(Parcel source) {
        url = source.readString();
        host = source.readString();
        rank = source.readString();
    }

    /**
     * @return
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @param dest
     * @param flags
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(host);
        dest.writeString(rank);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LocateDownloadUrls [url=" + url + ", host=" + host + ", rank=" + rank + "]";
    }

    public static final Parcelable.Creator<LocateDownloadUrls> CREATOR = new Creator<LocateDownloadUrls>() {

        @Override
        public LocateDownloadUrls[] newArray(int size) {
            return new LocateDownloadUrls[size];
        }

        // 将Parcel对象反序列化为ParcelableDate
        @Override
        public LocateDownloadUrls createFromParcel(Parcel source) {
            return new LocateDownloadUrls(source);
        }
    };
}
