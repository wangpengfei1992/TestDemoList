package com.wpf.studywork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.wpf.studywork.extension.startAct
import com.wpf.studywork.main.TestAct

class MainActivity : AppCompatActivity() {
    private lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        act = this
        var start = findViewById<TextView>(R.id.start)
        start.setOnClickListener {
            startAct<TestAct>(act)
        }
    }
}