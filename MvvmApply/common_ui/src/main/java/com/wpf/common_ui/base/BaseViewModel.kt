package com.wpf.common_ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *  Author: feipeng.wang
 *  Time:   2021/12/31
 *  Description : This is description.
 */
open class BaseViewModel : ViewModel(){
    //处理异常
    val mException: MutableLiveData<Throwable> = MutableLiveData()
}