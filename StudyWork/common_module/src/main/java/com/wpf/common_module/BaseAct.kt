package com.wpf.common_module

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseAct<T:BaseViewModel> :AppCompatActivity(){
    protected lateinit var mActivity:AppCompatActivity
    protected lateinit var mContext: Context
    protected lateinit var mViewModel: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutViewId())
        mActivity  = this
        mContext = this
        /*利用反射获取类实例*/
        val persistentClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        mViewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(persistentClass)
        onCreated(savedInstanceState)
    }
    abstract fun getLayoutViewId():Int
    abstract fun onCreated(savedInstanceState: Bundle?)
}