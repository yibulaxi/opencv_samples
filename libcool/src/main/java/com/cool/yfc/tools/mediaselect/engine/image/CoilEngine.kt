package com.cool.yfc.tools.mediaselect.engine.image

import android.content.Context
import android.widget.ImageView
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.cool.yfc.ext.getDefaultImageError
import com.cool.yfc.ext.getDefaultImageFallback
import com.cool.yfc.ext.getDefaultImagePlaceholder
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * CoilEngine
 */
open class CoilEngine : ImageEngine {
    companion object {
        val instance: CoilEngine = lazy { CoilEngine() }.value
    }

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        val target = ImageRequest.Builder(context)
            .data(url)
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadImage(
        context: Context?,
        imageView: ImageView?,
        url: String?,
        maxWidth: Int,
        maxHeight: Int,
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        context?.let {
            val builder = ImageRequest.Builder(it)
            if (maxWidth > 0 && maxHeight > 0) {
                builder.size(maxWidth, maxHeight)
            }
            imageView?.let { v -> builder.data(url).target(v) }
            val request = builder.build();
            context.imageLoader.enqueue(request)
        }
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .transformations(RoundedCornersTransformation(8F))
            .size(180, 180)
            .placeholder(getDefaultImagePlaceholder())
            .error(getDefaultImageError())
            .fallback(getDefaultImageFallback())
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }

        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        val target = ImageRequest.Builder(context)
            .data(url)
            .size(270, 270)
            .placeholder(getDefaultImagePlaceholder())
            .error(getDefaultImageError())
            .fallback(getDefaultImageFallback())
            .target(imageView)
            .build()
        context.imageLoader.enqueue(target)
    }

    override fun pauseRequests(context: Context?) {
    }

    override fun resumeRequests(context: Context?) {
    }
}