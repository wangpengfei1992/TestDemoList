package com.wpf.func_login.api

import com.wpf.common_net.bean.BaseResp
import com.wpf.func_conmmon.bean.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */
interface LoginApi {
    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(@Field("username") username: String, @Field("password") password: String
                         , @Field("repassword") repassword: String) : BaseResp<LoginResponse>

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(@Field("username") username: String,@Field("password") password: String): BaseResp<LoginResponse>
}