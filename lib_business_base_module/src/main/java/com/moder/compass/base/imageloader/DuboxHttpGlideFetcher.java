package com.moder.compass.base.imageloader;

import static com.moder.compass.business.kernel.HostURLManager.getDataDomain;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import com.baidu.android.common.util.CommonParam;
import com.moder.compass.BaseApplication;
import com.moder.compass.account.Account;
import com.dubox.drive.kernel.Constants;
import com.dubox.drive.base.network.FallbackManager;
import com.dubox.drive.base.network.NetworkUtil;
import com.dubox.drive.base.network.StokenManager;
import com.dubox.drive.base.storage.config.ConfigSystemLimit;
import com.dubox.drive.kernel.util.PhoneStatusKt;
import com.dubox.drive.kernel.architecture.AppCommon;
import com.dubox.drive.kernel.architecture.debug.DuboxLog;
import com.dubox.drive.kernel.architecture.net.HttpParams;
import com.dubox.drive.kernel.architecture.net.RequestCommonParams;
import com.moder.compass.business.kernel.HostURLManagerKt;
import com.moder.compass.statistics.StatisticsLogForMutilFields;
import com.dubox.glide.Priority;
import com.dubox.glide.load.DataSource;
import com.dubox.glide.load.HttpException;
import com.dubox.glide.load.data.DataFetcher;
import com.dubox.glide.load.model.GlideUrl;
import com.dubox.glide.util.ContentLengthInputStream;
import com.dubox.glide.util.LogTime;
import com.dubox.glide.util.Synthetic;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * A DataFetcher that retrieves an {@link InputStream} for a Url.
 */
public class DuboxHttpGlideFetcher implements DataFetcher<InputStream> {
    private static final String TAG = "ImageNetWorkFetcher";
    private static final int MAXIMUM_REDIRECTS = 5;
    private static final int CONNECT_TIMEOUT_MILLIS = 5 * 1000;
    private static final int READ_TIMEOUT_MILLIS = 20 * 1000;

    /**
     * 返回header不包含这个字符串的就是类似moder mobile这样的没准入的网络
     *
     * @author 孙奇 V 1.0.0 Create at 2013-1-9 下午04:54:59
     */
    private static final String PCS_SERVER_TAG = "x-bs-client-ip";
    private static final String BS_SERVER_TAG = "x-bs-request-id";

    @VisibleForTesting
    static final HttpUrlConnectionFactory DEFAULT_CONNECTION_FACTORY =
            new DefaultHttpUrlConnectionFactory();
    /**
     * Returned when a connection error prevented us from receiving an http error.
     */
    private static final int INVALID_STATUS_CODE = -1;

    private final GlideUrl glideUrl;
    private final int timeout;
    private final HttpUrlConnectionFactory connectionFactory;

    private HttpURLConnection urlConnection;
    private InputStream stream;
    private volatile boolean isCancelled;
    private long mBeginLoadTime;
    private String mThumbnailScheme;
    private String mThumbnailDomain;
    private long mImageLoadReportThreshold = 3000L;

    public DuboxHttpGlideFetcher(GlideUrl glideUrl, int timeout) {
        this(glideUrl, timeout, DEFAULT_CONNECTION_FACTORY);
    }

    @VisibleForTesting
    DuboxHttpGlideFetcher(GlideUrl glideUrl, int timeout, HttpUrlConnectionFactory connectionFactory) {
        this.glideUrl = glideUrl;
        this.timeout = timeout;
        this.connectionFactory = connectionFactory;
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        mThumbnailScheme = configSystemLimit.pcsdataEnableHttps ?
                HostURLManagerKt.PRO_STR_HTTPS : HostURLManagerKt.PRO_STR_HTTP;
        mThumbnailDomain = getDataDomain();
    }

    @Override
    public void loadData(@NonNull Priority priority,
                         @NonNull DataCallback<? super InputStream> callback) {
        long startTime = LogTime.getLogTime();
        try {
            InputStream result = loadDataWithRedirects(glideUrl.toURL(), 0, null, glideUrl.getHeaders());
            callback.onDataReady(result);
        } catch (IOException e) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Failed to load data for url", e);
            }
            callback.onLoadFailed(e);
        } finally {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Finished http url fetcher fetch in " + LogTime.getElapsedMillis(startTime));
            }
        }
    }

    // 加载String类型
    private InputStream loadDataWithRedirects(URL url, int redirects, URL lastUrl,
                                              Map<String, String> headers) throws IOException {
        if (redirects >= MAXIMUM_REDIRECTS) {
            throw new HttpException("Too many (> " + MAXIMUM_REDIRECTS + ") redirects!");
        } else {
            // Comparing the URLs using .equals performs additional network I/O and is generally broken.
            // See http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html.
            try {
                if (lastUrl != null && url.toURI().equals(lastUrl.toURI())) {
                    throw new HttpException("In re-direct loop");
                }
            } catch (URISyntaxException e) {
                // Do nothing, this is best effort.
            }
        }

        if (isCancelled) {
            return null;
        }
        mBeginLoadTime = System.currentTimeMillis();
        ConfigSystemLimit configSystemLimit = ConfigSystemLimit.getInstance();
        final FallbackManager fallbackManager = new FallbackManager(configSystemLimit);
        String auth = url.getAuthority();
        if (!TextUtils.isEmpty(auth) && auth.equals(getDataDomain())) {
            url = new URL(getAuthParams(url.toString()));
            try {
                urlConnection = connectionFactory.build(url);
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                }
                urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
                urlConnection.setReadTimeout(READ_TIMEOUT_MILLIS);
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);

                // Stop the urlConnection instance of HttpUrlConnection from following redirects so that
                // redirects will be handled by recursive calls to this method, loadDataWithRedirects.
                urlConnection.setInstanceFollowRedirects(false);

                urlConnection.setRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
                // 压缩包内部分图片预览失败的问题，部分请求发生EOFException
                urlConnection.setRequestProperty("Accept-Encoding", "");

                    String cookie = Constants.DUBOX_BDUSS_FIELD_NAME + "=" + Account.INSTANCE.getNduss();
                    StokenManager stokenManager = new StokenManager(Account.INSTANCE.getNduss());
                    cookie = stokenManager.addPanPsc(cookie);
                    cookie = stokenManager.addSToken(cookie);
                    cookie = stokenManager.addPanNdutFmt(cookie);
                    urlConnection.setRequestProperty(Constants.DUBOX_COOKIE_TAG, cookie);

                final boolean isSuccess = connect(fallbackManager, url.toString(), urlConnection);

                if (isSuccess && (urlConnection.getHeaderField(PCS_SERVER_TAG) != null
                        || urlConnection.getHeaderField(BS_SERVER_TAG) != null)) {
                    stream = urlConnection.getInputStream();
                }
            } catch (IOException e) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                throw e;
            }
        } else {
            try {
                urlConnection = connectionFactory.build(url);
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    urlConnection.addRequestProperty(headerEntry.getKey(), headerEntry.getValue());
                }
                urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
                urlConnection.setReadTimeout(READ_TIMEOUT_MILLIS);
                urlConnection.setChunkedStreamingMode(1024);
                urlConnection.setRequestProperty("User-Agent", RequestCommonParams.getUserAgent());
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);

                // Stop the urlConnection instance of HttpUrlConnection from following redirects so that
                // redirects will be handled by recursive calls to this method, loadDataWithRedirects.
                urlConnection.setInstanceFollowRedirects(false);
                // Connect explicitly to avoid errors in decoders if connection fails.
                urlConnection.connect();
                stream = urlConnection.getInputStream();
            } catch (SecurityException e) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                DuboxLog.e(TAG, "SecurityException", e);
                throw new IOException("SecurityException");
            } catch (IOException e) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                throw e;
            }
        }

        reportImageLoadTime(url.toString());

        if (isCancelled) {
            return null;
        }
        final int statusCode = urlConnection.getResponseCode();
        if (isHttpOk(statusCode)) {
            return getStreamForSuccessfulRequest(urlConnection);
        } else if (isHttpRedirect(statusCode)) {
            String redirectUrlString = urlConnection.getHeaderField("Location");
            if (TextUtils.isEmpty(redirectUrlString)) {
                throw new HttpException("Received empty or null redirect url");
            }
            URL redirectUrl = new URL(url, redirectUrlString);
            // Closing the stream specifically is required to avoid leaking ResponseBodys in addition
            // to disconnecting the url connection below. See #2352.
            cleanup();
            return loadDataWithRedirects(redirectUrl, redirects + 1, url, headers);
        } else if (statusCode == INVALID_STATUS_CODE) {
            throw new HttpException(statusCode);
        } else {
            throw new HttpException(urlConnection.getResponseMessage(), statusCode);
        }
    }

    // Referencing constants is less clear than a simple static method.
    private static boolean isHttpOk(int statusCode) {
        return statusCode / 100 == 2;
    }

    // Referencing constants is less clear than a simple static method.
    private static boolean isHttpRedirect(int statusCode) {
        return statusCode / 100 == 3;
    }

    private InputStream getStreamForSuccessfulRequest(HttpURLConnection urlConnection)
            throws IOException {
        if (TextUtils.isEmpty(urlConnection.getContentEncoding())) {
            int contentLength = urlConnection.getContentLength();
            stream = ContentLengthInputStream.obtain(urlConnection.getInputStream(), contentLength);
        } else {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Got non empty content encoding: " + urlConnection.getContentEncoding());
            }
            stream = urlConnection.getInputStream();
        }
        return stream;
    }

    @Override
    public void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
        if (urlConnection != null) {
            urlConnection.disconnect();
        }
        urlConnection = null;
    }

    @Override
    public void cancel() {
        // directly because cancel is often called on the main thread.
        isCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }

    interface HttpUrlConnectionFactory {
        HttpURLConnection build(URL url) throws IOException;
    }

    private static class DefaultHttpUrlConnectionFactory implements HttpUrlConnectionFactory {

        @Synthetic
        DefaultHttpUrlConnectionFactory() { }

        @Override
        public HttpURLConnection build(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }

    /**
     * 设置分散认证参数
     *
     * @param url
     * @return
     */
    private String getAuthParams(String url) {
        String bduss = Account.INSTANCE.getNduss();
        String uid = Account.INSTANCE.getUid();

        HttpParams params = new HttpParams();

        // 7.18.2 修复传个devuid问题
        if (!url.contains("devuid=")) {
            params.add("devuid", AppCommon.DEVUID);
        }

        params.add("clienttype", RequestCommonParams.getClientType());
        params.add("channel", RequestCommonParams.getChannel());
        params.add("version", AppCommon.VERSION_DEFINED);
        params.add("logid", RequestCommonParams.getLogId());
        params.add("imgtype", GlideHelper.getInstance().getImageType(url));
        if (AppCommon.FIRST_LAUNCH_TIME > 0) {
            params.add("firstlaunchtime", String.valueOf(AppCommon.FIRST_LAUNCH_TIME));
        }

        // 7.10 加入新的校验参数 libin09 2015-7-19
        if (!TextUtils.isEmpty(uid)) {
            NetworkUtil.addRand(url, params, bduss, uid);
        }

        // 7.18.2
        params.add(Constants.APN, NetworkUtil.getCurrentNetworkAPN());

        Context context = BaseApplication.getInstance();
        params.add(PhoneStatusKt.ISO_KEY, PhoneStatusKt.getSimCarrierInfo(context));
        params.add(AppCommon.COMMON_PARAM_APP_ID, AppCommon.getSecondBoxPcsAppId());
        params.add("app_name", "moder");
        if (context != null) {
            params.add("cuid", CommonParam.getCUID(context));
            // 加入网络情况
            NetworkUtil.addNetworkType(context, params);
        }
        // 转换成url参数字符串
        if (!url.contains("?")) {
            url += "?";
        } else if (!url.endsWith("?")) {
            url += "&";
        }
        url += params.toString();

        return url;
    }

    /**
     * 连接服务器
     *
     * @param fallbackManager 回退管理器
     * @param imageUri 图片地址
     * @param conn 连接
     * @return 是否成功
     * @throws IOException
     * @since 7.13.1
     */
    private boolean connect(FallbackManager fallbackManager, String imageUri, HttpURLConnection conn)
            throws IOException {
        final ArrayList<String> urls = new ArrayList<String>(2);
        urls.add(imageUri);

        if (ConfigSystemLimit.getInstance().isImageDownloadFallbackHttpsEnabled && fallbackManager.isHTTPS(imageUri)) {
            urls.add(fallbackManager.https2http(imageUri));
        }

        for (String url : urls) {
            final boolean isHTTPS = fallbackManager.isHTTPS(url);

            // 修复webview 中播放多媒体信息时cookie 错乱的问题.libin09 2017-2-8 7.17.0
            CookieManager.setDefault(new CookieManager());

            try {
                conn.connect();

                return true;
            } catch (IOException e) {
                DuboxLog.w(TAG, "IOException", e);

                if (!isHTTPS) {
                    throw e;
                }

                conn.disconnect();
            } catch (Exception e) {
                conn.disconnect();

                DuboxLog.w(TAG, "Exception", e);

                if (!isHTTPS) {
                    break;
                }
            }
        }

        return false;
    }

    private void reportImageLoadTime(String loadUrl) {
        long endTime = System.currentTimeMillis();
        String url = mThumbnailScheme + mThumbnailDomain + "/rest/2.0/pcs/thumbnail";
        long time = endTime - mBeginLoadTime;
        if (loadUrl.startsWith(url) && time > mImageLoadReportThreshold) {
            // 大于阈值则统计
            Uri uri = Uri.parse(loadUrl);
            String filePath = uri.getQueryParameter("path");
            // 上报单张图片下载时间
            StatisticsLogForMutilFields.getInstance().updateCount(
                    StatisticsLogForMutilFields.StatisticsKeys.CLOUD_IMAGE_THUMBNAIL_LOAD_TIME, true,
                    filePath, String.valueOf(time));
        }
    }
}
