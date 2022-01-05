package com.wpf.common_net.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wpf.common_net.bean.BaseResp
import com.wpf.common_net.bean.DataState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */
 fun <T:Any> StateLiveData<T>.callBackObserver(
    owner: LifecycleOwner,
    success: (BaseResp<T>) -> Unit,
    error: (BaseResp<T>) -> Unit,
    start:()->Unit
) {
    this.observe(owner, Observer<BaseResp<T>> {
        when(it.dataState){
            DataState.STATE_SUCCESS, DataState.STATE_EMPTY->{
                success(it)
            }
            DataState.STATE_FAILED, DataState.STATE_ERROR->{
                error(it)
            }
            DataState.STATE_LOADING->{
                start()
            }
        }
    })
}
fun <T:Any> StateLiveData<T>.callBackObserver(
    owner: LifecycleOwner,
    success: (BaseResp<T>) -> Unit,
    error: (BaseResp<T>) -> Unit,
) {
    this.observe(owner, Observer<BaseResp<T>> {
        when(it.dataState){
            DataState.STATE_SUCCESS, DataState.STATE_EMPTY->{
                success(it)
            }
            DataState.STATE_FAILED, DataState.STATE_ERROR->{
                error(it)
            }
        }
    })
}
fun <T:Any> StateLiveData<T>.callBackObserver(
    owner: LifecycleOwner,
    success: (BaseResp<T>) -> Unit,
) {
    this.observe(owner, Observer<BaseResp<T>> {
        when(it.dataState){
            DataState.STATE_SUCCESS, DataState.STATE_EMPTY->{
                success(it)
            }
        }
    })
}

fun ViewModel.launch(
    block: suspend CoroutineScope.() -> Unit,
    onError: (e: Throwable) -> Unit = {},
    onComplete: () -> Unit = {}
) {
    viewModelScope.launch(CoroutineExceptionHandler { _, e -> onError(e) }) {
        try {
            block.invoke(this)
        } finally {
            onComplete()
        }
    }
}