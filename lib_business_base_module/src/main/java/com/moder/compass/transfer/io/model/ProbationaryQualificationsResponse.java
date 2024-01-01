
package com.moder.compass.transfer.io.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuliangping on 2015/7/2.
 */
public class ProbationaryQualificationsResponse {
    private static final String TAG = "ProbationaryQualificationsResponse";

    @SerializedName("could_experience")
    public int mCouldExperience;

    @SerializedName("freq_cnt")
    public int mFreqCnt;

    @SerializedName("button_infos")
    public ProbationaryButtonInfos mButtonInfos;

    @SerializedName("request_id")
    public long mRequestId;

    @SerializedName("error_code")
    public int mErrorCode;

    @SerializedName("error_msg")
    public String mErrorMsg;

    @SerializedName("duration")
    public long mDuration;

    /**
     * 是否为免费试用 非下载券
     */
    public boolean mIsSpeedTry;

    @Override
    public String toString() {
        return "[mCouldExperience:" + mCouldExperience + " mFreqCnt:" + mFreqCnt + " mButtonInfos:" + mButtonInfos
                + " mRequestId:" + mRequestId + " mErrorCode:" + mErrorCode + " mErrorMsg:" + mErrorMsg + " mDuration:"
                + mDuration + "]";
    }
}
