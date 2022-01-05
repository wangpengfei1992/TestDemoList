package com.wpf.common_ui.base

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.wpf.common_ui.utils.ContextUtil

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : This is description.
 */
abstract class BaseApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }
    override fun onCreate() {
        super.onCreate()
        ContextUtil.initContext(this)
        onCreateAfter()
    }
    abstract fun onCreateAfter()
}