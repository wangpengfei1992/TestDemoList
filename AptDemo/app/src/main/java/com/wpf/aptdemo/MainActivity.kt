package com.wpf.aptdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.wpf.apt_annotation.BindView

class MainActivity : AppCompatActivity() {

    @BindView(value = R.id.hello)
    private var helloText: TextView ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloText?.setOnClickListener{
            Log.e("wpf","欢迎来点我呀")
        }
    }
}