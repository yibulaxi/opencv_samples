@file:JvmName(STRING_EXTEND)

package com.cool.yfc.ext

import java.util.regex.Pattern

/**
 *
 * @author yfc
 * @since 2022/07/29 09:12
 * @version V1.0
 */
/**
 * 是否包含中文
 */
fun String.haveChineseCharacter(): Boolean {
    val regEx = "[\u4e00-\u9fa5]"
    val pattern = Pattern.compile(regEx)
    val matcher = pattern.matcher(this)
    while (matcher.find()) {
        return true
    }
    return false
}

/**
 * 是否包含英文、数字
 */
fun String.haveLetterOrDigit(): Boolean {
    for (index in this.indices) {
        if (Character.isLetterOrDigit(this[index])) {
            return true
        }
    }
    return false
}

/**
 * 是否包含特殊字符
 */
fun String.haveSpecificSymbol(): Boolean {
    val regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
    val pattern = Pattern.compile(regEx)
    val matcher = pattern.matcher(this)
    return matcher.find()
}

fun String?.getOrEmpty(): String = if (this.isNullOrEmpty()) "" else this