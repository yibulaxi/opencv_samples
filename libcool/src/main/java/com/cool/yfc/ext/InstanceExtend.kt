@file:JvmName(INSTANCE_EXTEND)

package com.cool.yfc.ext

/**
 *
 * @author yfc
 * @since 2022/07/22 11:16
 * @version V1.0
 */
inline fun <reified T> newInstance(): T {
    return T::class.java.newInstance()
}