package com.moder.compass.stats.upload;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2016/9/12.
 */
public class UploadData {
    private static final String TAG = "UploadData";

    private String op;
    private int count;
    private String other0;
    private String other1;
    private String other2;
    private String other3;
    private String other4;
    private String other5;
    private String other6;
    private String opTime;
    private String opParam;

    private UploadData() {

    }

    public UploadData(String op, int count, String other0,
                      String other1, String other2, String other3, String other4,
                      String other5, String other6, String opTime) {
        this.count = count;
        this.other0 = other0;
        this.other1 = other1;
        this.other2 = other2;
        this.other3 = other3;
        this.other4 = other4;
        this.other5 = other5;
        this.other6 = other6;
        this.opTime = opTime;
        this.op = op;
        if (!TextUtils.isEmpty(op) && op.contains(Separator.OP_PARAM_SPLIT)) {
            String[] opParam = op.split(Separator.OP_PARAM_SPLIT);
            if (opParam.length > 0) {
                this.op = opParam[0];
            }
            if (opParam.length > 1) {
                this.opParam = opParam[1];
            }
        }
    }

    public String getOp() {
        return op;
    }

    public String getOpParam() {
        return opParam;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOther0() {
        return other0;
    }

    public String getOther1() {
        return other1;
    }

    public String getOther2() {
        return other2;
    }

    public String getOther3() {
        return other3;
    }

    public String getOther4() {
        return other4;
    }

    public String getOther5() {
        return other5;
    }

    public String getOther6() {
        return other6;
    }

    public String getOpTime() {
        return opTime;
    }

    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }
}
