package com.moder.compass.transfer.transmitter;

import com.google.gson.annotations.SerializedName;

/**
 * 图片上传的exif信息
 *
 * Created by liuliangping on 2015/11/4.
 */
public class ExifInterfaceBean {
    @SerializedName("parent_path")
    public String mParentPath;

    @SerializedName("icloud")
    public String mIcloud;

    @SerializedName("manual_type")
    public String mManualType;

    @SerializedName("date_time_original")
    public String mDateTimeOriginal;

    @SerializedName("date_time_digitized")
    public String mDateTimeDigitized;

    @SerializedName("date_time")
    public String mDateTime;

    @SerializedName("orientation")
    public int mOrientation;

    @SerializedName("latitude")
    public String mLatitude;

    @SerializedName("longitude")
    public String mLongitude;

    @SerializedName("latitude_ref")
    public String mLatitudeRef;

    @SerializedName("longitude_ref")
    public String mLongitudeRef;

    @SerializedName("model")
    public String mModel;

    @SerializedName("width")
    public int mWidth;

    @SerializedName("height")
    public int mHeight;

    @SerializedName("recovery")
    public int mRecovery;

    @SerializedName("make")
    public String mMake;

    @SerializedName("scene_type")
    public String mSceneType;

    @SerializedName("flash")
    public String mFlash;

    @SerializedName("exposure_time")
    public String mExposureTime;

    @SerializedName("iso_speed_ratings")
    public String mIsoSpeedRatings;

    @SerializedName("fnumber")
    public String mFNumber;

    @SerializedName("shutter_speed_value")
    public String mShutterSpeedValue;

    @SerializedName("white_balance")
    public double mWhiteBalance;

    @SerializedName("focal_length")
    public String mFocalLength;

    @SerializedName("gps_altitude")
    public String mGpsAltitude;

    @SerializedName("gps_altitude_ref")
    public String mGPSAltitudeRef;

    @SerializedName("gps_img_direction")
    public String mGPSImgDirection;

    @SerializedName("gps_img_direction_ref")
    public String mGPSImgDirectionRef;

    @SerializedName("gps_timestamp")
    public String mGPSTimeStamp;

    @SerializedName("gps_datastamp")
    public String mGPSDateStamp;

    @SerializedName("gps_processing_method")
    public String mGPSProcessingMethod;
}
