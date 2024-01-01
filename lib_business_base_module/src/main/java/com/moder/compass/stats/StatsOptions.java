package com.moder.compass.stats;

/**
 * Created by liuliangping on 2016/9/14.
 */
public final class StatsOptions {
    private String mFileName;
    private String mUploadKey;
    private int mSourceType;
    private boolean mIsOnlyWifiSend;
    private int mMaxMemoryCount;
    private int mReportType;
    private String mJobName;
    private boolean mDebugSave;
    private int mCompressType;
    private int mMaxReportCount;

    private StatsOptions(Builder builder) {
        mFileName = builder.fileName;
        mUploadKey = builder.uploadKey;
        mSourceType = builder.sourceType;
        mIsOnlyWifiSend = builder.onlyWifiSend;
        mMaxMemoryCount = builder.maxMemoryCount;
        mReportType = builder.reportType;
        mJobName = builder.jobName;
        mDebugSave = builder.debugSave;
        mCompressType = builder.compressType;
        mMaxReportCount = builder.maxReportCount;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getUploadKey() {
        return mUploadKey;
    }

    public int getSourceType() {
        return mSourceType;
    }

    public boolean isOnlyWifiSend() {
        return mIsOnlyWifiSend;
    }

    public int getMaxMemoryCount() {
        return mMaxMemoryCount;
    }

    public int getReportType() {
        return mReportType;
    }

    public int getCompressType() {
        return mCompressType;
    }

    public String getJobName() {
        return mJobName;
    }

    public boolean isDebugSave() {
        return mDebugSave;
    }

    public int getMaxReportCount() {
        return mMaxReportCount;
    }

    public static final class Builder {
        private String fileName;
        private String uploadKey;
        private int sourceType;
        private boolean onlyWifiSend = false;
        private int reportType;
        private int maxMemoryCount = 30;
        private String jobName;
        private boolean debugSave = false;
        private int compressType;
        private int maxReportCount = 100; // 默认最大100条

        /**
         * 文件名
         *
         * @param fileName
         * @return
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 上传的key
         *
         * @param uploadKey
         * @return
         */
        public Builder setUploadKey(String uploadKey) {
            this.uploadKey = uploadKey;
            return this;
        }

        /**
         * 上传的来源
         *
         * @param sourceType
         * @return
         */
        public Builder setSourceType(int sourceType) {
            this.sourceType = sourceType;
            return this;
        }

        /**
         * 是否仅wifi发送
         *
         * @param isOnlyWifiSend
         * @return
         */
        public Builder setOnlyWifiSend(boolean isOnlyWifiSend) {
            this.onlyWifiSend = isOnlyWifiSend;
            return this;
        }

        /**
         * 最大的内存数量
         *
         * @param maxCount
         * @return
         */
        public Builder setMaxMemoryCount(int maxCount) {
            this.maxMemoryCount = maxCount;
            return this;
        }

        /**
         * 每次上报的最大条数
         *
         * @param maxCount
         * @return
         */
        public Builder setMaxReportCount(int maxCount) {
            this.maxReportCount = maxCount;
            return this;
        }

        /**
         * 设置上报类型
         *
         * @param reportType
         * @return
         */
        public Builder setReportType(int reportType) {
            this.reportType = reportType;
            return this;
        }

        /**
         * 设置上报类型
         *
         * @param compressType
         * @return
         */
        public Builder setCompressType(int compressType) {
            this.compressType = compressType;
            return this;
        }

        /**
         * 设置job name
         * @param jobName
         * @return
         */
        public Builder setJobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public Builder setSaveLog2StorageOnDebug(boolean save) {
            this.debugSave = save;
            return this;
        }

        public StatsOptions build() {
            return new StatsOptions(this);
        }
    }
}
