package com.moder.compass.ui.dialog;

import java.util.List;
import java.util.Map;

import com.moder.compass.component.base.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {
    private Context mContext;
    private int mLayout;
    private List<? extends Map<String, ?>> mItems;
    private LayoutInflater mInflater;
    private int mSelectedIndex = -1;
    private int mType;
    public static final int CHOOSE_APP_LIST_CONTENT = 1; // 选择应用列表
    public static final int CHECK_LIST_CONTENT = 2; // 排序带checkbox
    public static final int CHOOSE_SMS_RESTORE_LIST_CONTENT = 3;// 恢复设备列表
    public static final int ALBUM_OPERATION = 4;// 专辑内点击以后的菜单
    public static final int ONLY_TEXT = 5;// 只有文字的形式
    public static final int CHOOSE_CALLLOG_DEVICE_LIST = 6;
    public static final int TEXT_AND_VIEW_TAG = 7; // 文字+设置tag方式
    public static final String VIEW_TAG = "tag"; // view tag的名称
    public static final String VIEW_DOUBLE_TEXT = "double_text"; // view tag的名称


    public CustomListAdapter(Context context, int layout, List<? extends Map<String, ?>> shareItems, int type) {
        mContext = context;
        mLayout = layout;
        mItems = shareItems;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mType = type;
    }

    public void setSelectedPosition(int position) {
        mSelectedIndex = position;
    }

    public int getSelectedPosition() {
        return mSelectedIndex;
    }

    public int getCount() {
        if (mItems != null && mItems.size() > 0) {
            return mItems.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        if (mItems != null && mItems.size() > 0) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = mInflater.inflate(mLayout, parent, false);
        } else {
            view = convertView;
        }
        Map shareItem = mItems.get(position);
        /*
         * ((ImageView) view.findViewById(R.id.list_image)) .setImageResource((Integer) shareItem.get("icon"));
         */
        CheckBox cb = (CheckBox) view.findViewById(R.id.sort_checkbox);
        if (position == mSelectedIndex) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        if (shareItem.get("text") instanceof String) {
            ((TextView) view.findViewById(R.id.list_text)).setText((CharSequence) shareItem.get("text"));
        } else if (shareItem.get("text") instanceof Integer) {
            ((TextView) view.findViewById(R.id.list_text)).setText((Integer) shareItem.get("text"));
        }
        if (shareItem.get(VIEW_DOUBLE_TEXT) != null && shareItem.get(VIEW_DOUBLE_TEXT) instanceof String) {
            TextView text2 = ((TextView) view.findViewById(R.id.list_text_2));
            text2.setVisibility(View.VISIBLE);
            text2.setText((CharSequence) shareItem.get(VIEW_DOUBLE_TEXT));
        }
        // 设置view的tag
        if (TEXT_AND_VIEW_TAG == mType) {
            view.setTag(shareItem.get(VIEW_TAG));
        }

        if (mType == CHECK_LIST_CONTENT) {
            ((ImageView) view.findViewById(R.id.list_image)).setVisibility(View.GONE);
            ((CheckBox) view.findViewById(R.id.sort_checkbox)).setVisibility(View.VISIBLE);

        } else if (mType == CHOOSE_APP_LIST_CONTENT) {
            ((ImageView) view.findViewById(R.id.list_image)).setVisibility(View.VISIBLE);
            ((ImageView) view.findViewById(R.id.list_image)).setBackgroundDrawable((Drawable) shareItem.get("icon"));
            ((CheckBox) view.findViewById(R.id.sort_checkbox)).setVisibility(View.GONE);
        } else if ((mType == ALBUM_OPERATION) || (ONLY_TEXT == mType) || (TEXT_AND_VIEW_TAG == mType)) {
            ((ImageView) view.findViewById(R.id.list_image)).setVisibility(View.GONE);
            ((CheckBox) view.findViewById(R.id.sort_checkbox)).setVisibility(View.GONE);
        }

        return view;
    }
}
