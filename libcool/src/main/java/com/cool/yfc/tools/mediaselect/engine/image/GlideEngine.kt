package com.cool.yfc.tools.mediaselect.engine.image

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.cool.yfc.ext.getDefaultImageError
import com.cool.yfc.ext.getDefaultImageFallback
import com.cool.yfc.ext.getDefaultImagePlaceholder
import com.cool.yfc.ext.logE
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * Glide加载引擎
 */
open class GlideEngine : ImageEngine {
    companion object {
        val instance: GlideEngine = lazy { GlideEngine() }.value
    }

    /**
     * 加载图片
     *
     * @param context   上下文
     * @param url       资源url
     * @param imageView 图片承载控件
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    override fun loadImage(context: Context, imageView: ImageView, url: String, maxWidth: Int, maxHeight: Int) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        Glide.with(context)
            .load(url)
            .override(maxWidth, maxHeight)
            .into(imageView)
    }

    /**
     * 加载相册目录封面
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(8))
            .placeholder(getDefaultImagePlaceholder())
            .error(getDefaultImageError())
            .fallback(getDefaultImageFallback())
            .into(imageView)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        Glide.with(context)
            .load(url)
            .override(200, 200)
            .centerCrop()
            .placeholder(getDefaultImagePlaceholder())
            .error(getDefaultImageError())
            .fallback(getDefaultImageFallback())
            .into(imageView)
    }

    override fun pauseRequests(context: Context) {
        kotlin.runCatching {
            Glide.with(context).pauseRequests()
        }.onFailure {
            logE(it)
        }
    }

    override fun resumeRequests(context: Context) {
        kotlin.runCatching {
            Glide.with(context).resumeRequests()
        }.onFailure {
            logE(it)
        }
    }
}