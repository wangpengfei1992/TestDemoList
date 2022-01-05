package com.wpf.common_ui.base

import android.os.Bundle
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.anker.common.utils.WeakHandler

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : This is description.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    protected val TAG = this::class.java.simpleName
    private lateinit var _binding: VB
    protected val mViewBinding get() = _binding
    lateinit var mBaseHandler:WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        onCreateBefore()
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding.root)
        mBaseHandler = WeakHandler(this, Looper.myLooper()!!)
        onCreateAfter()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBaseHandler!=null){
            mBaseHandler.removeCallbacksAndMessages(null)
        }
    }

    open fun onCreateBefore(){}
    abstract fun getViewBinding(): VB
    abstract fun onCreateAfter()
    open fun handleMessage(msg: Message) {}
}