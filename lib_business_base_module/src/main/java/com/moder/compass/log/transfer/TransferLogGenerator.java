package com.moder.compass.log.transfer;

import android.text.TextUtils;

import com.moder.compass.log.ILogGenerator;

import androidx.annotation.NonNull;

/**
 * Created by liuliangping on 2016/3/22.
 */
public abstract class TransferLogGenerator<D extends TransferLog> implements ILogGenerator<D> {
    private static final String TAG = "TransferLogGenerator";

    @Override
    public String generator(D field) {
        if (TransferFieldKey.FileTypeKey.TYPE.equals(field.getLogUploadType())) {
            String header = getFileListHeader(field);
            if (!TextUtils.isEmpty(header)) {
                if (!header.endsWith(field.getFieldSeparator())) {
                    header += field.getFieldSeparator();
                }
                return header + getFileListField(field);
            }
            return getFileListField(field);
        }
        return getBlockListField(field);
    }

    @Override
    public void clear(D field) {
        // 只有删除或者完成才需要删除记录
        if (field.getFinishStates() == TransferFieldKey.TRANSFER_FINISH
                || field.getFinishStates() == TransferFieldKey.TRANSFER_REMOVE) {
            field.clear();
        }
    }

    /**
     * File 纬度
     * 
     * @param field
     * @return
     */
    protected abstract String getFileListField(D field);

    /**
     * Block 纬度
     * 
     * @param field
     * @return
     */
    protected abstract String getBlockListField(D field);

    protected String getTimeString(long time) {
        return String.valueOf((long) Math.ceil((double) time / 1000));
    }

    /**
     * 为了保证精度选择使用double强转一次
     * 
     * @param start
     * @param end
     * @return
     */
    protected long getIntervalTime(long end, long start) {
        long castTime = (long) Math.ceil((double) (end - start) / 1000);
        return castTime > 0L ? castTime : 1L;
    }

    /**
     * 用于生成一些特殊的前缀信息
     * 
     * @param field D
     * @return String headerData
     */
    protected String getFileListHeader(D field) {
        return "";
    }

    class DataConnector {
        private final StringBuffer mStringBuffer;

        DataConnector() {
            mStringBuffer = new StringBuffer();
        }

        public void append(@NonNull String key, @NonNull String value, @NonNull String separator) {
            mStringBuffer.append(key);
            mStringBuffer.append("=");
            mStringBuffer.append(value);
            mStringBuffer.append(separator);
        }

        public void append(@NonNull String key, @NonNull String value) {
            mStringBuffer.append(key);
            mStringBuffer.append("=");
            mStringBuffer.append(value);
        }

        public String generatorResult(boolean separate) {
            if (separate) {
                return generatorResult();
            }
            return mStringBuffer.toString();
        }

        public String generatorResult() {
            mStringBuffer.append("\r\n");
            return mStringBuffer.toString();
        }
    }
}
