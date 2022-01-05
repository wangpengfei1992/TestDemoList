package com.wpf.studywork.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.wpf.common_module.BaseAct
import com.wpf.studywork.R

class TestAct : BaseAct<TestViewModel>() {

    override fun getLayoutViewId(): Int= R.layout.activity_test

    override fun onCreated(savedInstanceState: Bundle?) {
        var testViewModel:TextView = findViewById(R.id.textView)
        testViewModel.setOnClickListener {
            mViewModel.launch(mViewModel.mutableLiveData){
                Log.e("wpf","123")
            }
            mViewModel.doThing()
        }
        mViewModel.mutableLiveData.observe(this, Observer{
            Log.e("wpf","::::$it")
        })
    }
}