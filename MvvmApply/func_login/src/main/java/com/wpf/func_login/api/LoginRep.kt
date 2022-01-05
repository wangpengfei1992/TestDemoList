package com.wpf.func_login.api

import com.wpf.common_net.base.BaseRepository
import com.wpf.common_net.base.StateLiveData
import com.wpf.func_conmmon.bean.LoginResponse

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */
class LoginRep(var api: LoginApi) : BaseRepository() {

    suspend fun login(
        userName: String,
        password: String,
        loginLiveData: StateLiveData<LoginResponse>
    ) = executeReqWithFlow(loginLiveData) {
        api.login(userName, password)
    }

    suspend fun rigester(
        userName: String,
        password: String,
        rePassword: String,
        rigesterLiveData: StateLiveData<LoginResponse>
    ) = executeReqWithFlow(rigesterLiveData) {
        api.register(userName, password, rePassword)
    }
}