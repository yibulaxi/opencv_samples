package com.cool.yfc.base

import android.app.Application
import android.content.Context

/**
 *
 * @author yfc
 * @since 2023/02/21 15:40
 * @version V1.0
 */
abstract class BaseApp : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        BaseInit.attachBaseContext(this)
    }

    override fun onCreate() {
        super.onCreate()

        BaseInit.onCreate(this)
    }
}