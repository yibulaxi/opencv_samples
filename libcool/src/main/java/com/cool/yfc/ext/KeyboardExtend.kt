@file:JvmName(KEYBOARD_EXTEND)

package com.cool.yfc.ext

import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RomUtils
import com.blankj.utilcode.util.ThreadUtils

/**
 *
 * @author yfc
 * @since 2022/07/29 09:14
 * @version V1.0
 */
fun <T : View> T?.showSoftInput() {
    this?.let {
        if (RomUtils.isHuawei()) {
            ThreadUtils.runOnUiThreadDelayed({ KeyboardUtils.showSoftInput(it) }, 500)
        } else {
            KeyboardUtils.showSoftInput(it)
        }
    }
}

fun <T : View> T?.hideSoftInput() {
    this?.let {
        KeyboardUtils.hideSoftInput(it)
    }
}