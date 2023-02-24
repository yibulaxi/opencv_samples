package com.cool.yfc.tools.mediaselect.engine.image

import com.cool.yfc.tools.mediaselect.engine.image.picasso.PicassoEngine

/**
 *
 * @author yfc
 * @since 2022/07/29 13:56
 * @version V1.0
 */
object ImageEngine {
    val glide: GlideEngine = lazy { GlideEngine.instance }.value
    val coil: CoilEngine = lazy { CoilEngine.instance }.value
    val picasso: PicassoEngine = lazy { PicassoEngine.instance }.value
}