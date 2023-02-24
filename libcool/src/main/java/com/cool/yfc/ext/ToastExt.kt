@file:JvmName(TOAST_EXTEND)

package com.cool.yfc.ext

import androidx.annotation.StringRes
import com.blankj.utilcode.util.ToastUtils

/**
 *
 * @author yfc
 * @since 2022/12/16 15:07
 * @version V1.0
 */
fun CharSequence?.showShortToast() {
    if (!this.isNullOrEmpty()) {
        showShort(this)
    }
}

fun @receiver:StringRes Int.showShortToast() {
    kotlin.runCatching {
        showShort(this)
    }.onFailure {
        logE(it)
    }
}

fun CharSequence?.showLongToast() {
    if (!this.isNullOrEmpty()) {
        showLong(this)
    }
}

fun @receiver:StringRes Int.showLongToast() {
    kotlin.runCatching {
        showLong(this)
    }.onFailure {
        logE(it)
    }
}

///////////////////////////////////////////////////////////////////////////Short/////////////////////////////////////////////////////////////////////
fun showShort(text: CharSequence?) {
    if (!text.isNullOrEmpty()) {
        ToastUtils.showShort(text)
    }
}

fun showShort(@StringRes resId: Int) {
    runCatching {
        ToastUtils.showShort(resId)
    }.onFailure {
        logE(it)
    }
}

fun showShort(@StringRes resId: Int, vararg args: Any?) {
    runCatching {
        ToastUtils.showShort(resId, args)
    }.onFailure {
        logE(it)
    }
}

fun showShort(format: String?, vararg args: Any?) {
    runCatching {
        ToastUtils.showShort(format, args)
    }.onFailure {
        logE(it)
    }
}
///////////////////////////////////////////////////////////////////////////Short/////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////Long/////////////////////////////////////////////////////////////////////
fun showLong(text: CharSequence?) {
    if (!text.isNullOrEmpty()) {
        ToastUtils.showLong(text)
    }
}

fun showLong(@StringRes resId: Int) {
    runCatching {
        ToastUtils.showLong(resId)
    }.onFailure {
        logE(it)
    }
}

fun showLong(@StringRes resId: Int, vararg args: Any?) {
    runCatching {
        ToastUtils.showLong(resId, args)
    }.onFailure {
        logE(it)
    }
}

fun showLong(format: String?, vararg args: Any?) {
    runCatching {
        ToastUtils.showLong(format, args)
    }.onFailure {
        logE(it)
    }
}
///////////////////////////////////////////////////////////////////////////Long/////////////////////////////////////////////////////////////////////

fun makeToastUtils(): ToastUtils = ToastUtils.make()

fun getDefaultMakerToastUtils(): ToastUtils = ToastUtils.getDefaultMaker()

fun showPermissionDenied() = showLong("您拒绝了相关权限的使用，无法进行该操作")