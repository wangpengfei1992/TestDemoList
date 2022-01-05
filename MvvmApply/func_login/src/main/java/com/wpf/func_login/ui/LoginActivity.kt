package com.wpf.func_login.ui

import com.wpf.common_net.base.callBackObserver
import com.wpf.common_ui.base.BaseActivity
import com.wpf.common_ui.utils.LogUtil
import com.wpf.func_login.databinding.ActivityLoginMainBinding
import com.wpf.func_login.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 *  Author: feipeng.wang
 *  Time:   2021/7/8
 *  Description : This is description.
 */
class LoginActivity : BaseActivity<ActivityLoginMainBinding>() {

    private val mViewModel: LoginViewModel by viewModel()
    override fun getViewBinding(): ActivityLoginMainBinding = ActivityLoginMainBinding.inflate(layoutInflater)

    override fun onCreateAfter() {
        mViewBinding.login.setOnClickListener {

            mViewModel.login("123", "123456")

        }
        mViewBinding.rigster.setOnClickListener {

        }
        mViewModel.loginLiveData.callBackObserver(this,{
            LogUtil.e(TAG,"成功")
        },{
            LogUtil.e(TAG,"失败，${it.errorMsg}")
        },{
            LogUtil.e(TAG,"开始")
        })
    }
}