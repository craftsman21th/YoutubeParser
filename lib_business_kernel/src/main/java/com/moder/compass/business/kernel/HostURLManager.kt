package com.moder.compass.business.kernel

import android.net.Uri
import android.text.TextUtils
import com.dubox.drive.kernel.Constants
import com.dubox.drive.kernel.architecture.AppCommon
import com.dubox.drive.kernel.architecture.debug.DuboxLog
import com.dubox.drive.kernel.architecture.net.RequestCommonParams
import com.dubox.drive.kernel.i18n.getDuboxLanguageCountry
import com.dubox.drive.kernel.util.INT_300
import com.moder.compass.lib_business_kernel.BuildConfig
import java.util.concurrent.CopyOnWriteArraySet
import com.moder.compass.util.isGoogleChannel

/**
 * 预览时添加参数，以便server跟踪在线消费
 */
const val PREVIEW_PARAM: String = "user=1"

/**
 * 预览时添加参数，文件预览50M以下不限速
 */
const val FILE_PREVIEW_PARAM: String = "use=1"
const val PRO_STR_HTTP: String = "http://"
const val PRO_STR_HTTPS: String = "https://"
const val DEFAULT_DOMAIN: String = BuildConfig.DEFAULT_DOMAIN
const val DEFAULT_SHARE_DOMAIN: String = BuildConfig.DEFAULT_SHARE_DOMAIN
private const val DEFAULT_DATA_DOMAIN_FORMAT: String = "data.%s"
private const val DEFAULT_DATA_SIMPLE_DOMAIN_FORMAT: String = "d.%s"
private const val DEFAULT_UPLOAD_PCS_DOMAIN_FORMAT: String = "c-jp.%s"
private const val DEFAULT_PASSPORT_FORMAT: String = "${PRO_STR_HTTPS}passport.%s"
private const val DEFAULT_ANTISPAM_DOMAIN_FORMAT: String = "${PRO_STR_HTTPS}sofire.%s"

/**
 * 备用域名
 */
private val OTHER_DOMAINS = setOf<String>(
    BuildConfig.DEFAULT_DOMAIN,
    BuildConfig.DEFAULT_SHARE_DOMAIN
)

// 仅有heybox.ai,heyboxshare.com,easypavo.com是线上包的域名,其他都是线下包的

/**
 * 线下包 备用域名
 */
private val webPageAlternativeDomains = setOf(
    "pavoshare.com",
    "pavofun.com",
    "pavoeasy.com",
    "pavosafe.com",
    "pavobox.fun",
    "pavobox.app",
    "freepavo.com",
    "funpavo.com",
    "pavoearn.com",
    "pavoboxapp.com",
)

/**
 * 不支持HTTPS自动回退到HTTP的本地接口
 */
val fallbackHttpsDisableList = arrayOf(
    "/cfginvoke", "/getconfig", "/getlatestversion", "/phonecalls/", "/membership/",
    "/security/command", "/rest/2.0/dcs/", "/sbox", "/aipic/cardcatalog/"
)

/**
 * 域名、url管理
 * Created by yeliangliang on 2020/10/27
 */
object HostURLManager {

    private var domain: String = DEFAULT_DOMAIN
    private var dataDomain: String = String.format(DEFAULT_DATA_DOMAIN_FORMAT, domain)
    private var dataSimpleDomain: String = String.format(DEFAULT_DATA_SIMPLE_DOMAIN_FORMAT, domain)
    private var antiSpamDomain: String = String.format(DEFAULT_ANTISPAM_DOMAIN_FORMAT, domain)
    private var uploadPCSDomain: String = String.format(DEFAULT_UPLOAD_PCS_DOMAIN_FORMAT, domain)
    private var passportDomain: String = String.format(DEFAULT_PASSPORT_FORMAT, domain)

    private val allDomainList: CopyOnWriteArraySet<String> = CopyOnWriteArraySet<String>().apply {
        add(DEFAULT_DOMAIN)
        addAll(OTHER_DOMAINS)
        if (!isGoogleChannel()) {
            addAll(webPageAlternativeDomains)
        }
    }

    /**
     * https+控制流域名
     */
    val domainWithHttps: String
        get() {
            return PRO_STR_HTTPS + getDomain()
        }

    /**
     * 更新所有的domain
     */
    fun updateAllDomain(domains: List<String>?) {
        if (!domains.isNullOrEmpty()) {
            allDomainList.addAll(domains)
        }
    }

    /**
     * 获取所有domain
     */
    @JvmStatic
    fun getAllDomain(): CopyOnWriteArraySet<String> {
        if (allDomainList.isEmpty()) {
            allDomainList.add(DEFAULT_DOMAIN)
            allDomainList.addAll(OTHER_DOMAINS)
            if (!isGoogleChannel()) {
                allDomainList.addAll(webPageAlternativeDomains)
            }
        }
        return allDomainList
    }

    @JvmStatic
    fun getAllDomainPatternString():String{
        return getAllDomain().joinToString("/|");
    }

    /**
     * 控制流域名
     */
    @JvmStatic
    fun getDomain(): String {
        return "www.$domain"
    }

    /**
     * 控制流域名
     */
    @JvmStatic
    fun getDomainNoWWW(): String {
        return domain
    }
    @JvmStatic
    fun getDefaultRemoteResourcePrefix():String{
        return "http://data.${getDomainNoWWW()}/issue/pavobox"
    }

    /**
     * 数据流域名
     */
    @JvmStatic
    fun getDataDomain(): String {
        return dataDomain
    }

    /**
     * 数据流域名
     */
    @JvmStatic
    fun getDataSimpleDomain(): String {
        return dataSimpleDomain
    }

    /**
     * 反作弊域名
     * */
    @JvmStatic
    fun getAntiSpamDomain(): String {
        return antiSpamDomain
    }

    /**
     * PCS上传兜底域名
     */
    @JvmStatic
    fun getUploadPCSDomain(): String {
        return uploadPCSDomain
    }

    /**
     * passport域名
     */
    @JvmStatic
    fun getPassportDomain(): String {
        return passportDomain
    }

    private val HOST_SERVER_FE: String
        get() {
            return PRO_STR_HTTPS + getDomain()
        }

    private var hostFE: String? = ""

    @JvmStatic
    fun getHostFE(): String {
        if (!DuboxLog.isDebug() || hostFE.isNullOrBlank()) return HOST_SERVER_FE
        return hostFE ?: HOST_SERVER_FE
    }

    fun setHostFE(host: String?) {
        hostFE = host
    }

    /**
     * 更新控制流域名
     */
    fun setControlDomain(domain: String?) {
        if (domain?.isNotBlank() == true) {
            HostURLManager.domain = domain
        }
    }

    /**
     * 更新数据流域名
     */
    fun setDataDomain(domain: String?) {
        if (domain?.isNotBlank() == true) {
            dataDomain = String.format(DEFAULT_DATA_DOMAIN_FORMAT, domain)
            uploadPCSDomain = String.format(DEFAULT_UPLOAD_PCS_DOMAIN_FORMAT, domain)
            passportDomain = String.format(DEFAULT_PASSPORT_FORMAT, domain)
        }
    }

    /**
     * 下载
     */
    val DOWNLOAD_URL: String
        get() = "%s%s/rest/2.0/pcs/file?method=%s&path=%s&app_id=${AppCommon.getSecondBoxPcsAppId()}&app_name=pavobox&ec=1&check_blue=1"

    /**
     * 上传tmpfile
     */
    val UPLOAD_TMPFILE_URL: String =
        "%1s/rest/2.0/pcs/superfile2?method=upload&type=tmpfile&path=%2s&partoffset=%3\$s&app_name=pavobox&app_id=" + AppCommon.getSecondBoxPcsAppId()


    /** 外链视频流播放接口，返回播放文件  */
    @JvmStatic
    fun defaultShareVideoName(): String {
        return ("%1\$s" + getDomain() + "/share/streaming?app_id=${AppCommon.getSecondBoxPcsAppId()}&"
                + "uk=%2\$s&type=%3\$s&path=%4\$s&shareid=%5\$s&albumid=%6\$s&timestamp=%7\$s&sign=%8\$s&fid=%9"
                + "\$s&clienttype=%10\$s&channel=%11\$s&devuid=%12\$s&version=%13\$s&sekey=%14\$s&ehps=%15\$s&app_id=" + AppCommon.PCS_APP_ID)
    }

    /** 网页用到的视频流播放接口，返回播放文件  */
    @JvmStatic
    fun defaultWebVideoName(): String {
        return ("%1\$s" + getDomain() + "/share/streaming?"
                + "uk=%2\$s&type=%3\$s&path=%4\$s&shareid=%5\$s&albumid=%6\$s&timestamp=%7\$s&sign=%8\$s&fid=%9"
                + "\$s&clienttype=%10\$s&channel=%11\$s&devuid=%12\$s&version=%13\$s&sekey=%14\$s&ehps=%15\$s&app_id=" + AppCommon.PCS_APP_ID)
    }


    /**
     * 视频播放的流畅地址 不带bduss
     */
    @JvmStatic
    fun videoPlayUrlNoBduss(): String {
        return ("%1\$s" + getDomain()
                + "/api/streaming?app_id=" + AppCommon.getSecondBoxPcsAppId() + "&app_name=pavobox&type=%2\$s&path=%3\$s&ehps=%4\$s&"
                + Constants.SECRET_TOKEN + "=%5\$s")
    }

    /**
     * 外链环境 私密分享外链环境地址 http://dbl-rc-chunlei14.vm.baidu.com:8087/share/
     */
    @JvmStatic
    fun defaultShareHostName() = "$domainWithHttps/share/"

    /**
     * 封禁申诉URL
     */
    private const val SERVER_BAN_APPEAL_URL = "/wap/share/complaint?from=android"

    /**
     * 获取申诉URL
     */
    @JvmStatic
    fun getAppealUrl(banCode: Int): String? {
        val url: String = domainWithHttps + SERVER_BAN_APPEAL_URL
        DuboxLog.d("HostURLManager", " BAN DBG banCode:$banCode  url:$url")
        return url
    }

    /**
     * rest host
     */
    @JvmStatic
    fun getRestDefaultHostName(): String? {
        return "$domainWithHttps/rest/"
    }

    /**
     * api host
     */
    @JvmStatic
    fun getApiDefaultHostName(): String? {
        return "$domainWithHttps/api/"
    }

    /**
     * 设置配置文件的url
     */
    @JvmStatic
    fun getConfSetUrl(): String? {
        return getApiDefaultHostName() + "backups/set"
    }

    /**
     * 获取配置文件的url
     */
    @JvmStatic
    fun getConfGetUrl(): String? {
        return getApiDefaultHostName() + "backups/get"
    }

    /**
     * 云图地址
     */
    @JvmStatic
    fun newCloudImageHostName(): String {
        return "$domainWithHttps/aipic/"
    }

    /**
     * 有信号无网络判断检测地址
     */
    @JvmStatic
    fun defaultVerifierHostName(): String {
        return PRO_STR_HTTP + getDomain() + "/res/static/thirdparty/connect.jpg?t=" + Uri.encode("%@")
    }

    /**
     * statistics url
     */
    @JvmStatic
    fun statisticsUrl(): String {
        return "%s" + getDomain() + "/statistics?clienttype=%s&devuid=%s&channel=%s&version=%s&app_id=%s&app_name=%s"
    }


    /***
     * 获取统计日志上传链接：<br></br>
     * https://update.pan.baidu.com/statistics?clienttype=%s&devuid=%s&channel=%s&version=%s&isvip=%s";
     */
    @JvmStatic
    fun getUpdateStatisticsUrl(scheme: String?): String? {
        var rootUrl = String.format(
            statisticsUrl(), scheme,
            Uri.encode(RequestCommonParams.getClientType()), Uri.encode(AppCommon.DEVUID),
            Uri.encode(RequestCommonParams.getChannel()), Uri.encode(AppCommon.VERSION_DEFINED),
            Uri.encode(AppCommon.getSecondBoxPcsAppId()),Uri.encode("pavobox")
        )
        if (AppCommon.FIRST_LAUNCH_TIME > 0) {
            rootUrl += "&firstlaunchtime=" + AppCommon.FIRST_LAUNCH_TIME
        }
        rootUrl += if (RequestCommonParams.isVip()) {
            "&isVip=1"
        } else {
            "&isVip=0"
        }
        rootUrl += "&" + Constants.AF_MEDIA_SOURCE + "=" +
                RequestCommonParams.getAppInstallMediaSource() +
                "&" + Constants.APP_LANGUAGE + "=" + getDuboxLanguageCountry() +
                "&versioncode=" + AppCommon.VERSION_CODE
        return rootUrl
    }

    /**
     * 积分商城入口 url
     */
    @JvmStatic
    fun wapCoinsHome(): String {
        return "$domainWithHttps/wap/coins/home?logFrom=%d"
    }

    /**
     * 获得缩略图链接 卡顿优化 避免 [String.format] 耗时问题
     * @return 拼接后的缩略图url
     */
    @JvmStatic
    fun getThumbnailUrl(scheme: String?, domain: String?, path: String?, size: String?): String? {
        // 给予足够的空间，避免字符串复制损耗性能
        // 注意：和原 THUMBNAIL_URL 参数顺序保持一致，避免Glide缓存失效
        val sb = StringBuilder(INT_300)
        sb.append(scheme).append(domain).append("/rest/2.0/pcs/thumbnail").append("?")
            .append("method=generate")
            .append("&path=").append(path).append("&quality=80").append("&size=").append(size)
            .append("&app_id=")
            .append(AppCommon.getSecondBoxPcsAppId())
            .append("&app_name=")
            .append("pavobox")
        return sb.toString()
    }

    /**
     * 获得缩略图链接不带图片size，仅用于本地缓存管理 卡顿优化 避免 [String.format] 耗时问题
     * @return 拼接后的缩略图url
     */
    @JvmStatic
    fun getThumbnailUrlWithoutSize(scheme: String?, domain: String?, path: String?): String? {
        // 给予足够的空间，避免字符串复制损耗性能
        // 注意：和原 THUMBNAIL_URL_WITHOUT_SIZE 参数顺序保持一致，避免Glide缓存失效
        val sb = java.lang.StringBuilder(INT_300)
        sb.append(scheme).append(domain).append("/rest/2.0/pcs/thumbnail").append("?")
            .append("method=generate")
            .append("&path=").append(path).append("&quality=80").append("&app_id=")
            .append(AppCommon.getSecondBoxPcsAppId())
            .append("&app_name=")
            .append("pavobox")
        return sb.toString()
    }

    /**
     * locateupload、locatedownload等特殊接口是否使用HTTPS的开关<br/>
     * 为true时，以上接口ehps将使用1
     */
    @JvmStatic
    @SuppressWarnings("FunctionOnlyReturningConstant")
    fun getEhps() = "0"

    /**
     * pcs host
     * 端上用与预览图片
     */
    @JvmStatic
    fun getPCSHostName(): String {
        return PRO_STR_HTTPS + getDataDomain() + "/rest/2.0/pcs/file"
    }

    /**
     * pcs simple host
     * 端上用于下载文件
     */
    @JvmStatic
    fun getSimplePCSHostName(): String {
        return PRO_STR_HTTPS + getDataSimpleDomain() + "/rest/2.0/pcs/file"
    }


    /**
     * 添加获取p2p下载域名hostName的方法
     * @return
     */
    @JvmStatic
    fun getP2PHostName(): String {
        return "$domainWithHttps/cms"
    }

    /**
     * 检测是否是百度域的url请求
     * @return true是
     */
    @JvmStatic
    fun checkPavoBoxDomain(url: String?): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }
        getAllDomain().forEach { domain ->
            if (url.contains(domain)) {
                return true
            }
        }
        return false
    }


    /**
     * check公司域名
     */
    @JvmStatic
    fun checkDBDomain(url: String?): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }
        if (url.contains(DEFAULT_DOMAIN) || url.contains(".dubox.com")
            || url.contains("https://terabox.com")
        ) {
            return true
        }
        getAllDomain().forEach { domain ->
            if (url.contains(domain)) {
                return true
            }
        }
        return false
    }

    /**
     * 批量拉取视频m3u8文件的url，不带bduss
     */
    @JvmStatic
    fun videoM3u8DownloadUrlNoBduss(): String =
        ("%1\$s" + getDomain() + "/api/batch/streaming?app_id="
                + AppCommon.getSecondBoxPcsAppId() + "&app_name=pavobox&type=%2\$s&path=%3\$s&ehps=%4\$s")

    /**
     * 外链批量拉取视频m3u8文件的url，不带bduss，多个surl用","分隔
     */
    @JvmStatic
    fun shareVideoM3u8DownloadUrlNoBduss(): String = ("%1\$s" + getDomain()
            + "/share/preload/ShareStreaming?app_id=" + AppCommon.getSecondBoxPcsAppId()
            + "&app_name=pavobox&type=%2\$s&path=%3\$s&surl=%4\$s&ehps=%5\$s")

    /**
     * 根据url前缀判断是否是http或https
     * @param url
     * @return
     */
    @JvmStatic
    fun isNetURL(url: String?): Boolean {
        return !TextUtils.isEmpty(url) && (url?.startsWith(PRO_STR_HTTP) == true || url?.startsWith(
            PRO_STR_HTTPS
        ) == true)
    }

}
