package com.moder.compass.base.imageloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.dubox.drive.base.imageloader.SimpleFileInfo
import com.dubox.drive.basemodule.R
import com.dubox.glide.load.DecodeFormat
import com.dubox.glide.load.engine.DiskCacheStrategy
import com.dubox.glide.request.RequestOptions

private var defaultDrawable: Drawable? = null
internal fun getDefaultDrawable(context: Context): Drawable? = defaultDrawable
    ?: context.resources.getDrawable(R.color.timeline_image_default_bg_color)?.also { defaultDrawable = it }

/**
 * Created by yeliangliang on 2020/8/26
 */
fun ImageView.loadThumb(
    context: Context, path: String, md5: String?, isLocalPath: Boolean,
    imageSize: ThumbnailSizeType = ThumbnailSizeType.THUMBNAIL_SIZE_96,
    listener: GlideLoadingListener<Any?>? = null
) {
    if (isLocalPath) {
        val requestOptions = RequestOptions()
        val gridThumbnailSize = ThumbnailHelper(
            context
        ).getImageSizeByType(imageSize)
        requestOptions.skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .override(gridThumbnailSize.mWidth, gridThumbnailSize.mHeight)
            .fitCenter()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(getDefaultDrawable(context))
        GlideHelper.getInstance().displayImageFromLocalFile(
            path, requestOptions, this, listener
        )
    } else {
        GlideHelper.getInstance().displayImageWithLoadFromInfo(
            null,
            SimpleFileInfo(path, md5),
            getDefaultDrawable(context), getDefaultDrawable(context) != null,
            null, null, true,
            imageSize, this, listener
        )
    }
}

/**
 * 
 */
fun ImageView.loadImage(url: String, placeHolder: Int = 0,
                        status: ((GlideLoadStatus) -> Unit)? = null) {
    GlideHelper.getInstance().displayImageFromNetwork(url, placeHolder, 0,
        0, true, this, object : GlideLoadingListener<Any?> {
        override fun onLoadStarted(imageView: View, placeholder: Drawable?) {
            status?.invoke(GlideLoadStatus.START)
        }

        override fun onLoadFailed(imageView: View, errorDrawable: Drawable?) {
            status?.invoke(GlideLoadStatus.FAILED)
        }

        override fun onResourceReady(imageView: View, resource: Any) {
            status?.invoke(GlideLoadStatus.SUCCESS)
        }

        override fun onLoadCleared(imageView: View, placeholder: Drawable?) {
            // do nothing
        }
    })
}

/**
 * 加载图片状态
 */
enum class GlideLoadStatus { START, FAILED, SUCCESS }