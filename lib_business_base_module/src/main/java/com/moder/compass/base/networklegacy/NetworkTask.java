/*
 * NetworkTask.java
 * classes : NetworkTask
 * @author 李彬
 * V 1.0.0
 * Create at 2012-8-3 上午10:13:32
 */
package com.moder.compass.base.networklegacy;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONException;

import com.dubox.drive.base.network.FallbackManager;
import com.dubox.drive.base.network.HttpRequestHelper;
import com.dubox.drive.base.network.HttpsStatisticsReporter;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.kernel.android.util.RealTimeUtil;
import com.dubox.drive.kernel.architecture.config.PersonalConfig;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.HttpRequest;
import com.dubox.drive.kernel.architecture.net.HttpResponse;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.dubox.drive.kernel.architecture.net.exception.RemoteException;
import com.dubox.drive.kernel.architecture.net.parser.IApiResultParseable;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.moder.compass.util.StoreAuditAdaptationUtil;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/**
 * @author <a href="mailto:">李彬</a> <br/>
 * 联网通讯的任务，同时提供同步和异步的方案 <br/>
 * create at 2012-8-3 上午10:13:32
 */
public class NetworkTask<T> {
    private static final String TAG = "NetworkTask";
    /**
     * 重试3回
     */
    private static final int RETRY_LIMIT = 3;
    private final int mTimeOut;
    /**
     * 回退控制器 libin09 2016-4-5
     *
     * @since 7.12.1
     */
    private final FallbackManager mFallbackManager;
    private final HttpsStatisticsReporter mReporter;

    public NetworkTask(int timeout) {
        mTimeOut = timeout;
        mFallbackManager = new FallbackManager(ConfigSystemLimit.getInstance());
        mReporter = new HttpsStatisticsReporter();
    }

    public NetworkTask() {
        // 10s * 2 = 20s
        this(Constants.READ_TIMEOUT * 2);
    }

    /**
     * 发送数据
     *
     * @param connection
     * @param params
     * @throws IOException
     */
    private void sendData(HttpURLConnection connection, String params) throws IOException {
        if (!TextUtils.isEmpty(params)) {
            OutputStream os = connection.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            bos.write(params.getBytes());
            bos.flush();
            bos.close();
            os.close();
            DuboxLog.d(TAG, "http params " + params);
        }
    }

    /**
     * 使用同步方法执行http请求
     *
     * @param requestAndParser Object... 参数，其中,requestAndParser[0]是HttpUriRequest, http请求<br />
     *                         requestAndParser[1]是IApiResultParseable<T>, response的解析器
     * @return
     */
    @SuppressWarnings("unchecked")
    public T send(Object... requestAndParser)
        throws UnsupportedOperationException, IOException, JSONException, RemoteException {
        // 要执行的http请求
        final HttpRequest[] requests = (HttpRequest[]) requestAndParser[0];
        // 如果bduss为空就取消前一次操作，停止当前操作
        if (requests == null) {
            DuboxLog.v(TAG, "request == null");
            return null;
        }
        // 要解析response的解析器
        IApiResultParseable<T> parser = null;
        // 确认调用方法时是否传入IApiResultParseable参数，如果没有传入，无需解析
        if (requestAndParser.length > 1 && requestAndParser[1] instanceof IApiResultParseable<?>) {
            parser = (IApiResultParseable<T>) requestAndParser[1];
        }
        T result = null;
        HttpURLConnection connection = null;
        // HTTPS失败计数器
        int httpsFailedCounter = 0;
        boolean isSuccess = false;
        String params = null;
        // 标识是否使用 HttpDns 服务
        boolean useHttpDns = false;
        // 当前请求 Url 地址的域名
        String host = null;
        // 是否为白名单内接口，这些接口不回退
        boolean isFallbackHttpsDisable;
        // requests的长度可能1,3,4.
        // 只有4个长度的列表会支持回退前3个https，第4个http,
        // 3个的都是http或都是https，
        // 1个的是文件下载
        final int retryCount = Math.min(RETRY_LIMIT, requests.length);
        HttpRequestHelper requestHelper = new HttpRequestHelper();
        for (int i = 0; i < retryCount; i++) {
            int index = i;
            // 最后一个请求是否替换为HTTP
            if ((i == retryCount - 1) && (httpsFailedCounter == retryCount - 1)) {
                index = requests.length - 1;
            }
            if (i > 0) {
                // 若requests长度大于1，且当前请求非第0次，则证明是重试类型的请求，需要增加重试标记
                // 服务端使用此参数做请求管理
                HttpRequest tmpRequest = requests[index];
                HttpParams tmpParams = tmpRequest.getParams();
                if (tmpParams == null) {
                    tmpParams = new HttpParams();
                }
                tmpParams.add("wp_retry_num", String.valueOf(i));
            }
            final HttpRequest request = requestHelper.appendParams(requests[index]);
            String url = request.getUrl();
            host = new URL(request.getUrl()).getHost();
            DuboxLog.d(TAG, "send request > " + request.toString());
            // 是否为白名单内接口，这些接口不回退
            isFallbackHttpsDisable = mFallbackManager.filterHTTPS(url);
            final boolean isHTTPS = mFallbackManager.isHTTPS(url);
            connection = buildURLConnection(request, host, useHttpDns);
            // 尝试记录HTTPS请求回退次数
            if (requests.length > 1) {
                mFallbackManager.tryToIncreaseCounter(!isHTTPS);
            }
            try {
                // 修复webview 中播放多媒体信息时cookie 错乱的问题.libin09 2017-2-8 7.17.0
                CookieManager.setDefault(new CookieManager());
                connection.connect();
                // 尝试清除HTTPS请求回退次数
                mFallbackManager.tryToResetCounter(isHTTPS, isFallbackHttpsDisable);
                if (request.isPost()) {
                    params = request.getParamsString();
                }
                // 此部分代码，转移至此，目的为根据 Http 状态码判断当前请求是否已成功，即 isSucess
                sendData(connection, params);
                // 获取http响应代码
                final int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // 错误的http请求
                    DuboxLog.e(TAG, "Http 返回的状态码为：" + statusCode);
                } else {
                    if (useHttpDns) {
                        // 统计，使用 HttpDns 服务的请求，请求成功
                        String statisticKey =
                            StatisticsLogForMutilFields.StatisticsKeys.HTTP_DNS_REQUEST_SUCCESS;
                        StatisticsLogForMutilFields.getInstance().updateCount(statisticKey);
                    }
                }
                isSuccess = true;
                // 成功，不再重试
                break;
            } catch (IOException e) {
                if (isHTTPS) {
                    httpsFailedCounter++;
                }
                DuboxLog.e(TAG, "IOException", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
            } catch (SecurityException e) {
                DuboxLog.e(TAG, "SecurityException", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                break;
            } catch (NullPointerException e) {
                DuboxLog.e(TAG, "NullPointerException system inner error", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                break;
            } catch (Exception e) {
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                throw e;
            } finally {
                if (!isSuccess) {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }
        if (!isSuccess) {
            throw new IOException("retry failed.");
        }
        try {
            // 服务器结果，返回业务层
            getPanPSC(connection);
            RealTimeUtil.initTimeDelta(connection);
            HttpResponse response = new HttpResponse(connection);
            if (parser != null) {
                // 调用解析器对response解析
                result = parser.parse(response);
            }
        } catch (NullPointerException e) {
            throw new IOException("system inner error");
        } finally {
            // Releases this connection so that its resources may be either reused or closed
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * 使用同步方法执行http请求
     *
     * @param filename         上传文件的名字
     * @param data             上传文件的字节
     * @param requestAndParser Object... 参数，其中,requestAndParser[0]是HttpUriRequest, http请求<br />
     *                         requestAndParser[1]是IApiResultParseable<T>, response的解析器
     * @return T
     */
    @SuppressWarnings("unchecked")
    public T send(String filename, byte[] data, Object... requestAndParser)
        throws UnsupportedOperationException, KeyManagementException, UnrecoverableKeyException,
        NoSuchAlgorithmException, KeyStoreException, IOException, JSONException, RemoteException {
        // 要执行的http请求
        final HttpRequest[] requests = (HttpRequest[]) requestAndParser[0];
        // 如果bduss为空就取消前一次操作，停止当前操作
        if (requests == null) {
            DuboxLog.v(TAG, "request == null");
            return null;
        }
        // 要解析response的解析器
        IApiResultParseable<T> parser = null;
        // 确认调用方法时是否传入IApiResultParseable参数，如果没有传入，无需解析
        if (requestAndParser.length > 1 && requestAndParser[1] instanceof IApiResultParseable<?>) {
            parser = (IApiResultParseable<T>) requestAndParser[1];
        }
        T result = null;
        HttpURLConnection connection = null;
        // HTTPS失败计数器
        int httpsFailedCounter = 0;
        boolean isSuccess = false;
        String params = null;
        // 标识是否使用 HttpDns 服务
        boolean useHttpDns = false;
        // 当前请求 Url 地址的域名
        String host = null;
        // 是否为白名单内接口，这些接口不回退
        boolean isFallbackHttpsDisable;
        // requests的长度可能1,3,4.
        // 只有4个长度的列表会支持回退前3个https，第4个http,
        // 3个的都是http或都是https，
        // 1个的是文件下载
        final int retryCount = Math.min(RETRY_LIMIT, requests.length);
        HttpRequestHelper requestHelper = new HttpRequestHelper();
        for (int i = 0; i < retryCount; i++) {
            int index = i;
            // 最后一个请求是否替换为HTTP
            if ((i == retryCount - 1) && (httpsFailedCounter == retryCount - 1)) {
                index = requests.length - 1;
            }
            if (i > 0) {
                // 若requests长度大于1，且当前请求非第0次，则证明是重试类型的请求，需要增加重试标记
                // 服务端使用此参数做请求管理
                HttpRequest tmpRequest = requests[index];
                HttpParams tmpParams = tmpRequest.getParams();
                if (tmpParams == null) {
                    tmpParams = new HttpParams();
                }
                tmpParams.add("wp_retry_num", String.valueOf(i));
            }
            final HttpRequest request = requestHelper.appendParams(requests[index]);
            String url = request.getUrl();
            host = new URL(request.getUrl()).getHost();
            DuboxLog.d(TAG, "send request > " + request.toString());
            // 是否为白名单内接口，这些接口不回退
            isFallbackHttpsDisable = mFallbackManager.filterHTTPS(url);
            final boolean isHTTPS = mFallbackManager.isHTTPS(url);
            if (request.isPost()) {
                params = request.getParamsString();
            }
            String boundary = String.valueOf(System.currentTimeMillis());
            byte[] bodyHeader = initBodyHeader(boundary, filename);
            byte[] bodyEnding = initBodyEnding(boundary, params);
            long bodyLength = initBodyLength(bodyHeader, bodyEnding, data.length);
            connection = buildFormDataURLConnection(request, host, useHttpDns, boundary, bodyLength);
            // 尝试记录HTTPS请求回退次数
            if (requests.length > 1) {
                mFallbackManager.tryToIncreaseCounter(!isHTTPS);
            }
            try {
                // 修复webview 中播放多媒体信息时cookie 错乱的问题.libin09 2017-2-8 7.17.0
                CookieManager.setDefault(new CookieManager());
                connection.connect();
                // 尝试清除HTTPS请求回退次数
                mFallbackManager.tryToResetCounter(isHTTPS, isFallbackHttpsDisable);
                // 此部分代码，转移至此，目的为根据 Http 状态码判断当前请求是否已成功，即 isSucess
                sendFormData(connection, bodyHeader, data, bodyEnding);
                // 获取http响应代码
                final int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    // 错误的http请求
                    DuboxLog.e(TAG, "Http 返回的状态码为：" + statusCode);
                } else {
                    if (useHttpDns) {
                        // 统计，使用 HttpDns 服务的请求，请求成功
                        String statisticKey =
                            StatisticsLogForMutilFields.StatisticsKeys.HTTP_DNS_REQUEST_SUCCESS;
                        StatisticsLogForMutilFields.getInstance().updateCount(statisticKey);
                    }
                }
                isSuccess = true;
                // 成功，不再重试
                break;
            } catch (IOException e) {
                if (isHTTPS) {
                    httpsFailedCounter++;
                }
                DuboxLog.e(TAG, "IOException", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
            } catch (SecurityException e) {
                DuboxLog.e(TAG, "SecurityException", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                break;
            } catch (NullPointerException e) {
                DuboxLog.e(TAG, "NullPointerException system inner error", e);
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                break;
            } catch (Exception e) {
                reportError(isHTTPS, isFallbackHttpsDisable, e.getClass().getName() + ":" + e.getMessage());
                throw e;
            } finally {
                if (!isSuccess) {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }
        if (!isSuccess) {
            throw new IOException("retry failed.");
        }
        try {
            // 服务器结果，返回业务层
            getPanPSC(connection);
            RealTimeUtil.initTimeDelta(connection);
            HttpResponse response = new HttpResponse(connection);
            if (parser != null) {
                // 调用解析器对response解析
                result = parser.parse(response);
            }
        } catch (NullPointerException e) {
            throw new IOException("system inner error");
        } finally {
            // Releases this connection so that its resources may be either reused or closed
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private HttpURLConnection buildURLConnection(HttpRequest request, String host, boolean useHttpDns)
        throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection connection;
        // 判断当前请求是否为 Https 协议
        boolean isHttps = request.getUrl().contains("https://");
        // 命中 Sni 证书校验需要满足两个条件：1、当前协议为 Https 2、使用 HttpDns 服务
        if (isHttps && useHttpDns) {
            final HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setInstanceFollowRedirects(false);
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    String host = httpsConn.getRequestProperty("Host");
                    if (null == host) {
                        host = httpsConn.getURL().getHost();
                    }
                    DuboxLog.d(TAG, "校验证书 session : " + session + " host : " + host);
                    return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                }
            });
            connection = httpsConn;
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        connection.setRequestProperty("Host", host);
        connection.setRequestMethod(request.getMethod()); // 提交模式
        connection.setConnectTimeout(mTimeOut); // 连接超时 单位毫秒
        connection.setReadTimeout(mTimeOut); // 读取超时 单位毫秒
        connection.addRequestProperty("Cookie", request.getCookie());
        connection.addRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // 增加是否在审核中标记
        if (StoreAuditAdaptationUtil.Companion.getIns().checkCurrentChannelNoIsUnderReview()) {
            DuboxLog.d(getClass().getName(), "[商店审核适配拦截器]当前渠道需要增加服务器请求标志...audit=1(HttpConnect)");
            connection.setRequestProperty("audit", "1");
        }
        String referer = RequestCommonParams.getReferer(request.getUrl());
        if (referer != null) {
            connection.setRequestProperty("Referer", referer);
        }
        // 只有post请求输入参数
        if (request.isPost() && !TextUtils.isEmpty(request.getParamsString())) {
            connection.setDoOutput(true);
        }
        return connection;
    }

    private HttpURLConnection buildFormDataURLConnection(HttpRequest request, String host,
                                                         boolean useHttpDns, String boundary, long bodyLen)
        throws IOException {
        URL url = new URL(request.getUrl());
        HttpURLConnection connection;
        // 判断当前请求是否为 Https 协议
        boolean isHttps = request.getUrl().contains("https://");
        // 命中 Sni 证书校验需要满足两个条件：1、当前协议为 Https 2、使用 HttpDns 服务
        if (isHttps && useHttpDns) {
            final HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            httpsConn.setInstanceFollowRedirects(false);
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    String host = httpsConn.getRequestProperty("Host");
                    if (null == host) {
                        host = httpsConn.getURL().getHost();
                    }
                    DuboxLog.d(TAG, "校验证书 session : " + session + " host : " + host);
                    return HttpsURLConnection.getDefaultHostnameVerifier().verify(host, session);
                }
            });
            connection = httpsConn;
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        connection.setRequestProperty("Host", host);
        connection.setRequestMethod(request.getMethod()); // 提交模式
        connection.setConnectTimeout(20000); // 连接超时 单位毫秒
        connection.setReadTimeout(20000); // 读取超时 单位毫秒
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        connection.addRequestProperty("Cookie", request.getCookie());
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.addRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
        connection.setRequestProperty("Content-Transfer-Encoding", "binary");
        String referer = RequestCommonParams.getReferer(request.getUrl());
        if (StoreAuditAdaptationUtil.Companion.getIns().checkCurrentChannelNoIsUnderReview()) {
            DuboxLog.d(getClass().getName(), "[商店审核适配拦截器]当前渠道需要增加服务器请求标志...audit=1(HttpConnect)");
            connection.setRequestProperty("audit", "1");
        }
        if (referer != null) {
            connection.setRequestProperty("Referer", referer);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && bodyLen > 0) {
            connection.setFixedLengthStreamingMode(bodyLen);
        }
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * 发送数据
     *
     * @param connection
     * @throws IOException
     */
    private void sendFormData(HttpURLConnection connection, byte[]... datas) throws IOException {
        OutputStream os = connection.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        // 写头数据
        bos.write(datas[0]);
        // 写数据
        bos.write(datas[1]);
        bos.flush();
        // 写尾部数据
        bos.write(datas[2]);
        bos.flush();
        bos.close();
        os.close();
    }

    private String initParamData(String boundary, String params) {
        if (TextUtils.isEmpty(params)) {
            return null;
        }
        // 为了方便解析参数对，增加一个无意义的协议头
        Uri uri = Uri.parse("http://queryparam?" + params);
        Set<String> parameterNames = uri.getQueryParameterNames();
        StringBuilder buffer = new StringBuilder();
        for (String name : parameterNames) {
            buffer.append("\r\n----").append(boundary).append("\r\n");
            buffer.append("Content-Disposition: form-data; name=")
                .append("\"")
                .append(name)
                .append("\"")
                .append("\r\n\r\n")
                .append(uri.getQueryParameter(name));
        }
        return buffer.toString();
    }

    private byte[] initBodyHeader(String boundary, String filename) {
        StringBuffer boundaryStart = new StringBuffer("----").append(boundary).append("\r\n");
        boundaryStart.append("Content-Disposition: form-data; name=\"Filename\"").append("\r\n\r\n");
        boundaryStart.append(filename).append("\r\n");
        boundaryStart.append("----").append(boundary).append("\r\n");
        boundaryStart.append("Content-Disposition: form-data; name=\"FileNode\"; filename=\"").append(filename)
            .append("\"\r\n");
        boundaryStart.append("Content-Type: application/octet-stream").append("\r\n\r\n");
        return boundaryStart.toString().getBytes();
    }

    private byte[] initBodyEnding(String boundary, String params) {
        StringBuffer boundaryUpload = new StringBuffer("\r\n----").append(boundary).append("\r\n");
        boundaryUpload.append("Content-Disposition: form-data; name=\"Upload\"").append("\r\n\r\n");
        boundaryUpload.append("Submit Query");
        String paramData = initParamData(boundary, params);
        if (!TextUtils.isEmpty(paramData)) {
            boundaryUpload.append(paramData);
        }
        boundaryUpload.append("\r\n");
        boundaryUpload.append("----").append(boundary).append("--");
        return boundaryUpload.toString().getBytes();
    }

    private long initBodyLength(byte[] bodyHeader, byte[] bodyEnding, int blockLength) {
        if (bodyHeader == null || bodyEnding == null) {
            return -1;
        }
        return bodyHeader.length + bodyEnding.length + blockLength;
    }

    private void getPanPSC(HttpURLConnection response) {
        Map<String, List<String>> headers = response.getHeaderFields();
        if (headers == null || !headers.containsKey(Constants.HTTP_COOKIE_NAME)) {
            return;
        }
        for (String value : headers.get(Constants.HTTP_COOKIE_NAME)) {
            Matcher matcher2 = Constants.PANPSC2_PATTERN.matcher(value);
            if (matcher2.find() && matcher2.groupCount() > 0) {
                String panPsc = matcher2.group(1);
                DuboxLog.v(TAG, "获得当前PANPSC setCookie " + panPsc); // 整个匹配到的内容
                if (!TextUtils.isEmpty(panPsc)) {
                    PersonalConfig.getInstance().putString(Constants.PANPSC_KEY, panPsc);
                    PersonalConfig.getInstance().asyncCommit();
                }
            }
        }
    }

    /**
     * 添加https支持
     *
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     */
    private void registerHttps(HttpsURLConnection connection) {
        // 采用系统默认，不要自定义信任所有证书的TrustManager，否则会有安全漏洞，fiddler等工具能够抓包
        connection.setSSLSocketFactory(((SSLSocketFactory) SSLSocketFactory.getDefault()));
    }

    private void reportError(boolean isHTTPS, boolean isFallbackHttpsDisable, String error) {
        if (isHTTPS) {
            mReporter.reportHttpsError(isFallbackHttpsDisable, error);
        } else {
            mReporter.reportHttpError(error);
        }
    }
}