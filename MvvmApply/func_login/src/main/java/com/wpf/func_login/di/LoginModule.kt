package com.wpf.func_login.di

import com.wpf.common_net.base.RetrofitManager
import com.wpf.func_login.api.LoginApi
import com.wpf.func_login.api.LoginRep
import com.wpf.func_login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */

val loginModule = module {
    single { RetrofitManager.initRetrofit().getService(LoginApi::class.java) }
    single { LoginRep(get()) }
    viewModel { LoginViewModel(get()) }
}