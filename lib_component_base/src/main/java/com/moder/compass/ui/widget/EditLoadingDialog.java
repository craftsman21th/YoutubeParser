package com.moder.compass.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.moder.compass.component.base.R;
import com.dubox.drive.kernel.android.util.TextTools;
import com.dubox.drive.kernel.android.util.deviceinfo.DeviceDisplayUtils;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.mars.united.widget.LengthLimitedEditText;
import com.moder.compass.util.DayNightModeKt;

/**
 * 编辑LOADING对话框
 * <p>
 * Title<br>
 * EditText<br>
 * Loading<br>
 * Btn Btn
 *
 * @author 孙奇 <br/>
 *         create at 2013-3-29 下午03:07:35
 */
public class EditLoadingDialog extends Dialog {
    private static final String TAG = "EditLoadingDialog";

    private LengthLimitedEditText mEditText;
    private Button mRightBtn;
    private Button mLeftBtn;
    private TextView mTitle;
    private TextView mSubtitleTitle;
    private LinearLayout mLoadingBox;
    private ImageView mIconBtn;
    private CheckBox mShowPassword;
    private View mShowPasswordLayout;
    private boolean mAcceptEmpty = false;
    private TextView mInputNumText;
    private ImageView mDeleteEditText;
    private final Type mType;

    // 记录在黑暗模式下相应的颜色id
    private int mInputTextNumColor = R.color.blue_black;

    public enum Type {
        NORMAL, CHECKBOX, PASSWORD, DARKMODEL
    }

    public static EditLoadingDialog build(Context context) {
        return new EditLoadingDialog(context);
    }

    /**
     * 创建一个带类型的dialog
     *
     * @param context
     * @return
     */
    public static EditLoadingDialog build(Context context, Type type) {
        return new EditLoadingDialog(context, type);
    }

    protected EditLoadingDialog(final Context context) {
        super(context, R.style.ModerDialogTheme);
        mType = Type.NORMAL;
        initDialog(context);
    }

    protected EditLoadingDialog(final Context context, Type type) {
        super(context, R.style.ModerDialogTheme);
        mType = type;
        initDialog(context);
        initAboutPassword();
    }

    private void initDialog(Context context) {
        final View view;
        if (mType == Type.PASSWORD || mType == Type.CHECKBOX) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.dialog_password_edit_layout, null);
        } else if (mType == Type.DARKMODEL) {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.dialog_edit_dark_layout, null);
        } else {
            view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.dialog_edit_layout, null);
        }
        setContentView(view);
        mEditText = (LengthLimitedEditText) findViewById(R.id.input_edittext);
        mEditText.setSelection(mEditText.getText().length());
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        mInputNumText = (TextView) findViewById(R.id.input_text_num);
        mRightBtn = (Button) findViewById(R.id.alertdialog_btn_confirm);
        mLeftBtn = (Button) findViewById(R.id.alertdialog_btn_cancel);
        mTitle = (TextView) findViewById(R.id.txt_confirmdialog_title);
        mSubtitleTitle = (TextView) findViewById(R.id.txt_confirmdialog_subtitle_title);
        mLoadingBox = (LinearLayout) findViewById(R.id.loadingBox);
        mIconBtn = (ImageView) findViewById(R.id.button_icon);
        mShowPassword = (CheckBox) findViewById(R.id.show_password_view);
        mShowPasswordLayout = findViewById(R.id.show_password_layout);
        mDeleteEditText = findViewById(R.id.delete_edit_text);
        mDeleteEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText(null);
            }
        });

        switch2NormalMode();
        setCanceledOnTouchOutside(false);
        setLeftBtnOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initAboutPassword() {
        if (mShowPasswordLayout != null) {
            mShowPasswordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowPassword.setChecked(!mShowPassword.isChecked());
                }
            });

            if (mType == Type.PASSWORD) {
                mEditText.setTransformationMethod(mShowPassword.isChecked()
                        ? HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
            }
        }
        if (mShowPassword != null) {
            mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mEditText.setTransformationMethod(isChecked
                            ? HideReturnsTransformationMethod.getInstance() :
                            PasswordTransformationMethod.getInstance());
                    String inputStr = mEditText.getText().toString();
                    if (inputStr != null) {
                        mEditText.setSelection(inputStr.length());
                    }
                }
            });
        }
    }

    public void switch2LoadingMode() {
        mEditText.setEnabled(false);
        mEditText.setClickable(false);
        mLoadingBox.setVisibility(View.VISIBLE);
        mRightBtn.setVisibility(View.GONE);
        mLeftBtn.setEnabled(false);
        setCancelable(false);
    }

    public void switch2NormalMode() {
        mEditText.setEnabled(true);
        mEditText.setClickable(true);
        mLoadingBox.setVisibility(View.GONE);
        mRightBtn.setVisibility(View.VISIBLE);
        mLeftBtn.setEnabled(true);
        setCancelable(true);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }

    public void setMaxLength(final int length) {
        mInputNumText.setVisibility(View.VISIBLE);
        // 显示输入文字长度时不显示删除按钮
        mEditText.setCompoundDrawables(null, null, null, null);
        mDeleteEditText.setVisibility(View.GONE);
        mEditText.setPadding(mEditText.getPaddingLeft(), mEditText.getPaddingTop(),
                DeviceDisplayUtils.dip2px(getContext(), 45), mEditText.getPaddingBottom());
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int fetchCharNumber = TextTools.fetchCharNumber(s.toString());
                mRightBtn.setEnabled(mAcceptEmpty || !TextUtils.isEmpty(s));
                if (fetchCharNumber > length) {
                    mInputNumText.setTextColor(Color.RED);
                } else {
                    mInputNumText.setTextColor(getContext().getResources().getColor(mInputTextNumColor));
                }
                mInputNumText.setText((fetchCharNumber + 1) / 2 + "/" + length / 2);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (TextUtils.isEmpty(text.trim())) {
                    s.clear();
                } else {
                    int fetchCharNumber = TextTools.fetchCharNumber(text);
                    DuboxLog.d(TAG, "fetchCharNumber:" + fetchCharNumber + ",length:" + length);
                    final int diffLength = fetchCharNumber - length;
                    if (diffLength > 4) {
                        s = s.delete(s.length() - diffLength / 2, s.length());
                    } else if (diffLength > 0) { // 递归
                        s = s.delete(s.length() - 1, s.length());
                    }
                }
            }
        };
        textWatcher.onTextChanged(mEditText.getText(), 0, 0, 0);
        mEditText.addTextChangedListener(textWatcher);
    }

    public void setRightBtnText(int resid) {
        mRightBtn.setText(resid);
    }

    public void setLeftBtnOnClickListener(android.view.View.OnClickListener l) {
        mLeftBtn.setOnClickListener(l);
    }

    public void setRightBtnOnClickListener(android.view.View.OnClickListener l) {
        mRightBtn.setOnClickListener(l);
    }

    public LengthLimitedEditText getEditText() {
        return this.mEditText;
    }


    public Button getRightBtn() {
        return this.mRightBtn;
    }

    public TextView getTitle() {
        return this.mTitle;
    }

    public void setIcon(int icon) {
        mIconBtn.setVisibility(View.VISIBLE);
        mIconBtn.setImageResource(icon);
    }

    public CheckBox getCheckBox() {
        return mShowPassword;
    }


    @Override
    public void dismiss() {
        switch2NormalMode();
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        float radius = DeviceDisplayUtils.dip2px(getContext(), 12);
        DayNightModeKt.setDayOrNightModeForDialog(this, radius,
                radius, radius, radius);
    }
}
