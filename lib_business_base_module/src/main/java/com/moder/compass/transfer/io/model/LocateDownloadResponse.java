package com.moder.compass.transfer.io.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.dubox.drive.network.base.BaseResponse;
import com.moder.compass.transfer.transmitter.ErrorMessageHelper;
import com.moder.compass.transfer.transmitter.locate.LocateDownloadUrls;
import com.google.gson.annotations.SerializedName;

/**
 * LocateDownloadResponse
 * 
 * @author 孙奇 <br/>
 *         create at 2013-7-22 下午03:20:03
 */
public class LocateDownloadResponse extends BaseResponse implements Parcelable {
    private static final String TAG = "LocateDownloadResponse";

    @SerializedName("client_ip")
    public String clientIP;

    @SerializedName("sl")
    public long downloadThreshold;

    // PCS的locatedownload下发下来的为error_code，pan的api/locatedownload下发下来的是errno
    @SerializedName(value = "error_code", alternate = "errno")
    public int errorCode;

    // 服务端下发下来的用于交互的文案， PCS的locatedownload下发下来的为error_info
    // pan的api/locatedownload下发下来的是errmsg
    @SerializedName(value = "error_info", alternate = "errmsg")
    public String errorInfo;

    // 标记用户是否为破解用户（被限速）
    @SerializedName("type")
    public String type;

    // 发生错误时端上需要的重试次数
    @SerializedName("redo")
    public int redo = ErrorMessageHelper.NO_REDO_DEFAULT;

    public String host;
    public String path;
    public List<String> server;

    public int httpCode;

    public List<LocateDownloadUrls> urls;

    public LocateDownloadResponse() {
    }

    /**
     * Intent传递时序列化
     * 
     * @param source
     */
    public LocateDownloadResponse(Parcel source) {
        clientIP = source.readString();
        server = source.readArrayList(String.class.getClassLoader());
        host = source.readString();
        path = source.readString();
        urls = source.readArrayList(LocateDownloadUrls.class.getClassLoader());
        downloadThreshold = source.readLong();
        errorCode = source.readInt();
        errorInfo = source.readString();
        type = source.readString();
        redo = source.readInt();
        httpCode = source.readInt();
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
        dest.writeString(clientIP);
        dest.writeStringList(server);
        dest.writeString(host);
        dest.writeString(path);
        dest.writeList(urls);
        dest.writeLong(downloadThreshold);
        dest.writeInt(errorCode);
        dest.writeString(errorInfo);
        dest.writeString(type);
        dest.writeInt(redo);
        dest.writeInt(httpCode);
    }

    /**
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LocateDownloadResponse [clientIP=" + clientIP + ", host=" + host + ", path=" + path + ", server="
                + server + ", urls=" + urls + " ,downloadThreshold=" + downloadThreshold + " ,errorCode=" + errorCode
                + " ,errorInfo=" + errorInfo + " ,type=" + type + "]";
    }

    /**
     * Intent传递时序列化
     */
    public static final Parcelable.Creator<LocateDownloadResponse> CREATOR = new Creator<LocateDownloadResponse>() {

        @Override
        public LocateDownloadResponse[] newArray(int size) {
            return new LocateDownloadResponse[size];
        }

        // 将Parcel对象反序列化为ParcelableDate
        @Override
        public LocateDownloadResponse createFromParcel(Parcel source) {
            return new LocateDownloadResponse(source);
        }
    };

    @Override
    public int getErrorNo() {
        return errorCode;
    }

    @NonNull
    @Override
    public String getRequestId() {
        return "";
    }

    @NonNull
    @Override
    public String getErrorMsg() {
        return errorInfo;
    }

    @Override
    public boolean isSuccess() {
        return errorCode == 0;
    }

    @Override
    public void setHeaderYme(@NonNull String headerYme) {
        //
    }

    @NonNull
    @Override
    public String getHeaderYme() {
        return "";
    }
}
