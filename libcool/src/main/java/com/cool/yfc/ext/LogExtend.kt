@file:JvmName(LOG_EXTEND)

package com.cool.yfc.ext

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.cool.yfc.BuildConfig
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 *
 * @author yfc
 * @since 2022/07/29 09:10
 * @version V1.0
 */

fun getLogConfig(): LogUtils.Config = LogUtils.getConfig()

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logV(any: Any?) {
    logV(any, BASE_TAG)
}

fun logV(any: Any?, tag: String) {
    logV(any, tag, true)
}

fun logV(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.V, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logD(any: Any?) {
    logD(any, BASE_TAG)
}

fun logD(any: Any?, tag: String) {
    logD(any, tag, true)
}

fun logD(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.D, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logI(any: Any?) {
    logI(any, BASE_TAG)
}

fun logI(any: Any?, tag: String) {
    logI(any, tag, true)
}

fun logI(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.I, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logW(any: Any?) {
    logW(any, BASE_TAG)
}

fun logW(any: Any?, tag: String) {
    logW(any, tag, true)
}

fun logW(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.W, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logE(any: Any?) {
    logE(any, BASE_TAG)
}

fun logE(any: Any?, tag: String) {
    logE(any, tag, true)
}

fun logE(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.E, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
fun logA(any: Any?) {
    logA(any, BASE_TAG)
}

fun logA(any: Any?, tag: String) {
    logA(any, tag, true)
}

fun logA(any: Any? = null, tag: String = BASE_TAG, simpleFormat: Boolean = true) {
    log(any, tag, LogUtils.A, simpleFormat)
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

fun log(any: Any? = null, theTag: String = BASE_TAG, level: Int = LogUtils.E, simpleFormat: Boolean = true) {
    if (!BuildConfig.DEBUG) return

    val tag = if (theTag.length > 30) theTag.substring(0, 30) else theTag

    when (level) {
        LogUtils.V -> if (any.canSimpleFormat(simpleFormat)) Log.v(tag, any) else LogUtils.vTag(tag, any)
        LogUtils.D -> if (any.canSimpleFormat(simpleFormat)) Log.d(tag, any) else LogUtils.dTag(tag, any)
        LogUtils.I -> if (any.canSimpleFormat(simpleFormat)) Log.i(tag, any) else LogUtils.iTag(tag, any)
        LogUtils.W -> if (any.canSimpleFormat(simpleFormat)) Log.w(tag, any) else LogUtils.wTag(tag, any)
        LogUtils.E -> if (any.canSimpleFormat(simpleFormat)) Log.e(tag, any) else LogUtils.eTag(tag, any)
        LogUtils.A -> if (any.canSimpleFormat(simpleFormat)) Log.wtf(tag, any) else LogUtils.aTag(tag, any)
        else -> LogUtils.eTag(tag, any)
    }
}

@OptIn(ExperimentalContracts::class)
private fun Any?.canSimpleFormat(simpleFormat: Boolean): Boolean {
    contract {
        returns(true) implies (this@canSimpleFormat is String)
    }

    return simpleFormat && this is String
}