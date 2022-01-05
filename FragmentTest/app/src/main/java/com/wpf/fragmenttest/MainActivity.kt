package com.wpf.fragmenttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.Fragment

/*
* fragment问题记录
* 切换暗黑模式、切换语言导致：activiety被销毁，fragment重复添加，所以看起来fragment重叠
* 1.replace 替换 add;每个fragment都是新建，不会重叠
* 2.onSaveInstanceState保存fragment，onCreate恢复
*
* */

class MainActivity : AppCompatActivity() {

    private var deviceText:TextView ?= null
    private var noteText:TextView ?= null
    private var meText:TextView ?= null

    private var deviceHomeFm: DeviceHomeFm ?= null
    private var noteFm:NoteFm ?= null
    private var meFm:MeFm ?= null

    private var currentFm :Fragment ?= null
    private var currentPoint = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView(savedInstanceState)
        initClick()
        currentPoint.let {
            when(it){
                0->deviceText?.performClick()
                1->noteText?.performClick()
                2->meText?.performClick()
                else -> {}
            }
        }

    }

    private fun initView(savedInstanceState: Bundle?) {
        deviceText = findViewById(R.id.device)
        noteText = findViewById(R.id.note)
        meText = findViewById(R.id.me)


        savedInstanceState?.let {
            deviceHomeFm = supportFragmentManager.getFragment(savedInstanceState,"DeviceHomeFm")  as DeviceHomeFm?
            noteFm = supportFragmentManager.getFragment(savedInstanceState,"NoteFm") as NoteFm?
            meFm = supportFragmentManager.getFragment(savedInstanceState,"MeFm") as MeFm?
            currentPoint = savedInstanceState.getInt("CurrentPoint")
        }
    }

    private fun initClick() {
        deviceText?.setOnClickListener {
            currentPoint = 0
            if (deviceHomeFm == null)deviceHomeFm = DeviceHomeFm()
            hideAllFragment()
            showFragment("DeviceHomeFm",deviceHomeFm)
        }
        noteText?.setOnClickListener {
            currentPoint = 1
            if (noteFm == null)noteFm = NoteFm()
            hideAllFragment()
            showFragment("NoteFm",noteFm)
        }
        meText?.setOnClickListener {
            currentPoint = 2
            if (meFm == null)meFm = MeFm()
            hideAllFragment()
            showFragment("MeFm",meFm)
        }
    }

    fun showFragment(tag:String,frag:Fragment?){
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment != null){
            supportFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
            currentFm = fragment
        }else{
            if (frag != null) {
                currentFm = frag
                supportFragmentManager.beginTransaction().replace(R.id.view_content,frag,tag).commitAllowingStateLoss()
            }
        }
    }
    fun hideAllFragment(){
        currentFm?.let {
            supportFragmentManager.beginTransaction().hide(currentFm!!).commitAllowingStateLoss()
        }
    }
    fun hideFragment(tag:String){
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        fragment?.let {
            supportFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        deviceHomeFm?.let {
            if (it.isAdded) supportFragmentManager.putFragment(outState,"DeviceHomeFm",deviceHomeFm!!)
        }
        noteFm?.let {
            if (it.isAdded) supportFragmentManager.putFragment(outState,"NoteFm",noteFm!!)
        }
        meFm?.let {
            if (it.isAdded) supportFragmentManager.putFragment(outState,"MeFm",meFm!!)
        }
        outState.putInt("CurrentPoint",currentPoint);
        super.onSaveInstanceState(outState)
    }

}