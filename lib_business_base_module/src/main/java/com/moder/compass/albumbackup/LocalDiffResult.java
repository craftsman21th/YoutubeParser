package com.moder.compass.albumbackup;

import java.util.ArrayList;

import com.dubox.drive.kernel.util.CollectionUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 本地diff结果
 *
 * Created by huantong on 2018/5/22.
 */
public class LocalDiffResult implements Parcelable {
    /**
     * 新增
     */
    public ArrayList<MediaFile> insertList;
    /**
     * 删除
     */
    public ArrayList<String> deleteList;

    public LocalDiffResult() {
        insertList = new ArrayList<MediaFile>();
        deleteList = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private LocalDiffResult(Parcel source) {
        insertList = source.readArrayList(MediaFile.class.getClassLoader());
        deleteList = source.readArrayList(String.class.getClassLoader());
    }

    /**
     * 获取要备份任务的size大小，如果=0就提示没有备份的任务
     *
     * @return
     */
    public int getSize() {
        if (CollectionUtils.isEmpty(insertList)) {
            return 0;
        }
        return insertList.size();
    }

    public void clear() {
        if (insertList != null) {
            insertList.clear();
        }
    }

    /**
     * Intent传递时序列化
     */
    public static final Creator<LocalDiffResult> CREATOR =
            new Creator<LocalDiffResult>() {

                @Override
                public LocalDiffResult[] newArray(int size) {
                    return new LocalDiffResult[size];
                }

                // 将Parcel对象反序列化为ParcelableDate
                @Override
                public LocalDiffResult createFromParcel(Parcel source) {
                    return new LocalDiffResult(source);
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(insertList);
        dest.writeList(deleteList);
    }
}
