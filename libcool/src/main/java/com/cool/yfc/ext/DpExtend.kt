@file:JvmName(DP_EXTEND)

package com.cool.yfc.ext

import android.content.Context
import android.content.res.Resources

/**
 *
 * @author yfc
 * @since 2022/07/29 09:15
 * @version V1.0
 */
fun Context?.px2dp(pxValue: Float): Int {
    return this?.resources.px2dp(pxValue)
}

fun Resources?.px2dp(pxValue: Float): Int {
    return if (this != null) {
        val density = displayMetrics.density
        (pxValue / density + 0.5f).toInt()
    } else {
        0
    }
}

fun Context?.dp2px(dpValue: Float): Int {
    return this?.resources.dp2px(dpValue)
}

fun Resources?.dp2px(dpValue: Float): Int {
    return if (this != null) {
        val density = displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    } else {
        0
    }
}

fun Context?.px2sp(pxValue: Float): Int {
    return this?.resources.px2sp(pxValue)
}

fun Resources?.px2sp(pxValue: Float): Int {
    return if (this != null) {
        val scaleDensity = displayMetrics.scaledDensity
        return (pxValue / scaleDensity + 0.5f).toInt()
    } else {
        0
    }
}

fun Context?.sp2px(spValue: Float): Int {
    return this?.resources.sp2px(spValue)
}

fun Resources?.sp2px(spValue: Float): Int {
    return if (this != null) {
        val scaleDensity = displayMetrics.scaledDensity
        return (spValue * scaleDensity + 0.5f).toInt()
    } else {
        0
    }
}