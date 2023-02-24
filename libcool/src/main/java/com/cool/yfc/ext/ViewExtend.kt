@file:JvmName(VIEW_EXTEND)

package com.cool.yfc.ext

import android.content.Context
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.allViews

/**
 *
 * @author yfc
 * @since 2022/07/29 09:12
 * @version V1.0
 */
fun clearText(vararg textViews: TextView) {
    setText("", *textViews)
}

fun setText(text: String?, vararg textViews: TextView) {
    textViews.forEach {
        it.text = text
    }
}

fun setTextColor(@ColorRes id: Int, context: Context, vararg textViews: TextView) {
    textViews.forEach {
        it.setTextColor(ContextCompat.getColor(context, id))
    }
}

fun setHintTextColor(@ColorRes id: Int, context: Context, vararg editTexts: EditText) {
    editTexts.forEach {
        it.setHintTextColor(ContextCompat.getColor(context, id))
    }
}

fun setAllViewVisibility(view: ViewGroup) {
    for (index in 0 until view.childCount) {
        val childView = view.getChildAt(index)
        if (childView is ViewGroup) {
            setAllViewVisibility(childView)
        } else {
            childView.visibility = View.VISIBLE
        }
    }
}

fun setViewsVisibility(visible: Boolean, vararg views: View) {
    views.forEach {
        it.visibility = if (visible) View.VISIBLE else View.GONE
    }
}

fun setViewsEnabled(isEnabled: Boolean, vararg views: View) {
    views.forEach {
        it.isEnabled = isEnabled
    }
}

fun View.handleChildTVNight(isNight: Boolean, colorNight: Int, colorDefault: Int) {
    if (this is ViewGroup) {
        handleChildTVNight(isNight, colorNight, colorDefault)
    }
}

fun ViewGroup.handleChildTVNight(isNight: Boolean, colorNight: Int, colorDefault: Int) {
    kotlin.runCatching {
        allViews.forEach {
            if (it is TextView) {
                it.setTextColor(if (isNight) colorNight else colorDefault)
            }
        }
    }.onFailure {
        logE(it)
    }
}

fun View?.getFirstChild(): View? {
    return if (this is ViewGroup) {
        this.getFirstChild()
    } else {
        null
    }
}

fun ViewGroup?.getFirstChild(): View? {
    return if (this != null && this.childCount > 0) {
        this.getChildAt(0)
    } else {
        null
    }
}

fun Editable?.getEditableSafe(): String = this.getEditableSafe(false)
fun Editable?.getEditableSafe(trim: Boolean): String {
    if (this != null) {
        val string = this.toString()
        return if (trim) string.trim() else string
    }
    return ""
}