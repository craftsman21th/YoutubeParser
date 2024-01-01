package com.moder.compass.ui.preview.image;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 图片预览
 * 
 * @author tianzengming <br/>
 *         Created by tianzengming on 2017/7/14.
 */

public class ImagePreviewExtras implements Parcelable {

    public static final String IMAGE_PREVIEW_EXTRAS = "image_preview_extras";

    /** 上报与否，按照当前类型的PagerActivity默认值不改变 */
    private static final int RECENT_REPORT_DEFAULT = 0;
    /** 上报打开 */
    public static final int RECENT_REPORT_ON = 1;
    /** 上报关闭 */
    public static final int RECENT_REPORT_OFF = 2;

    public Rect viewRect;
    /** 上报开关设置 */
    public int reportAction = RECENT_REPORT_DEFAULT;

    public static final int FROM_RECENT = 1;

    /**
     * 是否从最近列表进入
     */
    private int isFromRecent;

    public boolean showDelete = true;

    public boolean hasLocalFile = false;

    public ImagePreviewExtras() {

    }
    protected ImagePreviewExtras(Parcel in) {
        viewRect = in.readParcelable(Rect.class.getClassLoader());
        reportAction = in.readInt();
        isFromRecent = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(viewRect, flags);
        dest.writeInt(reportAction);
        dest.writeInt(isFromRecent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImagePreviewExtras> CREATOR = new Creator<ImagePreviewExtras>() {
        @Override
        public ImagePreviewExtras createFromParcel(Parcel in) {
            return new ImagePreviewExtras(in);
        }

        @Override
        public ImagePreviewExtras[] newArray(int size) {
            return new ImagePreviewExtras[size];
        }
    };
}
