package com.moder.compass.preview.image;

import static com.moder.compass.imagepager.ImagePagerActivityFromKt.IMAGE_PAGE_VIEW_ALL_VISIBLE;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class PreviewBeanLoaderParams implements Parcelable {
    private static final String TAG = "PreviewBeanLoaderParams";
    public Uri uri;
    public String[] projection;
    public String sort;
    public int type;
    public int position;
    public String selection;
    public String[] selectionArgs;
    public int forwardPos;
    public int backwardPos;
    public boolean isShowUnupload;

    /**
     * @since Terabox 2.12.0
     * 从不同的页面进入大图预览，会控制底部按钮的不同显示，如果使用 type 来区别导致 type 职能过多
     * 代码膨胀，所以新建属性控制底部按钮的显示和隐藏
     */
    public int hideViewPosition = IMAGE_PAGE_VIEW_ALL_VISIBLE;

    // 启动预览的页面
    public String fromPage = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeStringArray(projection);
        dest.writeString(sort);
        dest.writeInt(type);
        dest.writeInt(position);
        dest.writeString(selection);
        dest.writeStringArray(selectionArgs);
        dest.writeInt(forwardPos);
        dest.writeInt(backwardPos);
        dest.writeByte((byte) (isShowUnupload ? 1 : 0));
        dest.writeString(fromPage);
        dest.writeInt(hideViewPosition);
    }

    public void readFromParcel(Parcel src) {
        uri = src.readParcelable(Uri.class.getClassLoader());
        projection = src.createStringArray();
        sort = src.readString();
        type = src.readInt();
        position = src.readInt();
        selection = src.readString();
        selectionArgs = src.createStringArray();
        forwardPos = src.readInt();
        backwardPos = src.readInt();
        isShowUnupload = src.readByte() != 0;
        fromPage = src.readString();
        hideViewPosition = src.readInt();
    }

    public PreviewBeanLoaderParams(Parcel parcel) {
        readFromParcel(parcel);
    }

    public PreviewBeanLoaderParams(Uri uri, String[] projection, String sort, int position, int type) {
        this(uri, projection, null, null, sort, position, type);
    }


    public PreviewBeanLoaderParams(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort,
            int position, int type) {
        this.uri = uri;
        this.projection = projection;
        this.sort = sort;
        this.position = position;
        this.type = type;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
    }

    public PreviewBeanLoaderParams(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort,
                                   int position, int type, boolean isShowUnupload) {
        this(uri, projection, selection, selectionArgs, sort, position, type);
        this.isShowUnupload = isShowUnupload;
    }

    public PreviewBeanLoaderParams(Uri uri, String[] projection, String sort, int position, int type, int forwardPos,
            int backwardPos) {
        this(uri, projection, sort, position, type);
        this.forwardPos = forwardPos;
        this.backwardPos = backwardPos;
    }

    public void setForAndBackwardPos(int forwardPos, int backwardPos) {
        this.forwardPos = forwardPos;
        this.backwardPos = backwardPos;
    }

    public static final Parcelable.Creator<PreviewBeanLoaderParams> CREATOR = new Creator<PreviewBeanLoaderParams>() {

        @Override
        public PreviewBeanLoaderParams[] newArray(int size) {
            return new PreviewBeanLoaderParams[size];
        }

        // 将Parcel对象反序列化为ParcelableDate
        @Override
        public PreviewBeanLoaderParams createFromParcel(Parcel source) {
            return new PreviewBeanLoaderParams(source);
        }
    };

    public interface PreviewFromPage {
        String FROM_FILE_LIST = "file_list"; // 文件列表
        String FROM_TRANSFER_LIST = "transfer_list"; // 传输列表
        String FROM_SEARCH_RESULT = "search_result"; // 搜索结果

    }
}
