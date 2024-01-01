package com.moder.compass.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.moder.compass.base.imageloader.GlideHelper;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.util.CollectionUtils;
import com.dubox.drive.kernel.util.ConStantKt;
import com.moder.compass.util.DayNightModeKt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manyongqiang on 2017/7/13.
 * 相对于之前的{@link}， 此处的{@link NewVersionDialog}更新了底部取消和确认按钮的显示样式
 * {@link NewVersionDialog.Builder#setCustomViewId(int)}支持自定义View样式
 * {@link NewVersionDialog}提供多余6个采用滚动条的方式展示。
 *
 */

public class NewVersionDialog {
    private static final String TAG = "NewVersionDialog";
    private static final int DEFAULT_IMAGE = R.drawable.default_user_head_icon;

    private Dialog mDialog;
    private RecyclerView mContentItem;
    private MyAdapter mAdapter;
    private FrameLayout mCustomLayout;

    private Activity mActivity;
    private View mTitleContent;
    private TextView mTitle;
    private ImageView mImageHead;
    private TextView mSubTitle;
    private TextView mContentText;
    private TextView mBottomTitle;

    private View mTwoButtonLayout;
    private Button mCancel;
    private Button mConfirm;

    private View mOneButtonLayout;
    private Button mSingleConfirm;
    private ImageView mImageCloseView;

    private OnItemClickListener mItemClickListener;
    private OnClickListener mBottomTextViewListener;
    private OnClickListener mCancelListener;
    private OnClickListener mConfirmListener;
    private OnCancelShowDialogListener mCancelShowDialogListener;

    private OnClickListener mSingleConfirmListener;
    private OnCreateCustomViewListener mCreateCustomViewListener;
    private ImageLoader mImageLoader;


    private NewVersionDialog(final Builder builder) {
        mActivity = builder.activity;
        mDialog = new Dialog(mActivity, R.style.ModerDialogTheme);
        mCreateCustomViewListener = builder.createCustomViewListener;
        mImageLoader = builder.imageLoader;
        initDialogView();
        // 设置对话框内容
        if (builder.title != null && !builder.title.isEmpty()) {
            mTitleContent.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(builder.title);
        }
        if (builder.imageHeadResId != -1 || !TextUtils.isEmpty(builder.imageHeadUrl)) {
            mTitleContent.setVisibility(View.VISIBLE);
            if (builder.imageHeadResId != -1) {
                mImageHead.setVisibility(View.VISIBLE);
                mImageHead.setImageResource(builder.imageHeadResId);
            } else {
                mImageHead.setVisibility(View.VISIBLE);
                setImageResource(mImageHead, builder.imageHeadUrl);
            }
        }

        if (builder.subTitle != null && !builder.subTitle.isEmpty()) {
            mSubTitle.setVisibility(View.VISIBLE);
            mSubTitle.setText(builder.subTitle);
        }
        if (builder.contentText != null && !builder.contentText.isEmpty()) {
            mContentText.setVisibility(View.VISIBLE);
            mContentText.setText(builder.contentText);
        }
        if (builder.contentSpannableString != null) {
            mContentText.setVisibility(View.VISIBLE);
            mContentText.setText(builder.contentSpannableString);
        }
        if (builder.bottomText != null && !builder.bottomText.isEmpty()) {
            mBottomTitle.setVisibility(View.VISIBLE);
            mBottomTitle.setText(builder.bottomText);
        }
        if (builder.cancelText != null && !builder.cancelText.isEmpty()) {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mCancel.setText(builder.cancelText);
        }
        if (builder.confirmText != null && !builder.confirmText.isEmpty()) {
            mTwoButtonLayout.setVisibility(View.VISIBLE);
            mConfirm.setText(builder.confirmText);
        }
        if (builder.singleConfirmText != null && !builder.singleConfirmText.isEmpty()) {
            mOneButtonLayout.setVisibility(View.VISIBLE);
            mSingleConfirm.setText(builder.singleConfirmText);
        }

        if (builder.needShowCloseImage) {
            mImageCloseView.setVisibility(View.VISIBLE);
            mImageCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    notifyCloseDialogByUser();
                }
            });
        }

        if (!CollectionUtils.isEmpty(builder.itemUrl)) {
            mContentItem.setVisibility(View.VISIBLE);
            if (builder.itemUrl.size() < 4) {
                mContentItem.setLayoutManager(new GridLayoutManager(mActivity, builder.itemUrl.size()));
            } else if  (builder.itemUrl.size() == 4) {
                mContentItem.setLayoutManager(new GridLayoutManager(mActivity, 2)); // 4个分成两排显示
            } else {
                mContentItem.setLayoutManager(new GridLayoutManager(mActivity, 3));
            }
            mAdapter.setData(builder.itemUrl);
        }

        if (builder.customViewId != -1) { // 默认-1
            mCustomLayout.setVisibility(View.VISIBLE);
            View customView = LayoutInflater.from(mActivity).inflate(builder.customViewId, mCustomLayout);
            if (mCreateCustomViewListener != null) {
                mCreateCustomViewListener.onCreate(customView);
            }
        }

        mCancelListener = builder.cancelListener;
        mConfirmListener = builder.confirmListener;
        mBottomTextViewListener = builder.bottomTextViewListener;
        mSingleConfirmListener = builder.singleConfirmListener;
        mItemClickListener = builder.itemClickListener;
        if (builder.showListener != null) {
            mDialog.setOnShowListener(builder.showListener);
        }
        if (builder.dismissListener != null) {
            mDialog.setOnDismissListener(builder.dismissListener);
        }
        mCancelShowDialogListener = builder.cancelShowDialogListener;

        // cancelOnTouchOutside默认设置false 即点击对话框外面不关闭对话框
        mDialog.setCanceledOnTouchOutside(builder.cancelOnTouchOutside);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                notifyCloseDialogByUser();
            }
        });
        // needShiedReturnKey默认false 不屏蔽返回按键，即点击返回按键关闭对话框
        mDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // 弹出该对话框时屏蔽返回按键
                if (!builder.needShiedReturnKey && mCancelShowDialogListener != null) {
                    notifyCloseDialogByUser();
                }
                return (builder.needShiedReturnKey && keyCode == KeyEvent.KEYCODE_BACK);
            }
        });
    }

    private void initDialogView() {
        View contentView;
        contentView = LayoutInflater.from(mActivity).inflate(R.layout.new_version_dialog, null);

        mTitleContent = contentView.findViewById(R.id.title_content);
        mTitle = (TextView) contentView.findViewById(R.id.title);
        mImageHead = (ImageView) contentView.findViewById(R.id.image_title);
        mSubTitle = (TextView) contentView.findViewById(R.id.sub_title);
        mContentText = (TextView) contentView.findViewById(R.id.content_text);
        mBottomTitle = (TextView) contentView.findViewById(R.id.bottom_title);
        mBottomTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mBottomTextViewListener != null) {
                    mBottomTextViewListener.onClick();
                }
            }
        });

        mTwoButtonLayout = contentView.findViewById(R.id.bottom_two_button_layout);
        mCancel = (Button) contentView.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCancelListener != null) {
                    mCancelListener.onClick();
                }

                dismiss();
            }
        });
        mConfirm = (Button) contentView.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mConfirmListener != null) {
                    mConfirmListener.onClick();
                }

                dismiss();
            }
        });
        mImageCloseView = (ImageView) contentView.findViewById(R.id.img_close);

        mOneButtonLayout = contentView.findViewById(R.id.bottom_one_button_layout);
        mSingleConfirm = (Button) contentView.findViewById(R.id.single_confirm_button);
        mSingleConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSingleConfirmListener != null) {
                    mSingleConfirmListener.onClick();
                }
                dismiss();
            }
        });

        mCustomLayout = (FrameLayout) contentView.findViewById(R.id.customContent);
        mContentItem = (RecyclerView) contentView.findViewById(R.id.content_item);
        initContentItemView();

        mDialog.setContentView(contentView);
        // 设置对话框属性
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = mActivity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        if (mDialog.getWindow() != null) {
            mDialog.getWindow().setGravity(Gravity.CENTER);
            mDialog.getWindow().setWindowAnimations(R.style.NewVersionDialog_Animation);
        }
    }

    /**
     * 初始化contentView视图
     */
    private void initContentItemView() {
        mAdapter = new MyAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
        mContentItem.setLayoutManager(layoutManager);
        mContentItem.setAdapter(mAdapter);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnClickListener {
        void onClick();
    }

    /**
     * 在dialog显示的时候用户点击屏幕外面，或者返回按键时触发的监听器
     */
    public interface OnCancelShowDialogListener {
        void onCancelShowDialog();
    }

    public interface OnCreateCustomViewListener {
        void onCreate(View customView);
    }

    private void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * 用户点击对话框的右上角、点击dialog外部区域、点击返回按键导致对话框消失触发该方法，告知用户
     */
    private void notifyCloseDialogByUser() {
        if (mCancelShowDialogListener != null) {
            mCancelShowDialogListener.onCancelShowDialog();
        }
    }

//    @Override
//    public void onClick(View v) {
//        dismiss();
//        switch (v.getId()) {
//            case R.id.bottom_title:
//                if (mBottomTextViewListener != null) {
//                    mBottomTextViewListener.onClick();
//                }
//                break;
//            case R.id.cancel:
//                if (mCancelListener != null) {
//                    mCancelListener.onClick();
//                }
//                break;
//            case R.id.confirm:
//                if (mConfirmListener != null) {
//                    mConfirmListener.onClick();
//                }
//                break;
//            case R.id.single_confirm_button:
//                if (mSingleConfirmListener != null) {
//                    mSingleConfirmListener.onClick();
//                }
//                break;
//            case R.id.img_close:
//                dismiss();
//                notifyCloseDialogByUser();
//                break;
//            default:
//                // un support
//                break;
//        }
//    }

    private void setImageResource(ImageView imageView, String imageUrl) {
        if (mImageLoader != null) {
            mImageLoader.displayImageFromNetwork(imageView, imageUrl);
        } else {
            DEFAULT_IMAGE_LOADER.displayImageFromNetwork(imageView, imageUrl);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private static final int BIGGER_SIZE = 100;
        private static final int MIDDLE_SIZE = 101;
        private static final int NORMAL_SIZE = 102;
        private static final int MAX_ITEM_NUMBER = 6; // 一次性最多显示6个item

        private List<String> mItems = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_information,
                    parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final String item = mItems.get(position);
            updateImageSize(holder.imageView);
            setImageResource(holder.imageView, item);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        dismiss();
                        mItemClickListener.onItemClick(holder.getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (CollectionUtils.isEmpty(mItems)) {
                return 0;
            }
            return mItems.size();
        }

        private void updateImageSize(ImageView view) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            switch (getImageSizeType(mItems.size())) {
                case BIGGER_SIZE:
                    params.height = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_single_image_size);
                    params.width = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_single_image_size);
                    break;
                case MIDDLE_SIZE:
                    params.height = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_two_image_size);
                    params.width = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_two_image_size);
                    break;
                default:
                    params.height = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_three_image_size);
                    params.width = mActivity.getResources()
                            .getDimensionPixelSize(R.dimen.new_dialog_three_image_size);
                    break;
            }
            view.setLayoutParams(params);
        }

        private int getImageSizeType(int dataLength) {
            switch (dataLength) {
                case 1:
                    return BIGGER_SIZE;
                case 4:
                case 2:
                    return MIDDLE_SIZE;
                default:
                    return NORMAL_SIZE;
            }
        }

        public void setData(List<String> data) {
            if (data.size() > MAX_ITEM_NUMBER) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_grid_information,
                        mContentItem, false);
                int width = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                view.measure(width, height);
                ViewGroup.LayoutParams params = mContentItem.getLayoutParams();
                params.height = (int) (view.getMeasuredHeight() * (3 + 0.75)); // 最多显示3行
                mContentItem.setLayoutParams(params);
                mContentItem.setPadding(0, 0, 0, mActivity.getResources().getDimensionPixelSize(R.dimen.choice_dialog_padding_bottom));
            }
            mItems = data;
            notifyDataSetChanged();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.image);
            }
        }
    }

    public static class Builder {
        private Activity activity;
        private String title;
        private boolean needShowCloseImage =  false;
        private int imageHeadResId = -1;
        private String imageHeadUrl;
        private String subTitle;
        private String contentText;
        private SpannableStringBuilder contentSpannableString;
        private String bottomText;
        private String cancelText;
        private String confirmText;
        private String singleConfirmText;
        private OnClickListener cancelListener;
        private OnClickListener confirmListener;
        private OnClickListener bottomTextViewListener;
        private OnClickListener singleConfirmListener;
        private OnItemClickListener itemClickListener;
        private OnShowListener showListener;
        private OnDismissListener dismissListener;
        private OnCancelShowDialogListener cancelShowDialogListener;
        private OnCreateCustomViewListener createCustomViewListener;
        private List<String> itemUrl;
        private int customViewId = -1;
        private boolean cancelOnTouchOutside = false;
        private boolean needShiedReturnKey = false;
        private ImageLoader imageLoader;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setActivity(@NonNull Activity activity) {
            this.activity =  activity;
            return this;
        }

        /**
         * 设置显示dialog的关闭ImageView；
         */
        public Builder needShowCloseImageView() {
            needShowCloseImage = true;
            return this;
        }

        public Builder setTitle(int resId) {
            title = activity.getResources().getString(resId);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setImageHeadResId(int imageHeadResId) {
            this.imageHeadResId = imageHeadResId;
            return this;
        }

        public void setImageHeadUrl(String imageHeadUrl) {
            this.imageHeadUrl = imageHeadUrl;
        }

        public Builder setSubTitle(int resId) {
            this.subTitle = activity.getResources().getString(resId);
            return this;
        }

        public Builder setSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder setContentText(int resId) {
            this.contentText = activity.getResources().getString(resId);
            return this;
        }

        public Builder setContentText(String contentText) {
            this.contentText = contentText;
            return this;
        }

        public Builder setContentText(SpannableStringBuilder contentText) {
            this.contentSpannableString = contentText;
            return this;
        }

        public Builder setBottomText(int resId) {
            this.bottomText = activity.getResources().getString(resId);
            return this;
        }

        public Builder setBottomText(String bottomText) {
            this.bottomText = bottomText;
            return this;
        }

        public Builder setCancelText(int resId) {
            this.cancelText = activity.getResources().getString(resId);
            return this;
        }

        public Builder setCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder setConfirmText(int resId) {
            this.confirmText = activity.getResources().getString(resId);
            return this;
        }

        public Builder setConfirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        public Builder setSingleConfirmText(int resId) {
            this.singleConfirmText = activity.getResources().getString(resId);
            return this;
        }

        public Builder setSingleConfirmText(String singleConfirmText) {
            this.singleConfirmText = singleConfirmText;
            return this;
        }

        public Builder setCancelListener(OnClickListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setConfirmListener(OnClickListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

        public void setBottomTextViewListener(OnClickListener bottomTextViewListener) {
            this.bottomTextViewListener = bottomTextViewListener;
        }

        public Builder setSingleConfirmListener(OnClickListener singleConfirmListener) {
            this.singleConfirmListener = singleConfirmListener;
            return this;
        }

        public Builder setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public Builder setShowListener(OnShowListener showListener) {
            this.showListener = showListener;
            return this;
        }

        public Builder setDismissListener(OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        public Builder setCancelShowDialogListener(
                OnCancelShowDialogListener cancelShowDialogListener) {
            this.cancelShowDialogListener = cancelShowDialogListener;
            return this;
        }

        public Builder setCreateCustomViewListener(
                OnCreateCustomViewListener createCustomViewListener) {
            this.createCustomViewListener = createCustomViewListener;
            return this;
        }

        public Builder setItemUrl(List<String> itemUrl) {
            this.itemUrl = itemUrl;
            return this;
        }

        public Builder setCustomViewId(int customViewId) {
            this.customViewId = customViewId;
            return this;
        }

        public Builder setCancelOnTouchOutside(boolean cancelOnTouchOutside) {
            this.cancelOnTouchOutside = cancelOnTouchOutside;
            return this;
        }

        public Builder setNeedShiedReturnKey(boolean needShiedReturnKey) {
            this.needShiedReturnKey = needShiedReturnKey;
            return this;
        }

        public Builder setImageLoader(ImageLoader imageLoader) {
            this.imageLoader = imageLoader;
            return this;
        }

        public Dialog show() {
            NewVersionDialog dialog = new NewVersionDialog(Builder.this);
            dialog.mDialog.show();
            float radius = DeviceDisplayUtils.dip2px(activity, ConStantKt.SPACE_11);
            DayNightModeKt.setDayOrNightModeForDialog(dialog.mDialog, radius, radius, radius, radius);
            return dialog.mDialog;
        }
    }

    public interface ImageLoader {
        void displayImageFromNetwork(ImageView imageView, String url);
    }

    private static final ImageLoader DEFAULT_IMAGE_LOADER = new ImageLoader() {
        @Override
        public void displayImageFromNetwork(ImageView imageView, String url) {
            GlideHelper.getInstance().displayImageFromNetwork(url, DEFAULT_IMAGE, DEFAULT_IMAGE,
                    DEFAULT_IMAGE, true, imageView, null);
        }
    };
}
