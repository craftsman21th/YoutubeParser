package com.moder.compass.ui.widget.tooltip

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieTask
import com.moder.compass.BaseApplication
import com.moder.compass.business.kernel.HostURLManager
import com.moder.compass.sns.util.Utility
import com.moder.compass.ui.lottie.CDN_HOST_PREFIX
import java.io.File

/**
 * @Author 陈剑锋
 * @Date 2023/8/9-18:02
 * @Desc
 */
object LottieUtil {

    private const val DEFAULT_LOTTIE_JSON: String = ""

    fun fetchRemote(remoteUrl: String, onResult: (composition: LottieComposition) -> Unit) {
        kotlin.runCatching {
            val fullRemoteUrl = if (remoteUrl.startsWith(Utility.HTTPS_SCHEME) || remoteUrl.startsWith(
                    Utility.HTTP_SCHEME
                )) {
                // 如果已经是全路径，不需要拼接动态host
                remoteUrl
            } else {
                CDN_HOST_PREFIX + HostURLManager.getDomainNoWWW() + if (remoteUrl.startsWith(File.separator)) {
                    remoteUrl
                } else {
                    File.separator + remoteUrl
                }
            }
            LottieCompositionFactory.fromUrl(BaseApplication.getContext(), fullRemoteUrl).addListener { composition ->
                if (composition == null) {
                    getDefaultComposition()?.addListener { composition ->
                        if (composition == null) {
                            return@addListener
                        }
                        onResult.invoke(composition)
                    }
                    return@addListener
                }
                onResult.invoke(composition)
            }
        }.onFailure {
            getDefaultComposition()?.addListener { composition ->
                if (composition == null) {
                    return@addListener
                }
                onResult.invoke(composition)
            }
        }
    }

    fun getDefaultComposition(): LottieTask<LottieComposition>? {
        return LottieCompositionFactory.fromAsset(
            BaseApplication.getContext(),
            DEFAULT_LOTTIE_JSON
        )
    }

}
