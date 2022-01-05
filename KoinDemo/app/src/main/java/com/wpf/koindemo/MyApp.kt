package com.wpf.koindemo

import android.app.Application
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{ modules(appModel)}
    }
}