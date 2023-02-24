package com.cool.yfc.tools.mediaselect.engine.image.picasso

import android.graphics.Bitmap
import com.blankj.utilcode.util.ImageUtils
import com.squareup.picasso.Transformation

/**
 * RoundedCornersTransform
 */
open class RoundedCornersTransform(private val roundingRadius: Float) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        return ImageUtils.toRoundCorner(source, roundingRadius)
    }

    override fun key(): String {
        return "rounded_corners"
    }
}