package com.wpf.mvvmapply

import com.wpf.common_ui.base.BaseActivity
import com.wpf.mvvmapply.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreateAfter() {
        mViewBinding.login.setOnClickListener {

        }
        mViewBinding.rigster.setOnClickListener {

        }
    }

}


