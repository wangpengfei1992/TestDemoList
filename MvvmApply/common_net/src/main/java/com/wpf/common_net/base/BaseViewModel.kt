package com.wpf.common_net.base

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */
typealias EmitBlock<T> = suspend LiveDataScope<T>.() -> T
typealias EmitBlock1<T> = suspend MutableLiveData<T>.() -> T

open class BaseViewModel :ViewModel(){
    fun <T:Any> launch(block: suspend ()->StateLiveData<T>){
        viewModelScope.launch{
            block()
        }
    }
    fun <T : Any> emit(block: EmitBlock<T>): LiveData<T> = liveData {
        var result: T? = null
        withContext(Dispatchers.IO) {
            result = block()
        }
        emit(result!!)
    }

}