package com.wpf.common_net.base

import android.util.Log
import com.wpf.common_net.bean.BaseResp
import com.wpf.common_net.bean.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.Exception

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : 封装网络请求过程、结果处理
 */
open class BaseRepository {
    private val TAG = this::class.java.simpleName

    /*方式-*/
    suspend fun <T : Any> executeResp(
        stateLiveData: StateLiveData<T>,
        block: suspend () -> BaseResp<T>
    ): StateLiveData<T> {
        var baseResp = BaseResp<T>()
        try {
            baseResp.dataState = DataState.STATE_LOADING
            baseResp = block.invoke()
            if (baseResp.errorCode == 0) {
                //请求成功，判断数据是否为空，
                //因为数据有多种类型，需要自己设置类型进行判断
                if (baseResp.data == null || baseResp.data is List<*> && (baseResp.data as List<*>).size == 0) {
                    //TODO: 数据为空,结构变化时需要修改判空条件
                    baseResp.dataState = DataState.STATE_EMPTY
                } else {
                    //请求成功并且数据为空的情况下，为STATE_SUCCESS
                    baseResp.dataState = DataState.STATE_SUCCESS
                }
            } else {
                baseResp.dataState = DataState.STATE_FAILED
            }

        } catch (e: Exception) {
            baseResp.dataState = DataState.STATE_ERROR
            baseResp.error = e
            baseResp.errorMsg = e.message
        } finally {
            stateLiveData.postValue(baseResp)
        }
        return stateLiveData
    }

    /**
     * 方式二：结合Flow请求数据。
     * 根据Flow的不同请求状态，如onStart、onEmpty、onCompletion等设置baseResp.dataState状态值，
     * 最后通过stateLiveData分发给UI层。
     *
     * @param block api的请求方法
     * @param stateLiveData 每个请求传入相应的LiveData，主要负责网络状态的监听
     */
    suspend fun <T : Any> executeReqWithFlow(
        stateLiveData: StateLiveData<T>,
        block: suspend () -> BaseResp<T>
    ): StateLiveData<T> {
        var baseResp = BaseResp<T>()
        flow {
            baseResp = block.invoke()
            Log.d(TAG, "executeReqWithFlow: $baseResp")
            if (baseResp.errorCode == 0) {
                //请求成功，判断数据是否为空，
                //因为数据有多种类型，需要自己设置类型进行判断
                if (baseResp.data == null || baseResp.data is List<*> && (baseResp.data as List<*>).size == 0) {
                    //TODO: 数据为空,结构变化时需要修改判空条件
                    baseResp.dataState = DataState.STATE_EMPTY
                } else {
                    //请求成功并且数据为空的情况下，为STATE_SUCCESS
                    baseResp.dataState = DataState.STATE_SUCCESS
                }
            } else {
                baseResp.dataState = DataState.STATE_FAILED
            }
            emit(baseResp)
        }.flowOn(Dispatchers.IO)
            .onStart {
                Log.d(TAG, "executeReqWithFlow:onStart")
                baseResp.dataState = DataState.STATE_LOADING
                emit(baseResp)
            }
            /*.onEmpty {
                Log.d(TAG, "executeReqWithFlow:onEmpty")
                baseResp.dataState = DataState.STATE_EMPTY
                stateLiveData.postValue(baseResp)
            }
            .catch { exception ->
                run {
                    Log.d(TAG, "executeReqWithFlow:code  ${baseResp.errorCode}")
                    exception.printStackTrace()
                    baseResp.dataState = DataState.STATE_ERROR
                    baseResp.error = exception
                    stateLiveData.postValue(baseResp)
                }
            }*/
            .collect {
                Log.d(TAG, "executeReqWithFlow: collect")
                stateLiveData.postValue(baseResp)
            }
        return stateLiveData
    }
}