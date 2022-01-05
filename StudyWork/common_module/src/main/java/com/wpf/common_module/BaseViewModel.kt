package com.wpf.common_module

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel(){
    var mutableLiveData:MutableLiveData<String> = MutableLiveData()

    fun launch(mutableLiveData:MutableLiveData<String>,block:()->Unit){
        viewModelScope.launch {
            block()
            mutableLiveData.value = "wpf"
        }
    }
}