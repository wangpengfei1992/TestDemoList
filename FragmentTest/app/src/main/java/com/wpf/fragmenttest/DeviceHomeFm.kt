package com.wpf.fragmenttest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


/**
 *  Author: feipeng.wang
 *  Time:   2021/6/8
 *  Description : This is description.
 */
class DeviceHomeFm : Fragment() {
    private var device0Fm:Device0Fm ?= null
    private var device1Fm:Device1Fm ?= null
    private var device2Fm:Device2Fm ?= null
    private var device3Fm:Device3Fm ?= null

    private var currentFm :Fragment ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device_home,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        device0Fm = Device0Fm()
        device1Fm = Device1Fm()
        device2Fm = Device2Fm()
        device3Fm = Device3Fm()

        hideAllFragment()
        showFragment("Device0Fm",device0Fm)
    }

    fun showFragment(tag:String,frag:Fragment?){
        var fragment = childFragmentManager.findFragmentByTag(tag)
        if (fragment != null){
            childFragmentManager.beginTransaction().show(fragment).commitAllowingStateLoss()
            currentFm = fragment
        }else{
            if (frag != null) {
                childFragmentManager.beginTransaction().add(R.id.device_rootview,frag,tag).commitAllowingStateLoss()
                currentFm = frag
            }
        }
    }
    fun hideAllFragment(){
        currentFm?.let {
            childFragmentManager.beginTransaction().hide(currentFm!!).commitAllowingStateLoss()
        }
    }
}