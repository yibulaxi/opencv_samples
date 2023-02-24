package com.cool.yfc.tools.mediaselect.engine.image.picasso

import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.blankj.utilcode.util.ObjectUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler

/**
 * VideoRequestHandler
 */
class VideoRequestHandler : RequestHandler() {
    companion object {
        const val SCHEME_VIDEO = "video"
    }

    override fun canHandleRequest(data: Request): Boolean {
        return SCHEME_VIDEO.contentEquals(data.uri.scheme)
    }

    override fun load(request: Request, networkPolicy: Int): Result? {
        val uri = request.uri
        val path = uri.path
        if (!ObjectUtils.isEmpty(path)) {
            val bm = ThumbnailUtils.createVideoThumbnail(path!!, MediaStore.Images.Thumbnails.MINI_KIND)
            return Result(bm!!, Picasso.LoadedFrom.DISK)
        }
        return null
    }
}