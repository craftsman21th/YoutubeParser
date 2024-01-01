package com.moder.compass.stats.upload.network;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.network.FallbackManager;
import com.dubox.drive.base.network.HttpsStatisticsReporter;
import com.dubox.drive.base.network.NetworkException;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.moder.compass.business.kernel.HostURLManager;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.statistics.StatisticsLogForMutilFields;

import android.text.TextUtils;

/**
 * Created by liuliangping on 2016/9/13.
 */
class DuboxStatsReport extends StatsReport {
    private static final String TAG = "DuboxStatsReport";
    /**
     * 统计上传的超时时间
     */
    private static final int CONNECT_TIMEOUT = 30000;

    /**
     * 本地文件读取的超时时间
     */
    private static final int READ_TIMEOUT = 60000;

    /**
     * 服务器返回的状态码的Key 成功为0 其他为错误
     */
    private static final String ERROR_NO = "errno";

    @Override
    public boolean report(byte[] data, String fileName) {
        final ConfigSystemLimit config = ConfigSystemLimit.getInstance();
        if (data == null || data.length == 0) {
            statistics("mUploadData is null", config);
            return false;
        }

        final String boundary = "*****";

        // 读取数据
        try {
            return writeData(buildConnection(boundary), boundary, data, config, fileName);
        } catch (Exception e) {
            DuboxLog.d(TAG, "", e);
        }
        return false;
    }

    /**
     * 上传统计结果的统计
     *
     * @param error  错误
     * @param config 配置项
     * @author libin09 2016-5-3
     * @since 7.13.0
     */
    private void statistics(String error, ConfigSystemLimit config) {
        if (!config.isStatisticsResultUploadEnabled) {
            return;
        }

        // 统计上报失败次数
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.STATISTICS_FAILED);

        // 统计上报失败原因
        StatisticsLogForMutilFields.getInstance().updateCount(
                StatisticsLogForMutilFields.StatisticsKeys.STATISTICS_FAILED_REASON, error,
                String.valueOf(new NetworkException(BaseApplication.getInstance()).checkNetworkExceptionNoTip()));

        // 统计上报成功和失败总次数
        StatisticsLogForMutilFields.getInstance()
                .updateCount(StatisticsLogForMutilFields.StatisticsKeys.STATISTICS_SUCCEEDED_AND_FAILED);
    }

    /**
     * 构建连接
     *
     * @param boundary
     * @return
     */
    private HttpURLConnection buildConnection(String boundary) throws Exception {
        final ConfigSystemLimit config = ConfigSystemLimit.getInstance();
        final FallbackManager fallbackManager = new FallbackManager(config);

        String defaultScheme;
        final int retryTime;
//        if (config.httpsLogSystemEnable) {
            if (fallbackManager.filterHTTPS(HostURLManager.statisticsUrl())) {
                defaultScheme = HostURLManagerKt.PRO_STR_HTTPS;
                retryTime = 1;
            } else {
                defaultScheme = fallbackManager.createScheme();
                if (defaultScheme == null) {
                    retryTime = 2;
                } else {
                    retryTime = 1;
                }
            }
//        } else {
//            defaultScheme = HostURLManagerKt.PRO_STR_HTTP;
//            retryTime = 1;
//        }

        HttpURLConnection con = null;
        // 是否为白名单内接口，这些接口不回退
        boolean isFallbackHttpsDisable;

        // 建立连接
        boolean isSuccess = false;
        for (int i = 0; i < retryTime; i++) {
            final String scheme;
            if (defaultScheme == null) {
                if (i < retryTime - 1) {
                    scheme = HostURLManagerKt.PRO_STR_HTTPS;
                } else {
                    scheme = HostURLManagerKt.PRO_STR_HTTP;
                }
            } else {
                scheme = defaultScheme;
            }

            if (config.httpsLogSystemEnable) {
                fallbackManager.tryToIncreaseCounter(scheme);
            }

            final String url = HostURLManager.getUpdateStatisticsUrl(scheme);

            // 是否为白名单内接口，这些接口不回退
            isFallbackHttpsDisable = fallbackManager.filterHTTPS(url);

            try {
                DuboxLog.d(TAG, "url:" + url);
                URL urlObj = new URL(url);
                con = (HttpURLConnection) urlObj.openConnection();
                if (FallbackManager.HTTPS.equalsIgnoreCase(urlObj.getProtocol())) {
                    registerHttps((HttpsURLConnection) (con));
                }
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setConnectTimeout(CONNECT_TIMEOUT);
                con.setReadTimeout(READ_TIMEOUT);
                con.setRequestMethod("POST");

                con.setRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
                if (Account.INSTANCE.isLogin()) {
                    String bduss = Account.INSTANCE.getNduss();
                    con.addRequestProperty("Cookie", Constants.DUBOX_BDUSS_FIELD_NAME + "=" + bduss);
                }
                con.setRequestProperty("Content-Encoding", "gzip");
                con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                con.setAllowUserInteraction(false);

                // 修复webview 中播放多媒体信息时cookie 错乱的问题.libin09 2017-2-8 7.17.0
                CookieManager.setDefault(new CookieManager());

                con.connect();

                if (config.httpsLogSystemEnable) {
                    fallbackManager.tryToResetCounter(scheme, isFallbackHttpsDisable);
                }
                isSuccess = true;
                break;
            } catch (MalformedURLException e) {
                DuboxLog.e(TAG, "MalformedURLException", e);

                final String error = e.getClass().getName() + ":" + e.getMessage();
                reportError(scheme, isFallbackHttpsDisable, error, config);
                statistics(error, config);
                break;
            } catch (IOException e) {
                DuboxLog.e(TAG, "IOException", e);

                final String error = e.getClass().getName() + ":" + e.getMessage();
                reportError(scheme, isFallbackHttpsDisable, error, config);
                statistics(error, config);
            } catch (SecurityException e) {
                DuboxLog.e(TAG, "SecurityException", e);

                final String error = e.getClass().getName() + ":" + e.getMessage();
                reportError(scheme, isFallbackHttpsDisable, error, config);
                statistics(error, config);
                break;
            } catch (Exception e) {
                final String error = e.getClass().getName() + ":" + e.getMessage();
                reportError(scheme, isFallbackHttpsDisable, error, config);
                statistics(error, config);
                throw e;
            } finally {
                if (!isSuccess) {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }

        return isSuccess ? con : null;
    }

    /**
     * 写入数据
     *
     * @param con
     * @param boundary
     * @param data
     * @return
     */
    private boolean writeData(HttpURLConnection con, String boundary, byte[] data, ConfigSystemLimit config,
                              String fileName) {
        if (con == null) {
            return false;
        }

        String end = "\r\n";
        String twoHyphens = "--";

        StringBuilder sbHeader = new StringBuilder();
        sbHeader.append(twoHyphens);
        sbHeader.append(boundary);
        sbHeader.append(end);
        sbHeader.append("Content-Disposition: form-data; name=\"userfile\"; filename=\"");
        sbHeader.append(fileName);
        sbHeader.append("\"");
        sbHeader.append(end);
        sbHeader.append("Content-Type: application/octet-stream");
        sbHeader.append(end);
        sbHeader.append(end);
        byte[] dataHeader = sbHeader.toString().getBytes();
        byte[] dataEnd = (end + twoHyphens + boundary + twoHyphens + end).getBytes();

        OutputStream os = null;
        try {
            os = con.getOutputStream();

            if (os == null) {
                statistics("getOutputStream is null", config);
                return false;
            }

            os.write(dataHeader);
            os.write(data);
            os.write(dataEnd);

            int responseCode = con.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                return readResponseData(con, config);
            }

            DuboxLog.e(TAG, "统计上传失败 HttpStatusCode:[" + responseCode + "]" + con.getResponseMessage());
            statistics("responseCode:" + responseCode, config);
        } catch (MalformedURLException e) {
            DuboxLog.e(TAG, "MalformedURLException", e);
            final String error = e.getClass().getName() + ":" + e.getMessage();
            statistics(error, config);
        } catch (IOException e) {
            DuboxLog.e(TAG, "IOException", e);
            final String error = e.getClass().getName() + ":" + e.getMessage();
            statistics(error, config);
        } catch (SecurityException e) {
            DuboxLog.e(TAG, "SecurityException", e);
            final String error = e.getClass().getName() + ":" + e.getMessage();
            statistics(error, config);
        } catch (Exception e) {
            final String error = e.getClass().getName() + ":" + e.getMessage();
            statistics(error, config);
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    DuboxLog.e(TAG, "", e);
                }
            }
            con.disconnect();
        }
        return false;
    }

    /**
     * 读取数据
     *
     * @param con
     * @param config
     * @throws IOException
     */
    private boolean readResponseData(HttpURLConnection con, ConfigSystemLimit config) throws IOException {
        final String result = readResponse(con);

        return parseResponse(result, config);
    }

    /**
     * 读取响应
     *
     * @param con
     * @return
     * @throws IOException
     */
    private String readResponse(HttpURLConnection con) throws IOException {
        InputStreamReader bs = null;
        StringBuilder sb = new StringBuilder();
        try {
            bs = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
            char[] c = new char[1024];
            while (bs.read(c) != -1) {
                sb.append(c);
            }
        } finally {
            if (bs != null) {
                bs.close();
            }
        }

        return sb.toString();
    }

    /**
     * 解析响应
     *
     * @param string
     */
    private boolean parseResponse(String string, ConfigSystemLimit config) {
        try {
            int code = -1;
            if (!TextUtils.isEmpty(string)) {
                JSONObject jsonObject = new JSONObject(string);
                if (jsonObject.has(ERROR_NO)) {
                    code = jsonObject.getInt(ERROR_NO);
                }
            }

            if (code == 0) {
                return true;
            }
            DuboxLog.e(TAG, "server return errno != 0[" + code + "]");
            statistics("error_no:" + code, config);
        } catch (JSONException e) {
            DuboxLog.e(TAG, "", e);
            final String error = e.getClass().getName() + ":" + e.getMessage();
            statistics(error, config);
        }

        return false;
    }

    /**
     * 统计HTTPS失败信息
     *
     * @param scheme                 协议
     * @param isFallbackHttpsDisable 是否为不回退接口
     * @param error                  错误信息
     * @param config                 配置项
     * @since 7.12.1
     */
    private void reportError(String scheme, boolean isFallbackHttpsDisable, String error, ConfigSystemLimit config) {
        if (!config.isHTTPSStatisticsEnabled) {
            return;
        }
        HttpsStatisticsReporter reporter = new HttpsStatisticsReporter();
        if (scheme.toLowerCase().startsWith(FallbackManager.HTTPS)) {
            reporter.reportHttpsError(isFallbackHttpsDisable, error);
        } else {
            reporter.reportHttpError(error);
        }
    }

    /**
     * 添加https支持
     */
    private void registerHttps(HttpsURLConnection connection) {
        // 采用系统默认，不要自定义信任所有证书的TrustManager，否则会有安全漏洞，fiddler等工具能够抓包
        connection.setSSLSocketFactory(((SSLSocketFactory) SSLSocketFactory.getDefault()));
    }
}
