
package com.moder.compass.transfer.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuliangping on 2015/7/2.
 */
public class ProbationaryIncrdetail implements Parcelable {
    @SerializedName("intro_down")
    public String mIntroDown;

    @SerializedName("button_msg_down")
    public String mButtonMsgDown;

    @SerializedName("intro_down_notgood")
    public String mIntroDownNotGood;

    public ProbationaryIncrdetail() {
    }

    public ProbationaryIncrdetail(Parcel source) {
        mIntroDown = source.readString();
        mButtonMsgDown = source.readString();
        mIntroDownNotGood = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIntroDown);
        dest.writeString(mButtonMsgDown);
        dest.writeString(mIntroDownNotGood);
    }

    @Override
    public String toString() {
        return "[mIntroDown:" + mIntroDown + " mButtonMsgDown:" + mButtonMsgDown + " mIntroDownNotGood:"
                + mIntroDownNotGood + "]";
    }

    /**
     * Intent传递时序列化
     */
    public static final Parcelable.Creator<ProbationaryIncrdetail> CREATOR = new Creator<ProbationaryIncrdetail>() {

        @Override
        public ProbationaryIncrdetail[] newArray(int size) {
            return new ProbationaryIncrdetail[size];
        }

        @Override
        public ProbationaryIncrdetail createFromParcel(Parcel source) {
            return new ProbationaryIncrdetail(source);
        }
    };
}
