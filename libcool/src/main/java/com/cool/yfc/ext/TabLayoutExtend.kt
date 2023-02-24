@file:JvmName(TAB_LAYOUT_EXTEND)

package com.cool.yfc.ext

import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.blankj.utilcode.util.ReflectUtils
import com.cool.yfc.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

/**
 *
 * @author yfc
 * @since 2023/01/12 11:28
 * @version V1.0
 */
fun TabLayout.handleTextAppearance() {
    kotlin.runCatching {
        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.setTextAppearance(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.setTextAppearance(false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        getTabAt(selectedTabPosition)?.setTextAppearance(true)
    }.onFailure {
        logE(it)
    }
}

fun TabLayout.Tab.setTextAppearance(isSelect: Boolean) {
    runCatching {
        ReflectUtils.reflect(this).field("view").get<TabLayout.TabView>()?.apply {
            val textView: TextView? = ReflectUtils.reflect(view).field("textView").get<TextView>()
            textView?.apply {
                TextViewCompat.setTextAppearance(this, if (isSelect) R.style.TabLayoutTextStyleBold else R.style.TabLayoutTextStyleNormal)
            }
        }
    }.onFailure {
        logE(it)
    }
}