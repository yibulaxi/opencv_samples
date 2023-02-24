package com.cool.yfc.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.alibaba.android.arouter.launcher.ARouter
import com.cool.yfc.BuildConfig
import com.cool.yfc.ext.getLogConfig

/**
 *
 * @author yfc
 * 做一些初始化工作 需要符合应用市场隐私政策
 * @since 2023/02/23 09:31
 * @version V1.0
 */
object BaseInit {
    fun attachBaseContext(base: Context?) {
        MultiDex.install(base)
    }

    fun onCreate(application: Application) {
        // RouterConfig need set ROUTER_APP value!!!
        // RouterConfig need set ROUTER_APP value!!!
        // RouterConfig need set ROUTER_APP value!!!

        initARouter(application)
        initLog()
    }

    private fun initARouter(application: Application) {
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }

        ARouter.init(application)
    }

    private fun initLog() {
        getLogConfig().isLogSwitch = BuildConfig.DEBUG
    }
}