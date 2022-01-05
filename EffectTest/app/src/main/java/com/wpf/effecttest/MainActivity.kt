package com.wpf.effecttest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wpf.effecttest.extension.startAct
import com.wpf.effecttest.webviewtest.ScrolTestAct
import com.wpf.effecttest.webviewtest.ScrollingActivity
import com.wpf.effecttest.webviewtest.TestAct

class MainActivity : AppCompatActivity() {
    private lateinit var act:AppCompatActivity;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        act = this
        var start = findViewById<TextView>(R.id.start)
        start.setOnClickListener {
//            startAct<TestAct>(act)
            startAct<ScrolTestAct>(act)
        }
    }
}