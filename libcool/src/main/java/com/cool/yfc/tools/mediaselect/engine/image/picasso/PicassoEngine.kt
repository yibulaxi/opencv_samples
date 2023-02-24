package com.cool.yfc.tools.mediaselect.engine.image.picasso

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.cool.yfc.ext.getDefaultImagePlaceholder
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Picasso加载引擎
 */
open class PicassoEngine private constructor() : ImageEngine {
    companion object {
        val instance: PicassoEngine = lazy { PicassoEngine() }.value
    }

    /**
     * 加载图片
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val videoRequestHandler = VideoRequestHandler()
        if (PictureMimeType.isContent(url) || PictureMimeType.isHasHttp(url)) {
            Picasso.get().load(Uri.parse(url)).into(imageView)
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                val picasso = Picasso.Builder(context.applicationContext)
                    .addRequestHandler(videoRequestHandler)
                    .build()
                picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + url)
                    .into(imageView)
            } else {
                Picasso.get().load(File(url)).into(imageView)
            }
        }
    }

    override fun loadImage(context: Context, imageView: ImageView, url: String, maxWidth: Int, maxHeight: Int) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val picasso = Picasso.Builder(context)
            .build()
        val request = picasso.load(if (PictureMimeType.isContent(url)) Uri.parse(url) else Uri.fromFile(File(url)))
        request.config(Bitmap.Config.RGB_565)
        if (maxWidth > 0 && maxHeight > 0) {
            request.resize(maxWidth, maxHeight)
        }
        request.into(imageView)
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val videoRequestHandler = VideoRequestHandler()
        if (PictureMimeType.isContent(url)) {
            Picasso.get()
                .load(Uri.parse(url))
                .resize(180, 180)
                .centerCrop()
                .noFade()
                .transform(RoundedCornersTransform(8F))
                .placeholder(getDefaultImagePlaceholder())
                .into(imageView)
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                val picasso = Picasso.Builder(context.applicationContext)
                    .addRequestHandler(videoRequestHandler)
                    .build()
                picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + url)
                    .resize(180, 180)
                    .centerCrop()
                    .noFade()
                    .transform(RoundedCornersTransform(8F))
                    .placeholder(getDefaultImagePlaceholder())
                    .into(imageView)
            } else {
                Picasso.get()
                    .load(File(url))
                    .resize(180, 180)
                    .centerCrop()
                    .noFade()
                    .transform(RoundedCornersTransform(8F))
                    .placeholder(getDefaultImagePlaceholder())
                    .into(imageView)
            }
        }
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
        val videoRequestHandler = VideoRequestHandler()
        if (PictureMimeType.isContent(url)) {
            Picasso.get()
                .load(Uri.parse(url))
                .resize(200, 200)
                .centerCrop()
                .noFade()
                .placeholder(getDefaultImagePlaceholder())
                .into(imageView)
        } else {
            if (PictureMimeType.isUrlHasVideo(url)) {
                val picasso = Picasso.Builder(context.applicationContext)
                    .addRequestHandler(videoRequestHandler)
                    .build()
                picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + url)
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .placeholder(getDefaultImagePlaceholder())
                    .into(imageView)
            } else {
                Picasso.get()
                    .load(File(url))
                    .resize(200, 200)
                    .centerCrop()
                    .noFade()
                    .placeholder(getDefaultImagePlaceholder())
                    .into(imageView)
            }
        }
    }

    override fun pauseRequests(context: Context) {
        Picasso.get().pauseTag(context)
    }

    override fun resumeRequests(context: Context) {
        Picasso.get().resumeTag(context)
    }
}