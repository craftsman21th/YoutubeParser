
package com.moder.compass.transfer.io.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuliangping on 2015/7/2.
 */
public class ProbationaryButtonInfos extends ProbationaryQualificationsResponse implements Parcelable {
    @SerializedName("incr_calc_type")
    public int mIncrCalcType;

    @SerializedName("before_incr")
    public ProbationaryIncrdetail mBeforeIncr;

    @SerializedName("incring")
    public ProbationaryIncrdetail mIncring;

    @SerializedName("after_incr")
    public ProbationaryIncrdetail mAfterIncr;

    @Override
    public int describeContents() {
        return 0;
    }

    public ProbationaryButtonInfos(Parcel source) {
        mCouldExperience = source.readInt();
        mFreqCnt = source.readInt();
        mIncrCalcType = source.readInt();
        mBeforeIncr = source.readParcelable(ProbationaryIncrdetail.class.getClassLoader());
        mIncring = source.readParcelable(ProbationaryIncrdetail.class.getClassLoader());
        mAfterIncr = source.readParcelable(ProbationaryIncrdetail.class.getClassLoader());
        mRequestId = source.readLong();
        mErrorCode = source.readInt();
        mErrorMsg = source.readString();
        mDuration = source.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mCouldExperience);
        dest.writeInt(mFreqCnt);
        dest.writeInt(mIncrCalcType);
        dest.writeParcelable(mBeforeIncr, flags);
        dest.writeParcelable(mIncring, flags);
        dest.writeParcelable(mAfterIncr, flags);
        dest.writeLong(mRequestId);
        dest.writeInt(mErrorCode);
        dest.writeString(mErrorMsg);
        dest.writeLong(mDuration);
    }

    @Override
    public String toString() {
        return "[mIncrCalcType:" + mIncrCalcType + " mBeforeIncr:" + mBeforeIncr + " mIncring:" + mIncring
                + " mAfterIncr:" + mAfterIncr + "]";
    }

    /**
     * Intent传递时序列化
     */
    public static final Parcelable.Creator<ProbationaryButtonInfos> CREATOR = new Creator<ProbationaryButtonInfos>() {

        @Override
        public ProbationaryButtonInfos[] newArray(int size) {
            return new ProbationaryButtonInfos[size];
        }

        @Override
        public ProbationaryButtonInfos createFromParcel(Parcel source) {
            return new ProbationaryButtonInfos(source);
        }
    };
}
