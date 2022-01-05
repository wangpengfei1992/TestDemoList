package com.wpf.skinchange

import android.app.Application
import android.content.Context

/**
 *  Author: feipeng.wang
 *  Time:   2021/9/7
 *  Description : This is description.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        QDSkinManager.install(this);
    }
}