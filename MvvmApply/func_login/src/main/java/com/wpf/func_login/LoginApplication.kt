package com.wpf.func_login

import com.wpf.common_ui.base.BaseApplication
import com.wpf.func_login.di.loginModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : This is description.
 */
class LoginApplication : BaseApplication() {
    private val allModules = arrayListOf<Module>(loginModule)
    override fun onCreateAfter() {
        initKoin()
    }

    private fun initKoin() {
        startKoin{
            androidContext(this@LoginApplication)
            modules(allModules)
        }
    }

}