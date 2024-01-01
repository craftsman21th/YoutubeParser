package com.moder.compass.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TypeItem implements Parcelable, Serializable {
    public String text;
    public int resourceId;
    public int typeId;
    public int resPressedId;

    private OnItemUpdateListener mOnItemUpdateListener;

    public TypeItem(String text, int resourceId, int resPressedId, int typeId) {
        super();
        this.text = text;
        this.resourceId = resourceId;
        this.resPressedId = resPressedId;
        this.typeId = typeId;
    }

    public TypeItem(String text, int resourceId, int typeId) {
        super();
        this.text = text;
        this.resourceId = resourceId;
        this.resPressedId = -1;
        this.typeId = typeId;
    }

    protected TypeItem(Parcel in) {
        text = in.readString();
        resourceId = in.readInt();
        typeId = in.readInt();
        resPressedId = in.readInt();
    }

    public static final Creator<TypeItem> CREATOR = new Creator<TypeItem>() {
        @Override
        public TypeItem createFromParcel(Parcel in) {
            return new TypeItem(in);
        }

        @Override
        public TypeItem[] newArray(int size) {
            return new TypeItem[size];
        }
    };

    public void updateItem(String text, int resourceId, int typeId) {
        this.text = text;
        this.resourceId = resourceId;
        this.typeId = typeId;
        if (mOnItemUpdateListener != null) {
            mOnItemUpdateListener.onUpdateItem();
        }
    }

    public void dismiss() {
        if (mOnItemUpdateListener != null) {
            mOnItemUpdateListener.onDismiss(this);
        }
    }

    public void setOnItemUpdateListener(OnItemUpdateListener onItemUpdateListener) {
        mOnItemUpdateListener = onItemUpdateListener;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(resourceId);
        dest.writeInt(typeId);
        dest.writeInt(resPressedId);
    }

    public interface OnItemUpdateListener {
        void onUpdateItem();

        void onDismiss(TypeItem item);
    }
}
