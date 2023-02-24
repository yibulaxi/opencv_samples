@file:JvmName(BEAN_EXTEND)

package com.cool.yfc.ext

import java.io.Serializable

/**
 *
 * @author yfc
 * @since 2022/10/08 16:47
 * @version V1.0
 */

/**
 * 实现Serializable的基类
 */
open class SBean : Serializable {
    companion object {
        private const val serialVersionUID: Long = -2665661709721937849L
    }
}