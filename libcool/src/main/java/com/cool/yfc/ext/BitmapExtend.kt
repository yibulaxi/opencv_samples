@file:JvmName(BITMAP_EXTEND)

package com.cool.yfc.ext

import android.graphics.Bitmap
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 *
 * @author yfc
 * @since 2022/07/29 09:11
 * @version V1.0
 */
@OptIn(ExperimentalContracts::class)
fun Bitmap?.valid(): Boolean {
    contract {
        returns(true) implies (this@valid != null)
    }
    return this != null && !isRecycled
}

fun Bitmap?.invalid(): Boolean {
    return !valid()
}

fun Bitmap?.recycleSafe() {
    if (valid()) {
        recycle()
    }
}
