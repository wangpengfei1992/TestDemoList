package com.wpf.darkmodetest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var mContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        val darkTv = findViewById<TextView>(R.id.change_Dark)
        darkTv.setOnClickListener {
            startAct<WebviewAct>(mContext)
        }
    }
}