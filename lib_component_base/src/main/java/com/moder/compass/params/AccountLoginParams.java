package com.moder.compass.params;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wanglihong01 on 2019/3/25.
 */

public class AccountLoginParams implements Parcelable {

    /** 为用户登录后的bduss YQH 20121214 **/
    public String bduss;

    public String stoken = "dubox";

    /** 用户登录后的passport uid YQH 20121214 **/
    public String bduid;

    /** 头像（可能为空） YQH 20121214 **/
    public String osHeadurl;

    /** 如已绑定，该用户在passport的用户名 YQH 20121214 **/
    public String passportUname;
    /** 用户名（昵称） YQH 20121214 **/
    public String osUsername;

    private int vip;

    /**
     * 当前是使用哪个平台登陆 1 人人网 ;2 新浪微博;4 腾讯微博;15 QQ登录;42 微信登录 YQH 20121214
     * 只能是Facebook登录 固定值100
     **/
    public String osType = "100";

    public AccountLoginParams() {

    }

    protected AccountLoginParams(Parcel in) {
        bduss = in.readString();
        stoken = in.readString();
        bduid = in.readString();
        osHeadurl = in.readString();
        passportUname = in.readString();
        osUsername = in.readString();
        vip = in.readInt();
        osType = in.readString();
    }

    public static final Creator<AccountLoginParams> CREATOR = new Creator<AccountLoginParams>() {
        @Override
        public AccountLoginParams createFromParcel(Parcel in) {
            return new AccountLoginParams(in);
        }

        @Override
        public AccountLoginParams[] newArray(int size) {
            return new AccountLoginParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bduss);
        dest.writeString(stoken);
        dest.writeString(bduid);
        dest.writeString(osHeadurl);
        dest.writeString(passportUname);
        dest.writeString(osUsername);
        dest.writeInt(vip);
        dest.writeString(osType);
    }
}
