package com.wpf.common_ui.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/7
 *  Description : This is description.
 */
abstract class BaseFragment<VB : ViewBinding> :Fragment(){
    protected val TAG = this::class.java.simpleName
    private lateinit var _binding: VB
    protected val mViewBinding get() = _binding
    lateinit var mRootView: View
    val mBaseHandler: Handler by lazy { WithoutLeakHandler(this,Looper.getMainLooper()) }
    companion object {
        private class WithoutLeakHandler(fragment: BaseFragment<*>, mainLooper: Looper) : Handler(mainLooper){
            private var mFragment: WeakReference<BaseFragment<*>> = WeakReference(fragment)
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (mFragment.get()!=null){
                    handleMessage(msg)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        mRootView = _binding.root
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateAfter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBaseHandler!=null){
            mBaseHandler.removeCallbacksAndMessages(null)
        }
    }
    open fun onCreateAfter(){}
    open fun handleMessage(msg: Message) {}
    abstract fun getViewBinding(inflater: LayoutInflater, viewGroup: ViewGroup?): VB

}