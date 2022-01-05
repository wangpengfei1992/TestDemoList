package com.wpf.koindemo.demo

import androidx.lifecycle.ViewModel

class HelloViewModel (private val repository: HelloRepository):ViewModel(){
    fun sayHello() = repository.getHello()
}