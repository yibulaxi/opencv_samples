@file:JvmName(CLOSE_EXTEND)

package com.cool.yfc.ext

import android.app.Dialog
import android.widget.PopupWindow
import io.reactivex.disposables.Disposable
import java.io.Closeable

/**
 *
 * @author yfc
 * @since 2022/07/29 09:14
 * @version V1.0
 */
fun Disposable?.closeSafe() {
    kotlin.runCatching {
        if (this != null && !this.isDisposed) {
            this.dispose()
        }
    }.onFailure {
        logE(it)
    }
}

fun io.reactivex.rxjava3.disposables.Disposable?.closeSafe() {
    kotlin.runCatching {
        if (this != null && !this.isDisposed) {
            this.dispose()
        }
    }.onFailure {
        logE(it)
    }
}

fun Closeable?.closeSafe() {
    kotlin.runCatching {
        this?.close()
    }.onFailure {
        logE(it)
    }
}

fun Dialog?.dismissSafe() {
    try {
        if (this != null && this.isShowing) {
            this.dismiss()
        }
    } catch (e: Exception) {
        logE(e)
    }
}

fun PopupWindow?.dismissSafe() {
    try {
        if (this != null && this.isShowing) {
            this.dismiss()
        }
    } catch (e: Exception) {
        logE(e)
    }
}