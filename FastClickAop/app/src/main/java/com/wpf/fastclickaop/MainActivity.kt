package com.wpf.fastclickaop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.wpf.api.AopOnclick

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var tvHelloWord = findViewById<TextView>(R.id.hello_World)
        tvHelloWord.setOnClickListener(this)
    }

    @AopOnclick
    override fun onClick(v: View) {
        if (v.id == R.id.hello_World){
            Log.e("wpf","点击hello")
        }
    }
}