@file:JvmName(EVENTBUS_EXTEND)

package com.cool.yfc.ext

import org.greenrobot.eventbus.EventBus

/**
 *
 * @author yfc
 * @since 2022/07/29 09:17
 * @version V1.0
 */
fun Any?.eventBusRegister() {
    runCatching {
        this?.let {
            if (!getDefaultEventBus().isRegistered(it)) {
                getDefaultEventBus().register(it)
            }
        }
    }.onFailure {
        logE(it)
    }
}

fun Any?.eventBusUnRegister() {
    runCatching {
        this?.let {
            if (getDefaultEventBus().isRegistered(it)) {
                getDefaultEventBus().unregister(it)
            }
        }
    }.onFailure {
        logE(it)
    }
}

fun getDefaultEventBus(): EventBus = EventBus.getDefault()