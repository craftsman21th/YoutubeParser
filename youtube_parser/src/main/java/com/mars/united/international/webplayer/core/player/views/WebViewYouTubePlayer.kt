package com.mars.united.international.webplayer.core.player.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.webkit.*
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.core.player.PlayerConstants
import com.mars.united.international.webplayer.core.player.YouTubePlayer
import com.mars.united.international.webplayer.core.player.YouTubePlayerBridge
import com.mars.united.international.webplayer.core.player.listeners.FullscreenListener
import com.mars.united.international.webplayer.core.player.listeners.YouTubePlayerListener
import com.mars.united.international.webplayer.core.player.options.IFramePlayerOptions
import com.mars.united.international.webplayer.core.player.toFloat
import com.mars.united.international.webplayer.core.player.utils.HtmlUtil
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


private
class YouTubePlayerImpl(private val webView: WebView) : YouTubePlayer {
    private val mainThread: Handler = Handler(Looper.getMainLooper())
    val listeners = mutableSetOf<YouTubePlayerListener>()

    override fun loadVideo(videoId: String, startSeconds: Float) {
        webView.invoke("loadVideo", videoId, startSeconds)
    }

    override fun cueVideo(videoId: String, startSeconds: Float) {
        webView.invoke("cueVideo", videoId, startSeconds)
    }
    override fun play() = webView.invoke("playVideo")
    override fun pause() = webView.invoke("pauseVideo")
    override fun mute() = webView.invoke("mute")
    override fun unMute() = webView.invoke("unMute")
    override fun setVolume(volumePercent: Int) {
        require(volumePercent in 0..100) { "Volume must be between 0 and 100" }
        webView.invoke("setVolume", volumePercent)
    }

    override fun seekTo(time: Float) = webView.invoke("seekTo", time)
    override fun setPlaySpeedRate(playSpeedRate: PlayerConstants.PlaySpeedRate) = webView.invoke("setPlaybackRate", playSpeedRate.toFloat())
    override fun setPlaybackQuality(quality: PlayerConstants.PlaybackQuality) = webView.loadUrl("javascript:setPlaybackQuality('${quality.name.toLowerCase()}')")
    override fun loadVideoQuality() = webView.loadUrl("javascript:loadVideoQuality()")


    override fun toggleFullscreen() = webView.invoke("toggleFullscreen")
    override fun addListener(listener: YouTubePlayerListener) = listeners.add(listener)
    override fun removeListener(listener: YouTubePlayerListener) = listeners.remove(listener)
    override fun hideAllYtbElement() {
        //webView.invoke("hideAllYtbElements")
        //webView.invoke("initPlayer")
    }

    fun release() {
        listeners.clear()
        mainThread.removeCallbacksAndMessages(null)
    }

    private fun WebView.invoke(function: String, vararg args: Any) {
        val stringArgs = args.map {
            if (it is String) {
                "'$it'"
            } else {
                it.toString()
            }
        }
        mainThread.post { loadUrl("javascript:$function(${stringArgs.joinToString(",")})") }
    }
}

internal object FakeWebViewYouTubeListener : FullscreenListener {
    override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {}
    override fun onExitFullscreen() {}
}

/**
 * WebView implementation of [YouTubePlayer]. The player runs inside the WebView, using the IFrame Player API.
 */
internal class WebViewYouTubePlayer constructor(
    context: Context,
    private val listener: FullscreenListener,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), YouTubePlayerBridge.YouTubePlayerBridgeCallbacks {

    /** Constructor used by tools */
    constructor(context: Context) : this(context, FakeWebViewYouTubeListener)

    init {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
    }

    private val _youTubePlayer = YouTubePlayerImpl(this)
    internal val youtubePlayer: YouTubePlayer get() = _youTubePlayer

    private lateinit var youTubePlayerInitListener: (YouTubePlayer) -> Unit

    internal var isBackgroundPlaybackEnabled = false

    internal fun initialize(initListener: (YouTubePlayer) -> Unit, playerOptions: IFramePlayerOptions?) {
        youTubePlayerInitListener = initListener
        initWebView(playerOptions ?: IFramePlayerOptions.default)
    }

    // create new set to avoid concurrent modifications
    override val listeners: Collection<YouTubePlayerListener> get() = _youTubePlayer.listeners.toSet()
    override fun getInstance(): YouTubePlayer = _youTubePlayer
    override fun onYouTubeIFrameAPIReady() = youTubePlayerInitListener(_youTubePlayer)
    fun addListener(listener: YouTubePlayerListener) = _youTubePlayer.listeners.add(listener)
    fun removeListener(listener: YouTubePlayerListener) = _youTubePlayer.listeners.remove(listener)

    override fun destroy() {
        _youTubePlayer.release()
        super.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(playerOptions: IFramePlayerOptions) {
        settings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_DEFAULT
            domStorageEnabled = true
        }

        addJavascriptInterface(YouTubePlayerBridge(this), "YouTubePlayerBridge")

        val htmlPage = readTextFromInputStream(resources.openRawResource(R.raw.ayp_youtube_player))
            .replace("<<injectedPlayerVars>>", playerOptions.toString())

        loadDataWithBaseURL(playerOptions.getOrigin(), htmlPage, "text/html", "utf-8", null)

        webChromeClient = object : WebChromeClient() {

            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                super.onShowCustomView(view, callback)
                listener.onEnterFullscreen(view) { callback.onCustomViewHidden() }
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                listener.onExitFullscreen()
            }

            override fun getDefaultVideoPoster(): Bitmap? {
                val result = super.getDefaultVideoPoster()
                // if the video's thumbnail is not in memory, show a black screen
                return result ?: Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
            }
        }

        webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                view ?: return super.shouldInterceptRequest(view, request)
                request ?: return super.shouldInterceptRequest(view, request)
                if (request.url.toString().contains("www-player.css")) {
                    val css = HtmlUtil.request2Text(request) + readTextFromInputStream(resources.openRawResource(R.raw.inject_css))
                    return WebResourceResponse("text/css", "UTF-8", ByteArrayInputStream(css.toByteArray()))
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        if (isBackgroundPlaybackEnabled && (visibility == View.GONE || visibility == View.INVISIBLE)) {
            return
        }

        super.onWindowVisibilityChanged(visibility)
    }

    fun toggleFullscreen() {
        _youTubePlayer.toggleFullscreen()
    }
}

@VisibleForTesting
internal fun readTextFromInputStream(inputStream: InputStream): String {
    inputStream.use {
        try {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "utf-8"))
            return bufferedReader.readLines().joinToString("\n")
        } catch (e: Exception) {
            throw RuntimeException("Can't parse HTML file.")
        }
    }
}
